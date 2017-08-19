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
	 * Getter for state
	 * @return current state of the node
	 */
	public boolean getState() { return this.state; }
	
	/**
	 * Setter for state
	 * @param state
	 */
	public void setState(boolean state) { this.state = state; }
}
