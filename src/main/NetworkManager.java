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
		arr[0][0] = 3;
		arr[0][1] = 3;
		arr[1][0] = 3;
		arr[1][1] = 3;
		arr[2][0] = 3;
		arr[2][1] = 3;
		DBN dbn = new DBN(3,arr,0.5f,"C:\\Users\\Justin\\Documents\\bbc\\top50","",1,null,null);

	}

}
