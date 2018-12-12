package endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import dto.PlayerDto;
import service.IPlayerService;

@Path("/players")
public class PlayersEndpoint {
	
	@Inject
	@Named("playerService")
	private IPlayerService playerService;	
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getList() {
		System.out.println("PlayersEndpoint.getList()");
		Gson gson = new Gson();
		List<PlayerDto> players = playerService.getPlayersDto();
		players.add(new PlayerDto("Krzysztof",4,5,8));
		players.add(new PlayerDto("Wojtek",4,5,8));
		
		players.add(0, new PlayerDto("test1",5,6,7));
		players.add(1, new PlayerDto("test2",5,6,7));
		
		return gson.toJson(players);
	}
	
	@GET
	@Path("/test")
	@Produces({ MediaType.TEXT_PLAIN })
	public String test() {
		return playerService.test();
	}

}
