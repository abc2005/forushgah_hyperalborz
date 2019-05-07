package com.persiandesigners.hyperalborz;


import android.app.Application;

	public class MyApplication extends Application {
		@Override
        public void onCreate() {
            super.onCreate();

            // The following line triggers the initialization of ACRA
            //ACRA.init(this);
        }
	}
