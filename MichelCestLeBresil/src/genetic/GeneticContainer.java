package genetic;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

/**
 * Container for the genetic algorithm agent
 * 
 * @author Sethian & Bouzereau
 * @since Apr 2, 2020
 */
public class GeneticContainer {

	public static void main(String[] args) {
		try {
			// Instance of Jade's environment
			Runtime runtime = Runtime.instance();

			// Container's profile
			ProfileImpl profileImpl = new ProfileImpl(false);
			profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");

			// Agent container creation
			AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);

			AgentController agentController = agentContainer.createNewAgent("genetic", GeneticAgent.class.getName(),
					new Object[] {});

			// Launch the agent
			agentController.start();

		} catch (ControllerException e) {
			e.printStackTrace();
		}

	}

}
