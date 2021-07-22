package eu.fays.rockbox.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PDFBoxEssay {

	public static void main(String[] args) throws Exception {
		final File file = File.createTempFile(PDFBoxEssay.class.getSimpleName(), ".pdf");
		try (final FileOutputStream stream = new FileOutputStream(file)) {

			// Create a document and add a page to it
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			document.addPage(page);

			// Create a new font object selecting one of the PDF base fonts
			PDFont font = PDType1Font.HELVETICA_BOLD;

			// Start a new content stream which will "hold" the to be created content
			PDPageContentStream contentStream = new PDPageContentStream(document, page);

			// Define a text content stream using the selected font, moving the cursor and
			// drawing the text "Hello World"
			contentStream.beginText();
			contentStream.setFont(font, 12);
			contentStream.newLineAtOffset(100, 700);
			contentStream.showText("Hello World");
			contentStream.endText();

			// Make sure that the content stream is closed:
			contentStream.close();

			// Save the results and ensure that the document is properly closed:
			document.save(stream);
		}

		Desktop.getDesktop().open(file);
	}

}
