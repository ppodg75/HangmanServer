package service;

import java.util.List;
import static java.util.Optional.ofNullable;
import static java.util.Optional.of;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dto.PlayerDto;
import game.Player;
import server.IAppServer;

@Named("playerService")
@ApplicationScoped
public class PlayerService implements IPlayerService {
	
	@Inject
	@Named("AppServer")
	private IAppServer server;
	
	public List<PlayerDto> getPlayersDto() {
		System.out.println("PlayerService.getPlayersDto > ");
		return server.getPlayers().stream().map(this::mapToPlayerDto).collect(Collectors.toList());
	}
	
	private  PlayerDto mapToPlayerDto(Player player) {
		PlayerDto dto = new PlayerDto();
		dto.setUID(player.getUID());
		dto.setName(player.getName());
		dto.setCountLosts(player.getCountLosts());
		dto.setCountWins(player.getCountWins());
		dto.setPoints(player.getPoints());
		dto.setStatus(player.getStatus());
		return dto;
	}
	
	public String test() {
		return "PlayerService - test ok";
	}
	
	public PlayerDto updatePlayer(String UID, String userName) {
		server.updatePlayer(UID, userName);
		return getPlayer(userName);
	}

	public PlayerDto getPlayer(String user) {
		return mapToPlayerDto( of(server.findUserByName(user)).orElse(  createEmptyPlayer(user) ) );
	};
	
	public PlayerDto getPlayerByUID(String UID) {
		return mapToPlayerDto( of(server.findUserByUID(UID)).orElse(  createEmptyPlayer(UID) ) );
	};
	
	private Player createEmptyPlayer(String user) {
		Player player = new Player(user, "0000");
		player.setName(user);
		return player;
	}
}
