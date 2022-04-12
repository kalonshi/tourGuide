package tourGuide.service;

import java.util.List;
import java.util.UUID;

import tripPricer.Provider;
public interface TripPricerService {
	   List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints);

}
