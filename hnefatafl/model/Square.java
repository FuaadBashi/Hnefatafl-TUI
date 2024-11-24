package ws.aperture.hnefatafl.model;


import ws.aperture.hnefatafl.model.Piece;

public class Square {

    private int row;
    private int col; // chars
    private Piece piece;
    protected final Board board;

    Square(int row, int col) {
        this.row = row;
        this.col = col;
        board = null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Piece getPiece() {
        return piece;
    }

    public char getColChar() {
        return (char) (col + 'A');
    }

    @Override
    public String toString() {
        return getColChar() + String.valueOf(row + 1);
    }

    public boolean isEmpty() {
        return piece == null;
    }

    public boolean isOccupied() {
        return piece != null;
    }

    public boolean placePiece(Piece placedPiece) {
        if (this.piece == null) {
            this.piece = placedPiece;
            placedPiece.setSquare(this);
            return true;
        } else {
            return false;
        }
    }

    public void removePiece() {
        if (isOccupied()) {
            piece = null;
        }
    }

    public static boolean areAdjacent(Square sq1, Square sq2) {
        return  1 == (Math.abs(sq1.getRow() - sq2.getRow())
            + Math.abs(sq1.getCol() - sq2.getCol()));
    }

    



}
