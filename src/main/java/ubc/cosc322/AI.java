package ubc.cosc322;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
public class AI {

	ArrayList<Integer> board;
	int player;
	int opponent;
	int[][] directions = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

	public AI(ArrayList<Integer> startBoard, int id){
		board = startBoard;
		player = id;
		opponent = player == 1 ? 2 : 1;
	}

	public Map<String, Object> calculateNextMove(){
		ArrayList<ArrayList<Integer>> queens = getQueenLocations(player);
		TreeMap<Integer, ArrayList<ArrayList<Integer>>> scores = new TreeMap<Integer, ArrayList<ArrayList<Integer>>>(Collections.reverseOrder());
		ArrayList<Integer> originalBoard = (ArrayList<Integer>) board.clone();
		for(ArrayList<Integer> queen: queens){
			ArrayList<ArrayList<Integer>> moves = getPossibleMoves(queen);
			for(ArrayList<Integer> move: moves){
				ArrayList<Integer> backup = (ArrayList<Integer>) board.clone();
				updateQueen(queen, move);	
				ArrayList<ArrayList<Integer>> data = new ArrayList<ArrayList<Integer>>();
				data.add(queen);
				data.add(move);
				scores.put(calculateBoard(), data);
				board = backup;
			}
		}

		int count = 0;
		Set<Map.Entry<Integer, ArrayList<ArrayList<Integer>>>> set = scores.entrySet();
		Iterator<Map.Entry<Integer, ArrayList<ArrayList<Integer>>>> i = set.iterator();
		int highestScore = -100000;
		Map<String, Object> bestMove = null;

		while(i.hasNext()){
			Map.Entry<Integer, ArrayList<ArrayList<Integer>>> move = (Map.Entry<Integer, ArrayList<ArrayList<Integer>>>) i.next();
			ArrayList<Integer> queenCur = move.getValue().get(0);
			ArrayList<Integer> queenNext = move.getValue().get(1);
			ArrayList<ArrayList<Integer>> arrowMoves = getPossibleMoves(queenNext);
			for(ArrayList<Integer> arrowMove: arrowMoves){
				ArrayList<Integer> backup = (ArrayList<Integer>) board.clone();
				updateQueen(queenCur, queenNext);
				updateArrow(arrowMove);	
				int score = calculateBoard();
				if(score > highestScore){
					highestScore = score;
					bestMove = createMove(queenCur, queenNext, arrowMove);
					System.out.println("current best move: " + bestMove);
				}
				board = backup;
			}
			count++;
		}
		
		board = originalBoard;
		return bestMove;
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

	public int calculateBoard(){
		int[] scores = new int[3];
		for(int i = 1; i <= 10; i++){
			for(int j = 1; j <= 10; j++){
				if(board.get(getMoveFromCoordinates(i, j)) != 0){
					continue;
				}
				int playerCloset = getPlayerClosest(getMoveFromCoordinates(i, j));
				if(playerCloset == player){
					scores[0]++;
				}
				else if(playerCloset == opponent){
					scores[1]++;
				}
				else {
					scores[2]++;
				}
			}
		}
		System.out.println(Arrays.toString(scores));
		return scores[0] - scores[1];
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

	public int getPlayerClosest(int space){
		int[] playerDistances = new int[4];
		int[] opponentDistances = new int[4];


		ArrayList<ArrayList<Integer>> playerQueens = getQueenLocations(player);
		for(int i = 0; i < 4; i++){
			playerDistances[i] = getDistance(playerQueens.get(i), space);
		}

		ArrayList<ArrayList<Integer>> opponentQueens = getQueenLocations(opponent);
		for(int i = 0; i < 4; i++){
			opponentDistances[i] = getDistance(opponentQueens.get(i), space);
		}

		int playerMin = Arrays.stream(playerDistances).min().getAsInt();
		int opponentMin = Arrays.stream(opponentDistances).min().getAsInt();

		if(playerMin < opponentMin){
			return player;
		}
		if(opponentMin < playerMin){
			return opponent;
		}
		return -1;
	}

	public int getMoveFromCoordinates(int x, int y){
		return x * 11 + y;
	}

	public int getMoveFromCoordinates(ArrayList<Integer> space){
		return space.get(0) * 11 + space.get(1);
	}

	public ArrayList<Integer> getCoordinatesFromSpace(int space){
		ArrayList<Integer> coords = new ArrayList<Integer>();
		coords.add(space/11);
		coords.add(space%11);
		return coords;
	}

	public int getDistance(ArrayList<Integer> queen, int space){
		ArrayList<ArrayList<Integer>> queenMoves = getPossibleMoves(queen);
		for(ArrayList<Integer> move: queenMoves){
			if(space == getMoveFromCoordinates(move)){
				return 1;
			}
		}

		ArrayList<ArrayList<Integer>> spaceMoves = getPossibleMoves(getCoordinatesFromSpace(space));
		for(ArrayList<Integer> move: spaceMoves){
			for(ArrayList<Integer> queenMove: queenMoves){
				if(getMoveFromCoordinates(queenMove) == getMoveFromCoordinates(move)){
					return 2;
				}
			}
		}

		return 3;
	}

	public Map<String, Object> createMove(ArrayList<Integer> queen, ArrayList<Integer> queenNext, ArrayList<Integer> arrow){
		HashMap<String, Object> move = new HashMap<String, Object>();

		move.put(AmazonsGameMessage.QUEEN_POS_CURR, queen);
		move.put(AmazonsGameMessage.QUEEN_POS_NEXT, queenNext);
		move.put(AmazonsGameMessage.ARROW_POS, arrow);
		return move;
	}

	public void updateGameState(Map<String, Object> move){
		ArrayList<Integer> cur = (ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_CURR);
		board.set((cur.get(0) * 11) + cur.get(1), 0);
		ArrayList<Integer> next = (ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_NEXT);
		board.set((next.get(0) * 11)+ next.get(1), player);
		ArrayList<Integer> arrow = (ArrayList<Integer>) move.get(AmazonsGameMessage.ARROW_POS);
		board.set((arrow.get(0) * 11) + arrow.get(1), 3);
	}

	public void updateQueen(ArrayList<Integer> queen, ArrayList<Integer> nextQueen){
		board.set((queen.get(0) * 11) + queen.get(1), 0);
		board.set((nextQueen.get(0) * 11)+ nextQueen.get(1), player);
	}

	public void updateArrow(ArrayList<Integer> arrow){
		board.set((arrow.get(0) * 11) + arrow.get(1), 3);
	}
}