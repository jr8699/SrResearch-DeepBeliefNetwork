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
	public void testBigDBN() {
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
		
		DBN dbn = new DBN(5,arr,0.01f,"C:\\Users\\Justin\\Documents\\bbc\\top50","C:\\Users\\Justin\\Documents\\bbc",5,docs,names);
		
		//Test scanning
		assert(dbn.scanDocument(0, 3)[1] == true); //Yukos
		assert(dbn.scanDocument(0, 3)[183] == true); //back
		assert(dbn.scanDocument(0, 3)[144] == false);
		
		/**
		System.out.println("DBN PRETRAINING TEST");
		dbn.fullPreTraining(75, 75);

		System.out.println("DBN TRAINING TEST");
		dbn.fullBackPropagation(75, 75);
		dbn.fullBackPropagation(75, 75);
		dbn.fullBackPropagation(75, 75);
		dbn.fullBackPropagation(75, 75);
		dbn.fullBackPropagation(75, 75);
		dbn.fullBackPropagation(75, 75);
		dbn.fullBackPropagation(75, 75);
		dbn.fullBackPropagation(75, 75);

		dbn.fullTest(75, 75);
		*/
		
		/**
		System.out.println("DBN PRETRAINING TEST");
		dbn.fullPreTraining(150, 150);

		System.out.println("DBN TRAINING TEST");
		dbn.fullBackPropagation(150, 150);
		dbn.fullBackPropagation(150, 150);
		dbn.fullBackPropagation(150, 150);
		dbn.fullBackPropagation(150, 150);
		dbn.fullBackPropagation(150, 150);
		dbn.fullBackPropagation(150, 150);
		dbn.fullBackPropagation(150, 150);
		dbn.fullBackPropagation(150, 150);

		dbn.fullTest(150, 150);
		*/
		
		///**
		System.out.println("DBN PRETRAINING TEST");
		dbn.fullPreTraining(1750);

		System.out.println("DBN TRAINING TEST");
		dbn.fullBackPropagation(1750);

		System.out.println("DONE BACKPROP");
		
		/*
		dbn.fullBackPropagation(300, 300);
		dbn.fullBackPropagation(300, 300);
		dbn.fullBackPropagation(300, 300);
		dbn.fullBackPropagation(300, 300);
		dbn.fullBackPropagation(300, 300);
		dbn.fullBackPropagation(300, 300);
		dbn.fullBackPropagation(300, 300);
		dbn.fullBackPropagation(300, 300);
		*/

		dbn.fullTest(350);
		//*/
		
	}

}
