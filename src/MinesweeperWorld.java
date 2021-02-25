// Authored by Mara Hubelbank in April-May 2020; CS2510 @ Northeastern University.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 *                                        BOARD                                      *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// to represent a Minesweeper board, with a grid of cells and set number of mines
class Board extends World {
  int rows;
  int cols;
  int mines;
  int visibleCells;
  Random rand;
  ArrayList<ArrayList<Cell>> cells;
  ArrayList<Cell> minesList; // used when generating and revealing mines
  boolean gameOver;
  boolean win;

  // convenience constructor to be used in actual game-play (outside of testing)
  Board(int rows, int cols, int mines) {
    this(rows, cols, mines, new Random());
  }

  // constructor with Random instance given, to be called by user only in testing
  Board(int rows, int cols, int mines, Random rand) {
    if (mines > rows * cols) {
      throw new IllegalArgumentException("Cannot have more mines than cells.");
    }
    else if (rows < 2) {
      throw new IllegalArgumentException("Must have at least 2 rows in the grid.");
    }
    else if (cols < 2) {
      throw new IllegalArgumentException("Must have at least 2 columns in the grid.");
    }
    else {
      this.rows = rows;
      this.cols = cols;
      this.mines = mines;
      this.rand = rand;
      this.visibleCells = 0;

      this.initializeCells(); // add rows * cols cells to this board
      this.initializeNeighbors(); // set up all cells' neighbor links
      this.placeMines(); // randomly place mines on this board

      // initialize each cell's mine neighbor count
      for (ArrayList<Cell> row : this.cells) {
        for (Cell cell : row) {
          cell.initMineNeighbors();
        }
      }

      this.gameOver = false;
      this.win = false;
    }
  }

  // convenience constructor to be used in testing (drawing) intermediate boards
  // it is assumed that the given cells are initialized and linked
  Board(ArrayList<ArrayList<Cell>> cells) {
    this.cells = cells;
    this.rows = cells.size();
    this.cols = cells.get(0).size();

    // initialize each cell's mine neighbor count
    for (ArrayList<Cell> row : this.cells) {
      for (Cell cell : row) {
        cell.initMineNeighbors();
      }
    }

    // (we don't need other instance variables to test drawing functionality)
  }

  // initialize this board's rows * cols cells (MUTATION)
  // to be called during board initialization (from constructor)
  void initializeCells() {
    // maximum capacity of cell rows is the given row count
    this.cells = new ArrayList<ArrayList<Cell>>(this.rows);

    // initialize all cells
    for (int i = 0; i < this.rows; i++) {

      // maximum capacity of each cell row is the given col count
      ArrayList<Cell> row = new ArrayList<Cell>(this.cols);

      // add a cell to each spot in this row
      for (int j = 0; j < this.cols; j++) {
        row.add(new Cell());
      }

      // add this initialized row to the list of rows
      cells.add(row);
    }
  }

  // establish this board's cells' links to each of their neighbors
  // MUTATION: modify each cell's neighbors list
  // to be called during board initialization (from constructor)
  void initializeNeighbors() {
    /* NEIGHBOR INITIALIZATION
     * Start in top left; go along row, repeat with next row
     * Work in mini-grids of 4:
     * - top left corner cell
     * - cell on right
     * - cell below
     * - cell diagonal
     * Every time a neighbor is added to this, neighbor adds this to its neighbors
     * Outer loop: Go down rows -- stop after penultimate row
     * Inner loop: Go across column -- stop after penultimate column
     * (account for last row and last column when penultimates are reached)
     * NOTE: we need at least 2 columns/rows in the grid, so this won't error out
     */

    // initialize all cells' neighbors
    for (int i = 0; i < this.rows - 1; i++) {
      for (int j = 0; j < this.cols - 1; j++) {

        // get mini-grid of cells
        Cell topLeft = this.get(i, j);
        Cell topRight = this.get(i, j + 1);
        Cell bottomLeft = this.get(i + 1, j);
        Cell bottomRight = this.get(i + 1, j + 1);

        // addNeighbor mutates both the cell which calls it and the cell parameter
        topLeft.addNeighbor(topRight);
        topLeft.addNeighbor(bottomLeft);
        topLeft.addNeighbor(bottomRight);
        topRight.addNeighbor(bottomLeft);

        // account for the last row when on the second-to-last row
        if (i == this.rows - 2) {
          Cell left = this.get(i + 1, j);
          Cell right = this.get(i + 1, j + 1);
          left.addNeighbor(right);
        }

      }
      // account for the last column at the end of each row
      Cell top = this.get(i, this.cols - 1);
      Cell bottom = this.get(i + 1, this.cols - 1);
      top.addNeighbor(bottom);
    }
  }

