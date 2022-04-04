package tourGuide.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;

public class GpsUtilService {
	private GpsUtil gpsUtil;
	private ExecutorService executor = Executors.newFixedThreadPool(10000);

	public GpsUtilService() {
		gpsUtil = new GpsUtil();
	}

	public List<Attraction> getAttractions() {
		return gpsUtil.getAttractions();
	}

	/*
	 * public VisitedLocation getUserLocation(User user) {
	 * 
	 * return user.getVisitedLocations().get(0); }
	 */

	public void submitLocation(User user, UserService userService) {
		CompletableFuture.supplyAsync(() -> {
			return gpsUtil.getUserLocation(user.getUserId());
		}, executor).thenAccept(visitedLocation -> {
			userService.finalizeLocation(user, visitedLocation);
		});
	}
}
