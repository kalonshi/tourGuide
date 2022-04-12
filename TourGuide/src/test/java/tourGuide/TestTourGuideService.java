package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

	// ok 040422*****Attention internalTestUser use for
	// test*************************
	@Test
	public void getUserLocation() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		UserService userService = new UserService(gpsUtil, rewardsService);
		User user = userService.getAllUsers().get(0);

		userService.trackUserLocation(user);
		/*
		 * while(!user.getVisitedLocations().isEmpty()) { try {
		 * System.out.println("sleep"); TimeUnit.MILLISECONDS.sleep(50); } catch
		 * (InterruptedException e) { } }
		 */
		userService.tracker.stopTracker();

		assertTrue(user.getVisitedLocations().get(0).userId.equals(user.getUserId()));

	}

	// ok 240222
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

	// ok 240222***********************************************************
	@Test
	public void getAllUsers() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		/* InternalTestHelper.setInternalUserNumber(0); */
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

	/************* OK coverage 120422 ***********/
	// ok 040422*****Attention internalTestUser use for
	// test*************************

	@Test
	public void trackUser() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(10);

		UserService userService = new UserService(gpsUtil, rewardsService);

		User user = userService.getAllUsers().get(1);
		userService.trackUserLocation(user);
		VisitedLocation visitedLocation = user.getVisitedLocations().get(user.getVisitedLocations().size() - 1);

		userService.tracker.stopTracker();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	/************** Ok 120422 ***************/
	// @Ignore Not yet implemented

	@Test
	public void getNearbyAttractions() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		UserService userService = new UserService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(
				new VisitedLocation(user.getUserId(), new Location(attraction.latitude, attraction.longitude),
						Date.from(localDateTime.toInstant(ZoneOffset.UTC))));

		VisitedLocation visitedLocation = user.getVisitedLocations().get(user.getVisitedLocations().size() - 1);

		List<Attraction> attractions = userService.getNearByAttractions(visitedLocation);
		System.out.println(user.getVisitedLocations());
		System.out.println("**************** attractions");
		System.out.println(attractions.size());
		userService.tracker.stopTracker();

		assertEquals(4, attractions.size());
	}

	// ok 240322 avec 10 not OK 120422

	@Test
	public void getTripDeals() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = userService.getTripDeals(user);

		userService.tracker.stopTracker();

		assertEquals(5, providers.size());
	}

}
