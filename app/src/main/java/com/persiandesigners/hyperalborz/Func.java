package com.persiandesigners.hyperalborz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Func {
    Context ctx;
    ImageView img_sabad;
    TextView tvnumsabad;
    TextView countdown;
    Activity act;
    ImageView imgSearch, drawer, back;

    public Func(Context c) {
        ctx = c;
        act = (Activity) ctx;

        String languageToLoad  = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        act.getBaseContext().getResources().updateConfiguration(config,
                act.getBaseContext().getResources().getDisplayMetrics());
    }

    public void MakeActionBar(String title) {
        final Activity activity = (Activity) ctx;
        ImageView imgBack = (ImageView) activity.findViewById(R.id.back);
        if (imgBack != null) {
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }
        drawer = (ImageView) activity.findViewById(R.id.drawer);
        back = (ImageView) activity.findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();

                    View view = activity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });
        }
        imgSearch = (ImageView) activity.findViewById(R.id.imgsearch);
        if (imgSearch != null) {
            imgSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, Search.class));
                }
            });
        }

        Typeface typeface2 = Typeface.createFromAsset(activity.getAssets(), "IRAN Sans.ttf");
        TextView title_toolbar = (TextView) activity.findViewById(R.id.title_toolbar);
        if (title_toolbar != null) {
            title_toolbar.setTypeface(typeface2);
            title_toolbar.setText(title);
        }

        checkSabad(activity);

        img_sabad = (ImageView) activity.findViewById(R.id.img_sabad);
        if (img_sabad != null) {
            img_sabad.setOnClickListener(new View.OnClickListener() {
                // sabad kharid click  shod
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, SabadKharid_s2.class));
                    act.overridePendingTransition(R.anim.go_up,R.anim.go_down);
                }
            });
        }
        tvnumsabad = (TextView) activity.findViewById(R.id.text_numkharid);

        TextView search = (TextView) activity.findViewById(R.id.search_et);
        if (search != null) {
            search.setTypeface(typeface2);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, Search.class));
                }
            });
        }
    }


    public static List<FeedItem> parseResult(String result) {
        String TAG_CONTACTS = "contacts";
        return parsProducts(result, TAG_CONTACTS);
    }

    public static List<FeedItem> parseResultSimilar(String result) {
        String TAG_CONTACTS = "similar";
        return parsProducts(result, TAG_CONTACTS);
    }

    private static List<FeedItem> parsProducts(String result, String tag_contacts) {
        List<FeedItem> feedsList = null;
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray(tag_contacts);
            if (posts != null) {
                feedsList = new ArrayList<>();

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    FeedItem item = new FeedItem();

                    if (post.optString("name").length() < 2)
                        item.setname(post.optString("title"));
                    else
                        item.setname(post.optString("name"));

                    item.setid(post.optString("id"));

                    if (post.optString("price").length() < 2)
                        item.setPrice(post.optString("price1"));
                    else
                        item.setPrice(post.optString("price"));

                    if (post.optString("img").length() < 2)
                        item.setimg(post.optString("thumb"));
                    else
                        item.setimg(post.optString("img"));
                    item.setprice2(post.optString("price2"));
                    item.setnum(post.optString("num"));
                    item.setMajazi(post.optString("majazi"));
                    item.setTedad("1");
                    item.setCatId(post.optInt("cat"));
                    if (post.optString("vije").equals("1"))
                        item.setForushVije(true);
                    else
                        item.setForushVije(false);
                    item.setOmde_price(post.optInt("omde_price"));
                    item.setOmde_num(post.optInt("omde_num"));
                    feedsList.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
        return feedsList;
    }

    public static List<PishnahadVijeItems> parsePishnahadVije(String jadid) {
        List<PishnahadVijeItems> shopItems = null;
        try {
            JSONObject response = new JSONObject(jadid);
            JSONArray posts = response.optJSONArray("contacts");
            shopItems = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                PishnahadVijeItems item = new PishnahadVijeItems();
                item.setName(post.optString("onvan"));
                item.setIcon(post.optString("img"));
                item.setId(post.optString("id"));
                item.setHeight(post.optString("h"));
                item.setLink(post.optString("link"));
                item.setTimer(post.optInt("time"));
                shopItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
        return shopItems;
    }


    public static List<CatItems> parseResultCatsProd(String body) {
        String TAG_CONTACTS = "cats";
        List<CatItems> CatItems = null;
        try {
            JSONObject response = new JSONObject(body);
            JSONArray posts = response.optJSONArray(TAG_CONTACTS);
            CatItems = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                CatItems item = new CatItems();
                item.setName(post.optString("name"));
                item.setId(post.optString("id"));
                item.setThumb(post.optString("img"));
                item.settype(post.optString("type"));
                CatItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
        return CatItems;
    }

    public static List<CatItems> parseResultCats(String result, String cats) {
        String TAG_CONTACTS = "contacts";
        if (cats.length() > 0)
            TAG_CONTACTS = cats;
        List<CatItems> CatItems = null;
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray(TAG_CONTACTS);
            CatItems = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                CatItems item = new CatItems();
                if (cats.equals("cats")) {
                    if (post.optString("mainCat").equals("1")) {
                        item.setName(post.optString("name"));
                        item.setId(post.optString("id"));
                        item.setThumb(post.optString("img"));
                        item.settype(post.optString("type"));
                    }
                } else {
                    item.setName(post.optString("name"));
                    item.setId(post.optString("id"));
                    item.setThumb(post.optString("img"));
                    item.settype(post.optString("type"));
                }

                CatItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
        return CatItems;
    }

    public static void checkSabad(final Context ctx) {
        try {
            final Activity activity = (Activity) ctx;
            Typeface t = Typeface.createFromAsset(ctx.getAssets(), "IRAN Sans.ttf");
            final DatabaseHandler db2;
            db2 = new DatabaseHandler(ctx);
            if (!db2.isOpen())
                db2.open();


            final TextView tvnumsabad = (TextView) activity.findViewById(R.id.text_numkharid);
            tvnumsabad.setTypeface(t);

            final ImageView img_sabad = (ImageView) activity.findViewById(R.id.img_sabad);
            if (img_sabad.getVisibility() == View.GONE)
                tvnumsabad.setVisibility(View.GONE);
            img_sabad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, SabadKharid_s2.class));
                    activity.overridePendingTransition(R.anim.go_up,R.anim.go_down);
                }
            });

            int NumSabad = db2.getcountsabad();
            String sum=db2.getSabadMablaghKolWithTakhfif();
            SharedPreferences settings = activity.getSharedPreferences("settings", activity.MODE_PRIVATE);

            if (NumSabad > 0 && !settings.getString("sum","0").equals(sum)) {
                Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake);
                img_sabad.startAnimation(shake);

                Handler handler;
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        img_sabad.clearAnimation();
                    }
                }, 600);

                SharedPreferences.Editor pref= settings.edit();
                pref.putString("sum",sum);
                pref.commit();
            }

            tvnumsabad.setText(db2.getcountsabad() + "");
            TextView num_items_sabad = (TextView) activity.findViewById(R.id.num_items_sabad);
            TextView tvjamsabad = (TextView) activity.findViewById(R.id.tvjamsabad);

            if (tvjamsabad != null) {
                num_items_sabad.setTypeface(Func.getTypeface(activity));
                tvjamsabad.setTypeface(Func.getTypeface(activity));
                String price=db2.getSabadMablaghKolWithTakhfif() ;
                if(price.trim().equals("0")) {
                    price = "  0  ";
                    tvjamsabad.setText((price));
                }else
                    tvjamsabad.setText(Func.getCurrency(price));

                final CardView ln_sabad_bottom = (CardView) activity.findViewById(R.id.ln_sabad_bottom);
                Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake);

                if(db2.getNumSabad()>0) {
					if(activity.getResources().getBoolean(R.bool.display_num_on_bottom_sabad))
						num_items_sabad.setText(db2.getNumSabad()+"");
                    ln_sabad_bottom.startAnimation(shake);
                }
                ln_sabad_bottom.bringToFront();
                ln_sabad_bottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(activity,SabadKharid_s2.class));
                        activity.overridePendingTransition(R.anim.go_up,R.anim.go_down);
                    }
                });

                Handler handler;
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ln_sabad_bottom.clearAnimation();
                    }
                }, 600);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("this",e.getMessage());
        }

    }

    public void hideSabadKharidIcon() {
        img_sabad.setVisibility(View.GONE);
        tvnumsabad.setVisibility(View.GONE);
    }
    public void hideSearch() {
        if(imgSearch!=null){
            imgSearch.setVisibility(View.GONE);
        }
    }


    public void hideDrawer() {
        drawer.setVisibility(View.GONE);
    }

    public static String getCurrency(String number) {
        try {
            final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            final DecimalFormat decimalFormat = new DecimalFormat("###,###,###", symbols);
            return decimalFormat.format(Integer.parseInt(number));
        } catch (Exception e) {
            return number;
        }
    }

    public static int getScreenWidth(Activity act) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public static void checkSabadKharid(Context context, TextView tvnumsabad) {
        DatabaseHandler db2;
        db2 = new DatabaseHandler(context);
        if (!db2.isOpen())
            db2.open();

        tvnumsabad.setVisibility(View.GONE);
        if (db2.getcountsabad() > 0) {// سبد خرید مقدار دارد
            tvnumsabad.setVisibility(View.VISIBLE);
            tvnumsabad.setText(Integer.toString(db2.getcountsabad()));
        }

        try {
            db2.close();
        } catch (Exception e) {
        }
    }

    public static boolean isEmptySabadKharid(Context context) {
        DatabaseHandler db2;
        db2 = new DatabaseHandler(context);
        if (!db2.isOpen())
            db2.open();

        if (db2.getcountsabad() > 0) {// سبد خرید مقدار دارد
            return false;
        } else
            return true;

    }

    public static boolean isInternet(Context applicationContext) {
        if (NetworkAvailable.isNetworkAvailable(applicationContext))
            return true;
        else
            return false;
    }

    public static boolean hasOffline(Context applicationContext, String part, int partId) {
        DatabaseHandler dbOffline = new DatabaseHandler(applicationContext);
        dbOffline.open();
        Boolean offline = dbOffline.HasOffline(part, partId);
        dbOffline.close();
        return offline;
    }

    public static String getOffline(Context applicationContext, String part, Integer partId) {
        DatabaseHandler dbOffline = new DatabaseHandler(applicationContext);
        dbOffline.open();
        String offline = dbOffline.getOffline(part, partId);
        dbOffline.close();
        Log.v("this", "offline " + offline);
        return offline;
    }

    public static void insertOffline(Context applicationContext,
                                     String offlineValue, String part, Integer partId) {
        DatabaseHandler dbOffline = new DatabaseHandler(applicationContext);
        dbOffline.open();
        dbOffline.insertOffline(offlineValue.replace(applicationContext.getString(R.string.xml), ""), part, partId);
        dbOffline.close();
    }

    public static String getUid(Activity act) {
        SharedPreferences settings = act.getSharedPreferences("settings", act.MODE_PRIVATE);
        return settings.getString("uid", "0");
    }

    public static String getIsNemayeshgahi(Context act) {
        SharedPreferences settings = act.getSharedPreferences("settings", act.MODE_PRIVATE);
        return settings.getString("nemayeshgahi", "0");
    }

    public static List<Blog_items> ParseBlog(String services) {
        List<Blog_items> shopItems = null;
        try {
            JSONObject response = new JSONObject(services);
            JSONArray posts = response.optJSONArray("contacts");
            shopItems = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                Blog_items item = new Blog_items();
                item.setOnvan(post.optString("onvan"));
                item.setImg(post.optString("img"));
                item.setId(post.optString("id"));
                shopItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
        return shopItems;
    }

    public void MakeCountDown() {
        countdown = (TextView) act.findViewById(R.id.countdown);
        countdown.setTypeface(Typeface.createFromAsset(act.getAssets(), "IRAN Sans Bold.ttf"));

        CountDown timer = new CountDown(240000, 1000);
        timer.start();
    }

    public void HideSearch() {
        if (imgSearch != null)
            imgSearch.setVisibility(View.INVISIBLE);
    }

    public void showBack() {
        if (back != null)
            back.setVisibility(View.VISIBLE);
    }

    public static void NoNet(Activity act) {
        Snackbar snack = Snackbar.make(act.findViewById(R.id.ln),act.getResources().getString(R.string.nointernet), Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Func.getTypeface(act));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,act.getResources().getDimension(R.dimen.toast));
        snack.show();
    }

    public static boolean isDizbon(Activity act) {
        if(act.getString(R.string.url).contains("dizbon"))
            return true;
        else
            return false;
    }


    public class CountDown extends CountDownTimer {

        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long ms = millisUntilFinished;
            String text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                    TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
            countdown.setText(text);
        }

        @Override
        public void onFinish() {
            countdown.setText("0");
        }
    }


    public static Typeface getTypeface(Activity act) {
        return Typeface.createFromAsset(act.getAssets(), "IRAN Sans Bold.ttf");
    }

    public static List<CatsItems> ParseCats(String services) {
        List<CatsItems> shopItems = null;
        try {
            JSONObject response = new JSONObject(services);
            JSONArray posts = response.optJSONArray("contacts");
            shopItems = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                CatsItems item = new CatsItems();
                item.setName(post.optString("name"));
                item.setIcon(post.optString("img"));
                item.setId(post.optString("id"));
                item.setParrent(post.optString("parrent_id"));
                shopItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
        return shopItems;
    }

}
