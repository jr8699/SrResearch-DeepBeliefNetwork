package main;

import network.*;

/**
 * Contains the main method to setup the DBN and conduct experiments with
 * @author Justin
 *
 */
public class NetworkManager {

	public static void main(String[] args) {
		int arr[][] = new int[3][2];
		arr[0][0] = 100;
		arr[0][1] = 100;
		arr[1][0] = 100;
		arr[1][1] = 100;
		arr[2][0] = 100;
		arr[2][1] = 5; //softmax row
		
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
		
		DBN dbn = new DBN(3,arr,0.5f,"C:\\Users\\Justin\\Documents\\bbc\\top50","",1,docs,names);

		/**
		 * business			-	0
		 * entertainment	-	1
		 * politics			-	2
		 * sport			-	3
		 * tech				-	4
		 */
	}

}
