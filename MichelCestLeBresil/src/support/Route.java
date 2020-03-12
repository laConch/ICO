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
	//-->� partir d'une autre route
	public Route(Route route) {
		/*
		 * ajouter les villes de la route en param comme villes de cette route
		 */
		//� compl�ter
		
		
	}

	//-->� partir d'une liste de villes qu'on m�lange pour ne pas garder le m�me ordre
	public Route(ArrayList<City> cities) {
		this.cities.addAll(cities);
		//m�langer al�atoirement les villes de la route
		Collections.shuffle(this.cities);
	}

	//get methods
	public ArrayList<City> getCities() {
		return cities;
	}

	public double getTotalDistance() {
		int citiesSize = this.cities.size();
		double totalDistance = 0;
		
		for(int i = 0; i < citiesSize - 1; i++){
			totalDistance += cities.get(i).measureDistance(cities.get(i+1));
		}
		totalDistance += cities.get(citiesSize-1).measureDistance(cities.get(0));
		return totalDistance;
	}
}
