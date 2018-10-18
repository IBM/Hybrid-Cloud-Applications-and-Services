package microservices.api.sample;	

import javax.naming.InitialContext;
import microservices.api.sample.model.Weather;

public class WeatherAPI {

	public static String USERNAME;
	public static String PASSWORD;
	
	static {
		try {
			InitialContext ctx;
			ctx = new InitialContext();
			Object pw = ctx.lookup("weather_password");
			Object user = ctx.lookup("weather_user");

			// Set Weather API values
			USERNAME = (String) user;
			PASSWORD = (String) pw;

		} catch (Exception e) {
			System.err.println("Could not read Weather credentials from JNDI Context " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static Weather getWeather(String date, String airportTo){
		return DatabaseAccess.getAirportWeather(date, airportTo, USERNAME, PASSWORD);
	}

	public static String getUsername(){
		return USERNAME;
	}

	public static String getPassword(){
		return PASSWORD;
	}
}
