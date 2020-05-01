package agents;

import java.util.concurrent.TimeUnit;
import comportements.RSCollaborative;
import comportements.RSCollaborativeAvancee;
import comportements.RSConcurrence;
import support.Main;
import support.Route;

public class AgentRS extends jade.core.Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Route routeOptimaleAgentRS = new Route(Main.routeInitialeAgentRS);
	// Paramètres collaboration
	public static double coefficientRefroidissementAgentRSForCollaboration = 0.99;
	public static int nbIterationMaxPerCycleAgentRSForCollaboration = 1000;
	// Paramètres concurrence
	public static double coefficientRefroidissementAgentRS = 0.90;
	public static int stepCoefficientRefroidissementAgentRS = 5;
	public static int nbIterationMaxPerCycleAgentRS = 500;
	public static int nbMinIterationMaxPerCycleAgentRS = 100;
	public static int stepNbIterationMaxPerCycleAgentRS = 100;
	public static long startTime;
	public static long endTime;
	public static long duration;

	
	protected void setup() {
		System.out.println(this.getLocalName() + " is ready");
		if(Main.isCollaboration) {addBehaviour(new RSCollaborative(this));}
		else {
			if(Main.isCollaborationAvancee) {
				addBehaviour(new RSCollaborativeAvancee(this));
			}
			else {addBehaviour(new RSConcurrence(this));}
		}
	}

	protected void takeDown() {
		System.out.println(this.getLocalName() + " is terminated");

		endTime = System.nanoTime();
		duration = (endTime - startTime);
		
		System.out.println();
		System.out.println("---------------------------------------");
		System.out.println();
		System.out.println("Solution trouvée en " + nanoSecDurationToStringInMinSecAndMilliSec(duration));
		System.out.println("Score de la solution trouvée "+ Main.routeInitialeAgentRS.getTotalDistance());
		Main.routeInitialeAgentRS.printCitiesNameOfRoute();
		
		// Add the relative information of the test to the content to write
		Main.contentToWrite += Main.nbOfCities + Main.csvColumnDelimeter + Main.routeInitialeAgentRS.getTotalDistance() + Main.csvColumnDelimeter + Main.routeInitialeAgentRS.citiesNameOfRoute() + Main.csvColumnDelimeter+ Long.toString(Math.round(duration / 1000000))	+ Main.csvRowDelimeter;
		Main.nbOfTestsRealised++;
		Main.testerAgents();
	}

	public static String nanoSecDurationToStringInMinSecAndMilliSec(long nanoSecDuration) {
		long duration = nanoSecDuration;
		long durationInMin = TimeUnit.MINUTES.convert(duration, TimeUnit.NANOSECONDS);
		long durationInSec = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS) - durationInMin * 60;
		long durationInMilliSec = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS) - durationInSec * 1000
				- durationInMin * 60 * 1000;
		return Long.toString(durationInMin) + "mn : " + Long.toString(durationInSec) + "s : "
				+ Long.toString(durationInMilliSec) + "ms";
	}
}
