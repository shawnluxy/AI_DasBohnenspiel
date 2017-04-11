package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import bohnenspiel.BohnenspielMove.MoveType;
import student_player.mytools.MyTools;
import student_player.mytools.Node;

/** A Hus player submitted by a student. */
public class StudentPlayer extends BohnenspielPlayer {
	Random rand = new Random();
	MyTools tools = new MyTools();
	private Node current;
    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260567881"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class bohnenspiel.RandomPlayer
     * for another example agent. */
    public BohnenspielMove chooseMove(BohnenspielBoardState board_state)
    {
    	if(board_state.getTurnNumber()==0 || current==null){
    		Node root = new Node(1,0,null,board_state);
    		tools.MCTS_expansion(root, 1, player_id, opponent_id);
    		current = root;
    	}else{
    		// find opponent move in the child nodes of current state
    		boolean found = false;
    		ArrayList<Node> opponent = current.getChildren();
    		for(int i=0; i<opponent.size(); i++){
				if(Arrays.deepEquals(opponent.get(i).getState().getPits(), board_state.getPits())){
    				current = opponent.get(i);
    				found = true;
    				break;
    			}
    		}
    		if(!found){
				Node root = new Node(1,0,null,board_state);
	    		tools.MCTS_expansion(root, 1, player_id, opponent_id);
	    		current = root;
    		}
    	}
    	Node result = MCTSS(current);
    	current = result;
    	if(result==null){
//    		System.out.println("Null");
    		BohnenspielMove nothing = new BohnenspielMove();
    		return nothing;
    	}
    	BohnenspielMove bestmove = result.getMove();
    	return bestmove;
    	
//		BohnenspielMove bestmove = this.MonteCarlo(board_state);
//		return bestmove;
    }
    
    public Node MCTSS(Node current) {
    	long startTime = System.currentTimeMillis();
    	ArrayList<Node> allchild = current.getChildren();
    	if(allchild.size()==0){
    		ArrayList<BohnenspielMove> moves = current.getState().getLegalMoves();
    		if(moves.size()==0){
    			return null;}
    	}
		Node bestone = allchild.get(0);
		int times=20;
		ArrayList<Node> select = new ArrayList<Node>();
		select.add(current);
		int c=0;
		long endTime = System.currentTimeMillis();
    	while((endTime-startTime)<650){
    		c++;
    		// Selection
    		ArrayList<Node> candidate = new ArrayList<Node>();
    		for(int i=0; i<current.getChildren().size(); i++){
    			candidate.add(current.getChildren().get(i));
    		}
    		while(candidate.size()>0) {
    			select.clear();
    			double maxUCT = 0;
    			for(int i=0; i<candidate.size(); i++){
    				if(candidate.get(i).UCT()>maxUCT){
    					maxUCT = candidate.get(i).UCT();}
    			}
    			for(int i=0; i<candidate.size(); i++){
    				if(candidate.get(i).UCT()==maxUCT){
    					select.add(candidate.get(i));}
    			}
    			
    			candidate.clear();
    			for(int i=0; i<select.size(); i++){
    				ArrayList<Node> children = select.get(i).getChildren();
    				for(int j=0; j<children.size(); j++){
    					candidate.add(children.get(j));
    				}
    			}
    		}
    		// Expansion & Simulation
    		for(int i=0; i<select.size(); i++) {
    			tools.MCTS_expansion(select.get(i), times, player_id, opponent_id);
    		}
    		
    		if(c>=tools.MAX_ITERATION){break;}
    		endTime = System.currentTimeMillis();
    	}
    	// choose
    	allchild = new ArrayList<Node>();
    	for(int i=0; i<current.getChildren().size(); i++){
    		allchild.add(current.getChildren().get(i));
		}
    	// avoid to use SKIP before turn 5
    	if(current.getState().getTurnNumber()<5 &&allchild.size()>1 && allchild.get(allchild.size()-1).getMove().getMoveType().equals(MoveType.SKIP)){
    		allchild.remove(allchild.size()-1);
		}
    	double max_ratio = 0;
    	for(int i=0; i<allchild.size(); i++){
    		double ratio = (double)allchild.get(i).getA()/allchild.get(i).getS();
    		if(ratio>max_ratio){
    			max_ratio = ratio;
    			bestone = allchild.get(i);
    		}
//    		System.out.print(i+":"+allchild.get(i).getA()+"/"+allchild.get(i).getS()+" - ");
    	}
    	
    	return bestone;
    }
    
    public BohnenspielMove MonteCarlo(BohnenspielBoardState board_state) {
    	long startTime = System.currentTimeMillis();
    	MyTools tools = new MyTools();
    	long endTime = System.currentTimeMillis();
    	ArrayList<BohnenspielMove> moves = board_state.getLegalMoves();
    	if(moves.size()==0){
    		BohnenspielMove nothing = new BohnenspielMove();
    		return nothing;
    	}
    	if(board_state.getTurnNumber()<5 && moves.size()>1 && moves.get(moves.size()-1).getMoveType()==MoveType.SKIP){
				moves.remove(moves.size()-1);
		}
    	int[] sim = new int[moves.size()];
        BohnenspielMove bestmove = moves.get(0);
        int c=0;
    	while((endTime-startTime)<680){
    		c++;
    		for(int i=0; i<moves.size(); i++) {
            	BohnenspielBoardState cloned_state = (BohnenspielBoardState) board_state.clone();
            	BohnenspielMove move = moves.get(i);
                cloned_state.move(move);
            	int simulation = tools.defaultSimulation(cloned_state, player_id, opponent_id);
            	sim[i] += simulation;
            }
    		endTime = System.currentTimeMillis();
    	}
    	
    	int max = 0, max_index = 0;
    	for(int i=0; i<sim.length; i++) {
    		if(sim[i]>max){
    			max = sim[i];
    			max_index = i;
    		}
    	}
    	bestmove = moves.get(max_index);
    	System.out.println("Times:"+c+"- sim:"+Arrays.toString(sim));
    	return bestmove;
    }
    
    public BohnenspielMove MMGreedy(BohnenspielBoardState board_state){
    	int myscore;
        int mybestscore=-72;
        ArrayList<BohnenspielMove> my_moves = board_state.getLegalMoves();
        BohnenspielMove my_move = my_moves.get(0);
        for(int i=0; i<my_moves.size(); i++) {
        	BohnenspielBoardState cloned_board_state = (BohnenspielBoardState) board_state.clone();
        	BohnenspielMove move1 = my_moves.get(i);
            cloned_board_state.move(move1);
            myscore = cloned_board_state.getScore(player_id);
            int opscore;
            int opbestscore=0;
            ArrayList<BohnenspielMove> op_moves = cloned_board_state.getLegalMoves();
            for(int j=0; j<op_moves.size(); j++) {
            	BohnenspielBoardState cloned2 = (BohnenspielBoardState) cloned_board_state.clone();
            	BohnenspielMove move2 = op_moves.get(j);
                cloned2.move(move2);
                opscore=cloned2.getScore(opponent_id)-cloned_board_state.getScore(opponent_id);
    	        if (opscore>opbestscore)
    	        {
    		        opbestscore=opscore;
    	        }
            }
            myscore -= opbestscore;
            if(myscore>mybestscore) {
            	my_move = move1;
            	mybestscore = myscore;
            }
        }
        return my_move;
    }
    
}