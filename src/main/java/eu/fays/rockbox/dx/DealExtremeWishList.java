package eu.fays.rockbox.dx;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
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

			final List<Article> articles = new ArrayList<>();
			int pageIndex = 1;
			Document doc = fetchWishListPage(ua, pageIndex);
			while (doc.getElementsByTag("div").stream().filter(e -> "Page index out of range".equals(e.text())).count() == 0) {
				LOGGER.info(format("Page: {0,number,0}", pageIndex));
				for (Element anchorElement : doc.select("div.wishlist div.pi > p.title > a")) {
					final Matcher matcher = pattern.matcher(anchorElement.attr("href"));
					if (matcher.find()) {
						final Element divElement = anchorElement.parent().parent();
						final int sku = Integer.parseInt(matcher.group(1));
						final String description = anchorElement.text().trim();
						BigDecimal price = BigDecimal.ZERO;
						final Element priceElement = divElement.select("p.price").first();
						if (priceElement != null) {
							try {
								final String priceAsString = priceElement.text().replaceAll("[^\\p{Digit}.]", "");
								price = new BigDecimal(priceAsString);
							} catch (final NumberFormatException e) {
								// Do Nothing
							}
						}
						final boolean available = divElement.select("a.disable").isEmpty();

						final Article article = new Article(sku, available, price, description);
						articles.add(article);
						// LOGGER.info(article.toString());
					}

				}

				pageIndex++;
				doc = fetchWishListPage(ua, pageIndex);
			}

			final WishList wishList = new WishList(articles);
			wishList.marshal(new File("wishlist.xml"));
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
				.setRedirectsEnabled(true)
				.build();

		final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

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
		final ClassicHttpRequest httpGet = ClassicRequestBuilder.get()
				.setUri(new URI("https://passport.dx.com/?redirect=http%3A%2F%2Fwww.dx.com%2F"))
				.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "text/html"))
				.build();
		/* @formatter:on */

		// Open connection
		try (final CloseableHttpResponse response = ua.execute(httpGet)) {
			result = response.getCode();
			LOGGER.info(format("statusCode: {0}", result));
		}

		return result;
	}

	public static int performLogin(final CloseableHttpClient ua) throws IOException, URISyntaxException {
		int result = Integer.MAX_VALUE;

		final String login = System.getProperty("login");
		final String password = System.getProperty("password");
		if(login == null || password == null) {
			System.err.println("Either login or password has not been provided!");
			System.exit(1);
		}
		final List<BasicNameValuePair> form = Stream.of(new BasicNameValuePair("AccountName", login), new BasicNameValuePair("Password", password)).collect(toList());

		/* @formatter:off */
		final ClassicHttpRequest httpPost = ClassicRequestBuilder.post()
				.setUri(new URI("https://passport.dx.com/?redirect=http%3A%2F%2Fwww.dx.com%2F"))
				.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "text/html"))
				.setEntity(new UrlEncodedFormEntity(form, UTF_8))
				.build();
		/* @formatter:on */

		try (final CloseableHttpResponse response = ua.execute(httpPost)) {
			result = response.getCode();
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
		final ClassicHttpRequest httpGet = ClassicRequestBuilder.get()
				.setUri(new URI(format("https://my.dx.com/Wishlist/Index?pageIndex={0}", pageIndex)))
				.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "text/html"))
				.build();
		/* @formatter:on */

		try (final CloseableHttpResponse response = ua.execute(httpGet)) {
			final int statusCode = response.getCode();
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
