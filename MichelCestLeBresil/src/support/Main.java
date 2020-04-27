package support;

import java.util.ArrayList;

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
	public static int nbOfCities = 50;
	// "WORLD" for the world, "FRA" for France, "DEU" for Germany, "GBR" for United
	// Kingdom, "USA" for United States, "RUS" for Russia
	public static String countryOfCities = "WORLD";
	
	/*
	 * Parameters for the three agents
	 */
	public static int nbIterationsMaxSansAmelioration = 3;
	public static Route routeInitialeAgentRS;
	public static Route routeInitialeAgentTabou;
	public static Route routeOptimaleTabou;
	public static Route routeInitialeAgentGenetique;

	/*
	 * Parameters to test the RS algorithm and the Tabou algorithm
	 */
	// 6 < nbOfCitiesMin < 15494
	public static final int nbOfCitiesMin = 10;
	// nbOfCitiesMax > nbOfCitiesMin
	public static final int nbOfCitiesMax = 100;
	// stepNbOfCities > 0
	public static final int stepNbOfCities = 10;
	public static final int nbOfTestsPerNbOfCities = 5;
	public static Double[] coefficientRefroidissementList = new Double[] { 0.98 };
	public static int[] nbIterationMaxPerCycleList = new int[] { 1000 };
	public static int[] tabouListSizeList = new int[] {10};
	public static int[] nbIterationTabouList = new int[] { 50 };
	
	public static final String csvColumnDelimeter = ",";
	public static final String csvRowDelimeter = "\n";
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		testGeneticAlgorithm();
	}	
	
	/**
	 * Execute the genetic algorithm and print the results.
	 */
	public static void executeGenetic() {

//		ArrayList<City> route = initialisationBasique();
//		ArrayList<City> route = initialisationComplexe("FRA");
		routeInitialeAgentGenetique = new Route(initialisationComplexe(countryOfCities));
		
		AlgoGenetique geneticAlgorithm = new AlgoGenetique(Main.routeInitialeAgentGenetique.getCities());
		// First Population randomly generated
		Population population = new Population(geneticAlgorithm, Main.routeInitialeAgentGenetique.getCities(),
				geneticAlgorithm.getPopulationSize());
		
//		AlgoGenetique geneticAlgorithm = new AlgoGenetique(route);
//
//		// Random population
//		Population population = new Population(geneticAlgorithm, route, geneticAlgorithm.getPopulationSize());
//		population.sortRoutesByFitness();
		
		for (int generation = 0; generation < geneticAlgorithm.getNumberGeneration(); generation++) {
			System.out.println(String.format("Generation number : %s", generation));
			population = geneticAlgorithm.evolve(population);
			population.sortRoutesByFitness();
			System.out.println(String.format("Size : %s", population.getRoutes().get(0).getCities().size()));
			System.out.println(String.format("Popualtion : %s", population.getRoutes().get(0)));
			System.out.println("---------------------------------------");
		}
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
		String header = "Number of cities" + csvColumnDelimeter + "Optimal route" + csvColumnDelimeter
				+ "Optimal distance" + csvColumnDelimeter + "Optimal fitness" + csvColumnDelimeter + "Duration (in ms)"
				+ csvColumnDelimeter + "PopulationSize" + csvColumnDelimeter + "Number of generation"
				+ csvColumnDelimeter + "Mutation rate" + csvColumnDelimeter + "Tournament selection size"
				+ csvColumnDelimeter + "Number of elite routes" + csvRowDelimeter;

		String contentToWrite = "";

//		for (int i = nbOfCitiesMin; i < nbOfCitiesMax + 1; i += stepNbOfCities) {
			nbOfCities = 100;
			System.out.println(String.format("Number of cities : %s", nbOfCities));
			ArrayList<City> route = initialisationComplexe("WORLD");
			

			int maxNumberWithoutGettingBetter = 5000;
			int populationSize = 20;
			int numberGeneration = 10000;
			double mutationRate = 0.01;
			int tournamentSelectionSize = 14;
			int numberEliteRoute = 2;
			double crossOverCut = 0.5;
			
//			for (int j = 0; j < 0.11; j += 0.01) {
//				mutationRate = j;
				System.out.println(String.format("     MutationRate : %s", mutationRate * 100));
				AlgoGenetique geneticAlgorithm = new AlgoGenetique(route, populationSize, numberGeneration,
						mutationRate, numberEliteRoute, numberEliteRoute, crossOverCut);

				Population population = new Population(geneticAlgorithm, route, populationSize);
				population.sortRoutesByFitness();

				// Time just before starting the algorithm
				long startTime = System.nanoTime();

				int generation = 0;
				int numberWithoutGettingBetter = 0;
				double bestFitness = 0;

				while (numberWithoutGettingBetter < maxNumberWithoutGettingBetter
						&& generation < geneticAlgorithm.numberGeneration) {
					population = geneticAlgorithm.evolve(population);
					population.sortRoutesByFitness();
					double tempBestFitness = population.getRoutes().get(0).getFitness();
					if (tempBestFitness > bestFitness) {
						bestFitness = tempBestFitness;
						numberWithoutGettingBetter = 0;
					}
					else
						numberWithoutGettingBetter += 1;
					generation += 1;
				}
//				System.out.println("   numberWithoutGettingBetter : " + numberWithoutGettingBetter);
//				System.out.println("   generation : " + generation);

				long duration = (System.nanoTime() - startTime) / 1000000;
				Route bestRoute = population.getRoutes().get(0);
				
				System.out.println("bestRoute : " + bestRoute.getTotalDistance());

				// Add the relative information of the test to the content to write
				contentToWrite += nbOfCities + csvColumnDelimeter + bestRoute.citiesNameOfRoute() + csvColumnDelimeter
						+ bestRoute.getTotalDistance() + csvColumnDelimeter + bestRoute.getFitness()
						+ csvColumnDelimeter + duration + csvColumnDelimeter + populationSize + csvColumnDelimeter
						+ numberGeneration + csvColumnDelimeter + mutationRate + csvColumnDelimeter
						+ tournamentSelectionSize + csvColumnDelimeter + numberEliteRoute + csvRowDelimeter;
//			}
//		}
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
