/**
* (C) Copyright IBM Corporation 2016.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package microservices.api.sample.model;


import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModelProperty;

public class Airport {

	@ApiModelProperty(required=true)
	private List<Flight> flights;

	@ApiModelProperty(required=true)
	private Weather weather;
	
	public Airport() {
		
	}
	
	public Airport(List<Flight> flights, Weather weather) {
		this.flights = flights;
		this.weather = weather;	
	}

	public List<Flight> getFlights() {
		return flights;
	}

	public Weather getWeather() {
		return weather;
	}
}
