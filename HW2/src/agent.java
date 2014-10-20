import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
class node
{
	int value;
	char[][] boardState = new char[8][8];
	int alpha, beta;
	int depth;
	node parent;
	int passValue = 0; 
	String name;
	String moveUsed;
	//ArrayList<node> children = new ArrayList<node>();
	
	node(char[][] state)
	{
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				boardState[i][j] = state[i][j];
			}
		}
	}
	node()
	{
		
	}
	node(char[][] state,int val)
	{
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				boardState[i][j] = state[i][j];
			}
		}
		
		value = val;
	}
}

public class agent
{
	static int noChange = 0;
	static char[][] finalState = new char[8][8];
	static String finalMoveMinMax = null;
	static int task;
	static char initialPlayer;
	static char initialOpponent;
	static int noMove = 0;
	static int cutoffDepth;
	static char[][] inputStateofBoard=new char[8][8];
	static int[][] positionalWeight = new int[8][8];
	static Map <Integer,Character> intToCharMap = new TreeMap<Integer,Character>();
	static Map <Character,Integer> charToIntMap = new TreeMap<Character,Integer>();

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException 
	{
		processInputFile();
		createPositionalWeight();
		integerToCharMapCreater();
		charToIntegerMapCreater();
		//greedy();
		// int x =minmax();
		alphabeta();
		 minmaxOutput();
		//greedyOutput();
		//System.out.println(charToIntMap.get('h'));
		//UNCOMMENT WHEN DONE
		/*switch(task)
		{
			case 1:
				greedy();
				break;
			case 2:
				minmax();
				minmaxOutput();
				break;
			case 3:
				alphabeta();
				break;
		}
		 */


		/*System.out.println("Task: "+ task);
		System.out.println("Player: "+ player);
		System.out.println("cutoffDepth: "+ cutoffDepth);
		int i,j;
		for (i = 0; i < 8; i++)
		{
			for(j = 0; j < 8; j++)
			{
				System.out.print(inputStateofBoard[i][j]);
			}
			System.out.println();
		}*/
	}

	/*Task 1*/
	static void greedy() throws FileNotFoundException, UnsupportedEncodingException
	{
		ArrayList<String> possibleMove = predictMoves(inputStateofBoard,initialPlayer,initialOpponent);
		char[][] finalBoardState = new char[8][8];
		
		if(possibleMove.isEmpty())
		{
			noMove = 1;
		}
		else
		{
			int value = Integer.MIN_VALUE;
			int tempValue;
			String finalmove = null;
			for(String s:possibleMove)
			{
				char[][] tempBoard = new char[8][8];

				char[] charArray = s.toCharArray();
				int j = charToIntMap.get(charArray[0]);
				int i = (Character.getNumericValue(charArray[1])) - 1;
				tempBoard = flip(inputStateofBoard,i,j,initialPlayer,initialOpponent);
				tempValue = evaluate(tempBoard,initialPlayer,initialOpponent);

				/*Finding the best move*/
				if(tempValue > value)
				{
					value = tempValue;
					finalmove = s;
				}
				else if(tempValue == value)
				{
					char[] charArrayFinal = finalmove.toCharArray();
					if(Character.getNumericValue(charArray[1]) < Character.getNumericValue(charArrayFinal[1]))
					{
						finalmove = s;
						value = tempValue;
					}
				}
			}
			char[] charArrayFinal = finalmove.toCharArray();
			int j = charToIntMap.get(charArrayFinal[0]);
			int i = (Character.getNumericValue(charArrayFinal[1])) - 1;
			finalBoardState = flip(inputStateofBoard,i,j,initialPlayer,initialOpponent);
			for (int x = 0; x < 8; x++)
			{
				for(int y = 0; y < 8; y++)
				{
					System.out.print(finalBoardState[x][y]);
				}
				System.out.println();
			}
		}
		
		if(noMove == 1)
		{
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			for(int indexI = 0; indexI < 8 ; indexI++)
			{
				for(int indexJ = 0; indexJ < 8 ; indexJ++)
				{
					writer.print(inputStateofBoard[indexI][indexJ]);
				}
				if(indexI != 7)
					writer.println();
			}
			writer.close();
		}
		else
		{
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			for(int indexI = 0; indexI < 8 ; indexI++)
			{
				for(int indexJ = 0; indexJ < 8 ; indexJ++)
				{
					writer.print(finalBoardState[indexI][indexJ]);
				}
				if(indexI != 7)
					writer.println();
			}
			writer.close();
		}
	}
	
