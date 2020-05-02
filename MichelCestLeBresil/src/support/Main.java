package support;

import java.util.ArrayList;

import agents.AgentGenetique;
import agents.AgentRS;
import agents.AgentTabou;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;

import metaheuristiques.AlgoRS;
import metaheuristiques.AlgoTabou;
import metaheuristiques.AlgoGenetique;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Main class used to launch the different algorithms
 */
public class Main {
	
	/*
	 * Paramètres pouvant être modifiés :
	 * Si les booléens isCollaboration et isCollaborationAvancee sont faux alors c'est la concurrence
	 * Le booléen isCollaboration est prioritaire sur le booléen isCollaborationAvancee
	 */
	public static final Boolean isCollaboration = true;
	public static final Boolean isCollaborationAvancee = true;
	public static final Boolean afficherCommunicationEntreAgents = false; // Afficher ou non les messages d'envoi et de réception entre les agents
	public static int nbOfCities = 50;	// 2 < nbOfCities < 15494

	/*
	 * Parameters for the complex initialization
	 */
	// "WORLD" for the world, "FRA" for France, "DEU" for Germany, "GBR" for United Kingdom, "USA" for United States, "RUS" for Russia
	public static String countryOfCities = "WORLD";
	
	// 6 < nbOfCitiesMin < 15494
	public static final int nbOfCitiesMin = 10;
	// nbOfCitiesMax > nbOfCitiesMin
	public static final int nbOfCitiesMax = 100;
	// stepNbOfCities > 0
	public static final int stepNbOfCities = 10;
	public static final int nbOfTestsPerNbOfCities = 5;
	public static int nbOfTestsRealised = 0;
	
	/*
	 * Parameters for the three agents
	 */
	public static int nbIterationsMaxSansAmelioration = 3;
	public static Route routeInitialeAgentRS;
	public static Route routeInitialeAgentTabou;
	public static Route routeInitialeAgentGenetique;
	
	
	/*
	 * Parameters to test the RS algorithm and the Tabou algorithm
	 */
	public static Double[] coefficientRefroidissementList = new Double[] { 0.98 };
	public static int[] nbIterationMaxPerCycleList = new int[] { 1000 };
	
	/*
	 * Parameters to test the Tabou algorithm
	 */
	public static int[] tabouListSizeList = new int[] {10};
	public static int[] nbIterationTabouList = new int[] { 50 };
	
