package player;

public class BestMove {
	
	protected Move move;
	protected int score;
	
	public BestMove(){
		move = null;
		score = MachinePlayer.LOSE;
	}
}
