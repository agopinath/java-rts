package org.cyanojay.rts.world.map;

public abstract class GameMap {
	public abstract int[] screenToMap(int x, int y);
	public abstract int[] viewportToMap(int x, int y);
	public abstract int[] mapToScreen(int row, int col);
	
	public abstract <T> T[] getSurroundings(int row, int col);
	public abstract <T> T getBlockAt(int row, int col);
}
