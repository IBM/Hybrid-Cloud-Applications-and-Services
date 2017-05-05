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
package microservices.api.sample.app;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import microservices.api.sample.resources.AirlinesResource;
import microservices.api.sample.resources.AvailabilityResource;
import microservices.api.sample.resources.BookingResource;

@ApplicationPath("/")
@SwaggerDefinition(
		info = @Info(description = "Service for booking and managing air flights",
					 version = "1.0.1",
					 title = "Airline Service"),
		tags= {@Tag(name="Airline Booking API", 
					description="APIs for booking and managing air flights")})
public class JAXRSApp extends Application {
	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<Object>();
		singletons.add(new AirlinesResource());
		singletons.add(new AvailabilityResource());
		singletons.add(new BookingResource());
		return singletons;
	}
	

}
