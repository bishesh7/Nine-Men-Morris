import java.util.*;

public class AlphaBetaPruning{

	private State state;
	private Move bestMove;
	private int bestMoveValue;
    private State currentState;

    private static final int WIN = 1000;
    private static final int END = 1500;
    private static final int INFINITY= 1000000000;

    public AlphaBetaPruning(State state){
    	this.state = state;
    	this.currentState = null;
    	this.bestMove = null;
    	this.bestMoveValue = -INFINITY; 
    }

    public void setCurrentState(State state){
    	this.currentState = state;
    }

    public State getState(){
    	return currentState;
    }

    private int numAdjacentMoves(int player){
    	int total = 0;
    	for (int i=0; i<State.TOTAL_POS;i++){
    		if (currentState.getPositionState(i)==player+5){
    			for(int j: State.NEIGHBOURS.get(i)){
    				if(currentState.getPositionState(j)==0)
    					total++;
    			}
    		}
    	}
        return total;
    }

    private int numMills(int player){
    	int total = 0;
    	loop:
    	for(List<Integer> milli : State.MILLS){
    		for(int i : milli){
    			if(currentState.getPositionState(i)!= player+5)
    				continue loop;
    		}
    		total++;
    	}
    	return total;
    }

    private int numFormableMills(int player){
    	int total = 0;

    	for(int i=0; i<State.MILLS.size();i++){
                 int empty = -1;
                int num = 0;
                int p =0;
                for(int j : State.MILLS.get(i)){
                    
                    if(currentState.getPositionState(j)==player+5)
                        num++;
                    if (currentState.getPositionState(j)==0)
                        empty = j;

                    
                    
                }
                if (num==2 && empty!=-1){
                    for(int j : State.NEIGHBOURS.get(empty)){
                        if(currentState.getPositionState(j)==player+5)
                        p++;
                   }
                   if (currentState.getUnusedPieces(player)>0)
                    p++;
                }
                if(p>0)
                    total++;
            
            
        }   	
        return total;
    }

    private int numStoppableMills(int player){
    	int total = 0;
    	int otherplayer = (player+1)%2;

    	for(int i=0; i<State.MILLS.size();i++){
    		     int empty = -1;
                int num = 0;
                int opponent = 0;
                int t=0;
    			for(int j : State.MILLS.get(i)){
                    
    				if(currentState.getPositionState(j)==otherplayer+5)
    					num++;
                    if (currentState.getPositionState(j)==0)
                        empty = j;

                    
    				
    			}
                if (num==2 && empty!=-1){
                    for(int j : State.NEIGHBOURS.get(empty)){
                        if(currentState.getPositionState(j)==player+5)
                            opponent++;
                        if(currentState.getPositionState(j)==otherplayer+5)
                            t++;
                   }
                   if (currentState.getUnusedPieces(player)>0)
                    opponent++;
                }
    			if(opponent>0 && t>0)
    				total++;
    		
    		
    	}   	
        return total;
    }

    

    public int evaluateState(){
    	int total =0;
        total+= 10*(currentState.getRemainingPieces(0)-currentState.getRemainingPieces(1));
        total+= 8*(numMills(0)-numMills(1));
        total+= 3*(numStoppableMills(0)-numStoppableMills(1));
        total+= 1*(numFormableMills(0)-numFormableMills(1));
    	//total+= 10*(currentState.getRemainingPiecesCurrPlayer()-currentState.getRemainingPiecesNextPlayer());
    	//total+= 8*(numMills(currentState.getCurrPlayer())-numMills(currentState.getNextPlayer()));
    	//total+= 2*(numAdjacentMoves(currentState.getCurrPlayer())-numAdjacentMoves(currentState.getNextPlayer()));
    	//total+= 5*(numFormableMills(currentState.getCurrPlayer())-numFormableMills(currentState.getNextPlayer()));
    	//total+= 3*(numStoppableMills(currentState.getCurrPlayer())-numStoppableMills(currentState.getNextPlayer()));
       // if (numStoppableMills(currentState.getCurrPlayer())!=0)
      //  System.out.println(numStoppableMills(currentState.getCurrPlayer()));
        //System.out.println("cur   " + currentState.getCurrPlayer());
        //System.out.println(currentState.getRemainingPiecesCurrPlayer());
        //if (currentState.getCurrPlayer()==1)
          //  total = -total;
        //System.out.println(total);
        return total;


    }


    private int alphaBetaSearch(int alpha, int beta, int currentDepth, int remainingDepth, int player){
        

    	List<Move> validMoves = currentState.getValidMoves();

    	if (currentState.getRemainingPiecesCurrPlayer()<3){
            if (player == 0)
                return -WIN;
            else
                return WIN;
        }

    		
    	if (remainingDepth==0)
    		return evaluateState();
    	else{
    		Move nodeBestMove = null;
    		int nodeBestValue = -INFINITY;
    		for (Move move : validMoves){
    			currentState.makeMove(move);
    			int value = alphaBetaSearch(alpha, beta, currentDepth+1, remainingDepth-1, currentState.getCurrPlayer());
    			currentState.undoMove(move);
                
    			if (value == END)
    				return END;
                

                if (player ==0){
                	if (value>alpha){
                		alpha = value;
                		nodeBestMove = move;
                		if (currentDepth == 0){
    					bestMove = move;
    					bestMoveValue = alpha;
                      //System.out.println(value+" ");
                        //System.out.println(" ");
                       // System.out.println(currentState.getCurrPlayer());
    				   }
                	}	
                }

                else{
                	  if (value <beta){
                	  	beta = value;
                	  }
                }

                if (alpha>= beta)
                	break;

    		}
    		if (player ==0)
    			return alpha;
    		else
    			return beta;
    	}



    }

    public Move searchBestMove(State state){
    	setCurrentState(state);
    	alphaBetaSearch(-INFINITY,INFINITY,0,1,0);
        int a = bestMoveValue;
        Move aa = bestMove;
        alphaBetaSearch(-INFINITY,INFINITY,0,3,0);
        int b = bestMoveValue;

        if(a==b){
    
            bestMove =aa;
        }
    	return bestMove;

    }



  
}