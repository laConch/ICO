package support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Class representing a route
 */
public class Route {

	private ArrayList<City> cities = new ArrayList<City>();
	private City startCity;

	// Parameter used in the genetic algorithm
	// It is the parameter we try to maximize
	private double fitness = 0;

	// Parameter used in the genetic algorithm
	// Used to know if the fitness has changed
	private boolean isFitnessChanged = true;

	// ToString
	public String toString() {
		return Arrays.toString(cities.toArray());
	}

	// Constructor with another Route
	public Route(Route route) {
		this.startCity = route.getStartCity();
		for (int i = 0; i < route.getCities().size(); i++) {
			this.cities.add(route.getCities().get(i));
		}
	}
	
	/*
	 * Constructor with a list of cities.
	 * One mixes it randomly in order to create a new order.
	 */
	public Route(ArrayList<City> cities, int indexStartCity) {
		for (int i = 0; i < cities.size(); i++) {
			if (i == indexStartCity) this.startCity = cities.get(i);
			else this.cities.add(cities.get(i));
		}
		// Mixed randomly
		Collections.shuffle(this.cities);
	}

	// Getters
	public ArrayList<City> getCities() {
		// If one gets the city it may change its fitness
		isFitnessChanged = true;

		return cities;
	}

	public City getStartCity() {
		return startCity;
	}

	public double getTotalDistance() {
		int citiesSize = cities.size();
		double totalDistance = 0;

		for (int i = 0; i < citiesSize - 1; i++) {
			totalDistance += cities.get(i).measureDistance(cities.get(i + 1));
		}
		totalDistance += startCity.measureDistance(cities.get(0));
		totalDistance += startCity.measureDistance(cities.get(citiesSize - 1));

		return totalDistance;
	}

	public double getFitness() {
		if (isFitnessChanged) {
			fitness = 1 / getTotalDistance() * 1000;
			isFitnessChanged = false;
		}

		return this.fitness;
	}

	public void printCitiesNameOfRoute() {
		int citiesSize = cities.size();
		System.out.println("Listes des villes parcourues dans l'ordre :");
		System.out.print(startCity.getName() + ", ");
		for (int i = 0; i < citiesSize; i++) {
			System.out.print(cities.get(i) + ", ");
		}
		System.out.print(startCity.getName());
	}
}
