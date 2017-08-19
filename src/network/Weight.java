package network;

/**
 * Some weight in a RBM
 * Created to ease the storage of weights
 * @author Justin
 *
 */
public class Weight {
	/**
	 * Weight of the connection
	 */
	private float weight;
	
	/**
	 * Node on the left and right
	 */
	private Node leftNode;
	private Node rightNode;
	
	/**
	 * Constructor
	 * @param weight
	 * @param left
	 * @param right
	 */
	public Weight(float weight, Node left, Node right) {
		this.weight = weight;
		this.leftNode = left;
		this.rightNode = right;
	}
	
	/**
	 * Getter and Setter for this.weight
	 * @param w
	 */
	public void setWeight(float w) { this.weight = w; }
	public float getWeight() { return this.weight; }
	
	/**
	 * Getters for nodes
	 * @return
	 */
	public Node getRight() { return rightNode; }
	public Node getLeft() { return leftNode; }
}
