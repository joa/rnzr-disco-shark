import defrac.app.*;
import defrac.animation.*;
import defrac.util.*;
import defrac.display.*;
import defrac.event.*;
import defrac.audio.*;


class main extends GenericApp {

	private final EventListener<EnterFrameEvent> onEnterFrame = new EventListener<EnterFrameEvent>() {
		@Override public void onEvent(EnterFrameEvent event) {
			updateFrame();
		}
	};

	//Time
	private int beatCount;
	private int tickCount; // 1/32
	private Timer timer = new Timer(476);
	private Timer tickTimer = new Timer(60);


	//Graphic containers
	private float w; //Width
	private float h; //Height [of app]
	private float offset = 10.0f; //spacing between objects
	private Layer lay;

	//Components
	private Quad background;
	private Quad kick, snare, hat, cymbal, synth, tom1, tom2;


	//Musical
	private float[] k1  = { 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };
	private float[] k2  = { 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f };
	private float[] nu  = { 0f };
	private float[] currentKickPattern = k1;
	private float[] currentSnarePattern = nu;
	

	protected void onCreate() {
		beatCount = 0;
		tickCount = 0;

		timer.listener(new Timer.SimpleListener() {
			public void onTimerTick(final Timer timer) {
				kick();
			}
		});

		tickTimer.listener(new Timer.SimpleListener() {
			public void onTimerTick(final Timer timer) {
				tick();
			}
		});

		
		//Set up Scene
		w = width();
		h = height();
		lay = addChild(new Layer());

		lay.listener(new DisplayObject.SimpleListener() {
			public void onPointerClick(DisplayObject target, defrac.display.event.UIActionEvent event) {
				System.exit(0);
			}
		});

		Events.onEnterFrame.add(onEnterFrame);
		
		//Background
		background = lay.addChild( new Quad(w, h, 0xffffffff) );
		background.moveTo(0,0);
		background.alpha( (float) 0.00);

		//Kick
		kick = lay.addChild( new Quad(w/8, w/8, 0xffaaff66) );
		kick.moveTo(w/2 - kick.width(), h/2).centerRegistrationPoint();

		//Snare
		snare = lay.addChild( new Quad(w/8, w/8, 0xffffaa33) );
		snare.moveTo(w/2 + snare.width(), h/2).centerRegistrationPoint();

		//Tom [the one in breaks]
		tom1 = lay.addChild( new Quad(w/8, w/16, 0xffccc0ff) );
		tom1.moveTo(w/2, h/2 - tom1.width() ).centerRegistrationPoint();

		//Tom2 [the gliding one]
		tom2 = lay.addChild( new Quad(w/16, w/16, 0xffffffff));
		tom2.moveTo(w/2, h/2 + kick.width() ).centerRegistrationPoint();



		//Play the music and the timer!
		new AudioPlayer("rnzr.mp3").play();
		timer.resetAndStart();
		tickTimer.resetAndStart();

		
	}

	private void kick() {
		//Animation.create(500.0f, Alpha.to(tom1, 0.5f)).start();
		beatCount++;

		currentKickPattern = k1;
		if ( beatCount == 30 || beatCount == 46 || beatCount == 78 ) {
			currentKickPattern = k2;
		} else if ( beatCount == 31 || beatCount == 62 || beatCount == 63 ) {
			currentKickPattern = nu;
		}

		currentSnarePattern = nu;
		if ( (beatCount > 32 && beatCount < 129) && (beatCount%2 == 1) && beatCount != 22 && beatCount != 23 ) {
			currentSnarePattern = k1;
		}

	}

	private void tick() {
		tickCount++;
		if ( currentKickPattern[ tickCount % currentKickPattern.length ] == 1f ) {
			kick.alpha(1); 
		}
		if ( currentSnarePattern[ tickCount % currentSnarePattern.length ] == 1f ) {
			snare.alpha(1); 
		}
		
	}

	private void updateFrame() {
		kick.alpha( kick.alpha() - 0.05f );
		snare.alpha( snare.alpha() - 0.05f );
	}

	public static void kill() {
		System.exit(0);
	}

	public static void main(String[] args) {
		Bootstrap.run(new main());
	}

}