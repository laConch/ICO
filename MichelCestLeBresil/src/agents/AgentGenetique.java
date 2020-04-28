package agents;

import comportements.GeneticCollaborative;
import comportements.GeneticConcurrence;
import jade.core.Agent;

/**
 * Genetic Agent (daemon -> no graphical user interface)
 * 
 * @author Sethian & Bouzereau
 * @since Apr 2, 2020
 */
public class AgentGenetique extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		System.out.println(this.getLocalName() + " is ready");
		addBehaviour(new GeneticCollaborative(this));
		//addBehaviour(new GeneticConcurrence(this));
	}

	protected void takeDown() {
		System.out.println(this.getLocalName() + " is terminated");
	}
}
