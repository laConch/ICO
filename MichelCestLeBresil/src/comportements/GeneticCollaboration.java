package comportements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import metaheuristiques.AlgoGenetique;
import support.Main;
import support.Route;

/**
 * Behavior for the {@link AgentGenetique} in a collaborative mode.
 * 
 * @author Sethian & Bouzereau
 * @since Apr 22, 2020
 */
public class GeneticCollaboration extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Parameters for the GeneticAlgorithm
	private static int populationSize = 191;
	private static int numberGeneration = 1709;
	private static double mutationRate = 0.0336;
	private static int tournamentSelectionSize = 98;
	private static int numberEliteRoute = 3;
	private static int numberCrossOverRoute = 74;
	private static int numberRandomRoute = 19;
	private static double crossOverCut = 0.1470;
	private static int maxWithoutAmelioration = 1654;

	// Constructor
	public GeneticCollaboration(Agent agent) {
		super(agent);
	}

	private int step = 0;
	private int nbReplies = 0;
	private ArrayList<Route> routes = new ArrayList<Route>();
	private double bestScore = 0;
	private int nbIterations = 0;
	
	private AlgoGenetique geneticAlgorithm;
	private Route bestRoute;
	
	@Override
	public void action() {
		switch (step) {
		// Execution of the AlgoGenetique including the best solution of the
		// previous cycle in the population
		case 0:
			Route initialRoute = new Route(Main.routeInitialeAgentGenetique);
			
			geneticAlgorithm = new AlgoGenetique(initialRoute.getCities(), populationSize, numberGeneration,
					mutationRate, tournamentSelectionSize, numberEliteRoute, numberCrossOverRoute, numberRandomRoute,
					crossOverCut, maxWithoutAmelioration);
			
			bestRoute = geneticAlgorithm.runForAgent(initialRoute);

			// Select the best result
			routes.add(bestRoute);
			step = 1;
			break;

		// Send solution to other agents
		case 1:

			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.addReceiver(new AID("agentRS", AID.ISLOCALNAME));
			message.addReceiver(new AID("agentTabou", AID.ISLOCALNAME));
			try {
				message.setContentObject((Serializable) bestRoute);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(message);
			if(Main.afficherCommunicationEntreAgents){
				System.out.println(myAgent.getLocalName() + " sends road to agentRS and agentTabou");	
			}
			System.out.println(myAgent.getLocalName() + " : " + Math.round(bestRoute.getTotalDistance()));
			
			step = 2;
			break;

		// Receive other agents solutions
		case 2:

			ACLMessage reply = myAgent.receive();
			if (reply != null) {
				try {
					if(Main.afficherCommunicationEntreAgents){
						System.out.println(myAgent.getLocalName() + " receives road from " + reply.getSender().getLocalName());
					}
					routes.add((Route) reply.getContentObject());
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				nbReplies++;

			} else {
				block();
			}
			// if we received all the roads from the other agents
			if (nbReplies == 2) {
				nbReplies = 0;
				step = 3;
			}
			break;

		// Compare solutions, and keep the best one to start again the process
		case 3:
			// Add the best of the three routes
			Main.routeInitialeAgentGenetique = new Route(Collections.min(routes, Comparator.comparing(Route::getTotalDistance)));
			double score = Main.routeInitialeAgentGenetique.getTotalDistance();

			// Stop condition
			if (bestScore == 0 || score < bestScore) {
				bestScore = score;
				nbIterations = 0;
			} else {
				nbIterations++;
			}
			if (nbIterations == Main.nbIterationsMaxSansAmelioration) {
				myAgent.doDelete();
			}

			step = 0;
			break;
		}

	}
}
