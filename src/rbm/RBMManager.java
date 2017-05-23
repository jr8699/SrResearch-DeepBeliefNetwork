package rbm;

/**
 * Manages all the interactions between the layers of an RBM
 * Also holds all the nodes for some RBM
 * @author Justin Rebok
 *
 */
public class RBMManager {
	
	//The RBM network layers
	private Node[] RBMLayer1;
	private Node[] RBMLayer2;
	
	//Weights on the connections
	private double weights[];
	
	/**
	 * Constructor for an RBM where all layers must be created
	 * @param layer1
	 * @param layer2
	 */
	public RBMManager(int layer1, int layer2){
		this.RBMLayer1 = new Node[layer1];
		this.RBMLayer2 = new Node[layer2];
		
	}
	
	/**
	 * Constructor for some n RBM
	 * Only the hidden layer needs to be made
	 * May be redundant to do it this way but it makes the most sense
	 * Must use copy() to move new values for some layer1 to this RBM
	 * @param layer1
	 * @param layer2
	 */
	public RBMManager(Node layer1[], int layer2){
		
	}
	
	/**
	 * Grabs the hidden layer and from the param RBMManager
	 * and sets the visible layer of the current RBMManager to that hidden layer
	 * @param r
	 */
	public void copy(RBMManager r){
		
	}
	
	/**
	 * Pre-Training for the RBM
	 * This does one full step of it
	 */
	public void contrastiveDivergence(){
		
	}
	
	/**
	 * Calculates the Activation Energy for some node in some layer
	 * @param index
	 * @param layer
	 * @return A.E.
	 */
	private double calculateActivationEnergy(int index, int layer){
		return 0.0;
	}
	
	public Node[] getLayer1(){ return this.RBMLayer1; }
	public Node[] getLayer2(){ return this.RBMLayer2; }
	public double[] getWeights(){ return this.weights; }
	public void setLayer1(Node layer[]){ this.RBMLayer1 = layer; }
}
