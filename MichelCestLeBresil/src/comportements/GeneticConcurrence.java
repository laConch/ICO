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
 * Behavior for the {@link AgentGenetique} in a competition mode.
 * 
 * @author Sethian & Bouzereau
 * @since Apr 28, 2020
 */
public class GeneticConcurrence extends CyclicBehaviour {

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
	
	// Parameters uses in the competition behavior to improve the time to find a
	// solution
	public static final int NUMBER_GENERATION_MIN = 500;
	public static final int NUMBER_GENERATION_STEP = 500;

	// Constructor
	public GeneticConcurrence(Agent agent) {
		super(agent);
	}

	private int step = 0;
	private int nbReplies = 0;
	ArrayList<Route> routes = new ArrayList<Route>();
	private int nbIterations = 0;
	private static long startTime;
	private double currentTimeToFindSolution;
	private double previousTimeToFindSolution = 0;
	private double currentScore;
	private double previousScore = 0;
	private double previousBestScore = 0;

	AlgoGenetique geneticAlgorithm;

	@Override
	public void action() {
		switch (step) {
		// Execution of the AlgoGenetique from the best solution found before
		case 0:
			startTime = System.nanoTime();

			Route initialRoute = new Route(Main.routeInitialeAgentGenetique);
			geneticAlgorithm = new AlgoGenetique(initialRoute.getCities(), populationSize, numberGeneration,
					mutationRate, tournamentSelectionSize, numberEliteRoute, numberCrossOverRoute, numberRandomRoute,
					crossOverCut, maxWithoutAmelioration);

			Main.routeInitialeAgentGenetique = geneticAlgorithm.runForAgent(initialRoute);

			currentTimeToFindSolution = System.nanoTime() - startTime;
			currentScore = Main.routeInitialeAgentGenetique.getTotalDistance();

			// Select the best result
			routes.add(Main.routeInitialeAgentGenetique);
			step = 1;
			break;

		// Send solution to other agents
		case 1:

			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.addReceiver(new AID("agentRS", AID.ISLOCALNAME));
			message.addReceiver(new AID("agentTabou", AID.ISLOCALNAME));
			try {
				message.setContentObject((Serializable) Main.routeInitialeAgentGenetique);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(message);
			System.out.println(myAgent.getLocalName() + " sends road to agentRS and agentTabou");
			step = 2;

			System.out.println("------------------------------------------------------------------------------------");
			System.out.println(myAgent.getLocalName() + " : " + Main.routeInitialeAgentGenetique.getTotalDistance());
			// population.getRoutes().get(0).printCitiesNameOfRoute();
			System.out.println("------------------------------------------------------------------------------------");

			break;

		// Receive other agents solutions
		case 2:

			ACLMessage reply = myAgent.receive();
			if (reply != null) {
				try {
					System.out.println(
							myAgent.getLocalName() + " receives road from " + reply.getSender().getLocalName());
					routes.add((Route) reply.getContentObject());
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				nbReplies++;

			} else
				block();

			// if we received all the roads from the other agents
			if (nbReplies == 2) {
				nbReplies = 0;
				step = 3;
			}
			break;

		// Compare solutions, and keep the best one to start again the process
		case 3:
			Main.routeInitialeAgentGenetique = new Route(
					Collections.min(routes, Comparator.comparing(Route::getTotalDistance)));
			double currentBestScore = Main.routeInitialeAgentGenetique.getTotalDistance();

			// If the genetic Agent is the best
			if (currentScore == currentBestScore) {
				if (previousScore == 0 || currentScore < previousScore) {
					// Change the parameters in order to improve the time to find a solution
					if (numberGeneration - NUMBER_GENERATION_STEP >= NUMBER_GENERATION_MIN) {
						numberGeneration -= NUMBER_GENERATION_STEP;
						System.out.println(String.format("NumberGeneration : %s", numberGeneration));
					} else {
						// Change parameters to improve the score
						numberGeneration += NUMBER_GENERATION_STEP;
						System.out.println(String.format("NumberGeneration : %s", numberGeneration));
					}
				} else {
					// If the genetic Agent is not the best
					// Change the parameters to improve the score
					// TODO : improve another parameter
					numberGeneration += NUMBER_GENERATION_STEP;
					System.out.println(String.format("NumberGeneration : %s", numberGeneration));
				}
			}

			// Stop condition
			if (previousBestScore == 0 || currentBestScore < previousBestScore)
				nbIterations = 0;
			else
				nbIterations++;

			if (nbIterations == Main.nbIterationsMaxSansAmelioration)
				myAgent.doDelete();

			// Save current variables as previous variables for next iteration
			previousScore = currentScore;
			previousBestScore = currentBestScore;
			previousTimeToFindSolution = currentTimeToFindSolution;

			step = 0;
			break;
		}
	}

}
