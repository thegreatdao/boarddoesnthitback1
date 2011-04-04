package boards.dont.hit.back;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.widget.Toast;

public class Main extends BaseGameActivity implements IOnSceneTouchListener
{

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;

	private static final long CHANGE_FREQUENCY = 200l;
	private static final int BOTTOM_OF_BAR = 600;
	private static final int BAR_WIDTH = 150;
	private static final int BAR_HEIGHT = 16;
	private static final float OFF_SCREEN_X_Y = 1000f;
	private Camera mCamera;

	private Texture mTexture;
	private Texture barTexture;
	private Texture whiteBrickTexture;
	private Texture greenBrickTexture;
	private Texture blueBrickTexture;
	private Texture purpleBrickTexture;
	private Texture yellowBrickTexture;
	private Texture redBrickTexture;
	private TiledTextureRegion ballTextureRegion;
	private TextureRegion barTextureRegion;
	private TextureRegion whiteBrickTextureRegion;
	private TextureRegion greenBrickTextureRegion;
	private TextureRegion blueBrickTextureRegion;
	private TextureRegion purpleBrickTextureRegion;
	private TextureRegion yellowBrickTextureRegion;
	private TextureRegion redBrickTextureRegion;
	private List<TextureRegion> textureRegions = new ArrayList<TextureRegion>();
	private List<Sprite> bricks = new ArrayList<Sprite>();
	private Sprite bar;
	private boolean ballOffScreen;

