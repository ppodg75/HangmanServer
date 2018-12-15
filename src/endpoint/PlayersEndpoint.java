package endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
	
	private Gson gson;
	
	public PlayersEndpoint() {
		gson = new Gson();
	}	
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getList() {
		System.out.println("PlayersEndpoint.getList");		
		List<PlayerDto> players = playerService.getPlayersDto();
		players.stream().forEach(p->System.out.println("XX->"+p));
		return toJson(players);
	}
	
	@GET
	@Path("/{user}/state")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getPlayer(@PathParam("user") String user) {
		System.out.println("PlayersEndpoint.getPlayer");
		return toJson(playerService.getPlayer(user));
	}	
	
	@GET
	@Path("/byUID/{UID}/state")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getPlayerByUID(@PathParam("UID") String UID) {
		System.out.println("PlayersEndpoint.getPlayerByUID");
		return toJson(playerService.getPlayerByUID(UID));
	}
	
	@PUT
	@Path("/{UID}/{userName}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String updatePlayer(@PathParam("UID") String UID, @PathParam("userName") String user) {
		System.out.println("PlayersEndpoint.createPlayer");
		return toJson(playerService.updatePlayer(UID, user));
	}	
	
	private String toJson(Object o) {
		return gson.toJson(o);
	}
	
	@GET
	@Path("/test")
	@Produces({ MediaType.TEXT_PLAIN })
	public String test() {
		System.out.println("PlayersEndpoint.test");
		return playerService.test();
	}

}
