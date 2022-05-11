package tourGuide.Controller;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.Attraction;

import tourGuide.service.GpsUtilService;
import tourGuide.service.TourService;
import tourGuide.user.RecommandedAttraction;
import tourGuide.user.User;

@RestController
public class AttractionController {
	/*
	 * @Autowired private AttractionService attractionService;
	 */
	@Autowired
	private GpsUtilService gpsUtilService;
	@Autowired
	TourService tourService;

	private User getUser(String userName) {
		return tourService.getUser(userName);
	}

	@GetMapping("/getRecommandedAttractions")
	public List<RecommandedAttraction> getAttractions(@RequestParam String userName) {
		User user = getUser(userName);
		return tourService.getAllAttraction(user);
	}

	@GetMapping("/getNearbyAttractions")
	public String getNearbyAttractions(String userName) {

		User user = getUser(userName);
		return JsonStream.serialize(tourService.getFiveClosestAttraction(user));
	}

	@GetMapping("/getAttractions")
	public List<Attraction> getAttractions() {
		return gpsUtilService.getAttractions();
		/* return attractionService.getAttractions() ; */
	}
}
