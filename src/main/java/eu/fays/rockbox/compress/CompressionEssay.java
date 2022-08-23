package eu.fays.rockbox.compress;

import static java.text.MessageFormat.format;
import static org.tukaani.xz.LZMA2Options.PRESET_MAX;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.apache.commons.io.IOUtils;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

public class CompressionEssay {

	public static void main(String[] args) throws Exception {
		final String source = System.getProperty("source");
		final File file = new File(source);
		final LZMA2Options lzma2Options = new LZMA2Options(PRESET_MAX);
		try(final FileInputStream fis = new FileInputStream(file); final ByteArrayOutputStream baos = new ByteArrayOutputStream(); final XZOutputStream xzos = new XZOutputStream(baos, lzma2Options)) {
			IOUtils.copy(fis, xzos);
			xzos.flush();
			byte[] byteArray = baos.toByteArray();
			final String base64 = Base64.getEncoder().encodeToString(byteArray);
			System.out.println(format("XZ: {0,number,.0}% = {1,number,0}/{2,number,0} - base64: {3,number,0}", (100F*(float)byteArray.length/(float)file.length()), byteArray.length, file.length(), base64.length()));
		}

	  final Deflater deflater = new Deflater(9);
		try(final FileInputStream fis = new FileInputStream(file); final ByteArrayOutputStream baos = new ByteArrayOutputStream(); final DeflaterOutputStream dos = new DeflaterOutputStream(baos, deflater)) {
			IOUtils.copy(fis, dos);
			dos.finish();
			byte[] byteArray = baos.toByteArray();
			final String base64 = Base64.getEncoder().encodeToString(byteArray);
			System.out.println(format("Deflate: {0,number,.0}% = {1,number,0}/{2,number,0} - base64: {3,number,0}", (100F*(float)byteArray.length/(float)file.length()), byteArray.length, file.length(), base64.length()));
		}
	}

}
