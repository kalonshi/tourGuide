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

	

	// OK test avec UserLocation 050522
	@Test
	public void getUserLocation3() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourService.trackUserLocations2(user);
		VisitedLocation visitedLocation = user.getLastVisitedLocation();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	// Test OK 06/05/22
	@Ignore
	@Test
	public void getUserLocationOriginalTestFixedTour() {
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtilService, rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourService.trackUserLocations2(user);
		
		tourService.tracker.stopTracker();

		assertTrue(user.getVisitedLocations().get(0).userId.equals(user.getUserId()));
	}

	
	// Test Api simuler OK 080522******************************
	@Test
	public void getUserLocationOriginalTestFixedTour2() {
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtilService, rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourService.trackUserLocation(user);
		 while(user.getVisitedLocations().isEmpty()) { try {
			  TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { } }
			 
		tourService.tracker.stopTracker();

		assertTrue(user.getVisitedLocations().get(0).userId.equals(user.getUserId()));
	}
	
	
	//***********************************************
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
		 InternalTestHelper.setInternalUserNumber(0); 
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

	
	// Test Ok avec UserLocation 0605
	@Test
	public void trackUser2() throws Exception {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		TourService tourService = new TourService(gpsUtil, rewardsService);
		InternalTestHelper.setInternalUserNumber(0);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		tourService.trackUserLocations2(user);

		VisitedLocation visitedLocation = user.getLastVisitedLocation();
		tourService.tracker.stopTracker();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}
	
	// Test  avec VisitedLocation OK 080522 11:26
		@Test
		public void trackUserVisitedLocation()  {
			GpsUtilService gpsUtil = new GpsUtilService();
			RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
			TourService tourService = new TourService(gpsUtil, rewardsService);
			InternalTestHelper.setInternalUserNumber(0);
			User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

			tourService.trackUserLocation(user);
			 while(user.getVisitedLocations().isEmpty()) { try {
				  TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { } }
				 
			VisitedLocation visitedLocation = user.getVisitedLocations().get(0);
		/*
		 * VisitedLocation visitedLocation = user.getLastVisitedLocation();
		 */tourService.tracker.stopTracker();

			assertEquals(user.getUserId(), visitedLocation.userId);
		}

// Test avec la methode getFiveclosest Attractions
	@Test
	public void getNearbyAttractionsUserAttration2() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		/* tourService.trackUserLocations2(user); */

		
		// Test VisitedLocation 080522
		tourService.trackUserLocation(user);
		 while(user.getVisitedLocations().isEmpty()) { try {
			  TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { } }
		// Fin de Test VisitedLocation 080522	
		
		 
		 List<RecommandedAttraction> attractions = tourService.getFiveClosestAttraction(user);
		/*
		 * System.out.println("user.getVisitedLocations() size");
		 * System.out.println(user.getVisitedLocations().size());
		 * 
		 * System.out.println("**************** attractions");
		 * System.out.println(attractions.size());
		 */
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
	
	
	
	
	// ok 040422*****Attention internalTestUser use for
		// test*************************
	@Ignore	
	@Test
		public void getUserLocation() {
			GpsUtilService gpsUtilService = new GpsUtilService();
			RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
			InternalTestHelper.setInternalUserNumber(1);
			TourService tourService = new TourService(gpsUtilService, rewardsService);
			User user = tourService.getAllUsers().get(0);

			tourService.tracker.stopTracker();

			assertTrue(user.getVisitedLocations().get(0).userId.equals(user.getUserId()));

		}
		
		/************* OK coverage 120422 ***********/
		// ok 040422*****Attention internalTestUser use for
	/* @Ignore */
	/*
	 * @Test public void trackUser() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral());
	 * 
	 * InternalTestHelper.setInternalUserNumber(0);
	 * 
	 * TourService tourService = new TourService(gpsUtil, rewardsService);
	 * 
	 * User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	 * 
	 * tourService.trackUserLocation(user); VisitedLocation visitedLocation =
	 * user.getLastVisitedLocation(); tourService.tracker.stopTracker();
	 * 
	 * assertEquals(user.getUserId(), visitedLocation.userId); }
	 */
}
