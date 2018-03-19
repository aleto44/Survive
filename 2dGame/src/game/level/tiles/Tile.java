package game.level.tiles;

import game.gfx.Colors;
import game.gfx.Screen;
import game.level.Level;

public abstract class Tile {
	
	public static final Tile[] tiles = new Tile[256];
	public static final Tile VOID = new BasicSolidTile(0,0,0, Colors.get(000, -1, -1, -1), 0xFF000000);
	public static final Tile STONE = new BasicSolidTile(1, 1, 0, Colors.get(-1, 222, 111, -1),0xFF555555);
	public static final Tile GRASS = new BasicTile(2, 2, 0, 
			Colors.get(-1, 131, 121, -1), 0xFF00FF00);
	public static final Tile WATER = new AnimatedTile(3, new int[][] { {0, 5},{1, 5},{2, 5}, {1,5} }, 
				Colors.get(-1, 004, 115, -1), 0xFF0000FF, 250);
	public static final Tile WOOD = new BasicTile(4, 3, 0, Colors.get(-1, 210, 431, 321),0xFF9A8055);
	public static final Tile WALL = new BasicSolidTile(5, 4, 0, Colors.get(-1, 210, 222, -1),0xFF563010);
	public static final Tile SAND = new BasicTile(6, 5, 0, Colors.get(-1, 542, 431, -1),0xFFF0D030);
	public static final Tile FURNATURE = new BasicSolidTile(7, 6, 0, Colors.get(-1, 111, 222, -1),0xFF505000);
	public static final Tile SIDEWALK = new BasicTile(8, 7, 0, Colors.get(-1, 222, 333, -1),0xFFA0A0A0);
	public static final Tile ROAD = new BasicTile(9, 8, 0, Colors.get(-1, 111, 222, -1),0xFF202020);
	public static final Tile ROADSTRIP = new BasicTile(10, 9, 0, Colors.get(-1, 440, 550, -1),0xFFF0F000);
	
	
	
	
	
	
	
	//ok very important.  For colors.  the OxFF... points to the color id on the sprite sheet.  
	//to get this number you have to take the R, G, B values and divide each by 16.  Take the factor
	//and the remainder and boom you got it.   
	//
	//The color that shows in game, first spot in Colors.get refers to the black on sprite,
	//and so on until white.  There are only 4 possible shades per block. 
	//There are 3 spots r, g, b and 6 values from 0 - 5.  so 555 would be 255, 255, 255
	
	protected byte id;
	protected boolean solid;
	protected boolean emitter;
	private int levelColor;  //index on the level tile
	
	public Tile(int id, boolean isSolid, boolean isEmitter, int levelColor){
		this.id = (byte) id;
		if(tiles[id] != null) throw new RuntimeException("Duplicate Tile id on" + id);
		this.solid = isSolid;
		this.emitter = isEmitter;
		this.levelColor = levelColor;  
		tiles[id] = this;
	}
	
	public byte getId(){
		return id;
	}
	
	public boolean isSolid(){
		return solid;
	}
	
	public boolean isEmitter(){
		return emitter;
	}
	public int getLevelColor(){
		return levelColor;
	}
	
	public abstract void tick();

	public abstract void render(Screen screen, Level level, int x, int y);
}
