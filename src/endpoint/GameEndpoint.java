package endpoint;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import game.Game;
import server.IGameServer;
import service.IPlayerService;

@ApplicationScoped
@Path("/game")
public class GameEndpoint {
	
	@Inject	
	private IPlayerService playerService;
	
	@Inject
	private IGameServer gameServer;
	
	
	private Gson gson;
	
	public GameEndpoint() {
		gson = new Gson();
	}	
	
	@GET	
	@Produces({ MediaType.TEXT_PLAIN })
	public String getList() {						
		System.out.println("getList");
		return toJson(gameServer.getListOfGames());
	}	
	
	@GET
	@Path("{userName}/{opponentName}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String createtGame(@PathParam("userName") String userName, @PathParam("opponentName") String opponentName) {
		System.out.println("PlayersEndpoint.createtGame "+userName+" vs "+opponentName);		
		Game game = playerService.createGame(userName, opponentName);
		if (game.getGuessPlayer().getName().equals(userName)) {
			return "guess";			
		}
		return "word";
	}	
	
	@GET
	@Path("gameByPlayerName/{userName}")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response getGame(@PathParam("userName") String userName) {
		System.out.println("PlayersEndpoint.getGame "+userName);		
		return Optional
				.ofNullable( playerService.getGame(userName) )
							.map(this::toJson)
							.map( json -> Response.ok(json,MediaType.TEXT_PLAIN).build() )
							.orElse( Response.status(Response.Status.NOT_FOUND).entity("GAME FOR USER NOT EXIST!" ).build() )
							;
	}
	
	@GET
	@Path("sendLetter/{userName}/{letter}")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response sendLetterAndReturnGame(@PathParam("userName") String userName, @PathParam("letter") String letter) {
		System.out.println("PlayersEndpoint.getGame "+userName);		
		return Optional
				.ofNullable( playerService.updateGappedWordLetter(userName, letter) )
							.map(this::toJson)
							.map( json -> Response.ok(json,MediaType.TEXT_PLAIN).build() )
							.orElse( Response.status(Response.Status.NOT_FOUND).entity("GAME FOR USER NOT EXIST!" ).build() )
							;
	}	
	
	@PUT
	@Path("updateWord/{userName}/{word}")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response setWordAndReturnGame(@PathParam("userName") String userName, @PathParam("word") String word) {
		System.out.println("PlayersEndpoint.getGame "+userName);		
		return Optional
				.ofNullable( playerService.updateWord(userName, word) )
							.map(this::toJson)
							.map( json -> Response.ok(json,MediaType.TEXT_PLAIN).build() )
							.orElse( Response.status(Response.Status.NOT_FOUND).entity("GAME FOR USER NOT EXIST!" ).build() )
							;
	}	
	
	private String toJson(Object o) {
		return gson.toJson(o);
	}

}
