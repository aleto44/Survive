package game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;


import game.entities.MobNpc;
import game.entities.Player;
import game.entities.Zombie;

import game.gfx.Screen;
import game.gfx.SpriteSheet;
import game.level.Level;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 500;				//adjust how much screen is showing, 250 is a good size
	public static final int HEIGHT = WIDTH/12*9;		//scales with width
	public static final int SCALE = 3;					//how zoomed is everything
	public static final String NAME ="Game";			//top title
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount=0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private int[] colors = new int[6*6*6];
	
	private Screen screen;
	public InputHandler input;
	Random random = new Random();
	
	public Level level;				//stuff im adding is here, for now
	public Player player;
	public MobNpc npc, npc2;
	Zombie[] zombieList1;
	MobNpc[] npcList1;
	
	
	public Game(){
		setMinimumSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		setMaximumSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		
		frame= new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.requestFocusInWindow();
	}
	
	
	
	
	
	
	public void init(){
		int index = 0;
		for(int r = 0; r< 6; r++){			//gets shades from 0 - 5
			for(int g = 0; g< 6; g++){
				for(int b = 0; b< 6; b++){
					int rr = (r * 255/5);
					int gg = (g * 255/5);
					int bb = (b * 255/5);
					
					colors[index++]= rr << 16 | gg << 8 | bb;		//put it all in 1 line of numbers
				}
			}
		}
		
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		level = new Level("/levels/level_one.png", 1);		//figure out a way to automatically do this
		
		player = new Player(level, 450, 280, input, 1 , "");  //adding all entities here for now, 304,360
		level.addEntity(player);
		
		//generate random zombies and npc arrays
		
		this.zombieList1 = new Zombie[zombLevelAmount()];
		this.npcList1 = new MobNpc[npcLevelAmount()];
		generateLists();										//generate all mobs lists
		
		for(int i = 0; i < zombieList1.length; i++){
			level.addEntity(zombieList1[i]);
		}
		for(int i = 0; i < npcList1.length; i++){
			level.addEntity(npcList1[i]);
		}
	}
	
	
	
	
	
	
	
	public void generateLists(){
		for(int i = 0; i < zombieList1.length; i++){
			int randomX= random.nextInt(7000);		//out of how big the map is 1024,1024, (80)+262, (72) +340
			int randomY= random.nextInt(7000);		// todo figure coordinates of certain places on map they shouldnt spawn different for all levels, will need if statements burrrr
			zombieList1[i]=  new Zombie(level, randomX, randomY, 1);
		}
		for(int i = 0; i < npcList1.length; i++){
			int randomX= random.nextInt(80) + 262;		//out of how big the map is 
			int randomY= random.nextInt(72) + 340;		// todo figure coordinates of certain places on map they shouldnt spawn different for all levels, will need if statements burrrr
			npcList1[i]=  new MobNpc(level, randomX, randomY, 1);
		}
	}
	
	
	
	
	public int npcLevelAmount(){
		if(level.getLevelNumber() == 1){				//adjust npc amounts for each level HERE ONLYYTYYYYYY
			return 1;							//
		} else if (level.getLevelNumber() == 2){		//
			return 2;
		}
		return 0;
	}
	
	public int zombLevelAmount(){
		if(level.getLevelNumber() == 1){				//adjust zombie amounts for each level HERE ONLYYTYYYYYY
			return 10000;							//
		} else if (level.getLevelNumber() == 2){		//
			return 2;
		}
		return 0;
	}
	
	
	
	
	
	public synchronized void start() {
		running = true;
		new Thread(this).start();
	}
	public synchronized void stop() {
		running = false;
	}
	
	
	
	
	
	
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/60D;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		init();
		
		while(running){
			long now = System.nanoTime();
			delta +=(now - lastTime)/nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			while(delta >= 1){
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(shouldRender){
				frames++;
				render();
			}
			
			if(System.currentTimeMillis()- lastTimer >= 1000){
				lastTimer += 1000;
				
				System.out.println("FPS:"+ frames + " " + "Tps:" + ticks);
				frames = 0;
				ticks = 0;
			}
			
			check();
		}
	}

	
	
	
	
	
	public void tick(){
		tickCount++;	
		level.tick();

		for(int i = 0; i< npcList1.length; i++){
			npcList1[i].collision(player);
		}
		for(int i = 0; i < zombieList1.length; i++){				//checking range for all zombies
			
			if(player.getHealth() > 0 ){
				zombieList1[i].inRange(player);
			}
			zombieList1[i].collision(player);
			this.player.collision(zombieList1[i]);
			
			if(zombieList1[i].getCollided() == true){				//player loses health for running into zombies
				if(player.getHealth() != 0){
					this.player.takingDamage = true;
					this.player.loseHp();
				}
			}
			
		
			
		//	if(zombieList1[i].close == false){					//right now this is broken, zombie flashes between chasing player and chasing npc
		//		for(int j = 0; j < npcList1.length; j++){
		//			zombieList1[i].inRange(npcList1[j]);
		//		}
		//	}
		}
		
		
		}

	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		
		int xOffset = player.x- (screen.width/2);		
		int yOffset = player.y- (screen.height/2);
		
		level.renderTiles(screen, xOffset, yOffset);
		
		level.renderEntities(screen);
		
		for(int y=0;y<screen.height;y++){
			for(int x=0;x<screen.width;x++){
				int colorCode = screen.pixels[x+y * screen.width];
				if(colorCode < 255) pixels[x+y * WIDTH] = colors[colorCode];
			}
		}
		
		Graphics g =bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		g.dispose();
		bs.show();
	}
	
	public void check(){		//things to check constantly
		
		
	}
	
	public static void main(String[] args){
		new Game().start();
	}

	

}
