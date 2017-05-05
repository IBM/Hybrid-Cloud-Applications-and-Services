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
package microservices.api.sample.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import microservices.api.sample.Controller;
import microservices.api.sample.model.Booking;

@Path("/bookings")
@Api("Airline Booking API")
	public class BookingResource {

	@GET
	@ApiOperation(value="Retrieve all bookings for current user", 
		responseContainer="array", response=Booking.class)
	@Produces("application/json")
	public Response getBookings(){
		return Controller.getBookings();
	}
	
	@POST
	@ApiOperation("Create a booking")
	@Consumes("application/json")
	@Produces("application/json")
	@ApiResponses({
		@ApiResponse(code = 201, message= "Booking created", response=String.class)})
	public Response createBooking(Booking task){
		return Controller.createBooking(task);
	}
	
	@GET
	@Path("{id}")
	@ApiOperation(value="Get a booking with ID")
	@Produces("application/json")
	@ApiResponses({
		@ApiResponse(code = 200, message= "Booking retrieved", response=Booking.class),
		@ApiResponse(code = 404, message = "Booking not found")})
	public Response getBooking(@PathParam("id") String id){
		return Controller.getBooking(id);
	}
	
	@PUT
	@Path("{id}")
	@ApiOperation(value="Update a booking with ID")
	@Consumes("application/json")
	@Produces("text/plain")
	@ApiResponses({
		@ApiResponse(code = 200, message= "Booking updated"),
		@ApiResponse(code = 404, message = "Booking not found")})
	public Response updateBooking(@PathParam("id") String id, Booking booking){
		return Controller.updateBooking(id, booking);
	}
	
	@DELETE
	@Path("{id}")
	@ApiOperation(value="Delete a booking with ID")
	@ApiResponses({
		@ApiResponse(code = 200, message= "Booking deleted"),
		@ApiResponse(code = 404, message = "Booking not found")})
	@Produces("text/plain")
	public Response deleteBooking(@PathParam("id") String id){
		return Controller.deleteBooking(id);
	}
}
