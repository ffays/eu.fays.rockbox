package eu.fays.rockbox.pdf;

import static java.awt.Color.BLACK;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
import static org.vandeseer.easytable.settings.HorizontalAlignment.RIGHT;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Row.RowBuilder;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;
import org.vandeseer.easytable.structure.cell.TextCell.TextCellBuilder;

public class EasyTableEssay {
	public static void main(String[] args) throws IOException {
		final int rN = 50;
		final int cN = 15;
		final double randMax = 100_000d;

		final File file = File.createTempFile(EasyTableEssay.class.getSimpleName(), ".pdf");
		try (final PDDocument document = new PDDocument()) {
			final PDPage page = new PDPage(A4);
			document.addPage(page);

			try (final PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

				final TableBuilder tableBuilder = Table.builder();
				for(int c = 0; c<cN; c++) {
					tableBuilder
						.addColumnOfWidth(38f)
						.fontSize(8)
						.font(HELVETICA);
				}
				
				// Header
				{
					final RowBuilder rowBuilder = Row.builder();
					for(int c = 0; c<cN; c++) {
						final TextCellBuilder<?, ?> textCellBuilder = TextCell.builder();
						final String text = Character.toString(65+c);
						textCellBuilder.text(text).borderWidth(1).borderColorLeft(BLACK);
						rowBuilder.add(textCellBuilder.build());
					}
					tableBuilder.addRow(rowBuilder.build());
				}

				// Data
				for(int r = 0; r<rN; r++) {
					final RowBuilder rowBuilder = Row.builder();
					for(int c = 0; c<cN; c++) {
						final String text = Integer.toString((int)(Math.random()*randMax));
						final TextCellBuilder<?, ?> textCellBuilder = TextCell.builder();
						textCellBuilder.text(text).borderWidth(1).borderColorLeft(BLACK).horizontalAlignment(RIGHT);
						rowBuilder.add(textCellBuilder.build());
					}
					tableBuilder.addRow(rowBuilder.build());
				}
				
				// Build the table
				final Table table = tableBuilder.build();

				// Set up the drawer
				final TableDrawer tableDrawer = TableDrawer.builder()
					.contentStream(contentStream)
					.startX(20f)
					.startY(page.getMediaBox().getUpperRightY() - 20f)
					.table(table)
					.build();

				tableDrawer.draw();
			}

			try (final FileOutputStream fos = new FileOutputStream(file)) {
				document.save(fos);
			}
		}

		Desktop.getDesktop().open(file);
	}
}
