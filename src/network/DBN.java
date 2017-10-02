package network;

import java.util.List;

import io.DBNInputLoader;

/**
 * Some Deep Belief Network
 * @author Justin
 *
 */
public class DBN {
	/**
	 * Loads input from directories
	 */
	private DBNInputLoader loader;
	
	/**
	 * All the RBMs in the DBN
	 */
	private RBM rbmArray[];
	
	/**
	 * Top 50 words for each category
	 */
	private String top50[][];
	
	/**
	 * On construction:
	 * Create all RBMs and tie them together (softmax at the end)
	 * Create the softmax RBM and place/tie to the end
	 * @param rbmNum
	 * @param rowNum[][]
	 * @param learningRate
	 * @param top50Dir
	 * @param docDir
	 * @param numCat
	 * @param docsPerCat[]
	 */
	public DBN(int rbmNum, int rowNum[][], float learningRate, 
					String top50Dir, String docDir,
					int numCat, int docsPerCat[],
					String catNames[]){
		
		//Loader for input for the network
		this.loader = new DBNInputLoader(top50Dir, docDir, numCat, docsPerCat);
		
		//all rbms in the network
		this.rbmArray = new RBM[rbmNum];
		
		//rather inefficient since weights are reset each time
		//new rows are set but it works
		
		//create rbms and tie to the prev
		for(int i = 0; i < rbmNum; i++){
			this.rbmArray[i] = new RBM(rowNum[i][0], rowNum[i][1],learningRate);
			if(i>0){
				this.rbmArray[i].tieToPrev(rbmArray[i-1]);
			}
		}
		
		//Tie to the next rbm
		for(int i = 0; i < rbmNum-1;i++){
			this.rbmArray[i].tieToNext(this.rbmArray[i+1]);
		}
		
		//Load top50 words
		loadTop50();
	}
	
	/**
	 * Does all steps of pretraining for all input
	 * Need to consult on how to do this (Random documents, each category at once, etc.)
	 */
	public void fullPreTraining() {
		
	}
	
	/**
	 * Do one step of C.D. w/ category, doc, and rbm index to train
	 * @param cat
	 * @param doc
	 */
	public void preTrainingOneStep(int rbm, boolean values[]){
		int j = 0;
		this.rbmArray[0].setRow1(values); //set beginning values
		
		while(j < rbm) { //propagate
			this.rbmArray[j].activationPhase();
		}
		this.rbmArray[j].preTrainingStep();
	}
	
	/**
	 * One step of the backpropagation algorithm
	 * @param input
	 * @param expected
	 */
	public void backpropOneStep(boolean input[], boolean expected[]) {
		this.rbmArray[0].setRow1(input); //Set input
		for(int i = 0; i < this.rbmArray.length; i++) { //Propagate input
			this.rbmArray[i].activationPhase();
		}
		
		//Assuming expected is the same length as the last row
		for(int i = 0;i < expected.length; i++) {
			if(expected[i] != rbmArray[rbmArray.length-1].getRow2()[i].getState()) { //detect error
				rbmArray[rbmArray.length-1].getRow2()[i].setState(expected[i]); //set new state
			}
		}
		
		//Backpropogate the corrected output
		for(int i = this.rbmArray.length-1;i > -1; i--) {
			this.rbmArray[i].reconstructionPhase(); //activate nodes on row1
			this.rbmArray[i].updateWeightsAndBias(); //compare current/previous states and update the weights/bias
		}
	}
	
	/**
	 * Compares all top50 words against the words in a document
	 * Set a boolean matrix accordingly
	 * @param cat
	 * @param doc
	 * @return
	 */
	public boolean[] scanDocument(int cat, int doc) {
		List<String> document = loader.getDocument(cat,doc);
		boolean values[] = new boolean [top50.length*50]; //hard coded for simplicity, extremely unlikely to change
		//1d array for simplicity
		for(int i = 0; i < top50.length; i++) {
			for(int j = 0; j < top50[i].length; j++) {
				for(int g = 0; g < document.size();g++) {
					if(top50[i][j].equals(document.get(g))) {
						values[j + (i * 50)] = true;
						break; //Stop looking for that word
					}
				}
			}
		}
		return values;
	}
	
	/**
	 * Getter for the array of RBMs
	 * @return
	 */
	public RBM[] getRBMArray() { return this.rbmArray; }
	
	/**
	 * Getter for the top 50 data structure
	 * @return
	 */
	public String[][] getTop50() { return this.top50; }
	
	/**
	 * Load the top50 words
	 */
	private void loadTop50() {
		this.top50 = this.loader.loadTop50();
	}
	
	/**
	 * Set start values directly, alternative to scanning a document for the values
	 * @param values
	 */
	public void setBeginning(boolean[] values) {
		this.rbmArray[0].setRow1(values);
	}
}