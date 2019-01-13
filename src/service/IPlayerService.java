package service;

import java.util.List;

import dto.GameDto;
import dto.PlayerDto;
import game.Game;

public interface IPlayerService {
	
	List<PlayerDto> getPlayersDto();
	
	PlayerDto getPlayer(String userName);
	
	PlayerDto getPlayer(long id);
	
	PlayerDto createPlayer(String userName);
	
//	PlayerDto removePlayer(String userName);
	
	PlayerDto removePlayer(long id);
	
//	Game createGame(String userName, String opponentName);
	
	Game createGame(long playerId, long opponentId);
	
//	GameDto updateGappedWordLetter(String userName, String letter);
	
	GameDto updateGappedWordLetter(long playerId, String letter);
	
//	GameDto updateWord(String userName, String word);
	
	GameDto updateWord(long playerId, String word);
	
//	GameDto getGame(String userName);
	
	GameDto getGame(long playerId);
	
	GameDto endGame(long playerId);
	
	String test();

}
