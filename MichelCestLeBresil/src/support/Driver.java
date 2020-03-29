package support;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jdt.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import genetic.GeneticAlgorithm;
import genetic.Population;

/**
 * Main class used to launch the different algorithms
 */
public class Driver {

	public static final int nbOfCitiesMax = 10; // nbOfCitiesMax > 6

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// executeRecuitSimule();
		executeGenetic();
	}
	
	/**
	 * Execute the genetic algorithm and print the results. 
	 */
	public static void executeGenetic() {
		Driver driver = new Driver();
		
//		Population population = new Population(GeneticAlgorithm.POPULATION_SIZE, driver.initialRoute);
		Population population = new Population(GeneticAlgorithm.POPULATION_SIZE, init2("FRA"));
		
		population.sortRoutesByFitness();
		
//		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(driver.initialRoute);
		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(init2("FRA"));
		
		int generationNumber = 0;
		driver.printHeading(generationNumber++);
		driver.printPopulation(population);
		while (generationNumber < GeneticAlgorithm.NUMBER_GENERATION) {
			driver.printHeading(generationNumber++);
			population = geneticAlgorithm.evolve(population);
			population.sortRoutesByFitness();
			driver.printPopulation(population);
		}
	}

	// Sample of 6 cities to test the algorithms
	public ArrayList<City> initialRoute = new ArrayList<City>(
			Arrays.asList(
					new City("Bordeaux", 44.833333, -0.566667), new City("Lyon", 45.750000, 4.850000),
					new City("Nantes", 47.216667, -1.550000), new City("Paris", 48.866667, 2.333333),
					new City("Marseille", 43.300000, 5.400000), new City("Dijon", 47.316667, 5.016667)));

	/**
	 * Print the given population
	 * Used for genetic algorithm
	 * 
	 * @param population
	 */
	public void printPopulation(Population population) {
		population.getRoutes().forEach(x -> {
			System.out.println(Arrays.toString(x.getCities().toArray()) + " |  "
					+ String.format("%.4s  ", x.getFitness()) + " | " + String.format("   %.2s", x.getTotalDistance()));
		});
	}
	
	/**
	 * Print the heading
	 * 
	 * @param generationNumber
	 */
	public void printHeading(int generationNumber) {
		System.out.println("> Generation # " + generationNumber);
		String headingColumn1 =  "Route";
		String remainingHeadingColumns = "Fitness | Distance (in km)";
		int cityNamesLength = 0;
		for (int i = 0; i < initialRoute.size(); i++)
			cityNamesLength += initialRoute.get(i).getName().length();
		
		int arrayLength = cityNamesLength + initialRoute.size()*2;
		int partialLength = (arrayLength - headingColumn1.length())/2;
		for (int i = 0; i < partialLength; i++)
			System.out.print(" ");
		
		System.out.print(headingColumn1);		
		for (int i = 0; i < partialLength; i++)
			System.out.print(" ");
		
		if ((arrayLength % 2) == 0)
			System.out.print(" ");
		
		System.out.println(" | " + remainingHeadingColumns);
		cityNamesLength += remainingHeadingColumns.length() + 3;
		
		for (int i =0; i < cityNamesLength + initialRoute.size()*2; i++)
			System.out.print("-");
		System.out.println("");
	}
	
	public static ArrayList<City> init() {
		City bordeaux = new City("Bordeaux", 44.833333, -0.566667);
		City lyon = new City("Lyon", 45.750000, 4.850000);
		City nantes = new City("Nantes", 47.216667, -1.550000);
		City paris = new City("Paris", 48.866667, 2.333333);
		City marseille = new City("Marseille", 43.300000, 5.400000);
		City dijon = new City("Dijon", 47.316667, 5.016667);

		ArrayList<City> cities = new ArrayList<City>();

		cities.add(bordeaux);
		cities.add(lyon);
		cities.add(nantes);
		cities.add(paris);
		cities.add(marseille);
		cities.add(dijon);

		return cities;
	}

	/**
	 * Return a list of cities extracted from the csv file "worldcities"
	 * 
	 * @param country : null for the world, "FRA" for France, "DEU" for Germany,
	 *                "GBR" for United Kingdom, "USA" for United States, "RUS" for
	 *                Russia.
	 * @return
	 */
	public static ArrayList<City> init2(@Nullable String country) {
		String line = "";
		String splitBy = ",";

		int nbOfCitiesVisited = 0;
		int nbOfCitiesAdded = 0;
		ArrayList<City> cities = new ArrayList<City>();

		try {
			//parsing a CSV file into BufferedReader class constructor
			BufferedReader buffer = new BufferedReader(new FileReader("src/worldcities.csv"));  
			while ((line = buffer.readLine()) != null && nbOfCitiesAdded < nbOfCitiesMax){  
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cities;
	}
}
