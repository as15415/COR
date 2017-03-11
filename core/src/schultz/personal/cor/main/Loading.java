package schultz.personal.cor.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Loading implements Screen {
	
	private CORGame game;
	private GlyphLayout loadingLayout;
	
	private float progress;
	
	public Loading(CORGame game) {
		this.game = game;
		
		loadingLayout = new GlyphLayout(game.mainFont, "Loading...");
		
		game.cam.update();
		
		progress = 0f;
		
		queueAssets();
	}
	
	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		int rectWidth = (Gdx.graphics.getWidth() - 100);
		
		Gdx.gl.glClearColor(141/255f, 141/255f, 141/255f, 255/255f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
		
		game.batch.begin();
		game.mainFont.draw(game.batch, loadingLayout, (Gdx.graphics.getWidth() - loadingLayout.width)/2, ((Gdx.graphics.getHeight() - loadingLayout.height)/2) + (Gdx.graphics.getHeight() - 500));
		game.batch.end();
		
		
		game.shape.begin(ShapeType.Filled);
		game.shape.setColor(Color.SLATE);
		game.shape.rect(((Gdx.graphics.getWidth() - rectWidth)/2), ((Gdx.graphics.getHeight() - rectWidth)/2), rectWidth * progress, 150);
		game.shape.end();
		
		update(delta);
	}
	
	public void update(float delta) {
		progress = game.mgr.getProgress();
		
		if(game.mgr.update()) {
			game.setScreen(new MainMenu(game));
		}
		
		Gdx.app.log("FPS", String.valueOf(Gdx.graphics.getFramesPerSecond()));
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
	
	private void queueAssets() {
		
	}

}
