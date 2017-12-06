package test;

import static org.junit.Assert.*;

import org.junit.Test;

import io.DBNInputLoader;
import network.DBN;
import network.RBM;

public class DBNTest {

	@Test
	public void testLinking() {
		int arr[][] = new int[3][2];
		arr[0][0] = 3;
		arr[0][1] = 3;
		arr[1][0] = 3;
		arr[1][1] = 3;
		arr[2][0] = 3;
		arr[2][1] = 3;
		DBN dbn = new DBN(3,arr,0.5f,"","",0,null,null);
		
		RBM[] rbms = dbn.getRBMArray();
		
		//rbm0 to rbm1
		assert(rbms[0].getRow2()[0] == rbms[1].getRow1()[0]);
		assert(rbms[0].getRow2()[1] == rbms[1].getRow1()[1]);
		assert(rbms[0].getRow2()[2] == rbms[1].getRow1()[2]);
		
		assertFalse(rbms[0].getRow2()[2] == rbms[1].getRow1()[0]);
		
		//rbm1 to rbm2
		assert(rbms[1].getRow2()[0] == rbms[2].getRow1()[0]);
		assert(rbms[1].getRow2()[1] == rbms[2].getRow1()[1]);
		assert(rbms[1].getRow2()[2] == rbms[2].getRow1()[2]);
		
		assertFalse(rbms[1].getRow2()[2] == rbms[2].getRow1()[0]);
		
		//Test for loop around
		assertFalse(rbms[2].getRow2()[2] == rbms[0].getRow1()[2]);
	}
	
	@Test
	public void testMultiRBM() {
		int arr[][] = new int[3][2];
		arr[0][0] = 3;
		arr[0][1] = 3;
		arr[1][0] = 3;
		arr[1][1] = 3;
		arr[2][0] = 3;
		arr[2][1] = 3;
		
		DBN dbn = new DBN(3,arr,0.5f,"","",0,null,null);
		RBM[] rbms = dbn.getRBMArray();
		
		//input
		boolean values[] = new boolean[3];
		values[0] = true;
		values[1] = false;
		values[2] = false;
		
		//Check that rows are correct when doing many rbms at the same time
		dbn.preTrainingOneStep(2, values); //do three rbms w/ values (index 2)
		
		assert(rbms[1].getRow1()[0] == rbms[0].getRow2()[0]);
		assert(rbms[1].getRow1()[1] == rbms[0].getRow2()[1]);
		assert(rbms[1].getRow1()[2] == rbms[0].getRow2()[2]);
		
		dbn.preTrainingOneStep(2, values); //do three rbms w/ values
		
		assert(rbms[2].getRow1()[0] == rbms[1].getRow2()[0]);
		assert(rbms[2].getRow1()[1] == rbms[1].getRow2()[1]);
		assert(rbms[2].getRow1()[2] == rbms[1].getRow2()[2]);
		
		//loader.getDocument(cat, doc);
		//boolean values[] = scanDocument(cat,doc);
	}
	
	@Test
	public void testLoader() {
		int docs[] = new int[5];
		docs[0] = 100;
		docs[1] = 100;
		docs[2] = 100;
		docs[3] = 100;
		docs[4] = 100;
		
		DBNInputLoader loader = new DBNInputLoader("C:\\Users\\Justin\\Documents\\bbc\\top50","C:\\Users\\Justin\\Documents\\bbc",5,docs);
		
		assert(loader.getDocument(0, 3).get(14).equals("are"));
		assert(loader.getDocument(0, 3).get(0).equals("Yukos"));
	}
	
	@Test
	public void testSmallDBN() {
		int arr[][] = new int [2][2];
		arr[0][0] = 3;
		arr[0][1] = 3;
		arr[1][0] = 3;
		arr[1][1] = 3; //softmax row
		
		//not used
		int docs[] = new int[5];
		docs[0] = 100;
		docs[1] = 100;
		docs[2] = 100;
		docs[3] = 100;
		docs[4] = 100;
	
		String names[] = new String[5];
		names[0] = "business";
		names[1] = "entertainment";
		names[2] = "politics";
		names[3] = "sport";
		names[4] = "tech";
		//
		for(int g = 0; g < 1; g++){
		DBN dbn = new DBN(2,arr,0.1f,"small/top50","small",3,docs,names);

		//experiments
		System.out.println("DBN PRETRAINING TEST");
		//dbn.fullPreTraining(4);

		System.out.println("DBN TRAINING TEST");
		for(int j = 0; j < 1000; j++)
			dbn.fullBackPropagation(4);



		System.out.println("DONE BACKPROP");

		dbn.fullTest(2);
		}
	}
	
