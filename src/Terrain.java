import java.awt.Graphics2D;
import java.awt.Image;


public class Terrain {
	public static int IMG_HEIGHT;
	public static int IMG_WIDTH;
	
	private Image img;
	private TerrainType type;
	private int x, y;
	private int row;
	private int col;
	
	public Terrain(int row, int col, TerrainType type, Image img) {
		this.row = row;
		this.col = col;
		this.type = type;
		this.img = img;
	}

	public void draw(Graphics2D g) {
		g.drawImage(img, x, y, null);
	}

	public Image getImage() {
		return img;
	}

	public void setImage(Image img) {
		this.img = img;
	}

	public TerrainType getType() {
		return type;
	}

	public void setType(TerrainType type) {
		this.type = type;
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
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
}
