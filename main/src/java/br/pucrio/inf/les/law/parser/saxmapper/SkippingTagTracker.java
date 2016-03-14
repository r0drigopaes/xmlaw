package br.pucrio.inf.les.law.parser.saxmapper;

import java.io.CharArrayWriter;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

public class SkippingTagTracker extends TagTracker {

	private static final Log LOG = LogFactory.getLog(SkippingTagTracker.class);

	// Tag trackers work together on a stack.
	// The tag tracker at the top of the stack
	// is the "active" tag tracker.
	//
	// This class represents a skipping place
	// marker on the stack. When a real tag
	// tracker places a skipping tag tracker on
	// the stack, that is an indication that
	// all tag names found during the skip are
	// of no intrest to the tag tracking network.
	//
	// This means that if the skipping tag tracker
	// is notified of a new tag name, this new
	// tag name should also be skipped.
	//
	// Since this class never varies it's behavior,
	// it is ok for it to skip new tag names by
	// placing itself on the stack again.
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr, Stack tagStack) {

		//
		// If the current tag name is being
		// skipped, all children should be
		// skipped.
		//

		LOG.trace("Skipping tag: [" + localName + "]");
		tagStack.push(this);

	}

	//
	// The skipping tag tracker has
	// nothing special to do when
	// a closing tag is found other
	// than to remove itself from
	// the stack, which as a side
	// effect replaces it with it's
	// parent as the "active", top
	// of stack tag tracker.
	//
	public void endElement(String namespaceURI, String localName, String qName,
			CharArrayWriter contents, Stack tagStack) {

		// Clean up the stack...
		LOG.trace("Finished skipping tag: [" + localName + "]");
		tagStack.pop();

	}

}
