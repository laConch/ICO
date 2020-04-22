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

public class TabouCollaborative extends jade.core.behaviours.CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TabouCollaborative(Agent a) {
		super(a);
	}

	private int step = 0;
	private int nbReplies = 0;
	ArrayList<Route> routes = new ArrayList<Route>();
	private double bestScore = 0;
	private int nbIterations = 0;

	@Override
	public void action() {
		// Initialization of the parameters of the algorithm

		switch (step) {
		// Execution of the Tabou algorithm with as a start the best solution found
		// before
		case 0:

			Route routeInitiale = new Route(Main.routeInitialeAgentTabou);
			int nbIterationsTabou = AgentTabou.nbIterationSansAmelioration;
			int tailleListeTabou = AgentTabou.tailleListeTabou;

			AgentTabou.routeOptimaleAgentTabou = AlgoTabou.optiTS(routeInitiale, nbIterationsTabou, tailleListeTabou);
			System.out.println("Route de l'agent Tabou :");
			AgentTabou.routeOptimaleAgentTabou.printCitiesNameOfRoute();
			
			routes.add(AgentTabou.routeOptimaleAgentTabou);
			step = 1;
			break;

		// Send solution to the other agents
		case 1:

			ACLMessage mes = new ACLMessage(ACLMessage.INFORM);
			mes.addReceiver(new AID("agentGenetique", AID.ISLOCALNAME));
			mes.addReceiver(new AID("agentRS", AID.ISLOCALNAME));
			try {
				mes.setContentObject((Serializable) AgentTabou.routeOptimaleAgentTabou);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(mes);
			System.out.println(myAgent.getLocalName() + " sends road to agentGenetique and agentRS");
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

			Main.routeInitialeAgentTabou = new Route(
					Collections.min(routes, Comparator.comparing(Route::getTotalDistance)));
			double score = Main.routeInitialeAgentTabou.getTotalDistance();
			System.out.println("Score de la route donnée par :" +myAgent.getLocalName()+score);
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
