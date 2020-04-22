package agents;

import comportements.TabouCollaboratif;
/*
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
*/
import support.Route;

public class AgentTabou extends jade.core.Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Route routeOptimaleAgentTabou;
	public static int tailleListeTabou = 10;
	public static int nbIterationSansAmelioration = 50;

	protected void setup() {

		System.out.println(this.getLocalName() + " is ready");
		addBehaviour(new TabouCollaboratif(this));

	}

	protected void takedown() {
		System.out.println(this.getLocalName() + " is terminated");
	}
}
