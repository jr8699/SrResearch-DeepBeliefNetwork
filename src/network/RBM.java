package network;

/**
 * Some Restricted Boltzmann Machine
 * @author Justin
 *
 */
public class RBM {
	/**
	 * Rows of the RBM, limited to two
	 */
	private Node row1[];
	private Node row2[];
	
	/**
	 * Weight matrix between the rows
	 * Weights between node1 and n are first then node2 and n and etc.
	 */
	private float weights[];
	
	/**
	 * Bias matrix to each of the nodes
	 * Row1 nodes first then Row2
	 */
	private float bias[];
	
	/**
	 * The previous/next RBM that this RBM is connected to
	 */
	private RBM prevRBM;
	private RBM nextRBM;
	
	/**
	 * The learning rate that the RBM will follow
	 */
	private float learningRate;
	
	
}
