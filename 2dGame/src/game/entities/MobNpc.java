package game.entities;

import java.awt.Rectangle;
import java.util.Random;


import game.gfx.Colors;
import game.gfx.Font;
import game.gfx.Screen;
import game.level.Level;

public class MobNpc extends Mob{

	
	private int color = Colors.get(-1, 10, 531, 543);
	private int scale = 1;
	protected boolean isSwimming = false;
	private int tickCount = 0;
	private String talkText;
	protected static int curTick = 0;
	Random random = new Random();
	protected boolean safeCollided;
	
	public MobNpc(Level level, int x, int y, int speed) {
		super(level, "woman", x, y, speed);
		
	}
	
	
	
	public void tick() {
		int xa = 0;
		int ya = 0;
											//Little random movements
		int move = random.nextInt(1500);	//
		if(move >= (75) && move <= 100) 	//
		    ya = ya + random.nextInt(2);	//
		else if(move >= 50 && move < 75) 	//
			ya = ya - random.nextInt(2);	//
		else if(move >= 25 && move < 50) 	//
			xa = xa + random.nextInt(2);	//
		else if(move >= 0 && move < 25) 	//
			xa = xa - random.nextInt(2); 	//
		    	
		if(xa != 0 || ya != 0){
			move(xa,ya);
			isMoving = true;
		} else {
			isMoving = false;
		}
		if(level.getTile(this.x >> 3, this.y >> 3).getId() == 3){							//Checking for water tile
			isSwimming = true;																//
		}																					//
		if(isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 3){				//
			isSwimming =false;																//
		}
		
		
		
		tickCount++;
	}
	
	
	
	public void render(Screen screen) {
		int xTile = 0, yTile = 25;		//sprite sheet coords for player
		int walkingSpeed = 1;			//change the quickness of animation change
		int flipTop = (numSteps >> walkingSpeed ) & 1;		
		int flipBottom= (numSteps >> walkingSpeed ) & 1;;
	
		if(movingDir == 1){											//keeps track of direction of player
			xTile += 2;												//
		} else if (movingDir > 1){									//
			xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;		//
			flipTop = (movingDir - 1) % 2;							//
			flipBottom = (movingDir - 1) % 2;						//
		}
	
		int modifier = 8 * scale;
		int xOffset = x - modifier/2;
		int yOffset = y - modifier /2 - 4;
	
	
		if(isSwimming){
			int waterColor = 0;
			yOffset += 4;													//makes player go deeper into water
			if(tickCount % 60 < 15){										//all this does it change the water color around players head in water
				waterColor = Colors.get(-1, -1, 225, -1);					//
			} else if ( 15 <= tickCount % 60 && tickCount %60 < 30){		//
				waterColor = Colors.get(-1, 225, 115, -1);					//
				yOffset -= 1;												//bob down in water
			} else if (30 <= tickCount % 60 && tickCount % 60 < 45){		//
			waterColor = Colors.get(-1, 115, -1, 225);					//
			} else {														//
				waterColor = Colors.get(-1, 225, 115, -1);					//
				yOffset -= 1;												//bob down in water
			}
			screen.render(xOffset, yOffset + 3, 0 + 27 *32, waterColor, 0x00, 1);			//renders the bottom half of player as water
			screen.render(xOffset + 8, yOffset + 3, 0 + 27 *32, waterColor, 0x01, 1);		//
		}
	
		screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile *32, color, flipTop, scale);					//render head
		screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile +1) + yTile *32, color, flipTop, scale);	//always render head
		
		if(!isSwimming){
			screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile+1) *32, color, flipBottom, scale);						//renders bottom body if not in water
			screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile+1) *32, color,flipBottom, scale);		//
		}
		
		
		
		
		if(this.safeCollided != true){
			int rand = random.nextInt(100)+1;
			if( rand < 25){
				talkText = "sup";
			} else if ( rand < 50){
				talkText = "Howdy";
			} else if ( rand < 75){
				talkText = "hm?";
			} else if (rand < 100){
				talkText = "Hi";
			}
		}
	
		if(Player.isInteracting == true && this.safeCollided == true){ //change close to collided
			Font.render(talkText, screen, xOffset - ((talkText.length() -1) / 2 * 8), 			//text to talk
					yOffset - 10, Colors.get(-1, -1, -1, 555), 1);								//
	}
}
	
	
	
	
	
	public boolean hasCollided(int xa, int ya) {			//checks if tiles are solid as player moves.
		int xMin = 0;										
		int xMax = 7;
		int yMin = 3;
		int yMax = 7;
		
		for(int x = xMin; x < xMax; x++){					//checking the whole damn thing
			if(isSolidTile(xa, ya, x, yMin)){				//all this
				return true;
			}
		}
		for(int x = xMin; x < xMax; x++){
			if(isSolidTile(xa, ya, x, yMax)){
				return true;
			}
		}
		for(int y = yMin; y < yMax; y++){
			if(isSolidTile(xa, ya, xMin, y)){
				return true;
			}
		}
		for(int y = yMin; y < yMax; y++){
			if(isSolidTile(xa, ya, xMax, y)){
				return true;
			}
		}
		return false;
	}
	
	
	
	

	public Rect getRect() {
		return new Rect(x,y);
	}
	
	public void collision(Mob mob){
		Rectangle r1 = this.getRect().getBounds();
		Rectangle r2 = mob.getRect().getBounds();
		
		if(mob.name.equals("zombie")){
			if(r1.intersects(r2)){
				collided = true;
			} else {
				collided = false;
			}
		} else if(r1.intersects(r2)){
			safeCollided = true;
		} else {
			safeCollided = false;
		}
	}



	@Override
	public void inRange(Mob mob) {
		// TODO Auto-generated method stub
		
	}

}


