package endpoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/control")
public class ControlEndpoint {
	
	@GET
	@Path("/health")
	@Produces({ MediaType.TEXT_PLAIN })
	public String health() {
//		System.out.println("ControlEndpoint.health");	
		return "Server date time: "+getFomratedDateTime(); 
	}
	
	private String getFomratedDateTime() {
		 LocalDateTime date = LocalDateTime.now();
		 DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		 return date.format(formatter );
	}


}
