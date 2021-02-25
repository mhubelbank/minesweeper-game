import java.awt.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;

import javalib.worldimages.*;

//to run the Minesweeper game
class MinesweeperTests {

  Cell cellUnflaggedUnclicked; // default cell
  Cell cellUnflaggedClicked;
  Cell cellFlaggedUnclicked;

  Cell mineUnflaggedUnclicked;
  Cell mineUnflaggedClicked;
  Cell mineFlaggedUnclicked;

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
   *                                     CELL TESTS                                    *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // reset all cells' data
  void initCellConditions() {
    this.cellUnflaggedUnclicked = new Cell(); // default cell

    this.cellUnflaggedClicked = new Cell();
    this.cellUnflaggedClicked.click();

    this.cellFlaggedUnclicked = new Cell();
    this.cellFlaggedUnclicked.negFlag();

    this.mineUnflaggedUnclicked = new Cell();
    this.mineUnflaggedUnclicked.negMine();

    this.mineUnflaggedClicked = new Cell();
    this.mineUnflaggedClicked.negMine();
    this.mineUnflaggedClicked.click();

    this.mineFlaggedUnclicked = new Cell();
    this.mineFlaggedUnclicked.negMine();
    this.mineFlaggedUnclicked.negFlag();
    
    this.cellUnflaggedUnclicked.initMineNeighbors();
    this.cellUnflaggedClicked.initMineNeighbors();
    this.cellFlaggedUnclicked.initMineNeighbors();

    this.mineUnflaggedUnclicked.initMineNeighbors();
    this.mineUnflaggedClicked.initMineNeighbors();
    this.mineFlaggedUnclicked.initMineNeighbors();
  }

  // test the initializeColors method for the Cell class
  // purpose: insert 8 int-color pairs into the colors HashMap
  void testInitializeColors(Tester t) {
    // initializeColors is called in the constructor
    this.initCellConditions();

    t.checkExpect(this.cellUnflaggedUnclicked.colors.size(), 8);
    t.checkExpect(this.cellUnflaggedUnclicked.colors.get(1), Color.BLUE);
    t.checkExpect(this.cellUnflaggedUnclicked.colors.get(3), Color.RED);
    t.checkExpect(this.cellUnflaggedUnclicked.colors.get(0), null);
    t.checkExpect(this.cellUnflaggedUnclicked.colors.containsKey(2), true);
    t.checkExpect(this.cellUnflaggedUnclicked.colors.containsKey(9), false);
  }

  // test the negMine method for the Cell class
  // purpose: negate cell's mine boolean
  void testNegMine(Tester t) {
    // negMine is called in the constructor
    this.initCellConditions();

    t.checkExpect(this.mineFlaggedUnclicked.mine, true);
    t.checkExpect(this.mineUnflaggedClicked.mine, true);
    t.checkExpect(this.cellUnflaggedUnclicked.mine, false); // default cell
    this.cellUnflaggedUnclicked.negMine();
    t.checkExpect(this.cellUnflaggedUnclicked.mine, true); // now a mine
  }

  // test the negFlag method for the Cell class
  // purpose: negate cell's flagged boolean
  void testNegFlag(Tester t) {
    this.initCellConditions();

    t.checkExpect(this.cellUnflaggedUnclicked.flagged, false); // default cell
    t.checkExpect(this.mineUnflaggedClicked.flagged, false);
    t.checkExpect(this.mineFlaggedUnclicked.flagged, true);
    this.mineFlaggedUnclicked.negFlag();
    t.checkExpect(this.mineFlaggedUnclicked.flagged, false);
  }

  // test the click method for the Cell class
  // purpose: set cell's clicked boolean to true
  void testClick(Tester t) {
    this.initCellConditions();

    t.checkExpect(this.cellUnflaggedUnclicked.clicked, false); // default cell
    t.checkExpect(this.mineUnflaggedClicked.clicked, true);
    t.checkExpect(this.cellUnflaggedClicked.clicked, true);
    t.checkExpect(this.mineFlaggedUnclicked.clicked, false);
    this.mineFlaggedUnclicked.click();
    t.checkExpect(this.mineFlaggedUnclicked.clicked, true);
  }

