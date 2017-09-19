package test;

import static org.junit.Assert.*;

import org.junit.Test;

import network.RBM;
import network.Weight;

public class RBMTest {

	@Test
	public void testRBM() {
		RBM rbm = new RBM(2,2,0.5f);
		
		//Input
		boolean in[] = new boolean[2];
		in[0] = true;
		in[1] = true;
		
		//Get weights going to node 0, row 2
		//0 and 2 go to node0 row2
		Weight w1 = rbm.getWeights()[0];
		Weight w2 = rbm.getWeights()[2];
		
		assertEquals(0,w1.getRight().getIndex());
		assertEquals(0,w2.getRight().getIndex());
		
		//Set weights to 0.5 for easy calculations
		w1.setWeight(0.5f);
		w2.setWeight(0.5f);
		
		//Test that weights setting respond correctly
		assertEquals(0.5f, rbm.getWeights()[0].getWeight(), 0.001);
		assertEquals(0.5f, rbm.getWeights()[2].getWeight(), 0.001);
		
		//Set input
		rbm.setRow1(in);
		
		float probability = rbm.calcProbability(rbm.getWeights()[0].getWeight()+rbm.getWeights()[2].getWeight()
							,0.0f);
		
		//Make sure probability function works
		assertEquals(0.2689,probability, 0.001);
		
		int oncnt = 0;
		for(int i = 0;i < 10000; i++) {
			//Do positive phase
			rbm.activationPhase();
		
			if(rbm.getRow2()[0].getState())
				oncnt++;
		}
		
		//Test number of turn ons, should reflect the probability roughly
		assertTrue(oncnt>2500 && oncnt < 2800);
		
		//Test reconstruction now
		w1 = rbm.getWeights()[0];
		w2 = rbm.getWeights()[1];
		
		assertEquals(0,w1.getLeft().getIndex());
		assertEquals(0,w2.getLeft().getIndex());
		
		w1.setWeight(0.5f);
		w2.setWeight(0.5f);
		
		assertEquals(0.5f, rbm.getWeights()[0].getWeight(), 0.001);
		assertEquals(0.5f, rbm.getWeights()[1].getWeight(), 0.001);
		
		rbm.setRow2(in);
		
		oncnt = 0;
		for(int i = 0;i < 10000; i++) {
			//Do reconstruction phase
			rbm.reconstructionPhase();
		
			if(rbm.getRow1()[0].getState())
				oncnt++;
		}
		
		assertTrue(oncnt>2500 && oncnt < 2800);
		
		//Don't need to get Negative phase since pos/neg share the same function
	}

}
