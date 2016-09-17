package fbird;

public class Cell {

    private int x;
    private int y;
    private double valueOfCell;

    public Cell() {
    }

    public Cell(final int x, final int y, final double valueOfCell) {
        this.x = x;
        this.y = y;
        this.valueOfCell = valueOfCell;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getValueOfCell() {
        return valueOfCell;
    }

    public void setValueOfCell(double valueOfCell) {
        this.valueOfCell = valueOfCell;
    }
}
