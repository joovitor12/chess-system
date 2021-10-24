package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0);
		// white pawn
		if (getColor() == Color.WHITE) {
			p.setValues(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsApiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 2, position.getColumn());
			Position p2 = new Position(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsApiece(p) && getBoard().positionExists(p2)
					&& !getBoard().thereIsApiece(p2) && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOppenentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOppenentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// specialmove enpassant white
			// right side
			if (position.getRow() == 3) {
				Position pos = new Position(position.getRow(), position.getColumn() - 1);
				if (getBoard().positionExists(pos) && isThereOppenentPiece(pos)
						&& getBoard().piece(pos) == chessMatch.getEnPassantVulnerable()) {
					mat[pos.getRow() - 1][pos.getColumn()] = true;
				}
				// left side
				Position pos2 = new Position(position.getRow(), position.getColumn() + 1);
				if (getBoard().positionExists(pos2) && isThereOppenentPiece(pos2)
						&& getBoard().piece(pos2) == chessMatch.getEnPassantVulnerable()) {
					mat[pos2.getRow() - 1][pos2.getColumn()] = true;
				}
			}

		} else {
			p.setValues(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsApiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 2, position.getColumn());
			Position p2 = new Position(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsApiece(p) && getBoard().positionExists(p2)
					&& !getBoard().thereIsApiece(p2) && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOppenentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOppenentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;

			}
			// specialmove enpassant white
			// right side
			if (position.getRow() == 4) {
				Position pos = new Position(position.getRow(), position.getColumn() - 1);
				if (getBoard().positionExists(pos) && isThereOppenentPiece(pos)
						&& getBoard().piece(pos) == chessMatch.getEnPassantVulnerable()) {
					mat[pos.getRow() + 1][pos.getColumn()] = true;
				}
				// left side
				Position pos2 = new Position(position.getRow(), position.getColumn() + 1);
				if (getBoard().positionExists(pos2) && isThereOppenentPiece(pos2)
						&& getBoard().piece(pos2) == chessMatch.getEnPassantVulnerable()) {
					mat[pos2.getRow() + 1][pos2.getColumn()] = true;
				}
			}

		}
		return mat;

	}

	@Override
	public String toString() {
		return "P";
	}
}
