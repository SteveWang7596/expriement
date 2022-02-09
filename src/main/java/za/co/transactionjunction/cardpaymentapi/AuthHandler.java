package za.co.transactionjunction.cardpaymentapi;

import io.undertow.io.Receiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.transactionjunction.imbeko.web.ImbekoService;

import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

public class AuthHandler implements HttpHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthHandler.class);
	public ImbekoService imbekoService;

	public AuthHandler(ImbekoService imbekoService)
	{
		this.imbekoService = imbekoService;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception
	{
		exchange.getRequestReceiver().receiveFullBytes(new Receiver.FullBytesCallback() {
			@Override
			public void handle(HttpServerExchange exchange, byte[] body) {
				try {
					String body_string = new String(body, "UTF-8");
					LogRequest(exchange, body_string);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void LogRequest(HttpServerExchange exchange, String body)
	{

		final StringBuilder sb = new StringBuilder();
		final SecurityContext sc = exchange.getSecurityContext();
		sb.append("\n----------------------------REQUEST---------------------------\n");
		sb.append("               URI=" + exchange.getRequestURI() + "\n");
		sb.append(" characterEncoding=" + exchange.getRequestHeaders().get(Headers.CONTENT_ENCODING) + "\n");
		sb.append("     contentLength=" + exchange.getRequestContentLength() + "\n");
		sb.append("       contentType=" + exchange.getRequestHeaders().get(Headers.CONTENT_TYPE) + "\n");
		if (sc != null)
		{
			if (sc.isAuthenticated())
			{
				sb.append("          authType=" + sc.getMechanismName() + "\n");
				sb.append("         principle=" + sc.getAuthenticatedAccount().getPrincipal() + "\n");
			}
			else
			{
				sb.append("          authType=none" + "\n");
			}
		}

		Map<String, Cookie> cookies = exchange.getRequestCookies();
		if (cookies != null)
		{
			for (Map.Entry<String, Cookie> entry : cookies.entrySet())
			{
				Cookie cookie = entry.getValue();
				sb.append("            cookie=" + cookie.getName() + "=" +
						cookie.getValue() + "\n");
			}
		}
		for (HeaderValues header : exchange.getRequestHeaders())
		{
			for (String value : header)
			{
				LOGGER.trace("HeaderName is {}", header.getHeaderName().toString());
				if (!header.getHeaderName().toString().equalsIgnoreCase("X-API-KEY"))
				{
					sb.append("            header=" + header.getHeaderName() + "=" + value + "\n");
				}
				else
				{
					String reference = value.toString().substring(0, value.toString().indexOf('-'));
					String maskedPin = value.toString().substring(value.toString().indexOf('-') + 1).replaceAll(".", "*");
					sb.append("            header=" + header.getHeaderName() + "=" + reference + "-" + maskedPin + "\n");
				}
			}
		}
		sb.append("            locale=" + LocaleUtils.getLocalesFromHeader(exchange.getRequestHeaders().get(Headers.ACCEPT_LANGUAGE)) + "\n");
		sb.append("            method=" + exchange.getRequestMethod() + "\n");
		Map<String, Deque<String>> pnames = exchange.getQueryParameters();
		for (Map.Entry<String, Deque<String>> entry : pnames.entrySet())
		{
			String pname = entry.getKey();
			Iterator<String> pvalues = entry.getValue().iterator();
			sb.append("         parameter=");
			sb.append(pname);
			sb.append('=');
			while (pvalues.hasNext())
			{
				sb.append(pvalues.next());
				if (pvalues.hasNext())
				{
					sb.append(", ");
				}
			}
			sb.append("\n");
		}
		sb.append("          protocol=" + exchange.getProtocol() + "\n");
		sb.append("       queryString=" + exchange.getQueryString() + "\n");
		sb.append("        remoteAddr=" + exchange.getSourceAddress() + "\n");
		sb.append("        remoteHost=" + exchange.getSourceAddress().getHostName() + "\n");
		sb.append("            scheme=" + exchange.getRequestScheme() + "\n");
		sb.append("              host=" + exchange.getRequestHeaders().getFirst(Headers.HOST) + "\n");
		sb.append("        serverPort=" + exchange.getDestinationAddress().getPort() + "\n");
		sb.append("Body: \n");
		sb.append(body);
		LOGGER.trace("Message from client:\n{}", sb.toString());
	}
}
