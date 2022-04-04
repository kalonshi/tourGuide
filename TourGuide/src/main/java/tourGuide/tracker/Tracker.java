package tourGuide.tracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tourGuide.service.UserService;
import tourGuide.user.User;

public class Tracker extends Thread {
	private Logger logger = LoggerFactory.getLogger(Tracker.class);
	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
	private final Executor executorService = Executors.newSingleThreadExecutor();
	private final UserService userService;
	private final Map<User, Boolean> completedTrackingMap = new HashMap<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private boolean stop = false;

	public Tracker(UserService userService) {
		this.userService = userService;
		
		executor.submit(this);
	}
	
	/**
	 * Assures to shut down the Tracker thread
	 */
	/*
	 * public void stopTracking() { stop = true; executorService.shutdownNow(); }
	 */
	public void stopTracker() {
		stop = true;
		executor.shutdownNow();
	}
	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while(true) {
			if(Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}
			
			List<User> users = userService.getAllUsers();
			
			users.forEach(u -> completedTrackingMap.put(u, false));
			/* users.forEach(u -> userService.trackUserLocation(u)); */
			
			logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
			stopWatch.start();
			boolean notFinished = true;
			while(notFinished) {
				try {
					//logger.debug("Waiting for tracking to finish...");
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
				
				if(!completedTrackingMap.containsValue(false)) {
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
