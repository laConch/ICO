package genetic;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class GeneticBehaviour extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ACLMessage received;
	
	public ACLMessage response;
	
	// Constructor
	GeneticBehaviour(ACLMessage received) {
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
		} else
			block();
	}

}
