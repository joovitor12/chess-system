package boardgame;

public class Board {
	private int rows;
	private int columns;
	private Piece[][] pieces;
	public Board(int rows, int columns) {
		if(rows < 1 || columns < 1) {
			throw new BoardException("Erro encontrado, o tabuleiro precisa ter mais de 1 linha e 1 coluna");
		}
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	//retorna a peca
	public Piece piece(int row, int column) {
		if(!positionExists(row,column)) {
			throw new BoardException("Erro encontrado, posicao nao encontrada");
		}
		return pieces[row][column];
	}
	//retorna a posicao da peca
	public Piece piece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Erro encontrado, posicao nao encontrada");
		}
		return pieces[position.getRow()][position.getColumn()];
	}
	//colocando uma peca no lugar
	public void placePiece(Piece piece, Position position) {
		if(thereIsApiece(position)) {
			throw new BoardException("Erro encontrado, a posicao " + position + " ja esta ocupada");
		}
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	public Piece removePiece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Erro encontrado, posicao nao econtrada");
		}
		if(piece(position) == null) {
			return null;
		}
		Piece aux = piece(position);
		aux.position = null;
		pieces[position.getRow()][position.getColumn()] = null;
		//aux contem a peca retirada
		return aux;
		
	}
	private boolean positionExists(int row, int column) {
		return row >= 0 && row < rows && column >= 0 && column < columns ;
		
	}
	public boolean positionExists(Position position) {
		return positionExists(position.getRow(),position.getColumn());
		
	}
	public boolean thereIsApiece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Erro encontrado, posicao nao encontrada");
		}
		return piece(position) != null;
	}
	
	
}
