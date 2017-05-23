package rbm;

/**
 * A single node of some neural network
 * @author Justin Rebok
 *
 */
public class Node {
	
	//Current value that the node holds
	//Working with binary nodes so a boolean is appropriate
	//Set to false at first
	private boolean value;
	
	//Constructor
	public Node(){
		this.value=false;
	}
	
	/**
	 * Activate the node based off a sigmoid activation function
	 * @param energy
	 * @return new state of the node
	 */
	public boolean activate(double energy){
		return true;
	}
	
	/**
	 * Setter for the node's state
	 * @param new value
	 */
	public void setValue(boolean nv){ this.value = nv;}
	
	/**
	 * Getter for for the node's state
	 * @return node state
	 */
	public boolean getValue(){ return this.value; }
}
