package com.mukuru.currencyexchange.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelperMethods {

    //MOST OF THESE ARE NOT USED FOR THIS PROJECT, THESE ARE JUST SOME HELPFUL METHODS I HAVE USED IN
    //THE PAST AND KEEP IN ONE CLASS FOR EASY ACCESS

    /**
     * Hides the software keyboard if it is open
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        if(activity != null){
            if(activity.getCurrentFocus() != null){
                if(activity.getCurrentFocus().getWindowToken() != null){
                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                }
            }
        }
    }

	public static boolean hasActiveInternetConnection(Context context) {

		if (haveWifiConnection(context)) {
			try {
				HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.co.za").openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500);
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				Log.e("Wifi", "Error checking internet connection", e);
			}
		} else if (haveMobileConnection(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.co.za").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("Mobile", "Error checking internet connection", e);
            }
        } else {
            Log.d("WiFi", "No network available!");
        }
		return false;
	}

    /**
     * Checks if there is presently wifi connection
     * @param context
     * @return Boolean of if there is connection or not
     */
    public static boolean haveWifiConnection(Context context) {
        boolean haveConnectedWifi = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
        }
        return haveConnectedWifi;
    }

	/**
	 * Checks if there is presently mobile connection
	 * @param context
	 * @return Boolean of if there is connection or not
	 */
	public static boolean haveMobileConnection(Context context) {
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedMobile;
	}

    //Gets the file extension eg .pdf from a string
    public static String getFileExtension(String title) {
        String fileExtension = ".";

        String tempExtension = "";
        for (int i = title.length() - 1; i > 0; i--) {
            if (title.charAt(i) != '.') {
                tempExtension = tempExtension + title.charAt(i);
            } else {
                break;
            }
        }

        if (tempExtension.length() > 0) {
            for (int i = tempExtension.length() - 1; i >= 0; i--) {
                fileExtension = fileExtension + tempExtension.charAt(i);
            }
        }

        if(fileExtension.equals("." + title)){
            return "";
        }
        if(fileExtension.length() == title.length()){
            return "";
        }

        return fileExtension;
    }

    //Gets the name from a string that includes an extension
    public static String getFileName(String title) {
        String fileName = "";

        String tempName = "";

        for (int i = 0; i < title.lastIndexOf('.'); i++) {
            tempName = tempName + title.charAt(i);
        }

        String pattern = "(\\W)(\\s )";
        tempName = tempName.replaceAll(pattern, "-");

        fileName = tempName;

        if (fileName.equals("")){
            fileName = title;
        }

        return fileName;
    }

    /**
     * Gets text out of html
     * @param html html to retrieve text from
     * @return String of text from the html
     */
	public static String getTextFromHtml(String html) {
        String text = html.replace("â", "");

        String displayText = Html.fromHtml(text).toString();
        displayText = displayText.replace("€™", "\'");
        displayText = displayText.replace("€“", "-");
        displayText = displayText.replace("€�", "\"");
        displayText = displayText.replace("€œ", "\"");

        return displayText;
    }
}
