package za.co.transactionjunction.cardpaymentapi;

import io.undertow.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gen.api.PathHandlerProvider;
import za.co.transactionjunction.imbeko.web.ImbekoService;

import javax.annotation.Nonnull;

public class AcceptorAuthPathHandler extends PathHandlerProvider
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AcceptorAuthPathHandler.class);
	private ImbekoService imbekoService;

	public AcceptorAuthPathHandler(ImbekoService imbekoService)
	{
		this.imbekoService = imbekoService;
	}

	@Nonnull
	@Override
	public HttpHandler authorisation() {
		LOGGER.debug("Getting authorisation request from client ...");
		return new AuthHandler(imbekoService);
	}
}
