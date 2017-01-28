package eu.fays.rockbox.dx;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DealExtremeWishList {
	public static void main(String[] args) throws Exception {
		try (final CloseableHttpClient ua = createHttpClient()) {
			int rc0 = openLoginPage(ua);
			assert rc0 == 200;
			int rc1 = performLogin(ua);
			assert rc1 < 400;

			final Pattern pattern = Pattern.compile("http://www.dx.com/p/(\\d+)");
			
			int pageIndex=47;
			Document doc = fetchWishListPage(ua, pageIndex);
			while(doc.getElementsByTag("div").stream().filter(e -> "Page index out of range".equals(e.text())).count() == 0) {
				for (Element anchorElement : doc.select("div.wishlist div.pi > p.title > a")) {					
					Matcher matcher = pattern.matcher(anchorElement.attr("href"));
					if(matcher.find()) {
						final Element divElement = anchorElement.parent().parent();
						final int sku = Integer.parseInt(matcher.group(1));
						final String description = anchorElement.text().trim();
						Element priceElement = divElement.select("p.price").first();
						String price = "na";
						if(priceElement != null) {
								price = priceElement.text();
						}
						boolean disabled = divElement.select("a.disable").size() > 0;
						LOGGER.info(format("{0,number,0} - {1} - {2} - {3}", sku, price, disabled, description));
					}

				}

				break;
//				pageIndex++;
//				doc = fetchWishListPage(ua, pageIndex);
			}
		}
	}

	public static CloseableHttpClient createHttpClient() {
		// c.f. http://www.baeldung.com/httpclient-custom-http-header
		/* @formatter:off */
		final List<Header> headers = Collections.unmodifiableList(Arrays.asList(new Header[] {
				new BasicHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8"),
				new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip;q=1.0,identity;q=0.5")
		}));

		final RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(-1)
				.setRedirectsEnabled(true)
				.build();

		final ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setCharset(Consts.UTF_8)
				.build();

		final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setDefaultConnectionConfig(connectionConfig);

		final CloseableHttpClient result = HttpClients.custom()
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig)
				.setDefaultHeaders(headers)
				.build();
		/* @formatter:on */

		return result;
	}

	public static int openLoginPage(final CloseableHttpClient ua) throws IOException, URISyntaxException {
		int result = Integer.MAX_VALUE;
		/* @formatter:off */
		final HttpUriRequest httpGet = RequestBuilder.get()
				.setUri(new URI("https://passport.dx.com/?redirect=http%3A%2F%2Fwww.dx.com%2F"))
				.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "text/html"))
				.build();
		/* @formatter:on */

		// Open connection
		try (final CloseableHttpResponse response = ua.execute(httpGet)) {
			result = response.getStatusLine().getStatusCode();
			LOGGER.info(format("statusCode: {0}", result));
		}

		return result;
	}

	public static int performLogin(final CloseableHttpClient ua) throws IOException, URISyntaxException {
		int result = Integer.MAX_VALUE;

		final String login = System.getProperty("login");
		final String password = System.getProperty("password");
		final List<BasicNameValuePair> form = Stream.of(new BasicNameValuePair("AccountName", login), new BasicNameValuePair("Password", password)).collect(toList());

		/* @formatter:off */
		final HttpUriRequest httpPost = RequestBuilder.post()
				.setUri(new URI("https://passport.dx.com/?redirect=http%3A%2F%2Fwww.dx.com%2F"))
				.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "text/html"))
				.setEntity(new UrlEncodedFormEntity(form, Consts.UTF_8))
				.build();
		/* @formatter:on */

		try (final CloseableHttpResponse response = ua.execute(httpPost)) {
			result = response.getStatusLine().getStatusCode();
			LOGGER.info(format("statusCode: {0}", result));

			final HttpEntity responseEntity = response.getEntity();
			assert responseEntity != null;
			try (final InputStream in = responseEntity.getContent();) {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(in, baos);
				final String html = baos.toString();
				final Document document = org.jsoup.Jsoup.parse(html);
				assert !"DX Account Login".equals(document.title());
			}
		}

		return result;
	}

	public static Document fetchWishListPage(final CloseableHttpClient ua, final int pageIndex) throws IOException, URISyntaxException {
		Document result = null;

		/* @formatter:off */
		final HttpUriRequest httpGet = RequestBuilder.get()
				.setUri(new URI(format("https://my.dx.com/Wishlist/Index?pageIndex={0}", pageIndex)))
				.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "text/html"))
				.build();
		/* @formatter:on */

		try (final CloseableHttpResponse response = ua.execute(httpGet)) {
			final int statusCode = response.getStatusLine().getStatusCode();
			LOGGER.info(format("statusCode: {0}", statusCode));

			if (statusCode >= 400) {
				return null;
			}

			final HttpEntity responseEntity = response.getEntity();
			assert responseEntity != null;
			try (final InputStream in = responseEntity.getContent();) {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(in, baos);
				final String html = baos.toString();
				result = org.jsoup.Jsoup.parse(html);
			}
			return result;
		}

	}

	static final Logger LOGGER = Logger.getLogger(DealExtremeWishList.class.getName());
}
