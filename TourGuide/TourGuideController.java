package tourGuide;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.service.TourService;
import tourGuide.user.User;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourService tourService;

	/*
	 * @RequestMapping("/") public String index() { return
	 * "Greetings from TourGuide!"; }
	 * 
	 * @RequestMapping("/getLocation") public String getLocation(@RequestParam
	 * String userName) { VisitedLocation visitedLocation =
	 * tourService.getUserLocation(getUser(userName)); return
	 * JsonStream.serialize(visitedLocation.location); }
	 */
	// TODO: Change this method to no longer return a List of Attractions.
	// Instead: Get the closest five tourist attractions to the user - no matter how
	// far away they are.
	// Return a new JSON object that contains:
	// Name of Tourist attraction,
	// Tourist attractions lat/long,
	// The user's location lat/long,
	// The distance in miles between the user's location and each of the
	// attractions.
	// The reward points for visiting each Attraction.
	// Note: Attraction reward points can be gathered from RewardsCentral
	/*
	 * @RequestMapping("/getNearbyAttractions") public String
	 * getNearbyAttractions(@RequestParam String userName) { VisitedLocation
	 * visitedLocation = tourService.getUserLocation(getUser(userName));
	 * 
	 * 
	 * return
	 * JsonStream.serialize(tourService.getNearByAttractions(visitedLocation)); }
	 */
	/*
	 * @RequestMapping("/getRewards") public String getRewards(@RequestParam String
	 * userName) { return
	 * JsonStream.serialize(tourService.getUserRewards(getUser(userName))); }
	 */

	/*
	 * @RequestMapping("/getAllCurrentLocations") public JSONArray
	 * getAllCurrentLocations() throws JSONException { // TODO: Get a list of every
	 * user's most recent location as JSON //- Note: does not use gpsUtil to query
	 * for their current location, // but rather gathers the user's current location
	 * from their stored location history. // // Return object should be the just a
	 * JSON mapping of userId to Locations similar to: // { //
	 * "019b04a9-067a-4c76-8817-ee75088c3822":
	 * {"longitude":-48.188821,"latitude":74.84371} // ... // } return
	 * JsonStream.serialize(tourService.getAllCurrentLocations()); return
	 * tourService.getAllCurrentLocations(); return JsonStream.serialize(""); return
	 * JsonStream.serialize("Hello World"); }
	 */
	/*
	 * @RequestMapping("/getTripDeals") public String getTripDeals(@RequestParam
	 * String userName) { List<Provider> providers =
	 * tourService.getTripDeals(getUser(userName)); return
	 * JsonStream.serialize(providers); }
	 * 
	 * private User getUser(String userName) { return tourService.getUser(userName);
	 * }
	 */
}