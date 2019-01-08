package service;

import java.util.List;

import dto.GameDto;
import dto.PlayerDto;
import game.Game;

public interface IPlayerService {
	
	List<PlayerDto> getPlayersDto();
	
	PlayerDto getPlayer(String userName);
	
	PlayerDto createPlayer(String userName);
	
	PlayerDto removePlayer(String userName);
	
	Game createGame(String userName, String opponentName);
	
	GameDto updateGappedWordLetter(String userName, String letter);
	
	GameDto updateWord(String userName, String letter);
	
	GameDto getGame(String userName);
	
	String test();

}
