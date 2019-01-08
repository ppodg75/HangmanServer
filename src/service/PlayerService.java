package service;

import static dto.GameDto.of;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dto.GameDto;
import dto.PlayerDto;
import exception.EmptyNameForPlayerException;
import game.Game;
import game.Player;
import server.IGameServer;

@ApplicationScoped
public class PlayerService implements IPlayerService {
	
	private final static String COMPUTER_PLAYER_NAME = "COMPUTER";

	@Inject	
	private IGameServer gameServer;

	public List<PlayerDto> getPlayersDto() {
		System.out.println("PlayerService.getPlayersDto > ");
		return gameServer.getPlayers().stream().map(this::mapToPlayerDto).collect(Collectors.toList());
	}

	private PlayerDto mapToPlayerDto(Player player) {
		System.out.print("PlayerService.mapToPlayerDto");
		PlayerDto dto = new PlayerDto();
		dto.setName(player.getName());
		dto.setCountLosts(player.getCountLosts());
		dto.setCountWins(player.getCountWins());
		dto.setPoints(player.getPoints());
		dto.setStatus(player.getStatus());
		return dto;
	}

	public String test() {
		System.out.print("PlayerService.test");
		return "PlayerService - test ok";
	}

	public PlayerDto getPlayer(String userName) {
		System.out.print("PlayerService.getPlayer: "+userName);
		return ofNullable(gameServer.findPlayerByName(userName)).map(this::mapToPlayerDto).orElse(null);
	};

	public PlayerDto createPlayer(String userName) {
		System.out.print("PlayerService.createPlayer: "+userName);
		if (userName.isEmpty()) {
			throw new EmptyNameForPlayerException();
		}
		if (getPlayer(userName)!=null) {
			return null;
		}
		Player player = new Player(userName);
		player.setName(userName);
		gameServer.addPlayer(player);
		return mapToPlayerDto(player);
	}
	
	public PlayerDto removePlayer(String userName) {
		System.out.print("PlayerService.removePlayer: "+userName);
		if (userName.isEmpty()) {
			throw new EmptyNameForPlayerException();
		}
		Player player = gameServer.findPlayerByName(userName); 
		if (player==null) {
			return null;
		} else {
			gameServer.removePlayer(player);
			return mapToPlayerDto(player);
		}
	}
	
	public Game createGame(String userName, String opponentName) {
		System.out.print("PlayerService.createGame: "+userName+" vs " + opponentName);
		Player player = gameServer.findPlayerByName(userName); 
		Player opponent = gameServer.findPlayerByName(opponentName);	
		System.out.println("PlayerService.createGame > player ="+player);
		System.out.println("PlayerService.createGame > opponent ="+opponent);		
		if (COMPUTER_PLAYER_NAME.equals(opponentName)) {
  		  return gameServer.createGame(player);	
		} else {
		  return gameServer.createGame(player, opponent);
		}
	}
	
	public GameDto updateGappedWordLetter(String userName, String letter) {
		Player player = gameServer.findPlayerByName(userName); 
		return of(gameServer.updateGappedWordLetter(player, letter));
	}
	
	public GameDto updateWord(String userName, String word) {
		Player player = gameServer.findPlayerByName(userName); 
		return of(gameServer.updateGappedWordLetter(player, word));
	}
	
	public GameDto getGame(String userName) {
		return GameDto.of( gameServer.getGameByPlayerName(userName) );
	}
	
	
}
