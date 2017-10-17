package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DBNInputLoader {
	
	/**
	 * Number of categories and how many docs to scan
	 * per category.
	 */
	private int numCat;
	private int docsPerCat[];
	
	/**
	 * Directories for the top 50 words
	 * and documents to train on
	 * 
	 * docDir:
	 * 			Should be the root directory of all the documents
	 * 			Assuming that each category has it's own folder
	 */
	private String top50Dir;
	private String docDir;
	
	//Constructor
	public DBNInputLoader(String top50Dir, String docDir, int numCat, int docsPerCat[]){
		this.top50Dir = top50Dir;
		this.docDir = docDir;
		this.numCat = numCat;
		this.docsPerCat = docsPerCat;
	}
	
	/**
	 * Get a certain document at a certain index
	 * @param cat
	 * @param i
	 * @return
	 */
	public List<String> getDocument(int cat, int i){
		String path;
		if(i < 10){ //adjust path to account for preceding 0s
			path = docDir + "\\" + cat + "\\00" + i + ".txt";
		}else if(i < 100){
			path = docDir + "\\" + cat + "\\0" + i + ".txt";
		}else{
			path = docDir + "\\" + cat + "\\" + i + ".txt";
		}
		
		File f = new File(path);
		List<String> doc = new ArrayList<String>();
		
		try { //read
			InputStream in = new FileInputStream(f);
			Reader r = new InputStreamReader(in, Charset.forName("ASCII"));
			int c;
			String currentWord = "";
			
			//Taken from the TF-IDF tool, have to keep word processing consistent
			while((c = r.read()) != -1) {
				char character = (char) c;
				if(character >= 'A' && character <= 'Z' ||	
						character >= 'a' && character <= 'z' ||
						character == '-' //This will create empty words. Will have to ignore
						){ //Add character to the current word
					currentWord = currentWord + character;
				}else{ //Bad character detected, add current word
					if(currentWord != "") doc.add(currentWord); //handle chains of bad characters
					currentWord = "";
				}
			}
		
			in.close();
		}catch(Exception e) {
			System.out.println("Bad Document index");
			return null;
		}
		return doc;
	}
	
	/**
	 * Get all the documents in a certain category
	 * @return
	 */
	public List<List<String>> getAllDocumentInCat(int cat){
		List<List<String>> all = new ArrayList<List<String>>();

		for(int i = 0; i < docsPerCat[cat]; i++) {
			all.add(getDocument(cat,i));
		}
		return all;
	}
	
	/**
	 * Load the top50 words according to the top50 directory
	 * @return
	 */
	public String[][] loadTop50(){
		String top50[][] = new String[numCat][50];
		
		for(int j = 0; j < this.numCat; j++) {
			File f = new File(top50Dir + "\\" + j + "\\top50.txt"); //append filepath
			try { //read
				InputStream in = new FileInputStream(f);
				Reader r = new InputStreamReader(in, Charset.forName("ASCII"));
				BufferedReader buffR = new BufferedReader(r);
				String line;
				int i = 0;
			
				while((line = buffR.readLine()) != null) {
					//store
					top50[j][i++] = line;
				}
			
				in.close();
			}catch(Exception e) {
				return null;
			}
		}
		return top50;
	}
}
