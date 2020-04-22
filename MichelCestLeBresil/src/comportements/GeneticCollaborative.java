package comportements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import agents.AgentTabou;
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
 * Behaviour for the {@link AgentGenetique} in a collaborative mode.
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

	AlgoGenetique geneticAlgorithm = new AlgoGenetique(Main.initialRouteCollaborative.getCities());
	// First Population randomly generated
	Population population =new Population(geneticAlgorithm, Main.initialRouteCollaborative.getCities(),
			geneticAlgorithm.getPopulationSize());
	
	
	@Override
	public void action() {
		// Initialization of the parameters of the algorithm
		
		
		switch (step) {
		// First execution of the AlgoGenetique including the best solution of the previous cycle in the population
		case 0:
			
			Route initialRoute = new Route(Main.initialRouteCollaborative);
			
			// Add the best route of the previous cycle to the population
			population = new Population(population, initialRoute);
			population.sortRoutesByFitness();
			
			int generationNumber = 0;
			
			while (generationNumber++ < geneticAlgorithm.getNumberGeneration()) {
				population = geneticAlgorithm.evolve(population);
				population.sortRoutesByFitness();
			}
			
			// Select the best result
			routes.add(population.getRoutes().get(0));
			System.out.println("Route de l'agent Génétique :");
			population.getRoutes().get(0).printCitiesNameOfRoute();
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
			break;
		
		// Receive other agents solution
		case 2:

			ACLMessage reply = myAgent.receive();
			if (reply != null) {
				try {
					System.out.println(myAgent.getLocalName() + " receives road from " + reply.getSender().getName());
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
			
			Main.initialRouteCollaborative = new Route(
					Collections.min(routes, Comparator.comparing(Route::getTotalDistance)));
			double score = Main.initialRouteCollaborative.getTotalDistance();
			
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