  // place this board's given number of mines randomly on the board
  // MUTATION: set some of this board's cells to be mines
  // to be called during board initialization (from constructor)
  void placeMines() {
    // grid of cells to be taken from
    ArrayList<ArrayList<Cell>> nonMines = this.copyCells();

    // list of mines to add to
    this.minesList = new ArrayList<Cell>(this.mines);

    // create the given number of mines
    for (int i = 0; i < this.mines; i++) {
      int randRow = this.rand.nextInt(nonMines.size()); // generate random row in nonMines
      int randCol = this.rand.nextInt(nonMines.get(randRow).size()); // random col in row

      Cell mine = nonMines.get(randRow).get(randCol);
      mine.negMine(); // init false

      this.minesList.add(mine); // update newMines
      nonMines.get(randRow).remove(randCol); // update nonMines -- this cell is a mine now

      // if the row we took a new mine from is now empty, remove the row from nonMines
      if (nonMines.get(randRow).isEmpty()) {
        nonMines.remove(randRow);
      }
    }
  }

  // get the cell on this board at the given row and column
  Cell get(int row, int column) {
    return this.cells.get(row).get(column);
  }

  // return a duplicate list of this board's cells
  ArrayList<ArrayList<Cell>> copyCells() {
    // empty cell list to be populated
    ArrayList<ArrayList<Cell>> cellsCopy = new ArrayList<ArrayList<Cell>>(this.rows);

    // add all the cells in this board to the copy
    for (int i = 0; i < this.rows; i++) {
      ArrayList<Cell> row = new ArrayList<Cell>(this.cols);
      for (int j = 0; j < this.cols; j++) {
        row.add(this.get(i, j));
        // add every cell at row i, col j to the copy
      }
      cellsCopy.add(row);
    }

    return cellsCopy;
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
   *                                 WORLD METHODS                                     *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // provide the scene to be rendered for the Minesweeper game (draw cells with
  // grid)
  @Override
  public WorldScene makeScene() {
    // empty scene to start
    WorldScene scene = this.getEmptyScene();

    // draw the cells and add them onto the image
    for (int i = 0; i < this.rows; i++) {
      int phY = (i * IConstants.CELL_SIDE) + (IConstants.CELL_SIDE / 2);
      for (int j = 0; j < this.cols; j++) {
        int phX = (j * IConstants.CELL_SIDE) + (IConstants.CELL_SIDE / 2);
        Cell c = this.get(i, j);
        scene.placeImageXY(c.draw(), phX, phY);
      }
    }

    // draw the grid over the cells
    scene = this.drawGrid(scene);

    // if the game is over, display message and restart prompt
    if (this.gameOver) {

      // determine text box dimensions dynamically, based on this board's size
      int boxLength = Math.max((this.rows * IConstants.CELL_SIDE * 4) / 5,
          IConstants.CELL_SIDE * 2);
      int boxHeight = boxLength / 10;
      RectangleImage box = new RectangleImage(boxLength, boxHeight, OutlineMode.SOLID, Color.BLACK);
      int textSize = boxHeight / 2;

      TextImage text; // the message to be displayed

      if (this.win) {
        text = new TextImage("You won! Press enter to play again :)", textSize, FontStyle.REGULAR,
            Color.GREEN);
      }
      else {
        text = new TextImage("Game over! Press enter to play again :)", textSize, FontStyle.REGULAR,
            Color.RED);
      }

      OverlayImage textBox = new OverlayImage(text, box);
      scene.placeImageXY(textBox, this.cols * IConstants.CELL_SIDE / 2,
          this.rows * IConstants.CELL_SIDE / 2); // place text box in center of board
    }

    return scene;

  }

  // draw the grid lines on this board
  WorldScene drawGrid(WorldScene scene) {
    // horizontal line length
    int rightMostX = this.cols * IConstants.CELL_SIDE;

    // add horizontal grid lines (including borders)
    for (int i = 0; i <= this.rows; i++) {
      LineImage line = new LineImage(new Posn(rightMostX, 0), Color.BLACK);
      scene.placeImageXY(line, rightMostX / 2, i * IConstants.CELL_SIDE);
    }

    // vertical line length
    int bottomMostY = this.rows * IConstants.CELL_SIDE;

    // add vertical grid lines (including borders)
    for (int i = 0; i <= this.cols; i++) {
      LineImage line = new LineImage(new Posn(0, bottomMostY), Color.BLACK);
      scene.placeImageXY(line, i * IConstants.CELL_SIDE, bottomMostY / 2);
    }

    return scene;
  }

  // mouse click listener, to add events to the Minesweeper game
  @Override
  public void onMouseClicked(Posn pos, String buttonName) {

    // did this click event happen on this board?
    int maxX = this.cols * IConstants.CELL_SIDE;
    int maxY = this.rows * IConstants.CELL_SIDE;

    boolean onBoard = 0 <= pos.x && pos.x <= maxX && 0 <= pos.y && pos.y <= maxY;

    if (!this.gameOver && onBoard) { // track click movement only during gameplay & on board

      Cell clicked = this.getCellPos(pos);

      // left-click events happen only when a cell isn't flagged or clicked
      boolean validLeftClick = buttonName.equals("LeftButton") && !clicked.isFlagged()
          && !clicked.isClicked();

      if (validLeftClick) {
        // left-clicked mine ends game if not on first turn
        if (clicked.isMine()) {
          for (Cell mine : this.minesList) {
            mine.click(); // reveal all the mines on the board
          }
          this.endGame(false); // end this game (loss)
        }

        // left-clicked non-mine triggers flood-fill unless it's the last cell remaining
        else {
          this.clickCell(clicked);
        }
      }

      // right-click events happen only when a cell isn't clicked
      boolean validRightClick = buttonName.equals("RightButton") && !clicked.isClicked();

      if (validRightClick) {
        clicked.negFlag(); // negate this unclicked cell's flag
      }
    }
  }

  // determine which cell the user clicked, based on the mouse's position
  Cell getCellPos(Posn pos) {
    // this is linear -- nested for-loops is m*n iterations every click

    int row = 0;
    int col = 0;

    // find row
    if (pos.x == this.cols * IConstants.CELL_SIDE) {
      col = this.cols - 1; // edge case -- on right border
    }
    else {
      col = pos.x / IConstants.CELL_SIDE; // int div truncates decimal
    }

    // find column
    if (pos.y == this.rows * IConstants.CELL_SIDE) {
      row = this.rows - 1; // edge case -- on bottom border
    }
    else { // within y bounds
      row = pos.y / IConstants.CELL_SIDE; // int div truncates decimal
    }

    return this.get(row, col);
  }

  // handle the flood-fill effect when a non-mine is clicked
  // EFFECT: increment the number of cells that have been clicked,
  // and end the game if necessary
  void clickCell(Cell clicked) {
    this.visibleCells += clicked.floodFill(0);
    // if all cells are visible, end the game
    if (this.visibleCells == this.rows * this.cols - this.mines) {
      this.endGame(true); // end this game (win)
    }
  }

  // EFFECT: set this game's gameOver to true and win to the given value
  void endGame(boolean won) {
    this.gameOver = true;
    this.win = won;
  }

  // handle key events when the game is over (enter => restart)
  // EFFECT: re-initialize all board data except size and number of mines
  @Override
  public void onKeyEvent(String key) {
    // only pay attention to key presses when game is over
    if (this.gameOver) {
      if (key.equals("enter")) {
        // re-initialize starting board data
        this.visibleCells = 0;
        this.initializeCells();
        this.initializeNeighbors();
        this.placeMines();

        // initialize each cell's mine neighbor count
        for (ArrayList<Cell> row : this.cells) {
          for (Cell cell : row) {
            cell.initMineNeighbors();
          }
        }

        this.gameOver = false;
        this.win = false;
      }
      else {
        this.endOfWorld(":(");
      }
    }
  }

}

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 *                                         CELL                                      *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// to represent a cell in the Minesweeper board
class Cell {
  boolean mine;
  boolean flagged;
  boolean clicked;
  ArrayList<Cell> neighbors;
  HashMap<Integer, Color> colors; // ref. lecture 26
  int numMineNeighbors;

