package metaheuristiques;
import support.Route;

import java.util.Collections;

public class AlgoRS {

	public static double initialTemperature;
	public static double temperature;
	
	public static Route obtainOptimalSolutionWithRecuitSimuleAlgorithm(Route initialRoute, Double coefficientRefroidissement, int nbIterationMaxPerCycle) {
		
		Route currentRoute = new Route(initialRoute);
		Route searchedRoute = new Route(initialRoute);
		Route optimalRoute = new Route(initialRoute);

		int routeSize = initialRoute.getCities().size();
		
		double averageDistance = 0;
		for(int i=0; i<routeSize; i++) {
			averageDistance += initialRoute.getCities().get(i).measureDistance(initialRoute.getCities().get(0));  
		}
		initialTemperature = averageDistance;
		temperature = initialTemperature;
		Boolean nouveauCycle = true;
		
		
		
		while(nouveauCycle) {
			nouveauCycle = false;
			int nbIteration = 0;
			while(nbIteration < nbIterationMaxPerCycle) {
				nbIteration += 1;
				
				int r1 = (int) Math.round(Math.random() * (routeSize-1));
				int r2 = (int) Math.round(Math.random() * (routeSize-1));
				Collections.swap(searchedRoute.getCities(), r1, r2);
				double deltaDistance = searchedRoute.getTotalDistance()-currentRoute.getTotalDistance();
				double probabiliteModificationAccepted = Math.exp(-deltaDistance/temperature);
				if(deltaDistance<0) {
					currentRoute = new Route(searchedRoute);
					nouveauCycle = true;
				}
				else if(Math.random()<probabiliteModificationAccepted && deltaDistance>0){
					currentRoute = new Route(searchedRoute);
					nouveauCycle = true;
				}
				else {
					searchedRoute = new Route(currentRoute);
				}
				
				if(currentRoute.getTotalDistance()<optimalRoute.getTotalDistance()) {
					optimalRoute = new Route(currentRoute);
				}
			}
			temperature *= coefficientRefroidissement;
		}
		return optimalRoute;
	}
	
	
}
