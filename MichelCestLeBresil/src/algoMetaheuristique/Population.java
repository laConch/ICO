package algoMetaheuristique;

import java.util.ArrayList;
import java.util.stream.IntStream;

import support.City;
import support.Route;

/**
 * <p>
 * This class represents a population for the genetic algorithm</br>
 * A population is an array of routes specific to a certain geneticAlgorithm
 * (populationSize)
 * </p>
 * 
 * By Sethian & Bouzereau 3/18/2020
 */
public class Population {
	
	public GeneticAlgorithm geneticAlgorithm;
	
	private ArrayList<Route> routes;
	
	// TODO  delete
//	private ArrayList<Route> routes = new ArrayList<Route>(geneticAlgorithm.getPopulationSize());

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
	 *  TODO 
	 *  
	 *  size of the cities list
	 * @param geneticAlgorithm
	 * @param cities
	 */
	public Population(GeneticAlgorithm geneticAlgorithm, ArrayList<City> cities, int populationSize) {
		this.geneticAlgorithm = geneticAlgorithm;
		IntStream.range(0, populationSize).forEach(x -> this.routes.add(new Route(cities)));
	}

	public Population(GeneticAlgorithm geneticAlgorithm) {
		this.geneticAlgorithm = geneticAlgorithm;
		IntStream.range(0, geneticAlgorithm.getPopulationSize()).forEach(
				x -> this.routes.add(new Route(geneticAlgorithm.getInitialRoute())));
	}
}