  Cell() {
    this.mine = false;
    this.flagged = false;
    this.clicked = false;
    this.neighbors = new ArrayList<Cell>(8); // a cell can have at most 8 neighbors
    this.initializeColors();
  }

  // initialize the integer-color HashMap to be used in setting neighbor text
  // colors when drawing this cell (MUTATION)
  // to be called during instantiation
  void initializeColors() {
    colors = new HashMap<Integer, Color>(8);
    colors.put(1, Color.BLUE);
    colors.put(2, Color.GREEN);
    colors.put(3, Color.RED);
    colors.put(4, new Color(102, 0, 153)); // purple
    colors.put(5, new Color(153, 0, 0)); // maroon
    colors.put(6, new Color(64, 224, 208)); // turquoise
    colors.put(7, Color.BLACK);
    colors.put(8, Color.LIGHT_GRAY);
  }

  // set this cell's mine value to true (MUTATION)
  // to be called during randomized mine initialization
  void negMine() {
    this.mine = !this.mine;
  }

  // negate this cell's flagged value (MUTATION)
  // to be called when this cell is (un)flagged by the user
  void negFlag() {
    this.flagged = !this.flagged;
  }

  // set this cell's clicked value to true (MUTATION)
  // to be called when this cell is clicked by the user
  void click() {
    this.clicked = true;
  }

