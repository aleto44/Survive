package game.entities;

import java.awt.Rectangle;
import java.util.Random;
import java.util.Timer;

import game.gfx.Colors;
import game.gfx.Font;
import game.gfx.Screen;
import game.level.Level;

public class Zombie extends Mob {

	private int color = Colors.get(-1, 1, 10, 220);
	private int scale = 1;
	protected boolean isSwimming = false;
	private int tickCount = 0;
	private String talkText = "gr";
	protected static int curTick = 0;
	public boolean close;
	Random random = new Random();
	private boolean collided0, collided1, collided2, collided3;
	
	
	public Zombie(Level level, int x, int y, int speed) {
		super(level,"zomb", x, y, speed);
		
	}
	
	
	
	public void tick() {
		int xa = 0;
		int ya = 0;
		
		if(close == false && tickCount % 7 == 0){ 					//Little random movements, its bad need to fix make more long walks rather than equally spread.
			
			if(counter > 20){
				mov = random.nextInt(100);
			}
		
			if(mov == 0){
				ya--;
			}else if(mov == 1){
				ya++;
			}else if (mov == 2){
				xa--;
			}else if (mov == 3){
				xa++;
			}else if (mov == 4){
				ya++;
				xa++;
			}else if (mov == 5){
				ya--;
				xa--;
			}else if (mov == 6){
				ya++;
				xa--;
			}else if (mov == 7){
				ya--;
				xa++;
			}
		
//				int mov = random.nextInt(2000);	//old shtty move formula, npc still uses
//			
//				if(mov >= (75) && mov <= 100) 	
//					ya = ya + random.nextInt(2);	
//				else if(mov >= 50 && mov < 75) 	
//					ya = ya - random.nextInt(2);	
//				else if(mov >= 25 && mov < 50) 	
//					xa = xa + random.nextInt(2);	
//				else if(mov >= 0 && mov < 25) 	
//					xa = xa - random.nextInt(2); 	
			}  

		
		if(xa != 0 || ya != 0){
			move(xa,ya);
			isMoving = true;
		} else {
			isMoving = false;
			counter++;
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
		int xTile = 0, yTile = 23;		//sprite sheet coords for player
		int walkingSpeed = 2;			//change the quickness of animation change
		int flipTop = (numSteps >> walkingSpeed ) & 1;		
		int flipBottom= (numSteps >> walkingSpeed ) & 1;
	
		if(movingDir == 1){											//keeps track of direction of player
			xTile += 2;												//
		} else if (movingDir > 1){									//
			xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;		//
			flipTop = (movingDir - 1) % 2;							//
			flipBottom = (movingDir - 1) % 2;						//
		}
		if(collided0  && tickCount % 30 < 15){ 				//change what heads are rendering when on top of player.
			xTile += 8;										//I want it to switch back between this and the original when
		} else if (collided1 && tickCount % 30 < 15){		//collided
			xTile += 8;
		} else if (collided2 && tickCount % 30 < 15){
			xTile +=8;
		} else if (collided3  && tickCount % 30 < 15){
			xTile +=8;
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
			screen.render(xOffset, yOffset + 3, 0 + 27 *32, waterColor, 0x00, 1);			//renders the bottom half of mob as water
			screen.render(xOffset + 8, yOffset + 3, 0 + 27 *32, waterColor, 0x01, 1);		//other side flipped to make circle
		}
			screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile *32, color, flipTop, scale);					//render head
			screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile +1) + yTile *32, color, flipTop, scale);	
		
		
		
		if(!isSwimming){
			screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile+1) *32, color, flipBottom, scale);						//renders bottom body if not in water
			screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile+1) *32, color,flipBottom, scale);		//
		}
		
		//if(close == true){
		//	Font.render(talkText, screen, xOffset - ((talkText.length() -1) / 2 * 8), 			//saying gr when in range
		//			yOffset - 10, Colors.get(-1, -1, -1, 555), 1);								//
		//}
}
	
	
	public void inRange(Mob mob){													
		
		if( Math.abs(this.x - mob.x) <= 16 && Math.abs(this.y - mob.y) <= 16) {		//is the player close enough to interact
			close = true;
		}
		if(movingDir == 0 && Math.abs(this.x - mob.x) <= 70 && mob.y <= this.y){
				curTick = tickCount;
				close = true;
		}
		if(movingDir == 1 && Math.abs(this.x - mob.x ) <= 70 && mob.y >= this.y){
				curTick = tickCount;
				close = true;
		}
		if(movingDir == 2 && Math.abs(this.y - mob.y) <= 70 && mob.x <= this.x){
				curTick = tickCount;
				close = true;
		}
		if(movingDir == 3 && Math.abs(this.y - mob.y) <= 70 && mob.x >= this.x){
				curTick = tickCount;
				close = true;
		}
		
		if(movingDir == 0 && (Math.abs(this.y - mob.y) > 120 || Math.abs(this.x - mob.x ) > 120 )){
			close = false;
		}
		if(movingDir == 1 && (Math.abs(this.y - mob.y) > 120 || Math.abs(this.x - mob.x ) > 120 )){
			close = false;
		}
		if(movingDir == 2 && (Math.abs(this.x - mob.x) > 120 || Math.abs(this.y - mob.y) > 120 )){
			close = false;
		}
		if(movingDir == 3 && (Math.abs(this.x - mob.x) > 120 || Math.abs(this.y - mob.y) > 120 )){
			close = false;
		}
		
		if( Math.abs(this.x - mob.x) >= 70 && Math.abs(this.y - mob.y) >= 70) {		//is the player close enough to interact
			close = false;
		}
		
		int xa = 0, ya = 0;
		if(close == true && tickCount % 7 == 0 && collided == false){
			if (this.x < mob.x && !collided3)
			    xa += 1;
			else if (this.x > mob.x && !collided2)
			    xa -= 1;

			if (this.y < mob.y && !collided1)
			    ya += 1;
			else if (this.y > mob.y && !collided0)
			    ya -= 1;
			
			if(xa != 0 || ya != 0){
				move(xa,ya);
				isMoving = true;
			} else {
				isMoving = false;
			}
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
	
	public Rect getRect(){
		return new Rect(x,y);
	}
	
	public void collision(Mob mob){
		Rectangle r1 = this.getRect().getBounds();			//makes zombie stop before going on top of player
		Rectangle r2 = mob.getRect().getBounds();
		
		if(mob.getHealth() != 0){
			if(r1.intersects(r2) && this.movingDir == 0){		//I made 4 collide booleans for each direction, but Im 95% sure I just need the one.
				this.collided0 = true;
			} else if(r1.intersects(r2) && this.movingDir == 1){
				this.collided1 = true;
			}else if(r1.intersects(r2) && this.movingDir == 2){
				this.collided2 = true;
			}else if(r1.intersects(r2) && this.movingDir == 3){
				this.collided3 = true;
			}else {
				collided0 =  collided1 = collided2 = collided3 = false;
			}
		} else {
			collided = false;
			collided0 =  collided1 = collided2 = collided3 = false;
			close = false;
		}
		
		
		if(mob.name.equals("Player") && (collided0 || collided1 || collided2 || collided3)){
			this.collided = true;
		} else {
			this.collided = false;
		}
	}
	public boolean getCollided(){
		return collided;
	}
}


