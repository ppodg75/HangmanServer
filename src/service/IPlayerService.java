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
	
	PlayerDto removePlayer(long id);
	
	Game createGame(long playerId, long opponentId);
	
	GameDto updateGappedWordLetter(long playerId, String letter);
	
	GameDto updateWord(long playerId, String word);
	
	GameDto getGame(long playerId);
	
    void playerAlive(long playerId);


}
