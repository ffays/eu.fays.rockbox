package eu.fays.rockbox.jaxb.list;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static java.lang.Boolean.TRUE;
import static org.eclipse.persistence.oxm.annotations.XmlMarshalNullRepresentation.ABSENT_NODE;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.annotations.XmlNullPolicy;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SchoolClass {

	@XmlElement
	@XmlNullPolicy(nullRepresentationForXml = ABSENT_NODE)
	String teacher;

	// Wrapped without custom Adapter
	@XmlElementWrapper(name = "pupils0")
	@XmlElement(name="pupil")
	List<String> pupils0;
	
	// Wrapped with custom adapter + list wrapper
	@XmlJavaTypeAdapter(PupilsAdapter.class)
	@XmlElement
	List<String> pupils1;
	
	// Wrapped with custom combined adapter + list wrapper
	@XmlJavaTypeAdapter(CombinedPupilsAdapter.class)
	@XmlElement
	List<String> pupils2;

	// Wrapped with templated custom combined adapter + list wrapper
	@XmlJavaTypeAdapter(TemplatedPupilsAdapter.class)
	@XmlElement
	@XmlNullPolicy(nullRepresentationForXml = ABSENT_NODE)
	List<String> pupils3;
	
	
	/**
	 * Marshal to XML this instance into the given output file
	 * @param targetFile the output file
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final File targetFile) throws JAXBException {
		//
		assert targetFile != null;
		//

		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { getClass() }, null);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, targetFile);
	}
	/**
	 * Marshal to XML this instance into the given output file
	 * @param output the output stream
	 * @throws JAXBException in case of unexpected error
	 */
	public void marshal(final OutputStream output) throws JAXBException {
		//
		assert output != null;
		//
		
		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { getClass() }, null);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
		marshaller.marshal(this, output);
	}

	/**
	 * Unmarshal from XML the given input file
	 * @param sourceFile the input file
	 * @return a new instance
	 * @throws JAXBException in case of unexpected error
	 */
	public static SchoolClass unmarshal(final File sourceFile) throws JAXBException {
		//
		assert sourceFile != null;
		assert sourceFile.isFile();
		assert sourceFile.canRead();
		//

		final JAXBContext context = JAXBContextFactory.createContext(new Class<?> [] { SchoolClass.class }, null);

		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final SchoolClass result = (SchoolClass) unmarshaller.unmarshal(sourceFile);

		//
		assert result != null;
		//
		return result;
	}
	
	
}
