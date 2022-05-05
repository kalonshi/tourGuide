package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.RecommandedAttraction;
import tourGuide.user.User;
import tourGuide.user.UserAttraction;
import tourGuide.user.UserLocation;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourService {

	private Logger logger = LoggerFactory.getLogger(TourService.class);
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private final GpsUtilService gpsUtilService;
	private final RewardsService rewardsService;

	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	ExecutorService executor = Executors.newFixedThreadPool(1000);
	// Fixed Thread Pool : un pool qui contient un nombre fixe de threads. Ceux-ci
	// sont utilisés pour exécuter en parallèle les différentes tâches. Si tous les
	// threads sont occupés alors la tâche est empilée jusqu'à ce qu'elle puisse
	// être exécutée par un thread
	boolean testMode = true;

	public TourService(GpsUtilService gpsUtilService, RewardsService rewardsService) {
		this.gpsUtilService = gpsUtilService;
		this.rewardsService = rewardsService;

		if (testMode) {
			logger.info("TestMode enabled");

			logger.debug("Initializing users");
			initializeInternalUsers();

			logger.debug("Finished initializing users");

		}
		tracker = new Tracker(this);
		initializeTripPricer();
		addShutDownHook();
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutdown UserService");
				tracker.stopTracker();
			}
		});
	}

	private void initializeTripPricer() {
		logger.debug("Initialize tripPricer");
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

//test userLocation Ok 050522
	public UserLocation trackUserLocations2(User user) {
		
		UserLocation userLocation = gpsUtilService.getUserLocation2(user.getUserId());
		Location location= new Location(userLocation.getLatitude(), userLocation.getLongitude());
		VisitedLocation visitedLocation=new VisitedLocation(user.getUserId(), location, new Date());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return userLocation;
	}
	// methode de recupération de la dernier visited Location si elle existe sinon
	// ajouter la localisation actuelle de l'user: Recupération de
	// l'object UserLocation

	public UserLocation getUserLocation2(User user) {
		UserLocation userLocation = (user.getUserLocations().size() > 0)
				? user.getUserLocations().get(user.getUserLocations().size() - 1)
				: trackUserLocations2(user);
		return userLocation;
	}

	// methode de recupération de la dernier visited Location si elle existe sinon
	// ajouter la localisation actuelle de l'user: Recupération de
	// l'object VisitedLocation

	/*
	 * public VisitedLocation getUserLocation(User user) { VisitedLocation
	 * visitedLocation = (user.getVisitedLocations().size() > 0) ?
	 * user.getLastVisitedLocation() : trackUserLocation(user); return
	 * visitedLocation; }
	 */
	public VisitedLocation getUserLocation(User user) {
		if (user.getVisitedLocations().size() > 0) {
			VisitedLocation visitedLocation = user.getLastVisitedLocation();
			return visitedLocation;
		}

		trackUserLocation(user);
		VisitedLocation visitedLocation = user.getLastVisitedLocation();
		return visitedLocation;
	}
	// Ajout de la localisation dans l'historique de localisation

	public void finalizeLocation(User user, VisitedLocation visitedLocation) {
		user.addToVisitedLocations(visitedLocation);
		System.out.println("liste des localisation=" + user.getVisitedLocations().size());
		rewardsService.calculateRewards(user);
		tracker.finalizeTrack(user);

	}

	// Test 02052022Ajout de la localisation dans l'historique de Userlocalisation

	/*
	 * public void finalizeLocation(User user, UserLocation userLocation) {
	 * user.addToVisitedLocations(new VisitedLocation(userLocation.getUserId(), new
	 * Location(userLocation.getLatitude(), userLocation.getLongitude()), new
	 * Date())); System.out.println("liste des localisation=" +
	 * user.getVisitedLocations().size()); rewardsService.calculateRewards(user);
	 * tracker.finalizeTrack(user);
	 * 
	 * }
	 */
	// Liste des recentes localisations des users JSON ARRAY

	public JSONArray getAllCurrentLocations() throws JSONException {

		List<User> users = getAllUsers();
		JSONArray allCurrentLocation = new JSONArray();
		JSONObject object = new JSONObject();

		for (int i = 0; i < users.size(); i++) {
			int lastIndex = (users.get(i).getVisitedLocations().size()) - 1;
			object.put("id", users.get(i).getUserId());
			object.put("longitude", users.get(i).getVisitedLocations().get(lastIndex).location.longitude);
			object.put("latitude", users.get(i).getVisitedLocations().get(lastIndex).location.latitude);
			allCurrentLocation.put(object);
		}
		return allCurrentLocation;
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

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}

	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	// Ajout de la localisation actuelle de l'User

	public void trackUserLocation(User user) {

		try {
			gpsUtilService.submitLocation(user, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Test UserLocation Ajout de la localisation actuelle de l'User

	/*
	 * public VisitedLocation trackUserLocation(User user) {
	 * 
	 * UserLocation userLocation=gpsUtilService.getLocation(user.getUserId());
	 * VisitedLocation visitedLocation = new
	 * VisitedLocation(userLocation.getUserId(), new
	 * Location(userLocation.getLatitude(), userLocation.getLongitude()), new
	 * Date());
	 * 
	 * user.addToVisitedLocations(visitedLocation);
	 * rewardsService.calculateRewards(user); return visitedLocation; }
	 */
	/*
	 * public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation)
	 * { List<Attraction> nearbyAttractions = new ArrayList<>(); for (Attraction
	 * attraction : gpsUtilService.getAttractions()) { if
	 * (rewardsService.isWithinAttractionProximity(attraction,
	 * visitedLocation.location)) { nearbyAttractions.add(attraction); } } return
	 * nearbyAttractions; }
	 * 
	 * public List<UserAttraction> getNearByAttractions2(UserLocation userLocation)
	 * { List<UserAttraction> nearbyAttractions = new ArrayList<>(); for
	 * (UserAttraction userAttraction : gpsUtilService.getUserAttractions()) { if
	 * (rewardsService.isWithinAttractionProximity2(userAttraction, userLocation)) {
	 * nearbyAttractions.add(userAttraction); } } return nearbyAttractions; } public
	 * List<UserAttraction> getNearByAttractions3(UserLocation userLocation) {
	 * List<UserAttraction> nearbyAttractions = new ArrayList<>();
	 * 
	 * for (UserAttraction userAttraction : gpsUtilService.getUserAttractions()) {
	 * if (rewardsService.isWithinAttractionProximity2(userAttraction,
	 * userLocation)) { nearbyAttractions.add(userAttraction); } } return
	 * nearbyAttractions; }
	 */

	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes
	// internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		((org.slf4j.Logger) logger)
				.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
					new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
