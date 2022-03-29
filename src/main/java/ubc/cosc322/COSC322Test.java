
package ubc.cosc322;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.Amazon;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsBoard;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;

	private AI aiplayer = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {	
		String arg0 = "test" + Math.random();
		String arg1 = "test" + Math.random();			 
		if(args.length > 0){
			arg0 = args[0];
			arg1 = args[1];
		}
    	COSC322Test player2 = new COSC322Test(arg0, arg1);
    	
    	if(player2.getGameGUI() == null) {
    		player2.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player2.Go();
                }
            });
    	}
		

    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	//this.gamegui = new BaseGameGUI(this);

		this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
		userName = gameClient.getUserName();
		if(gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 
	
		System.out.println("Receiving message of type " + messageType);
if(messageType.equals(GameMessage.GAME_STATE_BOARD)){
  ArrayList<Integer> board = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
  getGameGUI().setGameState(board);
  this.aiplayer = new AI(board, 1);          
}

if(messageType.equals(GameMessage.GAME_ACTION_MOVE)){
  getGameGUI().updateGameState(msgDetails);
  this.aiplayer.updateGameState(msgDetails, false);
  Map<String, Object> nextMove = this.aiplayer.calculateNextMove();
  if(nextMove == null){
	System.out.println("SURRENDER");
	return false;
  }
  System.out.println(nextMove.toString());
  gameClient.sendMoveMessage(nextMove);
  this.aiplayer.updateGameState(nextMove, true);
  getGameGUI().updateGameState(nextMove);
}
   	return true;   	
    }
    
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);			
	}

 
}//end of class
