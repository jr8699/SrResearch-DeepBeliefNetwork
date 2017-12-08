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
			this.rbmArray[i] = new RBM(rowNum[i][0], rowNum[i][1],learningRate,false);
			if(i>0){
				this.rbmArray[i].tieToPrev(rbmArray[i-1]);
			}
		}
		
		//Tie to the next rbm
		for(int i = 0; i < rbmNum-1;i++){
			this.rbmArray[i].tieToNext(this.rbmArray[i+1]);
		}
		
		this.rbmArray[rbmArray.length-1].setLast(true);
		
		//Load top50 words
		loadTop50();
		//this.top50 = this.loader.loadTop2();
		//this.top50 = new String[5][50];
	}
	
	/**
	 * Does all steps of pretraining for all input
	 * Need to consult on how to do this (Random documents, each category at once, etc.)
	 * @param totalInput
	 */
	public void fullPreTraining(int totalInput) {
		int training = (int)(totalInput*0.9); //number to train on
		float training2 = (float)(totalInput*0.9);
		System.out.println(this.top50.length);
		int docsPerCat = (int)Math.ceil((training2/this.top50.length));
		int rbmTotal = rbmArray.length;
		
		//
		//training=270;
		//
		
		//train all rbms on all input
		//-1, do not train last layer, that is for labelled classification
		for(int i = 0; i < rbmTotal-1; i++) {
			//keep track of docs
			boolean scanned[][] = new boolean[this.top50.length][docsPerCat];
			
			//Generate random cat and doc index
			//True --> already used that document
			int totalScanned = 0;
			while(totalScanned < training) {
				int cat = (int) (Math.floor(Math.random()*top50.length));
				//int cat = (int) (Math.floor(Math.random()*2));
				//int cat = 0;
				int doc = ((int) (Math.floor(Math.random()*docsPerCat)) + 1);
				if(scanned[cat][doc-1] == false) {
					//System.out.println("CAT" + cat + " DOC " + doc);
					scanned[cat][doc-1] = true;
					preTrainingOneStep(i,scanDocument(cat,doc));
					totalScanned++;
				}
			}
		}
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
			//System.out.println("Propagate to " + rbm);
			this.rbmArray[j++].activationPhase();
		}
		//System.out.println("PreTrain RBM: " + j);
		this.rbmArray[j].preTrainingStep();
	}
	
	/**
	 * Test a document with the network
	 * @param input
	 * @param expected
	 * @return
	 */
	public boolean testOneStep(boolean input[], boolean expected[]) {
		this.rbmArray[0].setRow1(input); //Set input
		for(int i = 0; i < this.rbmArray.length; i++) { //Propagate input
			this.rbmArray[i].activationPhase();
		}
		
		for(int i = 0;i < expected.length; i++) { //check expected vs. actual output
			if(expected[i] != rbmArray[rbmArray.length-1].getRow2()[i].getState()) {
				return false; //error, return false, not correct
			}
		}
		
		return true;
	}
	
	/**
	 * Test the network with all test documents
	 * @param totalInput
	 * @param iterationLimit
	 */
	public float fullTest(int totalInput){
		
		//Hardcode expected values of each category
		boolean cat0[] = new boolean[5];
		cat0[0] = true;
		boolean cat1[] = new boolean[5];
		cat1[1] = true;
		boolean cat2[] = new boolean[5];
		cat2[2] = true;
		boolean cat3[] = new boolean[5];
		cat3[3] = true;
		boolean cat4[] = new boolean[5];
		cat4[4] = true;
		
		int success = 0;
		
		int right[]=new int[5];
		int tot[]=new int[5];
		
		//Walk through all test documents
		//for(int i = 0;i < this.top50.length; i++) {
		for(int i = 0;i < 5; i++) {
			for(int j = 0; j < (int)(totalInput * 0.1); j++) {
				//System.out.println(totalInput +  " " + ((int)(totalInput * 0.1)-j));
				switch(i) {
				case 0:
					if(testOneStep(scanDocument(i,totalInput-j),cat0)) {
						right[0]++;
						success++;
						//System.out.println("SUCCESS: CAT: " + i);
					}
					tot[0]++;
					break;
				case 1:
					if(testOneStep(scanDocument(i,totalInput-j),cat1)) {
						success++;
						right[1]++;
						//System.out.println("SUCCESS: CAT: " + i);
					}
					tot[1]++;
					break;
				case 2:
					if(testOneStep(scanDocument(i,totalInput-j),cat2)) {
						success++;
						right[2]++;
						//System.out.println("SUCCESS: CAT: " + i);
					}
					tot[2]++;
					break;
				case 3:
					if(testOneStep(scanDocument(i,totalInput-j),cat3)) {
						success++;
						right[3]++;
						//System.out.println("SUCCESS: CAT: " + i);
					}
					tot[3]++;
					break;
				case 4:
					if(testOneStep(scanDocument(i,totalInput-j),cat4)) {
						success++;
						right[4]++;
						//System.out.println("SUCCESS: CAT: " + i);
					}
					tot[4]++;
					break;
				}
			}
		}
		//Output the results at the end of experiments
		System.out.println("Final Results: " + ((float)success/((float)totalInput*0.1*5))*100 + "% " + success + " " + totalInput*0.1*5);
		System.out.println();
		return (float)((float)success/((float)totalInput*0.1*5))*100;
		
		
		//System.out.println("Right:");
		//for(int i : right) {
		//	System.out.println(i);
		//}
		//System.out.println("TOT:");
		//for(int i : tot) {
		//	System.out.println(i);
		//}
		
		
	}
	
	/**
	 * Does backpropagation on all test input
	 * TO DO
	 * @param totalInput
	 * @param iterationLimit
	 */
	public boolean fullBackPropagation(int totalInput){
		//System.out.println("START BACKPROPAGATION");
		int training = (int)(totalInput*0.9); //number to train on
		float training2 = (float)(totalInput*0.9);
		int docsPerCat = (int)Math.ceil((training2/this.top50.length));
		int rbmTotal = rbmArray.length;
		
		//Hardcode expected values of each category
		boolean cat0[] = new boolean[5];
		cat0[0] = true;
		boolean cat1[] = new boolean[5];
		cat1[1] = true;
		boolean cat2[] = new boolean[5];
		cat2[2] = true;
		boolean cat3[] = new boolean[5];
		cat3[3] = true;
		boolean cat4[] = new boolean[5];
		cat4[4] = true;

		
		
		int success = 0;
		
		//
		//training=270;
		//
		
		int right[]=new int[5];
		int tot[]= new int[5];
		
		//keep track of docs
		boolean scanned[][] = new boolean[this.top50.length][docsPerCat];
		//Generate random cat and doc index
		//True --> already used that document
		int totalScanned = 0;
		while(totalScanned < training) {
			int cat = (int) (Math.floor(Math.random()*top50.length));
			//int cat = (int) (Math.floor(Math.random()*2));
			//int cat = 0;
			int doc = ((int) (Math.floor(Math.random()*docsPerCat)) + 1);
			//System.out.println("CAT: " + cat + " DOC: " + doc);
			if(scanned[cat][doc-1] == false) {
				scanned[cat][doc-1] = true;
				switch(cat) {
				case 0:
					if(backpropOneStep(scanDocument(cat,doc),cat0)) {
						success++;
						right[0]++;
					}
					tot[0]++;
					break;
				case 1:
					if(backpropOneStep(scanDocument(cat,doc),cat1)) {
						success++;
						right[1]++;
					}
					tot[1]++;
					break;
				case 2:
					if(backpropOneStep(scanDocument(cat,doc),cat2)) {
						success++;
						right[2]++;
					}
					tot[2]++;
					break;
				case 3:
					if(backpropOneStep(scanDocument(cat,doc),cat3)) {
						success++;
						right[3]++;
					}
					tot[3]++;
					break;
				case 4:
					if(backpropOneStep(scanDocument(cat,doc),cat4)) {
						success++;
						right[4]++;
					}
					tot[4]++;
					break;
				}
				totalScanned++;
			}
		}
		
		
		/*
		//keep track of docs
		boolean scanned[][] = new boolean[this.top50.length][docsPerCat];
		//Generate random cat and doc index
		//True --> already used that document
		int totalScanned = 0;
		while(totalScanned < training) {
			//int cat = (int) (Math.floor(Math.random()*top50.length));
			int cat = (int) (Math.floor(Math.random()*3));
			//int cat = 0;
			int doc = ((int) (Math.floor(Math.random()*docsPerCat)) + 1);
			//System.out.println("CAT: " + cat + " DOC: " + doc);
			if(scanned[cat][doc-1] == false) {
				scanned[cat][doc-1] = true;
				switch(cat) {
				case 0:
					if(backpropOneStep(scanDocument(cat,doc),cat0)) {
						success++;
						right[0]++;
					}
					tot[0]++;
					break;
				case 1:
					if(backpropOneStep(scanDocument(cat,doc),cat1)) {
						success++;
						right[1]++;
					}
					tot[1]++;
					break;
				case 2:
					if(backpropOneStep(scanDocument(cat,doc),cat2)) {
						success++;
						right[2]++;
					}
					tot[2]++;
					break;
			}
				totalScanned++;
			}
		}
		*/
		
		System.out.println("TRAINING " + success + " " + totalScanned + " " + (float)success/(float)totalScanned * 100 + "%");
		System.out.println("Right:");
		for(int i : right) {
			System.out.println(i);
		}
		//System.out.println("TOTAL:");
		//for(int i : tot) {
		//	System.out.println(i);
		//}
		
		//if (((((float)success/(float)totalScanned) * 100) < 19.5 && (((float)success/(float)totalScanned) * 100) > 20.5)) {
		//	return true;
		//}else
		//	return false;
		
		if((float)success/(float)totalScanned != 0.8) {
			return false;
		}else
			return true;
		
	}
	
	/**
	 * One step of the backpropagation algorithm
	 * @param input
	 * @param expected
	 */
	public boolean backpropOneStep(boolean input[], boolean expected[]) {
		this.rbmArray[0].setRow1(input); //Set input
		for(int i = 0; i < this.rbmArray.length; i++) { //Propagate input
			this.rbmArray[i].activationPhase();
		}
		
		//Debug
		//May remove
		/*
		System.out.println("OUTPUT");
		System.out.println("Actual: " + rbmArray[rbmArray.length-1].getRow2()[0].getState() + " | Expected: " + expected[0]);
		System.out.println("Actual: " + rbmArray[rbmArray.length-1].getRow2()[1].getState() + " | Expected: " + expected[1]);
		*/
		//if no errors, dont backpropagate
		int error = 0;
		for(int i = 0;i < expected.length; i++) {
			if(expected[i] != rbmArray[rbmArray.length-1].getRow2()[i].getState()) { //Check for error
				error++;
			}
			rbmArray[rbmArray.length-1].getRow2()[i].setState(expected[i]); //set new state
		}
		
		//Backpropagate error
		if(error > 0) {
			//System.out.println("BRACKPROPAGATING....");
			//Backpropogate the corrected output
			for(int i = this.rbmArray.length-1;i > -1; i--) {
				if(i!=0)
					this.rbmArray[i].reconstructionPhase(); //activate nodes on row1
				else
					this.rbmArray[0].setRow1(input);
				this.rbmArray[i].updateWeightsAndBias(); //compare current/previous states and update the weights/bias
				//visualizeNetwork();
			}
			return false;
		}else {
			return true;
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
		//System.out.println("word: " + document.get(0));
		boolean values[] = new boolean [top50.length*top50[cat].length]; //hard coded for simplicity, extremely unlikely to change
		//1d array for simplicity
		for(int i = 0; i < top50.length; i++) {
			for(int j = 0; j < top50[i].length; j++) {
				for(int g = 0; g < document.size();g++) {
					if(top50[i][j].equals(document.get(g))) {
						values[j + (i * top50[cat].length)] = true;
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
	
	public void visualizeNetwork() {
		String [] network = new String[500];
		for(int i = 0; i < this.rbmArray.length; i++) {
			Node[] row = this.rbmArray[i].getRow1();
			for(int j = 0; j < row.length; j++) {
				network[j] = network[j] + " " + row[j].getState() + " | ";
			}
			
		}
		Node[] row = this.rbmArray[rbmArray.length-1].getRow2();
		for(int j = 0; j < row.length; j++) {
			network[j] = network[j] + " " + row[j].getState() + " | ";
		}

		for(int i = 0; i < network.length; i++) {
			System.out.println(network[i]);
		}
		System.out.println(" ");
	}
}