package network;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	 * The learning rate that the RBM will follow
	 */
	private float learningRate;
	
	/**
	 * Lock state of the RBM for training
	 */
	private boolean locked;
	
	/**
	 * Mark the last RBM to use softmax
	 */
	private boolean lastRBM;
	
	/**
	 * Constructor
	 * @param row1Nodes
	 * @param row2Nodes
	 * @param prev
	 * @param next
	 * @param learningRate
	 */
	public RBM (int row1Nodes, int row2Nodes, float learningRate, boolean last) {
		this.lastRBM = last;
		this.locked = false;
		this.row1 = new Node[row1Nodes];
		this.row2 = new Node[row2Nodes];
		this.learningRate = learningRate;
		this.bias = new float[row1Nodes+row2Nodes];
		this.weights = new Weight[row1Nodes*row2Nodes];
		initHiddenBias(0);
		initRows();
		initWeights();
	}
	
	/**
	 * Initialize hidden bias nodes with 0.0
	 * @param init
	 */
	private void initHiddenBias(float init) {
		for(int i = 0; i < row2.length; i++) {
			bias[i+row1.length] = init;
		}
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
				float num = (float)Math.random();
				num *= (Math.floor(Math.random()*2) == 1 ? 1 : -1);
				//this.weights[w++] = new Weight((float)Math.random(),row1[i],row2[j]);
				this.weights[w++] = new Weight(num,row1[i],row2[j]);
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
			else
				row1[i].setState(false);
		}
	}
	
	/**
	 * Used to initialize the RBM's 2nd row with a set of values for back propagation.
	 * @param values
	 */
	public void setRow2(boolean values[]) {
		for(int i = 0;i < row2.length;i++) {
			if(values[i]) row2[i].setState(true);
			else
				row2[i].setState(false);
		}
	}
	
	/**
	 * Calculates activation energy and probability of turning on
	 * @param sum
	 * @param b
	 * @return
	 */
	public float calcProbability(float sum, float b) {
		float AE = -b - sum; //Activation energy
		//System.out.println("Energy: " + AE);
		//System.out.println("|BIAS: " + -b + " SUM: " + -sum + " |" );
		return (float)(1/(1+Math.exp(AE))); //Sigmoid
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
			float probability = calcProbability(sum,b);
			float rand = (float) Math.random(); //Random number between 0.0 and 1.0
			if(rand<=probability) { //toggle the node
				n.setState(true);
				//System.out.println("Node Activated with probability : " + probability + n);
			}else {
				n.setState(false);
				//System.out.println("Node Deactivated with probability : " + probability + n);
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
			float probability = calcProbability(sum,b);
			float rand = (float) Math.random(); //Random number between 0.0 and 1.0
			if(rand<=probability) { //toggle the node
				n.setState(true);
				//System.out.println("Node Activated with probability : " + probability);
			}else {
				n.setState(false);
				//System.out.println("Node Deactivated with probability : " + probability);
			}
		}
	}
	
	/**
	 * Conducts a step of pretraining for the RBM via contrastive divergence
	 * @param
	 */
	public void preTrainingStep() {
		//positive phase
		activationPhase();
		
		//reconstruct row1 from new row2
		reconstructionPhase();
		
		//negative phase
		activationPhase();
		
		//update weights and biases to end training step
		//Don't do if locked
		updateWeightsAndBias();
		
		//System.out.println("PreTraining step finished!");
	}
	
	/**
	 * Create a reconstruction of the input
	 */
	public void reconstructionPhase() {
		//System.out.println("");
		//System.out.println("----------------------");
		//System.out.println("");
		//System.out.println("Reconstruction");
		//dumpRBM();
		//Do C.D. the other way (Reconstruction)
		//System.out.println("Reconstruction:");
		for(int i = 0; i < row1.length;i++) {
			Weight nodeWeights[] = new Weight[row2.length]; //store all weight values for a node
			int g = 0;
			for(int j = 0;j < row2.length;j++) { //gather the weights
				nodeWeights[g++] = weights[j+(i*row2.length)];
				//System.out.println("NODE: " + nodeWeights[g-1].getLeft());
			}
			toggleNode(1,i,nodeWeights);
		}
		
		//System.out.println("After Recontruction");
		//System.out.println("");
		//dumpRBM();
	}
	
	/**
	 * Positive/Negative phase of contrastive divergence
	 */
	public void activationPhase() {
		//Do C.D. for row2 (POS/NEG phase)
		//An odd looking loop but it beats scanning the weights matrix thousands of times
		//System.out.println("Activation Phase:");
		if(this.lastRBM == false) {
			/*
			System.out.println("");
			System.out.println("----------------------");
			System.out.println("");
			System.out.println("Activation");
			*/
			//dumpRBM();
			for(int i = 0; i < row2.length;i++) {
				Weight nodeWeights[] = new Weight[row1.length]; //store all weight values for a node
				int g = 0;
				for(int j = 0;j < row1.length;j++) { //gather the weights
					nodeWeights[g++] = weights[i+(j*row2.length)];
				}
				toggleNode(2,i,nodeWeights);
			}
		}else { //constrain the last row of DBN
			//softmax();
			noSoftmax();
		}
	}
	
	public void updateWeightsAndBias() {
		//Update weights
		for(Weight w : weights) {
			float pos = (w.getRight().getPrevState() ? 1 : 0) * (w.getLeft().getPrevState() ? 1 : 0);
			float neg = (w.getRight().getState() ? 1 : 0) * (w.getLeft().getState() ? 1 : 0);
			w.setWeight(w.getWeight()-(learningRate*(pos-neg)));
			//w.setWeight(w.getWeight()-(learningRate*(neg-pos)));
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
		
		//System.out.println("Updated Weights");
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
		
		System.out.println("Bias (Row1 then row2): ");
		for(int i = 0; i < bias.length; i++){
			System.out.println(bias[i]);
		}
		System.out.println(" ");
	}
	
	/**
	 * Set row1 to row2 of the previous rbm
	 * @param prbm
	 */
	public void tieToPrev(RBM prbm){
		this.row1 = prbm.getRow2();
		
		//initweights to set the weight matrix to use the new node objects
		initWeights();
	}
	
	/**
	 * Set row2 to row 1 of the next rbm
	 * @param nrbm
	 */
	public void tieToNext(RBM nrbm){
		this.row2 = nrbm.getRow1();
		
		//initweights to set the weight matrix to use the new node objects
		initWeights();
	}
	
	/**
	 * Getters for the rows of the RBM
	 * @return
	 */
	public Node[] getRow1() { return this.row1; }
	public Node[] getRow2() { return this.row2; }
	
	/**
	 * Getter for the weight matrix
	 * @return
	 */
	public Weight[] getWeights() { return this.weights; }
	
	/**
	 * Toggles the lock state of the RBM
	 */
	public void toggleLock() {
		if(!locked) locked = true;
		else locked = false;
	}
	
	/**
	 * Calculate probabilities of the last row to determine classification
	 * @return
	 */
	public int softmax() {
		System.out.println("");
		System.out.println("----------------------");
		System.out.println("");
		System.out.println("SoftMax Activation");
		float energies[] = new float[row2.length];
		for(int i = 0; i < row2.length; i++) { //find all activation energies of the last row
			Weight nodeWeights[] = new Weight[row1.length]; //store all weight values for a node
			int g = 0;
			for(int j = 0;j < row1.length;j++) { //gather the weights
				nodeWeights[g++] = weights[i+(j*row2.length)];
			}
			
			Node n = nodeWeights[0].getRight(); //should be same right node for all
			float b;
			b = bias[row1.length+n.getIndex()]; //grab bias
			
			//sum all w*x_j
			float sum = 0;
			//System.out.println("ALL WEIGHTS:");
			for(int h = 0;h < nodeWeights.length;h++) {
				//System.out.println(nodeWeights[h].getWeight() * (nodeWeights[h].getLeft().getState() ? 1 : 0));
				sum += nodeWeights[h].getWeight() * (nodeWeights[h].getLeft().getState() ? 1 : 0);
			}
			energies[i] = (float) (-b - sum); //store activation energy
			//System.out.println("Energy find " + (b - sum));
		}
		//Find top according to softmax
		
		//Find max energy to get around underflow problem
		float maxEnergy = 0.0f;
		for(int i = 0; i < energies.length; i++) {
			if(energies[i] > maxEnergy)
				maxEnergy = energies[i];
		}
		
		float max = 0;
		int index = 0;
		float sum = 0;
		//sum energies
		for(int i = 0; i < row2.length; i++) {
			//System.out.println("AE-MAX AE: "+(energies[i]-maxEnergy));
			sum += Math.exp(energies[i]-maxEnergy);
		}
		System.out.println("SUM: " + sum);
		for(int i = 0; i < row2.length; i++) {
			System.out.println("ENERGIES: " + energies[i]);
		}
		//find highest
		for(int i = 0; i < row2.length; i++) {
			System.out.println("");
			System.out.println("Prob: " + (Math.exp(energies[i]-maxEnergy))/sum + " SUM: " + sum);
			//System.out.println((energies[i]-maxEnergy)/sum + " to activate node: " + i);
			float tmpMax = (float)((Math.exp(energies[i]-maxEnergy))/sum);
			if(tmpMax > max) {
				index = i;
				max = tmpMax;
			}
		}
		
		//activate appropriate nodes
		for(int i = 0; i < row2.length; i++) {
			if(i == index) {
				row2[i].setState(true);
			}else
				row2[i].setState(false);
		}
		
		
		dumpRBM();
		return index;
	}
	
	/**
	 * Alternative to softmax, just sigmoid functions at the end
	 * @return
	 */
	
	public int noSoftmax() {
		
		//System.out.println("");
		//System.out.println("NoSoftmax Activation");
		float energies[] = new float[row2.length];
		for(int i = 0; i < row2.length; i++) { //find all activation energies of the last row
			Weight nodeWeights[] = new Weight[row1.length]; //store all weight values for a node
			int g = 0;
			for(int j = 0;j < row1.length;j++) { //gather the weights
				nodeWeights[g++] = weights[i+(j*row2.length)];
			}
			
			Node n = nodeWeights[0].getRight(); //should be same right node for all
			float b;
			b = bias[row1.length+n.getIndex()]; //grab bias
			
			//sum all w*x_j
			float sum = 0;
			//System.out.println("ALL WEIGHTS:");
			for(int h = 0;h < nodeWeights.length;h++) {
				//System.out.println(nodeWeights[h].getWeight() * (nodeWeights[h].getLeft().getState() ? 1 : 0));
				sum += nodeWeights[h].getWeight() * (nodeWeights[h].getLeft().getState() ? 1 : 0);
			}
			//System.out.println("Energy: " + (-b - sum));
			energies[i] = (float) Math.exp(-b - sum); //store activation energy
		}
		
		//Find max energy to get around underflow problem
		float maxProb = 0.0f;
		int index = -1;
		for(int i = 0; i < energies.length; i++) {
			//System.out.println("Prob: " + (1/(1+Math.exp(energies[i]))));
			if((float)(1/(1+Math.exp(energies[i]))) > maxProb) {
				maxProb = (float)(1/(1+Math.exp(energies[i])));
				index=i;
			}
		}
		if (maxProb > 0.001f) {
			int[] choice = new int[row2.length];
			int choices = 0;
			for (int i = 0; i < energies.length; i++) {
				if ((float) (1 / (1 + Math.exp(energies[i]))) > maxProb - 0.00001) {
					choice[i] = i;
					choices++;
				}
			}
			if (choices > 1) {
				int[] options = new int[choices];
				int x = 0;
				for (int i = 0; i < choice.length; i++) {
					if (choice[i] != 0) {
						options[x] = i;
						x++;
					}
				}
				index = options[(int) Math.random() * choices];
			}
		}
		else
		{
			// Nothing was strong enough to fire.
			index = -1;
		}
		
		
		
		//activate appropriate nodes
		for(int i = 0; i < row2.length; i++) {
			if(i == index) {
				row2[i].setState(true);
			}else
				row2[i].setState(false);
		}
		
		//dumpRBM();
		
		return index;
	}
	
	/**
	 * Setter for last flag
	 * @param l
	 */
	public void setLast(boolean l) { this.lastRBM = l; }
}
