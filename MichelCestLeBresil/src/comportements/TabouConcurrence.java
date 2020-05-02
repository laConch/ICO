package comportements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import agents.AgentTabou;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import metaheuristiques.AlgoTabou;
import support.Main;
import support.Route;

public class TabouConcurrence extends jade.core.behaviours.CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TabouConcurrence(Agent a) {
		super(a);
	}

	private int step = 0;
	private int nbReplies = 0;
	private int nbIterations = 0;

	private static long startTime;
	private static long endTime;
	private double currentTimeToFindSolution;
	private double previousTimeToFindSolution = 0;
	
	private double currentScore;
	private double previousScore = 0;
	private double currentBestScore;
	private double previousBestScore = 0;
	
	ArrayList<Route> routes = new ArrayList<Route>();
	

	@Override
	public void action() {
		// Initialization of the parameters of the algorithm

		switch (step) {
		// Execution of the Tabou algorithm with as a start the best solution found
		// before
		case 0:

			Route routeInitiale = new Route(AgentTabou.routeOptimaleAgentTabou);
			int nbIterationsTabou = AgentTabou.nbIterationSansAmelioration;
			int tailleListeTabou = AgentTabou.tailleListeTabou;
			AgentTabou.routeOptimaleAgentTabou = AlgoTabou.optiTS(routeInitiale, nbIterationsTabou, tailleListeTabou);
			routes.add(AgentTabou.routeOptimaleAgentTabou);
			currentScore = AgentTabou.routeOptimaleAgentTabou.getTotalDistance();
			step = 1;
			break;

		// Send solution to the other agents
		case 1:
			
			ACLMessage mes = new ACLMessage(ACLMessage.INFORM);
			mes.addReceiver(new AID("agentGenetique", AID.ISLOCALNAME));
			mes.addReceiver(new AID("AgentRS", AID.ISLOCALNAME));
			try {
				mes.setContentObject((Serializable) AgentTabou.routeOptimaleAgentTabou);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(mes);
			if(Main.afficherCommunicationEntreAgents){
				System.out.println(myAgent.getLocalName() + " sends road to agentGenetique and AgentTabou");
			}
			System.out.println(myAgent.getLocalName() + " : " + Math.round(AgentTabou.routeOptimaleAgentTabou.getTotalDistance()));
			
			step = 2;
			break;

		// Receive other agents solution
		case 2:

			ACLMessage reply = myAgent.receive();
			if (reply != null) {
				try {
					if(Main.afficherCommunicationEntreAgents){
						System.out.println(myAgent.getLocalName() + " receives road from " + reply.getSender().getLocalName());
					}
					routes.add((Route) reply.getContentObject());
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
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
			Main.routeInitialeAgentTabou = new Route(Collections.min(routes, Comparator.comparing(Route::getTotalDistance)));
			currentBestScore = Main.routeInitialeAgentTabou.getTotalDistance();

			// Si l'agent Tabou est le plus performant
			if(currentScore == currentBestScore) {
				if(previousScore == 0 || currentScore < previousScore) {
					// Change parameters to improve the time to find solution
					if (AgentTabou.nbIterationMaxPerCycleAgentTabou - AgentTabou.stepNbIterationMaxPerCycleAgentTabou >= AgentTabou.nbMinIterationMaxPerCycleAgentTabou) {
						AgentTabou.nbIterationMaxPerCycleAgentTabou -= AgentTabou.stepNbIterationMaxPerCycleAgentTabou;
					}
					
				}
				else {
					// Change parameters to improve the score
					AgentTabou.tailleListeTabou += AgentTabou.steptailleListeTabou;
					//System.out.println(AgentTabou.tailleListeTabou);
				}
			}

			// Si l'agent Tabou n'est pas le plus performant
			else{
				// Change parameters to improve the score
				AgentTabou.tailleListeTabou += AgentTabou.steptailleListeTabou;
				//System.out.println(AgentTabou.tailleListeTabou);
			}
			
			// Stop condition
			if (previousBestScore == 0 || currentBestScore < previousBestScore) {
				nbIterations = 0;
			} else {
				nbIterations++;
			}
			if (nbIterations == Main.nbIterationsMaxSansAmelioration) {
				myAgent.doDelete();
			}
			
			// Save current variables as previous variables for next iteration
			previousScore = currentScore;
			previousBestScore = currentBestScore;
			previousTimeToFindSolution = currentTimeToFindSolution;

			step = 0;
			break;
		}

	}
}