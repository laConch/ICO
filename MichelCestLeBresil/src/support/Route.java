package support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import genetic.GeneticAlgorithm;

/**
 * Class representing a route
 */
public class Route {

	private ArrayList<City> cities = new ArrayList<City>();

	// Parameters for the genetic algorithm. Fitness is the parameter we try to
	// optimize
	private double fitness = 0;
	private boolean isFitnessChanged = true;

	// ToString
	public String toString() {
		return Arrays.toString(cities.toArray());
	}
	
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
	 * Constructor with another Route
	 * 
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
		cities.stream().forEach(x -> this.cities.add(x));
		
		// Mixed randomly
		Collections.shuffle(this.cities);
	}
	
	/**
	 * Constructor with GeneticAlgorithm
	 * 
	 * @param geneticAlgorithm
	 */
	public Route(GeneticAlgorithm geneticAlgorithm) {
		geneticAlgorithm.getInitialRoute().forEach(x -> this.cities.add(null));
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
}
