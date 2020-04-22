package support;

import java.util.ArrayList;
import java.util.Arrays;

import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import metaheuristiques.AlgoRS;
import metaheuristiques.AlgoTabou;
import metaheuristiques.AlgoGenetique;
import metaheuristiques.Population;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Main class used to launch the different algorithms
 */
public class Main {
	
	/*
	 * Parameters for the complex initialization
	 */
	// 2 < nbOfCities < 15494
	public static int nbOfCities = 100;
	// "WORLD" for the world, "FRA" for France, "DEU" for Germany, "GBR" for United
	// Kingdom, "USA" for United States, "RUS" for Russia
	public static String countryOfCities = "WORLD";

	/*
	 * Parameters for AgentRS
	 */
	public static Route routeInitialeAgentRS;
	public static int nbIterationsMaxSansAmelioration = 10;
	
	/*
	 * Initial route for Tabou Algorithm 
	 */
	public static Route routeOptimaleTabou;

	/*
	 * Parameters to test the RS algorithm and the Tabou algorithm
	 */
	// 6 < nbOfCitiesMin < 15494
	public static final int nbOfCitiesMin = 10;
	// nbOfCitiesMax > nbOfCitiesMin
	public static final int nbOfCitiesMax = 10;
	// stepNbOfCities > 0
	public static final int stepNbOfCities = 10;
	public static final int nbOfTestsPerNbOfCities = 5;
	public static Double[] coefficientRefroidissementList = new Double[] { 0.98 };
	public static int[] nbIterationMaxPerCycleList = new int[] { 1000 };
	public static int[] tabouListSizeList = new int[] {10};
	public static int[] nbIterationTabouList = new int[] { 50 };
	
	/*
	 * Parameters to test the Genetic algorithm
	 */
	//private static int[] numberCitiesList = new int[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	private static int[] populationSizeList = new int[] {8, 10, 12, 20};
	//private static int[] numberGenerationList = new int[] {10, 50, 100, 1000};
	// Should be between 0 and 1
	private static double[] mutationRateList = new double[] {0.1, 0.2, 0.3};
	// Should be below populationSize
	//private static int[] tournamentSelectionSizeList = new int[] {2, 3, 4};
	// Should be below populationSize
	//private static int [] numberEliteRouteList = new int[] {1, 2, 3};
	
