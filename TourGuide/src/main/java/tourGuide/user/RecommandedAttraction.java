package tourGuide.user;

public class RecommandedAttraction {
	 private String attractionName ;
	    private double userLat ;
	    private double userLong ;
	    private double attractionLat ;
	    private double attractionLong ;
	    private double distance ;
	    private int rewardPoint ;

	    public RecommandedAttraction (String attractionName, double userLat, double userLong, double attractionLat, double attractionLong, double distance, int rewardPoint){
	        this.attractionName = attractionName ;
	        this.userLat = userLat ;
	        this.userLong = userLong ;
	        this.attractionLat = attractionLat ;
	        this.attractionLong = attractionLong ;
	        this.distance = distance ;
	        this.rewardPoint = rewardPoint ;
	    }

	    public String getAttractionName() {
	        return attractionName;
	    }

	    public double getUserLat() {
	        return userLat;
	    }

	    public double getUserLong() {
	        return userLong;
	    }

	    public double getAttractionLat() {
	        return attractionLat;
	    }

	    public double getAttractionLong() {
	        return attractionLong;
	    }

	    public double getDistance() {
	        return distance;
	    }

	    public int getRewardPoint() {
	        return rewardPoint;
	    }
}
