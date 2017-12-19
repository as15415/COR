package schultz.personal.cor.main;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import schultz.personal.cor.helpers.AnimationObj;
import schultz.personal.cor.helpers.Car;
import schultz.personal.cor.helpers.LandMine;
import schultz.personal.cor.helpers.PlasmaBullet;
import schultz.personal.cor.helpers.Waypoint;

public class Track1 implements Screen {
	
	private CORGame game;
	private OrthographicCamera cam;
	
	private int numAiCars;
	
	private ArrayList<Car> cars;
	private ArrayList<Waypoint> waypoints;
	private ArrayList<PlasmaBullet> pBullets;
	private ArrayList<LandMine> landMines;
	private ArrayList<Polygon> carPolys;
	private ArrayList<Polygon> bulletPolys;
	private ArrayList<Polygon> landMinePolys;
	
	private Waypoint current;
	
	private Texture background;
	private Texture trackTex;
	private Texture playerCarTex;
	private Texture playerCarHit;
	private Texture AICarTex;
	private Texture AICarHit;
	private Texture plasmaBulletTex;
	private Texture collision;
	private Texture explosionSheet;
	private Texture landmineSheet;
	
	private Sprite playerCarSprite;
	private Sprite aiCarSprite;
	private Sprite track;
	
	private Car playerCar;
	private Vector2 initGunPos; // initial gun and rear positions before rotation for collision detection for playerCar
	private Vector2 initRearPos;
	private Vector2 gunPos;
	private Vector2 rearPos;
	
	private float trackX;
	private float trackY;
	
	private float acc; // acceleration for player
	private float aiAcc; // acceleration for ai
	private float friction;
	private float damageRate; // HP damage rate for PlasmaBullets and Mines
	
	private long startTimeBullets; // used in timer for PlasmaBullets
	private long startTimeMines; // used in timer for LandMines
	
	private Pixmap collisionPixmap; // used for collision map so playerCar doesn't run off the track
	
	private Polygon playerPoly;
	
	private Intersector intersector;
	
	private Music race1;
	private Sound pBulletSound;
	private Sound explosion;
	
	private AnimationObj explosionAni;
	private AnimationObj explosionAni2;
	private AnimationObj explosionAni3;
	private AnimationObj landMineAni;
	
