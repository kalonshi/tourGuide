package tourGuide.tracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import tourGuide.service.TourService;
import tourGuide.user.User;

public class Tracker extends Thread {

	private Logger logger = LoggerFactory.getLogger(Tracker.class);
	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
	private final TourService tourService;
	private final Map<User, Boolean> completedTrackingMap = new HashMap<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private boolean stop = false;

	public Tracker(TourService tourService) {
		this.tourService = tourService;

		executor.submit(this);
	}

	/**
	 * Assures to shut down the Tracker thread
	 */

	public void stopTracker() {
		stop = true;
		
		executor.shutdownNow();
	}

	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while (true) {
			if (Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}

			List<User> users = tourService.getAllUsers();
			logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
			stopWatch.start();
			users.forEach(u -> tourService.trackUserLocation(u));
			boolean notFinished = true;
			while (notFinished) {
				try {
					// logger.debug("Waiting for tracking to finish...");
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					break;
				}

				if (!completedTrackingMap.containsValue(false)) {
					notFinished = false;
				}
			}

			completedTrackingMap.clear();
			stopWatch.stop();
			
			logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
			stopWatch.reset();
			try {
				logger.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);
			} catch (InterruptedException e) {
				break;
			}
		}

	}

	public synchronized void finalizeTrack(User user) {
		completedTrackingMap.put(user, true);
	}
}

/*
 * public void run() { while (true) { if (Thread.currentThread().isInterrupted()
 * || stop) { logger.debug("Tracker stopping"); break; }
 * 
 * CompletableFuture[] futures = tourService.getAllUsers().stream()
 * .map(this::trackUserLocation) .toArray(CompletableFuture[]::new);
 * CompletableFuture.allOf(futures).join(); // waiting so that Tracker does not
 * keep asking CompletableFutures
 * 
 * try { logger.debug("Tracker sleeping");
 * TimeUnit.SECONDS.sleep(trackingPollingInterval); } catch
 * (InterruptedException e) {
 * 
 * break; } } }
 */