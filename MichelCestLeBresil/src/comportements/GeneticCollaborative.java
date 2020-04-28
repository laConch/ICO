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
import metaheuristiques.Population;
import support.Main;
import support.Route;

/**
 * Behavior for the {@link AgentGenetique} in a collaborative mode.
 * 
 * @author Sethian & Bouzereau
 * @since Apr 22, 2020
 */
public class GeneticCollaborative extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor
	public GeneticCollaborative(Agent agent) {
		super(agent);
	}

	private int step = 0;
	private int nbReplies = 0;
	ArrayList<Route> routes = new ArrayList<Route>();
	private double bestScore = 0;
	private int nbIterations = 0;
	
	Route initialRoute = new Route(Main.routeInitialeAgentGenetique);
	AlgoGenetique geneticAlgorithm = new AlgoGenetique(initialRoute.getCities());
	Population population;
	
	@Override
	public void action() {
		switch (step) {
		// Execution of the AlgoGenetique including the best solution of the
		// previous cycle in the population
		case 0:
//			System.out.println(String.format("Gen 1 : %s", Main.routeInitialeAgentGenetique));
			initialRoute = new Route(Main.routeInitialeAgentGenetique);
			geneticAlgorithm.setNumberGeneration(initialRoute.getCities().size() * 100);
			
			population = new Population(geneticAlgorithm, initialRoute.getCities(),
					geneticAlgorithm.getPopulationSize() - 1);

			// Add the best route of the previous cycle to the population
			population.getRoutes().add(new Route(Main.routeInitialeAgentGenetique));
			population.sortRoutesByFitness();

//			System.out.println(String.format("Gen 2 : %s", population.getRoutes().get(0)));

			for (int generation = 0; generation < geneticAlgorithm.getNumberGeneration(); generation++) {
				population = geneticAlgorithm.evolve(population);
				population.sortRoutesByFitness();
			}
			
//			System.out.println(String.format("Gen 3 : %s", population.getRoutes().get(0)));

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
			//population.getRoutes().get(0).printCitiesNameOfRoute();
			System.out.println("------------------------------------------------------------------------------------");
			
			break;

		// Receive other agents solutions
		case 2:

			ACLMessage reply = myAgent.receive();
			if (reply != null) {
				try {
					System.out.println(myAgent.getLocalName() + " receives road from " + reply.getSender().getLocalName());
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
			double score = Main.routeInitialeAgentGenetique.getTotalDistance();
			Main.routeInitialeAgentGenetique = new Route(Collections.min(routes, Comparator.comparing(Route::getTotalDistance)));
			score = Main.routeInitialeAgentGenetique.getTotalDistance();

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
