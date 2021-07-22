package eu.fays.rockbox.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;

public class XHTMLRendererEssay {

	public static void main(String[] args) throws Exception {
		final int rN = 100;
		final int cN = 15;
		final double randMax = 100_000d;
		final File file = File.createTempFile(XHTMLRendererEssay.class.getSimpleName(), ".pdf");
		try (final FileOutputStream stream = new FileOutputStream(file)) {
			final HtmlCanvas html = new HtmlCanvas();
			final HtmlAttributes style1 = html.attributes().style("border: 1px solid black;border-collapse: collapse");
			final HtmlAttributes style2 = html.attributes().style("border: 1px solid black;border-collapse: collapse; text-align: center");
			final HtmlAttributes style3 = html.attributes().style("border: 1px solid black;border-collapse: collapse; text-align: right");
			html.html();
			html.body();
			html.table(style1);
			// Header
			{
				html.tr();
				for(int c = 0; c<cN; c++) {
					html.th(style2).content(Character.toString(65+c));
				}
				html._tr();
			}
			
			for(int r = 0; r<rN; r++) {
				html.tr();
				for(int c = 0; c<cN; c++) {
					html.td(style3).content((int)(Math.random()*randMax));
				}
				html._tr();
			}
			html._table();
			html._body();
			html._html();

			final ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(html.toHtml());
			renderer.layout();
			renderer.createPDF(stream);
		}

		Desktop.getDesktop().open(file);
	}

}
