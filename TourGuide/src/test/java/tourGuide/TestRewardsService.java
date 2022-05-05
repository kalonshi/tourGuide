package tourGuide;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import tourGuide.service.TourService;

import tourGuide.user.User;
import tourGuide.user.UserAttraction;
import tourGuide.user.UserReward;

public class TestRewardsService {
	private ExecutorService executor = Executors.newFixedThreadPool(1000);

//test Ok reward = 1 040522
	@Test
	public void usersGetRewards() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtilService, rewardsService);
		tourService.addUser(user);
		assertTrue(user.getUserRewards().size() == 0);

		Attraction attraction = gpsUtilService.getAttractions().get(0);
		user.clearVisitedLocations();
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		rewardsService.calculateRewards(user);

		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}

		tourService.tracker.stopTracker();
		List<UserReward> userRewards = user.getUserRewards();
		System.out.println("userRewards.size():  " + userRewards.size());
		assertTrue(userRewards.size() == 1);
	}

	// OK 20/04 test reward>0
	@Test
	public void usersGetRewardsTest() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourService tourService = new TourService(gpsUtilService, rewardsService);
		tourService.addUser(user);
		assertTrue(user.getUserRewards().size() == 0);

		Attraction attraction = gpsUtilService.getAttractions().get(0);
		user.clearVisitedLocations();
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

		rewardsService.calculateRewards(user);
		
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		tourService.tracker.stopTracker();
		List<UserReward> userRewards = user.getUserRewards();

		System.out.println("userRewards.size():  " + userRewards.size());
		System.out.println("userRewards:  " + userRewards.get(0).getRewardPoints());
		assertTrue(userRewards.size() > 0);
	}
// testb Ok  setProximityBuffer(5) au lieu setProximityBuffer(10)  0505
	@Test
	public void nearAllAttractionsTest() {
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		rewardsService.setProximityBuffer(5);
		TourService tourService = new TourService(gpsUtilService, rewardsService);
		User user = tourService.getAllUsers().get(0);

		assertTrue(user.getUserRewards().size() == 0);

		user.clearVisitedLocations();
		gpsUtilService.getAttractions().forEach(attract -> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attract, new Date()));

		});

		rewardsService.calculateRewards(user);

		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}

		tourService.tracker.stopTracker();
		List<UserReward> userRewards = user.getUserRewards();
		System.out.println("userRewards.size():  " + userRewards.size());
		assertTrue(gpsUtilService.getAttractions().size() == userRewards.size());
	}
	
	
	
	
	
	// ok 240222***********************************************************
	/*
	 * @Test public void isWithinAttractionProximity() { GpsUtilService
	 * gpsUtilService = new GpsUtilService(); RewardsService rewardsService = new
	 * RewardsService(gpsUtilService, new RewardCentral()); Attraction attraction =
	 * gpsUtilService.getAttractions().get(0);
	 * assertTrue(rewardsService.isWithinAttractionProximity(attraction,
	 * attraction)); }
	 */

	/* @Ignore */ // Needs fixed - can throw ConcurrentModificationException

	// ok 28/0422
	/*
	 * @Test public void nearAllAttractions() throws InterruptedException {
	 * GpsUtilService gpsUtilService = new GpsUtilService(); RewardsService
	 * rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
	 * rewardsService.setProximityBuffer(Integer.MAX_VALUE);
	 * InternalTestHelper.setInternalUserNumber(1); TourService tourService = new
	 * TourService(gpsUtilService, rewardsService); List<Attraction> attractions =
	 * gpsUtilService.getAttractions(); System.out.println("Nb attractions = " +
	 * attractions.size());
	 * 
	 * User user = tourService.getAllUsers().get(0); user.clearVisitedLocations();
	 * System.out.println("Nb VisitedLocations = " +
	 * user.getVisitedLocations().size());
	 * 
	 * attractions.forEach(att -> {
	 * 
	 * user.addToVisitedLocations(new VisitedLocation(user.getUserId(), att, new
	 * Date())); });
	 * 
	 * System.out.println("Nb visited location = " +
	 * user.getVisitedLocations().size());
	 * 
	 * rewardsService.calculateRewards(user); try {
	 * TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { }
	 * 
	 * 
	 * List<UserReward> userRewards =
	 * tourService.getUserRewards(tourService.getAllUsers().get(0));
	 * 
	 * System.out.println("List reward size =" + userRewards.size());
	 * userRewards.forEach( reward -> System.out.println("reward Attration name : "
	 * + reward.userAttraction.getAttractionName()));
	 * 
	 * System.out.println("getRewardPoints" + userRewards.get(0).getRewardPoints());
	 * System.out.println("user.getUserRewards().size() = " +
	 * user.getUserRewards().size()); tourService.tracker.stopTracker();
	 * 
	 * assertTrue(gpsUtilService.getAttractions().size() < userRewards.size());
	 * System.out.println("user reward size");
	 * System.out.println(user.getUserRewards().size());
	 * 
	 * 
	 * assertEquals(gpsUtilService.getAttractions().size(), userRewards.size()); }
	 */
	/*
	 * @Test public void nearAllAttractions2() throws InterruptedException {
	 * GpsUtilService gpsUtilService = new GpsUtilService(); RewardsService
	 * rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
	 * rewardsService.setProximityBuffer(Integer.MAX_VALUE);
	 * 
	 * InternalTestHelper.setInternalUserNumber(1); TourService tourService = new
	 * TourService(gpsUtilService, rewardsService);
	 * 
	 * List<Attraction> attractions = gpsUtilService.getAttractions();
	 * System.out.println("Nb attractions = " + attractions.size());
	 * 
	 * User user = tourService.getAllUsers().get(0);
	 * 
	 * System.out.println("Nb VisitedLocations = " +
	 * user.getVisitedLocations().size());
	 * 
	 * System.out.println("Nb visited location = " +
	 * user.getVisitedLocations().size());
	 * rewardsService.calculateRewards(tourService.getAllUsers().get(0)); try {
	 * TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { }
	 * 
	 * List<UserReward> userRewards =
	 * tourService.getUserRewards(tourService.getAllUsers().get(0));
	 * 
	 * 
	 * 
	 * System.out.println("getRewardPoints" + userRewards.get(0).getRewardPoints());
	 * System.out.println("user.getUserRewards().size() = " +
	 * user.getUserRewards().size()); tourService.tracker.stopTracker();
	 * 
	 * assertTrue(gpsUtilService.getAttractions().size() < userRewards.size());
	 * System.out.println("user reward size");
	 * System.out.println(user.getUserRewards().size());
	 * 
	 * 
	 * assertEquals(gpsUtilService.getAttractions().size(), userRewards.size()); }
	 * 
	 */
	/* @Ignore */ // Needs fixed - can throw ConcurrentModificationException
	/*
	 * @Test public void nearAllAttractions() { GpsUtilService gpsUtilService = new
	 * GpsUtilService(); RewardsService rewardsService = new
	 * RewardsService(gpsUtilService, new RewardCentral());
	 * rewardsService.setProximityBuffer(Integer.MAX_VALUE);
	 * 
	 * InternalTestHelper.setInternalUserNumber(1); TourService tourGuideService =
	 * new TourService(gpsUtilService, rewardsService);
	 * 
	 * rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
	 * List<UserReward> userRewards =
	 * tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
	 * tourGuideService.tracker.stopTracker();
	 * 
	 * assertEquals(gpsUtilService.getAttractions().size(), userRewards.size()); }
	 */
}
