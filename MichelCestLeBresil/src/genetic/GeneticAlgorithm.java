package genetic;

import java.util.ArrayList;
import java.util.stream.IntStream;

import support.City;
import support.Route;

/**
 * This class implements the genetic algorithm
 * 
 * @author 
 * @created 3/19/2020
 */
/**
 * This class implements the genetic algorithm
 * @author Sethian & Bouzereau
 * @since Mar 19, 2020
 */
public class GeneticAlgorithm {

	public static final int POPULATION_SIZE = 8;
	public static final int NUMBER_GENERATION = 100;

	/*
	 * Probability that a chromosome's gene will do random mutation. Here a
	 * chromosome is a Route and a gene is a City in that Route.
	 */
	public static final double MUTATION_RATE = 0.25;

	/*
	 * Tournament population that is used for the Route's crossover selection.
	 */
	public static final int TOURNAMENT_SELECTION_SIZE = 3;

	/*
	 * Routes that are not subject to crossover or selection from one generation to
	 * the next.
	 */
	public static final int NUMBER_ELITE_ROUTES = 1;

	private ArrayList<City> initialRoute = null;

	// Getter
	public ArrayList<City> getInitialRoute() {
		return this.initialRoute;
	}

	/**
	 * Constructor
	 * 
	 * @param initialRoute
	 */
	public GeneticAlgorithm(ArrayList<City> initialRoute) {
		this.initialRoute = initialRoute;
	}

	/**
	 * Apply the evolution to the population (crossover + mutation)
	 * 
	 * @param population one wants to make evolve
	 * @return the evolved Population
	 */
	public Population evolve(Population population) {
		return mutatePopulation(crossoverPopulation(population));
	}

	/**
	 * Apply the crossover to the population
	 * 
	 * @param population
	 * @return after crossover Population
	 */
	private Population crossoverPopulation(Population population) {
		Population crossoverPopulation = new Population(population.getRoutes().size(), this);
		IntStream.range(0, NUMBER_ELITE_ROUTES)
				.forEach(x -> crossoverPopulation.getRoutes().set(x, population.getRoutes().get(x)));

		IntStream.range(NUMBER_ELITE_ROUTES, crossoverPopulation.getRoutes().size()).forEach(x -> {
			Route route1 = selectTournamentPopulation(population).getRoutes().get(0);
			Route route2 = selectTournamentPopulation(population).getRoutes().get(0);
			crossoverPopulation.getRoutes().set(x, crossoverRoute(route1, route2));
		});
		return crossoverPopulation;
	}

	/**
	 * Apply the crossover between route1 and route2.
	 * 
	 * An example :
	 * 		route 1 :           [Paris, Marseille, Lyon, Toulouse, Nice, Nantes, Strasbourg, Montpellier]
	 * 		route 2 :           [Lyon, Marseille, Montpellier, Nantes, Nice, Paris, Strasbourg, Toulouse]
	 * intermediate crossover : [Paris, Marseille, Lyon, Toulouse, null, null, null, null]		
	 * final crossover :        [Paris, Marseille, Lyon, Toulouse, Montpellier, Nantes, Nice, Strasbourg]
	 * 
	 * @param route1
	 * @param route2
	 * @return the "child" of route1 and route2
	 */
	private Route crossoverRoute(Route route1, Route route2) {
		Route crossoverRoute = new  Route(this);
		for (int x = 0; x < crossoverRoute.getCities().size()/2; x++)
			crossoverRoute.getCities().set(x, route1.getCities().get(x));
		return fillNullsInCrossoverRoute(crossoverRoute, route2);
	}
	
	/**
	 * Fills the null in the intermediate crossover with the route2's cities in the
	 * order they appear.
	 * 
	 * @param crossoverRoute
	 * @param route
	 * @return
	 */
	private Route fillNullsInCrossoverRoute(Route crossoverRoute, Route route2) {
		route2.getCities().stream().filter(x -> !crossoverRoute.getCities().contains(x)).forEach(cityX -> {
			for (int y = route2.getCities().size()/2; y < route2.getCities().size(); y++) {
				if (crossoverRoute.getCities().get(y) == null) {
					crossoverRoute.getCities().set(y, cityX);
					break;
				}
			}
		});
		return crossoverRoute;
	}

	/**
	 * Select randomly TOURNAMENT_SELECTION_SIZE Routes in the given Population.
	 * 
	 * @param population
	 * @return the tournament Population sorted by fitness
	 */
	private Population selectTournamentPopulation(Population population) {
		Population tournamentPopulation = new Population(TOURNAMENT_SELECTION_SIZE, this);
		IntStream.range(0, TOURNAMENT_SELECTION_SIZE).forEach(x -> tournamentPopulation.getRoutes().set(x,
				population.getRoutes().get((int) (Math.random() * population.getRoutes().size()))));
		tournamentPopulation.sortRoutesByFitness();
		return tournamentPopulation;

	}

	/**
	 * Keep the elite Route as they are and mutates the others.
	 * 
	 * @param population
	 * @return after mutation Population
	 */
	private Population mutatePopulation(Population population) {
		population.getRoutes().stream().filter(x -> population.getRoutes().indexOf(x) >= NUMBER_ELITE_ROUTES)
				.forEach(x -> mutateRoute(x));
		return population;
	}

	/**
	 * Switch each city in the route with another city with a probability of
	 * MUTATION_RATE
	 * 
	 * @param route
	 * @return after mutation Route
	 */
	private Route mutateRoute(Route route) {
		route.getCities().stream().filter(x -> Math.random() < MUTATION_RATE).forEach(cityX -> {
			int y = (int) (route.getCities().size() * Math.random());
			City cityY = route.getCities().get(y);
			// Switch cityX and cityY
			route.getCities().set(route.getCities().indexOf(cityX), cityY);
			route.getCities().set(y, cityX);
		});
		return route;
	}

}