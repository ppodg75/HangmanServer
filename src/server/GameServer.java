package server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import game.Game;
import game.Player;
import game.PlayerStatus;
import utils.WordCodeDecode;

/**
 * Klasa do obs³ugi listy gier
 * 
 * @author Norbert Matrzak
 * 
 */
@ApplicationScoped
public class GameServer implements IGameServer {

	private List<Player> players = new ArrayList<>();
	private Random random = new Random();

	private final static long MINUTES_WITHOUT_ACTIVITY_TO_REMOVE_GAME = 10;

	@Inject
	private IAppServer server;

	private ConcurrentLinkedQueue<Game> games = new ConcurrentLinkedQueue<>();

	/**
	 * Tworzy grê dla pary graczy 
	 * @author Norbert Matrzak 
	 * @param player - gracz 
	 * @param opponent - przeciwnik 
	 * @return Game - gra 
	 * @exception
	 */
	public Game createGame(Player player, Player opponent) {
		System.out.print("GameServer.createGame: ");
		Game game = makeGame(player, opponent, true);
		return game;
	}

	/**
	 * Tworzy grê dla gracz z przypisaniem wirtualnego gracza (komputer) 
	 * @author Norbert Matrzak 
	 * @param player - gracz 
	 * @return Game - gra 
	 * @exception
	 */
	public Game createGame(Player player) {
		System.out.print("GameServer.createGame");
		Game game = makeGame(player, Player.createComputerPlayer(), false);
		return game;
	}

	/**
	 * Tworzy grê dla pary graczy 
	 * @author Norbert Matrzak 
	 * @param player - gracz 
	 * @param opponent - przeciwnik 
	 * @param randomRole - czy ma byæ losowana rola zgaduj¹cy/wprowadza¹cy s³owo 
	 * @return Game - gra 
	 * @exception
	 */
	private Game makeGame(Player player, Player opponent, boolean randomRole) {
		System.out.print("GameServer.makeGame: ");
		removeGame(player);
		removeGame(opponent);
		Game game = new Game();
		removeGame(player);
		int r = randomRole ? random.nextInt(1000) : 0;
		if (r > 500) {
			game.setWordPlayer(player);
			game.setGuessPlayer(opponent);
		} else {
			game.setWordPlayer(opponent);
			game.setGuessPlayer(player);
		}
		game.init();
		refreshGames();
		games.add(game);
		String opponentPage = game.getWordPlayer() == opponent ? "word" : "guess";
		if (server != null) {
			server.sendGoToPage(opponent, opponentPage);
		}
		listPlayers();
		return game;
	}

	/**
	 * Dodaje gracza i wysy³a komunikat odœwie¿ listê graczy   
	 * @author Norbert Matrzak 
	 * @param player - gracz 
	 */
	public void addPlayer(Player player) {
		System.out.print("GameServer.createPlayer " + player.getName());
		players.add(player);
		listPlayers();
	}

	/**
	 * Usuwa gracza i wysy³a komunikat odœwie¿ listê graczy  
	 * @author Norbert Matrzak 
	 * @param player - gracz
	 * 
	 */
	public void removePlayer(Player player) {
		System.out.print("GameServer.removePlayer " + player.getName());
		players.remove(player);
		removeGame(player);
		listPlayers();
	}

	/**
	 * Realizuje akcjê roz³¹czenia-zakoñczenia gry 
	 * @author Norbert Matrzak 
	 * @param player - gracz 
	 * @return @exception
	 */
	public void playerDisconnected(Player player) {
		System.out.print("GameServer.playerDisconnected");
		Game game = findGameByPlayer(player);
		Player opponent = game.getOpponent(player);
		if (server != null) {
			server.sendMessagePlayerDisconnected(opponent);
		}
		games.remove(game);
	}

	/**
	 * Znajduje grê dla danego gracza 
	 * @author Norbert Matrzak 
	 * @param player - gracz (obiekt klasy Player) 
	 * @return znaleziony obiekt klasy Game 
	 * @exception
	 */
	public Game findGameByPlayer(Player player) {
		System.out.print("GameServer.findGameByPlayer - in: " + player);
		for (Game g : games) {
			if (g.playerIn(player)) {
				System.out.print("GameServer.findGameByPlayer - out: " + g);
				return g;
			}
		}
		System.out.print("GameServer.findGameByPlayer - out: GAME NOT FOUND!");
		return null;
	}

	/**
	 * Wyszukuje gracza o podanej nazwie 
	 * @author Norbert Matrzak 
	 * @param playerName - nazwa gracza 
	 * @return znaleziony obiekt klasy Player 
	 * @exception
	 */
	public Player findPlayerByName(String playerName) {
		System.out.print("GameServer.findPlayerByName: " + playerName);
		Optional<Player> player = players.stream().filter(it -> it.getName().equalsIgnoreCase(playerName)).findFirst();
		if (player.isPresent()) {
			return player.get();
		} else {
			return null;
		}
	}

	/**
	 * Wyszukuje gracza o podanym id 
	 * @author Norbert Matrzak 
	 * @param id - id gracza 
	 * @return znaleziony obiekt klasy Player 
	 * @exception
	 */
	public Player findPlayerById(long id) {
		System.out.print("GameServer.findPlayerById: " + id);
		Optional<Player> player = players.stream().filter(it -> it.getPlayerId() == id).findFirst();
		if (player.isPresent()) {
			return player.get();
		} else {
			return null;
		}
	}

