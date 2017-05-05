package microservices.api.sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import microservices.api.sample.model.Airline;
import microservices.api.sample.model.Booking;
import microservices.api.sample.model.Flight;
import microservices.api.sample.model.Airport;
import microservices.api.sample.model.Weather;

public class Controller {

	
	// --- These methods are called from the jaxrs.resources.* classes directly	
	public static Response getAirlines() {
		return Response.ok().entity(DatabaseAccess.getAllAirlines()).build();
	}
	
   public static Response getFlights (String date, String airportFrom, String airportTo) {
	   List<Flight> flights = new ArrayList<Flight>(6);

	   //Flights
	   for (int i = 0; i < 5; i++) {
		   flights.add(new Flight(getRandomAirline(), 
				   				  date + " " + getRandomTime(),
				   				  "AC" + getRandomNumber(200,10),
				   				  "on schedule",
				   				  airportFrom,
				   				  airportTo,
				   				  getRandomPrice())); 
	   }

	   //Weather
	   Weather weather = DatabaseAccess.getLocWeather(date,airportTo);
	   if (weather == null){
	   	   weather = new Weather();
	   } 
	   Airport airport = new Airport(flights,weather);
	   
	   return Response.ok().entity(airport).build();
   }
   
	public static Response getBookings(){
		return Response.ok().entity(DatabaseAccess.getAllBookings()).build();
	}
	
	public static Response createBooking(Booking booking){
		String id = DatabaseAccess.addBooking(booking);
		return Response.status(Status.CREATED).entity("{\"id\":\"" +id+ "\"}").build();	
	}
	
	public static Response getBooking(@PathParam("id") String id){
		Booking booking = DatabaseAccess.getBooking(id);
		if(booking!=null){
			return Response.ok().entity(booking).build();	
		}
		else{
			return Response.status(Status.NOT_FOUND).build();	
		}
	}
	
	public static Response updateBooking(@PathParam("id") String id, Booking booking){
		Booking oldBooking = DatabaseAccess.getBooking(id);

		if(oldBooking!=null){
			DatabaseAccess.updateBooking(id,booking);
			return Response.ok().build();	
		}
		else{
			return Response.status(Status.NOT_FOUND).build();	
		}		
	}
	
	public static Response deleteBooking(@PathParam("id") String id){
		Booking oldBooking = DatabaseAccess.getBooking(id);

		if(oldBooking!=null) {
			DatabaseAccess.removeBooking(id);
			return Response.ok().build();
		}
		else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
   
	// ---- Helper methods
	
	public static int getRandomNumber(int max, int min) {
		return (new Random()).nextInt(max - min) + min;
	}
	
   private static String getRandomTime() {
	   return getRandomNumber(23,10) + ":" + getRandomNumber(59,10); 
   }
   
   private static String getRandomPrice() {
	   return Integer.toString(getRandomNumber(600, 300));
   }
   
	public static Airline getRandomAirline(){
		Collection<Airline> airlines = DatabaseAccess.getAllAirlines();
		List<Airline> airlineList = new ArrayList<Airline>(airlines);
		return airlineList.get(getRandomNumber(airlineList.size() - 1,0));
	}
	
}
