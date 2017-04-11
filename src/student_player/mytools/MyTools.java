package student_player.mytools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielMove.MoveType;

public class MyTools {
	public final int MAX_SCORE = 72;
	public final int MAX_ITERATION = 200;
	Random rand = new Random();

	public MyTools(){

	}
    
    public int defaultSimulation(BohnenspielBoardState board_state, int my_id, int op_id) {
    	int c=0;
    	int my_score=0;
    	int op_score=0;
    	BohnenspielBoardState state = (BohnenspielBoardState) board_state.clone();
    	while(1>0) {
    		c++;
    		BohnenspielBoardState state0 = this.randomState(state);
    		my_score=state0.getScore(my_id);
            op_score=state0.getScore(op_id);
            if(op_score>MAX_SCORE/2){
            	return 0;
            } else if(my_score>MAX_SCORE/2){
            	return 1;
            }
        	state=(BohnenspielBoardState)state0.clone();
        	if(c>=MAX_ITERATION){break;}
    	}
    	if(my_score>op_score){
    		return 1;
    	}else {
    		return 0;
    	}
    }
    
    public BohnenspielBoardState randomState(BohnenspielBoardState state) {
    	ArrayList<BohnenspielMove> moves = state.getLegalMoves();
    	if(moves.size()>0){
//    		if(state.getTurnNumber()<5 && moves.size()>1 && moves.get(moves.size()-1).getMoveType()==MoveType.SKIP){
//    				moves.remove(moves.size()-1);
//    		}
    		BohnenspielBoardState clone_state = (BohnenspielBoardState) state.clone();
    		BohnenspielMove move = moves.get(rand.nextInt(moves.size()));
    		clone_state.move(move);
    		return clone_state;
    	} else{
    		return state;
    	}
    }
    
    
    public BohnenspielMove greedyMove(BohnenspielBoardState state, int player_id) {
    	int score;
    	int bestscore=-1;
    	ArrayList<BohnenspielMove> moves = state.getLegalMoves();
    	BohnenspielMove move = moves.get(0);
    	for(int i=0; i<moves.size(); i++) {
        	BohnenspielBoardState cloned_state = (BohnenspielBoardState) state.clone();
        	BohnenspielMove move0 = moves.get(i);
            cloned_state.move(move0);
            score = cloned_state.getScore(player_id);
            if(score>bestscore) {
            	move = move0;
            	bestscore = score;
            }
    	}
    	return move;
    }
    
    public ArrayList<Node> sortbyUCT(ArrayList<Node> list) {
    	Collections.sort(list, new Comparator<Node>(){
    		public int compare(Node a, Node b){
    			if(a.UCT()>b.UCT()){
    				return -1;
    			}else if(a.UCT()<b.UCT()){
    				return 1;
    			}
    			return 0;
    		}
    	});
    	return list;
    }
    
    public void MCTS_expansion(Node node, int times, int player_id, int opponent_id) {
    	BohnenspielBoardState state = (BohnenspielBoardState) node.getState().clone();
		ArrayList<BohnenspielMove> moves = state.getLegalMoves();
		for(int j=0; j<moves.size(); j++) {
			int win=0;
			BohnenspielBoardState cloned_state = (BohnenspielBoardState) state.clone();
        	BohnenspielMove move = moves.get(j);
            cloned_state.move(move);
			for(int t=0; t<times; t++) {
            	int simulation = this.defaultSimulation(cloned_state, player_id, opponent_id);
            	win += simulation;
			}
			if(win>(times/2)){
				win=1;
			}else{win=0;}
			Node child = new Node(1, win, moves.get(j), cloned_state);
			node.addChild(child);
        }
    }
    
}
