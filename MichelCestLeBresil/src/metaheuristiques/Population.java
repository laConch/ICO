package metaheuristiques;

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

	public AlgoGenetique geneticAlgorithm;

	private ArrayList<Route> routes;

	// Getters
	public ArrayList<Route> getRoutes() {
		return routes;
	}

	public AlgoGenetique getGeneticAlgorithm() {
		return geneticAlgorithm;
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
	 * <p>
	 * Constructor<br />
	 * We have to add the argument <i>populationSize</i> because it will not always
	 * be the same as <i>geneticAlgorithm.populationSize</i> for example when
	 * creating the tournament population.
	 * </p>
	 * 
	 * @param geneticAlgorithm
	 * @param cities
	 */
	public Population(AlgoGenetique geneticAlgorithm, ArrayList<City> cities, int populationSize) {
		this.geneticAlgorithm = geneticAlgorithm;
		this.routes = new ArrayList<Route>(populationSize);
		IntStream.range(0, populationSize).forEach(x -> this.routes.add(new Route(cities)));
	}

	/**
	 * Constructor
	 * 
	 * @param geneticAlgorithm
	 */
	public Population(AlgoGenetique geneticAlgorithm) {
		this.geneticAlgorithm = geneticAlgorithm;
		this.routes = new ArrayList<Route>(geneticAlgorithm.getPopulationSize());
		IntStream.range(0, geneticAlgorithm.getPopulationSize())
				.forEach(x -> this.routes.add(new Route(geneticAlgorithm.getInitialRoute())));
	}

	/**
	 * Constructor
	 * 
	 * @param initialPopualation
	 * @param route              : to add in the initialPopulation
	 */
	public Population(Population initialPopulation, Route route) {
		this.geneticAlgorithm = initialPopulation.getGeneticAlgorithm();
//
//		System.out.println("####################################################");
		initialPopulation.sortRoutesByFitness();
//		System.out.println(String.format("1 : %s", initialPopulation.getRoutes()));
		ArrayList<Route> newRoutes = new ArrayList<>(initialPopulation.getRoutes());
//		ArrayList<Route> newRoutes = initialPopulation.getRoutes();
		// Remove the worse route
		newRoutes.remove(newRoutes.size() - 1);
		// Add the route given in parameter
		newRoutes.add(route);
		this.routes = newRoutes;
//		System.out.println(String.format("2 : %s", this.getRoutes()));
	}
}