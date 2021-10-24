package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check = false;
	private boolean checkMate = false;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}

	public boolean getCheck() {
		return check;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	public boolean getCheckMate() {
		return checkMate;
	}
	

	public int getTurn() {
		return turn;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
	}

	

	public ChessPiece[][] getPieces(){
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for(int i = 0; i < board.getRows(); i++) {
			for(int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i,j);
			}
		}
		return mat;
	}
	public boolean[][] possibleMoves(ChessPosition src){
		Position pos = src.toPosition();
		validateSourcePosition(pos);
		return board.piece(pos).possibleMoves();
	}
	
		
	
	public ChessPiece performChessMove(ChessPosition sourcePos, ChessPosition targetPos) {
		Position src = sourcePos.toPosition();
		Position target = targetPos.toPosition();
		validateSourcePosition(src);
		validateTargetPosition(src, target);
		Piece capturedPiece = makeMove(src, target);
		
		if(testCheck(currentPlayer)) {
			undoMove(src,target,capturedPiece);
			throw new ChessException("Voce nao pode se sabotar!");
		}
		
		ChessPiece movedPiece = (ChessPiece) board.piece(target);
		// specialmove promotion
		promoted = null;
		if(movedPiece instanceof Pawn) {
			if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0 || movedPiece.getColor() == Color.BLACK && target.getRow() == 7 )) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		if(testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}else {
			nextTurn();
		}
		// specialmove enpassant
		if(movedPiece instanceof Pawn && (target.getRow() == src.getRow() - 2 || target.getRow() == src.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		}
		else {
			enPassantVulnerable = null;
		}
		
		//nextTurn();
		return (ChessPiece) capturedPiece;
	}
	
	
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("Nao existe peca a ser promovida");
		}
		if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return promoted; //retorna uma Rainha, que foi definida por default
		}
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		return newPiece;
		
		
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if(type.equals("B")) return new Bishop(board, color);
		if(type.equals("N")) return new Knight(board, color);
		if(type.equals("R")) return new Rook(board, color);
		return new Queen(board, color);
	}
	
	private void undoMove(Position src, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, src);
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		// specialmove castling kingside rook
				if(p instanceof King && target.getColumn() == src.getColumn() + 2) {
					Position srcT = new Position(src.getRow(), src.getColumn() + 3);
					Position targetT = new Position(src.getRow(), src.getColumn() + 1);
					ChessPiece rook = (ChessPiece)board.removePiece(targetT);
					board.placePiece(rook, srcT);
					rook.decreaseMoveCount();
							

				}
				// specialmove castling queenside rook
				if(p instanceof King && target.getColumn() == src.getColumn() - 2) {
					Position srcT = new Position(src.getRow(), src.getColumn() - 4);
					Position targetT = new Position(src.getRow(), src.getColumn() - 1);
					ChessPiece rook = (ChessPiece)board.removePiece(targetT);
					board.placePiece(rook, srcT);
					rook.decreaseMoveCount();
							

				}
				//specialmove enpassant
				if(p instanceof Pawn) {
					if(src.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
						ChessPiece pawn = (ChessPiece)board.removePiece(target);
						Position pawnPosition;
						if(p.getColor() == Color.WHITE) {
							pawnPosition = new Position(3, target.getColumn());
						}else {
							pawnPosition = new Position(4, target.getColumn());
						}
						board.placePiece(pawn, pawnPosition);
						
					
						
					}
				}
	}
	private Piece makeMove(Position src, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(src);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
		// specialmove castling kingside rook
		if(p instanceof King && target.getColumn() == src.getColumn() + 2) {
			Position srcT = new Position(src.getRow(), src.getColumn() + 3);
			Position targetT = new Position(src.getRow(), src.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(srcT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
					

		}
		// specialmove castling queenside rook
		if(p instanceof King && target.getColumn() == src.getColumn() - 2) {
			Position srcT = new Position(src.getRow(), src.getColumn() - 4);
			Position targetT = new Position(src.getRow(), src.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(srcT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
					

		}
		
		//specialmove enpassant
		if(p instanceof Pawn) {
			if(src.getColumn() != target.getColumn() && capturedPiece == null) {
				Position ppos;
				if(p.getColor() == Color.WHITE) {
					ppos = new Position(target.getRow() + 1, target.getColumn());
				}else {
					ppos = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(ppos);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	private void validateSourcePosition(Position position) {
		if(!board.thereIsApiece(position)) {
			throw new ChessException("Posicao fonte nao encontrada");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("A peca escolhida nao faz parte do seu exercito");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("Nao ha movimentos possiveis para a peca escolhida");
		}
	}
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private void validateTargetPosition (Position src, Position target) {
		if(!board.piece(src).possibleMove(target)) {
			throw new ChessException("Erro encontrado, a peca nao pode se mover para a coordenada final ");
		}
	}
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE; 
	}
	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			if(p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("Nao ha rei da cor " + color + " no tabuleiro");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces =  piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for(Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if(mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i=0; i<board.getRows(); i++) {
				for (int j=0; j<board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}	
	
	//trocando as coordenadas da matriz pela do xadrez
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	//metodo responsavel para por uma peca na sua posicao inicial
	private void initialSetup() {
		
		placeNewPiece('A', 1, new Rook(board, Color.WHITE));
        placeNewPiece('B', 1, new Knight(board, Color.WHITE));
        placeNewPiece('C', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('D', 1, new Queen(board, Color.WHITE));
        placeNewPiece('E', 1, new King(board, Color.WHITE, this));
        placeNewPiece('F', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('G', 1, new Knight(board, Color.WHITE));
        placeNewPiece('H', 1, new Rook(board, Color.WHITE));
        placeNewPiece('A', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('B', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('C', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('D', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('E', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('F', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('G', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('H', 2, new Pawn(board, Color.WHITE, this));
        
        
        placeNewPiece('A', 8, new Rook(board, Color.BLACK));
        placeNewPiece('B', 8, new Knight(board, Color.BLACK));
        placeNewPiece('C', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('D', 8, new Queen(board, Color.BLACK));
        placeNewPiece('E', 8, new King(board, Color.BLACK, this));
        placeNewPiece('F', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('G', 8, new Knight(board, Color.BLACK));
        placeNewPiece('H', 8, new Rook(board, Color.BLACK));
        placeNewPiece('A', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('B', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('C', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('D', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('E', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('F', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('G', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('H', 7, new Pawn(board, Color.BLACK, this));
        
		
	}
	
	
}
