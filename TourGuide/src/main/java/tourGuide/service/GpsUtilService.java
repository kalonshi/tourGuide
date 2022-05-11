package tourGuide.service;

import java.util.ArrayList;
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
import tourGuide.user.UserAttraction;
import tourGuide.user.UserLocation;

@Service
public class GpsUtilService {
	private GpsUtil gpsUtil;

	public GpsUtilService() {
		gpsUtil = new GpsUtil();
	}

	private ExecutorService executor = Executors.newFixedThreadPool(1000);

	/* Recuperations de toutes les attractions (object Attraction) */
	public List<Attraction> getAttractions() {
		
		return gpsUtil.getAttractions();
	}

	/* Recuperations de toutes les attractions (object Attraction) */
	public List<UserAttraction> getUserAttractions() {
		/*
		 * UUID userId = null; VisitedLocation rter=gpsUtil.getUserLocation(userId);
		 */
		List<UserAttraction> getUserAttractions = new ArrayList<UserAttraction>();
		List<Attraction> list = gpsUtil.getAttractions();
		list.forEach(att -> {
			UserAttraction userAtt = new UserAttraction(att.attractionId, att.attractionName, att.city, att.state,
					att.longitude, att.latitude);
			getUserAttractions.add(userAtt);

		});
		return getUserAttractions;
	}

	
	  public VisitedLocation getUserLocationApi(UUID userId) {
	  
	  return gpsUtil.getUserLocation(userId); }
	 

	/* *************** Test UserLocation *****************************/
	public UserLocation getUserLocation2(UUID userId) {
		
		return getLocation(userId);
	}

	/* Test get user location */
		public UserLocation getLocation(UUID userId) {

			UserLocation userLocation = new UserLocation(userId,
					ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D),
					ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D), new Date());
			return userLocation;
		}

	/* Test get VisitedLocation */
				public VisitedLocation getVisitedLocationFromUserLocation2(UUID userId) {

					UserLocation userLocation = new UserLocation(userId,
							ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D),
							ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D), new Date());
					VisitedLocation visitedLocation= new VisitedLocation(userId, new Location(userLocation.getLatitude(),userLocation.getLongitude()), new Date());
					return visitedLocation;
				}
				
	/*
	 * Demande de localisation -> Attente reponse-> ajout de la localisation dans
	 * l'historique de localisations
	 */
				
				  public void submitLocation(User user, TourService tourService) {
				  CompletableFuture.supplyAsync(() -> { return
						  getVisitedLocationFromUserLocation2(user.getUserId()); },
				  executor).thenAccept(visitedLocation -> {
				  
							tourService.finalizeLocation(user, visitedLocation); }); }
				 
				
				
				
	/* **************Fin de Test ******************/
		
	/*
	 * Demande de localisation -> Attente reponse-> ajout de la localisation dans
	 * l'historique de localisations
	 */
		
	/*
	 * public void submitLocation(User user, TourService tourService) {
	 * CompletableFuture.supplyAsync(() -> { return
	 * gpsUtil.getUserLocation(user.getUserId()); },
	 * executor).thenAccept(visitedLocation -> {
	 * 
	 * tourService.finalizeLocation(user, visitedLocation); }); }
	 * 
	 */
		
		
		
	
	/*
	 * public void submitLocation2(User user, TourService tourService) throws
	 * Exception {
	 * 
	 * CompletableFuture.supplyAsync(() -> { return
	 * gpsUtil.getUserLocation(user.getUserId()); },
	 * executor).thenAccept(userLocation -> {
	 * 
	 * VisitedLocation visitedLocation= new
	 * VisitedLocation(userLocation.getUserId(),new Location(
	 * userLocation.getLatitude(), userLocation.getLongitude()),new Date());
	 * tourService.finalizeLocation(user, visitedLocation); }); }
	 */
	
// ajout service attraction 
	
	

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
