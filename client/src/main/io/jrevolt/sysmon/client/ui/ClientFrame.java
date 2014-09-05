package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.rest.RestService;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

import io.jrevolt.sysmon.model.AppCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class ClientFrame extends Base<BorderPane> {

	@Autowired
	StandardEnvironment env;

	@Autowired
	AppCfg app;

	@Autowired
	RestService rest;

	@FXML
	TreeView servers;

	@FXML
	TextArea details;

	@Override
	protected void initialize() {
		super.initialize();


	}

}
