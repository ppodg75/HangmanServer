
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

	@Inject	
	private IGameServer gameServer;
	
	public List<PlayerDto> getPlayersDto() {
		System.out.println("PlayerService.getPlayersDto > ");
		checkAndRemoveNoActivePlayers();	
		return gameServer.getPlayers().stream().map(this::mapToPlayerDto).collect(Collectors.toList());
	}
	
	public void checkAndRemoveNoActivePlayers() {
		System.out.println("PlayerService.checkAndRemoveNoActivePlayers() ");		
		List<Player> players =gameServer.getPlayers().stream().filter(Player::noneFeedBack).collect(Collectors.toList());
		players.forEach( p -> { gameServer.removePlayer(p); });
	}

	private PlayerDto mapToPlayerDto(Player player) {
//		System.out.print("PlayerService.mapToPlayerDto");
		PlayerDto dto = new PlayerDto();
		dto.setPlayerId(player.getPlayerId());
		dto.setName(player.getName());
		dto.setCountLosts(player.getCountLosts());
		dto.setCountWins(player.getCountWins());
		dto.setPoints(player.getPoints());
		dto.setStatus(player.getStatus().name());
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
	

	public PlayerDto getPlayer(long playerId) {
		System.out.print("PlayerService.getPlayer: "+playerId);
		return ofNullable(gameServer.findPlayerById(playerId)).map(this::mapToPlayerDto).orElse(null);		
	}
	

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
	
	public PlayerDto removePlayer(long playerId) {
		System.out.print("PlayerService.removePlayer: "+playerId);		
		Player player = gameServer.findPlayerById(playerId); 
		if (player==null) {
			return null;
		} else {
			gameServer.removePlayer(player);
			return mapToPlayerDto(player);
		}
	}
	
	public Game createGame(long playerId, long opponentId) {
		System.out.print("PlayerService.createGame: "+playerId+" vs " + opponentId);
		Player player = gameServer.findPlayerById(playerId);
		System.out.println("PlayerService.createGame > player ="+player);
		if (opponentId==0) {
  		  return gameServer.createGame(player);	
		} else {
		  Player opponent = gameServer.findPlayerById(opponentId);
		  System.out.println("PlayerService.createGame > opponent ="+opponent);	
		  return gameServer.createGame(player, opponent);
		}
	}
	
	public GameDto updateGappedWordLetter(long playerId, String letter) {
		Player player = gameServer.findPlayerById(playerId); 
		return of(gameServer.updateGappedWordLetter(player, letter));
	}
	

	public GameDto updateWord(long playerId, String word) {
		Player player = gameServer.findPlayerById(playerId); 
		return of(gameServer.updateWord(player, word));
	}
		
	public GameDto getGame(String userName) {
		return GameDto.of( gameServer.getGameByPlayerName(userName) );
	}
	
//	public GameDto endGame(long playerId) {
//		return GameDto.of( gameServer.endGame(playerId) );
//	}
	
	public void playerAlive(long playerId) {
		Player player = gameServer.findPlayerById(playerId);
		player.updateLastActivity();
	}

	@Override
	public GameDto getGame(long playerId) {
		Player player = gameServer.findPlayerById(playerId);
		return GameDto.of( gameServer.findGameByPlayer(player) );
	}
	
	
}
