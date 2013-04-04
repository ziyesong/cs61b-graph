package player;

public class Board {

	private int[][] board = new int[8][8];//default 8*8 board

	public final static int black = 0;
	public final static int white = 1;
	public final static int empty = -1;
	public final static int illegal = 2;

	//numChips[0]:black [1]:white
	protected int chips[] = new int[2];



	//constructor
	public Board(){
		//initialize the board
		for(int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				board[i][j] = empty;
			}
		}
		//reset the corners
		board[0][0] = illegal;
		board[0][7] = illegal;
		board[7][0] = illegal;
		board[7][7] = illegal;
		chips[0] = 0;
		chips[1] = 0;
	}

	public int elementAt(int i, int j){
		return board[i][j];
	}

	public void setElement(int i, int j, int n){
		board[i][j] = n;
	}

	/**
	 * neighbors() will only be called by clustered()
	 * @param i: x coordinate
	 * @param j: y coordinate
	 * @param player: black or white
	 * @return n[0] is num of neighbors, 
	 * 		   n[1] is x coordinate of a neighbor, first come
	 *         n[2] is y coordinate of a neighbor, first come
	 */
	public int[] neighbors(int i, int j, int player){
		int[] n = new int[3];
		if (i > 0 && i < 7 && j > 0 && j < 7){//(1,1) to (6,6)
			if (this.elementAt(i-1, j-1) == player){n[0]++; n[1] = i-1; n[2] = j-1;}
			if (this.elementAt(i, j-1) == player){n[0]++; n[1] = i; n[2] = j-1;}
			if (this.elementAt(i+1, j-1) == player){n[0]++; n[1] = i+1; n[2] = j-1;}
			if (this.elementAt(i-1, j) == player){n[0]++; n[1] = i-1; n[2] = j;}
			if (this.elementAt(i+1, j) == player){n[0]++; n[1] = i+1; n[2] = j;}
			if (this.elementAt(i-1, j+1) == player){n[0]++; n[1] = i-1; n[2] = j+1;}
			if (this.elementAt(i, j+1) == player){n[0]++; n[1] = i; n[2] = j+1;}
			if (this.elementAt(i+1, j+1) == player){n[0]++; n[1] = i+1; n[2] = j+1;}	
		}
		if (i == 0){//(0,1) to (0,6)
			if (this.elementAt(i, j-1) == player){n[0]++; n[1] = i; n[2] = j-1;}
			if (this.elementAt(i+1, j-1) == player){n[0]++; n[1] = i+1; n[2] = j-1;}
			if (this.elementAt(i+1, j) == player){n[0]++; n[1] = i+1; n[2] = j;}
			if (this.elementAt(i, j+1) == player){n[0]++; n[1] = i; n[2] = j+1;}
			if (this.elementAt(i+1, j+1) == player){n[0]++; n[1] = i+1; n[2] = j+1;}
		}
		if (i == 7){//(7,1) to (7,6)
			if (this.elementAt(i-1, j-1) == player){n[0]++; n[1] = i-1; n[2] = j-1;}
			if (this.elementAt(i, j-1) == player){n[0]++; n[1] = i; n[2] = j-1;}
			if (this.elementAt(i-1, j) == player){n[0]++; n[1] = i-1; n[2] = j;}
			if (this.elementAt(i-1, j+1) == player){n[0]++; n[1] = i-1; n[2] = j+1;}
			if (this.elementAt(i, j+1) == player){n[0]++; n[1] = i; n[2] = j+1;}
		}
		if (j == 0){//(1,0) to (6,0)
			if (this.elementAt(i-1, j) == player){n[0]++; n[1] = i-1; n[2] = j;}
			if (this.elementAt(i+1, j) == player){n[0]++; n[1] = i+1; n[2] = j;}
			if (this.elementAt(i-1, j+1) == player){n[0]++; n[1] = i-1; n[2] = j+1;}
			if (this.elementAt(i, j+1) == player){n[0]++; n[1] = i; n[2] = j+1;}
			if (this.elementAt(i+1, j+1) == player){n[0]++; n[1] = i+1; n[2] = j+1;}
		}
		if (j == 7){//(1,7) to (6,7)
			if (this.elementAt(i-1, j-1) == player){n[0]++; n[1] = i-1; n[2] = j-1;}
			if (this.elementAt(i, j-1) == player){n[0]++; n[1] = i; n[2] = j-1;}
			if (this.elementAt(i+1, j-1) == player){n[0]++; n[1] = i+1; n[2] = j-1;}
			if (this.elementAt(i-1, j) == player){n[0]++; n[1] = i-1; n[2] = j;}
			if (this.elementAt(i+1, j) == player){n[0]++; n[1] = i+1; n[2] = j;}
		}
		return n;
	}

	/**
	 * @param i
	 * @param j
	 * @param player
	 * @return whether more than two chips form a cluster
	 */
	public boolean clustered(int i, int j, int player){
		if (neighbors(i, j, player)[0] == 0){
			return false;
		}
		if (neighbors(i, j, player)[0] == 1){
			int x = neighbors(i, j, player)[1];
			int y = neighbors(i, j, player)[2];
			if (neighbors(x, y, player)[0] >= 1){
				return true;
			}else{
				return false;
			}
		}else{//num of neighbors >= 2
			return true;
		}
	}

	/**
	 * Check if a player has a valid network
	 * @param player
	 * @return
	 */
	public boolean connected(int player){
		if (!inGoal(player)){//no chips in both goal areas
			return false;
		}

		list.DList start = new list.DList();//chips in starting goal area, up for black, left for white
		if (player == black){
			for (int i = 1; i <= 6; i++){
				if (elementAt(i, 0) == player){
					int[] temp = {i, 0};
					start.insertBack(temp);
				}
			}
		}
		if (player == white){
			for (int i = 1; i <= 6; i++){
				if (elementAt(0, i) == player){
					int[] temp = {0, i};
					start.insertBack(temp);
				}
			}
		}

		//System.out.println(start);

		dict.HashTableChained table = new dict.HashTableChained(chips[player]);

		list.ListNode node = start.front();

		try{
			while(true){

				int i = ((int[])node.item())[0];
				int j = ((int[])node.item())[1];


				//System.out.println(i + " " + j);
				//System.out.println("called");
				boolean result = checkNetwork(i, j, 0, player, 1, table);

				//System.out.println(result);

				if (result){
					//System.out.println("return true!!!");
					return true;
				}

				node = node.next();
			}
		}catch (list.InvalidNodeException e){
			//System.err.println(e);
		}

		//System.out.println("sad");
		return false;//this line is never reached
	}

	public boolean checkNetwork(int i, int j, int type, int player, int step, dict.HashTableChained table){
		//System.out.println("checkNetwork " + i + " " + j);
		if ((i == 7 || j == 7) && step >= 6 && step <= 10){//chip in goal area, has a win
			//System.out.println("network");
			return true;
		}
		
		//System.out.println("step " + step);
		//table.insert(10*i + j, 10*i + j);
		
		//System.out.println("tablesize " + table.size());
		//table.remove(10*i + j);
		list.DList connections = connections(i, j, player);


		list.ListNode node = connections.front();

		//System.out.println("connections" + connections);

		try{
			while(true){
				//System.out.println("while");
				
				int x = ((int[])node.item())[0];
				int y = ((int[])node.item())[1];
				int newtype = ((int[])node.item())[2];
				
				if ((x != i || y != j) && (Math.abs(type) != Math.abs(newtype)) && table.find(10*x+y) == null){
					table.insert(10*i + j, 10*i + j);
					//System.out.println("recurse " + x + " " + y);
					//table.insert(10*x + y, 10*x + y);
					boolean result = checkNetwork(x, y, newtype, player, step + 1, table);
					if (result){
						return true;
					}
					//table.remove(10*x + y);

				}
				table.remove(10*i + j);
				//System.out.println("next");
				node = node.next();
				//System.out.println("tablesize " + table.size());
				//table.remove(10*x + y);
				
			}
		}catch(list.InvalidNodeException e){
			//System.out.println("end");
			//System.err.println(e);
		}
		return false;

	}

	public list.DList connections(int i, int j, int player){
		list.DList connections = new list.DList();
		if (check1(i, j, player)[0] != -1){
			//System.out.println("check1 ");
			connections.insertBack(check1(i, j, player));
		}
		if (check11(i, j, player)[0] != -1){
			//System.out.println("check11 " + check11(i, j, player)[0] + " " + check11(i, j, player)[1]);
			connections.insertBack(check11(i, j, player));
		}
		if (check2(i, j, player)[0] != -1){
			//System.out.println("check2 ");
			connections.insertBack(check2(i, j, player));
		}
		if (check22(i, j, player)[0] != -1){
			//System.out.println("check22 ");
			connections.insertBack(check22(i, j, player));
		}
		if (check3(i, j, player)[0] != -1){
			//System.out.println("check3 ");
			connections.insertBack(check3(i, j, player));
		}
		if (check33(i, j, player)[0] != -1){
			//System.out.println("check33 ");
			connections.insertBack(check33(i, j, player));
		}
		if (check4(i, j, player)[0] != -1){
			//System.out.println("check4 ");
			connections.insertBack(check4(i, j, player));
		}
		if (check44(i, j, player)[0] != -1){
			//System.out.println("check44 ");
			connections.insertBack(check44(i, j, player));
		}
		return connections;
	}


	/**
	 * Determines if the player has chips in both goal areas
	 * @param player
	 * @return
	 */
	public boolean inGoal(int player){
		if (player == black){
			boolean top = false;
			boolean bottom = false;
			for(int i = 1; i < 7; i++){
				if (elementAt(i, 0) == black){
					top = true;
				}
			}
			for(int i = 1; i < 7; i++){
				if (elementAt(i, 7) == black){
					bottom = true;
				}
			}
			return top && bottom;
		}else{//white
			boolean left = false;
			boolean right = false;
			for(int i = 1; i < 7; i++){
				if (elementAt(0, i) == white){
					left = true;
				}
			}
			for(int i = 1; i < 7; i++){
				if (elementAt(7, i) == white){
					right = true;
				}
			}
			return left && right;
		}
	}


	//search connected chips in 8 directions
	//1 for up, 2 for upright, 3 for right, 4 for downright
	//negative for opposite directions
	//position[0] is x coordinate, -1 for no connection
	//position[1] is y coordinate
	//position[2] is direction

	//case 1
	public int[] check1(int i, int j, int player){
		int[] position = {-1, -1, 1};
		for(int k = j - 1; k >= 0; k--){
			if (elementAt(i, k) == 1 - player){//blocked
				return position;
			}
			if (elementAt(i, k) == player){
				position[0] = i;
				position[1] = k;
				return position;
			}
		}
		return position;
	}

	//case -1
	public int[] check11(int i, int j, int player){
		int[] position = {-1, -1, -1};
		for(int k = j + 1; k <= 7; k++){
			if (elementAt(i, k) == 1 - player){//blocked
				return position;
			}
			if (elementAt(i, k) == player){
				position[0] = i;
				position[1] = k;
				return position;
			}
		}
		return position;
	}

	//case 2
	public int[] check2(int i, int j, int player){
		int[] position = {-1, -1, 2};
		while(i < 7 && j > 0){
			i++;
			j--;
			if (elementAt(i, j) == 1 - player){//blocked
				return position;
			}
			if (elementAt(i, j) == player){
				position[0] = i;
				position[1] = j;
				return position;
			}
		}
		return position;
	}

	//case -2
	public int[] check22(int i, int j, int player){
		int[] position = {-1,-1, -2};
		while(i > 0 && j < 7){
			i--;
			j++;
			if (elementAt(i, j) == 1 - player){//blocked
				return position;
			}
			if (elementAt(i, j) == player){
				position[0] = i;
				position[1] = j;
				return position;
			}
		}
		return position;
	}

	//case 3
	public int[] check3(int i, int j, int player){
		int[] position = {-1, -1, 3};
		for (int k = i + 1; k <= 7; k++){
			if (elementAt(k, j) == 1 - player){//blocked
				return position;
			}
			if (elementAt(k, j) == player){
				position[0] = k;
				position[1] = j;
				return position;
			}
		}
		return position;
	}

	//case -3
	public int[] check33(int i, int j, int player){
		int[] position = {-1, -1, -3};
		for (int k = i - 1; k >= 0; k--){
			if (elementAt(k, j) == 1 - player){//blocked
				return position;
			}
			if (elementAt(k, j) == player){
				position[0] = k;
				position[1] = j;
				return position;
			}
		}
		return position;
	}

	//case 4
	public int[] check4(int i, int j, int player){
		int[] position = {-1, -1, 4};
		while(i < 7 && j < 7){
			i++;
			j++;
			if (elementAt(i, j) == 1 - player){//blocked
				return position;
			}
			if (elementAt(i, j) == player){
				position[0] = i;
				position[1] = j;
				return position;
			}
		}
		return position;
	}

	//case -4
	public int[] check44(int i, int j, int player){
		int[] position = {-1, -1, -4};
		while(i > 0 && j > 0){
			i--;
			j--;
			if (elementAt(i, j) == 1 - player){//blocked
				return position;
			}
			if (elementAt(i, j) == player){
				position[0] = i;
				position[1] = j;
				return position;
			}
		}
		return position;
	}

	public String toString(){
		return "";
	}


	public static void main(String[] args) {
		Board b = new Board();
		b.setElement(6, 0, black);
		//b.setElement(4, 2, black);
		b.setElement(6, 5, black);
		//b.setElement(3, 2, black);
		//b.setElement(1, 3, black);
		b.setElement(5, 5, black);
		b.setElement(3, 3, black);
		//b.setElement(1, 3, black);
		//b.setElement(1, 4, black);
		//b.setElement(3, 7, black);
		b.setElement(3, 5, black);
		//b.setElement(4, 5, black);
		b.setElement(5, 7, black);
		//list.DList l = b.connections(1, 2, black);
		//System.out.println(l);
		//System.out.println(b.connections(5, 5, black));
		//System.out.println(b.connected(black));
	}
}