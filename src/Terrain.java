import java.awt.Graphics2D;
import java.awt.Image;


public class Terrain {
	public static int IMG_HEIGHT;
	public static int IMG_WIDTH;
	
	private Image img;
	private TerrainType type;
	
	public Terrain(TerrainType type, Image img) {
		this.type = type;
		this.img = img;
	}
	
	public void draw(Graphics2D g, int x, int y) {
		g.drawImage(img, x, y, 32, 16, null);
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
}
