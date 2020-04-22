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

public class TabouCollaboratif extends jade.core.behaviours.CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TabouCollaboratif(Agent a) {
		super(a);
	}

	private int step = 0;
	private int nbReplies = 0;
	ArrayList<Route> routes = new ArrayList<Route>();
	private double bestScore = 0;
	private int nbIterations = 0;

	@Override
	public void action() {
		// Initialisation des paramètres de l'algorithme

		switch (step) {
		case 0:
			// Execution of the Tabou algorithm with as a start the best solution found
			// before
			Route routeInitiale = Main.routeOptimaleTabou;
			int nbIterationsTabou = AgentTabou.nbIterationSansAmelioration;
			int tailleListeTabou = AgentTabou.tailleListeTabou;

			AgentTabou.routeOptimaleAgentTabou = AlgoTabou.optiTS(routeInitiale, nbIterationsTabou, tailleListeTabou);

			routes.add(AgentTabou.routeOptimaleAgentTabou);
			step = 1;
			break;

		case 1:

			// Send solution to the other agents

			ACLMessage mes = new ACLMessage(ACLMessage.INFORM);
			mes.addReceiver(new AID("AgentAG", AID.ISLOCALNAME));
			mes.addReceiver(new AID("AgentRS", AID.ISLOCALNAME));
			try {
				mes.setContentObject((Serializable) AgentTabou.routeOptimaleAgentTabou);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(mes);
			System.out.println(myAgent.getLocalName() + "sends road to AgentAG and AgentRS");
			step = 2;
			break;

		case 2:

			// Receive other agents solution

			ACLMessage reply = myAgent.receive();
			if (reply != null) {
				try {
					System.out.println(myAgent.getLocalName() + "receives road from " + reply.getSender().getName());
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

		case 3:

			// Compare solutions, and keep the best one to start again the process

			Main.routeOptimaleTabou = new Route(Collections.min(routes, Comparator.comparing(Route::getTotalDistance)));
			double score = Main.routeOptimaleTabou.getTotalDistance();
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
