package ch.fha.ia02.above;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * The root of all objects that are contained in a model.
 *
 * @author Thomas Gerstendoerfer
 * @author Michael Muehlebach
 *
 * @see Model
 */
public class ModelGroup extends BranchGroup
{
	/**
	 * Creates a new ModelGroup.
	 */
	public ModelGroup() {
		super();
		setCapability(ALLOW_DETACH);
		setCapability(ALLOW_CHILDREN_WRITE);
	}
}
