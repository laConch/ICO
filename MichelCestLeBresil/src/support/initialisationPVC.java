package support;

import java.util.ArrayList;

public class initialisationPVC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
	}
	
	public ArrayList<City> init() {
		
		City bordeaux = new City("Bordeaux",44.8404400,-0.5805000);
		City lyon = new City("Lyon",45.7484600,4.8467100);
		ArrayList<City> cities = new ArrayList<City>();
		cities.add(bordeaux);
		cities.add(lyon);
		
		return cities;
	}
	
}
