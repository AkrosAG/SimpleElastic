package ch.akros.cc.elastic.connection;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * Created by Patrick on 09.05.2017.
 */
public class ClientConnectionFactory {

    private static ClientConnection clientConnection;

    private ClientConnectionFactory() {
    }

    public static void init(final String server) throws MalformedURLException, UnknownHostException {
        clientConnection = createClientConnection(server);
    }

    public static ClientConnection getClientConnection() {
        if (clientConnection == null) {
            throw new IllegalStateException("Factory must be initialized.");
        }
        return clientConnection;
    }

    private static ClientConnection createClientConnection(final String server) throws MalformedURLException, UnknownHostException {
        return new ClientConnection(server);
    }
}
