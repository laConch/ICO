package comportements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import agents.AgentRS;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import metaheuristiques.AlgoGenetique;
import metaheuristiques.Population;
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

	// Constructor
	public GeneticConcurrence(Agent agent) {
		super(agent);
	}

	private int step = 0;
	private int nbReplies = 0;
	ArrayList<Route> routes = new ArrayList<Route>();
	private int nbIterations = 0;
	private static long startTime;
	private static long endTime;
	private double currentTimeToFindSolution;
	private double previousTimeToFindSolution = 0;
	private double currentScore;
	private double previousScore = 0;
	private double currentBestScore;
	private double previousBestScore = 0;

	AlgoGenetique geneticAlgorithm;
	Population population;

	@Override
	public void action() {
		switch (step) {
		// Execution of the AlgoGenetique from the best solution found before
		case 0:
			startTime = System.nanoTime();

			Route initialRoute = new Route(Main.routeInitialeAgentGenetique);
			geneticAlgorithm.setNumberGeneration(initialRoute.getCities().size() * 100);

			// Add the best route of the previous cycle to the population
			population.getRoutes().add(new Route(Main.routeInitialeAgentGenetique));
			population.sortRoutesByFitness();

			for (int generation = 0; generation < geneticAlgorithm.getNumberGeneration(); generation++) {
				population = geneticAlgorithm.evolve(population);
				population.sortRoutesByFitness();
			}

			endTime = System.nanoTime();
			currentTimeToFindSolution = endTime - startTime;
			currentScore = population.getRoutes().get(0).getTotalDistance();
			// Select the best result
			routes.add(population.getRoutes().get(0));
			step = 1;
			break;

		// Send solution to other agents
		case 1:

			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.addReceiver(new AID("agentRS", AID.ISLOCALNAME));
			message.addReceiver(new AID("agentTabou", AID.ISLOCALNAME));
			try {
				message.setContentObject((Serializable) population.getRoutes().get(0));
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(message);
			System.out.println(myAgent.getLocalName() + " sends road to agentRS and agentTabou");
			step = 2;

			System.out.println("------------------------------------------------------------------------------------");
			System.out.println(myAgent.getLocalName() + " : " + population.getRoutes().get(0).getTotalDistance());
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
			currentBestScore = Main.routeInitialeAgentGenetique.getTotalDistance();

			// If the genetic Agent is the best
			if (currentScore == currentBestScore) {
				if (previousScore == 0 || currentScore < previousScore) {
					// Change the parameters in order to improve the time to find a solution
					if (geneticAlgorithm.getNumberGeneration()
							- AlgoGenetique.NUMBER_GENERATION_STEP >= AlgoGenetique.NUMBER_GENERATION_MIN) {
						geneticAlgorithm.setNumberGeneration(
								geneticAlgorithm.getNumberGeneration() - AlgoGenetique.NUMBER_GENERATION_STEP);
						System.out.println(
								String.format("NumberGeneration : %s", geneticAlgorithm.getNumberGeneration()));
					} else {
						// Change parameters to improve the score
						geneticAlgorithm.setNumberGeneration(
								geneticAlgorithm.getNumberGeneration() + AlgoGenetique.NUMBER_GENERATION_STEP);
						System.out.println(
								String.format("NumberGeneration : %s", geneticAlgorithm.getNumberGeneration()));
					}
				} else {
					// If the genetic Agent is not the best
					// Change the parameters to improve the score
					// TODO : improve another parameter
					geneticAlgorithm.setNumberGeneration(
							geneticAlgorithm.getNumberGeneration() + AlgoGenetique.NUMBER_GENERATION_STEP);
					System.out.println(String.format("NumberGeneration : %s", geneticAlgorithm.getNumberGeneration()));
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