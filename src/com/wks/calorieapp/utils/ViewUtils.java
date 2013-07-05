package com.wks.calorieapp.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class ViewUtils
{
	public static void hideKeyboard(TextView edit)
	{
		Context context = edit.getContext ();
		InputMethodManager inputManager = (InputMethodManager) context.getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}
}
