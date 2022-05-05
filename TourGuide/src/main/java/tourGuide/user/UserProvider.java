package tourGuide.user;

import java.util.UUID;

public class UserProvider {
	public String name;
    public double price;
    public UUID tripId;
	public UserProvider(String name, double price, UUID tripId) {
		super();
		this.name = name;
		this.price = price;
		this.tripId = tripId;
	}
    
}
