package service;

import java.util.List;

import dto.PlayerDto;

public interface IPlayerService {
	
	List<PlayerDto> getPlayersDto();
	
	PlayerDto getPlayer(String user);
	
	PlayerDto getPlayerByUID(String UID);
	
	PlayerDto updatePlayer(String UID, String userName);
	
	String test();

}
