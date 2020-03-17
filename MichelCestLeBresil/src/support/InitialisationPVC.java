package support;

import java.util.ArrayList;
import org.eclipse.jdt.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import RecuitSimule.RecuitSimuleAlgorithm;

public class InitialisationPVC {
	
	public static final int nbOfCitiesMin = 7; // nbOfCitiesMin > 6
	public static final int nbOfCitiesMax = 20; // nbOfCitiesMax > 6
	public static int nbOfCities = 7; // nbOfCities > 6
	public static final int nbOfTestsPerNbOfCities = 10;
	public static Double[] coefficientRefroidissementList = new Double[] {0.5,0.6,0.7,0.8,0.9};
	public static int[] nbIterationMaxPerCycleList = new int[] {400,500,600};
	public static final String csvColumnDelimeter =",";
	public static final String csvRowDelimeter ="\n";

	public static void main(String[] args) {

		int nbOfTestsRealised = 0;
		String header = "NbOfCities" + csvColumnDelimeter + "Optimal distance" + csvColumnDelimeter + "Duration (in ns)" + csvColumnDelimeter + "Temperature" + csvColumnDelimeter + "Coefficient de refroidissement" + csvColumnDelimeter + "NbIterationMaxPerCycle" +csvRowDelimeter;
		String contentToWrite = "";
		for (int i = nbOfCitiesMin; i < nbOfCitiesMax; i+=3) {
			nbOfCities = i;
			for(Double j: coefficientRefroidissementList) {
				for(int k: nbIterationMaxPerCycleList) {
					for (int l = 0; l < nbOfTestsPerNbOfCities; l++) {
						//Initialise the route with one of the two methods
						//Route initialRoute = new Route(init(),0);
						Route initialRoute = new Route(init2("FRA"),6);//null for the world, "FRA" for France, "DEU" for Germany, "GBR" for United Kingdom, "USA" for United States, "RUS" for Russia
						
						//Calculate the optimal route with the Recuit Simule Algorithm and calculate the duration of the method
						long startTime = System.nanoTime();
						Route optimalRoute = RecuitSimuleAlgorithm.obtainOptimalSolutionWithRecuitSimuleAlgorithm(initialRoute, j, k);
						long endTime = System.nanoTime();
						long duration = (endTime - startTime);
						
						//Add the relative information of the test to the content to write
						contentToWrite += i + csvColumnDelimeter + optimalRoute.getTotalDistance() + csvColumnDelimeter + Long.toString(duration) + csvColumnDelimeter + RecuitSimuleAlgorithm.temperature + csvColumnDelimeter + j + csvColumnDelimeter + k + csvRowDelimeter;
					}
				}
			}
		}
		writeOptimateResultInCSVFileCourbesComparaison(header,contentToWrite);
		
		/**
		//Display the optimal result obtained from the algorithm
		int nbOfCities = optimalRoute.getCities().size() + 1;
		System.out.print("Pour parcourir les " + nbOfCities + " villes à partir de " + initialRoute.getStartCity() + " et revenir à son point de départ, il faut : ");
		System.out.println(optimalRoute.getTotalDistance());
		optimalRoute.printCitiesNameOfRoute();
		**/
		
	}

	public static ArrayList<City> init() {
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
	
	public static ArrayList<City> init2(@Nullable String country){
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
				if(country != null) {
					String countryOfCityLine = city[6].substring(1,city[6].length()-1);
					if(countryOfCityLine.contentEquals(country)) {
						cities.add(new City(city[1].substring(1,city[1].length()-1),Double.parseDouble(city[2].substring(1, city[2].length()-1)),Double.parseDouble(city[3].substring(1, city[3].length()-1))));
						//System.out.println(city[1].substring(1,city[1].length()-1));
						nbOfCitiesAdded += 1;
					}
				}
				else {
					cities.add(new City(city[1].substring(1,city[1].length()-1),Double.parseDouble(city[2].substring(1, city[2].length()-1)),Double.parseDouble(city[3].substring(1, city[3].length()-1))));
					//System.out.println(city[1].substring(1,city[1].length()-1));
					nbOfCitiesAdded += 1;
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
