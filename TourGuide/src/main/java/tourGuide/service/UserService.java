package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class UserService {
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	private final GpsUtilService gpsUtilService;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	ExecutorService executor = Executors.newFixedThreadPool(1000);
	boolean testMode = true;

	public UserService(GpsUtilService gpsUtilService, RewardsService rewardsService) {
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

	private void initializeTripPricer() {
		logger.debug("Initialize tripPricer");
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	/*
	 * public VisitedLocation getUserLocation(User user) { VisitedLocation
	 * visitedLocation = (user.getVisitedLocations().size() > 0) ?
	 * user.getLastVisitedLocation() : trackUserLocation(user); return
	 * visitedLocation; }
	 */
	
	
	// methode de recupération de la localisation de l'user: Recupération de l'object VisitedLocation
	
	public VisitedLocation getUserLocation(User user) {
		/*
		 * int size=user.getVisitedLocations().size(); if (size>0) { return
		 * user.getVisitedLocations().get(size-1); }
		 */
		return user.getVisitedLocations().get(0);
	}

	// Ajout de la localisation dans l'historique de localisation
	
	public void finalizeLocation(User user, VisitedLocation visitedLocation) {
		user.addToVisitedLocations(visitedLocation);
		System.out.println("liste des localisation="+user.getVisitedLocations().size());
		rewardsService.calculateRewards(user);
		 tracker.finalizeTrack(user); 
		
	}

	// Liste des recentes localisations  des users JSON ARRAY
	
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

	/*
	 * public VisitedLocation trackUserLocation(User user) { VisitedLocation
	 * visitedLocation = gpsUtilService.getUserLocation(user);
	 * user.addToVisitedLocations(visitedLocation);
	 * rewardsService.calculateRewards(user); return visitedLocation; }
	 */
	
	// Ajout de la localisation actuelle de l'User
	
	public void trackUserLocation(User user) {
		
		gpsUtilService.submitLocation(user, this);
		/*
		 * GpsUtil gps=new GpsUtil(); gps.getUserLocation(user.getUserId());
		 * gpsUtilService.getUserLocation(user.getUserId());
		 * System.out.println(gpsUtilService.getUserLocation(user.getUserId()).location.
		 * latitude);
		 */
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		for (Attraction attraction : gpsUtilService.getAttractions()) {
			if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		return nearbyAttractions;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				
				tracker.stopTracker();
			}
		});
	}

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
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
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
