package game.entities;

import game.level.Level;
import game.level.tiles.Tile;

public abstract class Mob extends Entity{
	
	protected String name;
	protected int speed;
	protected int numSteps = 0;
	protected boolean isMoving;
	protected int movingDir = 1;
	protected int scale = 1;
	protected boolean collided;
	protected int counter;
	protected int mov;
	protected int health = 100;

	public Mob(Level level, String name, int x, int y, int speed) {
		super(level);
		this.name = name;
		this.x = x;
		this.y= y;
		this.speed = speed;
	}
	
	public void sprint(Player player){ //shouldnt be in mob
		speed = 40;
	}
	
	public void move(int xa, int ya){
		if(xa != 0 && ya != 0){
			move(xa, 0);
			move(0, ya);
			numSteps--;
			return;
		}
		
		counter++;
		if(counter > 21){
			counter = 0;
		}
		
		numSteps++;
		if(!hasCollided(xa, ya)) {			//get directions for mobs
			if(ya < 0)movingDir = 0;		//0 moving up, north
			if(ya > 0)movingDir = 1;		//1 moving down, south
			if(xa < 0)movingDir = 2;		//2 moving to the left, west
			if(xa > 0)movingDir = 3;		// 3 moving to the right, east
			x += xa*speed;
			y += ya*speed;
		}
	}
	
	public abstract boolean hasCollided(int xa, int ya);
	public abstract void inRange(Mob mob);
	public abstract Rect getRect();
	
	protected boolean isSolidTile(int xa, int ya, int x, int y){	//if a tile is solid, mobs will not be able to move through.
		if(level == null) { return false;}							//using the has collided method
		Tile lastTile = level.getTile((this.x + x) >>3, (this.y +y) >> 3);
		Tile newTile = level.getTile((this.x + x + xa) >> 3, (this.y + y + ya) >> 3);
		if(!lastTile.equals(newTile) && newTile.isSolid()){
			return true;
		}
		return false;
	}
	
	protected boolean solidInFront(int x, int y){  //trying to create a method to make zombies see solid walls in their field of vision
		if(level == null) { return false;}
		
		if(movingDir == 3){
			for(int i = 0; i < 10; i++){
				Tile tile = level.getTile(this.x + x + i >> 3, (this.y +y) >> 3);
				if(tile.isSolid()){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public String getName(){
		return name;
	}
	public int getMovingDir(){
		return movingDir;
	}
	public int getHealth(){
		return health;
	}
	public void setCollided(boolean collide){
		collided = collide;
	}
	
	public void gotHit(Mob mob){
		
		if(mob.movingDir == 0 && this.y < mob.y){
			if(mob.x - this.x > 4){
				this.y += -4;
				this.x += -4;
			} else if (this.x - mob.x > 4){
				this.y += -4;
				this.x += 4;
			} else {
				this.y += -10;
			}
		}
		
		if(mob.movingDir == 1 && this.y > mob.y){
			if(mob.x - this.x > 4){
				this.y += 4;
				this.x += -4;
			} else if (this.x - mob.x > 4){
				this.y += 4;
				this.x += 4;
			} else {
				this.y += 10;
			}
		}
		
		if(mob.movingDir == 2 && this.x < mob.x){
			if(mob.y - this.y > 4){
				this.y += -4;
				this.x += -4;
			} else if (this.y - mob.y > 4){
				this.y += 4;
				this.x += -4;
			} else {
				this.x += -10;
			}
		}
		
		if(mob.movingDir == 3 && this.x > mob.x){
			if(mob.y - this.y > 4){
				this.y += -4;
				this.x += 4;
			} else if (this.y - mob.y > 4){
				this.y += 4;
				this.x += 4;
			} else {
				this.x += 10;
			}
		}
	}

}
