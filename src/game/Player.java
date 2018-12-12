package game;

import javax.websocket.Session;

public class Player {
	
	private String name;
	private long points = 0;
	private long countWins = 0;
	private long countLosts = 0;	
	private PlayerStatus status = PlayerStatus.CREATED;

	public Player(String name) {
      this.name = name;    
	}
	
	public long addPoints(long countUniqueLetters) {
		this.points += countUniqueLetters;
		return countUniqueLetters;
	}
	
	public long incWin() {
		return ++countWins;
	}
	
	public long incLost() {
		return ++countLosts;
	}

	public String getName() {
		return name;
	}

	public long getPoints() {
		return points;
	}

	public long getCountWins() {
		return countWins;
	}

	public long getCountLosts() {
		return countLosts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
	}
	
	
}
