package com.francetelecom.orangetv.junithistory.client.panel;

import com.francetelecom.orangetv.junithistory.client.util.CssConstants;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel pour l'authentification
 * 
 * @author ndmz2720
 * 
 */
public class PanelConnection extends Composite implements CssConstants {

	private LabelAndBoxWidget wLogin = new LabelAndBoxWidget("login", 80, 100);
	private LabelAndBoxWidget wPassword = new LabelAndBoxWidget("password", 80, 100, true);

	// constructor
	public PanelConnection() {
		this.initWidget(this.buildMainPanel());
	}

	// ---------------------------------------- public method
	public Credential getDataFromWidget() {

		String login = this.wLogin.getBoxUserInput();
		String pwd = this.wPassword.getBoxUserInput();

		return new Credential(login, pwd);
	}

	// --------------------------------------- private methods
	private Widget buildMainPanel() {
		VerticalPanel main = new VerticalPanel();
		main.setSpacing(PANEL_SPACING);

		main.add(this.wLogin);
		main.add(this.wPassword);

		return main;
	}

	// --------------------------------- Overriding Widget

	@Override
	protected void onLoad() {
		super.onLoad();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				wLogin.setFocus(true);

			}
		});
	}

	// ======================== INNER CLASS
	public static class Credential {
		private final String login;
		private final String pwd;

		public String getLogin() {
			return login;
		}

		public String getPwd() {
			return pwd;
		}

		private Credential(String login, String pwd) {
			this.login = login;
			this.pwd = pwd;
		}

	}
}
