package tourGuide;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public class TestRewardsService {

	/*
	 * @Test public void userGetRewards() { GpsUtilService gpsUtilService = new
	 * GpsUtilService(); RewardsService rewardsService = new
	 * RewardsService(gpsUtilService, new RewardCentral());
	 * 
	 * InternalTestHelper.setInternalUserNumber(0); UserService userService = new
	 * UserService(gpsUtilService, rewardsService);
	 * 
	 * User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	 * Attraction attraction = gpsUtilService.getAttractions().get(0);
	 * user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction,
	 * new Date())); userService.trackUserLocation(user); List<UserReward>
	 * userRewards = user.getUserRewards(); userService.tracker.stopTracking();
	 * assertTrue(userRewards.size() == 1); }
	 */
	//ok 240222*********************************************************
	@Test
	public void usersGetRewards() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		GpsUtilService gpsUtilService = new GpsUtilService(); 
		RewardsService rewardsService = new RewardsService(gpsUtilService,new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtilService, rewardsService);
		userService.addUser(user);
		assertTrue(user.getUserRewards().size() == 0);
		
		Attraction attraction = gpsUtilService.getAttractions().get(0);
		user.clearVisitedLocations();
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		rewardsService.calculateRewards(user);
		
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}

		userService.tracker.stopTracker();
		List<UserReward> userRewards = user.getUserRewards();
		
		assertTrue(userRewards.size() > 0);
	}
	//ok 240222***********************************************************
	@Test
	public void isWithinAttractionProximity() {
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		Attraction attraction = gpsUtilService.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	/*
	 * @Ignore // Needs fixed - can throw ConcurrentModificationException
	 */
	//ok 120422 trop lent***********************************************************
	
	@Test
	public void nearAllAttractions() {
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		UserService userService = new UserService(gpsUtilService, rewardsService);
		
		rewardsService.calculateRewards(userService.getAllUsers().get(0));
		List<UserReward> userRewards = userService.getUserRewards(userService.getAllUsers().get(0));
		
		  try { TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { }
		 
		userService.tracker.stopTracker();

		assertEquals(gpsUtilService.getAttractions().size(), userRewards.size());
	}
	
}
