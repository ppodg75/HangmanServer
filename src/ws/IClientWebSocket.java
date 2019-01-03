package ws;

public interface IClientWebSocket {
	
	void sendToAll(String msg);	
    void sendToPlayer(String playerName, String msg);

}
