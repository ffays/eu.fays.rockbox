package eu.fays.rockbox.dx;

import java.net.MalformedURLException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public class SilentJavaScriptErrorListener implements JavaScriptErrorListener {

	@Override
	public void loadScriptError(HtmlPage arg0, URL arg1, Exception arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void malformedScriptURL(HtmlPage arg0, String arg1, MalformedURLException arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scriptException(HtmlPage arg0, ScriptException arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeoutError(HtmlPage arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
		// TODO Auto-generated method stub
		
	}

}
