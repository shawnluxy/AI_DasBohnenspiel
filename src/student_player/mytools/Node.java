package student_player.mytools;

import java.util.ArrayList;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;

public class Node {
	private int s;
	private int a;
	private BohnenspielMove move;
	private BohnenspielBoardState state;
	private Node parent;
	private ArrayList<Node> children;
	
	public Node(int s, int a, BohnenspielMove move, BohnenspielBoardState state){
		this.s = s;
		this.a = a;
		this.move = move;
		this.state = state;
		this.parent = null;
		this.children = new ArrayList<Node>();
	}

	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}
	
	public BohnenspielMove getMove() {
		return move;
	}

	public void setMove(BohnenspielMove move) {
		this.move = move;
	}
	
	public BohnenspielBoardState getState() {
		return state;
	}

	public void setState(BohnenspielBoardState state) {
		this.state = state;
	}
	
	public ArrayList<Node> getChildren() {
		return children;
	}
	
	public void addChild(Node child) {
		if(!children.contains(child)) {
			child.setParent(this);
			children.add(child);
			child.updateNodes(child);}
	}
	
	public void removeChild(Node child) {
		if(children.contains(child)) {
			child.setParent(null);
			children.remove(child);}
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public void removeSelf() {
		if(parent!=null) {
			parent.removeChild(this);}
	}
	
	public void updateNodes(Node node) {
		Node p = node.getParent();
		while(p!=null) {
			p.setS(p.getS()+node.getS());
			p.setA(p.getA()+node.getA());
			p = p.getParent();
		}
	}
	
	public double UCT() {
		double v = 0;
		if(s!=0 && parent!=null){
			v = ((double)a/s)+(Math.sqrt(2*Math.log(parent.getS())/s));
		}
		return v;
	}
	
}
