package eu.fays.rockbox.jackson;

import static java.text.MessageFormat.format;

import java.net.URL;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.util.ValidationEventCollector;

@SuppressWarnings({"serial", "nls"})
public class CustomXmlException extends Exception {
	private final ValidationEventCollector validationEventCollector;
	
	public boolean hasValidationEventCollector() {
		return validationEventCollector != null;
	}
	
	public ValidationEventCollector getValidationEventCollector() {
		return validationEventCollector;
	}

	public JAXBException getException() {
		return (JAXBException) getCause();
	}

	public CustomXmlException(final String message) {
		super(message);
		this.validationEventCollector = null;
	}

	public CustomXmlException(final URL resource, final JAXBException cause) {
		super(format("Invalid file ''{0}''!", resource), cause);
		this.validationEventCollector = null;
	}

	public CustomXmlException(final URL resource, final CustomValidationEventCollector validationEventCollector) {
		super(validationEventCollector.getErrorMessage(resource));
		this.validationEventCollector = validationEventCollector;
	}
	
	public CustomXmlException(final CustomValidationEventCollector validationEventCollector) {
		this.validationEventCollector = validationEventCollector;	
	}
}
