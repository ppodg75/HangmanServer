package server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.Session;

import game.Player;
import ws.ClientWebSocket;
import ws.IClientWebSocket;

@Named("AppServer")
public class AppServer implements IAppServer {

	private static final String CMD_SEP = "#";

	@Inject
	private IClientWebSocket clientWs;
	
	private final GameServer gameServer;

	private static ConcurrentHashMap<Session, Player> sessionPlayers = new ConcurrentHashMap<>();

	public AppServer() {
		this.gameServer = new GameServer(this);
	}

	public void removePlayerBySession(Session session) {
		Player player = sessionPlayers.get(session);
		gameServer.playerDisconnected(player);
	}

	public void initPlayer(Session session) {
		Player player = gameServer.createPlayer();
		sessionPlayers.put(session, player);
	}

	public void newPlayer(Session session, String name) {
		Player player = sessionPlayers.get(session);
		player.setName(name);
	}
	
	private boolean verifyOperation(String op) {
		return op.contains(CMD_SEP);
	}

	private Command getCommand(String op) {
		String[] cmd_args = op.split(CMD_SEP);
		return Command.resolve(cmd_args[0]);
	}

	private String getData(String op) {
		String[] cmd_args = op.split(CMD_SEP);
		return cmd_args.length > 1 ? cmd_args[1]: "";
	}

	public void messageReceived(Session session, String operationData) {
		if (verifyOperation(operationData)) {
			Command cmd = getCommand(operationData);
			String data = getData(operationData);
			Player player = sessionPlayers.get(session);
			doCommand(player, cmd, data);
		}
	}
	
	private void doCommand(Player player, Command cmd, String data) {
		switch (cmd) {
		case CMD_INVITIAION : invitationReceived(player, data); break;
		case CMD_HELLO      : helloReceived(player, data); break;
		}
	}
	
	private void invitationReceived(Player invitator, String invitatedName) {
		Player invitated = gameServer.findPlayerByName(invitatedName);
		gameServer.makeInvitation(invitator, invitated);
	}
	
	private void helloReceived(Player player, String name) {		
		if (!name.isEmpty()) {
			gameServer.setPlayerName(player, name);
			sendCommandRefershPlayerListToAll();
		}
	}

	public void sendMessageToClient(Player player, String operation, String data) {
		clientWs.send(getSession(player), operation + CMD_SEP + data);
	}

	public void sendMessage(Player toPlayer, String message) {
		sendMessageToClient(toPlayer, Command.CMD_MESSAGE.toString(), message);
	}

	public void sendInvitation(Player toPlayer, String message) {
		sendMessageToClient(toPlayer, Command.CMD_INVITIAION.toString(), message);
	}

	public void sendLetter(Player toPlayer, String letter) {
		sendMessageToClient(toPlayer, Command.CMD_LETTER.toString(), letter);
	}

	public void sendMessageOpponentDisconnected(Player toPlayer, String disconnectedPlayerName) {
		sendMessageToClient(toPlayer, Command.CMD_DISCONNECTED.toString(), disconnectedPlayerName);
	}

	public Session getSession(Player player) {
		for (Map.Entry<Session, Player> e : sessionPlayers.entrySet()) {
			if (e.getValue() == player) {
				return e.getKey();
			}
		}
		return null;
	} 
	
	public void sendCommandRefershPlayerListToAll() {
        clientWs.sendToAll(Command.CMD_REFERSH_PLAYERS.toString());		
	}
	
	public List<Player> getPlayers() {
		return gameServer.getPlayers();
	}
	
	public String getName() {
		return "Server name";
	}
	
	
}