  // test the addNeighbor method for the Cell class
  // purpose: establish two-way links between two cells
  void testAddNeighbor(Tester t) {
    this.initCellConditions();

    t.checkExpect(this.mineFlaggedUnclicked.neighbors.size(), 0);
    t.checkExpect(this.mineFlaggedUnclicked.neighbors, new ArrayList<Cell>(8));

    this.mineFlaggedUnclicked.addNeighbor(this.cellFlaggedUnclicked);
    t.checkExpect(this.mineFlaggedUnclicked.neighbors.size(), 1);
    t.checkExpect(this.mineFlaggedUnclicked.neighbors.get(0), this.cellFlaggedUnclicked);
    t.checkExpect(this.cellFlaggedUnclicked.neighbors.size(), 1);
    t.checkExpect(this.cellFlaggedUnclicked.neighbors.get(0), this.mineFlaggedUnclicked);

    this.mineFlaggedUnclicked.addNeighbor(this.cellUnflaggedClicked);
    t.checkExpect(this.mineFlaggedUnclicked.neighbors.size(), 2);
    t.checkExpect(this.mineFlaggedUnclicked.neighbors.get(1), this.cellUnflaggedClicked);
    t.checkExpect(this.cellUnflaggedClicked.neighbors.size(), 1);
    t.checkExpect(this.cellUnflaggedClicked.neighbors.get(0), this.mineFlaggedUnclicked);
  }

  // test the isMine method for the Cell class
  // purpose: return cell's mine boolean
  void testIsMine(Tester t) {
    this.initCellConditions();
    t.checkExpect(this.cellFlaggedUnclicked.isMine(), this.cellFlaggedUnclicked.mine);
    t.checkExpect(this.mineFlaggedUnclicked.isMine(), this.mineFlaggedUnclicked.mine);
  }

  // test the isFlagged method for the Cell class
  // purpose: return cell's flagged boolean
  void testIsFlagged(Tester t) {
    this.initCellConditions();
    t.checkExpect(this.cellFlaggedUnclicked.isFlagged(), this.cellFlaggedUnclicked.flagged);
    t.checkExpect(this.mineFlaggedUnclicked.isFlagged(), this.mineFlaggedUnclicked.flagged);
  }

  // test the isClicked method for the Cell class
  // purpose: return cell's clicked boolean
  void testIsClicked(Tester t) {
    this.initCellConditions();
    t.checkExpect(this.cellFlaggedUnclicked.isClicked(), this.cellFlaggedUnclicked.clicked);
    t.checkExpect(this.cellUnflaggedClicked.isClicked(), this.cellUnflaggedClicked.clicked);
  }

  // test the countMineNeighbors method for the Cell class
  // purpose: iterate through neighbors list, count mines
  void testCountMineNeighbors(Tester t) {
    this.initCellConditions();

    this.mineFlaggedUnclicked.addNeighbor(this.cellFlaggedUnclicked);
    this.mineFlaggedUnclicked.addNeighbor(this.cellUnflaggedClicked);
    t.checkExpect(this.mineFlaggedUnclicked.countMineNeighbors(), 0);
    t.checkExpect(this.cellFlaggedUnclicked.countMineNeighbors(), 1);
    t.checkExpect(this.cellUnflaggedClicked.countMineNeighbors(), 1);

    this.cellUnflaggedUnclicked.addNeighbor(this.mineUnflaggedClicked);
    this.cellUnflaggedUnclicked.addNeighbor(this.mineUnflaggedUnclicked);
    t.checkExpect(this.cellUnflaggedUnclicked.countMineNeighbors(), 2);
  }

