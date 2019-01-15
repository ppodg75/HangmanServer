package game;

import java.time.LocalDateTime;
import java.util.Random;

public class Player {
	
	private static final int MAX_ECHO_SECONDS = 10;
	private long playerId = 0;
	private static long seqPlayerId = 0;	
	private String name;
	private long points = 0;
	private long countWins = 0;
	private long countLosts = 0;	
	private PlayerStatus status = PlayerStatus.CREATED;
	private boolean isComputer = false;
	private LocalDateTime lastActivity;
	
	
	public Player(String name) {
	  playerId = ++seqPlayerId;
      System.out.println("Player '"+name+"' created with id="+playerId);
      this.name = name;
      updateLastActivity();
	}
	
	public void updateLastActivity() {
//		System.out.println("Player with id="+playerId+" updateLastActivity");
		lastActivity = LocalDateTime.now();
	}
	
	public boolean noneFeedBack() {
		return LocalDateTime.now().minusSeconds(MAX_ECHO_SECONDS).isAfter(lastActivity);
	}
		
	public long getPlayerId() {
		return playerId;
	}

	public static String createRandomName() {
		return "VirtualPlayer"+(10000000+(new Random()).nextInt(10000000));
	}
	
	public static Player createComputerPlayer() {
		Player player = new Player(createRandomName());
		player.isComputer = true;
		return player;
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
	
	public void endGame() {
		status = PlayerStatus.CREATED;
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
	
	public boolean isComputer() {
		return isComputer;
	}

	@Override
	public String toString() {
		return String.format("Player: %s, points: %d, wins: %d, losts: %d, is_computer: %d",name,points,countWins,countLosts, isComputer?1:0 );
	}
}
