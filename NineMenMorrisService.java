import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import javax.swing.JOptionPane;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.util.StringTokenizer;


 
public class NineMenMorrisService
{
	public static void main(String[] args)
	{
	    // default port
	    int port = 8080;
	   
	    
	    
	    // parse command line arguments to override defaults
	    if (args.length > 0)
		{
		    try
			{
			    port = Integer.parseInt(args[0]);
			    
			}
		    catch (NumberFormatException ex)
			{
			    System.err.println("USAGE: java KaylesService [port]");
			    System.exit(1);
			}
		}
	    
	    // set up an HTTP server to listen on the selected port
	    try
		{
		    InetSocketAddress addr = new InetSocketAddress("localhost",port);
		    HttpServer server = HttpServer.create(addr, 1);
		    
		    server.createContext("/move.html", new MoveHandler());
		    server.createContext("/check.html", new CheckHandler());
            
		    server.start();
		}
	    catch (IOException ex)
		{
		    ex.printStackTrace(System.err);
		    System.err.println("Could not start server");
		}
	}
    
    /**
     * An HTTP handler for roll requests.
     */
    public static class MoveHandler implements HttpHandler
    {
        @Override
	    public void handle(HttpExchange ex) throws IOException
        {
            
	    
            System.err.println(ex.getRequestURI());
            String q = ex.getRequestURI().getQuery();
	    
	    StringBuilder reponse = new StringBuilder();

	    // decode the string
	    StringTokenizer tok = new StringTokenizer(q, ";");
	    if (tok.countTokens() != 9)
		{
		    sendResponse(ex, error(q, "malformed state"));
		}
	    else
		{
		    String spots = tok.nextToken();
		    try
			{
			    int player = Integer.parseInt(tok.nextToken());

			    if (player != 0 && player != 1)
				{
				    sendResponse(ex, error(q, "invalid player"));
				}
			    else
				{
                   
                    
					State boardState = new State();
					for (int i=0; i < spots.length(); i++){
    		            if(spots.charAt(i)=='.')
    		            	boardState.setPositionState(i,0);
    		            else if (spots.charAt(i)=='x')
    		            	boardState.setPositionState(i,6);
    		            else
    		            	boardState.setPositionState(i,5);
                        
    	            }
                     

    	            boardState.setCurrPlayer (player);
    	            int a =  Integer.parseInt(tok.nextToken());
    	            boardState.setRemainingPieces (0,a);
    	            int b =  Integer.parseInt(tok.nextToken());
    	            boardState.setRemainingPieces (1,b);
    	            int c =  Integer.parseInt(tok.nextToken());
    	            boardState.setUnusedPieces (0,c);
    	            int d =  Integer.parseInt(tok.nextToken());
                    boardState.setUnusedPieces (1,d);
                    
 
    	            AlphaBetaPruning prune = new AlphaBetaPruning(boardState);
    	            Move move = prune.searchBestMove(boardState);
    	            int to = move.getToPos();
    	            int from = move.getFromPos();
    	            int taken = move.getTakenPos();
                    System.out.println("from "+ move.getFromPos() + " to " + move.getToPos()+" taken "+move.getTakenPos());

				

					    // build the response object
					    Map<String, String> response = new HashMap<String, String>();
					    response.put("state", q);
					   
					    response.put("player", "" + player);
					   
					    response.put("moveto", ""+ to);
					    response.put("movefrom", ""+ from);
					    response.put("movetaken", ""+ taken);
					    
					    sendResponse(ex, response);
					
				}
			}
		    catch (NumberFormatException e)
			{
			    sendResponse(ex, error(q, "malformed player index"));
			}
		}
	}	
    }

   
    public static class CheckHandler implements HttpHandler
    {
        @Override
	    public void handle(HttpExchange ex) throws IOException
        {   
        	
            System.err.println(ex.getRequestURI());
            String q = ex.getRequestURI().getQuery();
	    StringTokenizer tok = new StringTokenizer(q, ";");
	    String spots = tok.nextToken();
        int player = Integer.parseInt(tok.nextToken());
	    State boardState = new State();
		for (int i=0; i < spots.length(); i++){
            if(spots.charAt(i)=='.')
            	boardState.setPositionState(i,0);
            else if (spots.charAt(i)=='x')
            	boardState.setPositionState(i,6);
            else
            	boardState.setPositionState(i,5);

        }

        boardState.setCurrPlayer (player);
        int a =  Integer.parseInt(tok.nextToken());
        boardState.setRemainingPieces (0,a);
        int b =  Integer.parseInt(tok.nextToken());
        boardState.setRemainingPieces (1,b);
        int c =  Integer.parseInt(tok.nextToken());
        boardState.setUnusedPieces (0,c);
        int d =  Integer.parseInt(tok.nextToken());
        boardState.setUnusedPieces (1,d);
    	int e = Integer.parseInt(tok.nextToken());
    	int f = Integer.parseInt(tok.nextToken());
    	int g = Integer.parseInt(tok.nextToken());
    	

        
    	Move thismove = new Move(e,f,g);
    	
    	System.out.println(boardState.getPositionState(thismove.getToPos()));
    	boolean valid = boardState.isValidMove(thismove);
    	   //boolean valid = true;    
    	boolean formsMill = boardState.doesCompleteMill(e,f,player);
    	String set = "";
    	if (formsMill==true){
	    	for(int i=0;i<24;i++){
	    		if (boardState.getPositionState(i)==5){
	    			if(boardState.isFromMill(i)==false)
	    				set += ""+i+"/";
	    		}
	    	}
       }
	    
	    System.out.println(valid);
	    
	    Map<String, String> response  = new HashMap<String, String>();
	    response.put("state", q);
	   response.put("isValid", ""+valid);
	   response.put ("formsMill", ""+formsMill);
	   response.put("set",""+set);
	    
	    sendResponse(ex, response);
	  }
    }



    /**
     * Returns a map containing key-value pairs for the given state and message.
     *
     * @param state a string
     * @param message a string
     * 
     * @return a map containing key-value pairs for state and message
     */
    private static Map<String, String> error(String state, String message)
    {
	Map<String, String> result = new HashMap<String, String>();

	result.put("state", state);
	result.put("message", message);

	return result;
    }
    
    /**
     * Sends a JSON object as a response in the given HTTP exchange.  Each key-value pair
     * in the given map will be copied to the JSON object.
     *
     * @param ex an HTTP exchange
     * @param info a non-empty map
     */
    private static void sendResponse(HttpExchange ex, Map<String, String> info) throws IOException
    {
	// write the response as JSON
	StringBuilder response = new StringBuilder("{");
	for (Map.Entry<String, String> e : info.entrySet())
	    {
		response.append("\"").append(e.getKey()).append("\":")
		    .append("\"").append(e.getValue()).append("\",");
	    }
	response.deleteCharAt(response.length() - 1); // remove last ,
	response.append("}"); // close JSON
	
	ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
	byte[] responseBytes = response.toString().getBytes();
	ex.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
	ex.getResponseBody().write(responseBytes);
	ex.close();
    }
}
