package comportements;

import java.util.Collections;

import agents.AgentRS;
import jade.core.Agent;
import support.Main;
import support.Route;

public class ComportementRS extends jade.core.behaviours.OneShotBehaviour{
	
	public ComportementRS(Agent a) {
		super(a);
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		//Initialisation des paramètres de l'algorithme 
		Route currentRoute = new Route(Main.routeInitialeAgentRS);
		Route searchedRoute = new Route(Main.routeInitialeAgentRS);
		AgentRS.routeOptimaleAgentRS = new Route(Main.routeInitialeAgentRS);
		double coefficientRefroidissement = AgentRS.coefficientRefroidissementAgentRS;
		int nbIterationMaxPerCycle = AgentRS.nbIterationMaxPerCycleAgentRS;
		
		int routeSize = currentRoute.getCities().size();

		double averageDistance = 0;
		for(int i = 0; i < routeSize; i++) {
			averageDistance += currentRoute.getCities().get(i).measureDistance(currentRoute.getCities().get(0));
		}
		double temperature = averageDistance/routeSize;
		
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
				if(currentRoute.getTotalDistance()<AgentRS.routeOptimaleAgentRS.getTotalDistance()) {
					AgentRS.routeOptimaleAgentRS = new Route(currentRoute);
				}
			}
			temperature *= coefficientRefroidissement;
		}
	}
	
}
