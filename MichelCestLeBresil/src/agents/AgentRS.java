package agents;

import comportements.ComportementRS;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import support.Route;

public class AgentRS extends jade.core.Agent{

	
	public static Route routeOptimaleAgentRS;
	public static double coefficientRefroidissementAgentRS = 0.98;
	public static int nbIterationMaxPerCycleAgentRS = 1000;
	
	protected void setup() {
		SequentialBehaviour comportementSequentiel = new SequentialBehaviour();
		comportementSequentiel.addSubBehaviour(new ComportementRS(this));
		comportementSequentiel.addSubBehaviour(new OneShotBehaviour() {
			public void action() {
				System.out.println("La distance obtenue est de : " + routeOptimaleAgentRS.getTotalDistance());
				routeOptimaleAgentRS.printCitiesNameOfRoute();
				doDelete();
			}
		});
		addBehaviour(comportementSequentiel);
	}
}
