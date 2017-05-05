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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {


	@ApiModelProperty(required=true)
	private Flight departureFlight;
	
	@ApiModelProperty(required=true)
	private Flight returningFlight;

	@ApiModelProperty(required=true)
	private CreditCard creditCard;
	
	@ApiModelProperty(required=true, example="32126319")
	private String airMiles;
	
	@ApiModelProperty(required=true, example="window")
	private String seatPreference;
	
	@ApiModelProperty(hidden=true, required=false)
	private String _id;

	private Booking(){
	}



	public Flight getDepartureFlight() {
		return departureFlight;
	}



	public void setDepartureFlight(Flight departureFlight) {
		this.departureFlight = departureFlight;
	}


	@JsonProperty("_id")
	public String getId() {
		return _id;
	}


	@JsonProperty("_id")
	public void setId(String _id) {
		this._id = _id;
	}


	public Flight getReturningFlight() {
		return returningFlight;
	}

	public void setReturningFlight(Flight returningFlight) {
		this.returningFlight = returningFlight;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public String getAirMiles() {
		return airMiles;
	}

	public void setAirMiles(String airMiles) {
		this.airMiles = airMiles;
	}

	public String getSeatPreference() {
		return seatPreference;
	}

	public void setSeatPreference(String seatPreference) {
		this.seatPreference = seatPreference;
	}
	

}
