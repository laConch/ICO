package algoMetaheuristique;

import java.util.ArrayList;
import java.util.stream.IntStream;

import support.City;
import support.Route;

/**
 * This class represents a population for the genetic algorithm
 * 
 * By Sethian & Bouzereau 3/18/2020
 */
public class Population {

	private ArrayList<Route> routes = new ArrayList<Route>(GeneticAlgorithm.POPULATION_SIZE);

	// Getter
	public ArrayList<Route> getRoutes() {
		return routes;
	}

	/**
	 * Sorts routes by fitness
	 */
	public void sortRoutesByFitness() {
		routes.sort((route1, route2) -> {
			int flag = 0;
			if (route1.getFitness() > route2.getFitness())
				flag = -1;
			else if (route1.getFitness() < route2.getFitness())
				flag = 1;
			return flag;
		});
	}

	/**
	 * Constructors
	 */
	public Population(int populationSize, ArrayList<City> cities) {
		IntStream.range(0, populationSize).forEach(x -> routes.add(new Route(cities)));
	}

	public Population(int populationSize, GeneticAlgorithm geneticAlgorithm) {
		IntStream.range(0, populationSize).forEach(
				x -> routes.add(new Route(geneticAlgorithm.getInitialRoute())));
	}
}