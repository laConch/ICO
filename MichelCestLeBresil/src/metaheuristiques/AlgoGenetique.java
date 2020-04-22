package metaheuristiques;

import java.util.ArrayList;
import java.util.stream.IntStream;

import errors.GeneticCreationException;
import support.City;
import support.Route;

/**
 * This class implements the genetic algorithm
 * 
 * @author Sethian & Bouzereau
 * @since Mar 19, 2020
 */
public class AlgoGenetique {

	public int populationSize = 8;
	public int numberGeneration = 1000;

	/*
	 * Probability that a chromosome's gene will do random mutation. Here a
	 * chromosome is a Route and a gene is a City in that Route.
	 */
	public double mutationRate = 0.25;

	/*
	 * Tournament population that is used for the Route's crossover selection.
	 * Should be less than populationSize
	 */
	public int tournamentSelectionSize = 3;

	/*
	 * Routes that are not subject to crossover or selection from one generation to
	 * the next.
	 * Should be less than populationSize
	 */
	public int numberEliteRoute = 1;
	
	// To fix the size of the Routes
	private ArrayList<City> initialRoute = null;

	// Getters
	public ArrayList<City> getInitialRoute() {
		return this.initialRoute;
	}
	
	public int getPopulationSize() {
		return populationSize;
	}
	
	public int getNumberGeneration() {
		return numberGeneration;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public int getTournamentSelectionSize() {
		return tournamentSelectionSize;
	}

	public int getNumberEliteRoute() {
		return numberEliteRoute;
	}

	/**
	 * Constructor
	 * 
	 * @param initialRoute
	 */
	public AlgoGenetique(ArrayList<City> initialRoute) {
		this.initialRoute = initialRoute;
	}
	
	/**
	 * Constructor
	 * 
	 * @param initialRoute
	 * @param populationSize
	 * @param numberGeneration
	 * @param mutationRate
	 * @param tournamentSelectionSize
	 * @param numberEliteRoute
	 */
	public AlgoGenetique(ArrayList<City> initialRoute, int populationSize, int numberGeneration, double mutationRate,
			int tournamentSelectionSize, int numberEliteRoute) {
		this.initialRoute = initialRoute;
		this.populationSize = populationSize;
		this.numberGeneration = numberGeneration;
		
		try {
			parametersValidation(mutationRate, tournamentSelectionSize, numberEliteRoute, populationSize);
		} catch (GeneticCreationException e) {
			System.out.println("Error in GeneticAlgorithm creation : " + e);
		}
		this.mutationRate = mutationRate;
		this.tournamentSelectionSize = tournamentSelectionSize;
		this.numberEliteRoute = numberEliteRoute;
	}
	
	/**
	 * Validation of the parameters below
	 * 
	 * @param mutationRate            : should be between 0 and 1
	 * @param tournamentSelectionSize : should be below populationSize
	 * @param numberEliteRoute        : should be below populationSize
	 */
	private void parametersValidation(double mutationRate, int tournamentSelectionSize, int numberEliteRoute,
			int populationSize) throws GeneticCreationException {
		if (mutationRate < 0 || mutationRate > 1)
			throw new GeneticCreationException("MutationRate should be between 0 and 1");
		if (tournamentSelectionSize > populationSize)
			throw new GeneticCreationException("TournamentSelectionSize should be below populationSize");
		if (numberEliteRoute > populationSize)
			throw new GeneticCreationException("NumberEliteRoute should be below populationSize");
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
		IntStream.range(0, numberEliteRoute)
				.forEach(x -> crossoverPopulation.getRoutes().set(x, population.getRoutes().get(x)));

		IntStream.range(numberEliteRoute, crossoverPopulation.getRoutes().size()).forEach(x -> {
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
		Population tournamentPopulation = new Population(tournamentSelectionSize, this);
		IntStream.range(0, tournamentSelectionSize).forEach(x -> tournamentPopulation.getRoutes().set(x,
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
		population.getRoutes().stream().filter(x -> population.getRoutes().indexOf(x) >= numberEliteRoute)
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
		route.getCities().stream().filter(x -> Math.random() < mutationRate).forEach(cityX -> {
			int y = (int) (route.getCities().size() * Math.random());
			City cityY = route.getCities().get(y);
			// Switch cityX and cityY
			route.getCities().set(route.getCities().indexOf(cityX), cityY);
			route.getCities().set(y, cityX);
		});
		return route;
	}

}