package schultz.personal.cor.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class Tutorial implements Screen {
	
	private CORGame game;
	
	private Texture background;
	private Texture track;
	
	private int imgScale;
	
	public Tutorial(CORGame game) {
		this.game = game;
		
		game.cam.update();
		
		imgScale = 5;
		
		loadAssets();
	}

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0/255f, 0/255f, 0/255f, 0/255f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.batch.begin();
		
		game.batch.draw(background, 0, 0, background.getWidth()*imgScale, background.getHeight()*imgScale);
		
		game.batch.end();
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
		
	}
	
	private void loadAssets() {
		background = game.mgr.get("img/backgrnd_1.png", Texture.class);
		track = game.mgr.get("img/tile_map.png", Texture.class);
	}

}
