package tourGuide.user;



import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public class UserReward {

	public final VisitedLocation visitedLocation;
	public final Attraction attraction;
	private int rewardPoints;
	public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
	public int getRewardPoints() {
		return rewardPoints;
	}
	
	/*
	 * public final VisitedLocation visitedLocation; public final UserAttraction
	 * userAttraction; private int rewardPoints
	 * =ThreadLocalRandom.current().nextInt(1, 1000) ; private int rewardPoints ;
	 * 
	 * public UserReward(VisitedLocation visitedLocation, UserAttraction
	 * userAttraction, int rewardPoints) { this.visitedLocation = visitedLocation;
	 * this.userAttraction = userAttraction; this.rewardPoints = rewardPoints; }
	 * 
	 * public UserReward(VisitedLocation visitedLocation, UserAttraction
	 * userAttraction) { this.visitedLocation = visitedLocation; this.userAttraction
	 * = userAttraction; }
	 * 
	 * 
	 * public void setRewardPoints(int rewardPoints) { this.rewardPoints =
	 * rewardPoints; }
	 * 
	 * public int getRewardPoints() { return rewardPoints; }
	 */
}
