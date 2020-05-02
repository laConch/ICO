package agents;

import comportements.TabouCollaboration;
import comportements.TabouCollaborationAvancee;
import comportements.TabouConcurrence;
import support.Main;
import support.Route;

public class AgentTabou extends jade.core.Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Route routeOptimaleAgentTabou;
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
		if(Main.afficherCommunicationEntreAgents){System.out.println(this.getLocalName() + " is ready");}
		
		routeOptimaleAgentTabou = new Route(Main.routeInitialeAgentTabou);
		tailleListeTabou = 10;
		nbIterationMaxPerCycleAgentTabou = 500;
		
		if(Main.isCollaboration) {addBehaviour(new TabouCollaboration(this));}
		else {
			if(Main.isCollaborationAvancee) {
				addBehaviour(new TabouCollaborationAvancee(this));
			}
			else {addBehaviour(new TabouConcurrence(this));}
		}
	}
	

	protected void takeDown() {
		if(Main.afficherCommunicationEntreAgents){System.out.println(this.getLocalName() + " is terminated");}
	}

}