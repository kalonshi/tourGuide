package tourGuide;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
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
import tourGuide.user.UserLocation;
import tourGuide.user.UserReward;

public class TestPerformance {

	/*
	 * A note on performance improvements:
	 * 
	 * The number of users generated for the high volume tests can be easily
	 * adjusted via this method:
	 * 
	 * InternalTestHelper.setInternalUserNumber(100000);
	 * 
	 * 
	 * These tests can be modified to suit new solutions, just as long as the
	 * performance metrics at the end of the tests remains consistent.
	 * 
	 * These are performance metrics that we are trying to hit:
	 * 
	 * highVolumeTrackLocation: 100,000 users within 15 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 *
	 * highVolumeGetRewards: 100,000 users within 20 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	/* @Ignore */
	@Test
	public void highVolumeTrackLocation() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15
		// minutes
		InternalTestHelper.setInternalUserNumber(100000);
		TourService tourService = new TourService(gpsUtil, rewardsService);

		List<User> allUsers = tourService.getAllUsers();

		System.out.println("allUsers size = " + allUsers.size());
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int i = 0;
		for (User user : allUsers) {
			tourService.trackUserLocation(user);

			System.out.println("visited Location =" + user.getVisitedLocations().get(0) + " n°" + i);
			System.out.println("visited Location latitude =" + user.getVisitedLocations().get(0).location.latitude);
			i++;
		}
		stopWatch.stop();
		tourService.tracker.stopTracker();

		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeGetRewards6() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		// Users should be incremented up to 100,000, and test finishes within 20
		// minutes

		InternalTestHelper.setInternalUserNumber(10000);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		TourService tourGuideService = new TourService(gpsUtil, rewardsService);
		List<Attraction> attractions = gpsUtil.getAttractions();
		UserAttraction userAttraction = gpsUtil.getUserAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();

		allUsers.forEach(u -> u.addToUserLocations(new UserLocation(u.getUserId(),
				userAttraction.getAttractionLattitude(), userAttraction.getAttractionLongitude(), new Date())));

