package support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import metaheuristiques.AlgoGenetique;

/**
 * Class representing a route
 */
public class Route implements Serializable{

	private ArrayList<City> cities = new ArrayList<City>();
	
	// Parameters for the genetic algorithm. Fitness is the parameter we try to
	// optimize
	private double fitness = 0;
	private boolean isFitnessChanged = true;
	
	// Getters
	public ArrayList<City> getCities() {
		// If one gets the city it may change its fitness
		isFitnessChanged = true;
		return this.cities;
	}

	public double getFitness() {
		if (isFitnessChanged) {
			fitness = 1 / getTotalDistance() * 1000;
			isFitnessChanged = false;
		}
		return this.fitness;
	}

	/**
	 * Constructor from another route
	 * @param route
	 */
	public Route(Route route) {
		for (int i = 0; i < route.getCities().size(); i++) {
			this.cities.add(route.getCities().get(i));
		}
	}
	
	/**
	 * Constructor with a list of cities. One mixes it randomly in order to create a
	 * new order.
	 * 
	 * @param cities
	 * @param indexStartCity
	 */
	public Route(ArrayList<City> cities) {
		for (int i = 0; i < cities.size(); i++) {
			this.cities.add(cities.get(i));
		}
		
		// Mixed randomly
		Collections.shuffle(this.cities);
	}
	
	/**
	 * Constructor with GeneticAlgorithm
	 * 
	 * @param geneticAlgorithm
	 */
	// TODO change lenght instead o=f geneticAlgortim as parameter
	public Route(AlgoGenetique geneticAlgorithm) {
		geneticAlgorithm.getInitialRoute().forEach(x -> this.cities.add(null));
	}
	
	/*
	 * Constructor for Tabou Algorithm
	 * Clone an existing Route but swap the cities wich indice are i and j
	 */
	public Route(Route route, int i, int j){
		this(route);

		Collections.swap(this.cities,i,j);
	}
	
	
	/**
	 * Return the total Distance of the Route
	 * @return
	 */
	public double getTotalDistance() {
		int citiesSize = cities.size();
		double totalDistance = 0;

		for (int i = 0; i < citiesSize - 1; i++) {
			totalDistance += cities.get(i).measureDistance(cities.get(i + 1));
		}
		totalDistance += cities.get(0).measureDistance(cities.get(citiesSize - 1));
		return totalDistance;
	}

	public void printCitiesNameOfRoute() {
		int citiesSize = cities.size();
		System.out.println("Listes des villes parcourues dans l'ordre :");

		for (int i = 0; i < citiesSize; i++) {
			System.out.print(cities.get(i) + ", ");
		}
		System.out.println(cities.get(0).getName());

	}

	public String citiesNameOfRoute() {
		String citiesName = "";
		int citiesSize = cities.size();

		for (int i = 0; i < citiesSize; i++) {
			citiesName += cities.get(i) + " / ";
		}
		citiesName += cities.get(0).getName();
		return citiesName;
	}
	
	// ToString
	public String toString() {
		return Arrays.toString(cities.toArray());
	}
}
