package service;

import java.util.List;

import dto.PlayerDto;

public interface IPlayerService {
	
	List<PlayerDto> getPlayersDto(); 
	
	String test();

}
