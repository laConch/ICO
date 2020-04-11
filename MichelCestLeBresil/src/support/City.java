package support;

/**
 * Class representing a City
 * 
 */
public class City {

	// Constants
	private static final double EARTH_EQUATORIAL_RADIUS = 6378.1370D;
	private static final double CONVERT_DEGREES_TO_RADIANS = Math.PI / 180D;
	private static final double CONVERT_KMS_TO_MILES = 0.621371;

	// Attributes
	private double longitude;
	private double latitude;
	private String name;

	//Constructors
	public City(String name, double latitude, double longitude) {
		this.name = name;
		this.latitude = latitude * CONVERT_DEGREES_TO_RADIANS;
		this.longitude = longitude * CONVERT_DEGREES_TO_RADIANS;
	}

	// Getters
	public double getLongitude() {
		return this.longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public String getName() {
		return this.name;
	}

	// ToString
	public String toString() {
		return this.name;
	}

	/**
	 * 
	 * @param city
	 * @return the distance (km) between 2 cities
	 */
	public double measureDistance(City city) {
		double deltaLatitude = city.getLatitude() - this.getLatitude();
		double deltaLongitude = city.getLongitude() - this.getLongitude();
		double a = Math.pow(Math.sin(deltaLatitude / 2D), 2D) + Math.cos(this.getLatitude())
				* Math.cos(city.getLatitude()) * Math.pow(Math.sin(deltaLongitude / 2D), 2D);
		return EARTH_EQUATORIAL_RADIUS * 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
	}
}
