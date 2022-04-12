package tourGuideController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsoniter.output.JsonStream;

import tourGuide.service.UserService;
import tourGuide.user.User;
import tripPricer.Provider;

public class RewardController {
	@Autowired
	UserService userService;
	
	 private User getUser(String userName) {
	    	return userService.getUser(userName);
	    }
	 @RequestMapping("/getRewards") 
	    public String getRewards(@RequestParam String userName) {
	    	return JsonStream.serialize(userService.getUserRewards(getUser(userName)));
	    }
	
	/*
	 * @RequestMapping("/getTripDeals") public String getTripDeals(@RequestParam
	 * String userName) { List<Provider> providers =
	 * userService.getTripDeals(getUser(userName)); return
	 * JsonStream.serialize(providers); }
	 */
	 
	 
	
}
