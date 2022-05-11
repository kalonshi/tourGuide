package tourGuide.Controller;

import java.util.List;

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
public class UserController {
	@Autowired
	TourService tourService;

	@RequestMapping("/")
	public String index() {
		return "Greetings from TourGuide!";
	}
	/*
	 * @RequestMapping("/getLocation") public String getLocation(@RequestParam
	 * String userName) { VisitedLocation visitedLocation =
	 * tourService.getUserLocation(getUser(userName));
	 * tourService.trackUserLocation(getUser(userName)); return
	 * visitedLocation.location.latitude + ":" + visitedLocation.location.longitude;
	 * }
	 */

	/*
	 * private User getUser(String userName) { return tourService.getUser(userName);
	 * }
	 */

}
