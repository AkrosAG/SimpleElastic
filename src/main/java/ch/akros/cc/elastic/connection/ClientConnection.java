package ch.akros.cc.elastic.connection;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by Patrick on 09.05.2017.
 */
public class ClientConnection {

    private TransportClient client;

    public ClientConnection(final String server) throws UnknownHostException, MalformedURLException {
        final URL url = new URL(server);
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(url.getHost()), url.getPort()));
    }

    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }

    public IndexResponse insertDocument(final String index, final String type, final String json) {
        return client.prepareIndex(index, type) //
                .setSource(json, XContentType.JSON) //
                .get();
    }
}