	/*Task 2*/
	static int minmax()
	{
		
		
		node board = new node(inputStateofBoard,Integer.MIN_VALUE);
		board.depth = 0;
		board.parent = null;
		board.value = Integer.MIN_VALUE;
		board.name = "root";
		//max(root,initialPlayer,initialOpponent);
		char player = initialPlayer;
		char opponent = initialOpponent;
		board.value = Integer.MIN_VALUE;
		if(board.depth == cutoffDepth)
		{
			noChange = 1;
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value);
			return board.value;
		}
		
		if(gameEndCheck(board.boardState) == 1)
		{
			noChange = 1;
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value);
			
			return board.value;
		}
		ArrayList<String> moves = new ArrayList<String>();
		moves = predictMoves(board.boardState,player,opponent);
		if(moves.size() == 0)
		{
			if(board.passValue == 0)
			{
				//noChange = 1;
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.parent = board;
				pass.passValue = 1;
				//pass.value = Integer.MIN_VALUE;
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				board.value = Math.max(board.value, min(pass,opponent,player));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
			}
			if(board.passValue == 1)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.passValue = board.passValue + 1;
				pass.parent = board;
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				board.value = Math.max(board.value, min(pass,opponent,player));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
			}
			if(board.passValue == 2)
			{
				board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
			}
		}
		else
		{
			int initialValue = board.value;
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value);
			/*log for board comes here*/
			for(String move:moves)
			{
				
				node newnode = new node();
				newnode.parent = board;
				newnode.depth = board.depth + 1;
				newnode.name = move;
				char[] charArrayFinal = move.toCharArray();
				int j = charToIntMap.get(charArrayFinal[0]);
				int i = (Character.getNumericValue(charArrayFinal[1])) - 1;
				newnode.boardState = flip(board.boardState,i,j,player,opponent);
				
				board.value = Math.max(board.value, min(newnode,opponent,player));
				if(board.value != initialValue)
				{
					finalMoveMinMax = move;
				}
					value = getValue(board.value);
					System.out.println(board.name + ","+ board.depth + "," + value);
					initialValue = board.value;

			}
			
			
			
		}
		
		return 0;

		
	}
	
	static int min(node board,char player, char opponent)
	{
		board.value = Integer.MAX_VALUE;
		if(board.depth == cutoffDepth)
		{
			
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value);
			return board.value;
		}
		if(gameEndCheck(board.boardState) == 1)
		{
			
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value );
			return board.value;
		}
		ArrayList<String> moves = new ArrayList<String>();
		moves = predictMoves(board.boardState,player,opponent);
		
		if(moves.size() == 0)
		{
			if(board.passValue == 0)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.parent = board;
				pass.passValue = 1;
				
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				board.value = Math.min(board.value, max(pass,opponent,player));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
			}
			if(board.passValue == 1)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.passValue = board.passValue + 1;
				pass.parent = board;
				
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				board.value = Math.min(board.value, max(pass,opponent,player));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
				
			}
			if(board.passValue == 2)
			{
				
				board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
			}
		}
		else
		{
			int initialValue = board.value;
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value);
			
			for(String move:moves)
			{
				
				node newnode = new node();
				newnode.parent = board;
				newnode.depth = board.depth + 1;
				newnode.name = move;
				char[] charArrayFinal = move.toCharArray();
				int j = charToIntMap.get(charArrayFinal[0]);
				int i = (Character.getNumericValue(charArrayFinal[1])) - 1;
				newnode.boardState = flip(board.boardState,i,j,player,opponent);
				
				board.value = Math.min(board.value, max(newnode,opponent,player));
			
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				initialValue = board.value;
				
			}
			
			return board.value;
		}
		
		return board.value;
		
	}
	
	static int max(node board, char player, char opponent)
	{
		board.value = Integer.MIN_VALUE;
		if(board.depth == cutoffDepth)
		{
			
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value);
			return board.value;
		}
		
		if(gameEndCheck(board.boardState) == 1)
		{
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value);
			return board.value;
		}
		ArrayList<String> moves = new ArrayList<String>();
		moves = predictMoves(board.boardState,player,opponent);
		if(moves.size() == 0)
		{
			if(board.passValue == 0)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.parent = board;
				pass.passValue = 1;
				
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				board.value = Math.max(board.value, min(pass,opponent,player));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
			}
			if(board.passValue == 1)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.passValue = board.passValue + 1;
				pass.parent = board;
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				board.value = Math.max(board.value, min(pass,opponent,player));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
			}
			if(board.passValue == 2)
			{
				
				board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				return board.value;
			}
		}
		else
		{
			int initialValue = board.value;
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value);
			
			for(String move:moves)
			{
				
				node newnode = new node();
				newnode.parent = board;
				newnode.depth = board.depth + 1;
				newnode.name = move;
				char[] charArrayFinal = move.toCharArray();
				int j = charToIntMap.get(charArrayFinal[0]);
				int i = (Character.getNumericValue(charArrayFinal[1])) - 1;
				newnode.boardState = flip(board.boardState,i,j,player,opponent);
				
				board.value = Math.max(board.value, min(newnode,opponent,player));
			
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value);
				initialValue = board.value;
				
			}
			
			return board.value;
		}
		
		return board.value;
	}
	
	static void minmaxOutput()
	{
		if(noChange == 1)
		{
			for(int indexI = 0; indexI < 8 ; indexI++)
			{
				for(int indexJ = 0; indexJ < 8 ; indexJ++)
				{
					System.out.print(inputStateofBoard[indexI][indexJ]);
				}
				if(indexI != 7)
					System.out.println();
			}
		}
		else
		{
			if(finalMoveMinMax == null)
			{
				for(int indexI = 0; indexI < 8 ; indexI++)
				{
					for(int indexJ = 0; indexJ < 8 ; indexJ++)
					{
						System.out.print(inputStateofBoard[indexI][indexJ]);
					}
					if(indexI != 7)
						System.out.println();
				}
			}
			else
			{
				char[] charArrayFinal = finalMoveMinMax.toCharArray();
				int j = charToIntMap.get(charArrayFinal[0]);
				int i = (Character.getNumericValue(charArrayFinal[1])) - 1;
				finalState = flip(inputStateofBoard,i,j,initialPlayer,initialOpponent);
				for(int indexI = 0; indexI < 8 ; indexI++)
				{
					for(int indexJ = 0; indexJ < 8 ; indexJ++)
					{
						System.out.print(finalState[indexI][indexJ]);
					}
					if(indexI != 7)
						System.out.println();
				}
			}
			
		}
		
	}
	/*Task 3*/
	/*------------------------------------*/
	static int alphabeta()
	{
		
		
		node board = new node(inputStateofBoard,Integer.MIN_VALUE);
		board.depth = 0;
		board.parent = null;
		board.value = Integer.MIN_VALUE;
		board.name = "root";
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		//max(root,initialPlayer,initialOpponent);
		char player = initialPlayer;
		char opponent = initialOpponent;
		board.value = Integer.MIN_VALUE;
		if(board.depth == cutoffDepth)
		{
			noChange = 1;
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			return board.value;
		}
		
		if(gameEndCheck(board.boardState) == 1)
		{
			noChange = 1;
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			
			return board.value;
		}
		ArrayList<String> moves = new ArrayList<String>();
		moves = predictMoves(board.boardState,player,opponent);
		if(moves.size() == 0)
		{
			if(board.passValue == 0)
			{
				//noChange = 1;
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.parent = board;
				pass.passValue = 1;
				//pass.value = Integer.MIN_VALUE;
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				board.value = Math.max(board.value, minalpha(pass, opponent, player, alpha, beta));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
			}
			if(board.passValue == 1)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.passValue = board.passValue + 1;
				pass.parent = board;
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				board.value = Math.max(board.value, minalpha(pass, opponent, player, alpha, beta));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
			}
			if(board.passValue == 2)
			{
				board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
			}
		}
		else
		{
			int initialValue = board.value;
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			
			for(String move:moves)
			{
				
				node newnode = new node();
				newnode.parent = board;
				newnode.depth = board.depth + 1;
				newnode.name = move;
				char[] charArrayFinal = move.toCharArray();
				int j = charToIntMap.get(charArrayFinal[0]);
				int i = (Character.getNumericValue(charArrayFinal[1])) - 1;
				newnode.boardState = flip(board.boardState,i,j,player,opponent);
				
				board.value = Math.max(board.value, minalpha(newnode,opponent,player,alpha,beta));
				if(board.value != initialValue)
				{
					finalMoveMinMax = move;
				}
				if(board.value >= beta)
				{
					value = getValue(board.value);
					System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
					return board.value;
				}
				alpha = Math.max(alpha, board.value);
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				initialValue = board.value;

			}
			//return board.value;
			
			
		}
		
		return 0;

		
	}
	
	static int minalpha(node board,char player, char opponent,int alpha,int beta)
	{
		board.value = Integer.MAX_VALUE;
		if(board.depth == cutoffDepth)
		{
			
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			return board.value;
		}
		if(gameEndCheck(board.boardState) == 1)
		{
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			return board.value;
		}
		ArrayList<String> moves = new ArrayList<String>();
		moves = predictMoves(board.boardState,player,opponent);
		
		if(moves.size() == 0)
		{
			if(board.passValue == 0)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.parent = board;
				pass.passValue = 1;
				
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				board.value = Math.min(board.value, maxalpha(pass,opponent,player,alpha,beta));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
			}
			if(board.passValue == 1)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.passValue = board.passValue + 1;
				pass.parent = board;
				
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				board.value = Math.min(board.value, maxalpha(pass,opponent,player,alpha,beta));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
				
			}
			if(board.passValue == 2)
			{
				board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
			}
		}
		else
		{
			int initialValue = board.value;
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			
			for(String move:moves)
			{
				
				node newnode = new node();
				newnode.parent = board;
				newnode.depth = board.depth + 1;
				newnode.name = move;
				char[] charArrayFinal = move.toCharArray();
				int j = charToIntMap.get(charArrayFinal[0]);
				int i = (Character.getNumericValue(charArrayFinal[1])) - 1;
				newnode.boardState = flip(board.boardState,i,j,player,opponent);
				
				board.value = Math.min(board.value, maxalpha(newnode,opponent,player,alpha,beta));
				if(board.value <= alpha)
				{
					value = getValue(board.value);
					System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
					initialValue = board.value;
					return board.value;
				}
				beta = Math.min(beta, board.value);
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				initialValue = board.value;
			}
			
			return board.value;
		}
		
		return board.value;
		
	}
	
	static int maxalpha(node board, char player, char opponent,int alpha,int beta)
	{
		board.value = Integer.MIN_VALUE;
		if(board.depth == cutoffDepth)
		{
			
				board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			return board.value;
		}
		
		if(gameEndCheck(board.boardState) == 1)
		{
			board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			return board.value;
		}
		ArrayList<String> moves = new ArrayList<String>();
		moves = predictMoves(board.boardState,player,opponent);
		if(moves.size() == 0)
		{
			if(board.passValue == 0)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.parent = board;
				pass.passValue = 1;
				//pass.value = Integer.MIN_VALUE;
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				board.value = Math.max(board.value, minalpha(pass,opponent,player,alpha,beta));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
			}
			if(board.passValue == 1)
			{
				node pass = new node(board.boardState);
				pass.depth = board.depth + 1;
				pass.name = "pass";
				pass.passValue = board.passValue + 1;
				pass.parent = board;
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				board.value = Math.max(board.value, minalpha(pass,opponent,player,alpha,beta));
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
			}
			if(board.passValue == 2)
			{
				board.value = evaluate(board.boardState,initialPlayer,initialOpponent);
				String value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				return board.value;
			}
		}
		else
		{
			int initialValue = board.value;
			String value = getValue(board.value);
			System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
			
			for(String move:moves)
			{
				
				node newnode = new node();
				newnode.parent = board;
				newnode.depth = board.depth + 1;
				newnode.name = move;
				char[] charArrayFinal = move.toCharArray();
				int j = charToIntMap.get(charArrayFinal[0]);
				int i = (Character.getNumericValue(charArrayFinal[1])) - 1;
				newnode.boardState = flip(board.boardState,i,j,player,opponent);
				
				board.value = Math.max(board.value, minalpha(newnode,opponent,player,alpha,beta));
				if(board.value >= beta)
				{
					value = getValue(board.value);
					System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
					return board.value;
				}
				alpha = Math.max(alpha, board.value);
				
				value = getValue(board.value);
				System.out.println(board.name + ","+ board.depth + "," + value + "," + getValue(alpha) + "," + getValue(beta));
				initialValue = board.value;
				
			}
			
			
			return board.value;
		}
		
		return board.value;
	}
	
	/*------------------------------------*/
	static String getValue(int value)
	{
		String sValue;
		if(value == Integer.MIN_VALUE)
		{
			sValue = "-Infinity";
		}
		else if(value == Integer.MAX_VALUE)
		{
			sValue = "Infinity";
		}
		else
		{
			sValue = Integer.toString(value);
		}
		
		return sValue;
	}
	
	static int gameEndCheck(char[][] boardState)
	{
		int X = 0;
		int O = 0;
		int freeSpace = 0;
		int gameEnd = 0;
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				if(boardState[i][j] == 'X')
				{
					X++;
				}
				if(boardState[i][j] == 'O')
				{
					O++;
				}
				if(boardState[i][j] == '*')
				{
					freeSpace++;
				}
				
			}
		}
		
		if(freeSpace > 0 && X > 0 && O > 0)
		{
			return gameEnd;
		}
		else
		{
			gameEnd = 1;
			return gameEnd;
		}
	}
	
	static char[][] flip(char[][] stateofBoard,int i,int j,char player,char opponent)
	{
		char[][] tempBoard = new char[8][8];
		for(int indexI = 0; indexI < 8 ; indexI++)
		{
			for(int indexJ = 0; indexJ < 8 ; indexJ++)
			{
				if(indexI == i && indexJ == j)
				{
					if(stateofBoard[i][j] == '*')
						tempBoard[i][j] = player;
					else
						System.out.println("Something is wrong");
				}
				else
					tempBoard[indexI][indexJ] = stateofBoard[indexI][indexJ];
			}
		}

		if(tempBoard[i][j] == player)
		{
			int found = 0;
			int index = 1;
			/* 1 */
			if((j+index) < 8)
			{
				if(tempBoard[i][j+index] == opponent)
				{
					index++;
					if((j+index) < 8)
					{
						while(tempBoard[i][j+index] == opponent || tempBoard[i][j+index] == player)
						{
							if(tempBoard[i][j+index] == player)
							{
								found = 1;
								break;
							}

							index++;
							if((j+index) >= 8)
								break;
						}
					}
				}
				if(found == 1)
				{  
					for(int z = 1; z < index; z++)
					{
						tempBoard[i][j+z] = player;
					}
				}
			}
			/* 2 */
			found = 0;
			index = 1;
			if((j-index) >= 0)
			{
				if(tempBoard[i][j-index] == opponent)
				{
					index++;
					if((j-index) >= 0)
					{
						while(tempBoard[i][j-index] == opponent || tempBoard[i][j-index] == player)
						{
							if(tempBoard[i][j-index] == player)
							{
								found = 1;
								break;
							}
							index++;
							if((j-index) < 0)
								break;
						}
					}
				}
				if(found == 1)
				{
					for(int z = 1; z < index; z++)
					{
						tempBoard[i][j-z] = player;
					}
				}
			}
			/* 3 */
			found = 0;
			index = 1;
			if((i+index) < 8)
			{
				if(tempBoard[i+index][j] == opponent)
				{
					index++;
					if((i+index) < 8)
					{
						while(tempBoard[i+index][j] == opponent || tempBoard[i+index][j] == player)
						{
							if(tempBoard[i+index][j] == player)
							{
								found = 1;
								break;
							}
							index++;
							if((i+index) >= 8)
								break;
						}
					}
				}
				if(found == 1)
				{
					for(int z = 1; z < index; z++)
					{
						tempBoard[i+z][j] = player;
					}
				}
			}
			/* 4 */
			found = 0;
			index = 1;
			if((i-index) >= 0)
			{
				if(tempBoard[i-index][j] == opponent)
				{
					index++;
					if((i-index) >= 0)
					{
						while(tempBoard[i-index][j] == opponent || tempBoard[i-index][j] == player)
						{
							if(tempBoard[i-index][j] == player)
							{
								found = 1;
								break;
							}
							index++;
							if((i-index) <0)
								break;
						}
					}
				}
				if(found == 1)
				{
					for(int z = 1; z < index; z++)
					{
						tempBoard[i-z][j] = player;
					}
				}
			}
			/* 5 */
			found = 0;
			index = 1;
			if((i+index) < 8 && (j+index) < 8)
			{
				if(tempBoard[i+index][j+index] == opponent)
				{
					index++;
					if((i+index) <8 && (j+index) < 8)
					{
						while(tempBoard[i+index][j+index] == opponent || tempBoard[i+index][j+index] == player)
						{   
							if(tempBoard[i+index][j+index] == player)
							{
								found = 1;
								break;
							}
							index++;
							if((i+index) >= 8 || (j+index) >= 8)
								break;
						}
					}
				}
				if(found == 1)
				{
					for(int z = 1;z < index; z++)
					{
						tempBoard[i+z][j+z] = player;
					}
				}
			}
			/* 6 */
			found = 0;
			index = 1;
			if((i-index) >= 0 && (j-index) >= 0)
			{
				if(tempBoard[i-index][j-index] == opponent)
				{
					index++;
					if((i-index) >= 0 && (j-index) >= 0)
					{
						while(tempBoard[i-index][j-index] == opponent || tempBoard[i-index][j-index] == player)
						{
							if(tempBoard[i-index][j-index] == player)
							{
								found = 1;
								break;
							}
							index++;
							if((i-index) < 0 || (j-index) < 0)
								break;
						}
					}
				}
				if(found == 1)
				{
					for(int z = 1; z < index; z++)
					{
						tempBoard[i-z][j-z] = player;
					}
				}
			}
			/* 7 */
			found = 0;
			index = 1;
			if((i+index) < 8 && (j-index) >= 0)
			{
				if(tempBoard[i+index][j-index] == opponent)
				{
					index++;
					if((i+index) <8 && (j-index) >= 0)
					{
						while(tempBoard[i+index][j-index] == opponent || tempBoard[i+index][j-index] == player)
						{   
							if(tempBoard[i+index][j-index] == player)
							{
								found = 1;
								break;
							}
							index++;
							if((i+index) >= 8 || (j-index) < 0)
								break;
						}
					}
				}
				if(found == 1)
				{
					for(int z = 1; z < index; z++)
					{
						tempBoard[i+z][j-z] = player;
					}
				}
			}
			/* 8 */
			found = 0;
			index = 1;
			if((i-index) >= 0 && (j+index) < 8)
			{
				if(tempBoard[i-index][j+index] == opponent)
				{
					index++;
					if((i-index) >= 0 && (j+index) < 8)
					{
						while(tempBoard[i-index][j+index] == opponent || tempBoard[i-index][j+index] == player)
						{
							if(tempBoard[i-index][j+index] == player)
							{
								found = 1;
								break;
							}
							index++;
							if((i-index) < 0 || (j+index) >= 8)
								break;
						}
					}
				}
				if(found == 1)
				{
					for(int z = 1; z < index; z++)
					{
						tempBoard[i-z][j+z] = player;
					}
				}
			}
		}
		return tempBoard;

	}

	static int evaluate(char[][] tempBoard,char player,char opponent)
	{
		int playerWeight = 0;
		int opponentWeight = 0;

		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(tempBoard[i][j] == player)
					playerWeight = playerWeight + positionalWeight[i][j];
				else if(tempBoard[i][j] == opponent)
					opponentWeight = opponentWeight + positionalWeight[i][j];
			}
		}

		return (playerWeight - opponentWeight);
	}
	static void charToIntegerMapCreater()
	{
		int i=0;
		int j=97;
		char c = 'a';
		while(i<8)
		{
			//System.out.println("intToCharMap");
			charToIntMap.put(c,i);
			i++;
			c++;
		}
	}

	static void integerToCharMapCreater()
	{
		int i=0;
		int j=97;
		while(i<8)
		{
			//System.out.println("intToCharMap");
			intToCharMap.put(i, (char)j);
			i++;
			j++;
		}
	}
	static ArrayList<String> findMove(char[][] board,int i,int j,char player,char opponent)
	{   
		//System.out.println("Going ");
		int found,index;
		String move;
		ArrayList<String> possibleMove=new ArrayList<String>();
		/* 1 */
		found=0;
		index=1;
		if(j+index < 8)  
		{
			if(board[i][j+index] == opponent)    
			{
				//System.out.println("Position: "+(i+1)+(j+1));
				index++;
				if(j+index < 8)
				{
					while(board[i][j+index] == opponent|| board[i][j+index] == player) 
					{
						if(board[i][j+index] == player)  
						{
							found=1;
							break;
						}
						index++;
						if(j+index>=8)
							break;
					}
				}
			}
			if(found==1)
			{
				//System.out.println("Found: "+(i+1)+(j+1));
				move=intToCharMap.get(j)+Integer.toString(i+1);
				if(!possibleMove.contains(move))
					possibleMove.add(move);
			}
		}
		/* 2 */
		found=0;
		index=1;
		if(j-index >= 0)   
		{
			if(board[i][j-index] == opponent)    
			{
				index++;
				if((j-index) >= 0)
				{
					while(board[i][j-index] == opponent|| board[i][j-index] == player)  
					{
						if(board[i][j-index] == player)  
						{
							found=1;
							break;
						}
						index++;
						if(j-index<0)
							break;
					}
				}
			}
			if(found==1)
			{
				//System.out.println("Found: "+(i+1)+(j+1));
				move=intToCharMap.get(j)+Integer.toString(i+1);
				if(!possibleMove.contains(move))
					possibleMove.add(move);
			}
		}
		/* 3 */
		found=0;
		index=1;
		if(i+index<8)   
		{
			if(board[i+index][j] == opponent)  
			{
				index++;
				if(i+index<8)
				{
					while(board[i+index][j] == opponent || board[i+index][j] == player)  
					{
						if(board[i+index][j] == player)  
						{
							found=1;
							break;
						}
						index++;
						if(i+index>=8)
							break;
					}
				}
			}
			if(found==1)
			{
				//System.out.println("Found: "+(i+1)+(j+1));
				move=intToCharMap.get(j)+Integer.toString(i+1);
				if(!possibleMove.contains(move))
					possibleMove.add(move);
			}
		}
		/* 4 */
		found=0;
		index=1;
		if(i-index>=0)   
		{
			if(board[i-index][j] == opponent)   
			{
				index++;
				if(i-index>=0)
				{
					while(board[i-index][j] == opponent || board[i-index][j] == player)  
					{
						if(board[i-index][j] == player)  
						{
							found=1;
							break;
						}
						index++;
						if(i-index<0)
							break;
					}
				}
			}
			if(found==1)
			{
				move=intToCharMap.get(j)+Integer.toString(i+1);
				if(!possibleMove.contains(move))
					possibleMove.add(move);
			}
		}
		/* 5 */
		found=0;
		index=1;
		if(i+index<8 && j+index<8)   
		{
			if(board[i+index][j+index] == opponent)    
			{
				index++;
				if((i+index)<8 && (j+index)<8)
				{
					while(board[i+index][j+index] == opponent || board[i+index][j+index] == player)   
					{   
						if(board[i+index][j+index] == player)   
						{
							found=1;
							break;
						}
						index++;
						if((i+index)>=8 || (j+index)>=8)
							break;
					}
				}
			}
			if(found==1)
			{
				move=intToCharMap.get(j)+Integer.toString(i+1);
				if(!possibleMove.contains(move))
					possibleMove.add(move);
			}
		}
		/* 6 */
		found=0;
		index=1;
		if(i-index>=0 && j-index>=0)   
		{
			if(board[i-index][j-index] == opponent)   
			{
				index++;
				if((i-index)>=0 && (j-index)>=0)
				{
					while(board[i-index][j-index] == opponent || board[i-index][j-index] == player)  
					{
						if(board[i-index][j-index] == player)   
						{
							found=1;
							break;
						}
						index++;
						if((i-index)<0 || (j-index)<0)
							break;
					}
				}
			}
			if(found==1)
			{
				move=intToCharMap.get(j)+Integer.toString(i+1);
				if(!possibleMove.contains(move))
					possibleMove.add(move);
			}
		}
		/* 7 */
		found=0;
		index=1;
		if(i+index<8 && j-index >= 0)  
		{
			if(board[i+index][j-index] == opponent)    
			{
				index++;
				if((i+index) < 8 && (j-index) >= 0)
				{
					while(board[i+index][j-index] == opponent || board[i+index][j-index] == player)  
					{   
						if(board[i+index][j-index] == player)    
						{
							found=1;
							break;
						}

						index++;
						if((i+index)>=8 || (j-index)<0)
							break;
					}
				}
			}
			if(found==1)
			{
				move=intToCharMap.get(j)+Integer.toString(i+1);
				if(!possibleMove.contains(move))
					possibleMove.add(move);
			}

		}


		/*8*/
		found=0;
		index=1;
		if(i-index>=0 && j+index<8)  
		{
			if(board[i-index][j+index] == opponent) 
			{
				index++;
				if((i-index)>=0 && (j+index)<8)
				{
					while(board[i-index][j+index] == opponent|| board[i-index][j+index] == player) 
					{
						if(board[i-index][j+index] == player)    
						{
							found=1;
							break;
						}
						index++;

						if((i-index)<0 || (j+index)>=8)
							break;
					}
				}
			}
			if(found==1)
			{
				move=intToCharMap.get(j)+Integer.toString(i+1);
				if(!possibleMove.contains(move))
					possibleMove.add(move);
			}
		}
		return possibleMove;
	}
	static ArrayList<String> predictMoves(char[][] board ,char player ,char opponent)
	{
		ArrayList<String> moves = new ArrayList<String>();
		//System.out.println("Going ");
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(board[i][j]=='*')
				{

					for(String s:findMove(board,i,j,player,opponent))
					{
						if(!moves.contains(s))
							moves.add(s);

					}
				}
			}
		}

		return moves;
	}
	/*Creates a hard coded positional weight matrix*/
	static void createPositionalWeight()
	{
		positionalWeight[0][0]=positionalWeight[0][7]=positionalWeight[7][0]=positionalWeight[7][7]=99;
		positionalWeight[0][1]=positionalWeight[0][6]=positionalWeight[1][0]=positionalWeight[1][7]=positionalWeight[6][0]=positionalWeight[6][7]=positionalWeight[7][1]=positionalWeight[7][6]=-8;
		positionalWeight[0][2]=positionalWeight[0][5]=positionalWeight[2][0]=positionalWeight[2][7]=positionalWeight[5][0]=positionalWeight[5][7]=positionalWeight[7][2]=positionalWeight[7][5]=8;
		positionalWeight[0][3]=positionalWeight[0][4]=positionalWeight[3][0]=positionalWeight[3][7]=positionalWeight[4][0]=positionalWeight[4][7]=positionalWeight[7][3]=positionalWeight[7][4]=6;
		positionalWeight[1][1]=positionalWeight[1][6]=positionalWeight[6][1]=positionalWeight[6][6]=-24;
		positionalWeight[1][2]=positionalWeight[1][5]=positionalWeight[2][1]=positionalWeight[2][6]=positionalWeight[5][1]=positionalWeight[5][6]=positionalWeight[6][2]=positionalWeight[6][5]=-4;
		positionalWeight[1][3]=positionalWeight[1][4]=positionalWeight[3][1]=positionalWeight[3][6]=positionalWeight[4][1]=positionalWeight[4][6]=positionalWeight[6][3]=positionalWeight[6][4]=-3;
		positionalWeight[2][2]=positionalWeight[2][5]=positionalWeight[5][2]=positionalWeight[5][5]=7;
		positionalWeight[2][3]=positionalWeight[2][4]=positionalWeight[3][2]=positionalWeight[3][5]=positionalWeight[4][2]=positionalWeight[4][5]=positionalWeight[5][3]=positionalWeight[5][4]=4;
		positionalWeight[3][3]=positionalWeight[3][4]=positionalWeight[4][3]=positionalWeight[4][4]=0;
	}
	/*Process Input file*/
	static void processInputFile()
	{
		BufferedReader br;
		try 
		{
			br= new BufferedReader( new FileReader("input.txt"));
			int count = 0;
			String line;
			if(count == 0)
			{
				line = br.readLine();
				task = Integer.parseInt(line);
				count++;
			}
			if(count == 1)
			{
				line = br.readLine();
				//initialPlayer = line;
				char[] charArray = line.toCharArray();

				if(charArray[0] == 'X')
				{
					initialPlayer = 'X';
					initialOpponent = 'O';
					//System.out.println("Assigned"+ initialOpponent+ initialPlayer);

				}
				else if(charArray[0] == 'O')
				{
					initialPlayer = 'O';
					initialOpponent = 'X';
				}
				count++;
			}
			if(count == 2)
			{
				line = br.readLine();
				cutoffDepth = Integer.parseInt(line);
				count++;
			}
			if(count == 3)
			{
				int i = 0,j = 0;
				while ((line = br.readLine()) != null) {
					char[] charArray = line.toCharArray();
					j=0;
					while(j<charArray.length)
					{
						inputStateofBoard[i][j]=charArray[j];
						j++;
					}
					i++;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}
