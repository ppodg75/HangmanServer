package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import exception.UnknownPlayerException;
import game.Game;
import game.Player;
import game.PlayerStatus;

@ApplicationScoped
public class GameServer implements IGameServer {

	private List<Player> players = new ArrayList<>();

	@Inject
	private IAppServer server;
	
	private ConcurrentHashMap<Player, Game> games = new ConcurrentHashMap<>();

//	private boolean isPlayerBusy(Player player) {
//		return PlayerStatus.busyStatuses.contains(player.getStatus());
//	}

//	private void sendMessageToPlayer(Player player, String message) {
//		server.sendMessage(player, message);       
//	}
//
//	private void startGame(Player p1, Player p2) {
//		Game game = new Game(p1, p2);
//		games.put(p1, game);
//		playGame(game);
//	}

	private void playGame(Game game) {
//	  game.Start();	
	}

	public Player createPlayer(String name, String UID) {
		System.out.print("createPlayer "+name+" > "+UID);
		Player player;
		if (name.isEmpty()) {
			player =  new Player(createRandomName(), UID);
		} else {
			player = new Player(name, UID);
		}		
		players.add(player);
		
		listPlayers();
		
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
				  .filter(it -> it.getName().equals(name))
				  .findFirst()
				          .orElseThrow(() -> { return new UnknownPlayerException(name); } );
	}
	
	private void listPlayers() {
//		System.out.println("listPlayers -------------------> ");
//		players.stream().forEach( System.out::println);
//		System.out.println("listPlayers <------------------- ");
	}
	
	public Player findPlayerByUID(String UID) {
		System.out.println("findPlayerByUID > "+UID);
		listPlayers();
		
		return players.stream()
				  .filter( it -> it.getUID().equals(UID))
				  .findFirst().orElse(null);		
	}

	
	public List<Player> getPlayers() {
		return Arrays.asList( createInvisiblePlayer("test1"), createInvisiblePlayer("test2"));
	}
	
	private Player createInvisiblePlayer(String name) {
		Player player = new Player(name, "0000");
		player.setStatus(PlayerStatus.INVISIBLE);
		return player;
	}
	

}