  // test the initMineNeighbors for the Cell data
  // purpose: call countMineNeighbors() on the given cell
  void testInitMineNeighbors(Tester t) {
    this.initCellConditions();

    this.mineFlaggedUnclicked.addNeighbor(this.cellFlaggedUnclicked);
    this.mineFlaggedUnclicked.addNeighbor(this.cellUnflaggedClicked);

    this.mineFlaggedUnclicked.initMineNeighbors();
    this.cellFlaggedUnclicked.initMineNeighbors();
    this.cellUnflaggedClicked.initMineNeighbors();

    t.checkExpect(this.mineFlaggedUnclicked.numMineNeighbors, 0);
    t.checkExpect(this.cellFlaggedUnclicked.numMineNeighbors, 1);
    t.checkExpect(this.cellUnflaggedClicked.numMineNeighbors, 1);

    this.cellUnflaggedUnclicked.addNeighbor(this.mineUnflaggedClicked);
    this.cellUnflaggedUnclicked.addNeighbor(this.mineUnflaggedUnclicked);

    this.cellUnflaggedUnclicked.initMineNeighbors();

    t.checkExpect(this.cellUnflaggedUnclicked.numMineNeighbors, 2);
  }

  // test the draw method for the Cell class
  // purpose: represent cell as an image
  void testDraw(Tester t) {
    // default cell (initial cell drawing)
    RectangleImage drawnUnflaggedUnclicked = new RectangleImage(IConstants.CELL_SIDE,
        IConstants.CELL_SIDE, OutlineMode.SOLID, Color.LIGHT_GRAY);

    // cell or mine with flag (cannot be clicked)
    OverlayImage drawnFlagged = new OverlayImage(
        new EquilateralTriangleImage(13.0, OutlineMode.SOLID, Color.ORANGE),
        drawnUnflaggedUnclicked);

    // base for clicked cells (also clicked 0-neighbor cell)
    RectangleImage drawnClicked = new RectangleImage(IConstants.CELL_SIDE, IConstants.CELL_SIDE,
        OutlineMode.SOLID, Color.GRAY);

    // clicked mine
    OverlayImage drawnClickedMine = new OverlayImage(
        new CircleImage(7, OutlineMode.SOLID, Color.BLACK), drawnClicked);

    // clicked cell with 1 mine neighbor
    OverlayImage drawnClickedCellN1 = new OverlayImage(
        new TextImage(Integer.toString(1), 16, FontStyle.BOLD, Color.BLUE), drawnClicked);

    // clicked cell with 2 mine neighbors
    OverlayImage drawnClickedCellN2 = new OverlayImage(
        new TextImage(Integer.toString(2), 16, FontStyle.BOLD, Color.GREEN), drawnClicked);

    this.initCellConditions();
    t.checkExpect(this.cellUnflaggedUnclicked.draw(), drawnUnflaggedUnclicked);
    t.checkExpect(this.mineUnflaggedUnclicked.draw(), drawnUnflaggedUnclicked);
    t.checkExpect(this.cellFlaggedUnclicked.draw(), drawnFlagged);
    t.checkExpect(this.mineFlaggedUnclicked.draw(), drawnFlagged);
    t.checkExpect(this.mineUnflaggedClicked.draw(), drawnClickedMine);
    t.checkExpect(this.cellUnflaggedClicked.draw(), drawnClicked); // 0 mine neighbors
    this.cellUnflaggedClicked.addNeighbor(this.mineUnflaggedUnclicked); // add mine neighbor
    this.cellUnflaggedClicked.initMineNeighbors();
    t.checkExpect(this.cellUnflaggedClicked.draw(), drawnClickedCellN1); // 1 mine neighbor
    this.cellUnflaggedClicked.addNeighbor(this.mineFlaggedUnclicked); // add another mine neighbor
    this.cellUnflaggedClicked.initMineNeighbors();
    t.checkExpect(this.cellUnflaggedClicked.draw(), drawnClickedCellN2); // 2 mine neighbors
  }

