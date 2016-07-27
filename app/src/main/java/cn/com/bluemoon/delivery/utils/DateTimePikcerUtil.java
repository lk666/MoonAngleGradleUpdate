package cn.com.bluemoon.delivery.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ResourceAsColor")
public class DateTimePikcerUtil {

	public static void resizePikcer(FrameLayout tp) {
		List<NumberPicker> npList = findNumberPicker(tp);
		for (NumberPicker np : npList) {
			resizeNumberPicker(np);
		}
	}

	private static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				} else if (child instanceof TextView) {
					TextView textview = (TextView) child;
					textview.setTextColor(Color.WHITE);
					textview.setTextSize(20);
				}
			}
		}
		return npList;
	}


	private static void resizeNumberPicker(NumberPicker np) {
		LayoutParams params = new LayoutParams(120,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 10, 0);
		np.setLayoutParams(params);
		EditText edTxt = findEditText(np);
		edTxt.setFocusable(false);
	  //edTxt.setTextColor(Color.RED);
		
	}
	
	
	private static EditText findEditText(final NumberPicker np) {
		
		if (np != null) {
			for (int i = 0; i < np.getChildCount(); i++) {
				View child = np.getChildAt(i);

				if (child instanceof EditText) {
					return (EditText)child;
				}
			}

		}
		return null;
	}

}
