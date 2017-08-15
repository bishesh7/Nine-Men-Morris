
import java.util.*;

public class State {

	public static final int TOTAL_POS = 24;
	public static final int TOTAL_PIECES = 9;
	public static final boolean FLY_ALLOWED = false;


    public static final List<List<Integer>> NEIGHBOURS;

    public static final List<List<Integer>> MILLS;

    private static Integer [][] neigh = { {1, 9},{0, 2, 4},{1, 14},{4, 10},{1, 3, 5, 7},{4, 13},{7, 11},{4, 6, 8},{7, 12},
                                         {0, 10, 21},{3, 9, 11, 18},{6, 10, 15},{8, 13, 17},{5, 12, 14, 20},{2, 13, 23},{11, 16},
		                                 {15, 17, 19},{12, 16},{10, 19},{16, 18, 20, 22},{13, 19},{9, 22},{19, 21, 23},{14, 22} };

    private static Integer [][] mills1 =  { {0, 1, 2},{3, 4, 5},{6, 7, 8},{9, 10, 11},{12, 13, 14},{15, 16, 17},{18, 19, 20},{21, 22, 23},
                                            {0, 9, 21},{3, 10, 18},{6, 11, 15},{1, 4, 7 },{16, 19, 22},{8, 12, 17},{5, 13, 20},{2, 14, 23} };

    static {
    	        List<List<Integer>> neigh1 = new ArrayList<List<Integer>>();
    	        for (int i = 0 ; i < neigh.length; i++){
    	        	neigh1.add(Arrays.asList(neigh[i]));
    	        }
    	        NEIGHBOURS = neigh1;

    	        List<List<Integer>> mills2 = new ArrayList<List<Integer>>();
    	        for (int i = 0 ; i < mills1.length; i++){
    	        	mills2.add(Arrays.asList(mills1[i]));
    	        }
    	        MILLS = mills2;
    }

    private int [] statePosition;
    private int currPlayer;
    private int [] remainingPieces;
    private int [] unusedPieces;


    public State (){
    	statePosition = new int [TOTAL_POS];
    	for (int i=0; i < statePosition.length; i++){
    		statePosition[i]=0;
    	}

    	currPlayer = 0;

    	remainingPieces = new int [2];
    	remainingPieces[0] = remainingPieces [1] = TOTAL_PIECES;

    	unusedPieces = new int [2];
    	unusedPieces[0] = unusedPieces [1] = TOTAL_PIECES;

    }

    public State (State state){

    	statePosition = state.statePosition.clone();
    	currPlayer = state.currPlayer;
    	remainingPieces = state.remainingPieces.clone();
    	unusedPieces = state.unusedPieces.clone();
    }

    public int getPositionState (int pos){
    	return statePosition[pos];
    }
    public void setPositionState (int pos, int value){
        statePosition[pos]=value;
    }

    public int getCurrPlayer(){
    	return currPlayer;
    }

    public void setCurrPlayer(int i){
        currPlayer=i;
    }


    public int getNextPlayer(){
    	return (currPlayer+1) % 2;
    }

    public int getUnusedPieces(int player){
    	return unusedPieces[player];
    }

    public void setUnusedPieces(int player, int i){
        unusedPieces[player]=i;
    }

    public int getRemainingPieces(int player){
    	return remainingPieces[player];
    }

    public void setRemainingPieces(int player, int i){
        remainingPieces[player]=i;
    }

    public int getRemainingPiecesCurrPlayer(){
        return remainingPieces[currPlayer];
    }

    public int getRemainingPiecesNextPlayer(){
        return remainingPieces[getNextPlayer()];
    }

    private void changePlayer(){
    	currPlayer = getNextPlayer();
    }

    private void putOnBoard (int pos, int player){
    	statePosition[pos] = player+5;
    }

    private void removeFromBoard (int pos){
    	statePosition[pos] = 0;
    }

    public void deductCurrPlayerUnused (){
    	unusedPieces[currPlayer]--;
    }

    public void addCurrPlayerUnused (){
        unusedPieces[currPlayer]++;
    }

    public void makeMove (Move move){

    	if (move.getFromPos()==-1)
    		deductCurrPlayerUnused();
    	else 
    		removeFromBoard(move.getFromPos());
    	putOnBoard(move.getToPos(), currPlayer);

    	if (move.getTakenPos() != -1){
    		removeFromBoard(move.getTakenPos());
    		remainingPieces[getNextPlayer()]--;
    	}
    	changePlayer();

    }

