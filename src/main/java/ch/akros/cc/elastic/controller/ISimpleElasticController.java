package ch.akros.cc.elastic.controller;

import javafx.event.ActionEvent;

/**
 * Created by Patrick on 09.05.2017.
 */
public interface ISimpleElasticController {

    void doConnect(final ActionEvent event);

    void doSearch(final ActionEvent event);

    void doInsert(final ActionEvent event);

    void close();
}
