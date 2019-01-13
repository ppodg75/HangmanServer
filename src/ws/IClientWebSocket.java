package ws;

public interface IClientWebSocket {
	
	void sendToAll(String msg);	
    void sendToPlayer(long playerId, String msg);

}
