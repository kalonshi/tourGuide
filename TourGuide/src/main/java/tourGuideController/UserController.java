package tourGuideController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tripPricer.Provider;

public class UserController {
	@Autowired
	UserService userService;

	@RequestMapping("/getLocation")
	public String getLocation(@RequestParam String userName) {
		VisitedLocation visitedLocation = userService.getUserLocation(getUser(userName));
		// userService.trackUserLocation(getUser(userName));
		return visitedLocation.location.latitude + ":" + visitedLocation.location.longitude;
	}

	private User getUser(String userName) {
		return userService.getUser(userName);
	}

	

}
