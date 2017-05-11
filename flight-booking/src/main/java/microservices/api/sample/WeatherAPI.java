package microservices.api.sample;	

import microservices.api.sample.model.Weather;

public class WeatherAPI {
	//Please add your Weather API's USERNAME and PASSWORD here.
	private static String USERNAME = "Weather Data API's username";
	private static String PASSWORD = "Weather Data API's password";

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