	/**
	 * Oddœwie¿a listê gier, i wysy³a sygna³ do klienta (przegl¹darki) - wymuszenie oddœwie¿enia listy graczy 
	 * @author Norbert Matrzak
	 * 
	 */
	private void listPlayers() {
		System.out.print("GameServer.listPlayers");
		refreshGames();
		if (server != null) {
			server.sendRefreshListPlayersToAll();
		}
	}

	/**
	 * Zwraca listê graczy, na potrzeby javascript, zawsze dodawani s¹ dwaj
	 * gracze niewidoczni, tak aby JavaScrit zawsza widzia³ obiekt jako listê obiektów gracz 
	 * @author Norbert Matrzak 
	 * @param 
	 * @return lista graczy 
	 * @exception
	 */
	public List<Player> getPlayers() {
		System.out.print("GameServer.getPlayers");
		List<Player> pl = new ArrayList<>();
		pl.addAll(Arrays.asList(createInvisiblePlayer("test1"), createInvisiblePlayer("test2")));
		players.stream().filter(p -> !p.isComputer()).forEach(p -> pl.add(p));
		return pl;
	}

	/**
	 * Tworzy i zwraca nie widocznego gracza 
	 * @author Norbert Matrzak 
	 * @param playerName - nazwa gracza 
	 * @return gracz 
	 */
	private Player createInvisiblePlayer(String playerName) {
//		System.out.print("GameServer.createInvisiblePlayer: " + playerName);
		Player player = new Player(playerName);
		player.setStatus(PlayerStatus.INVISIBLE);
		return player;
	}

	/**
	 * Aktualizuje wybrakowane - szukane s³owo o podan¹ / wysy³a literê 
	 * @author Norbert Matrzak 
	 * @param player - gracz 
	 * @param letter - wys³ana litera (mo¿e byæ zakodowana w przypadku polskich diaktrycznych liter) 
	 * @return gra - przypisana do gracza 
	 */
	public Game updateGappedWordLetter(Player player, String letter) {
		Game game = findGameByPlayer(player);
		String decodedLetter = WordCodeDecode.decodeWordWithSpecsToPolishWord(letter);
		game.guessLetter(decodedLetter);
		if (!game.getWordPlayer().isComputer() && server != null) {
			server.sendLetter(game.getWordPlayer(), decodedLetter);
		}
		refreshGames();
		return game;
	}

	/**
	 * Wyszukuje zapisana gre dla gracza o podanej nazwie 
	 * @author Norbert Matrzak 
	 * @param playerName - nazwa gracza 
	 * @return znaleziony obiekt klasy Game 
	 * @exception
	 */
	public Game getGameByPlayerName(String playerName) {
		return findGameByPlayer(findPlayerByName(playerName));
	}

	/**
	 * Aktualizuje s³owo do zgadniêcia 
	 * @author Norbert Matrzak 
	 * @param player - gracz 
	 * @param letter - wys³ana litera (mo¿e byæ zakodowana w przypadku polskich diaktrycznych liter) 
	 * @return gra - przypisana do gracza 
	 */
	public Game updateWord(Player player, String word) {
		Game game = findGameByPlayer(player);
		game.updateWord(WordCodeDecode.decodeWordWithSpecsToPolishWord(word));
		if (server != null) {
			server.wordUpdated(game.getGuessPlayer());
		}
		refreshGames();
		return game;
	}

	/**
	 * Koñczy grê dla danego gracza i wykonuje dodatkowe czynnoœci - komunikat
	 * o zakoñczeniu gry 
	 * @author Norbert Matrzak 
	 * @param player - gracz 
	 */
	public void playerEndGame(Player player) {
		player.endGame();
		if (server != null) {
			server.sendMessageOpponentEndGame(player);
		}
	}

	/**
	 * Koñczy grê i wykonuje dodatkowe czynnoœci - np. wys³anie komunikatów 
	 * @author Norbert Matrzak 
	 * @param player - gracz
	 */
	public void removeGame(Game game) {
		playerEndGame(game.getGuessPlayer());
		playerEndGame(game.getWordPlayer());
		games.remove(game);
	}

	/**
	 * Usuwa grê 
	 * @author Norbert Matrzak 
	 * @param player -gracz 
	 * 
	 */
	private void removeGame(Player player) {
		Game game = findGameByPlayer(player);
		if (game != null) {
			removeGame(game);
		}
	}

	/**
	 * Aktualizuje listê gier i zwraca j¹ 
	 * @author Norbert Matrzak 
	 * @return - lista gier 
	 */
	public List<Game> getListOfGames() {
		refreshGames();
		return games.stream().collect(Collectors.toList());
	}

	/**
	 * Aktualizuje listê gier - usuwa gry o czasie bezczynnoœci d³u¿szym ni¿ X sekund 
	 * @author Norbert Matrzak 
	 * 
	 */
	private void refreshGames() {
		try {
			List<Game> gameToRemove = games.stream().filter(this::noActivityForLongTime).collect(Collectors.toList());
			for (Game game : gameToRemove) {
				removeGame(game);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Sprawdza stan bezczynnoœci gry 
	 * @author Norbert Matrzak
	 * @param game - badana gra
	 * @return true-je¿eli zosta³ przekroczony stan bezczynnoœci 
	 * @exception
	 */
	private boolean noActivityForLongTime(Game game) {
		return (LocalDateTime.now().minusMinutes(MINUTES_WITHOUT_ACTIVITY_TO_REMOVE_GAME)
				.isAfter(game.getLastActivity()));
	}

}
