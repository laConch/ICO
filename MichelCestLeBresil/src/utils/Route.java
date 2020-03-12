package utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Route {
	private ArrayList<City> cities = new ArrayList<City>();

	public String toString() {
		return Arrays.toString(cities.toArray());
	}

	//constructeurs
	//-->à partir d'une autre route
	public Route(Route route) {
		/*
		 * ajouter les villes de la route en param comme villes de cette route
		 */
		//à compléter
	}

	//-->à partir d'une liste de villes qu'on mélange pour ne pas garder le même ordre
	public Route(ArrayList<City> cities) {
		this.cities.addAll(cities);
		//mélanger aléatoirement les villes de la route
		Collections.shuffle(this.cities);
	}

	//get methods
	public ArrayList<City> getCities() {
		return cities;
	}

	public double getTotalDistance() {
		int citiesSize = this.cities.size();
		//à compléter
	}
}
