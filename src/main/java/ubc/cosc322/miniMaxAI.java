package ubc.cosc322;

import java.util.ArrayList;
import java.util.Map;

import ubc.cosc322.AI.Move;

public class miniMaxAI{
	
	public final int MAX = 1000;
	public final int MIN = -1000;
	public final int DEPTH = AI.DEPTH;

	public ArrayList<Move> tree;

	public miniMaxAI(ArrayList<Move> t) {
		tree = t;
	}
	
	public Map<String, Object> calculateNextMove(){ 
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
		
	
	public Position calculateMiniMax(ArrayList<Integer> position, int depth, Boolean self, int alpha, int beta) {
		Move current = getNode(position);
		if (depth >= DEPTH) 											
			return new Position(current.score, position);				
		if (self) { 											
			Position bestPosition = new Position(MIN, null);
			if(current.children.size() == 0){
				return new Position(current.score, position);
			}	
			for (int i = 0; i < current.children.size(); i++) { 
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