    public void undoMove(Move move){
        changePlayer();
        if (move.getFromPos()==-1)
            addCurrPlayerUnused();
        else
            putOnBoard(move.getFromPos(), currPlayer);
        removeFromBoard(move.getToPos());

        if (move.getTakenPos()!=-1){
            putOnBoard(move.getTakenPos(), getNextPlayer());
            remainingPieces[getNextPlayer()]++;
        }

    }

    public boolean doesCompleteMill (int removePos, int pos, int player){

    	for (List<Integer> mills44 : MILLS){
    		if (mills44.contains(pos)){
    			boolean doesit = true;
    			for (int i : mills44){
    				if (i == removePos)
    					doesit = false;
    				else if (i != pos && statePosition[i] != player+5)
    					doesit = false;	
   			    }
    			if (doesit)
    				return true;
    		}
         
       }
        return false;
    } 

    

    public boolean isFromMill(int pos){
    	if (statePosition[pos] != 0)
    		return doesCompleteMill(-1, pos, statePosition[pos]-5);
    	return false;
    }
   

    public boolean isAllFromMill (int player){
     for (int i : statePosition){
        if (statePosition[i]==player+5 && !isFromMill(i))
            return false;
     }
     return true;
    }


    public boolean isValidMove (Move move){
        if (statePosition[move.getToPos()]!= 0)
            return false;
        if (move.getFromPos() != -1){
            if (statePosition[move.getFromPos()] != currPlayer+5)
                return false;
            if (getRemainingPieces(currPlayer) >3 && !NEIGHBOURS.get(move.getFromPos()).contains(move.getToPos()))
                return false;
            if (getUnusedPieces(currPlayer)>0){
                System.out.println("this is it");

                return false;            
            }
        }
        else{
            if (getUnusedPieces(currPlayer)==0)
                return false;
        }
        if (move.getTakenPos()!=-1){
            if (statePosition[move.getTakenPos()]!= getNextPlayer()+5)
                return false;
            if (isFromMill(move.getTakenPos()) && !isAllFromMill(getNextPlayer()))
                return false;
        }

        return true;
    }

    public void possibleTakes(List<Move> validMoves, Move move){
        for(int i=0; i<statePosition.length;i++){
            if(statePosition[i]==getNextPlayer()+5){
                if(isAllFromMill(getNextPlayer())|| !doesCompleteMill(-1,i,getNextPlayer())){
                    Move move1 = new Move(move.getFromPos(), move.getToPos(),i);
                    validMoves.add(move1);
                }
            }
        }
    }


    public List<Move> getValidMoves (){
        List<Move> validMoves = new ArrayList<Move>();

        if (getUnusedPieces(currPlayer)>0){
            for(int i=0; i<statePosition.length;i++){
                if(statePosition[i]==0){
                    Move move = new Move(i);
                    if(doesCompleteMill(-1,i,currPlayer))
                        possibleTakes(validMoves, move);
                    else
                        validMoves.add(move);
                }
            }
        }
        else{
            if(getRemainingPiecesCurrPlayer()>3){
                for(int i=0;i<statePosition.length;i++){
                    if(statePosition[i]==currPlayer+5){
                        for(int neighbour: NEIGHBOURS.get(i)){
                            if(statePosition[neighbour]==0){
                                Move move = new Move(i,neighbour);
                                if(doesCompleteMill(i,neighbour, currPlayer))
                                    possibleTakes(validMoves,move);
                                else
                                    validMoves.add(move);
                            }
                        }
                    }
                }


            }
            else{
                for(int i=0; i<statePosition.length;i++){
                    if(statePosition[i]==currPlayer+5){
                        for(int j=0;j<statePosition.length;j++){
                            if (statePosition[j]==0){
                                Move move = new Move(i,j);
                                if(doesCompleteMill(i,j, currPlayer))
                                    possibleTakes(validMoves,move);
                                else
                                    validMoves.add(move);
                            }

                        }
                    }
                }
            }
        }
        return validMoves;
        
    } 
    
    public static void main(String [] args){
        State s = new State();
        s.statePosition[15]=5;
        Move m = new Move(15,11,-1);
        boolean t = s.isValidMove(m);
        System.out.println(t);

        
    }

}