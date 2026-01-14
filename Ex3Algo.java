import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;

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
        int dir = greenAlgo(game);
        return dir;
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

    private static int pinkAlgo(PacmanGame game) {
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

    private static int greenAlgo(PacmanGame game) {
        int blue = Game.getIntColor(Color.BLUE, CODE);
        int green = Game.getIntColor(Color.GREEN, CODE);
        Map2D colorBoard = new Map(game.getGame(CODE));
        Pixel2D pacmanPos = convertPos(game.getPos(CODE));
        Map2D distanceBoard = colorBoard.allDistance(pacmanPos, blue);
        // stage 2
        Pixel2D pixelMin = getClosestWantedPixel(colorBoard, green, distanceBoard, pacmanPos);
        if (pixelMin.equals(pacmanPos)) {
            return pinkAlgo(game);
        }
        // stage 3
        Pixel2D[] shortestPath = colorBoard.shortestPath(pacmanPos, pixelMin, blue);
        // stage 4
        Pixel2D second = shortestPath[1];
        int dir = getDirection(pacmanPos, second, colorBoard);
        return dir;
    }


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
}