	public Track1(CORGame game) {
		this.game = game;
		
		cam = new OrthographicCamera(game.getScreenWidth(), game.getScreenHeight());
		cam.update();
		
		loadAssets();
		
		numAiCars = 1;
		
		/*
		 * The carPolys arraylist is ordered according to the cars arraylist,
		 * in order to identify a car associated with a polygon. I was too
		 * lazy to write a better system.
		 * 
		 * == Modify the arraylists at your own risk. ==
		 */
		cars = new ArrayList<Car>();
		waypoints = new ArrayList<Waypoint>();
		pBullets = new ArrayList<PlasmaBullet>();
		landMines = new ArrayList<LandMine>();
		carPolys = new ArrayList<Polygon>();
		bulletPolys = new ArrayList<Polygon>();
		landMinePolys = new ArrayList<Polygon>();
		
		playerCarSprite = new Sprite(playerCarTex);
		
		track = new Sprite(trackTex);
		
		/*
		 * With this current configuration, the playerCar starts at (1750, 1.35) (x,y) (based on bottom-left origin of car)
		 */
		playerCarSprite.setPosition(track.getX() + (track.getWidth()/2 + 550), 
				(float) (track.getY() + (track.getHeight()/2 - 1198.65)));
		
		playerCar = new Car(playerCarSprite, this); // 'PC' = playerCar
		
		cars.add(playerCar); // index 0 of cars is always playerCar
		
		playerPoly = new Polygon(new float[] { playerCarSprite.getX(), playerCarSprite.getY(),
				playerCarSprite.getX(), playerCarSprite.getY() + playerCarSprite.getHeight(),
				playerCarSprite.getX() + playerCarSprite.getWidth(), playerCarSprite.getY() + playerCarSprite.getHeight(),
				playerCarSprite.getX() + playerCarSprite.getWidth(), playerCarSprite.getY()});
		
		playerPoly.setOrigin(playerCar.getMidXPos(), playerCar.getMidYPos());
		playerPoly.scale(-0.3f);
		
		playerCar.setBoundPoly(playerPoly);
		
		carPolys.add(playerPoly);
		
		initGunPos = new Vector2(playerCar.getSprite().getX() + 22, playerCar.getSprite().getY() + 37);
		initRearPos = new Vector2(playerCar.getSprite().getX() + 131, playerCar.getSprite().getY() + 37);
		
		gunPos = initGunPos;
		rearPos = initRearPos;
		
		aiCarSprite = new Sprite(AICarTex);

		/*
		 * With this current configuration, the AI Cars will spawn at (1750, 80) (x, y) (based on bottom-left origin of car)
		 */
		for(int i = 1; i <= numAiCars; i++) {
			Sprite aiSprite = new Sprite(AICarTex);
			
			aiSprite.setX(track.getX() + (track.getWidth()/2 + 550));
			aiSprite.setY(track.getY() + (track.getHeight()/2 - 1120));
			
			cars.add(new Car(aiSprite, this));
			
			Polygon aiPoly = new Polygon(new float[] { aiSprite.getX(), aiSprite.getY(),
					aiSprite.getX(), aiSprite.getY() + aiSprite.getHeight(),
					aiSprite.getX() + aiSprite.getWidth(), aiSprite.getY() + aiSprite.getHeight(),
					aiSprite.getX() + aiSprite.getWidth(), aiSprite.getY()});
			
			aiPoly.setOrigin(cars.get(i).getMidXPos(), cars.get(i).getMidYPos());
			aiPoly.scale(-0.3f);
			
			cars.get(i).setBoundPoly(aiPoly);
			
			carPolys.add(aiPoly);
		}
		
		// Default waypoints for the track
		float x = cars.get(1).getMidXPos();
		float y = cars.get(1).getMidYPos();
		waypoints.add(new Waypoint(new Vector2(x - 1742, y), cars.get(1), false));
		waypoints.add(new Waypoint(new Vector2(waypoints.get(0).getPos().x, y + 2192), cars.get(1), false));
		waypoints.add(new Waypoint(new Vector2(waypoints.get(1).getPos().x + 2242, waypoints.get(1).getPos().y), cars.get(1), false));
		waypoints.add(new Waypoint(new Vector2(waypoints.get(2).getPos().x, waypoints.get(2).getPos().y - 2242), cars.get(1), false));
		waypoints.add(new Waypoint(new Vector2(waypoints.get(3).getPos().x - 650, waypoints.get(3).getPos().y), cars.get(1), true));
		
		current = waypoints.get(0);
		
		trackX = 0;
		trackY = 0;
		
		track.setPosition(trackX, trackY);
		
		collision.getTextureData().prepare();
		
		collisionPixmap = collision.getTextureData().consumePixmap();
		
		intersector = new Intersector();
		
		race1.setLooping(true);
		race1.play();
		
		explosionAni = new AnimationObj(15, 5, 2, explosionSheet, false);
		explosionAni2 = new AnimationObj(15, 5, 2, explosionSheet, false);
		explosionAni3 = new AnimationObj(15, 5, 2, explosionSheet, false);
		landMineAni = new AnimationObj(5, 2, 1, landmineSheet, true);
		
		/*
		 * Default acceleration is 0.2f,
		 * this creates a top speed of 19.8
		 * b/c of friction (tested in-game)
		 * 
		 * Because 19.8 / 0.2 = 99,
		 * acc * 99 = top speed
		 * (0.2 * 99 = 19.8)
		 * 
		 * EX: 0.3 * 99 = 29.7 (approx. top speed)
		 * 
		 * This is with default friction of 0.01f
		 * of course, but the same process can be used
		 * with a different friction value (you just need to
		 * do the initial test in-game first)
		 */
		acc = 0.2f; // top speed (default friction): 19.8
		aiAcc = 0.1f; // top speed (default friction): 9.9
		friction = 0.01f;
		damageRate = 20f;
		startTimeBullets = 0;
		startTimeMines = 0;
	}

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0/255f, 0/255f, 0/255f, 0/255f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.batch.begin();
		
		game.batch.setProjectionMatrix(cam.combined);
		
		drawTrackAndBackground();
		drawCarsAndBullets();
		drawLandMines();
		
		game.batch.end();
		
		// \\

		game.shape.begin(ShapeType.Line);
		
		game.shape.setProjectionMatrix(cam.combined);
		
		game.shape.setColor(Color.WHITE);
		
		//drawPolygons();
	
		game.shape.end();
		
