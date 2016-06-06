package eu.fays.rockbox.ibood;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.apache.http.conn.HttpHostConnectException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Hunt {

	private static final Logger LOGGER = Logger.getLogger(Hunt.class.getName());

	public static void main(String[] args) throws Exception {
		// @formatter:off
		List<String> keywords = Arrays.asList(new String[] {
				"whirlpool",
				"combimagnetron",
				"oyster",
				"memory",
				"card",
				"stick",
				"phone",
				"bluetooth",
				"keyboard",
				"mouse", "muis", "muizen", "sdhc", "kaart", "klasse",
				"speaker",
				"monster" });
		// @formatter:on
		final String[] urls = { "http://www.ibood.com/extra-be/nl/", "http://www.ibood.com/be/nl/" };

		// c.f. http://stackoverflow.com/questions/11273773/javafx-2-1-toolkit-not-initialized
		final CountDownLatch latch = new CountDownLatch(1);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new JFXPanel(); // initializes JavaFX environment
				latch.countDown();
			}
		});
		latch.await();

		try (final WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER)) {
			webClient.setIncorrectnessListener(new IncorrectnessListener() {

				@Override
				public void notify(String arg0, Object arg1) {

				}
			});
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setActiveXNative(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setGeolocationEnabled(false);

			out: while (true) {
				try {
					for (String url : urls) {
						HtmlPage page = webClient.getPage(url);
						String text = page.asText().toLowerCase();
						LOGGER.info("Pull: " + url);
						for (String keyword : keywords) {
							if (text.contains(keyword)) {
								LOGGER.info("Keyword: " + keyword);
								// c.f. http://stackoverflow.com/questions/6092200/how-to-fix-an-unsatisfiedlinkerror-cant-find-dependent-libraries-in-a-jni-pro
								// c.f. http://stackoverflow.com/questions/17023419/win-7-64-bit-dll-problems (required or Java FX : Visual C++ Redistributable Packages for Visual Studio 2013)
								final String userHome = System.getProperty("user.home").replace("C:", "").replace('\\', '/');
								final String soundURL = MessageFormat.format("file://{0}/Music/music.mp3", userHome);
								final Media media = new Media(soundURL);
								final MediaPlayer player = new MediaPlayer(media);
								player.play();
								break out;
							}
						}

					}
				} catch (HttpHostConnectException e) {
					LOGGER.warning(e.getMessage());
				}
				Thread.sleep(15000);
			}
			LOGGER.info("Sound is playing");
		}
	}

}
