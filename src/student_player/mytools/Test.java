package student_player.mytools;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
		Node root = new Node(1,0,null,null);
		ArrayList<Node> select = new ArrayList<Node>();
		select.add(root);
		Node child1 = new Node(1,0,null,null);
		select.get(0).addChild(child1);
		Node child2 = new Node(1,1,null,null);
		select.get(0).addChild(child2);
		Node child3 = new Node(2,1,null,null);
		select.get(0).addChild(child3);
		Node child4 = new Node(2,2,null,null);
		select.get(0).addChild(child4);
		select.clear();
		
		MyTools tools = new MyTools();
		ArrayList<Node> list = root.getChildren();
		list = tools.sortbyUCT(list);
		for(int i=0; i<list.size(); i++){
			System.out.println(list.get(i).getA()+"/"+list.get(i).getS()+"=="+list.get(i).UCT());
		}
		System.out.println(root.getA()+"/"+root.getS());
		
	}

}
