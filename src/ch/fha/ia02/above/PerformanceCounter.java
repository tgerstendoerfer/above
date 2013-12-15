package ch.fha.ia02.above;

import javax.media.j3d.*;

/**
 * A behavior that periodically computes performance statistics
 * and writes the current FPS to <em>stdout</em>.
 */
public class PerformanceCounter extends WorldBehavior {
	private static final int FRAMES = 100;
	private long last;

	/** Creates a new performance counter. */
	public PerformanceCounter() {
		super(new WakeupOnElapsedFrames(FRAMES));
		last = System.currentTimeMillis();
	}

	/** Computes the current FPS. */
	public void processStimulus(java.util.Enumeration criteria) {
		long curr = System.currentTimeMillis();
		int fps = (int)(FRAMES*1000/(curr-last));
		System.out.println(fps + " FPS");
		last = curr;
		wakeupOn(w);
	}
}
