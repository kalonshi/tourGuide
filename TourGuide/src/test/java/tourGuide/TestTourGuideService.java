package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import tripPricer.Provider;

public class TestTourGuideService {

	/*
	 * @Test public void getUserLocation() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral()); InternalTestHelper.setInternalUserNumber(0);
	 * UserService userService = new UserService(gpsUtil, rewardsService);
	 * 
	 * User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	 * VisitedLocation visitedLocation = userService.trackUserLocation(user);
	 * userService.tracker.stopTracker();
	 * assertTrue(visitedLocation.userId.equals(user.getUserId())); }
	 */
	@Test
	public void getUserLocation() {
		GpsUtilService gpsUtilService = new GpsUtilService(); 
		RewardsService rewardsService = new RewardsService( gpsUtilService,new RewardCentral());
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserService userService = new UserService(gpsUtilService, rewardsService);
		userService.trackUserLocation(user);
		while(user.getVisitedLocations().isEmpty()) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		userService.tracker.stopTracker();

		assertTrue(user.getVisitedLocations().get(0).userId.equals(user.getUserId()));
	}
	@Test
	public void addUser() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);
		
		User retrivedUser = userService.getUser(user.getUserName());
		User retrivedUser2 = userService.getUser(user2.getUserName());

		userService.tracker.stopTracker();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);
		
		List<User> allUsers = userService.getAllUsers();

		userService.tracker.stopTracker();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserService userService = new UserService(gpsUtil, rewardsService);
		 InternalTestHelper.setInternalUserNumber(0); 
		userService.trackUserLocation(user);
		while(user.getVisitedLocations().isEmpty()) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		userService.tracker.stopTracker();
		System.out.println("**********************user********************************");
		System.out.println( userService.getUser(user.getUserName()).getUserId());
		System.out.println("**********************user2********************************");
		
		  assertEquals(user.getUserId(),
		  userService.getUser(user.getUserName()).getUserId());
		 
		/*
		 * assertEquals(user.getVisitedLocations().get(0).userId,
		 * userService.getUser(user.getUserName()).getUserId());
		 */
		
	}
	
	@Ignore // Not yet implemented
	
	/*@Test
	 * public void getNearbyAttractions() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral()); InternalTestHelper.setInternalUserNumber(0);
	 * UserService userService = new UserService(gpsUtil, rewardsService);
	 * 
	 * User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	 * VisitedLocation visitedLocation = userService.trackUserLocation(user);
	 * 
	 * List<Attraction> attractions =
	 * userService.getNearByAttractions(visitedLocation);
	 * 
	 * userService.tracker.stopTracker();
	 * 
	 * assertEquals(5, attractions.size()); }
	 */
	@Test
	public void getTripDeals() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = userService.getTripDeals(user);
		
		userService.tracker.stopTracker();
		
		assertEquals(10, providers.size());
	}
	
	
}
