package roadgraph;

public class Location {
	private double lat;
	private double lng;
	
	public Location(double latitude, double longitude) {
		this.lat = latitude;
		this.lng = longitude;
	}
	
	public double getLatitude() {
		return lat;
	}
	
	public double getLongitude() {
		return lng;
	}
}