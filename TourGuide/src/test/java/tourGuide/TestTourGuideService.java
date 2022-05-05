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

import org.apache.commons.lang3.time.StopWatch;
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
import tourGuide.service.TourService;
import tourGuide.user.RecommandedAttraction;
import tourGuide.user.User;
import tourGuide.user.UserAttraction;
import tourGuide.user.UserLocation;
import tripPricer.Provider;

public class TestTourGuideService {

	// ok 040422*****Attention internalTestUser use for
	// test*************************
	@Test
	public void getUserLocation() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		TourService tourService = new TourService(gpsUtil, rewardsService);
		User user = tourService.getAllUsers().get(0);

		tourService.tracker.stopTracker();

		assertTrue(user.getVisitedLocations().get(0).userId.equals(user.getUserId()));

	}

	// OK test avec UserLocation 050522
	@Test
	public void getUserLocation3() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		 tourService.trackUserLocations2(user);
		VisitedLocation visitedLocation=user.getLastVisitedLocation();
		 assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	// ok 240222
	@Test
	public void addUser() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
		/*
		 * VisitedLocation visitedLocation=gpsUtil.getUserLocationApi(user.getUserId());
		 * System.out.println("visitedLocation lat"+visitedLocation.location.latitude);
		 */
		tourService.addUser(user);
		tourService.addUser(user2);

		User retrivedUser = tourService.getUser(user.getUserName());
		User retrivedUser2 = tourService.getUser(user2.getUserName());

		tourService.tracker.stopTracker();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	// ok 240222***********************************************************
	@Test
	public void getAllUsers() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		/* InternalTestHelper.setInternalUserNumber(0); */
		TourService tourService = new TourService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourService.addUser(user);
		tourService.addUser(user2);

		List<User> allUsers = tourService.getAllUsers();

		tourService.tracker.stopTracker();

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

		TourService tourService = new TourService(gpsUtil, rewardsService);

		User user = tourService.getAllUsers().get(1);
		tourService.trackUserLocation(user);
		VisitedLocation visitedLocation = user.getLastVisitedLocation();
		tourService.tracker.stopTracker();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	// Test Ok 0305
	@Test
	public void trackUser2() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		TourService tourService = new TourService(gpsUtil, rewardsService);
		InternalTestHelper.setInternalUserNumber(0);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		tourService.trackUserLocation(user);
		
		VisitedLocation visitedLocation = user.getLastVisitedLocation();
		tourService.tracker.stopTracker();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractionsUserAttration2() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourService.trackUserLocations2(user);

		List<RecommandedAttraction> attractions = tourService.getFiveClosestAttraction(user);
		System.out.println("user.getVisitedLocations() size");
		System.out.println(user.getVisitedLocations().size());

		System.out.println("**************** attractions");
		System.out.println(attractions.size());
		tourService.tracker.stopTracker();

		assertEquals(5, attractions.size());
	}
	// ok 240322 avec 10 not OK 120422

	@Test
	public void getTripDeals() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourService.getTripDeals(user);

		tourService.tracker.stopTracker();

		assertEquals(5, providers.size());
	}
	/************** Ok 120422 ***************/
	// @Ignore Not yet implemented
	/*
	 * @Test public void getNearbyAttractions() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral()); InternalTestHelper.setInternalUserNumber(1);
	 * TourService tourService = new TourService(gpsUtil, rewardsService);
	 * 
	 * User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	 * LocalDateTime localDateTime = LocalDateTime.now().minusDays(new
	 * Random().nextInt(30)); Attraction attraction =
	 * gpsUtil.getAttractions().get(0); user.addToVisitedLocations( new
	 * VisitedLocation(user.getUserId(), new Location(attraction.latitude,
	 * attraction.longitude), Date.from(localDateTime.toInstant(ZoneOffset.UTC))));
	 * 
	 * VisitedLocation visitedLocation =
	 * user.getVisitedLocations().get(user.getVisitedLocations().size() - 1);
	 * 
	 * List<Attraction> attractions =
	 * tourService.getNearByAttractions(visitedLocation);
	 * System.out.println(user.getVisitedLocations());
	 * System.out.println("**************** attractions");
	 * System.out.println(attractions.size()); tourService.tracker.stopTracker();
	 * 
	 * assertEquals(4, attractions.size()); }
	 */

	/*
	 * @Test public void getNearbyAttractionsUserAttration() { GpsUtilService
	 * gpsUtil = new GpsUtilService(); RewardsService rewardsService = new
	 * RewardsService(gpsUtil, new RewardCentral());
	 * InternalTestHelper.setInternalUserNumber(1); TourService tourService = new
	 * TourService(gpsUtil, rewardsService);
	 * 
	 * User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	 * LocalDateTime localDateTime = LocalDateTime.now().minusDays(new
	 * Random().nextInt(30)); UserAttraction attraction =
	 * gpsUtil.getUserAttractions().get(0); user.addToVisitedLocations(new
	 * VisitedLocation(user.getUserId(), new
	 * Location(attraction.getAttractionLattitude(),
	 * attraction.getAttractionLongitude()),
	 * Date.from(localDateTime.toInstant(ZoneOffset.UTC))));
	 * 
	 * VisitedLocation visitedLocation =
	 * user.getVisitedLocations().get(user.getVisitedLocations().size() - 1);
	 * UserLocation userLocation = gpsUtil.getLocation(user.getUserId());
	 * userLocation.setLatitude(attraction.getAttractionLattitude());
	 * userLocation.setLongitude(attraction.getAttractionLongitude());
	 * List<UserAttraction> attractions =
	 * tourService.getNearByAttractions2(userLocation);
	 * System.out.println("user.getVisitedLocations() size");
	 * System.out.println(user.getVisitedLocations().size());
	 * 
	 * System.out.println("**************** attractions");
	 * System.out.println(attractions.size()); tourService.tracker.stopTracker();
	 * 
	 * assertEquals(4, attractions.size()); }
	 */

	/*
	 * @Test public void getNearbyAttractions2() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral()); InternalTestHelper.setInternalUserNumber(0);
	 * TourService tourService = new TourService(gpsUtil, rewardsService);
	 * 
	 * User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	 * 
	 * StopWatch stopWatch = new StopWatch(); stopWatch.start();
	 * 
	 * 
	 * tourService.trackUserLocation(user); user.getVisitedLocations();
	 * VisitedLocation visitedLocation = user.getVisitedLocations().get(0);
	 * List<Attraction> attractions =
	 * tourService.getNearByAttractions(visitedLocation); stopWatch.stop();
	 * tourService.tracker.stopTracker();
	 * 
	 * 
	 * assertEquals(5, attractions.size()); }
	 */

}
