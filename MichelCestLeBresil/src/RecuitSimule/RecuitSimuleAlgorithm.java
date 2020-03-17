package RecuitSimule;
import support.Route;

import java.util.Collections;

import support.City;

public class RecuitSimuleAlgorithm {

	
	public static Route obtainOptimalSolutionWithRecuitSimuleAlgorithm(Route initialRoute) {
		
		Route currentRoute = new Route(initialRoute);
		Route searchedRoute = new Route(initialRoute);
		Route optimalRoute = new Route(initialRoute);

		int routeSize = initialRoute.getCities().size();
		
		double averageDistance = 0;
		for(int i = 0; i < routeSize; i++) {
			averageDistance += initialRoute.getCities().get(i).measureDistance(initialRoute.getStartCity());
		}
		double temperature = averageDistance/routeSize;
		System.out.println("Temperature : " + temperature);
		final double coefficientRefroidissement = 0.95;
		final int nbIterationMaxPerCycle = 500;

		
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
				if(deltaDistance<0) {
					currentRoute = new Route(searchedRoute);
					nouveauCycle = true;
				}
				else{
					double probabiliteModificationAccepted = Math.exp(-deltaDistance/temperature);
					if(Math.random()<probabiliteModificationAccepted) {
						currentRoute = new Route(searchedRoute);
						nouveauCycle = true;
					}
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
