package ch.fha.ia02.above;

import java.awt.*;
import javax.vecmath.*;

/**
 * Factions in the galactic civil war.
 * <p>
 * Please note that enemy status needs not to be reciprocal
 * (though it usually is).
 * Such asymmetric enemy status might be of use to model
 * foodchain-like
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 */
public class Faction
{
	private String name;
	private Color3f color;

	private Faction(String name, Color3f color) {
		this.name = name;
		this.color = color;
	}

	/** Returns the name of this faction. */
	public String toString() {
		return name;
	}

	/** Returns the color assigned to this facation. */
	public Color3f getColor() {
		return color;
	}


	/**
	 * Determines if this faction considers the <var>other</var> faction
	 * to be an enemy, or not.
	 * <p>
	 * This implementation returns <code>true</code> if <var>other</var>
	 * is neither this faction nor <tt>NEUTRAL</tt>.
	 * In other words: Everybody who's neither me nor declaredly neutral
	 * is considered hostile and will be treated as such.
	 *
	 * @param other the faction we ask the status for.
	 * @return <code>true</code> if other should be treated as enemy.
	 */
	public boolean isEnemy(Faction other) {
		return other != this && other != NEUTRAL;
	}

	/**
	 * Determines if this faction is allied with <em>other</em>.
	 * This implememnation returns true if <em>other</em> is the
	 * the same faction as this.
	 *
	 * @param other the faction we ask the status for.
	 * @return <code>true</code> if other should be treated as ally.
	 */
	public boolean isAlly(Faction other) {
		return other == this;
	}



	/** Denotes neutral objects, such as planets and the like. */
	public static Faction NEUTRAL = new Faction("Neutral", new Color3f(0.8f, 0.8f, 0.8f)) {
		public boolean isEnemy(Faction other) { return false; }
	};

	/** Denotes vessels fighting for the Rebel Alliance. */
	public static Faction REBEL = new Faction("Rebel", new Color3f(1,0,0)) {
		public boolean isEnemy(Faction other) { return other == IMP; }
	};

	/** Denotes vessels fighting for the Galactic Empire. */
	public static Faction IMP = new Faction("Empire", new Color3f(0, 0, 0.8f)) {
		public boolean isEnemy(Faction other) { return other == REBEL; }
	};

}
