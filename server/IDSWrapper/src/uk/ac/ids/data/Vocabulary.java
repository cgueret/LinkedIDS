package uk.ac.ids.data;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.turtle.RdfTurtleReader;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class Vocabulary {
	// Location of the vocabulary
	private final static String VOCAB_FILE = "war:///WEB-INF/rdf/linkedids-schema.ttl";

	// The graph that will contain the vocabulary triples
	private Graph graph = new Graph();

	// Singleton instance
	private static Vocabulary instance = null;

	/**
	 * 
	 */
	protected Vocabulary() {
	}

	/**
	 * @param context
	 * @return
	 */
	public static Vocabulary getInstance(Context context, Reference reference) {
		if (instance == null) {
			instance = new Vocabulary();
			try {
				Graph tmp = new Graph();
				Response r = context.getClientDispatcher().handle(new Request(Method.GET, VOCAB_FILE));
				GraphBuilder d = new GraphBuilder(tmp);
				RdfTurtleReader reader = new RdfTurtleReader(r.getEntity(), d);
				reader.parse();
				for (Link l : tmp) {
					Reference s = l.getSourceAsReference();
					if (s.isRelative())
						s = new Reference(reference, s);
					Reference p = l.getTypeRef();
					if (p.isRelative())
						p = new Reference(reference, p);
					if (l.hasReferenceTarget()) {
						Reference o = l.getTargetAsReference();
						if (o.isRelative())
							o = new Reference(reference, p);
						instance.graph.add(new Link(s, p, o));
					} else {
						instance.graph.add(new Link(s, p, l.getTargetAsLiteral()));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	/**
	 * @param reference
	 * @return
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * @param predicate
	 * @return
	 */
	public Reference getRange(Reference predicate) {
		for (Link l : graph)
			System.out.println(l);

		for (Link l : graph)
			if (l.getSource().equals(predicate))
				if (l.getTypeRef().equals(new Reference("http://www.w3.org/2000/01/rdf-schema#range")))
					return l.getTargetAsReference();

		return null;
	}
}
