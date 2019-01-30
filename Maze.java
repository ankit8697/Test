import java.io.*;
import java.util.*;

/**
 * Maze.java
 * Solution to the Maze Assignment (HW5).
 * CS 201: Data Structures - Winter 2018
 *
 * @author Ankit Sanghi
 */
public class Maze {
    private ArrayList<ArrayList<MazeSquare>> rowList;
    private int w, h;
    private int startRow, startCol, endRow, endCol;

    // I am including MazeSquare as an inner class
    // to simplify the file structure a little bit.
    private class MazeSquare {
        private int r, c;
        private boolean top, bottom, left, right,
                start, end, visited;

        private MazeSquare(int r, int c,
                           boolean top, boolean bottom, boolean left, boolean right,
                           boolean start, boolean end) {
            this.r = r;
            this.c = c;
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
            this.start = start;
            this.end = end;
            visited = false;
        }

        boolean hasTopWall() {
            return top;
        }
        boolean hasBottomWall() {
            return bottom;
        }
        boolean hasLeftWall() {
            return left;
        }
        boolean hasRightWall() {
            return right;
        }
        boolean isStart() {
            return start;
        }
        boolean isEnd() {
            return end;
        }
        int getRow() {
            return r;
        }
        int getCol() {
            return c;
        }
        boolean isVisited() {
            return visited;
        }
        void visit() {
            visited = true;
        }
    }

    /**
     * Construct a new Maze
     */
    public Maze() {
        rowList = new ArrayList<ArrayList<MazeSquare>>();
    }

