package server;

import java.util.List;

import dto.PlayerDto;
import game.Player;

public interface IAppServer {

	List<Player> getPlayers();
}
