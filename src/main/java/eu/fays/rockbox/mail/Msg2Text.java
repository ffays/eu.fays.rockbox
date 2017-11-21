package eu.fays.rockbox.mail;

import static java.nio.file.Files.setAttribute;
import static java.nio.file.Files.walk;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.System.getProperty;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;

/**
 * Parameters:
 * <ul>
 * <li>source: source folder
 * <li>target: target folder
 * </ul>
 */
public class Msg2Text {
	public static void main(String[] args) throws Exception {
		final File sourceFolder = new File(getProperty("source"));
		final File targetFolder = new File(getProperty("target"));
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss X");
		final List<File> files = listMessages(sourceFolder);
		for (final File source : files) {
			final File target = new File(targetFolder.getAbsolutePath().concat(source.getAbsolutePath().substring(sourceFolder.getAbsolutePath().length()).replace(".msg", ".txt")));
			if (!target.exists()) {
				try (final MAPIMessage msg = new MAPIMessage(source)) {
					String text = null;
					try {
						text = msg.getTextBody().trim();
					} catch (final ChunkNotFoundException e) {
						// Do Nothing
					}

					if (text == null || text.isEmpty()) {
						try {
							text = msg.getHtmlBody().trim();
						} catch (final ChunkNotFoundException e) {
							// Do Nothing
						}
					}

					if (text == null || text.isEmpty()) {
						err.println("NO BODY\t" + source.getAbsolutePath());
						continue;
					}

					out.println("WRITE\t" + target.getAbsolutePath());
					target.getParentFile().mkdirs();
					final Calendar date = msg.getMessageDate();
					try (final PrintWriter pw = new PrintWriter(target)) {
						pw.println("Date: " + (date != null ? dateFormat.format(date.getTime()) : ""));
						pw.println("From: " + msg.getDisplayFrom());
						pw.println("To  : " + msg.getDisplayTo());
						if (msg.getDisplayCC() != null && !msg.getDisplayCC().isEmpty()) {
							pw.println("CC  : " + msg.getDisplayCC());
						}
						if (msg.getDisplayBCC() != null && !msg.getDisplayBCC().isEmpty()) {
							pw.println("BCC : " + msg.getDisplayBCC());
						}
						pw.println("Subj: " + msg.getSubject());
						for (final AttachmentChunks att : msg.getAttachmentFiles()) {
							if (att.getAttachLongFileName() != null) {
								pw.println("File: " + att.getAttachLongFileName().getValue());
							}
						}
						pw.println("------------------------------------------------------------------------");
						pw.println(text);
						pw.flush();
					}
					if (date != null) {
						target.setLastModified(date.getTimeInMillis());
						setAttribute(target.toPath(), "basic:creationTime", FileTime.fromMillis(date.getTimeInMillis()));
					} else {
						err.println("NO DATE\t" + source.getAbsolutePath());
					}
				}

			} else {
				out.println("SKIP\t" + source.getAbsolutePath());
			}
		}
	}

	/**
	 * Search all message under the given root directory matching the given file filter.
	 * @param root the root directory
	 * @return the list of file.
	 * @throws IOException if an I/O error is thrown when accessing the starting file.
	 */
	public static List<File> listMessages(final File root) throws IOException {
		try (final Stream<Path> stream = walk(root.toPath())) {
			final PathMatcher filter = root.toPath().getFileSystem().getPathMatcher("glob:**.msg");
			return unmodifiableList(stream.filter(filter::matches).map(p -> p.toFile()).collect(toList()));
		}
	}

}
