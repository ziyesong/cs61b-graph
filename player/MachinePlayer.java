/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

	public static final int black = 0;//black player
	public static final int white = 1;//white player
	public static final int WIN = 100;
	public static final int LOSE = -100;



	protected Board gameBoard;
	protected int searchDepth;

	protected int myColor;
	protected int opponentColor;

	public dict.HashTableChained table;

	// Creates a machine player with the given color.  Color is either 0 (black)
	// or 1 (white).  (White has the first move.)
	public MachinePlayer(int color) {
		this.gameBoard = new Board();
		this.myColor = color;
		this.opponentColor = 1 - color;
		this.searchDepth = 2;//default depth
	}

	// Creates a machine player with the given color and search depth.  Color is
	// either 0 (black) or 1 (white).  (White has the first move.)
	public MachinePlayer(int color, int searchDepth) {
		this.gameBoard = new Board();
		this.myColor = color;
		this.opponentColor = 1 - color;
		this.searchDepth = searchDepth;
	}

	/**
	 * Check if a move is valid for the player
	 * @param move
	 * @param player: white or black
	 * @return True if valid, false if not
	 */
	public boolean isValidMove(Move move, int player){
		if (player == black && (move.x1 == 0 || move.x1 == 7)){//place black in white goal area
			return false;
		}
		if (player == white && (move.y1 == 0 || move.y1 == 7)){//place white in black goal area
			return false;
		}
		if (gameBoard.elementAt(move.x1, move.y1) >= 0){//occupied or illegal
			return false;
		}
		if (move.moveKind == Move.ADD){//add move
			if (gameBoard.chips[player] >= 10){
				return false;
			}
			return !gameBoard.clustered(move.x1, move.y1, player);
		}
		if (move.moveKind == Move.STEP){//step move
			if (gameBoard.chips[player] != 10){//illegal until all 10 chips on board
				return false;
			}
			if (move.x1 == move.x2 && move.y1 == move.y2){//no move
				return false;
			}
			if (gameBoard.elementAt(move.x2, move.y2) != player){//illegal old chip
				return false;
			}
			gameBoard.setElement(move.x2, move.y2, -1);//set (x2,y2) to empty
			boolean b =  !gameBoard.clustered(move.x1, move.y1, player);
			gameBoard.setElement(move.x2, move.y2, player);//restore (x2,y2)
			return b;
		}
		if (move.moveKind == Move.QUIT){//QUIT
			//move.moveKind == move.QUIT
			System.out.println("[QUIT]");
			System.exit(0);
		}
		return false;//this line is never reached
	}

	/**
	 * Perform the specified move
	 * @param move
	 * @param player
	 * @return true if move is valid, false if not
	 */
	public boolean makeMove(Move move, int player){
		if (!isValidMove(move, player)){//invalid move
			return false;
		}else {//make the move
			if (move.moveKind == Move.ADD){//ADD move
				gameBoard.setElement(move.x1, move.y1, player);//add chip
				gameBoard.chips[player]++;//increment num of chips
				return true;
			}
			if(move.moveKind == Move.STEP){//STEP move
				gameBoard.setElement(move.x1, move.y1, player);//add chip
				gameBoard.setElement(move.x2, move.y2, Board.empty);//remove chip
				return true;
			}
			return false;//this line is never reached
		}
	}

	/**
	 * Undo the move
	 * @param move
	 * @param player
	 */
	public boolean undoMove(Move move, int player){
		if (move.moveKind == Move.ADD){
			gameBoard.setElement(move.x1, move.y1, Board.empty);
			return true;
		}
		if (move.moveKind == Move.STEP){
			Move m = new Move(move.x2, move.y2, move.x1, move.y1);//reverse move
			return makeMove(m, player);
		}
		return false;//this line is never reached
	}

	/**
	 * Assign a numerical value(0 to 100) to the board
	 * @param board
	 * @param player
	 * @return
	 */
	public static int evaluate(Board board, int player){
		//return (int)(Math.random()*100);
		int myGoalPairs = 0;
		int opGoalPairs = 0;
		int myPairs = 0;
		int opPairs = 0;
		int myGoals = 0;
		int opGoals = 0;
		
		//update nonGoalPairs
		for (int i = 1; i <= 6; i++){
			for (int j = 1; j <= 6; j++){
				if (board.elementAt(i, j) == player){
					list.DList list = board.connections(i, j , player);
					myPairs += list.length();
				}
				if (board.elementAt(i, j) == 1 - player){
					list.DList list1 = board.connections(i, j , 1 - player);
					opPairs += list1.length();
				}
			}
		}
		
		//update goalPairs and goals
		if (player == black){
			for (int i = 1; i <= 6; i++){
				if (board.elementAt(i, 0) == player){
					list.DList list = board.connections(i, 0, player);
					myGoalPairs += list.length();
					myGoals++;
				}
				if (board.elementAt(i, 7) == player){
					list.DList list = board.connections(i, 7, player);
					myGoalPairs += list.length();
					myGoals++;
				}
			}

		}else {//player == white
			for (int i = 1; i <= 6; i++){
				if (board.elementAt(0, i) == player){
					list.DList list = board.connections(0, i, player);
					opGoalPairs += list.length();
					opGoals++;
				}
				if (board.elementAt(7, i) == player){
					list.DList list = board.connections(7, i, player);
					opGoalPairs += list.length();
					opGoals++;
				}
			}

		}

		return 5*(myGoalPairs - opGoalPairs) + 2*(myPairs - opPairs);
	}

	/**
	 * @param player
	 * @return a Dlist of all valid moves the player can make
	 */
	public list.DList validMoves(int player){
		list.DList list = new list.DList();
		if (gameBoard.chips[player] < 10){//add move
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 8; j++){
					Move move = new Move(i, j);
					if (isValidMove(move, player)){
						list.insertBack(move);
					}
				}
			}
		}else{//step move
			list.DList pos = new list.DList();//list holding positions of all chips
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 8; j++){
					if(gameBoard.elementAt(i,j) == player){
						int[] position = {i, j};
						pos.insertBack(position);
					}
				}
			}
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 8; j++){
					list.ListNode node = pos.front();
					try{
						while (true){

							Move move = new Move(i, j, ((int[])node.item())[0], ((int[])node.item())[1]);
							if (isValidMove(move, player)){
								list.insertBack(move);
							}

							//node = node.next();
						}
					}catch (list.InvalidNodeException e){
						System.err.println(e);
					}
				}
			}
		}
		return list;
	}

	// Returns a new move by "this" player.  Internally records the move (updates
	// the internal game board) as a move by "this" player.
	public Move chooseMove() {
		Move move = minimax(myColor, searchDepth, 0, 100).move;
		makeMove(move, myColor);
		return move;
	}

	/**
	 * Perform alpha-beta pruning
	 * @param player
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public BestMove minimax(int player, int depth, int alpha, int beta){
		BestMove myBest = new BestMove();
		BestMove reply;

		//base case
		boolean computerWin = gameBoard.connected(myColor);
		boolean opponentWin = gameBoard.connected(opponentColor);
		if (computerWin && opponentWin){//opponent has a win
			myBest.score = LOSE;
			return myBest;
		}
		if (computerWin){//computer has a win
			myBest.score = WIN;
			return myBest;
		}
		if (opponentWin){//opponent has a win
			myBest.score = LOSE;
			return myBest;
		}
		if (depth == 0){//search ends
			myBest.score = evaluate(gameBoard, player);
			return myBest;
		}

		if (player == myColor){
			myBest.score = alpha;
		}else {
			myBest.score = beta;
		}

		list.DList moves = validMoves(player);
		list.ListNode node = moves.front();
		try{
			while (true){

				Move move = (Move)node.item();
				this.makeMove(move, player);//perform move

				node = node.next();

				reply = minimax(1 - player, depth - 1, alpha, beta);
				this.undoMove(move, player);//undo move

				if ((player == myColor) && (reply.score >= myBest.score)){
					myBest.move = move;
					myBest.score = reply.score;
					alpha = reply.score;
				}
				if ((player == opponentColor) && (reply.score <= myBest.score)){
					myBest.move = move;
					myBest.score = reply.score;
					beta = reply.score;
				}
				if (alpha >= beta){
					return myBest;
				}


			}
		}catch(list.InvalidNodeException e){
			System.err.println(e);
		}
		return myBest;
	}


	// If the Move m is legal, records the move as a move by the opponent
	// (updates the internal game board) and returns true.  If the move is
	// illegal, returns false without modifying the internal state of "this"
	// player.  This method allows your opponents to inform you of their moves.
	public boolean opponentMove(Move m) {
		return makeMove(m, opponentColor);
	}

	// If the Move m is legal, records the move as a move by "this" player
	// (updates the internal game board) and returns true.  If the move is
	// illegal, returns false without modifying the internal state of "this"
	// player.  This method is used to help set up "Network problems" for your
	// player to solve.
	public boolean forceMove(Move m) {
		return makeMove(m, myColor);
	}

}