		update(delta);
	}
	
	private void update(float delta) {		
		updateWaypoints();
		updatePlayerCar();
		updateAICars();
		updatePlasmaBullets();
		checkCollisions();
		updateLandMines();
		
		//System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
	}
	
	private void drawTrackAndBackground() {
		game.batch.draw(background, trackX, trackY, background.getWidth(), background.getHeight());
		game.batch.draw(track, trackX, trackY);
	}
	
	private void drawCarsAndBullets() {
		for(int i = 0; i < pBullets.size(); i++) {
			if(!pBullets.isEmpty() && !pBullets.get(i).getIsDestroyed()) {
				PlasmaBullet current = pBullets.get(i);

				game.batch.draw(current.getSprite(), current.getSprite().getX(), current.getSprite().getY(), current.getSprite().getOriginX(),
						current.getSprite().getOriginY(), current.getSprite().getWidth(), current.getSprite().getHeight(), 1, 1, current.getSprite().getRotation());
			}
		}
		
		if(!cars.isEmpty()) {
			for(int i = 0; i < cars.size(); i++) {
				Car current = cars.get(i);
				
				if(!current.getIsDestroyed()) {
					game.batch.draw(current.getSprite(), current.getSprite().getX(), current.getSprite().getY(), current.getSprite().getOriginX(),
							current.getSprite().getOriginY(), current.getSprite().getWidth(), current.getSprite().getHeight(), 1, 1, current.getSprite().getRotation());
				}
				
				else {
					if(i == 0) {
						game.batch.draw(explosionAni.getCurrentFrame(), current.getMidXPos() - explosionAni.getSpriteWidth()/2, 
								current.getMidYPos() - explosionAni.getSpriteHeight()/2);
						explosionAni.update();
					}
					
					else {
						game.batch.draw(explosionAni2.getCurrentFrame(), current.getMidXPos() - explosionAni2.getSpriteWidth()/2, 
								current.getMidYPos() - explosionAni2.getSpriteHeight()/2);
						explosionAni2.update();
					}
				}
			}
		}
	}
	
	private void drawLandMines() {
		for(int i = 0; i < landMines.size(); i++) {
			LandMine current = landMines.get(i);
			
			if(!landMines.get(i).getIsDestroyed())
				game.batch.draw(current.getSprite(), current.getSprite().getX(), current.getSprite().getY(), current.getSprite().getOriginX(),
						current.getSprite().getOriginY(), current.getSprite().getWidth(), current.getSprite().getHeight(), 1, 1, current.getSprite().getRotation());
			
			else {
				game.batch.draw(explosionAni3.getCurrentFrame(), current.getMidXPos() - explosionAni3.getSpriteWidth()/2, 
						current.getMidYPos() - explosionAni3.getSpriteHeight()/2);
				explosionAni3.update();
			}
		}
	}
	
	private void drawPolygons() {
		for(int i = 0; i < carPolys.size(); i++)
			game.shape.polygon(carPolys.get(i).getTransformedVertices());
		
		for(int i = 0; i < bulletPolys.size(); i++)
			game.shape.polygon(bulletPolys.get(i).getTransformedVertices());
		
		for(int i = 0; i < landMinePolys.size(); i++)
			game.shape.polygon(landMinePolys.get(i).getTransformedVertices());
	}
	
	private void updateWaypoints() {
		if(current.getCompleted()) {			
			if((waypoints.indexOf(current) + 1) < waypoints.size())
				current = waypoints.get(waypoints.indexOf(current)+1); // go to next waypoint
		}
	}

	private void updatePlayerCar() {
		int rotateAmt = 3;
		
		if(!playerCar.getIsDestroyed()) {
			if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
				playerCar.setSpeed(playerCar.getSpeed() - acc);
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				playerCar.setSpeed(playerCar.getSpeed() + acc);
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				playerCar.getSprite().rotate(rotateAmt);
				playerCar.getBoundPoly().rotate(rotateAmt);
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				playerCar.getSprite().rotate(-rotateAmt);
				playerCar.getBoundPoly().rotate(-rotateAmt);
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {	
				if(TimeUtils.timeSinceNanos(startTimeBullets) > 500000000) {
					createPlasmaBullet();
					
					startTimeBullets = TimeUtils.nanoTime();
				}
			}
			
			if(Math.abs(playerCar.getSpeed()) < 0.1) {
				playerCar.setSpeed(0);
			}
			
			playerCar.setSpeed(playerCar.getSpeed() * (1 - friction));
			
			playerCar.getSprite().setOriginCenter();
			
			playerCar.move();
			
			playerCar.checkHP();
			
			cam.position.set(playerCar.getSprite().getX() + (playerCar.getSprite().getWidth()/2), 
					playerCar.getSprite().getY() + (playerCar.getSprite().getHeight()/2), 0);
			
			initGunPos = new Vector2(playerCar.getSprite().getX() + 22, playerCar.getSprite().getY() + 37);
			initRearPos = new Vector2(playerCar.getSprite().getX() + 131, playerCar.getSprite().getY() + 37);
			
			gunPos.x = getRotatedX(initGunPos, playerCar);
			gunPos.y = getRotatedY(initGunPos, playerCar);
			
			rearPos.x = getRotatedX(initRearPos, playerCar);
			rearPos.y = getRotatedY(initRearPos, playerCar);
			
			cam.update();
		}
	}
	
	private void updateAICars() {
		Waypoint wp = current;
		
		Car car = wp.getTargetCar();
		
		if(!car.getIsDestroyed()) {
			car.getSprite().setRotation(wp.getDist().angle());
			
			car.getBoundPoly().setRotation(car.getSprite().getRotation());
				
			if(wp.getAbsDist().x > 0 || wp.getAbsDist().y > 0) { // if car is not on waypoint, speed up
				car.setSpeed(car.getSpeed() - aiAcc);
			}
				
			/*
			 * If car is set to stop at this waypoint OR this waypoint is the last in the list AND
			 * the car's distance meets the "threshold", stop the car (this prevents strange glitches)
			 */
			if((Math.floor(wp.getAbsDist().x) < 10 && Math.floor(wp.getAbsDist().y) < 10)) {
				if(wp.getStopHere() || waypoints.indexOf(wp) == (waypoints.size() - 1))
					car.setSpeed(0);
				
				wp.setCompleted(true);
			}
			
			car.setSpeed(car.getSpeed() * (1 - friction));
				
			car.move();
			
			car.checkHP();
			
			car.updateRearPos();
			
			cam.update();
		}
	}
	
	private void updatePlasmaBullets() {
		for(int i = 0; i < pBullets.size(); i++) {
			PlasmaBullet current = pBullets.get(i);
			
			current.getBoundPoly().setRotation(current.getSprite().getRotation());
			
			current.move();
			current.checkDist();
		}
	}
	
	private void checkCollisions() {
		if(!(getPixelColor(gunPos.x, background.getHeight() - gunPos.y).equals(Color.GREEN))) {
			playerCar.setSpeed(2);
		}
		
		if(!(getPixelColor(rearPos.x, background.getHeight() - rearPos.y).equals(Color.GREEN))) {
			playerCar.setSpeed(-2);
		}
		
		for(int i = 0; i < carPolys.size()-1; i++) {
			if(intersector.overlapConvexPolygons(carPolys.get(i), carPolys.get(i+1))) {
				cars.get(i).setSpeed(5);
				cars.get(i+1).setSpeed(-5);
			}
		}
		
		for(int i = 0; i < bulletPolys.size(); i++) {
			for(int j = 0; j < carPolys.size(); j++) {
				if(!pBullets.get(i).getIsDestroyed() && intersector.overlapConvexPolygons(bulletPolys.get(i), carPolys.get(j)) && j > 0) {
					cars.get(j).setHP(cars.get(j).getHP() - damageRate);
					cars.get(j).getSprite().setTexture(AICarHit);
					
					pBullets.get(i).setIsDestroyed(true);
					//System.out.println("AiCar HP: " + cars.get(j).getHP());
				}
				
				else if(j > 0) // "j > 0": do not affect playerCar
					cars.get(j).getSprite().setTexture(AICarTex);
			}
		}
		
		for(int i = 0; i < landMinePolys.size(); i++) {
			if(intersector.overlapConvexPolygons(landMinePolys.get(i), playerCar.getBoundPoly()) && !playerCar.getIsDestroyed()) { // only the PlayerCar can be affected by the LandMines
				cars.get(0).setHP(0);
				cars.get(0).getSprite().setTexture(playerCarHit);
				
				landMines.get(i).setHP(0);
			}
			
			else
				cars.get(0).getSprite().setTexture(playerCarTex);
		}
	}
	
	private void updateLandMines() {
		if(TimeUtils.timeSinceNanos(startTimeMines) > 5000000000L) {
			if(cars.get(1).getSpeed() < -0.5)
				createLandMine();
			
			startTimeMines = TimeUtils.nanoTime();
		}
		
		for(int i = 0; i < landMines.size(); i++) {
			landMines.get(i).checkHP();
		}
		
		System.out.println(cars.get(1).getSpeed());
	}
	
	/*
	 * A point on the car that was there initially will change position when the car rotates and moves.
	 * These methods return the correct rotated point about the given Car's middle x and y.
	 */
	public float getRotatedX(Vector2 pos, Car car) {
		double angle = Math.toRadians(car.getSprite().getRotation());
		
		return (float) (Math.cos(angle) * (pos.x - car.getMidXPos()) - Math.sin(angle) * (pos.y - car.getMidYPos()) + car.getMidXPos());
	}
	
	public float getRotatedY(Vector2 pos, Car car) {
		double angle = Math.toRadians(car.getSprite().getRotation());
		
		return (float) (Math.sin(angle) * (pos.x - car.getMidXPos()) + Math.cos(angle) * (pos.y - car.getMidYPos()) + car.getMidYPos());
	}
	
	/*
	 * Gets the color of a pixel specified by the x and y location from the collision map
	 * (collision.png).
	 * == LOCATIONS ARE BASED ON TOP LEFT WITH PIXMAP ==
	 */
	private Color getPixelColor(float f, float g) {
		return new Color(collisionPixmap.getPixel((int) f, (int) g));
	}
	
	private void createPlasmaBullet() {
		Sprite bulletSprite = new Sprite(plasmaBulletTex);
		PlasmaBullet pBullet = new PlasmaBullet(bulletSprite, playerCar, this);
		
		Polygon bulletPoly = new Polygon(new float[] { bulletSprite.getX(), bulletSprite.getY(),
				bulletSprite.getX(), bulletSprite.getY() + bulletSprite.getHeight(),
				bulletSprite.getX() + bulletSprite.getWidth(), bulletSprite.getY() + bulletSprite.getHeight(),
				bulletSprite.getX() + bulletSprite.getWidth(), bulletSprite.getY()});
		
		bulletPoly.setOrigin(pBullet.getMidPos().x, pBullet.getMidPos().y);
		
		pBullet.setBoundPoly(bulletPoly);
		
		pBullets.add(pBullet);
		bulletPolys.add(bulletPoly);
		
		pBulletSound.play();
	}
	
	private void createLandMine() {
		Sprite landMineSprite = new Sprite(landMineAni.getInitialFrame());
		LandMine landMine = new LandMine(landMineAni, cars.get(1), landMineSprite, this);
		
		Polygon landMinePoly = new Polygon(new float[] { landMineSprite.getX(), landMineSprite.getY(),
				landMineSprite.getX(), landMineSprite.getY() + landMineSprite.getHeight(),
				landMineSprite.getX() + landMineSprite.getWidth(), landMineSprite.getY() + landMineSprite.getHeight(),
				landMineSprite.getX() + landMineSprite.getWidth(), landMineSprite.getY()});
		
		landMinePoly.setOrigin(landMine.getMidPos().x, landMine.getMidPos().y);
		
		landMine.setBoundPoly(landMinePoly);
		
		landMines.add(landMine);
		landMinePolys.add(landMinePoly);
	}
	
	public ArrayList<Car> getCars() {
		return cars;
	}

	public ArrayList<Polygon> getCarPolys() {
		return carPolys;
	}
	
	public ArrayList<PlasmaBullet> getPBullets() {
		return pBullets;
	}
	
	public ArrayList<Polygon> getBulletPolys() {
		return bulletPolys;
	}
	
	public ArrayList<LandMine> getLandMines() {
		return landMines;
	}
	
	public ArrayList<Polygon> getLandMinePolys() {
		return landMinePolys;
	}
	
	public Sound getExplosion() {
		return explosion;
	}
	
	@Override
	public void resize(int width, int height) {
		game.viewport.update(width, height);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		pBulletSound.dispose();
		explosion.dispose();
	}
	
	private void loadAssets() {
		background = game.mgr.get("img/backgrnd_1.png", Texture.class);
		trackTex = game.mgr.get("img/track1.png", Texture.class);
		playerCarTex = game.mgr.get("img/car1.png", Texture.class);
		playerCarHit = game.mgr.get("img/car1_hit.png", Texture.class);
		AICarTex = game.mgr.get("img/car2.png", Texture.class);
		AICarHit = game.mgr.get("img/car2_hit.png", Texture.class);
		plasmaBulletTex = game.mgr.get("img/plasma_bullet.png", Texture.class);
		collision = game.mgr.get("img/collision.png", Texture.class);
		race1 = game.mgr.get("audio/Rhinoceros.mp3", Music.class);
		explosionSheet = game.mgr.get("img/explosion_sheet.png", Texture.class);
		pBulletSound = game.mgr.get("audio/plasmabullet.wav", Sound.class);
		explosion = game.mgr.get("audio/explosion.mp3", Sound.class);
		landmineSheet = game.mgr.get("img/landmine_sheet.png", Texture.class);
	}

}
