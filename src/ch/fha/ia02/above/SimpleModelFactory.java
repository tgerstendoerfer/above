package ch.fha.ia02.above;

import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * Simple model factory, for testing purposes.
 *
 * @author Michael Muehlebach
 * @author Thomas Gerstendoerfer
 */
public class SimpleModelFactory implements ModelFactory
{
	/** The number of starfighters per squadron. */
	int nFighters = 12;

	private Set objects;
	private Random r = new Random();

	public Model createModel() {

		r.setSeed(2000); // restart the random generator
		objects = new HashSet();

		Vector3f v = new Vector3f();
		Vector3f p = new Vector3f();

		v.set(-96, -22, -327);
		for (int i=0; i<nFighters; i++) {
			p.set(nextCoord()+30, nextCoord() + 15, nextCoord() +80);
			addNewStarfighter(p, v, 0, Vessel.XWING, "Rogue "+(i+1));
		}

		v.set(-240, -192, 240);
		for (int i=0; i<nFighters; i++) {
			p.set(nextCoord() +700, nextCoord() +100, nextCoord() -5030);
			addNewStarfighter(p, v, 1, Vessel.TIEF, "PK-"+(i+401));
		}

		v.set(-4, -45, 375);
		for (int i=0; i<nFighters; i++) {
			p.set(nextCoord() -370, nextCoord() -180, nextCoord() -4020);
			addNewStarfighter(p, v, 2, Vessel.TIEF, "CK-"+(i+101));
		}

		// create an ISD, kinda
		v.set(0, 0, 5);
		p.set(-370, -140, -4020);
		addAgent(new CapitalShip(Vessel.ISD.getStats(), p, v), Vessel.ISD, 3, "Chimaera");

		p.set(700, -400, -4920);
		addAgent(new CapitalShip(Vessel.ISD.getStats(), p, v), Vessel.ISD, 3, "Virulence");

		v.set(-2f, 0.1f, 3);
		p.set(650, 100, -5000);
		addAgent(new CapitalShip(Vessel.ISD.getStats(), p, v), Vessel.ISD, 3, "Pulsar");


		// create background objects
		if (Application.getSettings().detailedShapes() &&
			Application.getSettings().loadTextures())
		{
			addSprite("Death Star", 1000, "/media/ds2.png", 650, 100, -12000);
			addSprite("Endor", 60000, "/media/endor.png", 7000, -29000, -22000);
		}

		return new Model(objects);
	}

	/**
	 * Factory method that creates a new starfighter and adds
	 * it to the computation model and the 3D world.
	 *
	 * @param p initial position of the starfighter.
	 * @param v initial velocity.
	 * @param group identifies the fighter's group/squadron.
	 * @param type specifies the fighter type.
	 * @param name a descriptive name.
	 */
	private void addNewStarfighter(
		Vector3f p,
		Vector3f v,
		int group,
		Vessel type,
		String name)
	{
		addAgent(new Starfighter((Starfighter.Stats)type.getStats(), p, v), type, group, name);
	}

	/**
	 * Factory method that adds a sprite to
	 * the model as well as to the world.
	 *
	 * @param name a descriptive name.
	 * @param size width of the sprite when inserted into the world.
	 * @param file name of the image file to load.
	 * @param x x-coordinate of the sprite's position.
	 * @param y y-coordinate of the sprite's position.
	 * @param z z-coordinate of the sprite's position.
	 */
	private void addSprite(
		String name,
		int size,
		String file,
		int x,
		int y,
		int z)
	{
		Vessel vessel = new Vessel(
			name,
			new AgentStats(size),
			null,
			file,
			1,
			true);
		Agent agent = new Agent(
			vessel.getStats(),
			new Vector3f(x, y, z),
			new Vector3f(0, 0, 1));
		addAgent(agent, vessel, -1, name);
	}


	/**
	 * Configures various options on a model objects and adds
	 * adds it to both, the comoutation model and the 3D world.
	 *
	 * @param o the model object to be configured and added.
	 * @param type specifies the fighter type.
	 * @param group identifies the vessel's group.
	 * @param name a descriptive name.
	 */
	private void addAgent(
		Agent agent, Vessel type, int group, String name)
	{
		agent.name = name != null ? name : type.getName();
		agent.group = group;
		agent.faction = factionFromGroup(group);
		objects.add(new ViewObject(agent, type));
	}

	/** Computes a pseudo-random coordinate offset. */
	private float nextCoord() {
		return r.nextFloat()*nFighters - nFighters/2;
	}

	/** Determines the faction from the supplied group code. */
	private static Faction factionFromGroup(int group) {
		switch (group) {
			case 0:
				return Faction.REBEL;

			case 1:
			case 2:
			case 3:
				return Faction.IMP;
		}
		return Faction.NEUTRAL;
	}

	public String toString() {
		return getClass().getName() + "[" + nFighters + " fighters per squadron]";
	}
}
