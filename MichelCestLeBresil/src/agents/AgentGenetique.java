package agents;

import comportements.GeneticCollaboratif;
import jade.core.Agent;

/**
 * Genetic Agent (daemon -> no graphical user interface)
 * 
 * @author Sethian & Bouzereau
 * @since Apr 2, 2020
 */
public class AgentGenetique extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private static int[] numberCitiesList = new int[] {10, 20, 30, 40, 50, 60,
	// 70, 80, 90, 100};
	private static int[] populationSizeList = new int[] { 8, 10, 12, 20 };
	// private static int[] numberGenerationList = new int[] {10, 50, 100, 1000};
	// Should be between 0 and 1
	private static double[] mutationRateList = new double[] { 0.1, 0.2, 0.3 };
	// Should be below populationSize
	// private static int[] tournamentSelectionSizeList = new int[] {2, 3, 4};
	// Should be below populationSize
	// private static int [] numberEliteRouteList = new int[] {1, 2, 3};

	protected void setup() {
		System.out.println(this.getLocalName() + " is ready");
		addBehaviour(new GeneticCollaboratif(this));
	}

	protected void takedown() {
		System.out.println(this.getLocalName() + " is terminated");
	}
}
