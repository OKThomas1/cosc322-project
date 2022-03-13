package ubc.cosc322;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AI {

	ArrayList<Integer> board;
	int player;
	int[][] directions = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

	public AI(ArrayList<Integer> startBoard, int id){
		board = startBoard;
		player = id;
	}

	public Map<String, Object> calculateNextMove(){
		HashMap<String, Object> move = new HashMap<String, Object>();

		ArrayList<ArrayList<Integer>> queens = getQueenLocations(player);
		System.out.println("Queen locations: " + queens.toString());

		ArrayList<Integer> randomQueenMove = null;
		ArrayList<Integer> randomQueen = null;
		int tries = 0;
		while(randomQueenMove == null){
			if(tries == 4){
				break;
			}

			int randomQueenIdx = (int) Math.floor(Math.random() * queens.size());
			randomQueen = queens.get(randomQueenIdx);
			System.out.println("Random queen: " + randomQueen.toString() + " try: " + tries);
			ArrayList<ArrayList<Integer>> randomQueenMoves = getPossibleMoves(randomQueen);
			System.out.println("Queen moves: " + randomQueenMoves.toString() + " try: " + tries);
			if(randomQueenMoves.size() == 0){
				continue;
			}
			int randomQueenMoveIdx = (int) Math.floor(Math.random() * randomQueenMoves.size());
			randomQueenMove = randomQueenMoves.get(randomQueenMoveIdx);
			tries++;
		}

		if(randomQueenMove == null){
			//no moves, lose
			return null;
		}

		move.put(AmazonsGameMessage.QUEEN_POS_CURR, randomQueen);
		move.put(AmazonsGameMessage.Queen_POS_NEXT, randomQueenMove);
		System.out.println("Queen move: " + randomQueenMove.toString());
		
		ArrayList<ArrayList<Integer>> randomArrowMoves = getPossibleMoves(randomQueenMove);
		System.out.println("Arrow moves: " + randomArrowMoves.toString());
		int randomArrowMoveIdx = (int) Math.floor(Math.random() * randomArrowMoves.size());
		ArrayList<Integer> randomArrowMove = randomArrowMoves.get(randomArrowMoveIdx);
		System.out.println("Arrow move: " + randomArrowMove.toString());

		move.put(AmazonsGameMessage.ARROW_POS, randomArrowMove);
		return move;
	}

	public ArrayList<ArrayList<Integer>> getPossibleMoves(ArrayList<Integer> start){
		ArrayList<ArrayList<Integer>> possibleMoves = new ArrayList<ArrayList<Integer>>();
		for(int[] direction : directions){
			int row = start.get(0) + direction[0];
			int column = start.get(1) + direction[1];
			while(row >= 1 && row <= 10 && column >= 1 && column <= 10){
				if(board.get((row * 11) + column) == 0){
					ArrayList<Integer> move = new ArrayList<Integer>(List.of(row, column));
					possibleMoves.add(move);
				}
				row += direction[0];
				column += direction[1];
			}
		}
		return possibleMoves;
	}

	public ArrayList<ArrayList<Integer>> getQueenLocations(int id){
		ArrayList<ArrayList<Integer>> queens = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < board.size(); i++){
			if(board.get(i) == id){
				ArrayList<Integer> queen = new ArrayList<Integer>();
				queen.add(i/11);
				queen.add(i%11);
				queens.add(queen);
			}
		}
		return queens;
	}

	public void updateGameState(Map<String, Object> move){
		ArrayList<Integer> cur = (ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_CURR);
		board.set((cur.get(0) * 11) + cur.get(1), 0);
		ArrayList<Integer> next = (ArrayList<Integer>) move.get(AmazonsGameMessage.Queen_POS_NEXT);
		board.set((next.get(0) * 11)+ next.get(1), player);
		ArrayList<Integer> arrow = (ArrayList<Integer>) move.get(AmazonsGameMessage.ARROW_POS);
		board.set((arrow.get(0) * 11) + arrow.get(1), 3);
	}
}