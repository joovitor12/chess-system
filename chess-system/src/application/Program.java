package application;


import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();
		
		while(!chessMatch.getCheckMate()) {
			try {
			UI.clearScreen();
			UI.printMatch(chessMatch, captured);
			System.out.println();
			System.out.print("Source: ");
			ChessPosition src = UI.readChessPosition(in);
			boolean [][] possibleMoves = chessMatch.possibleMoves(src);
			UI.printBoard(chessMatch.getPieces(), possibleMoves);
			System.out.println();
			System.out.print("Target: ");
			ChessPosition target = UI.readChessPosition(in);
			ChessPiece capturedPiece = chessMatch.performChessMove(src, target);
			
			if(capturedPiece != null) {
				captured.add(capturedPiece);
			}
			if(chessMatch.getPromoted() != null) {
				System.out.print("Enter piece for promotion (B/N/R/Q): ");
				String type = in.nextLine().toUpperCase();
				while(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
					System.out.print("Invalid value!\nEnter piece for promotion (B/N/R/Q): ");
					type = in.nextLine().toUpperCase();
				}
				chessMatch.replacePromotedPiece(type);
			}
			
			}
			catch(ChessException e) {
				System.out.println(e.getMessage());
				
				
			}
			
			
			
		}
		UI.clearScreen();
		UI.printMatch(chessMatch, captured);
		
	}

}
