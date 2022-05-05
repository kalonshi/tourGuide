package tourGuide.user;

import java.util.Date;
import java.util.UUID;

public class UserLocation {
	private  UUID userId;
	private double latitude;
	private double longitude;
	private Date TimeLocation;
	
	public UserLocation() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserLocation(UUID userId, double latitude, double longitude, Date timeLocation) {
		super();
		this.userId = userId;
		this.latitude = latitude;
		this.longitude = longitude;
		TimeLocation = timeLocation;
	}
	public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Date getTimeLocation() {
		return TimeLocation;
	}
	public void setTimeLocation(Date timeLocation) {
		TimeLocation = timeLocation;
	}
	
	
	
	
}
