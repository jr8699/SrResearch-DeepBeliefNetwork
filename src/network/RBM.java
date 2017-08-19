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
	private Weight weights[];
	
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
	
	/**
	 * Constructor
	 * @param row1Nodes
	 * @param row2Nodes
	 * @param prev
	 * @param next
	 * @param learningRate
	 */
	public RBM (int row1Nodes, int row2Nodes, RBM prev, RBM next, float learningRate) {
		this.row1 = new Node[row1Nodes];
		this.row2 = new Node[row2Nodes];
		this.prevRBM = prev;
		this.nextRBM = next;
		this.learningRate = learningRate;
		this.bias = new float[row1Nodes+row2Nodes];
		this.weights = new Weight[row1Nodes*row2Nodes];
		//init weights
	}
	
	/**
	 * Used to initialize the RBM with a set of values for pre-training.
	 * @param values
	 */
	public void setRow1(boolean values[]) {
		for(int i = 0;i < row1.length;i++) {
			if(values[i]) row1[i].setState(true);
		}
	}
	
	/**
	 * Used to initialize the RBM's 2nd row with a set of values for back propagation.
	 * @param values
	 */
	public void setRow2(boolean values[]) {
		for(int i = 0;i < row1.length;i++) {
			if(values[i]) row1[i].setState(true);
		}
	}
	
	/**
	 * Randomizes the weights between nodes before pretraining
	 */
	private void randomizeWeights() {
		
	}
	
	/**
	 * Does all the needed calculations needed to activate a node and then
	 * activates that node. (Activation Energy, Sigmoid, and activation)
	 * @param row
	 * @param node
	 */
	private void toggleNode(int row, int node, Weight nodeWeights[]) {
		if(row == 2) {
			Node n = nodeWeights[0].getRight(); //should be same right node for all
			float b;
			b = bias[row1.length+node]; //grab bias
			
			//sum all w*x_j
			float sum = 0;
			for(int i = 0;i < nodeWeights.length;i++) {
				sum += nodeWeights[i].getWeight() * (nodeWeights[i].getLeft().getState() ? 1 : 0);
			}
			float AE = sum + b; //Activation energy
			float probability = (float)(1/(1+Math.exp(AE))); //Sigmoid
			float rand = (float) Math.random(); //Random number between 0.0 and 1.0
			if(rand<=probability) { //toggle the node
				n.setState(true);
				System.out.println("Node Activated with probability :" + probability + "and AE :" + AE);
			}else {
				n.setState(false);
				System.out.println("Node Deactivated with probability :" + probability + "and AE :" + AE);
			}
		}else {
			Node n = nodeWeights[0].getLeft(); //should be same right node for all
			float b;
			b = bias[node]; //grab bias
			
			//sum all w*x_j
			float sum = 0;
			for(int i = 0;i < nodeWeights.length;i++) {
				sum += nodeWeights[i].getWeight() * (nodeWeights[i].getRight().getState() ? 1 : 0);
			}
			float AE = sum + b; //Activation energy
			float probability = (float)(1/(1+Math.exp(AE))); //Sigmoid
			float rand = (float) Math.random(); //Random number between 0.0 and 1.0
			if(rand<=probability) { //toggle the node
				n.setState(true);
				System.out.println("Node Activated with probability :" + probability + "and AE :" + AE);
			}else {
				n.setState(false);
				System.out.println("Node Deactivated with probability :" + probability + "and AE :" + AE);
			}
		}
	}
	
	/**
	 * Conducts a step of pretraining for the RBM via contrastive divergence
	 * @param
	 * toggleNode(1,j); //calc A.E., plug into sigmoid, and activate
	 */
	public void preTrainingStep(boolean input[]) {
		setRow1(input); //Set row to do C.D. on
		//Do C.D. for row2 (POS phase)
		//An odd looking loop but it beats scanning the weights matrix 60,000 some times
		for(int i = 0; i < weights.length/row1.length;i++) {
			Weight nodeWeights[] = new Weight[row1.length]; //store all weight values for a node
			int g = 0;
			for(int j = i;j < weights.length;j += row2.length) { //gather the weights
				nodeWeights[g++] = weights[j];
			}
			toggleNode(2,i,nodeWeights);
		}
		
		//Do C.D. the other way
		for(int i = 0; i < weights.length/row2.length;i+=row2.length) {
			Weight nodeWeights[] = new Weight[row2.length]; //store all weight values for a node
			int g = 0;
			for(int j = i;j < row2.length;j++) { //gather the weights
				nodeWeights[g++] = weights[j];
			}
			toggleNode(1,i,nodeWeights);
		}
		
		//Do C.D. for row2 (Neg phase)
		for(int i = 0; i < weights.length/row1.length;i++) {
			Weight nodeWeights[] = new Weight[row1.length]; //store all weight values for a node
			int g = 0;
			for(int j = i;j < weights.length;j += row2.length) { //gather the weights
				nodeWeights[g++] = weights[j];
			}
			toggleNode(2,i,nodeWeights);
		}
		
		//Update weights
		for(Weight w : weights) {
			float pos = w.getRight().getPrevState() ? 1 : 0 * (w.getLeft().getPrevState() ? 1 : 0);
			float neg = w.getRight().getState() ? 1 : 0 * (w.getLeft().getState() ? 1 : 0);
			w.setWeight(w.getWeight()-learningRate*(pos-neg));
		}
		
		System.out.println("PreTraining step finished!");
	}
	
	/**
	 * Print all the information about the RBM, weights and node statuses
	 */
	public void dumpRBM() {
		for(int i = 0; i < weights.length; i++) {
			System.out.println(weights[i].getLeft().getState() + "   "
								+ weights[i].getWeight() + "   "
								+ weights[i].getRight().getState());
		}
	}
}
