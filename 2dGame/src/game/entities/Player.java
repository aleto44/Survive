package game.entities;

import java.awt.Rectangle;

import game.InputHandler;
import game.gfx.Colors;
import game.gfx.Font;
import game.gfx.Screen;
import game.level.Level;

public class Player extends Mob{

	
	private InputHandler input;
	private int skinColor = 543;
	private int color = Colors.get(-1, 110, 145, skinColor);
	private int scale = 1;
	protected boolean isSwimming = false;
	private int tickCount = 0;
	private String username;
	protected static boolean isInteracting;	
	protected boolean safeCollided;
	private String test;
	private boolean playerDead;
	private int hpColor;
	public boolean takingDamage;
	private boolean crouch;
	private int doSomethingTick;
	long time;
	private boolean attacking;
	
	
	
	
	public Player(Level level, int x, int y, InputHandler input, int speed, String username) {
		super(level, "Player", x, y, speed);
		this.input = input;
		this.username = username;
		this.speed = speed;
		this.health= health;
	}
	public void tick() {
		int xa = 0;
		int ya = 0;

		/////////////////////
		//start of controls//
		
		if(input.up.isPressed()){ ya--;}									//allow player to move	
		if(input.down.isPressed()){ya++;}									//
		if(input.left.isPressed()){xa--;}									//
		if(input.right.isPressed()){xa++;}								//
		
		if(input.sprint.isPressed()){
			super.sprint(this);
		} else {
			this.speed = 1;
		}
		
		if(input.interact.isPressed()){									//only interacting with friendly mobs as of right now
			isInteracting = true;
			MobNpc.curTick = tickCount;
		}
		if(tickCount == MobNpc.curTick + 40){    										//adjust this for text visable time
			isInteracting = false;														//fix this up later
		}
		
		if(input.crouch.isPressed()){
			crouch = true;
		} else {
			crouch = false;
		}
		
		if(input.attack.isPressed()){
			attacking = true;
		} else {
			attacking = false;
		}
		
		
		//end of controls//
		///////////////////
		
		
		
		if(crouch){
			doSomethingTick = 10;
		} else {
			doSomethingTick = 3;
		}
		
		if((xa != 0 || ya != 0) && tickCount % doSomethingTick == 0){  			//&& tickCount % 3 == 0 to make player move slower, need better way looks choppy
			if(isSwimming == true){
				if(tickCount % 2 == 0){				//slows player down in water
					move(xa, ya);
					isMoving = true;
				}
			} else {
				move(xa,ya);
				isMoving = true;
			}
		} else {
			isMoving = false;
		}
		if(level.getTile(this.x >> 3, this.y >> 3).getId() == 3){							//Checking for water tile
			isSwimming = true;																//
		}																					//
		if(isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 3){				//
			isSwimming = false;																//
		}
		
		if(health == 0){
			playerDead = true;
		} else if ((health < 100 &&  tickCount % 100 == 0)){
			health ++;
		}
		
		tickCount++;
	}

	public void render(Screen screen) {
	
	color = Colors.get(-1, 110, 145, skinColor);
	
	int xTile = 0, yTile = 28;		//sprite sheet coords for player
	
	int walkingSpeed = 2;			//change the quickness of animation change 
	if(this.speed == 40){			//TODO: will need to change when changing sprint from admin control
		walkingSpeed = 1;
	}				
	
	int flipTop = (numSteps >> walkingSpeed ) & 1;		
	int flipBottom= (numSteps >> walkingSpeed ) & 1;;
	
	if(walkingSpeed == 1 && (movingDir == 2 || movingDir == 3)){	//get him to the sprint animation
		xTile += 4;
	}
	
	if(crouch && this.speed == 1){
		xTile += 12;
		walkingSpeed = 1;
	}
	
	if(attacking && walkingSpeed == 2){
		xTile += 20;
	}
	
	if(movingDir == 1){											//keeps track of direction of player
		xTile += 2;												//
	} else if (movingDir > 1){									//
		xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;		//
		flipTop = (movingDir - 1) % 2;							//
		flipBottom = (movingDir - 1) % 2;						//
	}
	
	
	//location correct sprite sheet locations//
	//////////////////////////////////////////
	
	
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
		screen.render(xOffset, yOffset + 3, 0 + 27 *32, waterColor, 0x00, 1);			//renders the bottom half of player as water around player head
		screen.render(xOffset + 8, yOffset + 3, 0 + 27 *32, waterColor, 0x01, 1);		//flips it to make a circle
	}
	
	screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile *32, color, flipTop, scale);					//render head
	screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile +1) + yTile *32, color, flipTop, scale);	//
	
	
	if(!isSwimming){
		screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile+1) *32, color, flipBottom, scale);						//renders bottom body if not in water
		screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile+1) *32, color,flipBottom, scale);		//
	}
	
	
	

	if(health > 85){
		skinColor = 543;
	} else if(health < 85 && health >= 70){
		skinColor = 432;
	} else if (health < 70 && health >= 55){
		skinColor = 432;
	} else if (health < 55 && health >= 45){
		skinColor = 332;
	}  else if (health < 45 && health >= 30){
		skinColor = 332;
	} else if (health < 30 && health >= 15){
		skinColor = 333;
	} else if (health < 15 && health >= 5){
		skinColor = 222; 
	} else if (health < 10){
		skinColor = 220;
	}
	
	//test = ""+health;
	//Font.render(test, screen, xOffset - ((test.length() -1) / 2 * 8), yOffset - 10, Colors.get(-1, -1, -1, 555), 1);	//use to display a name over character
	
	}
	
	
	public void loseHp(){
		if(health == 0){
			playerDead = true;
		} else if(tickCount % 3 == 0){
				health --;
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
		Rectangle r1 = this.getRect().getBounds();
		Rectangle r2 = mob.getRect().getBounds();
		
		if(mob.name.equals("zomb")){
			if(r1.intersects(r2)){
				collided = true;  //doesnt work bc there is one player reacting with many things.  need to fix logic
				if(attacking == true){
					mob.gotHit(this);
				}
			}
		} else if(r1.intersects(r2)){
			safeCollided = true;
		}
	}
	public boolean getPlayerDead(){
		return playerDead;
	}
	
	public void inRange(Mob mob) {  //might use later
	}

}
