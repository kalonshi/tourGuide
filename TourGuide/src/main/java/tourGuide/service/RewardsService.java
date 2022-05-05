package tourGuide.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserAttraction;
import tourGuide.user.UserLocation;
import tourGuide.user.UserReward;

@Service
public class RewardsService {
	 private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

		private  int proximityBufferMiles = 10;
		
		private final RewardCentral rewardsCentral;
		
		private final GpsUtilService gpsUtilService;
		
		private ExecutorService executor = Executors.newFixedThreadPool(1000);

		public RewardsService(GpsUtilService gpsUtilService, RewardCentral rewardCentral) {
			this.rewardsCentral = rewardCentral;
			this.gpsUtilService = gpsUtilService;
		}
		
		public void setProximityBuffer(int proximityBuffer) {
			this.proximityBufferMiles = proximityBuffer;
		}
		
		public void calculateRewards(User user) {
			List<Attraction> attractions = gpsUtilService.getAttractions();
			List<VisitedLocation> visitedLocationList = user.getVisitedLocations().stream().collect(Collectors.toList());
			int i=0;
		/* int j=0; */
			for(VisitedLocation visitedLocation : visitedLocationList) {
				System.out.println("VisitedLocation visitedLocation latitude="+visitedLocation.location.latitude);
				System.out.println("nb de visited location : "+i);
				i++;
				for(Attraction attraction : attractions) {
					System.out.println("attraction.attractionName="+attraction.attractionName);
				/*
				 * System.out.println("attractionId="+attraction.attractionId);
				 * 
				 * System.out.println("user.getUserRewards() size ="+user.getUserRewards().size(
				 * ));
				 */
					System.out.println("user.getUserRewards() size ="+user.getUserRewards().size());
					if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
						calculateDistanceReward(user, visitedLocation, attraction);
					/*
					 * System.out.println("nb reward : "+j); j++;
					 */
				 }
				}
			}
		}
		
		public void calculateDistanceReward(User user, VisitedLocation visitedLocation, Attraction attraction) {
			Double distance = getDistance(attraction, visitedLocation.location);
			System.out.println("distance :"+distance+"<= proximityBufferMiles"+ proximityBufferMiles);
			if(distance <= proximityBufferMiles) {
				UserReward userReward = new UserReward(visitedLocation, attraction, distance.intValue());
				submitRewardPoints(userReward, attraction, user);
				System.out.println("attraction reward: "+attraction.attractionName);
			}
		}
		
		private void submitRewardPoints(UserReward userReward, Attraction attraction, User user) {
			//userReward.setRewardPoints(10);
			//user.addUserReward(userReward);
			CompletableFuture.supplyAsync(() -> {
			    return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
			}, executor)
				.thenAccept(points -> { 
					userReward.setRewardPoints(points);
					user.addUserReward(userReward);
				});
		}
		private int submitRewardPoints2(UserReward userReward, Attraction attraction, User user) {
			//userReward.setRewardPoints(10);
			//user.addUserReward(userReward);
			CompletableFuture.supplyAsync(() -> {
			    return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
			}, executor)
				.thenAccept(points -> { 
					userReward.setRewardPoints(points);
					user.addUserReward(userReward);
				});
			int index=user.getUserRewards().size()-1;
			return user.getUserRewards().get(index).getRewardPoints();
		}
		public int getRewardPoints(UserAttraction attraction, User user) {
			
			 return rewardsCentral.getAttractionRewardPoints(attraction.getAttractionId(),
			 user.getUserId()); }
		
		public double getDistance(Location loc1, Location loc2) {
	        double lat1 = Math.toRadians(loc1.latitude);
	        double lon1 = Math.toRadians(loc1.longitude);
	        double lat2 = Math.toRadians(loc2.latitude);
	        double lon2 = Math.toRadians(loc2.longitude);

	        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
	                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

	        double nauticalMiles = 60 * Math.toDegrees(angle);
	        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	        return statuteMiles;
		}
		
	/// ***********************************************************
	/*
	 * private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	 * private final Logger logger = LoggerFactory.getLogger(RewardsService.class);
	 * // proximity in miles private int proximityBufferMiles = 10; private int
	 * defaultProximityBuffer = 10; private int proximityBuffer =
	 * defaultProximityBuffer; private int attractionProximityRange = 200; private
	 * final GpsUtilService gpsUtilService; private TourService tourService; private
	 * final RewardCentral rewardsCentral; private ExecutorService executor =
	 * Executors.newFixedThreadPool(1000);
	 */
	/*
	 * public RewardsService(GpsUtilService gpsUtilService, TourService tourService,
	 * RewardCentral rewardsCentral) { super(); this.gpsUtilService =
	 * gpsUtilService; this.tourService = tourService; this.rewardsCentral =
	 * rewardsCentral; }
	 */
	/*
	 * public RewardsService(GpsUtilService gpsUtilService, RewardCentral
	 * rewardCentral) {
	 * 
	 * this.gpsUtilService = gpsUtilService; this.rewardsCentral = rewardCentral; }
	 * 
	 * public void setProximityBuffer(int proximityBuffer) { this.proximityBuffer =
	 * proximityBuffer; }
	 * 
	 * public void setDefaultProximityBuffer() { proximityBuffer =
	 * defaultProximityBuffer; }
	 */
	/*
	 * public void calculateRewards(User user) {
	 * 
	 * List<UserAttraction> userAttractions = gpsUtilService.getUserAttractions();
	 * List<VisitedLocation> visitedLocationList =
	 * user.getVisitedLocations().stream().collect(Collectors.toList()); for
	 * (VisitedLocation visitedLocation : visitedLocationList) { for (UserAttraction
	 * userAttraction : userAttractions) { if (user.getUserRewards().stream()
	 * .filter(r ->
	 * r.userAttraction.getAttractionName().equals(userAttraction.getAttractionName(
	 * ))) .count() == 0) {
	 * 
	 * if (nearAttraction(visitedLocation, userAttraction)) { user.addUserReward(
	 * new UserReward(visitedLocation, userAttraction,
	 * getRewardPoints(userAttraction, user))); System.out.println("new rewards"); }
	 * } } }
	 * 
	 * }
	 */
	/*
	 * public void calculateRewards(User user) { List<Attraction> attractions =
	 * gpsUtilService.getAttractions(); List<VisitedLocation> visitedLocationList =
	 * user.getVisitedLocations().stream().collect(Collectors.toList()); for
	 * (VisitedLocation visitedLocation : visitedLocationList) { for (Attraction
	 * attraction : attractions) { if (user.getUserRewards().stream() .filter(r ->
	 * r.attraction.attractionName.equals(attraction.attractionName)).count() == 0)
	 * { calculateDistanceReward(user, visitedLocation, attraction); } } } }
	 */
	/*
	 * public void calculateRewards(User user) {
	 * 
	 * List<UserAttraction> userAttractions = gpsUtilService.getUserAttractions();
	 * List<VisitedLocation> visitedLocationList =
	 * user.getVisitedLocations().stream().collect(Collectors.toList()); for
	 * (VisitedLocation visitedLocation : visitedLocationList) { for (UserAttraction
	 * userAttraction : userAttractions) { if (user.getUserRewards().stream()
	 * .filter(r ->
	 * r.userAttraction.getAttractionName().equals(userAttraction.getAttractionName(
	 * ))) .count() == 0) { calculateDistanceReward(user, visitedLocation,
	 * userAttraction);
	 * 
	 * if (nearAttraction(visitedLocation, userAttraction)) { user.addUserReward(
	 * new UserReward(visitedLocation, userAttraction,
	 * getRewardPoints(userAttraction, user))); System.out.println("new rewards"); }
	 * } } }
	 */
	/*
	 * public void calculateDistanceReward(User user, VisitedLocation
	 * visitedLocation, Attraction attraction) { Double distance =
	 * getDistance(attraction, visitedLocation.location); if(distance <=
	 * proximityBufferMiles) { UserReward userReward = new
	 * UserReward(visitedLocation, attraction, distance.intValue());
	 * submitRewardPoints(userReward, attraction, user); } }
	 */
	/*
	 * private boolean nearAttraction(VisitedLocation visitedLocation,
	 * UserAttraction userAttraction) { Location loc1 = new
	 * Location(visitedLocation.location.latitude,
	 * visitedLocation.location.longitude); Location loc2 = new
	 * Location(userAttraction.getAttractionLattitude(),
	 * userAttraction.getAttractionLongitude()); return getDistance(loc1, loc2) >
	 * proximityBuffer ? false : true;
	 * 
	 * 
	 * return getDistance2(userAttraction, visitedLocation.location) >
	 * proximityBuffer ? false : true; }
	 */
	/*
	 * public void calculateRewards(User user) { List<UserAttraction>
	 * userAttractions = gpsUtilService.getUserAttractions();
	 * System.out.println("calculateRewards(User user) userAttractions size =" +
	 * userAttractions.size());
	 * 
	 * List<VisitedLocation> visitedLocationList =
	 * user.getVisitedLocations().stream().collect(Collectors.toList());
	 * 
	 * List<VisitedLocation> visitedLocationList = user.getVisitedLocations();
	 * 
	 * System.out.println(" calculateRewards(User user) visitedLocationList = " +
	 * visitedLocationList.size()); for (VisitedLocation visitedLocation :
	 * visitedLocationList) { int i=0; for (UserAttraction userAttraction :
	 * userAttractions) {
	 * 
	 * 
	 * System.out.println("userAttractions List size"+userAttractions.size());
	 * 
	 * System.out.println("numero: "+i+"  userAttraction name:"+userAttraction.
	 * getAttractionName());
	 * System.out.println("  visitedLocation lat et long:"+visitedLocation.location.
	 * latitude+"longitude :"+visitedLocation.location.longitude);
	 * 
	 * i++; if (user.getUserRewards().stream() .filter(r ->
	 * r.userAttraction.getAttractionName().equals(userAttraction.getAttractionName(
	 * ))) .count() == 0 ) {
	 * 
	 * calculateDistanceReward(user, visitedLocation, userAttraction);
	 * 
	 * } } } }
	 */

	private int getDistance2(UserAttraction userAttraction, Location location) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * public void calculateDistanceReward(User user, VisitedLocation
	 * visitedLocation, UserAttraction userAttraction) { Double distance =
	 * getDistances2(visitedLocation, userAttraction); if (distance <=
	 * proximityBufferMiles) { UserReward userReward = new
	 * UserReward(visitedLocation, userAttraction, distance.intValue());
	 * submitRewardPoints(userReward, userAttraction, user); } }
	 */
	/*
	 * public boolean isWithinAttractionProximity(Attraction attraction, Location
	 * location) { return getDistance(attraction, location) >
	 * attractionProximityRange ? false : true; }
	 * 
	 * public boolean isWithinAttractionProximity2(UserAttraction userAttraction,
	 * UserLocation userLocation) { return getDistance2(userAttraction,
	 * userLocation) > attractionProximityRange ? false : true; }
	 * 
	 * private double getDistance2(UserAttraction userAttraction, UserLocation
	 * userLocation) { double lat2 = Math.toRadians(userLocation.getLatitude());
	 * double lon2 = Math.toRadians(userLocation.getLongitude()); double lat1 =
	 * Math.toRadians(userAttraction.getAttractionLattitude()); double lon1 =
	 * Math.toRadians(userAttraction.getAttractionLongitude());
	 * 
	 * double angle = Math .acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) *
	 * Math.cos(lat2) * Math.cos(lon1 - lon2));
	 * 
	 * double nauticalMiles = 60 * Math.toDegrees(angle); double statuteMiles =
	 * STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	 * System.out.println("statuteMiles" + statuteMiles); return statuteMiles; }
	 */

	/*
	 * public int getRewardPoints(UserAttraction attraction, User user) {
	 * 
	 * return rewardsCentral.getAttractionRewardPoints(attraction.getAttractionId(),
	 * user.getUserId()); }
	 * 
	 * private void submitRewardPoints(UserReward userReward, UserAttraction
	 * userAttraction, User user) {
	 * 
	 * userReward.setRewardPoints(10); user.addUserReward(userReward);
	 * 
	 * 
	 * CompletableFuture.supplyAsync(() -> { return
	 * rewardsCentral.getAttractionRewardPoints(userAttraction.getAttractionId(),
	 * user.getUserId()); }, executor).thenAccept(points -> {
	 * userReward.setRewardPoints(points); user.addUserReward(userReward);
	 * System.out.println(" Comaletable userReward n° :" +
	 * userReward.getRewardPoints()); });
	 */
	/*
	 * System.out.println("userReward n° :"+userReward.getRewardPoints());
	 */
	/*
	 * int points=rewardsCentral.getAttractionRewardPoints(userAttraction.
	 * getAttractionId(), user.getUserId());; userReward.setRewardPoints(points);
	 * user.addUserReward(userReward);
	 */
	/* } */

	/*
	 * public double getDistance(Location loc1, Location loc2) { double lat1 =
	 * Math.toRadians(loc1.latitude); double lon1 = Math.toRadians(loc1.longitude);
	 * double lat2 = Math.toRadians(loc2.latitude); double lon2 =
	 * Math.toRadians(loc2.longitude);
	 * 
	 * double angle = Math .acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) *
	 * Math.cos(lat2) * Math.cos(lon1 - lon2));
	 * 
	 * double nauticalMiles = 60 * Math.toDegrees(angle); double statuteMiles =
	 * STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles; return statuteMiles; }
	 * 
	 * public double getDistances(UserLocation userLocation, UserAttraction
	 * userAttraction) { double lat1 = Math.toRadians(userLocation.getLatitude());
	 * double lon1 = Math.toRadians(userLocation.getLongitude()); double lat2 =
	 * Math.toRadians(userAttraction.getAttractionLattitude()); double lon2 =
	 * Math.toRadians(userAttraction.getAttractionLongitude());
	 * 
	 * double angle = Math .acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) *
	 * Math.cos(lat2) * Math.cos(lon1 - lon2));
	 * 
	 * double nauticalMiles = 60 * Math.toDegrees(angle); double statuteMiles =
	 * STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles; double statueKilometers =
	 * statuteMiles * 1.6;
	 * 
	 * return statueKilometers; }
	 * 
	 * public double getDistances2(VisitedLocation visitedLocation, UserAttraction
	 * userAttraction) { double lat2 =
	 * Math.toRadians(visitedLocation.location.latitude); double lon2 =
	 * Math.toRadians(visitedLocation.location.longitude); double lat1 =
	 * Math.toRadians(userAttraction.getAttractionLattitude()); double lon1 =
	 * Math.toRadians(userAttraction.getAttractionLongitude());
	 * 
	 * double angle = Math .acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) *
	 * Math.cos(lat2) * Math.cos(lon1 - lon2));
	 * 
	 * double nauticalMiles = 60 * Math.toDegrees(angle); double statuteMiles =
	 * STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles; double statueKilometers =
	 * statuteMiles * 1.6;
	 * 
	 * return statuteMiles; }
	 */
}
