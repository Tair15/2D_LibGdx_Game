package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Audio.AudioManager;
import com.mygdx.game.EntityComponentSystem.EntityCSEngine;
import com.mygdx.game.Map.MapManager;
import com.mygdx.game.Screen.AbstractScreen;
import com.mygdx.game.Screen.ScreenType;
import com.mygdx.game.UI.GameRenderer;
import com.mygdx.game.input.InputManager;

import java.util.EnumMap;

import sun.net.ResourceManager;

public class Game2D extends Game {
	private static final String TAG = Game2D.class.getSimpleName();
	private EnumMap<ScreenType, AbstractScreen> screenCache;

	private SpriteBatch spriteBatch;
	private OrthographicCamera gameCamera;
	private FitViewport screenViewport;

	private World world;
	private WorldContactListener worldContactListener;
	private Box2DDebugRenderer box2DDebugRenderer;


	private float accumulator;
	private AssetManager assetManager;
	private AudioManager audioManager;

	private Stage stage;
	private Skin skin;
	private I18NBundle i18NBundle;

	private InputManager inputManager;

	private EntityCSEngine entityCSEngine;
	private MapManager mapManager;
	private GameRenderer gameRenderer;

	//constants
	public static final BodyDef BODY_DEF = new BodyDef();
	public static final FixtureDef FIXTURE_DEF = new FixtureDef();
	public static final float FIXED_TIME_STEP = 1 / 60f;
	public static final float UNIT_SCALE = 1 / 32f;
	public static final short BIT_PLAYER = 1 << 0;
	public static final short BIT_GROUND = 1 << 1;
	public static final short BIT_GAME_OBJECT = 1 << 2;


	public Game2D() {
	}

	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		spriteBatch = new SpriteBatch();

		//box
		accumulator = 0;
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		worldContactListener = new WorldContactListener();
		world.setContactListener(worldContactListener);
		box2DDebugRenderer = new Box2DDebugRenderer();

		//initialize assetManager
		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));
		initializeSkin();
		stage = new Stage(new FitViewport(450,800), spriteBatch);


		//audio
		audioManager = new AudioManager(this);

		//input
		inputManager = new InputManager();
		Gdx.input.setInputProcessor(new InputMultiplexer(inputManager,stage));

		//setup game viewport
		gameCamera = new OrthographicCamera();
		screenViewport = new FitViewport(9, 16, gameCamera);

		//ecs engine
		entityCSEngine = new EntityCSEngine(this);

		//manager
		mapManager = new MapManager(this);

		//game renderer
		gameRenderer = new GameRenderer(this);

		//set first screen
		screenCache = new EnumMap<>(ScreenType.class);
		setScreen(ScreenType.LOADING);
	}
	public static void resetBodyFixtureDefinition() {
		BODY_DEF.position.set(0,0);
		BODY_DEF.gravityScale = 1;
		BODY_DEF.type = BodyDef.BodyType.StaticBody;
		BODY_DEF.fixedRotation =false;

		FIXTURE_DEF.density =0;
		FIXTURE_DEF.isSensor = false;
		FIXTURE_DEF.restitution = 0;
		FIXTURE_DEF.friction = 0.2f;
		FIXTURE_DEF.filter.categoryBits = 0x0001;
		FIXTURE_DEF.filter.maskBits = -1;
		FIXTURE_DEF.shape = null;
	}

	private void initializeSkin() {
		//setup marking colors
		Colors.put("Black" , Color.BLACK);
		Colors.put("Red", Color.RED);
		Colors.put("Blue", Color.BLUE);

		//generate ttf bitmaps
		final ObjectMap<String, Object> resources = new ObjectMap<>();
		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font.ttf"));
		final FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.minFilter = Texture.TextureFilter.Linear;
		fontParameter.magFilter = Texture.TextureFilter.Linear;

		final int[] sizesToCreate = {16,20,26,32};
		for(int size : sizesToCreate) {
			fontParameter.size = size;

			final BitmapFont bitmapFont = fontGenerator.generateFont(fontParameter);
			bitmapFont.getData().markupEnabled = true;
			resources.put("font_" + size, bitmapFont);
		}
		fontGenerator.dispose();

		// load skin
		final SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter("ui/hud.atlas", resources);
		assetManager.load("ui/hud.json", Skin.class, skinParameter);
		assetManager.load("ui/strings", I18NBundle.class);

		assetManager.finishLoading();
		skin = assetManager.get("ui/hud.json",Skin.class);
		i18NBundle =  assetManager.get("ui/strings",I18NBundle.class);
	}



	public WorldContactListener getWorldContactListener() {
		return worldContactListener;
	}


	public MapManager getMapManager() {
		return mapManager;
	}

	public EntityCSEngine getEntityCSEngine() {

		return entityCSEngine;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public InputManager getInputManager() {
		return inputManager;
	}

	public I18NBundle getI18NBundle() {
		return i18NBundle;
	}
	public Stage getStage() {
		return stage;
	}

	public Skin getSkin() {
		return skin;
	}

	public World getWorld() {
		return world;
	}

	public FitViewport getScreenViewport() {
		return screenViewport;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public OrthographicCamera getGameCamera() {
		return gameCamera;
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public Box2DDebugRenderer getBox2DDebugRenderer() {
		return box2DDebugRenderer;
	}

	public void setScreen(final ScreenType screenType) {

		final Screen screen = screenCache.get(screenType);

		if (screen == null) {
			try {

				Gdx.app.debug(TAG, "Creating new screen: " + screenType);
				Class<?> screenClass = screenType.getScreenClass();
				Constructor constructor = ClassReflection.getConstructor(screenClass, Game2D.class);

				AbstractScreen newScreen;
				newScreen = (AbstractScreen) constructor.newInstance(this);

				screenCache.put(screenType, newScreen);
				setScreen(newScreen);
			} catch (ReflectionException e) {
				throw new GdxRuntimeException("Screen " + screenType + " could not be created", e);
			}
		} else {
			Gdx.app.debug(TAG, "Switching to screen: " + screenType);
			setScreen(screen);
		}
	}

	@Override
	public void render() {
		super.render();

		final  float deltaTime = Math.min(0.25f, Gdx.graphics.getDeltaTime());
		entityCSEngine.update(deltaTime);
		accumulator += deltaTime;
		while (accumulator>= FIXED_TIME_STEP){
			world.step(FIXED_TIME_STEP, 6, 2);
			accumulator -= FIXED_TIME_STEP;
		}

		gameRenderer.render(accumulator/ FIXED_TIME_STEP);
		stage.getViewport().apply();
		stage.act(deltaTime);
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		box2DDebugRenderer.dispose();
		world.dispose();
		assetManager.dispose();
		spriteBatch.dispose();
		stage.dispose();
	}


}
