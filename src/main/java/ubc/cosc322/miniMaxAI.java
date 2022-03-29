package ubc.cosc322;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ubc.cosc322.AI.Move;

public class miniMaxAI{
	
	public final int MAX = 1000;
	public final int MIN = -1000;

	public ArrayList<Move> tree;

	public miniMaxAI(ArrayList<Move> t) {
		tree = t;
	}
	
	public Map<String, Object> calculateNextMove(){ // use this to actually make the moves
		Position bestPosition = new Position(MIN, null);
		System.out.println(tree.size());
		for(int i = 0; i < tree.size(); i++){
			ArrayList<Integer> pos = new ArrayList<Integer>();
			pos.add(i);
			Position bestMove = calculateMiniMax(pos, 0, true, MIN, MAX);
			if(bestMove.value > bestPosition.value){
				bestPosition = bestMove;
			}
		}
		return getParent(bestPosition.position).getMapMove();
	}
		
	
	// a minimax algorithm with alpha-beta pruning. use whenever you need to find the best possible move based on heuristic values
	public Position calculateMiniMax(ArrayList<Integer> position, int depth, Boolean self, int alpha, int beta) {
		Move current = getNode(position);
		if (depth == 2) 											//maximum depth for the search, can be modified, deeper = better but slower
			return new Position(current.score, position);				//returns the index of the weight of the best possible move, use this index later to find the move you need to make
		if (self) { 											//this is the maximizing case, we want out player to get the maximum 
			Position bestPosition = new Position(MIN, null);
			if(current.children.size() == 0){
				return new Position(current.score, position);
			}	
			for (int i = 0; i < current.children.size(); i++) { //splits the tree in half for searching left and right children
				ArrayList<Integer> newPosition = (ArrayList<Integer>) position.clone();
				newPosition.add(i);
				Position pos = calculateMiniMax(newPosition, depth + 1, false, alpha, beta);
				if(pos.value > bestPosition.value){
					bestPosition = pos;
				}
				alpha = Math.max(bestPosition.value, alpha);
				
				if (alpha >= beta) 
					break;
			}
			return bestPosition;
		} else { 					//trying to minimize the value of our rival's moves
			Position bestPosition = new Position(MAX, null);
			if(current.children.size() == 0){
				return new Position(current.score, position);
			}	
			for (int i = 0; i < current.children.size(); i++) {
				ArrayList<Integer> newPosition = (ArrayList<Integer>) position.clone();
				newPosition.add(i);
				Position pos = calculateMiniMax(newPosition, depth + 1, true, alpha, beta);
				if(pos.value < bestPosition.value){
					bestPosition = pos;
				}
				beta = Math.min(bestPosition.value, beta);
				
				if (alpha >= beta)
					break;
			} 
			return bestPosition;
		}
	}

	public Move getNode(ArrayList<Integer> position){
		Move current = tree.get(position.get(0));
		for(int i = 1; i < position.size(); i++){
			current = current.children.get(position.get(i));
		}
		return current;
	}

	public Move getParent(ArrayList<Integer> position){
		
		return tree.get(position.get(0));
	}

	public class Position{
		int value;
		ArrayList<Integer> position;

		public Position(int v, ArrayList<Integer> p){
			value = v;
			position = p;
		}
	}

}
