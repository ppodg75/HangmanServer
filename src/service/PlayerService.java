package service;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dto.PlayerDto;
import exception.EmptyNameForPlayerException;
import game.Player;
import server.IGameServer;

@ApplicationScoped
public class PlayerService implements IPlayerService {

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

	public PlayerDto getPlayer(String user) {
		System.out.print("PlayerService.getPlayer: "+user);
		return ofNullable(gameServer.findPlayerByName(user)).map(this::mapToPlayerDto).orElse(null);
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
	
}
