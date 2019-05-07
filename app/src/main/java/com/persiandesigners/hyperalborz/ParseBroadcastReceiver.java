package com.persiandesigners.hyperalborz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseBroadcastReceiver extends BroadcastReceiver {
	JSONArray contacts = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extra = intent.getExtras();
		if(extra!=null){
	    String json = extra.getString("com.parse.Data");
	    JSONObject jObject;

        try {
			jObject = new JSONObject(json);
			String data=jObject.getString("alert");
			JSONObject jObj = new JSONObject(data);
			JSONObject subObj = jObj.getJSONObject("data");
			
			//getting datas 
			String tel = subObj.getString("tel");
			Log.v("this","tel" + tel);

        } catch (JSONException e) {

		}
		}
	}
}
