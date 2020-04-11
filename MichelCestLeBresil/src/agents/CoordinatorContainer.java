package agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

/**
 * Container for the coordinator agent
 * 
 * @since Apr 11, 2020
 */
public class CoordinatorContainer {

	public static void main(String[] args) {
		try {
			// Instance of the Jade environment
			Runtime runtime = Runtime.instance();

			// Container's profile
			ProfileImpl profileImpl = new ProfileImpl(false);
			profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");

			// Agent container creation
			AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);

			AgentController agentController = agentContainer.createNewAgent("coordinator",
					CoordinatorAgent.class.getName(), new Object[] {});
			
			// Launch the agent
			agentController.start();
			
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

}
