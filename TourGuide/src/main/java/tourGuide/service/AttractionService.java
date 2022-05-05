package tourGuide.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import tourGuide.user.RecommandedAttraction;
import tourGuide.user.User;
import tourGuide.user.UserAttraction;
import tourGuide.user.UserLocation;

@Service
public class AttractionService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private GpsUtilService gpsUtilService;

	private RewardsService rewardsService;

	/*
	 * // getAllAttractionDistanceFromALocation public HashMap<Attraction, Double>
	 * getAllAttractionDist(User user) { Location location =
	 * tourService.getUserLocation(user).location; List<Attraction> attractionList =
	 * gpsUtilService.getAttractions();
	 * 
	 * HashMap<Attraction, Double> attractionDists = new HashMap<Attraction,
	 * Double>();
	 * 
	 * attractionList.forEach(at -> { attractionDists.put(at, getDistance(at,
	 * location));
	 * 
	 * }); return attractionDists; }
	 * 
	 * Tri par distance croissante de toutes les attractions/user
	 * 
	 * public List<Entry<Attraction, Double>> getAttractionsFromAllDistance(User
	 * user) {
	 * 
	 * HashMap<Attraction, Double> getAllAttractionDist =
	 * getAllAttractionDist(user); List<Entry<Attraction, Double>>
	 * distAttrationsUser = new ArrayList<>(getAllAttractionDist.entrySet());
	 * distAttrationsUser.sort(Entry.comparingByValue()); return distAttrationsUser;
	 * 
	 * }
	 * 
	 * 
	 * Tri par distance croissante de N attractions, des plus proches au plus
	 * eloignées
	 * 
	 * 
	 * public List<Entry<Attraction, Double>> getNClosestAttractions(User user, int
	 * numberOfAttractions) { List<Entry<Attraction, Double>> getNClosestAttactions
	 * = new ArrayList<>(); if (getAttractionsFromAllDistance(user).size() >=
	 * numberOfAttractions) { } for (int i = 0; i < numberOfAttractions; i++) {
	 * getNClosestAttactions.add(getAttractionsFromAllDistance(user).get(i)); }
	 * return getNClosestAttactions; }
	 * 
	 * public List<UserAttraction> getNClosestAttractionsWithRewards(User user, int
	 * numberOfAttractions) { Location location =
	 * tourService.getUserLocation(user).location; Location lastVistedLocation =
	 * user.getVisitedLocations().get(user.getVisitedLocations().size() -
	 * 1).location;
	 * 
	 * List<Entry<Attraction, Double>> nClosestAttractions =
	 * getNClosestAttractions(user, numberOfAttractions); List<UserAttraction>
	 * ClosestAttractions = new ArrayList<UserAttraction>();
	 * nClosestAttractions.forEach(att -> { String attractionName =
	 * att.getKey().attractionName; double attractionLongitude =
	 * att.getKey().longitude; double attractionLattitude = att.getKey().latitude;
	 * double userLongitude = location.longitude; double userLattitude =
	 * location.latitude; double dist = att.getValue(); int rewardPoints =
	 * rewardsService.getRewardPoints(att.getKey(), user); UserAttraction
	 * newAttraction = new UserAttraction(attractionName, attractionLongitude,
	 * attractionLattitude, userLongitude, userLattitude, dist, rewardPoints);
	 * ClosestAttractions.add(newAttraction);
	 * 
	 * }); return ClosestAttractions;
	 * 
	 * }
	 */
	/*
	 * public UserAttraction getAttraction(String attractionName) { List<Attraction>
	 * attractions= gpsUtilService.getAttractions(); UserAttraction attraction= new
	 * UserAttraction(); for( Attraction userAttraction:attractions) {
	 * if(userAttraction.attractionName.matches(attractionName)) {
	 * attraction.setAttractionName(attractionName);
	 * attraction.setAttractionLongitude(userAttraction.longitude);
	 * attraction.setAttractionLattitude(userAttraction.latitude); } }
	 * 
	 * return attraction; }
	 */

	public List<Attraction> getAttractions() {
		return gpsUtilService.getAttractions();

	}

	/** Closest five attractions **/
	public List<RecommandedAttraction> getAllAttraction(User user) {
		List<RecommandedAttraction> recommandedAttractions = new ArrayList<>();

		Location lastVistedLocation = user.getVisitedLocations().get(user.getVisitedLocations().size() - 1).location;

		for (UserAttraction userAttraction : gpsUtilService.getUserAttractions()) {
			Location attractionLocation = new Location(userAttraction.getAttractionLattitude(),
					userAttraction.getAttractionLongitude());
			recommandedAttractions.add(new RecommandedAttraction(userAttraction.getAttractionName(),
					lastVistedLocation.latitude, lastVistedLocation.longitude, userAttraction.getAttractionLattitude(),
					userAttraction.getAttractionLongitude(),
					rewardsService.getDistance(lastVistedLocation, attractionLocation),
					rewardsService.getRewardPoints(userAttraction, user)));
		}

		Collections.sort(recommandedAttractions, new RecommandedAttractionDistanceComparator());
		return recommandedAttractions;
	}

	public List<RecommandedAttraction> getFiveClosestAttraction(User user) {
		List<RecommandedAttraction> recommandedAttractions = getAllAttraction(user);
		List<RecommandedAttraction> fiveClosestAttraction = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			fiveClosestAttraction.add(new RecommandedAttraction(recommandedAttractions.get(i).getAttractionName(),
					recommandedAttractions.get(i).getUserLat(), recommandedAttractions.get(i).getUserLong(),
					recommandedAttractions.get(i).getAttractionLat(), recommandedAttractions.get(i).getAttractionLong(),
					recommandedAttractions.get(i).getDistance(), recommandedAttractions.get(i).getRewardPoint()));
		}

		return fiveClosestAttraction;
	}

	public double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math
				.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}
}
