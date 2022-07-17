package com.backtracking;

public class Sudoku {

	/*
	 * Remember we are finding solution only once that's why no exponential
	 * Time Complexity: O(9(n ^ 2)), in the worst case, for each cell in the n2 board, we have 9 possible numbers.
	 * 
	 * Space Complexity: O(1), since we are refilling the given board itself, there is no extra space required, so constant space complexity.
	 */
	public static void main(String[] args) {
		char[][] board = { { '9', '5', '7', '.', '1', '3', '.', '8', '4' },
				{ '4', '8', '3', '.', '5', '7', '1', '.', '6' }, { '.', '1', '2', '.', '4', '9', '5', '3', '7' },
				{ '1', '7', '.', '3', '.', '4', '9', '.', '2' }, { '5', '.', '4', '9', '7', '.', '3', '6', '.' },
				{ '3', '.', '9', '5', '.', '8', '7', '.', '1' }, { '8', '4', '5', '7', '9', '.', '6', '1', '3' },
				{ '.', '9', '1', '.', '3', '6', '.', '7', '5' }, { '7', '.', '6', '1', '8', '5', '4', '.', '9' } };
		
		Sudoku sudoku = new Sudoku();
		sudoku.solve(board);

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print(board[i][j] + " ");
			System.out.println();
		}
	}
    
    boolean solve(char[][] board){
        
        for(int i=0;i<board.length;i++){
            for(int j =0;j<board.length;j++){
                
                if(board[i][j] == '.'){
                    
                    for(char c= '1';c <= '9';c++){
                        
                        if(isValid(board,c,i,j)){
                            board[i][j] = c;
                            
                            if(solve(board) == true){
                                return true;
                            }else{
                                board[i][j] = '.';
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
    
    boolean isValid(char[][] board,char c,int row,int col){
        for(int i=0;i<9;i++){
            
            if(board[i][col] == c){
                return false;
            }
             
            if(board[row][i] == c){
                return false;
            }
            
            if(board[3 * (row/3) + i/3 ][3*(col/3) + i%3] == c){
                return false;
            }
        }
        return true;
    }
}
