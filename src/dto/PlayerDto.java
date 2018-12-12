package dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlayerDto implements Serializable {
	
	private static final long serialVersionUID = 7526471155622776147L;
		
	private String name;
	private long points = 0;
	private long countWins = 0;
	private long countLosts = 0;
	
	public PlayerDto() { }
	
	public PlayerDto(String name, long points, long countWins, long countLosts) { 
		this.name=name;
		this.points=points;
		this.countWins=countWins;
		this.countLosts=countLosts;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getPoints() {
		return points;
	}
	public void setPoints(long points) {
		this.points = points;
	}
	public long getCountWins() {
		return countWins;
	}
	public void setCountWins(long countWins) {
		this.countWins = countWins;
	}
	public long getCountLosts() {
		return countLosts;
	}
	public void setCountLosts(long countLosts) {
		this.countLosts = countLosts;
	}

}
