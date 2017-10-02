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
	 * Do one step of C.D. w/ category, doc, and rbms to train
	 * @param cat
	 * @param doc
	 */
	public void preTrainingOneStep(int rbm, boolean values[]){
		int j = 0;
		this.rbmArray[0].setRow1(values); //set beginning values
		
		while(j < rbm) {
			if(j > 0) { //propagate input to current rbm
				int g = 0;
				while(g < j) { // lock, propagate, unlock
					this.rbmArray[g].toggleLock(); //should be false before toggle
					this.rbmArray[g].preTrainingStep();
					this.rbmArray[g].toggleLock();
					g++;
				}
			}
			this.rbmArray[j].preTrainingStep();
			j++;
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