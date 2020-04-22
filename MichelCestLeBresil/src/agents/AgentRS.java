package agents;

import java.util.concurrent.TimeUnit;

import comportements.RSCollaborative;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import support.Main;
import support.Route;

public class AgentRS extends jade.core.Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Route routeOptimaleAgentRS;
	public static double coefficientRefroidissementAgentRS = 0.98;
	public static int nbIterationMaxPerCycleAgentRS = 1000;
	public long startTime;

	protected void setup() {
		startTime = System.nanoTime();
		System.out.println(this.getLocalName() + " is ready");
		addBehaviour(new RSCollaborative(this));

	}

	protected void takeDown() {
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println(this.getLocalName() + " is terminated");
		System.out.println();
		System.out.println("---------------------------------------");
		System.out.println();
		System.out.println("Solution trouvée en " + nanoSecDurationToStringInMinSecAndMilliSec(duration));
		System.out.println("Score de la solution trouvée "+ Main.routeInitialeAgentRS.getTotalDistance());
		Main.routeInitialeAgentRS.printCitiesNameOfRoute();
		
	}
	
	public static String nanoSecDurationToStringInMinSecAndMilliSec(long nanoSecDuration) {
		long duration = nanoSecDuration;
		long durationInMin = TimeUnit.MINUTES.convert(duration, TimeUnit.NANOSECONDS);
		long durationInSec = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS)-durationInMin*60;
		long durationInMilliSec = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS)-durationInSec*1000-durationInMin*60*1000;
		return Long.toString(durationInMin) + "mn : " + Long.toString(durationInSec) + "s : " + Long.toString(durationInMilliSec) + "ms";
	}
}
