package io;

public class DBNInputLoader {
	
	/**
	 * Number of categories and how many docs to scan
	 * per category. Also category names
	 */
	private int numCat;
	private int docsPerCat[];
	private String catNames[];
	
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
	public DBNInputLoader(String top50Dir, String docDir, int numCat, int docsPerCat[],
							String catNames[]){
		this.top50Dir = top50Dir;
		this.docDir = docDir;
		this.numCat = numCat;
		this.docsPerCat = docsPerCat;
		this.catNames = catNames;
	}
	
	/**
	 * Get a certain document at a certain index
	 * @param cat
	 * @param i
	 * @return
	 */
	public String getDocument(int cat, int i){
		//Error, document out of scope
		if(i >= docsPerCat[cat])
			return "";
		return "";
	}
	
	/**
	 * Load the top50 words according to the top50 directory
	 * @return
	 */
	public String[] loadTop50(){
		String tmp[] = new String[5];
		return tmp;
	}
}
