package tourGuide.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;
import tourGuide.user.UserLocation;

@Service
public class GpsUtilService {

	/*
	 * @Autowired private Location location ;
	 */

	private GpsUtil gpsUtil;

	public GpsUtilService() {
		gpsUtil = new GpsUtil();
	}

	private ExecutorService executor = Executors.newFixedThreadPool(10000);

	// Recupérations de toutes les attractions (object Attraction)
	public List<Attraction> getAttractions() {
		/*
		 * UUID userId = null; VisitedLocation rter=gpsUtil.getUserLocation(userId);
		 */

		return gpsUtil.getAttractions();
	}

	public VisitedLocation getUserLocation(UUID userId) {

		VisitedLocation vt = gpsUtil.getUserLocation(userId);
		return gpsUtil.getUserLocation(userId);
	}

	// Demande de localisation -> Attente réponse-> ajout de la localisation dans
	// l'historique de localisations

	public void submitLocation(User user, UserService userService) /* throws Exception */ {

		CompletableFuture.supplyAsync(() -> {
			return gpsUtil.getUserLocation(user.getUserId());
		}, executor).thenAccept(visitedLocation -> {

			userService.finalizeLocation(user, visitedLocation);
		});
	}

	// Test get user location
	public UserLocation getLocation(UUID userId) {
		UserLocation userLocation = new UserLocation(userId, ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D), ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D), new Date());
		/*
		 * userLocation.location.setUserId(userId);
		 * userLocation.setLatitude(ThreadLocalRandom.current().nextDouble(-85.
		 * 05112878D, 85.05112878D));
		 * userLocation.setLongitude(ThreadLocalRandom.current().nextDouble(-180.0D,
		 * 180.0D)); userLocation.setTimeLocation(new Date());
		 */

		return userLocation;
	}

	/*
	 * public VisitedLocation getUserLocation(UUID userId) {
	 * 
	 * gpsUtil.location.VisitedLocation visitedLocation =
	 * gpsUtil.getUserLocation(userId); return new
	 * VisitedLocation(visitedLocation.userId, new
	 * Location(visitedLocation.location.longitude,
	 * visitedLocation.location.latitude), visitedLocation.timeVisited); }
	 */
	
}
