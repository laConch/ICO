package agents;

import comportements.RSCollaboratif;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import support.Route;

public class AgentRS extends jade.core.Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Route routeOptimaleAgentRS;
	public static double coefficientRefroidissementAgentRS = 0.98;
	public static int nbIterationMaxPerCycleAgentRS = 1000;

	protected void setup() {

		System.out.println(this.getLocalName() + " is ready");
		addBehaviour(new RSCollaboratif(this));

	}

	protected void takedown() {
		System.out.println(this.getLocalName() + " is terminated");
	}
}
