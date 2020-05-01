package metaheuristiques;

import java.util.ArrayList;

import support.Route;

/**
 * <p>
 * This class represents a population for the genetic algorithm</br>
 * A population is an array of routes specific to a certain geneticAlgorithm
 * (populationSize)
 * </p>
 * 
 * @author Sethian & Bouzereau
 * @since Mar 18, 2020
 */
public class Population extends ArrayList<Route> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Sorts routes by fitness
	 */
	public void sortRoutesByFitness() {
		this.sort((route1, route2) -> {
			int flag = 0;
			if (route1.getFitness() > route2.getFitness())
				flag = -1;
			else if (route1.getFitness() < route2.getFitness())
				flag = 1;
			return flag;
		});
	}
	
	/**
	 * Constructs an empty Population with the specified populationSize.
	 * 
	 * @param populationSize
	 */
	public Population(int populationSize) {
		super(populationSize);
	}

}