package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.Map;

import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.google.gwt.user.client.ui.HasWidgets;

public interface IPresenter {

	public static final String PARAMS_GROUP_ID = "paramGroupId";
	public static final String PARAMS_SUITE_ID = "paramSuiteId";
	public static final String PARAMS_TEST_ID = "paramTestId";
	public static final String PARAMS_TCOMMENT_ID = "paramTCommentId";

	public static final String PARAMS_FORCE_REFRESH = "paramForceRefresh";

	public static final String PARAMS_ITEM_ID = "paramItemId";

	public void go(HasWidgets container);

	public void go(HasWidgets container, Map<String, Object> params);

	public IView getView();
}
