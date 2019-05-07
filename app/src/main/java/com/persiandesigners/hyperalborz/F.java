package com.persiandesigners.hyperalborz;

import android.app.ProgressDialog;
import android.content.Context;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class F {
	static ProgressDialog pDialog;
	String fields,values,data;
	Context contexts;
	Boolean work;
	
	public static String farsi(Context context,int message) {
		return (context.getString(message));
	}



	public static String getCurrency(String number) {
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		final DecimalFormat decimalFormat = new DecimalFormat("###,###,###", symbols);
		return decimalFormat.format(Integer.parseInt(number));
	}
}
