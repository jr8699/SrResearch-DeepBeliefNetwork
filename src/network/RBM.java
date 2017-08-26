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
		initRows();
		initWeights();
	}
	
	/**
	 * Creates all the node objects for the row arrays
	 */
	private void initRows() {
		for(int i = 0;i < row1.length;i++) {
			row1[i] = new Node(i);
		}
		
		for(int i = 0;i < row2.length;i++) {
			row2[i] = new Node(i);
		}
	}
	
	/**
	 * Initializes the weight matrix with random values
	 * @param w
	 */
	private void initWeights() {
		//connect all row1 nodes to all row2 nodes
		//randomize the weight between each of the nodes along the way
		int w = 0;
		for(int i = 0;i < this.row1.length;i++) {
			for(int j = 0;j < this.row2.length;j++) {
				this.weights[w++] = new Weight((float)Math.random(),row1[i],row2[j]);
			}
		}
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
				System.out.println("Node Activated with probability : " + probability + " and AE : " + AE);
			}else {
				n.setState(false);
				System.out.println("Node Deactivated with probability : " + probability + " and AE : " + AE);
			}
		}else {
			Node n = nodeWeights[0].getLeft(); //should be same left node for all
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
				System.out.println("Node Activated with probability : " + probability + " and AE : " + AE);
			}else {
				n.setState(false);
				System.out.println("Node Deactivated with probability : " + probability + " and AE : " + AE);
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
		System.out.println("Positive:");
		for(int i = 0; i < row2.length;i++) {
			Weight nodeWeights[] = new Weight[row1.length]; //store all weight values for a node
			int g = 0;
			for(int j = 0;j < row1.length;j++) { //gather the weights
				nodeWeights[g++] = weights[i+(j*row2.length)];
			}
			toggleNode(2,i,nodeWeights);
		}
		
		//Do C.D. the other way (Reconstruction)
		System.out.println("Reconstruction:");
		for(int i = 0; i < row1.length;i++) {
			Weight nodeWeights[] = new Weight[row2.length]; //store all weight values for a node
			int g = 0;
			for(int j = 0;j < row2.length;j++) { //gather the weights
				nodeWeights[g++] = weights[j+(i*row2.length)];
			}
			toggleNode(1,i,nodeWeights);
		}
		
		//Do C.D. for row2 (Neg phase)
		System.out.println("Negative:");
		for(int i = 0; i < row2.length;i++) {
			Weight nodeWeights[] = new Weight[row1.length]; //store all weight values for a node
			int g = 0;
			for(int j = 0;j < row1.length;j++) { //gather the weights
				nodeWeights[g++] = weights[i+(j*row2.length)];
			}
			toggleNode(2,i,nodeWeights);
		}
		
		//Update weights
		for(Weight w : weights) {
			float pos = (w.getRight().getPrevState() ? 1 : 0) * (w.getLeft().getPrevState() ? 1 : 0);
			float neg = (w.getRight().getState() ? 1 : 0) * (w.getLeft().getState() ? 1 : 0);
			w.setWeight(w.getWeight()-(learningRate*(pos-neg)));
		}
		
		//Update bias (row1)
		for(int i = 0;i < row1.length;i++) {
			float pos = (this.row1[i].getPrevState() ? 1 : 0) * 1;
			float neg = (this.row1[i].getState() ? 1 : 0) * 1;
			bias[i] -= learningRate*(pos-neg);
		}
		
		//Update bias (row2)
		for(int i = 0;i < row2.length;i++) {
			float pos = (this.row2[i].getPrevState() ? 1 : 0) * 1;
			float neg = (this.row2[i].getState() ? 1 : 0) * 1;
			bias[i+row1.length] -= learningRate*(pos-neg);
		}
		
		System.out.println("PreTraining step finished!");
	}
	
	/**
	 * Print all the information about the RBM, weights and node statuses
	 */
	public void dumpRBM() {
		for(int i = 0; i < weights.length; i++) {
			System.out.println(weights[i].getLeft().getState() + "-" + weights[i].getLeft() + "   "
								+ weights[i].getWeight() + "   "
								+ weights[i].getRight().getState() + "-" + weights[i].getRight());
		}
		/**
		for(int i = 0; i < row1.length; i++) {
			System.out.println(row1[i].getState());
		}
		*/
	}
}
