package ch.akros.cc.elastic.connection;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Created by Patrick on 09.05.2017.
 */
public class ClientConnection implements IClientConnection {

	private final TransportClient client;

	public ClientConnection(final String server) throws UnknownHostException, MalformedURLException {
		final URL url = new URL(server);
		client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
				new InetSocketTransportAddress(InetAddress.getByName(url.getHost()), url.getPort()));
	}

	@Override
	public void disconnect() {
		if (client != null) {
			client.close();
		}
	}

	@Override
	public IndexResponse insertDocument(final String index, final String type, final String json) {
		return client.prepareIndex(index, type) //
				.setSource(json, XContentType.JSON) //
				.get();
	}

	@Override
	public SearchResponse search(final String[] indeces, final String[] types, final String query) {
		return client.prepareSearch(indeces) //
				.setTypes(types) //
				.setQuery(QueryBuilders.queryStringQuery(query).analyzeWildcard(true)) //
				.get();
	}
}
