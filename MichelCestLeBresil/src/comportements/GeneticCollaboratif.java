package comportements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import metaheuristiques.AlgoGenetique;
import metaheuristiques.Population;
import support.Route;

/**
 * Behaviour for the {@link AgentGenetique} in a collaborative mode.
 * 
 * @author Sethian & Bouzereau
 * @since Apr 22, 2020
 */
public class GeneticCollaboratif extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor
	public GeneticCollaboratif(Agent agent) {
		super(agent);
	}

	private int step = 0;
	private int nbReplies = 0;
	ArrayList<Route> routes = new ArrayList<Route>();
	private double bestScore = 0;
	private int nbIterations = 0;

	Population population;
	@Override
	public void action() {
		switch (step) {
		case 0:
			// First execution of the AlgoGenetique
			AlgoGenetique geneticAlgorithm = new AlgoGenetique(routes.get(0).getCities());
			population = new Population(geneticAlgorithm, routes.get(0).getCities(),
					geneticAlgorithm.getPopulationSize());
			
			
		case 3:
			
			// New population including the best solution of the previous cycle
			population = new Population(population,
					new Route(Collections.min(routes, Comparator.comparing(Route::getTotalDistance))));

		}
			
	}

}
