package tourGuide.user;

import java.util.UUID;

public class UserAttraction {
	private UUID attractionId ;
	private String attractionName; 
	private String city ;
    private String state ;
	 private double  attractionLongitude;
	 private double  attractionLattitude;
	 
	public UserAttraction(String attractionName, String city, String state, double attractionLongitude,
			double attractionLattitude) {
		super();
		this.attractionName = attractionName;
		this.city = city;
		this.state = state;
		this.attractionLongitude = attractionLongitude;
		this.attractionLattitude = attractionLattitude;
	}
	
	public UserAttraction(UUID attractionId, String attractionName, String city, String state,
			double attractionLongitude, double attractionLattitude) {
		super();
		this.attractionId = attractionId;
		this.attractionName = attractionName;
		this.city = city;
		this.state = state;
		this.attractionLongitude = attractionLongitude;
		this.attractionLattitude = attractionLattitude;
	}

	public String getAttractionName() {
		return attractionName;
	}
	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
	public UUID getAttractionId() {
		return attractionId;
	}
	public void setAttractionId(UUID attractionId) {
		this.attractionId = attractionId;
	}
	
	
	

}
