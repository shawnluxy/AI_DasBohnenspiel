package student_player;

import java.util.ArrayList;
import java.util.Arrays;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import student_player.mytools.MyTools;
import student_player.mytools.Node;

/** A Hus player submitted by a student. */
public class MCPlayer extends BohnenspielPlayer {

    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public MCPlayer() { super("mcmcmcmcm"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class bohnenspiel.RandomPlayer
     * for another example agent. */
    public BohnenspielMove chooseMove(BohnenspielBoardState board_state)
    {
//        BohnenspielMove greedy_move = this.Greedy(board_state);
//        return greedy_move;
        
        BohnenspielMove bestmove = this.MonteCarlo(board_state);
        
        return bestmove;
    }
    
    public BohnenspielMove MCTS(BohnenspielBoardState board_state) {
    	long startTime = System.currentTimeMillis();
    	MyTools tools = new MyTools();
    	Node root = new Node(0, 0, null, board_state);
    	ArrayList<BohnenspielMove> allmoves = board_state.getLegalMoves();
    	if(allmoves.size()==0){
    		BohnenspielMove nothing = new BohnenspielMove();
    		return nothing;
    	}
		BohnenspielMove bestmove = allmoves.get(0);
		int times=50;
		ArrayList<Node> select = new ArrayList<Node>();
		select.add(root);
		int c=0;
		long endTime = System.currentTimeMillis();
    	while((endTime-startTime)<680){
    		c++;
    		for(int i=0; i<select.size(); i++) {
    			BohnenspielBoardState state = (BohnenspielBoardState) select.get(i).getState().clone();
    			ArrayList<BohnenspielMove> moves = state.getLegalMoves();
    			for(int j=0; j<moves.size(); j++) {
    				int win=0;
    				BohnenspielBoardState cloned_state = (BohnenspielBoardState) state.clone();
                	BohnenspielMove move = moves.get(j);
                    cloned_state.move(move);
    				for(int t=0; t<times; t++) {
    	            	int simulation = tools.defaultSimulation(cloned_state, player_id, opponent_id);
    	            	win += simulation;
    				}
    				if(win>(times/2)){
    					win=1;
    				}else{win=0;}
    				Node child = new Node(1, win, moves.get(j), cloned_state);
    				select.get(i).addChild(child);
                }
    		}
    		
    		ArrayList<Node> candidate = new ArrayList<Node>();
    		for(int i=1; i<root.getChildren().size(); i++){
    			candidate.add(root.getChildren().get(i));
    		}
    		while(candidate.size()>0) {
    			select.clear();
    			candidate = tools.sortbyUCT(candidate);
    			select.add(candidate.get(0));
    			double maxUCT = candidate.get(0).UCT();
    			for(int i=1; i<candidate.size(); i++){
    				if(candidate.get(i).UCT()==maxUCT){
    					select.add(candidate.get(i));
    				}else{break;}
    			}
    			
    			candidate.clear();
    			for(int i=0; i<select.size(); i++){
    				ArrayList<Node> children = select.get(i).getChildren();
    				for(int j=0; j<children.size(); j++){
    					candidate.add(children.get(j));
    				}
    			}
    		}
    		
    		if(c>=tools.MAX_ITERATION){break;}
    		endTime = System.currentTimeMillis();
    	}
    	ArrayList<Node> childmove = root.getChildren();
    	double max_ratio = 0;
    	int max_index = 0;
    	for(int i=0; i<allmoves.size(); i++){
    		double ratio = (double)childmove.get(i).getA()/childmove.get(i).getS();
    		if(ratio>max_ratio){
    			max_ratio = ratio;
    			max_index = i;
    		}
    	}
    	System.out.println("C:"+c+" Root.A/S:"+root.getA()+"/"+root.getS()+" childSize:"+childmove.size()+" moveSize:"+allmoves.size());
    	bestmove = allmoves.get(max_index);
    	return bestmove;
    }
    
    public BohnenspielMove MonteCarlo(BohnenspielBoardState board_state) {
    	System.out.println(board_state.getTurnNumber());
    	long startTime = System.currentTimeMillis();
    	MyTools tools = new MyTools();
    	long endTime = System.currentTimeMillis();
    	ArrayList<BohnenspielMove> moves = board_state.getLegalMoves();
    	if(moves.size()==0){
    		BohnenspielMove nothing = new BohnenspielMove();
    		return nothing;
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
    
    public BohnenspielMove Greedy(BohnenspielBoardState board_state){
    	int myscore;
        int mybestscore=-72;
        ArrayList<BohnenspielMove> my_moves = board_state.getLegalMoves();
        BohnenspielMove my_move = my_moves.get(0);
        for(int i=0; i<my_moves.size(); i++) {
        	BohnenspielBoardState cloned_board_state = (BohnenspielBoardState) board_state.clone();
        	BohnenspielMove move1 = my_moves.get(i);
            cloned_board_state.move(move1);
            myscore = cloned_board_state.getScore(player_id);
            // System.out.println("me before:"+myscore);
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
            // System.out.println("me after:"+myscore);
            if(myscore>mybestscore) {
            	my_move = move1;
            	mybestscore = myscore;
            }
        }
        return my_move;
    }
    
}