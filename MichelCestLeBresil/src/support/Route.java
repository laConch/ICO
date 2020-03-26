package support;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import support.City;


public class Route {
	private ArrayList<City> cities = new ArrayList<City>();
	

	public String toString() {
		return Arrays.toString(cities.toArray());
	}

	//constructeurs
	public Route(Route route) {	//-->à partir d'une autre route
		for(int i = 0; i < route.getCities().size(); i++) {
			this.cities.add(route.getCities().get(i));
		}
	}
	
	
	public Route(ArrayList<City> cities) { //-->à partir d'une liste de villes qu'on mélange pour ne pas garder le même ordre
		for(int i = 0; i < cities.size(); i++) {
			this.cities.add(cities.get(i));
		}
		//mélanger aléatoirement les villes de la route
		Collections.shuffle(this.cities);
	}

	//get methods
	public ArrayList<City> getCities() {
		return cities;
	}
	

	public double getTotalDistance() {
		int citiesSize = cities.size();
		double totalDistance = 0;
		
		for(int i = 0; i < citiesSize - 1; i++){
			totalDistance += cities.get(i).measureDistance(cities.get(i+1));
		}
		totalDistance += cities.get(0).measureDistance(cities.get(citiesSize-1));
		return totalDistance;
	}
	
	public void printCitiesNameOfRoute() {
		int citiesSize = cities.size();
		System.out.println("Listes des villes parcourues dans l'ordre :");
		
		for(int i = 0; i < citiesSize; i++){
			System.out.print(cities.get(i) + ", ");
		}
		System.out.println(cities.get(0).getName());
		
	}
	
	public String citiesNameOfRoute() {
		String citiesName = "";
		int citiesSize = cities.size();

		for(int i = 0; i < citiesSize; i++){
			citiesName += cities.get(i) + " / ";
		}
		citiesName += cities.get(0).getName();
		return citiesName;
	}
}
