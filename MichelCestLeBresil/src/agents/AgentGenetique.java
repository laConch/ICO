package agents;

import comportements.GeneticCollaboration;
import comportements.GeneticCollaborationAvancee;
import comportements.GeneticConcurrence;
import jade.core.Agent;
import support.Main;

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
		if(Main.afficherCommunicationEntreAgents){System.out.println(this.getLocalName() + " is ready");}
		if(Main.isCollaboration) {addBehaviour(new GeneticCollaboration(this));}
		else {
			if(Main.isCollaborationAvancee) {
				addBehaviour(new GeneticCollaborationAvancee(this));
			}
			else {addBehaviour(new GeneticConcurrence(this));}
		}
	}

	protected void takeDown() {
		if(Main.afficherCommunicationEntreAgents){System.out.println(this.getLocalName() + " is terminated");}
	}
}
