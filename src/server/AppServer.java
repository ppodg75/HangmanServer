package server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import game.Player;
import ws.IClientWebSocket;

@ApplicationScoped
public class AppServer implements IAppServer {

	private static final String CMD_SEP = "#";

	@Inject
	private IClientWebSocket clientWs;
	
	@Inject
	private IGameServer gameServer;
	
	public void sendMessageToClient(Player player, String operation, String data) {
		if (player==null) { 
			System.out.println("sendMessageToClient - Player NULL!");
		}
		System.out.println("sendMessageToClient "+player.getName()+" > "+operation+" # "+data);
		if (player.isComputer()) {
			System.out.println("Players is the computer (virtual player), messege has not been sent");
			return;
		}
		clientWs.sendToPlayer(player.getPlayerId(), operation + CMD_SEP + data);		
	}
	
	public void sendMessageToClient(Player player, String operation) {
		if (player==null) { 
			System.out.println("sendMessageToClient - Player NULL!");
		}
		System.out.println("sendMessageToClient "+player.getName()+" > "+operation);
		if (player.isComputer()) {
			System.out.println("Players is the computer (virtual player), messege has not been sent");
			return;
		}
		clientWs.sendToPlayer(player.getPlayerId(), operation);		
	}

	public void sendLetter(Player toPlayer, String letter) {
		sendMessageToClient(toPlayer, Command.CMD_LETTER.toString());
	}

	public void sendMessagePlayerDisconnected(Player toPlayer) {
		sendMessageToClient(toPlayer, Command.CMD_DISCONNECTED.toString(), "");	
	}
	
	public void sendMessageOpponentEndGame(Player toPlayer) {
		sendMessageToClient(toPlayer, Command.CMD_OPPONENT_END_GAME.toString());	
	}
	
	public void sendGoToPage(Player toPlayer, String page) {
		sendMessageToClient(toPlayer, Command.CMD_GOTO_PAGE.toString()+"_" + page);	
	}
	
	
	public void wordUpdated(Player toPlayer) {
		sendMessageToClient(toPlayer, Command.CMD_WORD_UPDATED.toString());
	}

	public void sendRefreshListPlayersToAll() {
		clientWs.sendToAll(Command.CMD_REFERSH_PLAYERS.toString());	
	}

	public void removePlayerById(long playerId) {
		gameServer.removePlayer(gameServer.findPlayerById(playerId));
		sendRefreshListPlayersToAll();
	}
	
}