	/*
	 * Parameters to write in csv file
	 */
	public static final String csvColumnDelimeter = ",";
	public static final String csvRowDelimeter = "\n";
	public static String header = "NbOfCities" + csvColumnDelimeter + "Optimal distance" + csvColumnDelimeter + "Sequencing"
			+ csvColumnDelimeter + "Duration (in ms)" + csvRowDelimeter;
	public static String contentToWrite = "";
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		lancerAgents();
		//testerAgents();
		//testerAlgorithmeRS();
		//testerAlgorithmeTabou();
		//executeGenetic();
		//testGeneticAlgorithm();
	}
	
	/**
	 * Launch the mainContainer which contains the three agents
	 */
	public static void lancerAgents() {
		// Initialization of the initial road
		routeInitialeAgentGenetique = new Route(initialisationComplexe(countryOfCities));
		routeInitialeAgentRS = new Route(initialisationComplexe(countryOfCities));
		routeInitialeAgentTabou = new Route(initialisationComplexe(countryOfCities));
		MainContainer.start();
		nbOfTestsRealised=-2;
	}
	
	public static void testerAgents() {
		if(nbOfTestsRealised == -1) {}
		else if(nbOfTestsRealised == 0) {
			// Initialization of the initial road
			routeInitialeAgentGenetique = new Route(initialisationComplexe(countryOfCities));
			routeInitialeAgentRS = new Route(initialisationComplexe(countryOfCities));
			routeInitialeAgentTabou = new Route(initialisationComplexe(countryOfCities));
			MainContainer.start();
		}
		else if (nbOfTestsRealised<nbOfTestsPerNbOfCities || (nbOfTestsRealised==nbOfTestsPerNbOfCities && nbOfCities < nbOfCitiesMax)){
			if (nbOfTestsRealised==nbOfTestsPerNbOfCities && nbOfCities < nbOfCitiesMax) {
				nbOfCities += stepNbOfCities;
				nbOfTestsRealised = 0;
			}
			// Initialization of the initial road
			routeInitialeAgentGenetique = new Route(initialisationComplexe(countryOfCities));
			routeInitialeAgentRS = new Route(initialisationComplexe(countryOfCities));
			routeInitialeAgentTabou = new Route(initialisationComplexe(countryOfCities));
			try {
				// Instance of Jade's environment
				MainContainer.runtime = Runtime.instance();

				// Container profile
				Properties properties = new ExtendedProperties();
				ProfileImpl profileImpl = new ProfileImpl(properties);

				// Main container creation
				MainContainer.mainContainer = MainContainer.runtime.createMainContainer(profileImpl);

				// Launch the agent
				MainContainer.mainContainer.start();

				// Creation of the RS agentRS
				MainContainer.agentRS = MainContainer.mainContainer.createNewAgent("agentRS", AgentRS.class.getName(), null);
				MainContainer.agentRS.start();

				// Creation of the Genetique agentGenetique
				MainContainer.agentAG = MainContainer.mainContainer.createNewAgent("agentGenetique", AgentGenetique.class.getName(), null);
				MainContainer.agentAG.start();

				// Creation of the Tabou agentTabou
				MainContainer.agentTabou = MainContainer.mainContainer.createNewAgent("agentTabou", AgentTabou.class.getName(), null);
				MainContainer.agentTabou.start();
				
			}
			catch (ControllerException e) {
				e.printStackTrace();
			}
		}
		else {
			writeResultInCSV(header, contentToWrite);
			System.out.println("Test terminé");
		}
	}
	
	public static void testerAlgorithmeRS() {
		//int nbOfTestsRealised = 0;
		header = "NbOfCities" + csvColumnDelimeter + "Optimal distance" + csvColumnDelimeter + "Sequencing"
				+ csvColumnDelimeter + "Duration (in ms)" + csvColumnDelimeter + "Temperature" + csvColumnDelimeter
				+ "Cooling coefficient" + csvColumnDelimeter + "Number of iterations per cycle" + csvRowDelimeter;
		contentToWrite = "";
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
		header = "NbOfCities" + csvColumnDelimeter + "Optimal distance" + csvColumnDelimeter + "Sequencing"
				+ csvColumnDelimeter + "Duration (in ms)" + csvColumnDelimeter + "Taille liste Tabou"
				+ csvColumnDelimeter + "Nb itération sans changement" + csvRowDelimeter;
		contentToWrite = "";
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
	private static void executeGenetic() {
		int populationSize = 39;
		int numberGeneration = 1054;
		double mutationRate = 0.0157;
		int tournamentSelectionSize = 22;
		int numberEliteRoute = 0;
		int numberCrossOverRoute = 24;
		int numberRandomRoute = 0;
		double crossOverCut = 0.8823;
		int maxWithoutAmelioration = 165;

		routeInitialeAgentGenetique = new Route(initialisationComplexe(countryOfCities));

		AlgoGenetique geneticAlgorithm = new AlgoGenetique(routeInitialeAgentGenetique.getCities(), populationSize,
				numberGeneration, mutationRate, tournamentSelectionSize, numberEliteRoute, numberCrossOverRoute,
				numberRandomRoute, crossOverCut, maxWithoutAmelioration);
		geneticAlgorithm.run();
	}
	
	/**
	 * Return a list of cities extracted from the csv file "worldcities"
	 * 
	 * @param country : "WORLD" for the world, "FRA" for France, "DEU" for Germany,
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

		contentToWrite = "";

		for (int i = nbOfCitiesMin; i < nbOfCitiesMax + 1; i += stepNbOfCities) {
			nbOfCities = i;
			System.out.println(String.format("Number of cities : %s", nbOfCities));
			ArrayList<City> route = initialisationComplexe("WORLD");
			
			int maxWithoutGettingBetter = 10000;
			int populationSize = 30;
			int numberGeneration = nbOfCities * 100;
			double mutationRate = 0.01;
			int tournamentSelectionSize = 4;
			int numberEliteRoute = 2;
			int numberCrossOverRoute = 24;
			int numberRandomRoute = 0;
			double crossOverCut = 0.2;
			int maxWithoutAmelioration = 165;
			
			for (int j = 100; j < 1001; j += 100) {
				maxWithoutGettingBetter = j;
				System.out.println(String.format("     maxWithoutGettingBetter : %s", maxWithoutGettingBetter));
				AlgoGenetique geneticAlgorithm = new AlgoGenetique(route, populationSize, numberGeneration,
						mutationRate, tournamentSelectionSize, numberEliteRoute, numberCrossOverRoute,
						numberRandomRoute, crossOverCut, maxWithoutAmelioration);

				geneticAlgorithm.run();
			}
				
//				System.out.println("bestRoute : " + bestRoute.getTotalDistance());
//
//				// Add the relative information of the test to the content to write
//				contentToWrite += nbOfCities + csvColumnDelimeter + bestRoute.citiesNameOfRoute() + csvColumnDelimeter
//						+ bestRoute.getTotalDistance() + csvColumnDelimeter + bestRoute.getFitness()
//						+ csvColumnDelimeter + duration + csvColumnDelimeter + populationSize + csvColumnDelimeter
////						+ numberGeneration + csvColumnDelimeter + mutationRate + csvColumnDelimeter
//						+ tournamentSelectionSize + csvColumnDelimeter + numberEliteRoute + csvRowDelimeter;
//			}
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
