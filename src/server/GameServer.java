package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import game.Game;
import game.GameStatus;
import game.Player;
import game.PlayerStatus;

@ApplicationScoped
public class GameServer implements IGameServer {

	private List<Player> players = new ArrayList<>();
	private Random random = new Random();

	@Inject
	private IAppServer server;

	private ConcurrentHashMap<Player, Game> games = new ConcurrentHashMap<>();

	public Game createGame(Player player, Player opponent) {
		System.out.print("GameServer.createGame: ");
		Game game = makeGame(player, opponent, true);
		return game;
	}

	public Game createGame(Player player) {
		System.out.print("GameServer.createGame");
		Game game = makeGame(Player.createComputerPlayer(), player, false);		
		return game;
	}


	private Game makeGame(Player player, Player opponent, boolean randomRole) {
		System.out.print("GameServer.makeGame: ");
		Game game = Optional.ofNullable(findGameByPlayers(player, opponent)).orElseGet(Game::new);
		if (game.getGameStatus() == GameStatus.END) {
			game.init(true);
		} else {
			int r = randomRole ? random.nextInt(100) : 100;
			if (r > 50) {
				game.setWordPlayer(player);
				game.setGuessPlayer(opponent);
			} else {
				game.setWordPlayer(opponent);
				game.setGuessPlayer(player);
			}
			game.init(false);
		}
		System.out.print("GameServer.makeGame:game= " + game);
		listPlayers();
		return game;
	}

	public void addPlayer(Player player) {
		System.out.print("GameServer.createPlayer " + player.getName());
		players.add(player);
		listPlayers();
	}

	public void removePlayer(Player player) {
		System.out.print("GameServer.removePlayer " + player.getName());
		players.remove(player);
		listPlayers();
	}

	public void playerDisconnected(Player player) {
		System.out.print("GameServer.playerDisconnected");
		Game game = findGameByPlayer(player);
		Player opponent = game.getOpponent(player);
		server.sendMessageOpponentDisconnected(opponent, player.getName());
		games.remove(player);
	}
	
	private Game findGameByPlayers(Player player1, Player player2) {
		System.out.print("GameServer.findGameByPlayers: "+player1.getName()+" & "+player2.getName());
		for (Map.Entry<Player, Game> e : games.entrySet()) {
			if (e.getValue().playerIn(player1) && e.getValue().playerIn(player2)) {
				return e.getValue();
			}
		}
		return null;
	}

	private Game findGameByPlayer(Player player) {
		System.out.print("GameServer.findGameByPlayer");
		for (Map.Entry<Player, Game> e : games.entrySet()) {
			if (e.getValue().playerIn(player)) {
				return e.getValue();
			}
		}
		return null;
	}

	public Player findPlayerByName(String playerName) {
		System.out.print("GameServer.findPlayerByName: " + playerName);
		return players.stream().filter(it -> it.getName().equals(playerName)).findFirst().orElse(null);
//				          .orElseThrow(() -> { return new UnknownPlayerException(name); } );
	}

	private void listPlayers() {
		System.out.print("GameServer.listPlayers");
		server.sendRefreshListPlayersToAll();
	}

	public List<Player> getPlayers() {
		System.out.print("GameServer.getPlayers");
		List<Player> pl = new ArrayList<>();
		pl.addAll(Arrays.asList(createInvisiblePlayer("test1"), createInvisiblePlayer("test2")));
		players.stream().forEach( p -> System.out.println(p) );
		players.stream().filter( p -> !p.isComputer() ).forEach( p -> pl.add(p) );
		return pl;
	}

	private Player createInvisiblePlayer(String playerName) {
		System.out.print("GameServer.createInvisiblePlayer: " + playerName);
		Player player = new Player(playerName);
		player.setStatus(PlayerStatus.INVISIBLE);
		return player;
	}

	public Game updateGappedWordLetter(Player player, String letter) {
		Game game = findGameByPlayer(player);
		game.guessLetter(letter);
		if (!game.getWordPlayer().isComputer()) {
			server.sendLetter(game.getWordPlayer(), letter);
		}
		return game;
	}

	public Game getGameByPlayerName(String playerName) {
		return findGameByPlayer(findPlayerByName(playerName));
	}

	public Game updateWord(Player player, String word) {
		Game game = findGameByPlayer(player);
		game.updateWord(word);
		server.wordUpdated(game.getGuessPlayer());
		return game;
	}

}
