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
	
	// Fixed parameter
	public static ArrayList<City> initialRoute;

	public int populationSize;
	public int numberGeneration;

	/*
	 * Probability that a chromosome's gene will do random mutation. Here a
	 * chromosome is a Route and a gene is a City in that Route.
	 */
	public double mutationRate;

	/*
	 * Tournament population that is used for the Route's crossover selection.
	 * Should be less than populationSize
	 */
	public int tournamentSelectionSize;

	/*
	 * Routes that are not subject to crossover or selection from one generation to
	 * the next.
	 * Should be less than populationSize
	 */
	public int numberEliteRoute;
	
	/*
	 * Routes that will go through a crossover
	 */
	public int numberCrossOverRoute;
	
	/*
	 * Number of random Routes that will be added to each population
	 */
	public int numberRandomRoute;
	
	/*
	 * Part of the parent 1 we take as it is in the crossover part
	 */
	public double crossOverCut;
	
	/*
	 * The maximum number of iterations we do without amelioration before stopping
	 */
	public int maxWithoutAmelioration;

	// Getters
	public ArrayList<City> getInitialRoute() {
		return AlgoGenetique.initialRoute;
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
	
	public int getNumberCrossOverRoute() {
		return numberCrossOverRoute;
	}
	
	public int getNumberRandomRoute() {
		return numberRandomRoute;
	}
	
	public double getCrossOverCut() {
		return crossOverCut;
	}
	
	public int getMaxWithoutAmelioration() {
		return maxWithoutAmelioration;
	}
	
	// Setters
	public void setNumberGeneration(int numberGeneration) {
		this.numberGeneration = numberGeneration;
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
	 * @param numberCrossOverRoute
	 * @param numberRandomRoute
	 * @param crossOverCut
	 * @param maxWithoutAmelioration
	 */
	public AlgoGenetique(ArrayList<City> initialRoute, int populationSize, int numberGeneration, double mutationRate,
			int tournamentSelectionSize, int numberEliteRoute, int numberCrossOverRoute, int numberRandomRoute,
			double crossOverCut, int maxWithoutAmelioration) {
		AlgoGenetique.initialRoute = initialRoute;
		this.populationSize = populationSize;
		this.numberGeneration = numberGeneration;
		this.maxWithoutAmelioration = maxWithoutAmelioration;

		try {
			parametersValidation(mutationRate, tournamentSelectionSize, numberEliteRoute, numberCrossOverRoute, numberRandomRoute, populationSize, crossOverCut);
		} catch (GeneticCreationException e) {
			System.out.println("Error in GeneticAlgorithm creation : " + e);
		}
	}
	
	/**
	 * Validation of the parameters below and assignment if okay<br />
	 * 
	 * In addition of all the other conditions, the sum of numberEliteRoute,
	 * numberCrossOverRoute and numberRandomRoute should be below or equal to the
	 * populationSize.
	 * 
	 * @param mutationRate            : should be between 0 and 1
	 * @param tournamentSelectionSize : should be below populationSize and not null
	 * @param numberEliteRoute        : should be below populationSize
	 * @param numberCrossOverRoute    : should be below populationSize
	 * @param numberRandomRoute       : should be below populationSize
	 * @param crossOverCut            : should be between 0 and 1
	 * @throws GeneticCreationException
	 */
	public void parametersValidation(double mutationRate, int tournamentSelectionSize, int numberEliteRoute,
			int numberCrossOverRoute, int numberRandomRoute, int populationSize, double crossOverCut)
			throws GeneticCreationException {
		if (mutationRate < 0 || mutationRate > 1)
			throw new GeneticCreationException("MutationRate should be between 0 and 1");
		if (crossOverCut < 0 || crossOverCut > 1)
			throw new GeneticCreationException("CrossOverCut should be between 0 and 1");
		this.mutationRate = mutationRate;
		this.crossOverCut = crossOverCut;

		if (tournamentSelectionSize > populationSize)
			this.tournamentSelectionSize = populationSize;
		else if (tournamentSelectionSize == 0)
			this.tournamentSelectionSize = 1;
		else
			this.tournamentSelectionSize = tournamentSelectionSize;

		int sumRoutes = numberEliteRoute + numberCrossOverRoute + numberRandomRoute;
		if (sumRoutes > populationSize) {
			this.numberEliteRoute *= populationSize / sumRoutes;
			this.numberCrossOverRoute *= populationSize / sumRoutes;
			this.numberRandomRoute *= populationSize / sumRoutes;
		} else {
			this.numberEliteRoute = numberEliteRoute;
			this.numberCrossOverRoute = numberCrossOverRoute;
			this.numberRandomRoute = numberRandomRoute;
		}
	}
	
	/**
	 * Run the GeneticAlgorithm
	 */
	public void run() {
		Population population = initialPopulation(populationSize);
		population.sortRoutesByFitness();

		// Time just before starting the algorithm
		long startTime = System.nanoTime();
		
		int generation = 0;
		int withoutAmelioration = 0;
		double bestFitness = 0;
		
		System.out.println("GeneticAlgorithm " + this.toString());
		while (withoutAmelioration < maxWithoutAmelioration && generation < numberGeneration) {
			System.out.println(String.format("Generation %s", generation));
			population = evolve(population);
			population.sortRoutesByFitness();
			double tempBestFitness = population.get(0).getFitness();
			withoutAmelioration = (tempBestFitness > bestFitness) ? 0 : withoutAmelioration + 1;
			bestFitness = (withoutAmelioration == 0) ? tempBestFitness : bestFitness;
			generation += 1;
		}
		
		long duration = System.nanoTime() - startTime;
		System.out.println("Best route : " + population.get(0).toString());
		System.out.println("     " + "Distance " + population.get(0).getTotalDistance() + " - Fitness "
				+ population.get(0).getFitness());
		System.out.println(String.format("Total Duration : %s s", duration / 1_000_000_000));
	}
	
	/**
	 * Run the genetic algorithm for the agent. It means that we do no print the
	 * same things, we return the best result found and we may adapt the initial
	 * population.
	 * 
	 * @param initialRoute : the previous best route found by the agents
	 * 
	 * @return Route : the best route found
	 */
	public Route runForAgent(Route initialRoute) {
		Population population = initialPopulation(populationSize);
		//We add the best route previously found to the population.
		population.set(0, initialRoute);
		population.sortRoutesByFitness();
		
		int generation = 0;
		int withoutAmelioration = 0;
		double bestFitness = 0;
		
		//System.out.println("GeneticAlgorithm " + this.toString());
		while (withoutAmelioration < maxWithoutAmelioration && generation < numberGeneration) {
			//System.out.println(String.format("Generation %s", generation));
			population = evolve(population);
			population.sortRoutesByFitness();
			double tempBestFitness = population.get(0).getFitness();
			withoutAmelioration = (tempBestFitness > bestFitness) ? 0 : withoutAmelioration + 1;
			bestFitness = (withoutAmelioration == 0) ? tempBestFitness : bestFitness;
			generation += 1;
		}
		return population.get(0);
	}
	
	/**
	 * Generates the initial population from the initial road
	 * 
	 * @param populationSize
	 * @return
	 */
	public static Population initialPopulation(int populationSize) {
		Population population = new Population(populationSize);
		IntStream.range(0, populationSize).forEach(x -> {
			Route newRoute = new Route(initialRoute);
			newRoute.setFitness();
			population.add(newRoute);
		});
		return population;
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
		Population crossoverPopulation = new Population(populationSize);

		// Elite Routes
		IntStream.range(0, this.numberEliteRoute).forEach(x -> crossoverPopulation.add(population.get(x)));

		// CrossOver Routes
		IntStream.range(0, this.numberCrossOverRoute).forEach(x -> {
			Route route1 = selectTournamentPopulation(population).get(0);
			Route route2 = selectTournamentPopulation(population).get(0);
			crossoverPopulation.add(crossoverRoute(route1, route2));
		});
		
		// Random Routes
		IntStream.range(0, this.numberRandomRoute).forEach(x -> crossoverPopulation.add(new Route(population.get(0))));

		// Finish to fill
		IntStream.range(this.numberEliteRoute + this.numberCrossOverRoute + this.numberRandomRoute, this.populationSize)
				.forEach(x -> crossoverPopulation.add(population.get(x)));
		
		return crossoverPopulation;
	}

	/**
	 * Apply the crossover between route1 and route2.
	 * 
	 * An example :
	 * route 1 : [Paris, Marseille, Lyon, Toulouse, Nice, Nantes, Strasbourg, Montpellier]
	 * route 2 : [Lyon, Marseille, Montpellier, Nantes, Nice, Paris, Strasbourg, Toulouse]
	 * intermediate crossover : [Paris, Marseille, Lyon, Toulouse, null, null, null, null]
	 * final crossover : [Paris, Marseille, Lyon, Toulouse, Montpellier, Nantes, Nice, Strasbourg]
	 * 
	 * @param route1
	 * @param route2
	 * @return the "child" of route1 and route2
	 */
	private Route crossoverRoute(Route route1, Route route2) {
		Route crossoverRoute = new Route(route1.getCities().size());
		int partTakenAsItIs = (int) (route1.getCities().size() * crossOverCut);
		IntStream.range(0, partTakenAsItIs).forEach(x -> crossoverRoute.getCities().add(route1.getCities().get(x)));
		return fillNullsInCrossoverRoute(crossoverRoute, route2, partTakenAsItIs);
	}
	
	/**
	 * Fills the null in the intermediate crossover with the route2's cities in the
	 * order they appear.
	 * 
	 * @param crossoverRoute
	 * @param route
	 * @param partTakenAsItIs
	 * @return
	 */
	private Route fillNullsInCrossoverRoute(Route crossoverRoute, Route route2, int partTakenAsItIs) {
		route2.getCities().stream().filter(x -> !crossoverRoute.getCities().contains(x))
				.forEach(cityX -> crossoverRoute.getCities().add(cityX));
		return crossoverRoute;
	}

	/**
	 * Select randomly tournamentSelectionSize Routes in the given Population.
	 * 
	 * @param population
	 * @return the tournament Population sorted by fitness
	 */
	private Population selectTournamentPopulation(Population population) {
		Population tournamentPopulation = new Population(this.tournamentSelectionSize);
		IntStream.range(0, this.tournamentSelectionSize)
				.forEach(x -> tournamentPopulation.add(population.get((int) (Math.random() * population.size()))));
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
		population.stream().filter(x -> population.indexOf(x) >= this.numberEliteRoute).forEach(x -> {
			mutateRoute(x);
			x.setFitness();
		});
		return population;
	}

	/**
	 * Switch each city in the route with another city with a probability of
	 * mutationRate
	 * 
	 * @param route
	 * @return after mutation Route
	 */
	private Route mutateRoute(Route route) {
		route.getCities().stream().filter(x -> Math.random() < this.mutationRate).forEach(cityX -> {
			int y = (int) (route.getCities().size() * Math.random());
			City cityY = route.getCities().get(y);
			// Switch cityX and cityY
			route.getCities().set(route.getCities().indexOf(cityX), cityY);
			route.getCities().set(y, cityX);
		});
		return route;
	}
	
	@Override
	public String toString() {
		return String.format(
				"PopulationSize %s - NumberGeneration %s - MutationRate %s - TournamentSelectionSize %s - NumberEliteRoute %s - NumberCrossOverRoute %s - NumberRandomRoute %s - CrossOverCut %s - MaxWithoutAmelioration %s.",
				populationSize, numberGeneration, mutationRate, tournamentSelectionSize, numberEliteRoute,
				numberCrossOverRoute, numberRandomRoute, crossOverCut, maxWithoutAmelioration);
	}

}