import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a 2D map as a "screen" or a raster matrix or maze over integers.
 *
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D, Serializable {
	private int[][] _arr;
	private boolean _cyclicFlag = true;
	private static final int OBSTACLE = -2;

	/**
	 * Constructs a w*h 2D raster map with an init value v.
	 *
	 * @param w
	 * @param h
	 * @param v
	 */
	public Map(int w, int h, int v) {
		if (w == 0 || h == 0) {
			throw new RuntimeException("the array is not legal");
		}
		init(w, h, v);
	}

	/**
	 * Constructs a square map (size*size).
	 *
	 * @param size
	 */
	public Map(int size) {
		this(size, size, 0);
	}

	/**
	 * Constructs a map from a given 2D array.
	 *
	 * @param data
	 */
	public Map(int[][] data) {
		if (data == null) {
			throw new RuntimeException("the array is not legal");
		}
		if (data.length == 0) {
			throw new RuntimeException("the array arr is not legal");
		}
		if (data[0].length == 0) {
			throw new RuntimeException("the array arr is not legal");
		}
		if (!isRagged(data)) {
			throw new RuntimeException("the array arr is not legal");
		}
		init(data);

	}

	@Override
	public void init(int w, int h, int v) {
		_arr = new int[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				_arr[i][j] = v;

			}
		}
	}

	@Override
	public void init(int[][] arr) {
		_arr = getDeepCopy(arr);

	}

	@Override
	public int[][] getMap() {

		return getDeepCopy(_arr);

	}

	@Override
	public int getWidth() {
		int ans = _arr.length;

		return ans;
	}

	@Override
	public int getHeight() {
		int ans = _arr[0].length;

		return ans;
	}

	@Override
	public int getPixel(int x, int y) {
		int ans = _arr[x][y];

		return ans;
	}

	@Override
	public int getPixel(Pixel2D p) {
		int ans = _arr[p.getX()][p.getY()];

		return ans;
	}

	@Override
	public void setPixel(int x, int y, int v) {
		_arr[x][y] = v;
	}

	@Override
	public void setPixel(Pixel2D p, int v) {
		_arr[p.getX()][p.getY()] = v;
	}


	@Override
	/**
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	public int fill(Pixel2D xy, int new_v) {
		int count = 0;
		Map board = new Map(getWidth(), getHeight(), -1);
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				if (this.getPixel(i, j) != this.getPixel(xy)) {
					board.setPixel(i, j, OBSTACLE);
				}
			}
		}
		Map2D distances = board.allDistance(xy, OBSTACLE);


		for (int i = 0; i < this.getWidth(); i++) {
			for (int j = 0; j < this.getHeight(); j++) {
				if (distances.getPixel(i, j) != -1 && distances.getPixel(i, j) != OBSTACLE) {
					this.setPixel(i, j, new_v);
					count++;
				}
			}
		}
		return count;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
		// the result.
		Map2D distances = allDistance(p1, obsColor);
		Pixel2D[] path = new Pixel2D[distances.getPixel(p2) + 1];
		Pixel2D current = p2;
		ArrayList<Pixel2D> neighbours = null;

		for (int i = path.length - 1; i >= 0; i--) {
			path[i] = current;
			if (_cyclicFlag) {
				neighbours = getNeighboursWithCyclic(current, (Map) distances);
			} else {
				neighbours = getNeighbours(current, (Map) distances);
			}
			for (Pixel2D neighbour : neighbours) {
				if (distances.getPixel(neighbour) == distances.getPixel(current) - 1) {
					current = neighbour;
					break;
				}
			}
		}
		return path;
	}

	@Override
	public boolean isInside(Pixel2D p) {

		boolean x = (_arr.length > p.getX()) && (0 <= p.getX());
		boolean y = (_arr[0].length > p.getY()) && (0 <= p.getY());

		return x && y;
	}

	@Override

	public boolean isCyclic() {
		return _cyclicFlag;
	}

	@Override

	public void setCyclic(boolean cy) {
		_cyclicFlag = cy;
	}


	@Override
	public Map2D allDistance(Pixel2D start, int obsColor) {
		Map distances = new Map(getDeepCopy(this._arr));
		resetDistances(distances, obsColor);
		distances.setPixel(start, 0);
		Queue<Pixel2D> q = new LinkedList<>();
		q.add(start);
		while (!q.isEmpty()) {
			Pixel2D current = q.poll();
			ArrayList<Pixel2D> neighbours = null;
			if (_cyclicFlag) {
				neighbours = getNeighboursWithCyclic(current, distances);
			} else {
				neighbours = getNeighbours(current, distances);
			}
			for (Pixel2D neighbour : neighbours) {
				if (distances.getPixel(neighbour) == -1) {
					distances.setPixel(neighbour, distances.getPixel(current) + 1);
					q.add(neighbour);
				}
			}
		}
		// the result.
		return distances;
	}


	/// /////////////////// Private Methods ///////////////////////
	///
	private ArrayList<Pixel2D> getNeighboursWithCyclic(Pixel2D current, Map distances) {

		ArrayList<Pixel2D> neighbours = new ArrayList<>();
		Index2D up = new Index2D(current.getX() - 1, current.getY());
		Index2D down = new Index2D(current.getX() + 1, current.getY());
		Index2D left = new Index2D(current.getX(), current.getY() - 1);
		Index2D right = new Index2D(current.getX(), current.getY() + 1);

		if (!isInside(up)) {
			up = new Index2D(distances.getWidth() - 1, up.getY());
		}

		if ((distances.getPixel(up) != OBSTACLE)) {
			neighbours.add(up);
		}

		if (!isInside(down)) {
			down = new Index2D(0, down.getY());
		}

		if ((distances.getPixel(down) != OBSTACLE)) {
			neighbours.add(down);
		}

		if (!isInside(left)) {
			left = new Index2D(left.getX(), distances.getHeight() - 1);
		}

		if ((distances.getPixel(left) != OBSTACLE)) {
			neighbours.add(left);
		}

		if (!isInside(right)) {
			right = new Index2D(right.getX(), 0);
		}

		if ((distances.getPixel(right) != OBSTACLE)) {
			neighbours.add(right);
		}

		return neighbours;
	}

	private ArrayList<Pixel2D> getNeighbours(Pixel2D current, Map distances) {
		ArrayList<Pixel2D> neighbours = new ArrayList<>();
		Index2D up = new Index2D(current.getX() - 1, current.getY());
		Index2D down = new Index2D(current.getX() + 1, current.getY());
		Index2D left = new Index2D(current.getX(), current.getY() - 1);
		Index2D right = new Index2D(current.getX(), current.getY() + 1);

		if (isInside(up)) {
			if ((distances.getPixel(up) != OBSTACLE)) {
				neighbours.add(up);
			}
		}
		if (isInside(down)) {
			if ((distances.getPixel(down) != OBSTACLE)) {
				neighbours.add(down);
			}
		}
		if (isInside(left)) {
			if ((distances.getPixel(left) != OBSTACLE)) {
				neighbours.add(left);
			}
		}
		if (isInside(right)) {
			if ((distances.getPixel(right) != OBSTACLE)) {
				neighbours.add(right);
			}
		}
		return neighbours;
	}


	private void resetDistances(Map distances, int obsColor) {
		for (int i = 0; i < distances.getWidth(); i++) {
			for (int j = 0; j < distances.getHeight(); j++) {
				if (distances.getPixel(i, j) != obsColor) {
					distances.setPixel(i, j, -1);
				} else {
					distances.setPixel(i, j, OBSTACLE);
				}
			}
		}
	}

	/**
	 * This method checks whether the given 2D array is ragged or not
	 *
	 * @param arr
	 * @return True if 2D array is ragged, False otherwise
	 * @throws RuntimeException if arr == null or if the array is empty.
	 */
	private boolean isRagged(int[][] arr) {

		int checkLength = arr[0].length;

		for (int i = 0; i < arr.length; i++) {
			if (arr[i].length != checkLength) {
				return false;
			}
		}
		return true;
	}


	/*
	 * @throws RuntimeException
	 */
	private int[][] getDeepCopy(int[][] arr) {

		int[][] copyArr = new int[arr.length][arr[0].length];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				copyArr[i][j] = arr[i][j];

			}
		}
		return copyArr;
	}


	public void printMap() {
		for (int i = 0; i < _arr.length; i++) {
			for (int j = 0; j < _arr[i].length; j++) {
				System.out.print(_arr[i][j] + " ");
			}
			System.out.println();
		}
	}

}