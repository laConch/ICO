package comportements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import agents.AgentRS;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import support.Main;
import support.Route;

public class RSCollaborative extends jade.core.behaviours.CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RSCollaborative(Agent a) {
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
		// Execution of the Recuit Simule algorithm with as a start the best solution
		// found before
		case 0:
			
			Route currentRoute = new Route(Main.initialRouteCollaborative);
			Route searchedRoute = new Route(Main.initialRouteCollaborative);
			AgentRS.routeOptimaleAgentRS = new Route(Main.initialRouteCollaborative);
			double coefficientRefroidissement = AgentRS.coefficientRefroidissementAgentRS;
			int nbIterationMaxPerCycle = AgentRS.nbIterationMaxPerCycleAgentRS;

			int routeSize = currentRoute.getCities().size();

			double averageDistance = 0;
			for (int i = 0; i < routeSize; i++) {
				averageDistance += currentRoute.getCities().get(i).measureDistance(currentRoute.getCities().get(0));
			}
			double temperature = averageDistance / routeSize;

			Boolean nouveauCycle = true;

			while (nouveauCycle) {
				nouveauCycle = false;
				int nbIteration = 0;
				while (nbIteration < nbIterationMaxPerCycle) {
					nbIteration += 1;

					int r1 = (int) Math.round(Math.random() * (routeSize - 1));
					int r2 = (int) Math.round(Math.random() * (routeSize - 1));
					Collections.swap(searchedRoute.getCities(), r1, r2);
					double deltaDistance = searchedRoute.getTotalDistance() - currentRoute.getTotalDistance();
					double probabiliteModificationAccepted = Math.exp(-deltaDistance / temperature);
					if (deltaDistance < 0) {
						currentRoute = new Route(searchedRoute);
						nouveauCycle = true;
					} else if (Math.random() < probabiliteModificationAccepted && deltaDistance > 0) {
						currentRoute = new Route(searchedRoute);
						nouveauCycle = true;
					} else {
						searchedRoute = new Route(currentRoute);
					}
					if (currentRoute.getTotalDistance() < AgentRS.routeOptimaleAgentRS.getTotalDistance()) {
						AgentRS.routeOptimaleAgentRS = new Route(currentRoute);
					}
				}
				temperature *= coefficientRefroidissement;
			}
			routes.add(AgentRS.routeOptimaleAgentRS);
			step = 1;
			break;

		// Send solution to the other agents
		case 1:

			ACLMessage mes = new ACLMessage(ACLMessage.INFORM);
			mes.addReceiver(new AID("agentGenetique", AID.ISLOCALNAME));
			mes.addReceiver(new AID("agentTabou", AID.ISLOCALNAME));
			try {
				mes.setContentObject((Serializable) AgentRS.routeOptimaleAgentRS);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(mes);
			System.out.println(myAgent.getLocalName() + " sends road to agentGenetique and agentTabou");
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