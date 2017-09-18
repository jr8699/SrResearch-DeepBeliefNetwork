package main;

import network.*;

/**
 * Contains the main method to setup the DBN and conduct experiments with
 * @author Justin
 *
 */
public class NetworkManager {

	public static void main(String[] args) {
		//DBN dbn = new DBN();
		RBM rbm = new RBM(3, 2, 0.2f);
		boolean input[] = new boolean[3];
		input[0] = true;
		input[1] = false;
		input[2] = true;
		
		
		rbm.dumpRBM();
		rbm.preTrainingStep(input);
		rbm.dumpRBM();
	}

}