		allUsers.forEach(u -> {
			u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attractions.get(0), new Date()));
			u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attractions.get(1), new Date()));
			u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attractions.get(2), new Date()));
		});

		allUsers.forEach(u -> rewardsService.calculateRewards(u));
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}
		for (User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracker();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
				+ " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	/*
	 * @Test public void highVolumeTrackLocation2() throws InterruptedException,
	 * ExecutionException { GpsUtilService gpsUtil = new GpsUtilService();
	 * RewardsService rewardsService = new RewardsService(gpsUtil, new
	 * RewardCentral()); // Users should be incremented up to 100,000, and test
	 * finishes within 15 // minutes
	 * 
	 * TourService tourService = new TourService(gpsUtil, rewardsService);
	 * InternalTestHelper.setInternalUserNumber(1); List<User> allUsers =
	 * tourService.getAllUsers(); StopWatch stopWatch = new StopWatch();
	 * stopWatch.start(); for (User user : allUsers) {
	 * tourService.trackUserLocation(user); }
	 * 
	 * for (User user : allUsers) { while (user.getVisitedLocations().size() < 4) {
	 * try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { }
	 * } }
	 * 
	 * for (User user : allUsers) { VisitedLocation visitedLocation =
	 * user.getVisitedLocations().get(3); assertTrue(visitedLocation != null); }
	 * tourService.tracker.stopTracker();
	 * 
	 * stopWatch.stop();
	 * System.out.println("highVolumeTrackLocation: Time Elapsed: " +
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
	 * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())); }
	 */

	/* @Ignore */

	/*
	 * @Test public void highVolumeGetRewards() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral()); Attraction attraction =
	 * gpsUtil.getAttractions().get(0);
	 * 
	 * // Users should be incremented up to 100,000, and test finishes within 20 //
	 * minutes InternalTestHelper.setInternalUserNumber(100);
	 * 
	 * TourService tourService = new TourService(gpsUtil, rewardsService);
	 * List<User> allUsers = tourService.getAllUsers(); StopWatch stopWatch = new
	 * StopWatch(); stopWatch.start(); for (User u : allUsers) {
	 * u.clearVisitedLocations(); u.addToVisitedLocations(new
	 * VisitedLocation(u.getUserId(), attraction, new Date())); } for (User user :
	 * allUsers) {
	 * 
	 * while (user.getUserRewards().isEmpty()) {
	 * rewardsService.calculateRewards(user); try {
	 * TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { }
	 * 
	 * } } tourService.tracker.stopTracker();
	 * 
	 * stopWatch.stop(); System.out.println("Seconds ellapsed: " +
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 * 
	 * for (User user : allUsers) {
	 * assertTrue(tourService.getUserRewards(user).size() >= 1); }
	 * assertTrue(TimeUnit.MINUTES.toSeconds(30) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())); }
	 * 
	 * 
	 * allUsers.forEach(u -> { u.clearVisitedLocations();
	 * u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new
	 * Date())); });
	 * 
	 * 
	 * allUsers.forEach(u -> rewardsService.calculateRewards(u));
	 * 
	 * for (User user : allUsers) { assertTrue(user.getUserRewards().size() > 1); }
	 * stopWatch.stop(); tourService.tracker.stopTracker();
	 * 
	 * System.out.println("highVolumeGetRewards: Time Elapsed: " +
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())); }
	 * 
	 * @Ignore
	 * 
	 * @Test public void highVolumeGetRewards2() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral()); Attraction attraction =
	 * gpsUtil.getAttractions().get(0); UserAttraction userAttraction =
	 * gpsUtil.getUserAttractions().get(0); Attraction attraction = new
	 * Attraction(userAttraction.getAttractionName(), userAttraction.getCity(),
	 * userAttraction.getState(), userAttraction.getAttractionLattitude(),
	 * userAttraction.getAttractionLongitude()); // Users should be incremented up
	 * to 100,000, and test finishes within 20 minutes//
	 * InternalTestHelper.setInternalUserNumber(100); StopWatch stopWatch = new
	 * StopWatch(); stopWatch.start(); TourService tourGuideService = new
	 * TourService(gpsUtil, rewardsService);
	 * 
	 * List<User> allUsers = new ArrayList<>(); allUsers =
	 * tourGuideService.getAllUsers(); allUsers.forEach(u ->
	 * u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new
	 * Date())));
	 * 
	 * allUsers.forEach(u -> rewardsService.calculateRewards(u));
	 * 
	 * for (User user : allUsers) { assertTrue(user.getUserRewards().size() > 0); }
	 * stopWatch.stop(); tourGuideService.tracker.stopTracker();
	 * 
	 * System.out.println("highVolumeGetRewards: Time Elapsed: " +
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())); }
	 * 
	 * @Test public void highVolumeGetRewards3() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral());
	 * 
	 * // Users should be incremented up to 100,000, and test finishes within 20
	 * minutes //
	 * 
	 * InternalTestHelper.setInternalUserNumber(100); StopWatch stopWatch = new
	 * StopWatch(); stopWatch.start(); TourService tourGuideService = new
	 * TourService(gpsUtil, rewardsService);
	 * 
	 * UserAttraction userAttraction = gpsUtil.getUserAttractions().get(0);
	 * List<User> allUsers = new ArrayList<>(); allUsers =
	 * tourGuideService.getAllUsers();
	 * 
	 * allUsers.forEach(u -> u.addToUserLocations(new UserLocation(u.getUserId(),
	 * userAttraction.getAttractionLattitude(),
	 * userAttraction.getAttractionLongitude(), new Date()))); allUsers.forEach(u ->
	 * rewardsService.calculateRewards(u)); for (User user : allUsers) {
	 * assertTrue(user.getUserRewards().size() > 0); } stopWatch.stop();
	 * tourGuideService.tracker.stopTracker();
	 * 
	 * System.out.println("highVolumeGetRewards: Time Elapsed: " +
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())); }
	 * 
	 * @Test public void highVolumeGetRewards4() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral()); Attraction attraction =
	 * gpsUtil.getAttractions().get(0); UserAttraction userAttraction =
	 * gpsUtil.getUserAttractions().get(0); Attraction attraction = new
	 * Attraction(userAttraction.getAttractionName(), userAttraction.getCity(),
	 * userAttraction.getState(), userAttraction.getAttractionLattitude(),
	 * userAttraction.getAttractionLongitude());
	 * 
	 * InternalTestHelper.setInternalUserNumber(100); TourService tourGuideService =
	 * new TourService(gpsUtil, rewardsService); List<User> allUsers =
	 * tourGuideService.getAllUsers(); StopWatch stopWatch = new StopWatch();
	 * stopWatch.start(); for (User user : allUsers) { user.clearVisitedLocations();
	 * user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction,
	 * new Date())); rewardsService.calculateRewards(user);
	 * System.out.println("user.getUserRewards()" + user.getUserRewards()); try {
	 * TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { }
	 * 
	 * }
	 * 
	 * 
	 * for (User user : allUsers) { rewardsService.calculateRewards(user);
	 * 
	 * 
	 * while (user.getUserRewards().isEmpty()) {
	 * 
	 * try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { }
	 * }
	 * 
	 * }
	 * 
	 * 
	 * tourGuideService.tracker.stopTracker();
	 * 
	 * stopWatch.stop(); System.out.println("Seconds ellapsed: " +
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 * 
	 * for (User user : allUsers) {
	 * assertTrue(tourGuideService.getUserRewards(user).size() >= 1); }
	 * assertTrue(TimeUnit.MINUTES.toSeconds(30) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())); }
	 * 
	 * //Ok mais trop long
	 * 
	 * @Test public void highVolumeGetRewards5() { GpsUtilService gpsUtil = new
	 * GpsUtilService(); RewardsService rewardsService = new RewardsService(gpsUtil,
	 * new RewardCentral());
	 * 
	 * UserAttraction userAttraction = gpsUtil.getUserAttractions().get(0);
	 * Attraction attraction = new Attraction(userAttraction.getAttractionName(),
	 * userAttraction.getCity(), userAttraction.getState(),
	 * userAttraction.getAttractionLattitude(),
	 * userAttraction.getAttractionLongitude());
	 * 
	 * InternalTestHelper.setInternalUserNumber(100); TourService tourGuideService =
	 * new TourService(gpsUtil, rewardsService); List<User> allUsers =
	 * tourGuideService.getAllUsers(); StopWatch stopWatch = new StopWatch();
	 * stopWatch.start(); for (User user : allUsers) { user.clearVisitedLocations();
	 * user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction,
	 * new Date()));
	 * 
	 * }
	 * 
	 * for (User user : allUsers) { tourGuideService.addUser(user);
	 * 
	 * rewardsService.calculateRewards(user);
	 * System.out.println("user.getUserRewards()" + user.getUserRewards()); try {
	 * TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { }
	 * 
	 * tourGuideService.tracker.stopTracker(); }
	 * 
	 * stopWatch.stop(); System.out.println("Seconds ellapsed: " +
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 * 
	 * for (User user : allUsers) {
	 * assertTrue(tourGuideService.getUserRewards(user).size() >= 1); }
	 * assertTrue(TimeUnit.MINUTES.toSeconds(30) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())); }
	 * 
	 */
}
