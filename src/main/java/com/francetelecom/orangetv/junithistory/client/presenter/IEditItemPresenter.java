package com.francetelecom.orangetv.junithistory.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;

public interface IEditItemPresenter extends IPresenter {

	public enum ViewActionEnum {
		update, cancel
	}

	public static final class UpdateClickEvent extends ClickEvent {

		private final boolean updateDone;

		// ------------------------- constructor
		public UpdateClickEvent() {
			this(false);
		}

		public UpdateClickEvent(boolean updateDone) {
			this.updateDone = updateDone;
		}

		// -------------------- accessor
		public boolean isUpdateDone() {
			return this.updateDone;
		}
	}

}
