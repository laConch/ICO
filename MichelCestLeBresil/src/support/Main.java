package support;

import java.util.ArrayList;
import algoMetaheuristique.AlgoRS;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	
	//Paramètre initialisationComplexe
	public static int nbOfCities = 10; // 2 ≤ nbOfCities ≤ 15494
	public static String countryOfCities = "WORLD";//"WORLD" for the world, "FRA" for France, "DEU" for Germany, "GBR" for United Kingdom, "USA" for United States, "RUS" for Russia
	
	//Paramètre agentRS
	public static Route routeInitialeAgentRS;
	
	//Paramètres test algorithme RS
	public static final int nbOfCitiesMin = 10; // nbOfCitiesMin > 6
	public static final int nbOfCitiesMax = 30; // nbOfCitiesMax > 6
	public static final int nbOfTestsPerNbOfCities = 100;
	public static Double[] coefficientRefroidissementList = new Double[] {0.8,0.9,0.95,0.99};
	public static int[] nbIterationMaxPerCycleList = new int[] {250,500,750};
	public static final String csvColumnDelimeter =",";
	public static final String csvRowDelimeter ="\n";

	
	
	public static void main(String[] args) {
		lancerAgentRS();
		//testerAlgorithmeRS();
	}
	
	
	
	public static void lancerAgentRS() {
		//initialisation de la route initiale
		//routeInitialeAgentRS = new Route(initialisationBasique());
		routeInitialeAgentRS = new Route(initialisationComplexe(countryOfCities));//null for the world, "FRA" for France, "DEU" for Germany, "GBR" for United Kingdom, "USA" for United States, "RUS" for Russia
		
		//Création d’une instance de l’environnement Jade
		jade.core.Runtime rt = jade.core.Runtime.instance();
			
		//Création d’un profil de Container par défaut pour lancer la plateforme (création du main container)
		ProfileImpl pMain = new ProfileImpl();
		
		//Création du main container
		AgentContainer mc = rt.createMainContainer(pMain);
		
		//Création de l'agentRS
		AgentController agentRS;
		try {
			agentRS = mc.createNewAgent("AgentRS","agents.AgentRS", null);
			agentRS.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void testerAlgorithmeRS() {
		int nbOfTestsRealised = 0;
		String header = "NbOfCities" + csvColumnDelimeter + "Optimal distance" + csvColumnDelimeter + "Sequencing" + csvColumnDelimeter + "Duration (in ns)" + csvColumnDelimeter + "Temperature" + csvColumnDelimeter + "Coefficient de refroidissement" + csvColumnDelimeter + "NbIterationMaxPerCycle" +csvRowDelimeter;
		String contentToWrite = "";
		for (int i = nbOfCitiesMin; i < nbOfCitiesMax; i+=3) {
			nbOfCities = i;
			for(Double j: coefficientRefroidissementList) {
				for(int k: nbIterationMaxPerCycleList) {
					for (int l = 0; l < nbOfTestsPerNbOfCities; l++) {
						//Initialise the route with one of the two methods
						//Route initialRoute = new Route(initalisationBasique(),0);
						Route initialRoute = new Route(initialisationComplexe(countryOfCities));//null for the world, "FRA" for France, "DEU" for Germany, "GBR" for United Kingdom, "USA" for United States, "RUS" for Russia
						
						//Calculate the optimal route with the Recuit Simule Algorithm and calculate the duration of the method
						long startTime = System.nanoTime();
						Route optimalRoute = AlgoRS.obtainOptimalSolutionWithRecuitSimuleAlgorithm(initialRoute, j, k);
						long endTime = System.nanoTime();
						long duration = (endTime - startTime);
						
						//Add the relative information of the test to the content to write
						contentToWrite += i + csvColumnDelimeter + optimalRoute.getTotalDistance() + csvColumnDelimeter + optimalRoute.citiesNameOfRoute() + csvColumnDelimeter + Long.toString(duration) + csvColumnDelimeter + AlgoRS.initialTemperature + csvColumnDelimeter + j + csvColumnDelimeter + k + csvRowDelimeter;
					}
				}
			}
		}
		writeOptimateResultInCSVFileCourbesComparaison(header,contentToWrite);
	}
	

	public static ArrayList<City> initialisationBasique() {
		//Initialise the route with 6 french cities
		
		City bordeaux = new City("Bordeaux",44.833333,-0.566667);
		City lyon = new City("Lyon",45.750000,4.850000);
		City nantes = new City("Nantes",47.216667,-1.550000);
		City paris = new City("Paris",48.866667,2.333333);
		City marseille = new City("Marseille",43.300000,5.400000);
		City dijon = new City("Dijon",47.316667,5.016667);
		
		ArrayList<City> cities = new ArrayList<City>();
		
		cities.add(bordeaux);
		cities.add(lyon);
		cities.add(nantes);
		cities.add(paris);
		cities.add(marseille);
		cities.add(dijon);
		
		return cities;
	}
	
	public static ArrayList<City> initialisationComplexe(String country){
		//Initialise the route from a certain number of cities from all around the world or from a country thanks to a csv database
		
		String line = "";  
		String splitBy = ","; 
		
		int nbOfCitiesVisited = 0;
		int nbOfCitiesAdded = 0;
		ArrayList<City> cities = new ArrayList<City>();
		
		try {
			//parsing a CSV file into BufferedReader class constructor
			BufferedReader br = new BufferedReader(new FileReader("src/worldcities.csv"));  
			while ((line = br.readLine()) != null && nbOfCitiesAdded < nbOfCities){  
			String[] city = line.split(splitBy);    // use comma as separator    
			if(nbOfCitiesVisited != 0) {
				if(country.contentEquals("WORLD")){
					cities.add(new City(city[1].substring(1,city[1].length()-1),Double.parseDouble(city[2].substring(1, city[2].length()-1)),Double.parseDouble(city[3].substring(1, city[3].length()-1))));
					//System.out.println(city[1].substring(1,city[1].length()-1));
					nbOfCitiesAdded += 1;
				}
				else if(country.length() == 3) {
					String countryOfCityLine = city[6].substring(1,city[6].length()-1);
					if(countryOfCityLine.contentEquals(country)) {
						cities.add(new City(city[1].substring(1,city[1].length()-1),Double.parseDouble(city[2].substring(1, city[2].length()-1)),Double.parseDouble(city[3].substring(1, city[3].length()-1))));
						//System.out.println(city[1].substring(1,city[1].length()-1));
						nbOfCitiesAdded += 1;
					}
				}
				
			}
			nbOfCitiesVisited += 1;
			if(nbOfCitiesVisited == 15494) {
				break;
			}
			}
		}
		catch (IOException e){  
			e.printStackTrace();
		}
		
		return cities;
	}
	
	public static void writeOptimateResultInCSVFileCourbesComparaison(String header, String contentToWrite) {
		//Open the csv file "courbescomparaison.csv" and add header and contentToWrite strings inside
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
