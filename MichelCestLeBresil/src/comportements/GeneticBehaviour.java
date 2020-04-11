package comportements;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import support.Main;

public class GeneticBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// For the communication with the coordinator agent
	public ACLMessage received;
	public ACLMessage response;
	
	// Constructors
	public GeneticBehaviour() {
	}
	
	public GeneticBehaviour(ACLMessage received) {
		this.received = received;
	}

	@Override
	public void action() {
		if (received != null) {
			String content = received.getContent();
			// TODO : do stuff
			
			// Respond
			this.response = received.createReply();
			this.response.setPerformative(ACLMessage.INFORM);
			this.response.setContent("Demande received : processing");
		} else {
			// Default action while no coordinator agent
			// TODO launch Genetic Algorithm
			Main.executeGenetic();
		}
	}

}
