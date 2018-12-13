package ws;

import javax.websocket.Session;

public interface IClientWebSocket {
	
	void sendToAll(String msg);
	void send(Session session, String msg);

}
