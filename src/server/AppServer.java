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
		
//	private static ConcurrentLinkedQueue<Player> sessionPlayers = new ConcurrentLinkedQueue<>();

	
//	public void removePlayerByName(String playerName) {
//		System.out.print("AppServer.removePlayerByName: "+playerName); 
//		Player player = getPlayerByName(playerName);
//		gameServer.playerDisconnected(player);
//	}

//    private Player getPlayerByName(String playerName) {
//    	System.out.print("AppServer.getPlayerByName: "+playerName);
//    	return sessionPlayers.stream()
//    							.filter(player -> player.getName().equals(playerName))
//    							.findFirst()
//    							.orElse(null)
//    									 ;
//    }
//
//	public void createPlayer(String playerName) {
//		System.out.print("AppServer.createPlayer: "+playerName);
//		Player player = gameServer.createPlayer(playerName);		
//	}
	
	private boolean verifyOperation(String op) {
		System.out.print("AppServer.verifyOperation: "+op);
		return op.contains(CMD_SEP);
	}

	private Command getCommand(String op) {
		System.out.print("AppServer.getCommand: "+op);
		String[] cmd_args = op.split(CMD_SEP);
		return Command.resolve(cmd_args[0]);
	}

	private String getData(String op) {
		System.out.print("AppServer.getData: "+op);
		String[] cmd_args = op.split(CMD_SEP);
		return cmd_args.length > 1 ? cmd_args[1]: "";
	}

	public void messageReceived(String playerName, String operationData) {
		System.out.print("AppServer.messageReceived: "+playerName+" > "+operationData);
		if (verifyOperation(operationData)) {
			Command cmd = getCommand(operationData);
			String data = getData(operationData);
			Player player = gameServer.findPlayerByName(playerName);
			doCommand(player, cmd, data);
		}
	}
	
	private void doCommand(Player player, Command cmd, String data) {
//		switch (cmd) {
//				
//		}
	}	

	public void sendMessageToClient(Player player, String operation, String data) {
		clientWs.sendToPlayer(player.getName(), operation + CMD_SEP + data);
	}

	public void sendMessage(Player toPlayer, String message) {
		sendMessageToClient(toPlayer, Command.CMD_MESSAGE.toString(), message);
	}

	public void sendLetter(Player toPlayer, String letter) {
		sendMessageToClient(toPlayer, Command.CMD_LETTER.toString(), letter);
	}

	public void sendMessageOpponentDisconnected(Player toPlayer, String disconnectedPlayerName) {
		sendMessageToClient(toPlayer, Command.CMD_DISCONNECTED.toString(), disconnectedPlayerName);
	}

	
	public void sendCommandRefershPlayerListToAll() {
//        clientWs.sendToAll(Command.CMD_REFERSH_PLAYERS.toString());		
	}

	
	public String getName() {
		return "Server name";
	}

}
