package tourGuide.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import tourGuide.service.TourService;
import tourGuide.user.User;
import tripPricer.Provider;
@RestController
public class TripDealController {
	@Autowired
	TourService tourService;

	private User getUser(String userName) {
		return tourService.getUser(userName);
	}

	@RequestMapping("/getTripDeals")
	public String getTripDeals(@RequestParam String userName) {
		List<Provider> providers = tourService.getTripDeals(getUser(userName));
		return JsonStream.serialize(providers);
	}

}
