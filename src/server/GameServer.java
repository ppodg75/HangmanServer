package server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import game.Game;
import game.GameStatus;
import game.Player;
import game.PlayerStatus;
import utils.WordCodeDecode;

@ApplicationScoped
public class GameServer implements IGameServer {

	private List<Player> players = new ArrayList<>();
	private Random random = new Random();
	
	private final static long MINUTES_WITHOUT_ACTIVITY_TO_REMOVE_GAME = 10;

	@Inject
	private IAppServer server;

	private ConcurrentLinkedQueue<Game> games = new ConcurrentLinkedQueue<>();

	public Game createGame(Player player, Player opponent) {
		System.out.print("GameServer.createGame: ");
		Game game = makeGame(player, opponent, true);
		return game;
	}

	public Game createGame(Player player) {
		System.out.print("GameServer.createGame");
		Game game = makeGame( player, Player.createComputerPlayer(), false);		
		return game;
	}

	private Game makeGame(Player player, Player opponent, boolean randomRole) {
		System.out.print("GameServer.makeGame: ");
		Game game = Optional.ofNullable(findGameByPlayers(player, opponent)).orElseGet(Game::new);		
		if (game.getGameStatus() == GameStatus.END) {
			game.init(true);
		} else {
			removeGame(player);
			int r = randomRole ? random.nextInt(1000) : 0;
			if (r > 500) { 
				game.setWordPlayer(player);
				game.setGuessPlayer(opponent);		
			} else {
				game.setWordPlayer(opponent);
				game.setGuessPlayer(player);
			}
			game.init(false);
		}
		System.out.print("GameServer.makeGame:game= " + game);
		refreshGames();
		games.add(game);
		String opponentPage = game.getWordPlayer()==opponent? "word":"guess";
		server.sendGoToPage(opponent, opponentPage);
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
		removeGame(player);	
		listPlayers();
	}

	public void playerDisconnected(Player player) {
		System.out.print("GameServer.playerDisconnected");
		Game game = findGameByPlayer(player);
		Player opponent = game.getOpponent(player);
		server.sendMessagePlayerDisconnected(opponent);
		games.remove(game);
	}
	
	private Game findGameByPlayers(Player player1, Player player2) {
		System.out.print("GameServer.findGameByPlayers: "+player1.getName()+" & "+player2.getName());
		for (Game g : games) {
			if (g.playerIn(player1) && g.playerIn(player2)) {
				return g;
			}
		}
		return null;
	}

	public Game findGameByPlayer(Player player) {
		System.out.print("GameServer.findGameByPlayer - in: "+player);
		for (Game g : games) {
			if (g.playerIn(player)) {
				System.out.print("GameServer.findGameByPlayer - out: "+g);
				return g;
			}
		}
		System.out.print("GameServer.findGameByPlayer - out: GAME NOT FOUND!");
		return null;
	}
	
	public Game endGame(long playerId) {
		Player player = findPlayerById(playerId);
	    Game game = findGameByPlayer(player);
	    Player opponent = game.getOpponent(player);
	    server.sendMessageOpponentEndGame(opponent);		
	    game.endGameBeforeBecouseOfPlayer(player);	    
	    refreshGames();
	    return game;
	}
	
	public Player findPlayerByName(String playerName) {
		System.out.print("GameServer.findPlayerByName: " + playerName);
		Optional<Player> player = players.stream().filter(it -> it.getName().equalsIgnoreCase(playerName)).findFirst();
		if (player.isPresent()) {
			return player.get();
		} else {
			return null;
		}
	}
	
	public Player findPlayerById(long id) {
		System.out.print("GameServer.findPlayerById: " + id);
		Optional<Player> player = players.stream().filter(it -> it.getPlayerId()==id).findFirst();
		if (player.isPresent()) {
			return player.get();
		} else {
			return null;
		}
	}

	private void listPlayers() {
		System.out.print("GameServer.listPlayers");
		refreshGames();
		server.sendRefreshListPlayersToAll();
	}

	public List<Player> getPlayers() {
		System.out.print("GameServer.getPlayers");
		List<Player> pl = new ArrayList<>();
		pl.addAll(Arrays.asList(createInvisiblePlayer("test1"), createInvisiblePlayer("test2")));
//		players.stream().forEach( p -> System.out.println(p) );
		players.stream().filter( p -> !p.isComputer() ).forEach( p -> pl.add(p) );
		return pl;
	}

	private Player createInvisiblePlayer(String playerName) {
//		System.out.print("GameServer.createInvisiblePlayer: " + playerName);
		Player player = new Player(playerName);
		player.setStatus(PlayerStatus.INVISIBLE);
		return player;
	}


	public Game updateGappedWordLetter(Player player, String letter) {
		Game game = findGameByPlayer(player);
		String decodedLetter = WordCodeDecode.decodeWordWithSpecsToPolishWord(letter);		
		game.guessLetter(decodedLetter);
		if (!game.getWordPlayer().isComputer()) {
			server.sendLetter(game.getWordPlayer(), decodedLetter);
		}
		refreshGames();
		return game;
	}

	public Game getGameByPlayerName(String playerName) {
		return findGameByPlayer(findPlayerByName(playerName));	
	}	

	public Game updateWord(Player player, String word) {
		Game game = findGameByPlayer(player);
		game.updateWord(WordCodeDecode.decodeWordWithSpecsToPolishWord(word));
		server.wordUpdated(game.getGuessPlayer());
		refreshGames();
		return game;
	}
	
	public void playerEndGame(Player player) {
		player.endGame();
		server.sendMessageOpponentEndGame(player);
	}
	
	public void removeGame(Game game) {		
		playerEndGame(game.getGuessPlayer());
		playerEndGame(game.getWordPlayer());
		games.remove(game);
	}
	
	private void removeGame(Player player) {
		Game game = findGameByPlayer(player);
		if (game != null) {
		   removeGame(game);	
		}		
	}
	
	public List<Game> getListOfGames() {
		return games.stream().collect(Collectors.toList());
	}
	
	private void refreshGames() {
		try {
		  List<Game> gameToRemove = games.stream().filter(this::noActivityForLongTime).collect(Collectors.toList());
		  for(Game game : gameToRemove) { removeGame(game); } 
		} catch (Exception e) {
			
		}
	}
	
	private boolean noActivityForLongTime(Game game) {
		return (LocalDateTime.now().minusMinutes(MINUTES_WITHOUT_ACTIVITY_TO_REMOVE_GAME).isAfter(game.getLastActivity()));
	}

}
