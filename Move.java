

public class Move{
	private int fromPos;
	private int toPos;
	private int takenPos;


	public Move(int toPos){
		if (toPos <0 || toPos >= State.TOTAL_POS)
			throw new IllegalArgumentException();
		this.fromPos = -1;
		this.toPos = toPos;
		this.takenPos = -1;
	}

	public Move (int fromPos, int toPos){
		this.fromPos = fromPos;
		this.toPos = toPos;
		this.takenPos = -1;
	}

	public Move (int fromPos, int toPos, int takenPos){
		this.fromPos = fromPos;
		this.toPos = toPos;
		this.takenPos = takenPos;
	}

	public int getFromPos(){
		return fromPos;
	}

	public int getToPos(){
		return toPos;
	}

	public int getTakenPos(){
		return takenPos;
	}
}