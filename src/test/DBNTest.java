package test;

import static org.junit.Assert.*;

import org.junit.Test;

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
		dbn.preTrainingOneStep(1, values); //do three rbms w/ values
		
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

}
