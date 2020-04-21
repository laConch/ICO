package support;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

/**
 * Main container
 * 
 * @author Sethian & Bouzereau
 * @since Apr 2, 2020
 */
public class MainContainer {

	public static void main(String[] args) {
		try {
			// Instance of Jade's environment
			Runtime runtime = Runtime.instance();
			
			// Container profile
			Properties properties = new ExtendedProperties();
			ProfileImpl profileImpl = new ProfileImpl(properties);
			
			// Main container creation
			AgentContainer mainContainer = runtime.createMainContainer(profileImpl);
			
			// Launch the agent
			mainContainer.start();
			
			// Creation of the RS agentRS
			AgentController agentRS;
			agentRS = mainContainer.createNewAgent("AgentRS", "agents.AgentRS", null);
			agentRS.start();
			
			// Creation of the AG agentAG
			AgentController agentAG;
			agentAG = mainContainer.createNewAgent("agentAG", "agents.GeneticAgent", null);
			agentAG.start();
			
			//Creation of the Tabou agentTabou
			AgentController agentTabou;
			agentTabou = mainContainer.createNewAgent("agentTabou", "agents.AgentTabou", null);
			agentTabou.start();
			
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

}
