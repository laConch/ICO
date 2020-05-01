package agents;

import comportements.TabouCollaborative;
import comportements.TabouCollaborativeAvancee;
import comportements.TabouConcurrence;
import support.Main;
import support.Route;

public class AgentTabou extends jade.core.Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Route routeOptimaleAgentTabou = new Route(Main.routeInitialeAgentTabou);
	public static int tailleListeTabou = 10;
	public static int steptailleListeTabou = 1;

	public static int nbIterationSansAmelioration = 50;
	public static int nbIterationMaxPerCycleAgentTabou = 500; // 1000
	public static int nbMinIterationMaxPerCycleAgentTabou = 100;
	public static int stepNbIterationMaxPerCycleAgentTabou = 100;

	public static long startTime;
	public static long endTime;
	public static long duration;

	protected void setup() {
		System.out.println(this.getLocalName() + " is ready");
		if(Main.isCollaboration) {addBehaviour(new TabouCollaborative(this));}
		else {
			if(Main.isCollaborationAvancee) {
				addBehaviour(new TabouCollaborativeAvancee(this));
			}
			else {addBehaviour(new TabouConcurrence(this));}
		}
	}
	

	protected void takeDown() {
		System.out.println(this.getLocalName() + " is terminated");
	}

}