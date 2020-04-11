package agents;

import comportements.GeneticBehaviour;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * Genetic Agent (daemon -> no graphical user interface)
 * 
 * @author Sethian & Bouzereau
 * @since Apr 2, 2020
 */
public class GeneticAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		//yellowPagesRegistration();
		System.out.println(this.getLocalName() + " is ready");
		// Adding a behavior
		
		SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
		addBehaviour(sequentialBehaviour);
		
		
		sequentialBehaviour.addSubBehaviour(new GeneticBehaviour());
		//coordinator agent creation & update gentic agent
	}
	

	/**
	 * Register the different services of the agent to the yellow pages (directory
	 * facilitator)
	 */
	private void yellowPagesRegistration() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("genetic algorithm");
		sd.setName("genetic");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

}
