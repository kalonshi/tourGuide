package tourGuide.user;

public class UserAttraction {
	public String attractionName; 
	public double  attractionLongitude;
	public double  attractionLattitude;
	public double  userLongitude;
	public double  userLattitude;
	public double  dist;
	public int rewardPoints;
	
	
	
	public UserAttraction(String attractionName, double attractionLongitude, double attractionLattitude,
			double userLongitude, double userLattitude, double dist, int rewardPoints) {
		super();
		this.attractionName = attractionName;
		this.attractionLongitude = attractionLongitude;
		this.attractionLattitude = attractionLattitude;
		this.userLongitude = userLongitude;
		this.userLattitude = userLattitude;
		this.dist = dist;
		this.rewardPoints = rewardPoints;
	}
	public UserAttraction(String attractionName, double attractionLongitude, double attractionLattitude,
			double userLongitude, double userLattitude, int rewardPoints) {
		super();
		this.attractionName = attractionName;
		this.attractionLongitude = attractionLongitude;
		this.attractionLattitude = attractionLattitude;
		this.userLongitude = userLongitude;
		this.userLattitude = userLattitude;
		this.rewardPoints = rewardPoints;
	}
	public UserAttraction() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getAttractionName() {
		return attractionName;
	}
	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}
	public double getAttractionLongitude() {
		return attractionLongitude;
	}
	public void setAttractionLongitude(double attractionLongitude) {
		this.attractionLongitude = attractionLongitude;
	}
	public double getAttractionLattitude() {
		return attractionLattitude;
	}
	public void setAttractionLattitude(double attractionLattitude) {
		this.attractionLattitude = attractionLattitude;
	}
	public double getUserLongitude() {
		return userLongitude;
	}
	public void setUserLongitude(double userLongitude) {
		this.userLongitude = userLongitude;
	}
	public double getUserLattitude() {
		return userLattitude;
	}
	public void setUserLattitude(double userLattitude) {
		this.userLattitude = userLattitude;
	}
	public int getRewardPoints() {
		return rewardPoints;
	}
	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
	// Tourist attractions lat/long, 
     // The user's location lat/long, 
     // The distance in miles between the user's location and each of the attractions.
     // The reward points 
}
