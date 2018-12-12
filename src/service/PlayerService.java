package service;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dto.PlayerDto;
import game.Player;
import server.IAppServer;

@Named("playerService")
public class PlayerService implements IPlayerService {
	
	@Inject
	@Named("AppServer")
	private IAppServer server;
	
	public List<PlayerDto> getPlayersDto() {
		return server.getPlayers().stream().map(this::mapToPlayerDto).collect(Collectors.toList());
	}
	
	private  PlayerDto mapToPlayerDto(Player player) {
		PlayerDto dto = new PlayerDto();
		dto.setName(player.getName());
		dto.setCountLosts(player.getCountLosts());
		dto.setCountWins(player.getCountWins());
		dto.setPoints(player.getPoints());
		return dto;
	}
	
	public String test() {
		return "PlayerService - test ok";
	}

}
