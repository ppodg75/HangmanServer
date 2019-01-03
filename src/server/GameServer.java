package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import exception.EmptyNameForPlayerException;
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
		System.out.print("GameServer.playGame");
	}

	public void addPlayer(Player player) {
		System.out.print("GameServer.createPlayer "+player.getName());				
		players.add(player);		
		listPlayers();
	}

//	private String createRandomName() {
//		Random r = new Random();
//		return "Player" + r.nextInt(100000);
//	}

	public void playerDisconnected(Player player) {		
		System.out.print("GameServer.playerDisconnected");
		Game game = findGameByPlayer(player);
		Player opponent = game.getOpponent(player);
		server.sendMessageOpponentDisconnected(opponent, player.getName());
		games.remove(game);
	}
	
	private Game findGameByPlayer(Player player) {
		System.out.print("GameServer.findGameByPlayer");
		for(Map.Entry<Player, Game> e : games.entrySet()) {
			if (e.getValue().playerIn(player)) {
				return e.getValue();
			}
		}
		return null;
	}
	
	public Player findPlayerByName(String playerName) {
		System.out.print("GameServer.findPlayerByName: "+playerName);
		return players.stream()
				  .filter(it -> it.getName().equals(playerName))
				  .findFirst()
				  .orElse(null);
//				          .orElseThrow(() -> { return new UnknownPlayerException(name); } );
	}
	
	private void listPlayers() {
		System.out.print("GameServer.listPlayers"); 
//		System.out.println("listPlayers -------------------> ");
//		players.stream().forEach( System.out::println);
//		System.out.println("listPlayers <------------------- ");
	}
	
	
	public List<Player> getPlayers() {		
		System.out.print("GameServer.getPlayers"); 
		List<Player> pl = new ArrayList<>();
		pl.addAll(Arrays.asList( createInvisiblePlayer("test1"), createInvisiblePlayer("test2")));				
		pl.addAll(players);
		return pl;
	}
	
	private Player createInvisiblePlayer(String playerName) {
		System.out.print("GameServer.createInvisiblePlayer: "+playerName); 
		Player player = new Player(playerName);
		player.setStatus(PlayerStatus.INVISIBLE);
		return player;
	}
	

}