  // test the floodFill method for the Cell class
  // purpose: flood-fill the board, return the number of cells that were clicked
  void testFloodFill(Tester t) {
    this.initCellConditions();

    // can't flood fill from a flagged or already-clicked cell
    t.checkExpect(this.cellFlaggedUnclicked.floodFill(0), 0);
    t.checkExpect(this.cellUnflaggedClicked.floodFill(1), 1);

    // flood-fill with no neighbors
    t.checkExpect(this.cellUnflaggedUnclicked.isClicked(), false);
    t.checkExpect(this.cellUnflaggedUnclicked.floodFill(1), 2);
    t.checkExpect(this.cellUnflaggedUnclicked.isClicked(), true);

    // flood-fill with neighbors
    this.initCellConditions(); // set CUU to non-clicked
    Cell neighbor = new Cell();
    this.cellUnflaggedUnclicked.addNeighbor(neighbor);
    t.checkExpect(this.cellUnflaggedUnclicked.neighbors.size(), 1);
    t.checkExpect(this.cellUnflaggedUnclicked.floodFill(0), 2);
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
   *                                     BOARD TESTS                                   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // test the Board constructor exceptions
  void testConstructorExceptions(Tester t) {
    ArrayList<Posn> posns = new ArrayList<Posn>(Arrays.asList(new Posn(1,1)));
    System.out.println(posns.contains(new Posn(1, 1)));
    t.checkConstructorException(new IllegalArgumentException("Cannot have more mines than cells."),
        "Board", 2, 2, 5);
    t.checkConstructorException(
        new IllegalArgumentException("Must have at least 2 rows in the grid."), "Board", 1, 2, 1);
    t.checkConstructorException(
        new IllegalArgumentException("Must have at least 2 columns in the grid."), "Board", 2, 1,
        1);
  }

  Board board;
  Board boardWithMine;

  Cell cell00;
  Cell cell01;
  Cell cell10;
  Cell cell11;

  void initBoardConditions() {
    this.board = new Board(2, 2, 0); // no mines -- prevent randomness
    this.boardWithMine = new Board(2, 2, 1, new Random(1)); // 1 mine; given random seed

    this.cell00 = new Cell();
    this.cell01 = new Cell();
    this.cell10 = new Cell();
    this.cell11 = new Cell();

    // 6 combinations (4 choose 2)
    this.cell00.addNeighbor(this.cell01); // top left <-> top right
    this.cell00.addNeighbor(this.cell10); // top left <-> bottom left
    this.cell00.addNeighbor(this.cell11); // top left <-> bottom right
    this.cell01.addNeighbor(this.cell10); // top right <-> bottom left
    this.cell01.addNeighbor(this.cell11); // top right <-> bottom right
    this.cell10.addNeighbor(this.cell11); // bottom left <-> bottom right
    
    this.cell00.initMineNeighbors();
    this.cell01.initMineNeighbors();
    this.cell10.initMineNeighbors();
    this.cell11.initMineNeighbors();
  }

  // test the get method for the Board data
  // purpose: access specific cells by row and column
  void testGet(Tester t) {
    this.initBoardConditions();

    t.checkExpect(this.board.get(0, 0), this.board.cells.get(0).get(0));
    t.checkExpect(this.boardWithMine.get(1, 1), this.boardWithMine.cells.get(1).get(1));
  }

  // test the copyCells method for the Board data
  // purpose: copy board's cells list
  void testCopyCells(Tester t) {
    this.initBoardConditions();

    t.checkExpect(this.board.copyCells(), this.board.cells);
    t.checkExpect(this.boardWithMine.copyCells(), this.boardWithMine.cells);
  }

  // test the initializeCells method for the Board data
  // purpose: populate the cells list with cell data
  void testInitializeCells(Tester t) {
    // initializeCells is called in the constructor
    this.initBoardConditions();

    t.checkExpect(this.board.cells.size(), 2); // 2 rows of cells
    t.checkExpect(this.board.cells.get(0).size(), 2); // 2 cols of cells
    t.checkExpect(this.board.cells.get(1).size(), 2); // 2 cols of cells
    t.checkExpect(this.board.get(0, 0).isMine(), false); // no mines on board
  }

  // test the initializeNeighbors method for the Board data
  // purpose: set up the links between each cell
  void testInitializeNeighbors(Tester t) {
    this.initBoardConditions();

    t.checkExpect(this.board.get(0, 0).neighbors,
        new ArrayList<Cell>(Arrays.asList(this.cell01, this.cell10, this.cell11)));
    t.checkExpect(this.board.get(0, 1).neighbors,
        new ArrayList<Cell>(Arrays.asList(this.cell00, this.cell10, this.cell11)));
    t.checkExpect(this.board.get(1, 0).neighbors,
        new ArrayList<Cell>(Arrays.asList(this.cell00, this.cell01, this.cell11)));
    t.checkExpect(this.board.get(1, 1).neighbors,
        new ArrayList<Cell>(Arrays.asList(this.cell00, this.cell01, this.cell10)));

    t.checkExpect(this.board.get(0, 0), this.cell00);
    t.checkExpect(this.board.get(0, 1), this.cell01);
    t.checkExpect(this.board.get(1, 0), this.cell10);
    t.checkExpect(this.board.get(1, 1), this.cell11);
  }

  // test the placeMines method for the Board data
  // purpose: randomly place this board's given number of mines
  void testPlaceMines(Tester t) {
    // placeMines is called in the constructor
    this.initBoardConditions();

    Random randTest = new Random(1);
    int randRow = randTest.nextInt(2);
    int randCol = randTest.nextInt(2);

    // check that there was a mine placed at the randomly generated position
    t.checkExpect(this.boardWithMine.get(randRow, randCol).isMine(), true);

    // check that minesList was updated
    t.checkExpect(this.boardWithMine.minesList.size(), 1);
    t.checkExpect(this.boardWithMine.minesList.get(0).isMine(), true);
  }

  // WORLD METHODS ------------------------------------------------------------

  // test the world functionality of Minesweeper
  void testGo(Tester t) {
    // create Minesweeper world - 20 x 20, 80 mines
    Board board = new Board(20, 20, 50);

    /*
    for (ArrayList<Cell> cells : board.cells) {
      for (Cell cell : cells) {
        cell.click(); // show the contents of each cell
      }
    }   
    */

    board.bigBang(400, 400);
  }

  // test the drawGrid method for the Board class
  // purpose: draw a grid on a given scene, based on board's dimensions
  void testDrawGrid(Tester t) {
    this.initBoardConditions();

    WorldScene scene = new WorldScene(40, 40);
    WorldScene gridScene = new WorldScene(40, 40);

    // draw horizontal grid lines
    LineImage horizLine = new LineImage(new Posn(40, 0), Color.BLACK); // (cols * 20, 0)
    gridScene.placeImageXY(horizLine, 20, 0); // border
    gridScene.placeImageXY(horizLine, 20, 20); // mid
    gridScene.placeImageXY(horizLine, 20, 40); // border

    // draw vertical grid lines (cell side length = 20)
    LineImage vertLine = new LineImage(new Posn(0, 40), Color.BLACK); // (0, cols * 20)
    gridScene.placeImageXY(vertLine, 0, 20); // border
    gridScene.placeImageXY(vertLine, 20, 20); // mid
    gridScene.placeImageXY(vertLine, 40, 20); // border

    t.checkExpect(this.board.drawGrid(scene), gridScene); // 2x2 grid on 2x2 board
    t.checkExpect(this.boardWithMine.drawGrid(scene), gridScene);
  }

  // test the makeScene method for the Board class
  // purpose: draw the cells in the right positions, and place a grid on top
  void testMakeScene(Tester t) {
    this.initBoardConditions();

    /* Key:
     * CUU: cellUnflaggedUnclicked
     * CUC: cellUnflaggedClicked
     * CFU: cellFlaggedUnclicked
     * MUU: mineUnflaggedUnclicked
     * MUC: mineUnflaggedClicked
     * MFU: mineFlaggedUnclicked
     * C = cell
     * F = flagged
     * M = mine
     */

    this.initCellConditions(); // reset cell conditions for board 1

    /* Board 1 Layout:
     * CUU CUC
     * MUU MFU
     */

    ArrayList<Cell> row1 = new ArrayList<Cell>(
        Arrays.asList(this.cellUnflaggedUnclicked, this.cellUnflaggedClicked));
    ArrayList<Cell> row2 = new ArrayList<Cell>(
        Arrays.asList(this.mineUnflaggedUnclicked, this.mineFlaggedUnclicked));
    ArrayList<ArrayList<Cell>> cells1 = new ArrayList<ArrayList<Cell>>(Arrays.asList(row1, row2));
    Board board1 = new Board(cells1);
    WorldScene scene1 = board1.getEmptyScene();

    // initialize neighbors
    this.cellUnflaggedUnclicked.addNeighbor(this.cellUnflaggedClicked); // TL <-> TR
    this.cellUnflaggedUnclicked.addNeighbor(this.mineUnflaggedUnclicked); // TL <-> BL
    this.cellUnflaggedUnclicked.addNeighbor(this.mineFlaggedUnclicked); // TL <-> BR
    this.cellUnflaggedClicked.addNeighbor(this.mineUnflaggedUnclicked); // TR <-> BL
    this.cellUnflaggedClicked.addNeighbor(this.mineFlaggedUnclicked); // TR <-> BR
    // BL <-> BR
    this.mineUnflaggedUnclicked.addNeighbor(this.mineFlaggedUnclicked);

    /* Board 1 Drawing:
     * 2 2
     * C F
     */

    // x = (column * IConstants.CELL_SIDE) + (IConstants.CELL_SIDE / 2)
    // y = (row * IConstants.CELL_SIDE) + (IConstants.CELL_SIDE / 2)
    scene1.placeImageXY(this.cellUnflaggedUnclicked.draw(), 10, 10); // TL
    scene1.placeImageXY(this.cellUnflaggedClicked.draw(), 30, 10); // TR
    scene1.placeImageXY(this.mineUnflaggedUnclicked.draw(), 10, 30); // BL
    scene1.placeImageXY(this.mineFlaggedUnclicked.draw(), 30, 30); // BR

    scene1 = board1.drawGrid(scene1);

    t.checkExpect(board1.makeScene(), scene1); // intermediate board test

    // test end game board drawing -- win (all non-mines clicked)
    this.mineFlaggedUnclicked.negFlag(); // unflag the last cell
    this.mineFlaggedUnclicked.click(); // click the last cell

    RectangleImage box = new RectangleImage(40, 4, OutlineMode.SOLID, Color.BLACK);
    TextImage winText = new TextImage("You won! Press enter to play again :)", 2, FontStyle.REGULAR,
        Color.GREEN);
    OverlayImage textBox = new OverlayImage(winText, box);
    scene1.placeImageXY(textBox, 20, 20);

    t.checkExpect(board1.makeScene(), scene1);

    this.initCellConditions(); // reset cell conditions for board 2

    /* Board 2 Layout:
     * CUU CUC
     * MUC CFU
     */

    ArrayList<Cell> row1B2 = new ArrayList<Cell>(
        Arrays.asList(this.cellUnflaggedUnclicked, this.cellUnflaggedClicked));
    ArrayList<Cell> row2B2 = new ArrayList<Cell>(
        Arrays.asList(this.mineUnflaggedClicked, this.cellFlaggedUnclicked));
    ArrayList<ArrayList<Cell>> cells2 = new ArrayList<ArrayList<Cell>>(
        Arrays.asList(row1B2, row2B2));
    Board board2 = new Board(cells2);
    WorldScene scene2 = board2.getEmptyScene();

    // initialize neighbors
    this.cellUnflaggedUnclicked.addNeighbor(this.cellUnflaggedClicked); // TL <-> TR
    this.cellUnflaggedUnclicked.addNeighbor(this.mineUnflaggedClicked); // TL <-> BL
    this.cellUnflaggedUnclicked.addNeighbor(this.cellFlaggedUnclicked); // TL <-> BR
    this.cellUnflaggedClicked.addNeighbor(this.mineUnflaggedClicked); // TR <-> BL
    this.cellUnflaggedClicked.addNeighbor(this.cellFlaggedUnclicked); // TR <-> BR
    this.mineUnflaggedUnclicked.addNeighbor(this.cellFlaggedUnclicked); // BL <-> BR

    /* Board 2 Drawing:
     * C 1
     * M F
     */

    // x = (column * IConstants.CELL_SIDE) + (IConstants.CELL_SIDE / 2)
    // y = (row * IConstants.CELL_SIDE) + (IConstants.CELL_SIDE / 2)
    scene1.placeImageXY(this.cellUnflaggedUnclicked.draw(), 10, 10); // TL
    scene1.placeImageXY(this.cellUnflaggedClicked.draw(), 30, 10); // TR
    scene1.placeImageXY(this.mineUnflaggedClicked.draw(), 10, 30); // BL
    scene1.placeImageXY(this.cellFlaggedUnclicked.draw(), 30, 30); // BR

    scene2 = board2.drawGrid(scene2);

    t.checkExpect(board2.makeScene(), scene2); // intermediate board test

    // test end game board drawing -- loss (mine clicked)
    RectangleImage box2 = new RectangleImage(40, 4, OutlineMode.SOLID, Color.BLACK);
    TextImage loseText = new TextImage("Game over! Press enter to play again :)", 2,
        FontStyle.REGULAR, Color.RED);
    OverlayImage textBox2 = new OverlayImage(loseText, box2);
    scene2.placeImageXY(textBox2, 20, 20);

    t.checkExpect(board2.makeScene(), scene2);

  }

  // test the clickCell method for the Board class
  void testClickCell(Tester t) {
    this.initBoardConditions();

    // 0 mine neighbors => flood-fill
    this.board.clickCell(this.board.get(0, 0));
    t.checkExpect(this.board.get(0, 0).isClicked(), true);
    t.checkExpect(this.board.get(0, 1).isClicked(), true);
    t.checkExpect(this.board.get(1, 0).isClicked(), true);
    t.checkExpect(this.board.get(1, 1).isClicked(), true);
    t.checkExpect(this.board.visibleCells, 4);
    t.checkExpect(this.board.gameOver, true);
    t.checkExpect(this.board.win, true);

    Board board3x3 = new Board(3, 3, 0);
    
    board3x3.get(0, 0).negMine();

    // 1 mine neighbor => no flood-fill
    board3x3.clickCell(board3x3.get(0, 1));
    t.checkExpect(board3x3.get(0, 0).isClicked(), false); // mine isn't shown
    t.checkExpect(board3x3.get(0, 1).isClicked(), true);
    t.checkExpect(board3x3.visibleCells, 1);
    t.checkExpect(board3x3.win, false);
  }

  // test the endGame method for the Board class
  // purpose: mutate the board's endgame booleans
  void testEndGame(Tester t) {
    Board board1 = new Board(2, 2, 2);
    Board board2 = new Board(2, 2, 2);

    // init conditions
    t.checkExpect(board1.gameOver, false);
    t.checkExpect(board2.win, false);

    board1.endGame(false);
    board2.endGame(true);

    t.checkExpect(board1.gameOver, true);
    t.checkExpect(board2.gameOver, true);
    t.checkExpect(board1.win, false);
    t.checkExpect(board2.win, true);
  }

  // test the getCellPos method for the Board class
  // purpose: get the cell on the board at the supplied position
  void testGetCellPos(Tester t) {
    this.initBoardConditions();

    t.checkExpect(this.board.getCellPos(new Posn(0, 0)), this.cell00);
    t.checkExpect(this.board.getCellPos(new Posn(19, 19)), this.cell00);

    t.checkExpect(this.board.getCellPos(new Posn(20, 0)), this.cell01);
    t.checkExpect(this.board.getCellPos(new Posn(39, 39)), this.cell01);

    t.checkExpect(this.board.getCellPos(new Posn(0, 20)), this.cell10);
    t.checkExpect(this.board.getCellPos(new Posn(39, 39)), this.cell10);

    t.checkExpect(this.board.getCellPos(new Posn(20, 20)), this.cell11);
    t.checkExpect(this.board.getCellPos(new Posn(39, 39)), this.cell11);
    t.checkExpect(this.board.getCellPos(new Posn(40, 40)), this.cell11); // edge case
  }

  // test the onMouseClicked method for the Board class
  // purpose: right click => negate flag; left click => click
  void testOnMouseClicked(Tester t) {
    this.initBoardConditions();
    this.board.onMouseClicked(new Posn(20, 0), "RightButton"); // flag
    t.checkExpect(this.board.get(0, 1).isFlagged(), true);
    this.board.onMouseClicked(new Posn(20, 0), "RightButton"); // unflag
    t.checkExpect(this.board.get(0, 1).isFlagged(), false);
    t.checkExpect(this.board.visibleCells, 0);

    // click => flood fill
    this.board.onMouseClicked(new Posn(0, 0), "LeftButton");
    t.checkExpect(this.board.get(0, 0).isClicked(), true);
    t.checkExpect(this.board.get(0, 1).isClicked(), true);
    t.checkExpect(this.board.get(1, 0).isClicked(), true);
    t.checkExpect(this.board.get(1, 1).isClicked(), true);
    t.checkExpect(this.board.visibleCells, 4);

    this.initBoardConditions(); // reset board

    // set two cells to be mines
    this.board.get(0, 0).negMine();
    this.board.get(0, 1).negMine();

    t.checkExpect(this.board.get(0, 0).isMine(), true);
    t.checkExpect(this.board.get(0, 1).isMine(), true);

    this.initBoardConditions(); // reset board

    // set two cells to be mines
    this.board.get(0, 0).negMine();
    this.board.get(0, 1).negMine();
    this.board.minesList = new ArrayList<Cell>(
        Arrays.asList(this.board.get(0, 0), this.board.get(0, 1)));

    // click a non-mine on first turn
    this.board.onMouseClicked(new Posn(40, 40), "LeftButton");
    t.checkExpect(this.board.get(1, 1).isClicked(), true);

    // second click is a mine => end the game and reveal all mines
    this.board.onMouseClicked(new Posn(0, 0), "LeftButton");
    t.checkExpect(this.board.get(0, 0).isClicked(), true);
    t.checkExpect(this.board.get(0, 1).isClicked(), true);
    t.checkExpect(this.board.gameOver, true);
    t.checkExpect(this.board.win, false);

    // the game is over => clicks don't do anything
    this.board.onMouseClicked(new Posn(0, 20), "LeftButton");
    t.checkExpect(this.board.get(1, 0).isClicked(), false);
  }

  // test the onKeyEvent method for the Board class
  // purpose: allow the user to play again or end the game
  void testOnKeyEvent(Tester t) {
    this.initBoardConditions();
    this.board.endGame(false);
    this.board.onKeyEvent("f"); // not enter => end world
    // the world has ended => clicks don't do anything
    this.board.onMouseClicked(new Posn(0, 20), "LeftButton");
    t.checkExpect(this.board.get(1, 0).isClicked(), false);

    // key press during gameplay does nothing
    this.initBoardConditions(); // reset board
    this.board.onKeyEvent("f");
    t.checkExpect(this.board.gameOver, false); // doesn't end game

    this.initBoardConditions(); // reset board
    this.board.endGame(false);
    this.board.onKeyEvent("enter");
    t.checkExpect(this.board.gameOver, false); // check that board was reset
    this.board.onMouseClicked(new Posn(0, 20), "LeftButton");
    t.checkExpect(this.board.get(1, 0).isClicked(), true);
  }
}