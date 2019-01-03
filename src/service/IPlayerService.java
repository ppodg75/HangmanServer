package service;

import java.util.List;

import dto.PlayerDto;

public interface IPlayerService {
	
	List<PlayerDto> getPlayersDto();
	
	PlayerDto getPlayer(String userName);
	
	PlayerDto createPlayer(String userName);
	
	String test();

}
