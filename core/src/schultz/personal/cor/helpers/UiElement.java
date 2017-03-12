package schultz.personal.cor.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import schultz.personal.cor.main.CORGame;

public class UiElement {
	
	private Texture tex;
	
	private GlyphLayout textLayout;
	
	private float elementWidth;
	private float elementHeight;
	private float elementX;
	private float elementY;
	
	private int scale;
	
	private float textX;
	private float textY;
	
	private boolean isButton;
	private boolean isText;
	
	public UiElement(Texture tex, int scale) { // for images
		this.tex = tex;
		this.scale = scale;
		elementWidth = tex.getWidth();
		elementHeight = tex.getHeight();
	}
	
	public UiElement(Texture tex, String text, int scale,CORGame game) { // for buttons (with text)
		this.tex = tex;
		this.isButton = true;
		this.scale = scale;
		textLayout = new GlyphLayout(game.mainFont, text);
		elementWidth = tex.getWidth();
		elementHeight = tex.getHeight();
	}
	
	public UiElement(String text, int scale,CORGame game) { // for text
		this.isText = true;
		this.scale = scale;
		textLayout = new GlyphLayout(game.mainFont, text);
		elementWidth = textLayout.width;
		elementHeight = textLayout.height;
	}
	
	public float getWidth() {
		return elementWidth;
	}
	
	public float getHeight() {
		return elementHeight;
	}
	
	public void setElementX(float x) {
		elementX = x;
	}
	
	public void setElementY(float y) {
		elementY = y;
	}
	
	public void setTextX(float x) {
		textX = x;
	}
	
	public void setTextY(float y) {
		textY = y;
	}
	
	public float getElementX() {
		return elementX;
	}
	
	public float getElementY() {
		return elementY;
	}
	
	public float getTextX() {
		return textX;
	}
	
	public float getTextY() {
		return textY;
	}
	
	public Texture getTexture() {
		return tex;
	}
	
	public int getScale() {
		return scale;
	}
	
	public GlyphLayout getGlyphLayout() {
		return textLayout;
	}
	
	public boolean isButton() {
		return isButton;
	}
	
	public boolean isText() {
		return isText;
	}
}