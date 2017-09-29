package network;

/**
 * Some node in a neural network
 * @author Justin
 *
 */
public class Node {
	/**
	 * State of the node: on or off
	 */
	private boolean state = false;
	
	/**
	 * Used for training so we can tell if the state changed
	 */
	private boolean prevState = false;
	
	/**
	 * Used mainly for toString to identify the node
	 */
	private int index;
	
	/**
	 * Default constructor
	 */
	public Node() {
		//Nothing
	}

	/**
	 * Constructor w/ index specification
	 * @param index
	 */
	public Node(int index) {
		this.index = index;
	}
	
	/**
	 * Getter for state and prevState
	 * @return current state of the node
	 */
	public boolean getState() { return this.state; }
	public boolean getPrevState() { return this.prevState; }
	
	/**
	 * Setter for state
	 * Sets prevState with state's previous value
	 * @param state
	 */
	public void setState(boolean state) { this.prevState = this.state; this.state = state; }
	
	/**
	 * Getter for index
	 * @return
	 */
	public int getIndex() { return this.index; }
	
	/**
	 * toString
	 */
	public String toString() {
		return "< " + index + " >";
	}
	
	/**
	 * For comparing Node references
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		else
			return false;
	}

}
