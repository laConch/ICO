package support;

import java.util.ArrayList;
import org.eclipse.jdt.annotation.Nullable;
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import RecuitSimule.RecuitSimuleAlgorithm;

public class InitialisationPVC {
	
	public static final int nbOfCitiesMax = 10; // nbOfCitiesMax > 6

	public static void main(String[] args) {
		Route initialRoute = new Route(init(),0);
		//Route initialRoute = new Route(init2("FRA"),6);//null for the world, "FRA" for France, "DEU" for Germany, "GBR" for United Kingdom, "USA" for United States, "RUS" for Russia
		int nbOfCities = initialRoute.getCities().size() + 1;
		Route optimalRoute = RecuitSimuleAlgorithm.obtainOptimalSolutionWithRecuitSimuleAlgorithm(initialRoute);
		
		System.out.print("Pour parcourir les " + nbOfCities + " villes à partir de " + initialRoute.getStartCity() + " et revenir à son point de départ, il faut : ");
		System.out.println(optimalRoute.getTotalDistance());
		optimalRoute.printCitiesNameOfRoute();
		
	}

	public static ArrayList<City> init() {
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
		String line = "";  
		String splitBy = ","; 
		
		int nbOfCitiesVisited = 0;
		int nbOfCitiesAdded = 0;
		ArrayList<City> cities = new ArrayList<City>();
		
		try {
			//parsing a CSV file into BufferedReader class constructor
			BufferedReader br = new BufferedReader(new FileReader("src/worldcities.csv"));  
			while ((line = br.readLine()) != null && nbOfCitiesAdded < nbOfCitiesMax){  
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
				br.close();
				break;
			}
			br.close();
			}
		}
		catch (IOException e){  
			e.printStackTrace();
		}
		
		return cities;
	}
}
