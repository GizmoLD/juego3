package com.brian.animacion;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MyGdxGame extends ApplicationAdapter {
	private static final int FRAME_COLS_WALK = 9, FRAME_COLS_IDLE = 3, FRAME_ROWS = 1;

	Animation<TextureRegion> walkAnimation, idleAnimation;
	Texture walkSheet, idleSheet, background;
	TextureRegion bgRegion;
	SpriteBatch spriteBatch;
	private OrthographicCamera camera;

	float stateTime;
	boolean isMoving = false;
	int horizontalDirection = 1;
	int verticalDirection = 1;
	float positionX, positionY;
	Rectangle up, down, left, right;
	final int IDLE = 0, UP = 1, DOWN = 2, LEFT = 3, RIGHT = 4;
	final float MOVEMENT_SPEED = 0.003f;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		spriteBatch = new SpriteBatch();

		background = new Texture(Gdx.files.internal("fondo.png"));
		background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
		bgRegion = new TextureRegion(background);
		positionX = 0;
		positionY = 0;

		up = new Rectangle(0, camera.viewportHeight * 2 / 3, camera.viewportWidth, camera.viewportHeight / 3);
		down = new Rectangle(0, 0, camera.viewportWidth, camera.viewportHeight / 3);
		left = new Rectangle(0, 0, camera.viewportWidth / 3, camera.viewportHeight);
		right = new Rectangle(camera.viewportWidth * 2 / 3, 0, camera.viewportWidth / 3, camera.viewportHeight);

		idleSheet = new Texture(Gdx.files.internal("quieto.png"));
		TextureRegion[][] tmpIdle = TextureRegion.split(idleSheet, idleSheet.getWidth() / FRAME_COLS_IDLE, idleSheet.getHeight() / FRAME_ROWS);
		TextureRegion[] idleFrames = new TextureRegion[FRAME_COLS_IDLE * FRAME_ROWS];
		int indexIdle = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS_IDLE; j++) {
				idleFrames[indexIdle++] = tmpIdle[i][j];
			}
		}
		idleAnimation = new Animation<TextureRegion>(0.12f, idleFrames);

		walkSheet = new Texture(Gdx.files.internal("caminando.png"));
		TextureRegion[][] tmpWalk = TextureRegion.split(walkSheet, walkSheet.getWidth() / FRAME_COLS_WALK, walkSheet.getHeight() / FRAME_ROWS);
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS_WALK * FRAME_ROWS];
		int indexWalk = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS_WALK; j++) {
				walkFrames[indexWalk++] = tmpWalk[i][j];
			}
		}
		walkAnimation = new Animation<TextureRegion>(0.12f, walkFrames);

		spriteBatch = new SpriteBatch();
		stateTime = 0f;
	}

	@Override
	public void render() {
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stateTime += Gdx.graphics.getDeltaTime();

		bgRegion.setRegion(positionX, positionY, camera.viewportWidth, camera.viewportHeight + positionY);

		TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
		TextureRegion currentFrameIdle = idleAnimation.getKeyFrame(stateTime, true);

		int spriteWidth = 25;
		int spriteHeight = 50;
		spriteBatch.begin();
		spriteBatch.draw(bgRegion, 0, 0);

		background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
		int touch = virtualJoystickControl();

		if (touch != IDLE) {
			isMoving = true;
			if (touch == UP) {
				verticalDirection = 1;
				positionY -= MOVEMENT_SPEED * 2;
			} else if (touch == LEFT) {
				horizontalDirection = -1;
				positionX -= MOVEMENT_SPEED * 2;
			} else if (touch == RIGHT) {
				horizontalDirection = 1;
				positionX += MOVEMENT_SPEED * 2;
			} else if (touch == DOWN) {
				verticalDirection = -1;
				positionY += MOVEMENT_SPEED * 2;
			}

		} else {
			isMoving = false;
		}

		if (isMoving) {
			spriteBatch.draw(currentFrame, camera.viewportWidth / 2 - 25 * horizontalDirection, camera.viewportHeight / 2 - 44, (spriteWidth + 50) * horizontalDirection, spriteHeight + 50);
		} else {
			spriteBatch.draw(currentFrameIdle, camera.viewportWidth / 2 - 25 * horizontalDirection, camera.viewportHeight / 2 - 44, (spriteWidth + 50) * horizontalDirection, spriteHeight + 50);
		}
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		walkSheet.dispose();
		idleSheet.dispose();
	}

	protected int virtualJoystickControl() {
		for (int i = 0; i < 10; i++)
			if (Gdx.input.isTouched(i)) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				camera.unproject(touchPos);
				if (up.contains(touchPos.x, touchPos.y)) {
					return UP;
				} else if (down.contains(touchPos.x, touchPos.y)) {
					return DOWN;
				} else if (left.contains(touchPos.x, touchPos.y)) {
					return LEFT;
				} else if (right.contains(touchPos.x, touchPos.y)) {
					return RIGHT;
				}
			}
		return IDLE;
	}
}
