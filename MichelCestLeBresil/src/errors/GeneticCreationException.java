package errors;

/**
 * Exception generated when wrong parameters are used in the creation of a
 * Genetic Algorithm
 * 
 * @author Sethian & Bouzereau
 * @since Apr 24, 2020
 */
public class GeneticCreationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GeneticCreationException(String errorMessage) {
		super(errorMessage);
	}

}