  // link this cell to the given cell
  // MUTATION: modify this cell and given cell's neighbor lists
  // to be called during cell initialization
  void addNeighbor(Cell neighbor) {
    this.neighbors.add(neighbor);
    neighbor.neighbors.add(this);
  }

  // is this cell a mine?
  boolean isMine() {
    return this.mine;
  }

  // is this cell flagged?
  boolean isFlagged() {
    return this.flagged;
  }

  // is this cell clicked?
  boolean isClicked() {
    return this.clicked;
  }

  // EFFECT: set this cell's initial count of mine neighbors
  void initMineNeighbors() {
    this.numMineNeighbors = this.countMineNeighbors();
  }

  // how many of this cell's neighbors are mines?
  // (iterate through this cell's neighbors, count mines)
  // to be called when displaying non-mines
  int countMineNeighbors() {
    int numMines = 0;
    for (Cell c : this.neighbors) {
      numMines += c.isMine() ? 1 : 0;
    }
    return numMines;
  }

  // return the image representation of this cell
  WorldImage draw() {

    RectangleImage cell = new RectangleImage(IConstants.CELL_SIDE, IConstants.CELL_SIDE,
        OutlineMode.SOLID, Color.LIGHT_GRAY); // default cell

    // if the cell is flagged, it can't be clicked
    if (this.flagged) {
      return new OverlayImage(new EquilateralTriangleImage(13.0, OutlineMode.SOLID, Color.ORANGE),
          cell);
    }

    // is this cell's content visible?
    if (this.clicked) {
      cell.color = Color.GRAY; // change the color of this cell to a darker gray

      // if this cell is a visible mine, add a black circle
      if (this.isMine()) {
        return new OverlayImage(new CircleImage(7, OutlineMode.SOLID, Color.BLACK), cell);
      }

      // if this cell is a visible non-mine, show the num of neighbors it has
      // or just a darker-colored cell if it has no neighbors
      if (this.numMineNeighbors > 0) {
        return new OverlayImage(new TextImage(Integer.toString(this.numMineNeighbors), 16,
            FontStyle.BOLD, this.colors.get(this.numMineNeighbors)), cell);
      }
    }
    return cell; // return default cell drawing
  }

  // flood-fill the board from this cell; return the num of newly clicked cells
  int floodFill(int numClicked) {
    if (!this.isClicked() && !this.isFlagged() && !this.isMine()) { // can't reveal a flagged cell
      this.click();
      numClicked += 1;
      // if this cell has cell has no mine neighbors
      if (this.countMineNeighbors() == 0) {
        // flood fill each of its neighbors
        for (Cell c : this.neighbors) {
          numClicked += c.floodFill(0);
        }
      }
    }
    return numClicked;
  }
}

// to represent constants for the Minesweeper game
interface IConstants {
  int CELL_SIDE = 20;
}