	public static final String csvColumnDelimeter = ",";
	public static final String csvRowDelimeter = "\n";
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		lancerAgents();
		//testerAlgorithmeRS();
		//executeGenetic();
		//testerAlgorithmeTabou();
	}
	
	public static void lancerAgents() {
		// Initialization of the initial road
		// routeInitialeAgentRS = new Route(initialisationBasique());
		routeInitialeAgentRS = new Route(initialisationComplexe(countryOfCities));

		// Creation of an instance of the Jade environment
		jade.core.Runtime rt = jade.core.Runtime.instance();

		// Creation of a default Container profile in order to launch the platform
		ProfileImpl pMain = new ProfileImpl();

		// Creation of the main container
		AgentContainer mc = rt.createMainContainer(pMain);

		// Creation of agents
		AgentController agentRS;
		AgentController agentAG;
		AgentController agentTabou;
		try {
			agentAG = mc.createNewAgent("agentAG", "agents.GeneticAgent", null);
			agentAG.start();
			agentRS = mc.createNewAgent("AgentRS", "agents.AgentRS", null);
			agentRS.start();
			agentTabou = mc.createNewAgent("AgentTabou", "agents.AgentTabou", null);
			agentTabou.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testerAlgorithmeRS() {
		//int nbOfTestsRealised = 0;
		String header = "NbOfCities" + csvColumnDelimeter + "Optimal distance" + csvColumnDelimeter + "Sequencing"
				+ csvColumnDelimeter + "Duration (in ms)" + csvColumnDelimeter + "Temperature" + csvColumnDelimeter
				+ "Cooling coefficient" + csvColumnDelimeter + "Number of iterations per cycle" + csvRowDelimeter;
		String contentToWrite = "";
		for (int i = nbOfCitiesMin; i < nbOfCitiesMax + 1; i += stepNbOfCities) {
			nbOfCities = i;
			for (Double j : coefficientRefroidissementList) {
				for (int k : nbIterationMaxPerCycleList) {
					for (int l = 0; l < nbOfTestsPerNbOfCities; l++) {
						// Initialise the route with one of the two methods
						// Route initialRoute = new Route(initalisationBasique(),0);
						Route initialRoute = new Route(initialisationComplexe(countryOfCities));

						// Calculate the optimal route with the Recuit Simule Algorithm and calculate
						// the duration of the method
						long startTime = System.nanoTime();
						Route optimalRoute = AlgoRS.obtainOptimalSolutionWithRecuitSimuleAlgorithm(initialRoute, j, k);
						long endTime = System.nanoTime();
						long duration = (endTime - startTime);

						// Add the relative information of the test to the content to write
						contentToWrite += i + csvColumnDelimeter + optimalRoute.getTotalDistance() + csvColumnDelimeter
								+ optimalRoute.citiesNameOfRoute() + csvColumnDelimeter
								+ Long.toString(Math.round(duration / 1000000)) + csvColumnDelimeter
								+ AlgoRS.initialTemperature + csvColumnDelimeter + j + csvColumnDelimeter + k
								+ csvRowDelimeter;
					}
					System.out.println(i + csvColumnDelimeter + j + csvColumnDelimeter + k + csvRowDelimeter);
				}
			}
		}
		writeResultInCSV(header, contentToWrite);
	}
	
	/*
	 * Test the Tabou algorithm
	 */
	public static void testerAlgorithmeTabou() {
		//int nbOfTestsRealised = 0;
		String header = "NbOfCities" + csvColumnDelimeter + "Optimal distance" + csvColumnDelimeter + "Sequencing"
				+ csvColumnDelimeter + "Duration (in ms)" + csvColumnDelimeter + "Taille liste Tabou"
				+ csvColumnDelimeter + "Nb itération sans changement" + csvRowDelimeter;
		String contentToWrite = "";
		for (int i = nbOfCitiesMin; i < nbOfCitiesMax + 1; i += stepNbOfCities) {
			nbOfCities = i;
			for (int j : tabouListSizeList) {
				for (int k : nbIterationTabouList) {
					for (int l = 0; l < nbOfTestsPerNbOfCities; l++) {
						// Initialise the route with one of the two methods
						// Route initialRoute = new Route(initalisationBasique(),0);
						Route initialRoute = new Route(initialisationComplexe(countryOfCities));

						// Calculate the optimal route with the Recuit Simule Algorithm and calculate
						// the duration of the method
						long startTime = System.nanoTime();
						Route optimalRoute = AlgoTabou.optiTS(initialRoute, k, j);
						long endTime = System.nanoTime();
						long duration = (endTime - startTime);

						// Add the relative information of the test to the content to write
						contentToWrite += i + csvColumnDelimeter + optimalRoute.getTotalDistance() + csvColumnDelimeter
								+ optimalRoute.citiesNameOfRoute() + csvColumnDelimeter
								+ Long.toString(Math.round(duration / 1000000)) + csvColumnDelimeter + j
								+ csvColumnDelimeter + k + csvRowDelimeter;
					}
					System.out.println(i + csvColumnDelimeter + j + csvColumnDelimeter + k + csvRowDelimeter);
				}
			}
		}
		writeResultInCSV(header, contentToWrite);
	}
	
	
	/**
	 * Execute the genetic algorithm and print the results.
	 */
	public static void executeGenetic() {
		//Main driver = new Main();

//		ArrayList<City> route = initialisationBasique();
		ArrayList<City> route = initialisationComplexe("FRA");

		AlgoGenetique geneticAlgorithm = new AlgoGenetique(route);

		// Random population
		Population population = new Population(geneticAlgorithm, route, geneticAlgorithm.getPopulationSize());
		population.sortRoutesByFitness();

		int generationNumber = 0;
		//driver.printHeading(generationNumber++);
		//driver.printPopulation(population);

		while (generationNumber < geneticAlgorithm.getNumberGeneration()) {
			//driver.printHeading(generationNumber++);
			population = geneticAlgorithm.evolve(population);
			population.sortRoutesByFitness();
			//driver.printPopulation(population);
		}
	}
	
	/**
	 * 
	 * @return the sample of six cities to test the algorithms
	 */
	public static ArrayList<City> initialisationBasique() {
		//Initialize the route with 6 French cities
		
		ArrayList<City> initialRoute = new ArrayList<City>(
				Arrays.asList(
						new City("Bordeaux", 44.833333, -0.566667), new City("Lyon", 45.750000, 4.850000),
						new City("Nantes", 47.216667, -1.550000), new City("Paris", 48.866667, 2.333333),
						new City("Marseille", 43.300000, 5.400000), new City("Dijon", 47.316667, 5.016667)
						));
		
		return initialRoute;
	}
	
	/**
	 * Return a list of cities extracted from the csv file "worldcities"
	 * 
	 * @param country : null for the world, "FRA" for France, "DEU" for Germany,
	 *                "GBR" for United Kingdom, "USA" for United States, "RUS" for
	 *                Russia.
	 * @return
	 */
	public static ArrayList<City> initialisationComplexe(String country) {

		String line = "";
		String splitBy = ",";

		int nbOfCitiesVisited = 0;
		int nbOfCitiesAdded = 0;
		ArrayList<City> cities = new ArrayList<City>();

		try {
			// parsing a CSV file into BufferedReader class constructor
			BufferedReader br = new BufferedReader(new FileReader("src/worldcities.csv"));
			while ((line = br.readLine()) != null && nbOfCitiesAdded < nbOfCities) {
				String[] city = line.split(splitBy); // use comma as separator
				if (nbOfCitiesVisited != 0) {
					if (country.contentEquals("WORLD")) {
						cities.add(new City(city[1].substring(1, city[1].length() - 1),
								Double.parseDouble(city[2].substring(1, city[2].length() - 1)),
								Double.parseDouble(city[3].substring(1, city[3].length() - 1))));
						// System.out.println(city[1].substring(1,city[1].length()-1));
						nbOfCitiesAdded += 1;
					} else if (country.length() == 3) {
						String countryOfCityLine = city[6].substring(1, city[6].length() - 1);
						if (countryOfCityLine.contentEquals(country)) {
							cities.add(new City(city[1].substring(1, city[1].length() - 1),
									Double.parseDouble(city[2].substring(1, city[2].length() - 1)),
									Double.parseDouble(city[3].substring(1, city[3].length() - 1))));
							// System.out.println(city[1].substring(1,city[1].length()-1));
							nbOfCitiesAdded += 1;
						}
					}

				}
				nbOfCitiesVisited += 1;
				if (nbOfCitiesVisited == 15494) {
					break;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cities;
	}
	
	/**
	 * To test the genetic algorithm and write the results in a csv file
	 * 
	 * The number of cities is fixed.
	 */
	public static void testGeneticAlgorithm() {
		int maxNumberOfIterWithoutGettingBetter = 500;
		int maxIter = 1000;
		String header = "Number of cities" + csvColumnDelimeter + "Optimal route" + csvColumnDelimeter
				+ "Optimal distance" + csvColumnDelimeter + "Optimal fitness" + csvColumnDelimeter + "Duration (in ms)"
				+ csvColumnDelimeter + "PopulationSize" + csvColumnDelimeter + "Number of generation"
				+ csvColumnDelimeter + "Mutation rate" + csvColumnDelimeter + "Tournament selection size"
				+ csvColumnDelimeter + "Number of elite routes" + csvRowDelimeter;

		String contentToWrite = "";

//		ArrayList<City> route = initialisationBasique();
//		ArrayList<City> route = initialisationComplexe("WORLD");

		for (int i = nbOfCitiesMin; i < nbOfCitiesMax + 1; i += stepNbOfCities) {
			nbOfCities = i;
			for (int populationSize : populationSizeList) {
				int minTournamentSelectionSize = populationSize / 8;
				int stepTournamentSelectionSize = populationSize / 8;
				int minNumberEliteRoute = populationSize / 20;
				int stepNumberEliteRoute = Math.max(1, populationSize / 10);
				for (double mutationRate : mutationRateList) {
					// for (int tournamentSelectionSize : tournamentSelectionSizeList) {
					for (int tournamentSelectionSize = minTournamentSelectionSize; tournamentSelectionSize < populationSize; tournamentSelectionSize += stepTournamentSelectionSize) {
						for (int numberEliteRoute = minNumberEliteRoute; numberEliteRoute < populationSize
								/ 2; numberEliteRoute += stepNumberEliteRoute) {
							// for (int numberGeneration : numberGenerationList) {
							// while (
							int numberGeneration = 1000;

							ArrayList<City> route = initialisationComplexe("WORLD");
							AlgoGenetique geneticAlgorithm = new AlgoGenetique(route, populationSize,
									numberGeneration, mutationRate, tournamentSelectionSize, numberEliteRoute);

							Population population = new Population(geneticAlgorithm, route, populationSize);
							population.sortRoutesByFitness();

							// Time just before starting the algorithm
							long startTime = System.nanoTime();

							population = geneticAlgorithm.evolve(population);
							population.sortRoutesByFitness();
							double bestFitness = population.getRoutes().get(0).getFitness();

							int iter = 0;
							int numberOfIterWithoutGettingBetter = 0;

							while (numberOfIterWithoutGettingBetter < maxNumberOfIterWithoutGettingBetter
									&& iter < maxIter) {
								population = geneticAlgorithm.evolve(population);
								population.sortRoutesByFitness();
								double tempBestFitness = population.getRoutes().get(0).getFitness();
								if (tempBestFitness > bestFitness) {
									bestFitness = tempBestFitness;
								} else {
									numberOfIterWithoutGettingBetter += 1;
								}

								iter += 1;
							}

							long duration = (System.nanoTime() - startTime) / 1000000;
							Route bestRoute = population.getRoutes().get(0);

							// Add the relative information of the test to the content to write
							contentToWrite += i + csvColumnDelimeter + bestRoute.citiesNameOfRoute()
									+ csvColumnDelimeter + bestRoute.getTotalDistance() + csvColumnDelimeter
									+ bestRoute.getFitness() + csvColumnDelimeter + duration + csvColumnDelimeter
									+ populationSize + csvColumnDelimeter + iter + csvColumnDelimeter + mutationRate
									+ csvColumnDelimeter + tournamentSelectionSize + csvColumnDelimeter
									+ numberEliteRoute + csvRowDelimeter;
							// }
						}
					}
				}
			}
		}
		writeResultInCSV(header, contentToWrite);
	}
	
	/**
	 * Open the csv file "courbescomparaison.csv" and add header and contentToWrite
	 * strings inside
	 * 
	 * @param header
	 * @param contentToWrite
	 */
	public static void writeResultInCSV(String header, String contentToWrite) {
		try {
			FileWriter fw = new FileWriter("src/courbescomparaison.csv");
			fw.append(header);
			fw.append(contentToWrite);
			fw.flush();
			fw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}		
	}
}
