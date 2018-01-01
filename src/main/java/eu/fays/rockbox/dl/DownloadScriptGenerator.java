package eu.fays.rockbox.dl;

import static eu.fays.rockbox.dx.DealExtremeWishList.createHttpClient;
import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;
import static java.lang.System.out;
import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.getCommonPrefix;
import static java.util.regex.Pattern.compile; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;

/**
 * Generates a Bash script to download a chunked video.<br>
 * <br>
 * The URI to the play list must be provided as "url" parameter<br>
 * <br>
 * E.g. -Durl=http://example.com/playlist.m3u8
 */
public class DownloadScriptGenerator {

	/**
	 * C.f. class description 
	 * @param args program arguments
	 * @throws Exception in case of unexpected error
	 */
	public static void main(String[] args) throws Exception {
		final String url = getProperty("url");
		try (final CloseableHttpClient ua = createHttpClient()) {
			final URI uri = new URI(url);
			final List<String> list = fetchPlayList(ua, uri);
			/* @formatter:off */
			final List<String> lines = list.stream()
					.filter(l -> l.length() > 0 && l.charAt(0) != '#')
					.collect(toList());
			/* @formatter:on */
			final String line0 = lines.get(0);
			final String line1 = lines.get(1);
			final String lineN = lines.get(lines.size()-1);
			final String prefix = getCommonPrefix(line0, line1);
			final String suffix = new StringBuilder(getCommonPrefix(new StringBuilder(line0).reverse().toString(), new StringBuilder(line1).reverse().toString())).reverse().toString(); 
			final boolean isPrefixURI = prefix.startsWith("http");
			final String basename = isPrefixURI?prefix.substring(prefix.lastIndexOf('/')+1):prefix;
			

			final Pattern pattern = compile(format("\\Q{0}\\E(\\d+)\\Q{1}\\E", prefix, suffix));
			final Matcher matcher = pattern.matcher(lineN);
			if (matcher.find()) {
				final int n = parseInt(matcher.group(1));
				final String path = uri.getPath().substring(0, uri.getPath().lastIndexOf('/') + 1);
				/* @formatter:off */
				final URI uriBase = new URIBuilder()
					.setScheme(uri.getScheme())
					.setHost(uri.getHost())
					.setPort(uri.getPort())
					.setPath(path)
					.build();
				/* @formatter:on */

				/* @formatter:off */
				final String command = format("# for i in $(seq 1 {0,number,0}); do curl -s -S -w ''%'{'filename_effective'}'\\n'' -o '\"'{1}$i{2}'\"' '\"'{3}{4}$i{2}'\"'; done"
					, n
					, basename
					, suffix
					, isPrefixURI?prefix:uriBase.toString()
					, isPrefixURI?"":basename);
				/* @formatter:on */

				/* @formatter:off */				
				list.stream()
					.map(l -> {
						if(l.length() > 0 && l.charAt(0) != '#') {
							final Matcher m = pattern.matcher(l);
							if(m.find()) {
								final int i = parseInt(m.group(1));
								return format("{0}{1,number,0}{2}", basename, i, suffix);
							} else {
								return "";
							}
						} else {
							return l;
						}
					})
					.forEach(l -> out.println(l));
				/* @formatter:on */
				out.println(command);
			}
		}
	}

	/**
	 * Fetches the play list
	 * @param ua the user agent robot
	 * @param uri the URI of the play list
	 * @return the content of the play list
	 * @throws IOException in case of unexpected error
	 * @throws URISyntaxException in case of unexpected error
	 */
	public static List<String> fetchPlayList(final CloseableHttpClient ua, final URI uri)
			throws IOException, URISyntaxException {
		List<String> result = new ArrayList<>();

		/* @formatter:off */
		final HttpUriRequest httpGet = RequestBuilder
			.get()
			.setUri(uri)
			.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "text/html"))
			.build();
		/* @formatter:on */

		try (final CloseableHttpResponse response = ua.execute(httpGet)) {
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode >= 400) {
				LOGGER.warning(format("statusCode: {0}", statusCode));
				return null;
			}

			final HttpEntity responseEntity = response.getEntity();
			assert responseEntity != null;
			try (final InputStream is = responseEntity.getContent();
					final InputStreamReader isr = new InputStreamReader(is, "UTF-8");
					final BufferedReader reader = new BufferedReader(isr)) {
				String line = reader.readLine();
				while (line != null) {
					result.add(line);
					line = reader.readLine();
				}
			}
		}
		return unmodifiableList(result);
	}

	/** Standard logger */
	static final Logger LOGGER = Logger.getLogger(DownloadScriptGenerator.class.getName());
}
