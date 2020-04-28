package support;

import agents.AgentGenetique;
import agents.AgentRS;
import agents.AgentTabou;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

/**
 * Main container for the three agents
 * 
 * @since Apr 2, 2020
 */
public class MainContainer {
	
	public static Runtime runtime;
	public static AgentContainer mainContainer;
	public static AgentController agentRS;
	public static AgentController agentAG;
	public static AgentController agentTabou;

	public static void start() {
		try {
			// Instance of Jade's environment
			runtime = Runtime.instance();

			// Container profile
			Properties properties = new ExtendedProperties();
			ProfileImpl profileImpl = new ProfileImpl(properties);

			// Main container creation
			mainContainer = runtime.createMainContainer(profileImpl);

			// Launch the agent
			mainContainer.start();

			// Creation of the RS agentRS
			agentRS = mainContainer.createNewAgent("agentRS", AgentRS.class.getName(), null);
			agentRS.start();

			// Creation of the Genetique agentGenetique
			agentAG = mainContainer.createNewAgent("agentGenetique", AgentGenetique.class.getName(), null);
			agentAG.start();

			// Creation of the Tabou agentTabou
			agentTabou = mainContainer.createNewAgent("agentTabou", AgentTabou.class.getName(), null);
			agentTabou.start();

		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}
	
	public static void shutdown() {
		try {
			mainContainer.kill(); // jade.wrapper.AgentContainer
		    Runtime.instance().shutDown(); // jade.core.Runtime
		}
		catch (StaleProxyException e ) {
			e.printStackTrace();
		}
	}

}