	@Test
	public void testBigDBN() {
		float results[] = new float[30];
		for(int i =0; i < 30; i++) {
			int arr[][] = new int[3][2];
			arr[0][0] = 250;
			arr[0][1] = 100;
			arr[1][0] = 100;
			arr[1][1] = 100;
			arr[2][0] = 100;
			arr[2][1] = 2; //softmax row
		
			//not used
			int docs[] = new int[5];
			docs[0] = 100;
			docs[1] = 100;
			docs[2] = 100;
			docs[3] = 100;
			docs[4] = 100;
		
			String names[] = new String[5];
			names[0] = "business";
			names[1] = "entertainment";
			names[2] = "politics";
			names[3] = "sport";
			names[4] = "tech";
		
			//DBN dbn = new DBN(3,arr,0.1f,"C:\\Users\\Justin\\Documents\\bbc\\test\\top50","C:\\Users\\Justin\\Documents\\bbc\\test",5,docs,names);
			DBN dbn = new DBN(3,arr,0.1f,"small/top50","small",2,docs,names);
			//Test scanning
			//assert(dbn.scanDocument(0, 3)[1] == true); //Yukos
			//assert(dbn.scanDocument(0, 3)[183] == true); //back
			//assert(dbn.scanDocument(0, 3)[144] == false);

			
			//experiments
			System.out.println("DBN PRETRAINING TEST");
			dbn.fullPreTraining(4);

			System.out.println("DBN TRAINING TEST");
			dbn.fullBackPropagation(4);



			System.out.println("DONE BACKPROP");

			results[i] = dbn.fullTest(2);
		}
		System.out.println("RESULTS");
		for(float f : results) {
			System.out.println(f);
		}
		
	}
	
	@Test
	public void whyMyExperimentsFailed() {
		
		int arr[][] = new int[5][2];
		arr[0][0] = 250;
		arr[0][1] = 50;
		arr[1][0] = 50;
		arr[1][1] = 50;
		arr[2][0] = 50;
		arr[2][1] = 50;
		arr[3][0] = 50;
		arr[3][1] = 50;
		arr[4][0] = 50;
		arr[4][1] = 5; //softmax row
		
		int docs[] = new int[5];
		docs[0] = 100;
		docs[1] = 100;
		docs[2] = 100;
		docs[3] = 100;
		docs[4] = 100;
		
		String names[] = new String[5];
		names[0] = "business";
		names[1] = "entertainment";
		names[2] = "politics";
		names[3] = "sport";
		names[4] = "tech";
		
		
		//DBN dbn = new DBN(5,arr,0.01f,"C:\\Users\\Justin\\Documents\\bbc\\test\\top50","C:\\Users\\Justin\\Documents\\bbc\\test",5,docs,names);
		DBN dbn = new DBN(5,arr,0.01f,"C:\\Users\\Justin\\Documents\\bbc\\top50","C:\\Users\\Justin\\Documents\\bbc",5,docs,names);
		
		//Statistics
		
		int sum[] = new int[5];
		int docVal[][][] = new int[5][300][5];
		int docVal2[][] = new int[5][300];
		for(int i = 0; i < 5; i++) {
			for(int j = 1; j < 10; j++) {
				boolean[] values = dbn.scanDocument(i, j);
					
				for(int g = 0; g < values.length; g++) {
					if(values[g]) {
						docVal2[i][j]++;
					if(g < 50) {
						docVal[i][j][0]++;

					}
					if(g >= 50 && g < 100) {
						docVal[i][j][1]++;

					}
					if(g >= 100 && g < 150) {
						docVal[i][j][2]++;

					}
					if(g >= 150 && g < 200) {
						docVal[i][j][3]++;

					}
					if(g >= 200 && g < 250) {
						docVal[i][j][4]++;

					}
					}
				}
			}
		}
		
		assert(true);
		/*
		for(int i = 0; i < 5; i++) {
			//System.out.println("DOCUMENT " + i);
			for(int j = 0; j < 300; j++) {
				System.out.println(docVal2[i][j]);
			}
		}
		*/
		double docAvg[][] = new double[5][5];
		
		for(int i = 0; i < 5; i++) {
			System.out.println("Category " + i);
			for(int j = 1; j < 10; j++) {
				docAvg[i][0] += docVal[i][j][0];
				docAvg[i][1] += docVal[i][j][1];
				docAvg[i][2] += docVal[i][j][2];
				docAvg[i][3] += docVal[i][j][3];
				docAvg[i][4] += docVal[i][j][4];
				System.out.println("WORDS IN CAT 0: " + j + " " + docVal[i][j][0]);
				System.out.println("WORDS IN CAT 1: " + j + " "+ docVal[i][j][1]);
				System.out.println("WORDS IN CAT 2: " + j + " "+ docVal[i][j][2]);
				System.out.println("WORDS IN CAT 3: " + j + " "+ docVal[i][j][3]);
				System.out.println("WORDS IN CAT 4: " + j + " "+ docVal[i][j][4]);
			}
		}
		
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				int sums[] = new int[5];
				for(int h = 0; h < 5; h++) {
					sums[i] += docAvg[i][h];
				}
				
				System.out.println("CAT: " + i + " CAT: " + j + " AVG: " + docAvg[i][j]/10);
			}
		}
	}

}
