package eu.fays.rockbox.pdf;

import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;

public class PDFBoxEssay {

	public static void main(String[] args) throws Exception {
		final File file = File.createTempFile(PDFBoxEssay.class.getSimpleName(), ".pdf");
		try (final PDDocument document = new PDDocument()) {
			final FontName helveticBoldFontName = Standard14Fonts.getMappedFontName(FontName.HELVETICA_BOLD.getName());
			final PDFont helveticBoldFont=  new PDType1Font(helveticBoldFontName);
			final PDPage page = new PDPage(A4);
			document.addPage(page);
			try (final PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				contentStream.beginText();
				contentStream.setFont(helveticBoldFont, 12);
				contentStream.newLineAtOffset(100, 700);
				contentStream.showText("Hello World");
				contentStream.endText();
			}

			try (final FileOutputStream fos = new FileOutputStream(file)) {
				document.save(fos);
			}
		}

		Desktop.getDesktop().open(file);
	}

}
