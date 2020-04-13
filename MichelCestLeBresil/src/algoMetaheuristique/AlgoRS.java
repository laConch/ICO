package algoMetaheuristique;
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
					//System.out.println(Math.round(optimalRoute.getTotalDistance()));
				}
			}
			temperature *= coefficientRefroidissement;
		}
		return optimalRoute;
	}
	
	private static double[] gaussian(double mean, double sigma){
		double x1, x2, w, y1, y2;
		double[] result = new double[2];
		do {
			x1 = 2.0 * (Math.random()) - 1.0;
			x2 = 2.0 * (Math.random()) - 1.0;
			w = x1 * x1 + x2 * x2;
		}
		while (w >= 1.0 || w == 0);
		w = Math.sqrt(-2.0 * Math.log(w)/w);
		y1 = x1 * w;
		y2 = x2 * w;
		result[0] = mean + y1 * sigma;
		result[1] = mean + y2 * sigma;
		return result;
	}
	
}