	@Override
	public Engine onLoadEngine()
	{
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources()
	{
		this.mTexture = new Texture(64, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.barTexture = new Texture(256, BAR_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.whiteBrickTexture = new Texture(128, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.greenBrickTexture = new Texture(128, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.blueBrickTexture = new Texture(128, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.purpleBrickTexture = new Texture(128, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.yellowBrickTexture = new Texture(128, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.redBrickTexture = new Texture(128, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.ballTextureRegion = TextureRegionFactory.createTiledFromAsset( this.mTexture, this, "gfx/ball.png", 0, 0, 4, 1);
		this.barTextureRegion = TextureRegionFactory.createFromAsset( barTexture, this, "gfx/bar.png", 0, 0);
		this.whiteBrickTextureRegion = TextureRegionFactory.createFromAsset( whiteBrickTexture, this, "gfx/white.png", 0, 0);
		this.greenBrickTextureRegion = TextureRegionFactory.createFromAsset( greenBrickTexture, this, "gfx/green.png", 0, 0);
		this.blueBrickTextureRegion = TextureRegionFactory.createFromAsset( blueBrickTexture, this, "gfx/blue.png", 0, 0);
		this.purpleBrickTextureRegion = TextureRegionFactory.createFromAsset( purpleBrickTexture, this, "gfx/purple.png", 0, 0);
		this.yellowBrickTextureRegion = TextureRegionFactory.createFromAsset( yellowBrickTexture, this, "gfx/yellow.png", 0, 0);
		this.redBrickTextureRegion = TextureRegionFactory.createFromAsset( redBrickTexture, this, "gfx/red.png", 0, 0);
		addTextureRegionsToCollection();
		this.mEngine.getTextureManager().loadTextures(this.mTexture, barTexture, whiteBrickTexture, greenBrickTexture, blueBrickTexture, purpleBrickTexture, yellowBrickTexture, redBrickTexture);
	}

	private void addTextureRegionsToCollection()
	{
		textureRegions.add(whiteBrickTextureRegion);
		textureRegions.add(greenBrickTextureRegion);
		textureRegions.add(blueBrickTextureRegion);
		textureRegions.add(purpleBrickTextureRegion);
		textureRegions.add(yellowBrickTextureRegion);
		textureRegions.add(redBrickTextureRegion);
	}

	@Override
	public Scene onLoadScene()
	{
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

		int BAR_X = CAMERA_WIDTH / 2 - BAR_WIDTH / 2;
		final Ball ball = new Ball(BAR_X, BOTTOM_OF_BAR - 16, this.ballTextureRegion, bricks);
		ball.animate(CHANGE_FREQUENCY);
		bar = new Sprite(BAR_X, BOTTOM_OF_BAR, barTextureRegion)
		{
			private final float[] VELOCITIES_Y = new float[] { 300f, 280f, 260f, 240f, 220f, 200f, 250f };
			private final float[] VELOCITIES_X = new float[] { 300f, 280f, 260f, 240f, 220f, 200f, 270f };

			@Override
			protected void onManagedUpdate(float pSecondsElapsed)
			{
				float[] velocities = Main.this.getVelocities(VELOCITIES_X, VELOCITIES_Y);
				if (this.collidesWith(ball))
				{
					if (ball.getX() > this.getX() + BAR_WIDTH / 2)
					{
						ball.getmPhysicsHandler().setVelocity(velocities[0], -velocities[1]);
					} else
					{
						ball.getmPhysicsHandler().setVelocity(-velocities[0], -velocities[1]);
					}
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		IEntity lastChild = scene.getLastChild();
		lastChild.attachChild(ball);
		lastChild.attachChild(bar);
		createBricks(lastChild);
		scene.setOnSceneTouchListener(this);
		TimerHandler pUpdateHandler = new TimerHandler(0.1f, true,
				new ITimerCallback()
				{
					private boolean haveShownSuccessfulMessage;
					private boolean haveShownFailureMessage;

					@Override
					public void onTimePassed(TimerHandler pTimerHandler)
					{
						runOnUiThread(new Runnable()
						{

							@Override
							public void run()
							{
								if (ballOffScreen)
								{
									if (!haveShownFailureMessage)
									{
										Toast.makeText(Main.this, "You lose.", Toast.LENGTH_SHORT).show();
										haveShownFailureMessage = true;
									}
								} else
								{
									if (allBricksRemoved())
									{
										ball.mPhysicsHandler .setVelocity(0f, 0f);
										if (!haveShownSuccessfulMessage)
										{
											Toast.makeText( Main.this, "Congralulations, you won.", Toast.LENGTH_SHORT).show();
											haveShownSuccessfulMessage = true;
										}
									}
								}
							}

							private boolean allBricksRemoved()
							{
								for (Sprite brick : Main.this.bricks)
								{
									if (brick.getX() != Main.OFF_SCREEN_X_Y)
									{
										return false;
									}
								}
								return true;
							}
						});
					}
				});
		scene.registerUpdateHandler(pUpdateHandler);
		return scene;
	}

	private void createBricks(IEntity lastChild)
	{
		for (int i = 1; i < 4; i++)
		{
			int j = 0;
			for (int m = 0; m < 2; m++)
			{
				for (TextureRegion textureRegion : textureRegions)
				{
					Sprite brick = null;
					if (i == 0)
					{
						brick = new Sprite(1 * 100, 100 + 16 * j, textureRegion);
					} else
					{
						brick = new Sprite(i * 100, 100 + 16 * j, textureRegion.clone());
					}
					bricks.add(brick);
					lastChild.attachChild(brick);
					j++;
				}
			}
		}
	}

	@Override
	public void onLoadComplete()
	{

	}

	private class Ball extends AnimatedSprite
	{
		private final PhysicsHandler mPhysicsHandler;
		private final float[] VELOCITIES_Y = new float[] { 300f, 280f, 260f, 240f, 220f, 200f, 210f };
		private final float[] VELOCITIES_X = new float[] { 200f, 220f, 240f, 260f, 280f, 300f, 230f };
		private int currentIndex;
		private List<Sprite> bricks;

		public Ball(final float pX, final float pY, final TiledTextureRegion pTextureRegion, List<Sprite> bricks)
		{
			super(pX, pY, pTextureRegion);
			this.bricks = bricks;
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);
		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed)
		{
			boolean collidesWithBrick = false;
			for (int i = 0; i < bricks.size(); i++)
			{
				Sprite brick = bricks.get(i);
				if (collidesWith(brick))
				{
					float brickCenterX = brick.getWidth() / 2 + brick.getX();
					float brickCenterY = brick.getHeight() / 2 + brick.getY();
					float ballCenterX = getX() + getWidth() / 2;
					float ballCenterY = getY() + getHeight() / 2;
					float[] velocities = Main.this.getVelocities(VELOCITIES_X, VELOCITIES_Y);
					if (ballCenterX > brickCenterX)
					{
						if (ballCenterY > brickCenterY)
						{
							mPhysicsHandler.setVelocity(velocities[0], -velocities[1]);
						} else
						{
							mPhysicsHandler.setVelocity(velocities[0], velocities[1]);
						}
					} else
					{
						if (ballCenterY > brickCenterY)
						{
							mPhysicsHandler.setVelocity(-velocities[0], velocities[1]);
						} else
						{
							mPhysicsHandler.setVelocity(-velocities[0], -velocities[1]);
						}
					}
					i = bricks.size();
					brick.setPosition(Main.OFF_SCREEN_X_Y, Main.OFF_SCREEN_X_Y);
					collidesWithBrick = true;
				}
			}
			if (!collidesWithBrick)
			{
				boundaryCheck();
			}
			super.onManagedUpdate(pSecondsElapsed);
		}

		private void boundaryCheck()
		{
			currentIndex = currentIndex % VELOCITIES_Y.length;
			if (this.mX < 0)
			{
				this.mPhysicsHandler.setVelocityX(VELOCITIES_X[currentIndex]);
			} 
			else if (this.mX + this.getWidth() > CAMERA_WIDTH)
			{
				this.mPhysicsHandler.setVelocityX(-VELOCITIES_X[currentIndex]);
			}

			if (this.mY < 0)
			{
				this.mPhysicsHandler.setVelocityY(VELOCITIES_Y[currentIndex]);
			} 
			else if (this.mY + this.getHeight() > CAMERA_HEIGHT)
			{
				ballOffScreen = true;
			}
			currentIndex++;
		}

		public PhysicsHandler getmPhysicsHandler()
		{
			return mPhysicsHandler;
		}

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
		{
			float x = pSceneTouchEvent.getX();
			final int HALF_LENGTH = BAR_WIDTH / 2;
			float targetX = x - HALF_LENGTH;
			if (targetX < 0)
			{
				targetX = 0;
			}
			if (x + HALF_LENGTH >= CAMERA_WIDTH)
			{
				targetX = CAMERA_WIDTH - BAR_WIDTH;
			}
			bar.setPosition(targetX, BOTTOM_OF_BAR);
			return true;
		}
		return false;
	}

	public float[] getVelocities(float[] velocitiesX, float[] velocitiesY)
	{
		Random generator = new Random();
		int currentIndexX = generator.nextInt(velocitiesX.length);
		int currentIndexY = generator.nextInt(velocitiesY.length);
		return new float[]{ velocitiesX[currentIndexX], velocitiesY[currentIndexY] };
	}

}