/**
 * 
 */
package uk.ac.ids.linker.impl;

import org.restlet.data.Reference;

import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
// http://www.lexvo.org/linkeddata/tutorial.html
public class Lexvo extends Linker {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ids.linker.Linker#getFromService(uk.ac.ids.linker.LinkerParameters)
	 */
	@Override
	protected Reference getFromService(LinkerParameters parameters) {
		return null;
	}
}
