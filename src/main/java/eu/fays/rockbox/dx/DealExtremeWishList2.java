package eu.fays.rockbox.dx;

import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;

public class DealExtremeWishList2 {

	public static void main(String[] args) throws Exception {
	    try (final WebClient webClient = new WebClient()) {
	    	webClient.getOptions().setThrowExceptionOnScriptError(false);
	    	webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	    	webClient.setCssErrorHandler(new SilentCssErrorHandler());    
	    	webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener(){});
	    	
			final String login = System.getProperty("login");
			final String password = System.getProperty("password");
			if (login == null || password == null) {
				System.err.println("Either login or password has not been provided!");
				System.exit(1);
			}
			
	    	// Open Login page
	    	final String isvalidateCode;
	    	final String verificationCode;
	        final HtmlPage loginPage = webClient.getPage("https://home.dx.com/users/login?redirect=%2F%2Fwww.dx.com");
	    	{
		        final String html = loginPage.asXml();	        
				final Document document = org.jsoup.Jsoup.parse(html);
				isvalidateCode = document.select("#isvalidateCode").attr("value");
				verificationCode = document.select("#VerificationCode").attr("value");
	    	}
	    	
	    	// Perform login
	    	final HtmlPage homePage;
	    	{
	    		final HtmlForm form = (HtmlForm) loginPage.getElementById("login-form");
	    		final HtmlEmailInput accountNameField = (HtmlEmailInput) loginPage.getElementById("AccountName");
	    		final HtmlPasswordInput passwordField = (HtmlPasswordInput) loginPage.getElementById("PasswordLogin");
	    		final HtmlAnchor loginButton = (HtmlAnchor) loginPage.getElementById("SignIn");
	            accountNameField.setValueAttribute(login);
	            passwordField.setValueAttribute(password);
	            homePage = loginButton.click();
		        System.out.println(homePage.asXml());
		        System.out.println("--------------------------------------------------");
	    	}


	    	final HtmlPage wishlistPage = webClient.getPage("https://home.dx.com/Wishlist/index.html");
	        System.out.println(isvalidateCode);
	        System.out.println(verificationCode);
	        System.out.println(wishlistPage.asXml());
	    }
	}

}
