package ch.akros.cc.elastic.connection;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

public interface IClientConnection {

	void disconnect();
	
	IndexResponse insertDocument(final String index, final String type, final String json);
	
	SearchResponse search(final String[] indeces, final String[] types, final String query);
}