    /**
     * Load Maze in from given file
     *
     * @param fileName the name of the file containing the Maze structure
     */
    public void load(String fileName) {

        // Create a scanner for the given file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        }

        // First line of file is "w h"
        String[] lineParams = scanner.nextLine().split(" ");
        w = Integer.parseInt(lineParams[0]);
        h = Integer.parseInt(lineParams[1]);

        // Second line of file is "startCol startRow"
        lineParams = scanner.nextLine().split(" ");
        startCol = Integer.parseInt(lineParams[0]);
        startRow = Integer.parseInt(lineParams[1]);

        // Third line of file is "endCol endRow"
        lineParams = scanner.nextLine().split(" ");
        endCol = Integer.parseInt(lineParams[0]);
        endRow = Integer.parseInt(lineParams[1]);

        // Read the rest of the lines (L or | or _ or -)
        String line;
        int rowNum = 0;
        boolean top, bottom, left, right;
        boolean start, end;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            rowList.add(new ArrayList<MazeSquare>());

            // Loop through each cell, creating MazeSquares
            for (int i = 0; i < line.length(); i++) {
                // For top, check row above, if there is one
                if (rowNum > 0) {
                    top = rowList.get(rowNum-1).get(i).hasBottomWall();
                } else {
                    top = true;
                }

                // For right, check cell to the right, if there is one
                if (i < line.length() - 1 ) {
                    char nextCell = line.charAt(i+1);
                    if (nextCell == 'L' || nextCell == '|') {
                        right = true;
                    } else {
                        right = false;
                    }
                } else {
                    right = true;
                }

                // For left and bottom, switch on the current character
                switch (line.charAt(i)) {
                    case 'L':
                        left = true;
                        bottom = true;
                        break;
                    case '_':
                        left = false;
                        bottom = true;
                        break;
                    case '|':
                        left = true;
                        bottom = false;
                        break;
                    case '-':
                        left = false;
                        bottom = false;
                        break;
                    default:
                        left = false;
                        bottom = false;
                }

                // Check to see if this is the start or end spot
                start = startCol == i && startRow == rowNum;
                end = endCol == i && endRow == rowNum;

                // Add a new MazeSquare
                rowList.get(rowNum).add(new MazeSquare(rowNum, i, top, bottom, left, right, start, end));
            }

            rowNum++;
        }
    }

    /**
    * Computes and returns a solution to this maze. If there are multiple
    * solutions, only one is returned, and getSolution() makes no guarantees about
    * which one. However, the returned solution will not include visits to dead
    * ends or any backtracks, even if backtracking occurs during the solution
    * process.
    *
    * @return a LLStack of MazeSquare objects containing the sequence of squares
    *         visited to go from the start square (bottom of the stack) to the
    *         finish square (top of the stack).
    */
    public LLStack<MazeSquare> getSolution() {
        LLStack<MazeSquare> stack = new LLStack<MazeSquare>();
        // Getting the start square
        MazeSquare startSquare = rowList.get(startRow).get(startCol);
        // Getting the finish square
        MazeSquare finishSquare = rowList.get(endRow).get(endCol);
        // Visiting the start square
        startSquare.visit();
        // Pushing the start square into the stack
        stack.push(startSquare);
        // If the stack is empty, the start has been popped out and the maze is unsolvable
        while (!stack.isEmpty()) {
              MazeSquare T = stack.peek();
              // If the current square is the finish square, the maze is solved
              if (T == finishSquare) {
                return stack;
              }
              // Check if there is no valid path to follow from the current MazeSquare
                if ((getNeighbour(T,"left").isVisited() || getNeighbour(T,"left").hasRightWall()) &&
                (getNeighbour(T,"right").isVisited() || getNeighbour(T,"right").hasLeftWall()) &&
                (getNeighbour(T,"top").isVisited() || getNeighbour(T,"top").hasBottomWall()) &&
                (getNeighbour(T,"bottom").isVisited() || getNeighbour(T,"bottom").hasTopWall())) {
                  // If there is no valid path, pop the stack and move one space back
                  stack.pop();
                }
                else {
                  // Check to see if there is a valid path available
                  // If there is a valid path, mark it as visited and push it into the stack
                  if (!(getNeighbour(T,"left").isVisited() || getNeighbour(T,"left").hasRightWall())) {
                    stack.push(getNeighbour(T,"left"));
                    getNeighbour(T,"left").visit();
                  }
                  else if (!(getNeighbour(T,"right").isVisited() || getNeighbour(T,"right").hasLeftWall())) {
                    stack.push(getNeighbour(T, "right"));
                    getNeighbour(T,"right").visit();
                  }
                  else if (!(getNeighbour(T,"top").isVisited() || getNeighbour(T,"top").hasBottomWall())) {
                    stack.push(getNeighbour(T, "top"));
                    getNeighbour(T,"top").visit();
                  }
                  else if (!(getNeighbour(T,"bottom").isVisited() || getNeighbour(T,"bottom").hasTopWall())) {
                    stack.push(getNeighbour(T, "bottom"));
                    getNeighbour(T,"bottom").visit();
                  }
                }
              }
          // If the stack is empty, the maze is unsolvable
          System.out.println("The maze is unsolvable");
          return stack;
    }

    /**
     * Finds the MazeSquare object in the desired direction and returns that MazeSquare object
     * @param s The current MazeSquare object
     * @param direction Which direction is the required MazeSquare with respect to the current MazeSquare
     * @return The MazeSquare object present in the requested direction
     */
    public MazeSquare getNeighbour(MazeSquare s, String direction) {
        // We use a try block in case a block that falls outside rowList is referenced
        try {
          // Reference the corrosponding MazeSquare using the index position in rowList
          if (direction.equals("left")) {
              return rowList.get(s.getRow()).get(s.getCol()-1);
          }
          else if (direction.equals("right")) {
              return rowList.get(s.getRow()).get(s.getCol()+1);
          }
          else if (direction.equals("top")) {
              return rowList.get(s.getRow()-1).get(s.getCol());
          }
          else if (direction.equals("bottom")) {
              return rowList.get(s.getRow()+1).get(s.getCol());
          }
          // This return statement will never trigger as either a return inside the conditional statements will be executed
          // or an IndexOutOfBoundsException will be thrown
          return null;
        } catch (IndexOutOfBoundsException e) {
          /* 
          To handle the exception, we return a new MazeSquare reference containing an enclosed box
          This makes the program think that there is a MazeSquare object outside the maze, but due to
          it being enclosed, it can never reach it and thus cannot interfere with the maze solving process
          */
            return new MazeSquare(0,0,true,true,true, true, false, false);
        }
    }


    /**
     * Print the Maze to the Console
     */
    public void print() {
        // We first get a stack of all the MazeSquares required to solve the maze
        LLStack stack = getSolution();

        ArrayList<MazeSquare> currRow;
        MazeSquare currSquare;

        // Print each row of text based on top and left
        for (int r = 0; r < rowList.size(); r++) {
            currRow = rowList.get(r);

            // First line of text: top wall
            for (int c = 0; c < currRow.size(); c++) {
                System.out.print("+");
                if (currRow.get(c).hasTopWall()) {
                    System.out.print("-----");
                } else {
                    System.out.print("     ");
                }
            }
            System.out.println("+");

            // Second line of text: left wall then space
            for (int c = 0; c < currRow.size(); c++) {
                if (currRow.get(c).hasLeftWall()) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }
                System.out.print("     ");
            }
            System.out.println("|");

            // Third line of text: left wall, then space, then start/end/sol, then space
            for (int c = 0; c < currRow.size(); c++) {
                currSquare = currRow.get(c);

                if (currSquare.hasLeftWall()) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }

                System.out.print("  ");

		// If currSquare is part of the solution, mark it with *
                if (currSquare.isStart() && currSquare.isEnd()) {
                    System.out.print("SE ");
                } else if (currSquare.isStart() && !currSquare.isEnd()) {
                    System.out.print("S  ");
                } else if (!currSquare.isStart() && currSquare.isEnd()) {
                    System.out.print("E  ");
                }
                else if (stack.contains(currSquare)) {
                  System.out.print("*  ");
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println("|");

            // Fourth line of text: same as second
            for (int c = 0; c < currRow.size(); c++) {
                if (currRow.get(c).hasLeftWall()) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }
                System.out.print("     ");
            }
            System.out.println("|");
        }

        // Print last row of text as straight wall
        for (int c = 0; c < rowList.get(0).size(); c++) {
            System.out.print("+-----");
        }
        System.out.println("+");
    }

    // This main program acts as a simple unit test for the
    // load-from-file and print-to-System.out Maze capabilities.
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Maze mazeFile");
            System.exit(1);
        }

        Maze maze = new Maze();
        maze.load(args[0]);
        maze.print();
    }
}
