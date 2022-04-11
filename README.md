# cosc322-project

<h1> Game of Amazons AI Player</h1>

<h2> Overview </h2>
Our AI was built in Java to play the game of Amazons against online opponents in the server while operating within the rules of the game and under the constraint of having 30 seconds allowed per move. This is achieved via the generation of a tree with min-distance heuristic values for each node and the traversal of such a tree with a minimax algorithm. The tree this AI generated for the tournament had a width and depth value of 2. However, our AI can choose the first move in less than 30 seconds with a width and depth of 3.

<h2> Heuristic Evaluation </h2>
Our AI uses a min-distance heuristic evaluation function and a minimax tree search with alpha-beta pruning to find the best move. It starts by taking every move, and calculating the total score each player has after each move. Each square is worth 1 point, and if white can move to the square in fewer moves than black, then white gets the point; if both players can get to the square in the same amount of moves, no one gets a point. 

The best moves that give the player the largest advantage over their opponent get added to a tree, and then it calculates the best move the opponent can do in response to each move. This alternates back and forth, and the AI can be set to any depth and width, but java will run out of stack space/memory eventually so there is a cap.

<h2> Tree Traversal </h2>
Once the tree is complete, we run a recursive minimax search algorithm to find the best branch to follow by evaluating the values of each branch at each level. Each level alternates between a player move and an opponent move, so it must maximize the value of the player’s move that best minimizes the value of the opponent’s potential moves. 
 
We have also implemented alpha-beta pruning to the traversal, so branches that the algorithm deems not worth exploring by initial value are “pruned” from the tree, decreasing runtime as only viable options are traversed. This allows it to find its predicted best move, which then gets sent to the server. 
