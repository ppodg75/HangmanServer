package server;

import static java.util.Optional.of;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import exception.UnknownPlayerException;
import game.Game;
import game.Player;
import game.PlayerStatus;

public class GameServer {

	private final static String INVITATION_MESSAGE = "The player \"%s\" invitate you.";
	private final static int INVITATION_TIMEOUT = 10;

	private List<Player> players = new ArrayList<>();

	private AppServer server;

	public GameServer(AppServer server) {
		this.server = server;
	}

	// invitations: invitator, invitated
	// invitations: invitated, invitator
	private ConcurrentHashMap<Player, Player> invitations = new ConcurrentHashMap<Player, Player>();
	private ConcurrentHashMap<Player, Game> games = new ConcurrentHashMap<>();

	public void makeInvitation(Player invitator, Player invitated) {
		if (playerNotBusy(invitated)) {
			invitations.put(invitator, invitated);
			sendInvitationToPlayerAndForAcceptWithTimeOut(invitator, invitated, INVITATION_TIMEOUT);
		} else {
			sendMessageToPlayer(invitator, "Player " + invitated + " is busy!");
		}
	}

	private boolean playerNotBusy(Player player) {
		return false;
	}

	private void sendMessageToPlayer(Player player, String message) {
		server.sendMessage(player, message);       
	}

	private void sendInvitationToPlayerAndForAcceptWithTimeOut(Player invitator, Player invitated, int timeout) {
			invitated.setStatus(PlayerStatus.INVITATED);
			server.sendInvitation(invitated, String.format(INVITATION_MESSAGE, invitator.getName()));
			Runnable waiting = new Runnable() {
				@Override
				public void run() {
					try {
						for (int i = 0; i < timeout; i++) {
							Thread.sleep(1000);
						}
						invitationTimeouted(invitator);
					} catch (InterruptedException e) {
					}
				}
			};
			ExecutorService executor = Executors.newFixedThreadPool(10);
			executor.execute(waiting);

	}

	public void playerAnswerOnInvitation(Player invitated, boolean accepted) {
		if (accepted) {
			startInvitatedGame(invitated);
		} else {
			removeInvitationByInvitated(invitated);
		}
	}

	private void invitationTimeouted(Player invitated) {
		removeInvitationByInvitated(invitated);
	}

	private void removeInvitationByInvitated(Player invitated) {
		invitations.forEach((k, v) -> {
			 if (v==invitated) {
				invitations.remove(k);
				return;
			}
		});
	}

	private void startInvitatedGame(Player invitated) {
		invitations.forEach((k, v) -> {
			if (v==invitated) {
				invitations.remove(k);
				startGame(k, v);
				return;
			}
		});
	}

	private void startGame(Player p1, Player p2) {
		Game game = new Game(p1, p2);
		games.put(p1, game);
		playGame(game);
	}

	private void playGame(Game game) {
//	  game.Start();	
	}

	public Player createPlayer() {
		Player player = new Player(createRandomName());
		players.add(player);
		return player;
	}

	private String createRandomName() {
		Random r = new Random();
		return "Player" + r.nextInt(100000);
	}

	public void playerDisconnected(Player player) {		
		Game game = findGameByPlayer(player);
		Player opponent = game.getOpponent(player);
		server.sendMessageOpponentDisconnected(opponent, player.getName());
		games.remove(game);
	}
	
	private Game findGameByPlayer(Player player) {
		for(Map.Entry<Player, Game> e : games.entrySet()) {
			if (e.getValue().playerIn(player)) {
				return e.getValue();
			}
		}
		return null;
	}
	
	public Player findPlayerByName(String name) {
		return players.stream()
				  .filter(name::equals)
				  .findFirst()
				          .orElseThrow(() -> { return new UnknownPlayerException(name); } );
	}
	
	public void setPlayerName(Player player, String name) {
		player.setName(name);
		server.sendCommandRefershPlayerListToAll();
	}
	
	
	public List<Player> getPlayers() {
//		return players;
		return Arrays.asList( new Player("XXX"), new Player("YYY"));
	}
	

}
