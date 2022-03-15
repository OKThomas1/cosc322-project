package ubc.cosc322;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class miniMaxAI extends AI {
	
	public final int MAX = 1000;
	public final int MIN = -1000;

	public miniMaxAI(ArrayList<Integer> startBoard, int id) {
		super(startBoard, id);
	}
	
	public Map<String, Object> calculateNextMove(){ // use this to actually make the moves
		HashMap<String, Object> move = new HashMap<String, Object>();
		
		return move;
	}
		
	
	// a minimax algorithm with alpha-beta pruning. use whenever you need to find the best possible move based on heuristic values
	public int calculateMiniMax(int moveIndex, int depth, Boolean ourPlayer, ArrayList<Integer> possibleMoveWeights, int alpha, int beta) {
		
		if (depth == 3) 											//maximum depth for the search, can be modified, deeper = better but slower
			return possibleMoveWeights.get(moveIndex); 				//returns the index of the weight of the best possible move, use this index later to find the move you need to make
		if (ourPlayer) { 											//this is the maximizing case, we want out player to get the maximum 
			int bestVal = MIN;
			
			for (int i = 0; i < 2; i++) { //splits the tree in half for searching left and right children
				int value = calculateMiniMax(moveIndex*2+i, depth + 1, false, possibleMoveWeights, alpha, beta);
				bestVal = Math.max(bestVal, value);
				alpha = Math.max(bestVal, alpha);
				
				if (alpha >= beta) 
					break;
			}
			return bestVal;
		} else { 					//trying to minimize the value of our rival's moves
			int bestVal = MAX;
			
			for (int i = 0; i < 2; i++) {
				int value = calculateMiniMax(moveIndex*2+i, depth + 1, true, possibleMoveWeights, alpha, beta);
				bestVal = Math.min(bestVal, value);
				beta = Math.min(bestVal, beta);
				
				if (alpha >= beta)
					break;
			} 
			return bestVal;
		}
		
		
	
	}

}
