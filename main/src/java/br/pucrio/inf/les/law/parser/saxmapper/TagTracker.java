package br.pucrio.inf.les.law.parser.saxmapper;

import java.io.CharArrayWriter;
import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.model.LawException;

public class TagTracker {

	private static final Log LOG = LogFactory.getLog(TagTracker.class);

	// Table of tag trackers.
	// This table contains an entry for
	// every tag name that this TagTracker
	// has been configured to follow.
	// This is a single-level parent-child relation.
	//
	private Hashtable trackers = new Hashtable();

	// Useful for skipping tag names that are not
	// being tracked.
	private static SkippingTagTracker skip = new SkippingTagTracker();

	// default constructor
	public TagTracker() {
	}

	// Configuration method for setting up a network
	// of tag trackers...
	// Each parent tag name should be configured
	// ( call this method ) for each child tag name
	// that it will track.
	public void track(String tagName, TagTracker tracker) {

		int slashOffset = tagName.indexOf("/");

		if (slashOffset < 0) {
			// if it is a simple tag name ( no "/" seperators )
			// simply add it.
			trackers.put(tagName, tracker);

		} else if (slashOffset == 0) {
			// Oooops leading slash, remove it and
			// try again recursivley.
			track(tagName.substring(1), tracker);

		} else {
			// if it is not a simple tag name
			// recursively add the tag.
			String topTagName = tagName.substring(0, slashOffset);
			String remainderOfTagName = tagName.substring(slashOffset + 1);
			TagTracker child = (TagTracker) trackers.get(topTagName);
			if (child == null) {
				// Not currently tracking this
				// tag. Add new tracker.
				child = new TagTracker();
				trackers.put(topTagName, child);
			}
			child.track(remainderOfTagName, tracker);

		}

	}

	// Tag trackers work together on a stack.
	// The tag tracker at the top of the stack
	// is the "active" tag tracker and is responsible
	// for delegating the tracking to a child tag
	// tracker or putting a skipping place marker on the
	// stack.
	//
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr, Stack tagStack) throws LawException {

		// Lookup up the tag name in the tracker table.
		// Note, this implementation does not address
		// using XML name space support that is now available
		// with SAX2.
		// We are simply using the localName as a key
		// to find a possible tracker.
		TagTracker tracker = (TagTracker) trackers.get(localName);

		//
		// Are we tracking this tag name?
		//
		if (tracker == null) {
			// Not tracking this
			// tag name. Skip the
			// entire branch.
			LOG.trace("Skipping tag: [" + localName + "]");
			tagStack.push(skip);
		} else {

			// Found a tracker for this
			// tag name. Make it the
			// new top of stack tag
			// tracker
			LOG.trace("Tracking tag: [" + localName + "]");

			// Send the deactivate event to this tracker.
			LOG.trace("Deactivating current tracker.");
			onDeactivate();

			// Send the on start to the new active
			// tracker.
			LOG.trace("Sending start event to [" + localName + "] tracker.");
			tracker.onStart(namespaceURI, localName, qName, attr);
			tagStack.push(tracker);

		}

	}

	// Tag trackers work together on a stack.
	// The tag tracker at the top of the stack
	// is the "active" tag tracker and is responsible
	// for reestablishing it's parent tag tracker
	// ( next to top of stack ) when it has
	// been notified of the closing tag.
	//
	public void endElement(String namespaceURI, String localName, String qName,
			CharArrayWriter contents, Stack tagStack) {

		// Send the end event.
		LOG.trace("Finished tracking tag: [" + localName + "]");
		onEnd(namespaceURI, localName, qName, contents);

		// Clean up the stack...
		tagStack.pop();

		// Send the reactivate event.
		TagTracker activeTracker = (TagTracker) tagStack.peek();
		if (activeTracker != null) {
			LOG.trace("Reactivating previous tag tracker.");
			activeTracker.onReactivate();
		}

	}

	// Methods for collecting content. These methods
	// are intended to be overridden with specific
	// actions for nodes in the tag tracking network
	// that require

	public void onStart(String namespaceURI, String localName, String qName,
			Attributes attr) throws LawException {

		// default is no action...
	}

	public void onDeactivate() {

		// default is no action...
	}

	public void onEnd(String namespaceURI, String localName, String qName,
			CharArrayWriter contents) {

		// default is no action...
	}

	public void onReactivate() {

		// default is no action...
	}

}