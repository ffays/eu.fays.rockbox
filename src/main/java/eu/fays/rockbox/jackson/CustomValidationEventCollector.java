package eu.fays.rockbox.jackson;

import static jakarta.xml.bind.ValidationEvent.ERROR;
import static java.text.MessageFormat.format;

import java.net.URL;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.util.ValidationEventCollector;

/**
 * A handler to handle errors during JAXB loading
 */
public class CustomValidationEventCollector extends ValidationEventCollector {
	
	private boolean valid = true;

	/**
	 * Tells if the XML file is valid
	 * @return either true or false;
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Returns the error message, may be null in case there are no errors
	 * @param resource the source file having the error
	 * @return the error message
	 */
	@SuppressWarnings("nls")
	public String getErrorMessage(final URL resource) {		
		if (isValid()) {
			return null;
		} else {
			final StringBuilder builder = new StringBuilder();
			builder.append(format("Invalid file ''{0}''!", resource));
			builder.append(System.lineSeparator());
			// choice output: (0: none, 1: one, 2: several)
			// severity == 0 : WARNING
			// severity == 1 : ERROR
			// severity >= 2 : FATAL_ERROR
			for (final ValidationEvent e : getEvents()) {
				 builder.append(format("\t{0,number,0}:{1,number,0}:{2,choice,0#WARNING|1#ERROR|2#FATAL_ERROR}:{3}\n", e.getLocator().getLineNumber(), e.getLocator().getColumnNumber(), e.getSeverity(), e.getMessage()));	
			}
			return builder.toString();
		}
	}

	/**
	 * @see jakarta.xml.bind.ValidationEventHandler#handleEvent(jakarta.xml.bind.ValidationEvent)
	 */
	public boolean handleEvent(final ValidationEvent event) {
		super.handleEvent(event);
		if(event.getSeverity() >= ERROR) {
			valid = false;
		}
		return true;
	}
}
