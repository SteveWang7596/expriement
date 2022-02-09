package za.co.transactionjunction.cardpaymentapi;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.server.HttpHandler;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gen.api.PathHandlerProvider;
import za.co.transactionjunction.imbeko.jdbc.data.handler.JunctionInstanceConfig;
import za.co.transactionjunction.imbeko.web.AWebJunction;
import za.co.transactionjunction.imbeko.web.ImbekoService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainVerticle extends AWebJunction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
	private static Map<String, Map<String, Undertow>> web_servers = new ConcurrentHashMap<>();
	private Config config;

	public MainVerticle(JunctionInstanceConfig junction_instance_config, ImbekoService imbeko_service) throws Exception
	{
		super(junction_instance_config, imbeko_service);
		this.config = new Config(junction_instance_config);
	}

	@Override
	public void start(Future<Void> future)
	{
		stopAllServersForJunctionInstance(junction_instance_id);
		startService(future);
	}

	private void stopAllServersForJunctionInstance(String junction_instance_id)
	{
		web_servers.computeIfPresent(junction_instance_id, (k, v) ->
		{
			v.forEach((deployment_id, undertow) ->
					{
						LOGGER.info("Stopping Undertow for junction instance {}, deployment ID {}.", junction_instance_id, deployment_id);
						undertow.stop();
					}
			);
			return null;
		});
	}

	@Override
	public void stop() throws Exception
	{
		super.stop();
		stopServerIfExistsForThisDeploymentID(this.junction_instance_id, deploymentID());
	}

	private void stopServerIfExistsForThisDeploymentID(String junction_instnace_id, String deployment_id)
	{
		web_servers.computeIfPresent(junction_instnace_id, (k, v) ->
		{
			v.computeIfPresent(deployment_id, (k2, undertow) ->
			{
				LOGGER.info("Stopping Undertow for junction instance {}, deployment ID {}.", junction_instnace_id, deployment_id);
				undertow.stop();
				return null;
			});
			return v;
		});
	}

	@SuppressWarnings("deprication")
	private void startService(Future<Void> future)
	{
		try
		{
			PathHandlerProvider pathHandlerProvider = new AcceptorAuthPathHandler(this.imbeko_service);
			HttpHandler httpHandler = pathHandlerProvider.getStatefulHandler();
			Builder builder = Undertow.builder();
			builder.addHttpListener(config.getPort(), config.getHost());
			Undertow web_server = builder.setHandler(httpHandler).build();
			web_server.start();
			web_servers.computeIfAbsent(junction_instance_id, k -> new ConcurrentHashMap<String, Undertow>())
					.put(deploymentID(), web_server);
			LOGGER.info("Starting Undertow for junction instance {}, deployment ID {}.", junction_instance_id,deploymentID());
			future.complete();
		}
		catch (Exception e)
		{
			future.fail(e);
		}
	}
}
