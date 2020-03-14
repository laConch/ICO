package support;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import support.City;


public class Route {
	private ArrayList<City> cities = new ArrayList<City>();
	private City startCity;

	public String toString() {
		return Arrays.toString(cities.toArray());
	}

	//constructeurs
	public Route(Route route) {	//-->à partir d'une autre route
		this.startCity = route.getStartCity();
		for(int i = 0; i < route.getCities().size(); i++) {
			this.cities.add(route.getCities().get(i));
		}
	}
	
	public Route(ArrayList<City> cities, int indexStartCity) { //-->à partir d'une liste de villes qu'on mélange pour ne pas garder le même ordre
		for(int i = 0; i < cities.size(); i++) {
			if(i == indexStartCity) {
				this.startCity = cities.get(i);
			}
			else {
				this.cities.add(cities.get(i));
			}
		}
		//mélanger aléatoirement les villes de la route
		Collections.shuffle(this.cities);
	}

	//get methods
	public ArrayList<City> getCities() {
		return cities;
	}
	public City getStartCity() {
		return startCity;
	}

	public double getTotalDistance() {
		int citiesSize = cities.size();
		double totalDistance = 0;
		
		for(int i = 0; i < citiesSize - 1; i++){
			totalDistance += cities.get(i).measureDistance(cities.get(i+1));
		}
		totalDistance += startCity.measureDistance(cities.get(0));
		totalDistance += startCity.measureDistance(cities.get(citiesSize-1));
		
		return totalDistance;
	}
	
	public void printCitiesNameOfRoute() {
		int citiesSize = cities.size();
		System.out.println("Listes des villes parcourues dans l'ordre :");
		System.out.print(startCity.getName() + ", ");
		for(int i = 0; i < citiesSize; i++){
			System.out.print(cities.get(i) + ", ");
		}
		System.out.print(startCity.getName());
	}
}
