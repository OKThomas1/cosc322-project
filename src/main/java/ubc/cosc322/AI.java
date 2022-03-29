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
import java.util.Stack;
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


		ArrayList<Move> moveTree = new ArrayList<Move>();
		getBestMove(player, moveTree, null);
		if(moveTree.size() == 0){
			ArrayList<ArrayList<Integer>> queens = getQueenLocations(player);
			for(ArrayList<Integer> queen : queens){
				ArrayList<ArrayList<Integer>> moves = getPossibleMoves(queen);
			}
			return null;
		}
		for(Move move : moveTree){
			getBestMove(opponent, move.children, move);
			if(move.children.size() != 0){
				for(Move childMove: move.children){
					getBestMove(player, childMove.children, childMove);
				}
			} 
		}
		miniMaxAI treeSearch = new miniMaxAI(moveTree);

		return treeSearch.calculateNextMove();
	}

	public void getBestMove(int person, ArrayList<Move> moveTree, Move current){
		ArrayList<ArrayList<Integer>> queens = getQueenLocations(person);
		TreeMap<Integer, ArrayList<ArrayList<Integer>>> scores = new TreeMap<Integer, ArrayList<ArrayList<Integer>>>(Collections.reverseOrder());
		ArrayList<Integer> originalBoard = (ArrayList<Integer>) board.clone();
		Stack<Move> moveStack = new Stack<Move>();
		if(current != null){
			Move cur = current;
			while(cur.parent != null){
				moveStack.add(cur.parent);
				cur = cur.parent;
			}
			while(moveStack.size() != 0){
				cur = moveStack.pop();
				updateGameState(cur.getMapMove(), cur.person == player ? true : false);
			}
		}

		for(ArrayList<Integer> queen: queens){
			ArrayList<ArrayList<Integer>> moves = getPossibleMoves(queen);
			for(ArrayList<Integer> move: moves){
				ArrayList<Integer> backup = (ArrayList<Integer>) board.clone();
				updateQueen(queen, move, person);	
				ArrayList<ArrayList<Integer>> data = new ArrayList<ArrayList<Integer>>();
				data.add(queen);
				data.add(move);
				scores.put(calculateBoard(person), data);
				board = backup;
			}
		}

		int count = 0;
		Set<Map.Entry<Integer, ArrayList<ArrayList<Integer>>>> set = scores.entrySet();
		Iterator<Map.Entry<Integer, ArrayList<ArrayList<Integer>>>> i = set.iterator();
		int bestScore = -1000;
		int secondBestScore = -1000;
		Move bestMove = null;		
		Move secondBestMove = null;		

		while(i.hasNext()){
			Map.Entry<Integer, ArrayList<ArrayList<Integer>>> move = (Map.Entry<Integer, ArrayList<ArrayList<Integer>>>) i.next();
			ArrayList<Integer> queenCur = move.getValue().get(0);
			ArrayList<Integer> queenNext = move.getValue().get(1);
			ArrayList<Integer> backup = (ArrayList<Integer>) board.clone();
			updateQueen(queenCur, queenNext, person);
			ArrayList<ArrayList<Integer>> arrowMoves = getPossibleMoves(queenNext);
			for(ArrayList<Integer> arrowMove: arrowMoves){
				ArrayList<Integer> backup2 = (ArrayList<Integer>) board.clone();
				updateArrow(arrowMove);	
				int score = calculateBoard(person);
				if(score > bestScore && score > secondBestScore){
					secondBestScore = bestScore;
					bestScore = score;
					secondBestMove = bestMove;
					bestMove = new Move(queenCur, queenNext, arrowMove, board, score, current, person);
				} else if (score > secondBestScore){
					secondBestScore = score;
					secondBestMove = new Move(queenCur, queenNext, arrowMove, board, score, current, person);
				}
				board = backup2;
			}
			board = backup;
			count++;
		}
		if(bestMove != null){
			moveTree.add(bestMove);
		}	
		if(secondBestMove != null){
			moveTree.add(secondBestMove);
		}
		board = originalBoard;
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
				} else {
					break;
				}
				row += direction[0];
				column += direction[1];
			}
		}
		return possibleMoves;
	}

	public int calculateBoard(int person){
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
		if(person == player){
			return scores[0] - scores[1];
		} else {
			return scores[1] - scores[0];
		}
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

	public void updateGameState(Map<String, Object> move, boolean self){
		ArrayList<Integer> cur = (ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_CURR);
		board.set((cur.get(0) * 11) + cur.get(1), 0);
		ArrayList<Integer> next = (ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_NEXT);
		board.set((next.get(0) * 11)+ next.get(1), self ? player : opponent);
		ArrayList<Integer> arrow = (ArrayList<Integer>) move.get(AmazonsGameMessage.ARROW_POS);
		board.set((arrow.get(0) * 11) + arrow.get(1), 3);
	}

	public void updateQueen(ArrayList<Integer> queen, ArrayList<Integer> nextQueen, int person){
		board.set((queen.get(0) * 11) + queen.get(1), 0);
		board.set((nextQueen.get(0) * 11)+ nextQueen.get(1), person);
	}

	public void updateArrow(ArrayList<Integer> arrow){
		board.set((arrow.get(0) * 11) + arrow.get(1), 3);
	}

	public class Move {
		public ArrayList<Integer> queenCur;
		public ArrayList<Integer> queenNext;
		public ArrayList<Integer> arrow;
		public ArrayList<Move> children;
		public ArrayList<Integer> board;
		public Move parent;
		public int score;
		public int person;

		public Move(ArrayList<Integer> cur, ArrayList<Integer> next, ArrayList<Integer> ar, ArrayList<Integer> bo, int sc, Move parent, int player){
			queenCur = cur;
			queenNext = next;
			arrow = ar;
			board = bo;
			score = sc;
			children = new ArrayList<Move>();
			person = player;
		}

		public Map<String, Object> getMapMove(){
			return createMove(queenCur, queenNext, arrow);
		}		

	}
}