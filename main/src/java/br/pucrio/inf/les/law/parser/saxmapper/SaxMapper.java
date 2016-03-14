package br.pucrio.inf.les.law.parser.saxmapper;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.pucrio.inf.les.law.model.LawException;

public abstract class SaxMapper extends DefaultHandler {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(SaxMapper.class);

    public abstract Object getMappedObject() throws LawException;

    public abstract TagTracker createTagTrackerNetwork();

    // A stack for the tag trackers to
    // coordinate on.
    //
    private Stack tagStack = new Stack();

    // The SAX 2 parser...
    private SAXParser saxParser;

    // Buffer for collecting data from
    // the "characters" SAX event.
    private CharArrayWriter contents = new CharArrayWriter();

    public SaxMapper() {
        try {
            // Create the XML reader...
            System.setProperty("org.xml.sax.driver",
                    "org.apache.xerces.parsers.SAXParser");

            /*
             * A version of the Xerces XML parser older than 2.7.0 present in
             * the endorsed directory of the Java runtime environment running
             * creates a conflict with the Xerces version that comes
             * with this project. A quick workaround is to send the parameter Code:
             * org.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XIncludeAwareParserConfiguration
             * to the Java virtual machine.
             * @see http://oxygenxml.com/forum/viewtopic.php?p=3454&sid=6ef807b444192086b73b93906bf87afd
             */
            System
                    .setProperty(
                            "org.apache.xerces.xni.parser.XMLParserConfiguration",
                            "org.apache.xerces.parsers.XIncludeAwareParserConfiguration");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setXIncludeAware(true);
            saxParser = factory.newSAXParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTagStack() {
        // Create the tag tracker network
        // and initialize the stack with
        // it.
        //
        // This constructor anchors the tag
        // tracking network to the begining
        // of the XML document. ( before the
        // first tag name is located ).
        //
        // By placing it first on the stack
        // all future tag tracking will follow
        // the network anchored by this
        // root tag tracker.
        //
        // The createTagTrackerNetwork() method
        // is abstract. All sub classes are
        // responsible for reacting to this
        // request with the creation of a
        // tag tracking network that will
        // perform the mapping for the sub class.
        //
        LOG.trace("Creating the tag tracker network.");
        tagStack.push(createTagTrackerNetwork());
        LOG.trace("Tag tracker network created.");
    }

    public Object fromXML(InputStream in, String inputLocation) throws IOException, SAXException, LawException 
    {
    	InputSource inputSource = new InputSource(in);
    	inputSource.setSystemId(inputLocation);
    	return fromXML(inputSource);
    }

    private synchronized Object fromXML(InputSource in) throws IOException, SAXException, LawException{

        // notes,
        // 1. The method is synchronized to keep
        // multiple threads from accessing the XML parser
        // at once. This is a limitation imposed by SAX.

        // Set the ContentHandler...

        // Parse the file...
        LOG.trace("About to parser XML document.");
        saxParser.parse(in, this);
        LOG.trace("XML document parsing complete.");

        return getMappedObject();
    }

    // Implement the content hander methods that
    // will delegate SAX events to the tag tracker network.

    public void startElement(String namespaceURI, String localName,
            String qName, Attributes attr) throws SAXException {

        // Resetting contents buffer.
        // Assuming that tags either tag content or children, not both.
        // This is usually the case with XML that is representing
        // data strucutures in a programming language independant way.
        // This assumption is not typically valid where XML is being
        // used in the classical text mark up style where tagging
        // is used to style content and several styles may overlap
        // at once.
        contents.reset();

        // delegate the event handling to the tag tracker
        // network.
        TagTracker activeTracker = (TagTracker) tagStack.peek();
        try {
            activeTracker.startElement(namespaceURI, localName, qName, attr,
                    tagStack);
        } catch (LawException e) {
            LOG.error(e.getMessage(),e);
        } catch (Exception e) {
        	LOG.error(e.getMessage(),e);
		}

    }

    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {

        // delegate the event handling to the tag tracker
        // network.
        TagTracker activeTracker = (TagTracker) tagStack.peek();
        activeTracker.endElement(namespaceURI, localName, qName, contents,
                tagStack);

    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // accumulate the contents into a buffer.
        contents.write(ch, start, length);

    }

}