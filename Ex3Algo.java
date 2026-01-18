import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;
import java.util.ArrayList;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 * <p>
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo {
    private int _count;
    private static final int CODE = 0;

    public Ex3Algo() {
        _count = 0;
    }

    @Override
    /**
     *  Add a short description for the algorithm as a String.
     */
    public String getInfo() {
        return null;
    }

    @Override
    /**
     * This ia the main method - that you should design, implement and test.
     */
    public int move(PacmanGame game) {
        if (_count == 0 || _count == 300) {
            int code = 0;
            int[][] board = game.getGame(0);
            printBoard(board);
            int blue = Game.getIntColor(Color.BLUE, code);
            int pink = Game.getIntColor(Color.PINK, code);
            int black = Game.getIntColor(Color.BLACK, code);
            int green = Game.getIntColor(Color.GREEN, code);
            System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);
            String pos = game.getPos(code).toString();
            System.out.println("Pacman coordinate: " + pos);
            GhostCL[] ghosts = game.getGhosts(code);
            printGhosts(ghosts);
            int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;
        }
        _count++;
        int dir = 0;
        /**
         * checks to deside if to go eat the ghost or the pink dots.
         */
        if (isInDanger(game) && ghostAreEatble(game)) {
             dir = chaseAlgo(game);
        }
        else {
             dir = pinkAlgo(game);
        }
        return dir;
    }

    /**
     * This function gets the time that the ghost Are Eatble.
     * This Allows the pacman to know when its Safe to go eat the ghosts.
     * @param game
     * @return remain Time for ghosts to be Eatable.
     */
    private static boolean ghostAreEatble(PacmanGame game) {
    GhostCL[] gs = game.getGhosts(CODE);
    for (int i = 0; i < gs.length; i++) {
        GhostCL g = gs[i];
        if (g.remainTimeAsEatable(CODE)>1.5) {
            return true;
        }
    }
    return false;
}
    private static void printBoard(int[][] b) {
        for (int y = 0; y < b[0].length; y++) {
            for (int x = 0; x < b.length; x++) {
                int v = b[x][y];
                System.out.print(v + "\t");
            }
            System.out.println();
        }
    }

    private static void printGhosts(GhostCL[] gs) {
        for (int i = 0; i < gs.length; i++) {
            GhostCL g = gs[i];
            System.out.println(i + ") status: " + g.getStatus() + ",  type: " + g.getType() + ",  pos: " + g.getPos(0) + ",  time: " + g.remainTimeAsEatable(0));
        }
    }

    private static Pixel2D convertPos(String pos) {
        String[] split = pos.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        Pixel2D pixelPos = new Index2D(x, y);

        return pixelPos;
    }

    /**
     * This is the default function for pacman.
     * pacman goes for the other functions from move or pinkAlgo if their conditions are met
     * @param game
     * @return escapeAlgo if pacman is close enough to the ghosts
     * @return The direction for the closest pink dot to pacman otherwise
     */
    private static int pinkAlgo(PacmanGame game) {
        /**
         *  Checks if the pacman is close enough to the ghosts.
         *  @return escapeAlgo if so.
         */
        if (isInDanger(game)) {
            return escapeAlgo(game);
        }
        int blue = Game.getIntColor(Color.BLUE, CODE);
        int pink = Game.getIntColor(Color.PINK, CODE);
        // stage 1
        Map2D colorBoard = new Map(game.getGame(CODE));
        Pixel2D pacmanPos = convertPos(game.getPos(CODE));
        Map2D distanceBoard = colorBoard.allDistance(pacmanPos, blue);
        // stage 2
        Pixel2D pixelMin = getClosestWantedPixel(colorBoard, pink, distanceBoard, pacmanPos);
        if (pixelMin.equals(pacmanPos)) {
            return randomDir();
        }
        // stage 3
        Pixel2D[] shortestPath = colorBoard.shortestPath(pacmanPos, pixelMin, blue);
        // stage 4
        Pixel2D second = shortestPath[1];
        int dir = getDirection(pacmanPos, second, colorBoard);
        return dir;
    }

    /**
     * This function returns The direction for the closest green dot to pacman
     * @param game
     * @return The direction for the closest green dot to pacman
     */

    private static int greenAlgo(PacmanGame game) {
        int blue = Game.getIntColor(Color.BLUE, CODE);
        int green = Game.getIntColor(Color.GREEN, CODE);
        // stage 1
        Map2D colorBoard = new Map(game.getGame(CODE));
        Pixel2D pacmanPos = convertPos(game.getPos(CODE));
        Map2D distanceBoard = colorBoard.allDistance(pacmanPos, blue);
        // stage 2
        Pixel2D pixelMin = getClosestWantedPixel(colorBoard, green, distanceBoard, pacmanPos);
        if (pixelMin.equals(pacmanPos)) {
            return randomDir();
        }
        // stage 3
        Pixel2D[] shortestPath = colorBoard.shortestPath(pacmanPos, pixelMin, blue);
        // stage 4
        Pixel2D second = shortestPath[1];
        int dir = getDirection(pacmanPos, second, colorBoard);
        return dir;
    }

    /**
     * This function gets the distances of the ghosts to pacman and his neigbors.
     * Then gets who's neighbor has the farest ghost from pacman.
     * Set the direction of pacman to that neighbor.
     * @param game
     * checks at first the distances of the ghosts to pacman,to see who is the closest ghost.
     * Then it checks distances of the neighbors, to see who is closest ghost to each one of them.
     * Once it has distances, it checks who's neighbor's ghost has the Highest distance from pacman.
     * Sets the direction of pacman to that neigbor.
     * @return The direction of pacman from his position to that chosen neigbor.
     */
    private static int escapeAlgo(PacmanGame game) {
        if (isNearGreen(game)) {
            return greenAlgo(game);
        }
        int blue = Game.getIntColor(Color.BLUE, CODE);
        //stage 1
        Map2D colorBoard = new Map(game.getGame(CODE));
        Pixel2D pacmanPos = convertPos(game.getPos(CODE));
        Map2D distanceBoard = colorBoard.allDistance(pacmanPos, blue);
        // stage 2
        GhostCL[] ghosts = game.getGhosts(CODE);
        int[] dangerDistance = new int[ghosts.length];
        Pixel2D[] ghostPos = new Pixel2D[ghosts.length];
        // Creates the dangerDistance of ghosts
        for (int i = 0; i < ghosts.length; i++) {
            ghostPos[i] = convertPos(ghosts[i].getPos(CODE));
            dangerDistance[i] = distanceBoard.getPixel(ghostPos[i]);
        }
        // Gets the minimum from dangerDistance
        int dangerDistanceMin = Integer.MAX_VALUE;
        for (int i = 0; i < dangerDistance.length; i++) {
            if (dangerDistance[i] < dangerDistanceMin) {
                dangerDistanceMin = dangerDistance[i];
            }
        }
        // stage 3
        // Get the neighbors of pacman
        ArrayList<Pixel2D> neighbors = new ArrayList<>();
        for (int i = 0; i < distanceBoard.getWidth(); i++) {
            for (int j = 0; j < distanceBoard.getHeight(); j++) {
                if (distanceBoard.getPixel(i, j) == 1) {
                    neighbors.add(new Index2D(i, j));
                }
            }
        }

        // distances of the neighbors
        ArrayList<Map2D> neighborsDistances = new ArrayList<>();
        for (int i = 0; i < neighbors.size(); i++) {
            neighborsDistances.add(i, colorBoard.allDistance(neighbors.get(i), blue));
        }
        //distances of the neighbors from the closest ghost
        int[] neighborsGrades = new int[neighbors.size()];
        for (int i = 0; i < neighbors.size(); i++) {
            //i==neighbor
            neighborsGrades[i] = Integer.MAX_VALUE;
            for (int j = 0; j < ghosts.length; j++) {
                if (neighborsDistances.get(i).getPixel(ghostPos[j]) < neighborsGrades[i]) {
                    neighborsGrades[i] = neighborsDistances.get(i).getPixel(ghostPos[j]);
                }
            }
        }
        /**
         * Gets the neighbor with farest ghost
         */

        int max = dangerDistanceMin;
        Pixel2D chosenNeighbor = new Index2D(pacmanPos);
        for (int i = 0; i < neighborsGrades.length; i++) {
            if (neighborsGrades[i] > max) {
                max = neighborsGrades[i];
                chosenNeighbor = neighbors.get(i);
            }
        }
        /**
         * set the direction of pacman
         */
        return getDirection(pacmanPos, chosenNeighbor, colorBoard);
    }

    /**
     * This function checks the distances of the ghosts to pacman,to decide ghost to chase after.
     * @param game
     * checks at first the distances of the ghosts to pacman,to see who is the closest ghost.
     * Finds the shortest path to pacman's closest ghost.
     * @return the direction for pacman to chase the ghosts
     */
    // A function for pacman for chasing the ghosts
    private static int chaseAlgo(PacmanGame game) {
        //OBSTACLE
        int blue = Game.getIntColor(Color.BLUE, CODE);
        //stage 1
        Map2D colorBoard = new Map(game.getGame(CODE));
        Pixel2D pacmanPos = convertPos(game.getPos(CODE));
        Map2D distanceBoard = colorBoard.allDistance(pacmanPos, blue);
        // stage 2
        GhostCL[] ghosts = game.getGhosts(CODE);
        int[] chaseDistance = new int[ghosts.length];
        Pixel2D[] ghostPos = new Pixel2D[ghosts.length];
        // Creates the chaseDistance of ghosts
        for (int i = 0; i < ghosts.length; i++) {
            ghostPos[i] = convertPos(ghosts[i].getPos(CODE));
            chaseDistance[i] = distanceBoard.getPixel(ghostPos[i]);
        }
        // Gets the minimum from chaseDistance
        int chaseDistanceMin = Integer.MAX_VALUE;
        // Gets the chosen Ghost
        Pixel2D chosenGhost = new Index2D(pacmanPos);
        for (int i = 0; i < chaseDistance.length; i++) {
            if (chaseDistance[i] < chaseDistanceMin && !isInBox(ghostPos[i],colorBoard)) {
                chaseDistanceMin = chaseDistance[i];
                chosenGhost = ghostPos[i];
            }
        }
        // set the shortestPath of pacman to the closest ghost
        if (chosenGhost.equals(pacmanPos)) {
            return randomDir();
        }
        Pixel2D[] shortestPath = colorBoard.shortestPath(pacmanPos, chosenGhost, blue);

        Pixel2D second = shortestPath[1];
        return getDirection(pacmanPos, second, colorBoard);
        // Gets the closet


    }

    /**
     * Gets the area of the starting box.
     * This is to prevent the pacman from entering it and been stuck inside it
     * @param ghostPos
     * @param colorBoard
     * @return the size the box of staring area of the ghosts
     */
    private static boolean isInBox(Pixel2D ghostPos, Map2D colorBoard) {
        if (ghostPos.getX()>=9 && ghostPos.getX()<= 14 && ghostPos.getY()>=11 && ghostPos.getY()<= 13) {
            return true;
        }
        else return false;
    }

    /**
     * A function to decide which direction the pacman needs to go.
     * Checks for cyclic and non cyclic.
     * Gets the direction for pacman by his position in x and y axis.
     * Gets the direction for his neighbors in x and y axis.
     * Then Decides which which direction the pacman needs to go by his position and his neighbors.
     * @param first
     * @param second
     * @param map
     * @return the direction for pacman to move to for cyclic and no cyclic
     */
    private static int getDirection(Pixel2D first, Pixel2D second, Map2D map) {
        //cyclic

        if (first.getX() == 0 && second.getX() == map.getWidth() - 1 && first.getY() == second.getY()) {
            return Game.LEFT;
        } else if (first.getX() == map.getWidth() - 1 && second.getX() == 0 && first.getY() == second.getY()) {
            return Game.RIGHT;
        } else if (first.getX() == second.getX() && first.getY() == map.getHeight() - 1 && second.getY() == 0) {
            return Game.UP;
        } else if (first.getX() == second.getX() && first.getY() == 0 && second.getY() == map.getHeight() - 1) {
            return Game.DOWN;
        }
        //no cyclic
        else if (first.getX() == second.getX() && first.getY() + 1 == second.getY()) {
            return Game.UP;
        } else if (first.getX() == second.getX() && first.getY() - 1 == second.getY()) {
            return Game.DOWN;
        } else if (first.getX() + 1 == second.getX() && first.getY() == second.getY()) {
            return Game.RIGHT;
        } else if (first.getX() - 1 == second.getX() && first.getY() == second.getY()) {
            return Game.LEFT;
        }
        return randomDir();
    }

    /**
     * A function to help the other functions to decide which way for pacman to go.
     * @param colorBoard
     * @param color
     * @param distanceBoard
     * @param pacmanPos
     * @return The Closest non obstacle pixel from pacman
     */
    private static Pixel2D getClosestWantedPixel(Map2D colorBoard, int color, Map2D distanceBoard, Pixel2D pacmanPos) {
        int min = Integer.MAX_VALUE;
        Pixel2D pixelMin = new Index2D(pacmanPos);
        for (int i = 0; i < colorBoard.getWidth(); i++) {
            for (int j = 0; j < colorBoard.getHeight(); j++) {
                if (colorBoard.getPixel(i, j) == color && distanceBoard.getPixel(i, j) < min && distanceBoard.getPixel(i, j) >= 0) {
                    min = distanceBoard.getPixel(i, j);
                    pixelMin = new Index2D(i, j);
                }
            }
        }
        return pixelMin;
    }

    private static int randomDir() {
        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        int ind = (int) (Math.random() * dirs.length);
        return dirs[ind];
    }

    /**
     * @param game
     * Checks the proximity of pacman to the green dots by his and the green dots positions on the map.
     * @return True if pacman is close enough to a green dot.
     */
    private static boolean isNearGreen(PacmanGame game) {
        int blue = Game.getIntColor(Color.BLUE, CODE);
        int green = Game.getIntColor(Color.GREEN, CODE);
        Map2D colorBoard = new Map(game.getGame(CODE));
        Pixel2D pacmanPos = convertPos(game.getPos(CODE));
        Map2D distanceBoard = colorBoard.allDistance(pacmanPos, blue);
        Pixel2D pixelMin = getClosestWantedPixel(colorBoard, green, distanceBoard, pacmanPos);
        if (distanceBoard.getPixel(pixelMin) < 7 && distanceBoard.getPixel(pixelMin) > 0) {
            return true;
        }

        return false;
    }
    /**
     * Checks the proximity of pacman to the ghosts.
     * @param game
     * Gets the distances from pacman to the ghosts.
     * Checks who is the closet ghost to pacman
     * @return True if pacman is close enough that ghost.
     */
    private static boolean isInDanger(PacmanGame game) {
        int blue = Game.getIntColor(Color.BLUE, CODE);
        //stage 1
        Map2D colorBoard = new Map(game.getGame(CODE));
        Pixel2D pacmanPos = convertPos(game.getPos(CODE));
        Map2D distanceBoard = colorBoard.allDistance(pacmanPos, blue);
        // stage 2
        GhostCL[] ghosts = game.getGhosts(CODE);
        int[] dangerDistance = new int[ghosts.length];
        Pixel2D[] ghostPos = new Pixel2D[ghosts.length];
        // Creates the dangerDistance of ghosts
        for (int i = 0; i < ghosts.length; i++) {
            ghostPos[i] = convertPos(ghosts[i].getPos(CODE));
            dangerDistance[i] = distanceBoard.getPixel(ghostPos[i]);
        }
        // Gets the minimum from dangerDistance
        int dangerDistanceMin = Integer.MAX_VALUE;
        for (int i = 0; i < dangerDistance.length; i++) {
            if (dangerDistance[i] < dangerDistanceMin) {
                dangerDistanceMin = dangerDistance[i];
            }
        }
        int distanceToRun = 5;
        if (dangerDistanceMin < distanceToRun) {
            return true;
        }
        return false;
    }
}
