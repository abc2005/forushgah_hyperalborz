package com.persiandesigners.hyperalborz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import ir.viewpagerindicator.CirclePageIndicator;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class FistActiivty extends AppCompatActivity implements View.OnClickListener, MyAdapterCallBack {
    private Toolbar toolbar;

    CustomProgressDialog mCustomProgressDialog;
    ListAdapterCat ladap_cat;
    Typeface IranSans;
    RecyclerView pishnahadrecycle, topsellrecycle, jadidtarinrecycle, rc_cats, pishanadVIjeRc;
    String urls;
    ExpandableHeightGridView grid_cat;
    SharedPreferences settings;
    Boolean exit = false, activeKifPul = false, inCat = false;
    LinearLayout ln_pishnahad, ln_topsold, ln_jadidtarin, lnchat, banners;
    LinearLayout under_cat, under_special, under_porforush, under_jadidtarin;
    Button tv1;
    LinearLayout product_reycles;
    Drawer drawer;
    String catId = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstactivity);

        try {
            declare();
            Recycles();

            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;

            urls = "http://hyperalborz.ir//getHomePage2.php?n=" + number + "&uid="
                    + Func.getUid(this) + "&w=" + Func.getScreenWidth(this) + "&catId=" + catId;
            if (Func.isInternet(getApplicationContext()) ||
                    Func.hasOffline(getApplicationContext(), "FistActivity", 0)) {
                new getXml().execute();
                if (Func.isInternet(this) == false)
                    Func.NoNet(this);
            } else {
                Func.NoNet(this);
            }

            actionbar();
            pushNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void MakeProdViews(String body) {
        try {
            JSONObject response = new JSONObject(body);
            JSONArray posts = response.optJSONArray("contacts");
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                JSONObject responseP = new JSONObject(body);
                JSONArray postsP = responseP.optJSONArray("c" + post.optString("id"));
                List<FeedItem> feedsList = new ArrayList<>();
                for (int p = 0; p < postsP.length(); p++) {
                    JSONObject postP = postsP.optJSONObject(p);
                    FeedItem item = new FeedItem();
                    item.setname(postP.optString("name"));
                    item.setid(postP.optString("id"));
                    item.setPrice(postP.optString("price"));
                    item.setimg(postP.optString("img"));
                    item.setprice2(postP.optString("price2"));
                    item.setnum(postP.optString("num"));
                    item.setMajazi(postP.optString("majazi"));
                    item.setTedad("1");
                    if (postP.optString("vije").equals("1"))
                        item.setForushVije(true);
                    else
                        item.setForushVije(false);
                    feedsList.add(item);
                }

                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.recycle_product_row, null);

                TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
                tv_title.setTypeface(IranSans);
                tv_title.setText(post.optString("name"));

                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                layoutManager.setReverseLayout(true);
                RecyclerView product_recycle = (RecyclerView) v.findViewById(R.id.product_recycle);
                product_recycle.setLayoutManager(layoutManager);

                MyAdapter adapterRec = new MyAdapter(FistActiivty.this, feedsList);
                ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
                scaldeAdapter.setDuration(300);
                product_recycle.setAdapter(scaldeAdapter);

                product_reycles.addView(v);
                product_reycles.setVisibility(View.VISIBLE);
            }
            mCustomProgressDialog.dismiss("");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
    }

    public class getXml extends AsyncTask<Void, String, String> {
        Boolean goterror = false;
        String pishnahad_vije, nemayeshgahi, name, msg, pishnahad, porforush, jadid, gallery, mainCats, version, kif, banner, version_link;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (Func.isInternet(getApplicationContext())) {
                mCustomProgressDialog = new CustomProgressDialog(FistActiivty.this);
                mCustomProgressDialog.show("");
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc = null;
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                if (Func.isInternet(getApplicationContext())) {
                    URL url = new URL(urls);
                    Log.v("this", urls);
                    URLConnection conn = url.openConnection();
                    doc = builder.parse(conn.getInputStream());

                    StringWriter sw = new StringWriter();
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                    transformer.transform(new DOMSource(doc), new StreamResult(sw));
                    Func.insertOffline(getApplicationContext(), sw.toString(), "FistActivity", 0);
                    Log.v("this", "va" + sw.toString());
                } else {
                    InputSource is = new InputSource(new StringReader(Func.getOffline(getApplicationContext(), "FistActivity", 0)));
                    doc = builder.parse((is));
                }

                NodeList nodes = doc.getElementsByTagName("data");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);
                    NodeList title = element.getElementsByTagName("content");
                    Element line = (Element) title.item(0);
                    if (i == 0)
                        pishnahad = line.getTextContent();
                    else if (i == 1)
                        porforush = line.getTextContent();
                    else if (i == 2)
                        jadid = line.getTextContent();
                    else if (i == 3)
                        gallery = line.getTextContent();
                    else if (i == 4)
                        mainCats = line.getTextContent();
                    else if ((i == 5))
                        version = line.getTextContent();
                    else if ((i == 7))
                        banner = line.getTextContent();
                    else if ((i == 6))
                        kif = line.getTextContent();
                    else if ((i == 8))
                        msg = line.getTextContent();
                    else if ((i == 9))
                        name = line.getTextContent();
                    else if ((i == 10))
                        nemayeshgahi = line.getTextContent();
                    else if (i == 11) {
                        pishnahad_vije = line.getTextContent();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                goterror = true;
                Log.v("this", e.getMessage() + "eror");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mCustomProgressDialog != null)
                mCustomProgressDialog.dismiss("");

            LinearLayout lnsabadbottom = (LinearLayout) findViewById(R.id.lnsabadbottom);
            lnsabadbottom.setVisibility(View.VISIBLE);

            JSONArray contacts = null;
            AutoScrollViewPager viewPager;// gallery
            PagerAdapter adapter;
            CirclePageIndicator mIndicator;

            makeNavar(msg);

            if (goterror == false && !isCancelled()) {
                makeBanners(banner);

                if (kif.equals("removed")) {
                    SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
                    settings.edit().clear().commit();
                    startActivity(new Intent(FistActiivty.this, FistActiivty.class));
                }

                if (!Func.getUid(FistActiivty.this).equals("0") && activeKifPul) {
                    SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                    SharedPreferences.Editor pref = settings.edit();
                    pref.putString("kif", kif);
                    pref.commit();
                    drawer.mAdapter.notifyDataSetChanged();
                }

                if (!Func.getUid(FistActiivty.this).equals("0") && Func.isDizbon(FistActiivty.this)) {
                    SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                    SharedPreferences.Editor pref = settings.edit();
                    pref.putString("name", name);
                    pref.commit();
                }

                MyAdapter adapterRec;

                PackageInfo pInfo;
                Integer versionC = 0;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    versionC = pInfo.versionCode;
                    Log.v("this", "version " + version + " " + versionC);
                    if (Integer.parseInt(version) > versionC) {
                        AlertDialog.Builder a = new AlertDialog.Builder(FistActiivty.this);
                        a.setMessage(getString(R.string.newVersion));
                        a.setPositiveButton(("باشه"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = a.show();
                        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                        messageText.setGravity(Gravity.RIGHT);
                    }
                } catch (Exception e) {
                }

//                DefaultItemAnimator animator = new DefaultItemAnimator() ;
//                animator.setAddDuration(1000);
//                animator.setRemoveDuration(1000);

                if (pishnahad != null && pishnahad.length() > 0 && !pishnahad.contains("not##")) {
                    adapterRec = new MyAdapter(FistActiivty.this, Func.parseResult(pishnahad));
//                    pishnahadrecycle.setItemAnimator(animator);
                    ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
                    scaldeAdapter.setDuration(300);
                    pishnahadrecycle.setAdapter(scaldeAdapter);
                } else
                    ln_pishnahad.setVisibility(View.GONE);


                if (porforush != null && porforush.length() > 0 && !porforush.contains("not##")) {
                    adapterRec = new MyAdapter(FistActiivty.this, Func.parseResult(porforush));
//                    topsellrecycle.setItemAnimator(animator);
                    ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
                    scaldeAdapter.setDuration(300);
                    topsellrecycle.setAdapter(scaldeAdapter);
                } else
                    ln_topsold.setVisibility(View.GONE);

                if (jadid != null && jadid.length() > 0 && !jadid.contains("not##")) {
                    adapterRec = new MyAdapter(FistActiivty.this, Func.parseResult(jadid));
//                    jadidtarinrecycle.setItemAnimator(animator);
                    ScaleInAnimationAdapter scaldeAdapter = new ScaleInAnimationAdapter(adapterRec);
                    scaldeAdapter.setDuration(300);
                    jadidtarinrecycle.setAdapter(scaldeAdapter);
                } else
                    ln_jadidtarin.setVisibility(View.GONE);

                if (pishnahad_vije != null && pishnahad_vije.length() > 0 && !pishnahad_vije.contains("not##")) {
                    FrameLayout fl_pishnahad_vije = (FrameLayout) findViewById(R.id.fl_pishnahad_vije);
                    if (getResources().getBoolean(R.bool.show_pishnahadvije)) {
                        TextView tvpishnahadvije = (TextView) findViewById(R.id.tvpishnahadvije);
                        tvpishnahadvije.setTypeface(IranSans);

                        List<PishnahadVijeItems> items = Func.parsePishnahadVije(pishnahad_vije);
                        if (items != null && items.size() > 0) {
							fl_pishnahad_vije.setVisibility(View.VISIBLE);
							
                            PishnahadVijeAdp pishnahadVijeAdp = new PishnahadVijeAdp(FistActiivty.this, items);
                            pishanadVIjeRc.setAdapter(pishnahadVijeAdp);

                            pishanadVIjeRc.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    Integer.parseInt(items.get(0).getHeight())));
                        }
                    }
                }


                if (getResources().getBoolean(R.bool.homepage_cats_like_digikala)) {
                    rc_cats.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.GONE);

                    List<CatItems> shopItems = null;
                    try {
                        JSONObject response = new JSONObject(mainCats);
                        JSONArray posts = response.optJSONArray("contacts");
                        shopItems = new ArrayList<>();

                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.optJSONObject(i);
                            CatItems item = new CatItems();
                            item.setName(post.optString("name"));
                            item.setId(post.optString("id"));
                            shopItems.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("this", e.getMessage());
                    }
                    rc_cats.setAdapter(new CatsHomeAdapter(FistActiivty.this, shopItems));
                }

                if (mainCats != null && mainCats.length() > 0) {
                    if (getResources().getBoolean(R.bool.show_cats_on_homepage) == false) {
                        grid_cat.setVisibility(View.GONE);
                    } else {
                        final ArrayList<HashMap<String, String>> dataC = new ArrayList<HashMap<String, String>>();
                        try {
                            JSONObject jsonObj = new JSONObject(mainCats);
                            contacts = jsonObj.getJSONArray("contacts");
                            for (int i = 0; i < contacts.length(); i++) {
                                JSONObject c = contacts.getJSONObject(i);
                                HashMap<String, String> contact = new HashMap<String, String>();
                                contact.put("id", (c.getString("id")));
                                contact.put("img", (c.getString("img")));
                                contact.put("name", (c.getString("name")));
                                contact.put("hasSubCat", c.getString("hsc"));
                                dataC.add(contact);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ladap_cat == null) {
                            ladap_cat = new ListAdapterCat(FistActiivty.this, dataC, IranSans);
                            grid_cat.setAdapter(ladap_cat);

                            grid_cat.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent in = null;
                                    if (inCat || dataC.get(position).get("hasSubCat").equals("0"))
                                        in = new Intent(FistActiivty.this, Productha.class);
                                    else
                                        in = new Intent(FistActiivty.this, Subcats.class);
                                    in.putExtra("catId", dataC.get(position).get("id"));
                                    in.putExtra("onvan", dataC.get(position).get("name"));
                                    startActivity(in);
                                }
                            });
                        }
                    }
                }

                if (gallery != null && gallery.length() > 0) {
                    int count = 0;
                    String img[], link[] = null;
                    ArrayList<HashMap<String, String>> dataC = new ArrayList<HashMap<String, String>>();
                    try {
                        JSONObject jsonObj = new JSONObject(gallery);
                        contacts = jsonObj.getJSONArray("contacts");

                        if (contacts.length() > 0) {
                            RelativeLayout tv1 = (RelativeLayout) findViewById(R.id.slider_ln);
                            tv1.setVisibility(View.VISIBLE);

                            if (Func.isInternet(FistActiivty.this) == false)
                                tv1.setVisibility(View.GONE);
                        }


                        link = new String[contacts.length()];
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            HashMap<String, String> contact = new HashMap<String, String>();
                            contact.put("img", new String(c.getString("img").getBytes("ISO-8859-1"), "UTF-8"));
                            link[i] = c.getString("link");
                            contact.put("h", c.getString("h"));
                            contact.put("id", c.getString("id"));
                            contact.put("link_type", c.getString("link_type"));
                            contact.put("link", c.getString("link"));
                            dataC.add(contact);
                        }
                    } catch (JSONException e) {
                        Log.v("this", e.getMessage());
                    } catch (UnsupportedEncodingException e) {
                        Log.v("this", e.getMessage());
                    }
                    if(dataC==null)
                        count=0;
                    else
                        count=dataC.size();
                    img = new String[count];
                    count--;
                    while (count >= 0) {
                        img[count] = dataC.get(count).get("img");
                        count--;
                    }
                    viewPager = (AutoScrollViewPager) findViewById(R.id.pager);
                    adapter = new ViewPagerAdaptergallery(FistActiivty.this, img,
                            "pictures", link, dataC);
                    try {
                        int height = Integer.parseInt(dataC.get(0).get("h"));
                        if (height > 0)
                            viewPager.getLayoutParams().height = height;
                    } catch (Exception e) {
                        viewPager.getLayoutParams().height = 300;
                    }
                    viewPager.setAdapter(adapter);
                    viewPager.startAutoScroll(5000);
                    viewPager.setInterval(5000);

                    // ViewPager Indicator
                    mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
                    mIndicator.setViewPager(viewPager);
                    mIndicator.setCurrentItem(adapter.getCount() - 1);
                }

                SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor pref = settings.edit();
                pref.putString("nemayeshgahi", nemayeshgahi);
                pref.commit();
            } else {
            }
        }
    }

    private void makeBanners(String banner) {
        if (banner != null) {
            int width = getWindowManager().getDefaultDisplay().getWidth() / 2;

            try {
                JSONObject response = new JSONObject(banner);
                JSONArray posts = response.optJSONArray("contacts");
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);

                    String type = post.optString("type");
                    String img = post.optString("img");
                    String link_type = post.optString("link_type");
                    String link = post.optString("link");

                    if (type.equals("ver")) {
                        banners.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(this).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        banners.addView(imgNew);
                    } else if (type.equals("under_cat")) {
                        under_cat.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(this).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        under_cat.addView(imgNew);
                    } else if (type.equals("under_special")) {
                        under_special.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(this).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        under_special.addView(imgNew);
                    } else if (type.equals("under_porforush")) {
                        under_porforush.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(this).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        under_porforush.addView(imgNew);
                    } else if (type.equals("under_jadidtarin")) {
                        under_jadidtarin.setVisibility(View.VISIBLE);
                        ImageView imgNew = makeTheBanner(img, link_type, link);
                        Glide.with(this).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);
                        under_jadidtarin.addView(imgNew);
                    } else if (type.equals("hor")) {//horzental
                        banners.setVisibility(View.VISIBLE);
                        //LinearLayout lnNew=new LinearLayout(new ContextThemeWrapper(this, R.style.horzentalLayout), null, 0);
                        LinearLayout lnNew = new LinearLayout(this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 5, 10, 5);
                        lnNew.setLayoutParams(params);

                        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = vi.inflate(R.layout.hor_layout, null);

                        ImageView imgNew = (ImageView) v.findViewById(R.id.imgnew1);
                        CardView.LayoutParams params2 = new CardView.LayoutParams(width - 10, ViewGroup.LayoutParams.WRAP_CONTENT);
                        imgNew.setLayoutParams(params2);
                        final String finalLink1 = link;
                        final String finalLink_type1 = link_type;
                        imgNew.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doCLickListener(v, finalLink_type1, finalLink1);
                            }
                        });
                        Glide.with(this).load(getString(R.string.url) + "Opitures/" + img).into(imgNew);

                        i++;
                        if (posts.optJSONObject(i) != null && post.optString("type").equals("hor")) {
                            post = posts.optJSONObject(i);
                            type = post.optString("type");
                            img = post.optString("img");
                            link_type = post.optString("link_type");
                            link = post.optString("link");

                            ImageView imgNew2 = (ImageView) v.findViewById(R.id.imgnew2);
                            imgNew2.setLayoutParams(params2);
                            final String finalLink_type = link_type;
                            final String finalLink = link;
                            imgNew2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doCLickListener(v, finalLink_type, finalLink);
                                }
                            });
                            Glide.with(this).load(getString(R.string.url) + "Opitures/" + img).into(imgNew2);
                        }
                        lnNew.addView(v);
                        banners.addView(lnNew);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ImageView makeTheBanner(String img, String link_type, String link) {
        ImageView imgNew = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 5, 10, 5);
        imgNew.setLayoutParams(params);
        final String finalLink2 = link;
        final String finalLink_type2 = link_type;
        imgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCLickListener(v, finalLink_type2, finalLink2);
            }
        });
        return imgNew;
    }

    private void doCLickListener(View v, String link_type, String link) {
        if (link_type.equals("web")) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (link_type.equals("cat")) {
            Intent in = new Intent(FistActiivty.this, Products.class);
            in.putExtra("catId", link);
            in.putExtra("onvan", "");
            startActivity(in);
        } else if (link_type.equals("prod") || link_type.equals("pro")) {
            Intent in = new Intent(FistActiivty.this, Detailss.class);
            in.putExtra("productid", link);
            in.putExtra("name", "");
            startActivity(in);
        }
    }


    private void makeNavar(String msg) {
        Log.v("this", "msg: " + msg);
        try {
            JSONObject j = new JSONObject(msg);
            if (j.getInt("msg_active") == 1) {
                final FrameLayout pad = (FrameLayout) findViewById(R.id.pad);

                final CardView marque = (CardView) findViewById(R.id.marque);
                marque.setVisibility(View.VISIBLE);
                marque.setCardBackgroundColor(Color.parseColor(j.getString("msg_color")));

                TextView tv = null;
                if (j.getInt("msg_marque") == 1) {
                    FrameLayout lnmarq = (FrameLayout) findViewById(R.id.lnmarq);
                    lnmarq.setVisibility(View.VISIBLE);

                    tv = (TextView) findViewById(R.id.tv_msg_mrq);
                    tv.setMarqueeRepeatLimit(-1);
                } else {
                    tv = (TextView) findViewById(R.id.tv_msg);
                }
                tv.setText(j.getString("msg"));
                tv.setTypeface(IranSans);
                tv.setVisibility(View.VISIBLE);
                tv.setSelected(true);

                ImageView closeit = (ImageView) findViewById(R.id.closeit);
                if (j.getInt("msg_allow_close") == 0) {
                    closeit.setVisibility(View.GONE);
                } else {
                    closeit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            marque.setVisibility(View.GONE);
                            pad.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Recycles() {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        jadidtarinrecycle = (RecyclerView) findViewById(R.id.jadidtarinrecycle);
        layoutManager.setReverseLayout(true);
        jadidtarinrecycle.setLayoutManager(layoutManager);

        rc_cats = (RecyclerView) findViewById(R.id.rc_cats);
        LinearLayoutManager lnrtl = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lnrtl.setReverseLayout(true);
        rc_cats.setLayoutManager(lnrtl);
		
		 pishanadVIjeRc = (RecyclerView) findViewById(R.id.pishnahad_vije_recycle);
        LinearLayoutManager lnpishanadVIjeRc = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lnpishanadVIjeRc.setReverseLayout(true);
        pishanadVIjeRc.setLayoutManager(lnpishanadVIjeRc);

        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2.setReverseLayout(true);
        topsellrecycle = (RecyclerView) findViewById(R.id.topsellrecycle);
        topsellrecycle.setLayoutManager(layoutManager2);

        LinearLayoutManager layoutManager3
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager3.setReverseLayout(true);
        pishnahadrecycle = (RecyclerView) findViewById(R.id.pishnahadrecycle);
        pishnahadrecycle.setLayoutManager(layoutManager3);
    }

    private void declare() {
        if (getResources().getBoolean(R.bool.isActiveKif)) {
            activeKifPul = true;
        }
        if (getResources().getBoolean(R.bool.show_category_digibar) == false) {
            FrameLayout fm = (FrameLayout) findViewById(R.id.category_digibar);
            fm.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_forush_vije) == false) {
            FrameLayout fm = (FrameLayout) findViewById(R.id.forush_vije);
            fm.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_porforush) == false) {
            FrameLayout fm = (FrameLayout) findViewById(R.id.porforush);
            fm.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_jadidtarin) == false) {
            FrameLayout fm = (FrameLayout) findViewById(R.id.jadidtarin);
            fm.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_cat_button) == false) {
            Button tv1 = (Button) findViewById(R.id.tv1);
            tv1.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.show_slideshow) == false) {
            RelativeLayout tv1 = (RelativeLayout) findViewById(R.id.slider_ln);
            tv1.setVisibility(View.GONE);
        }
        drawer = new Drawer(this);
        product_reycles = (LinearLayout) findViewById(R.id.product_reycles);

        settings = getSharedPreferences("settings", MODE_PRIVATE);
        IranSans = Typeface.createFromAsset(this.getAssets(), "IRAN Sans Bold.ttf");
        grid_cat = (ExpandableHeightGridView) findViewById(R.id.grid_cat);
        grid_cat.setExpanded(true);

        ln_pishnahad = (LinearLayout) findViewById(R.id.ln_pishnahad);
        ln_topsold = (LinearLayout) findViewById(R.id.ln_topsold);
        ln_jadidtarin = (LinearLayout) findViewById(R.id.ln_jadidtain);
        banners = (LinearLayout) findViewById(R.id.banners);

        under_cat = (LinearLayout) findViewById(R.id.under_cat);
        under_special = (LinearLayout) findViewById(R.id.under_special);
        under_porforush = (LinearLayout) findViewById(R.id.under_porforush);
        under_jadidtarin = (LinearLayout) findViewById(R.id.under_jadidtarin);

        tv1 = (Button) findViewById(R.id.tv1);
        tv1.setTypeface(IranSans);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getBoolean(R.bool.category_like_digikala))
                    startActivity(new Intent(FistActiivty.this, Cats_digi.class));
                else
                    startActivity(new Intent(FistActiivty.this, Cats.class));
            }
        });

        TextView tvpishnahad = (TextView) findViewById(R.id.tvpishnahad);
        tvpishnahad.setTypeface(IranSans);
        tvpishnahad.setText(android.text.Html.fromHtml("<font color=red>فروش ویژه</font> " + getString(R.string.app_name)));

        TextView tvtopsell = (TextView) findViewById(R.id.tvtopsell);
        tvtopsell.setTypeface(IranSans);
        tvtopsell.setText("پرفروش ترین ها");

        TextView tvharaj = (TextView) findViewById(R.id.tvjadidtarin);
        tvharaj.setTypeface(IranSans);
        tvharaj.setText("جدیدترین ها");

        TextView tvalltvtopsell = (TextView) findViewById(R.id.tvalltvtopsell);
        tvalltvtopsell.setTypeface(IranSans);
        tvalltvtopsell.setOnClickListener(this);

        TextView tvalljadidtarin = (TextView) findViewById(R.id.tvalljadidtarin);
        tvalljadidtarin.setTypeface(IranSans);
        tvalljadidtarin.setOnClickListener(this);


        TextView tvalltvjive = (TextView) findViewById(R.id.tvalltvjive);
        tvalltvjive.setTypeface(IranSans);
        tvalltvjive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent in = null;
        switch (v.getId()) {
            case R.id.tvalltvtopsell:
                in = new Intent(FistActiivty.this, Products.class);
                in.putExtra("for", "porForush");
                in.putExtra("title", "پرفروش ترین ها");
                startActivity(in);
                break;
            case R.id.tvalljadidtarin:
                in = new Intent(FistActiivty.this, Products.class);
                in.putExtra("for", "jadidtarinha");
                in.putExtra("title", "جدیدترین ها");
                startActivity(in);
                break;
            case R.id.tvalltvjive:
                in = new Intent(FistActiivty.this, Products.class);
                in.putExtra("for", "haraj");
                in.putExtra("title", "فروش ویژه");
                startActivity(in);
                break;
        }
    }


    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar("");
        Func.checkSabad(this);
        action.HideSearch();

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen())
            drawer.closeDrawers();
        else {
            if (exit) {

                ActivityCompat.finishAffinity(this);
                super.onBackPressed();
            } else {
                Toast.makeText(FistActiivty.this, (getString(R.string.exit)), Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }

    }

    @Override
    public void checkSabad() {
        actionbar();
    }


    private void pushNotification() {
        final Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null && intent.getExtras().getString("message") != null
                    ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(intent.getExtras().getString("message"))
                        .setCancelable(false)
                        .setPositiveButton(("بستن"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                TextView messageView = (TextView) alert.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.RIGHT);
                messageView.setTypeface(IranSans);

            }
        }

        String scheme = intent.getDataString();
        if (scheme != null) {
            try {
                scheme = scheme.substring(scheme.lastIndexOf("=") + 1);
                if (scheme.equals("false")) {
                    MyToast.makeText(FistActiivty.this, "پرداخت ناموفق");
                } else if (scheme.length() > 0) {
                    DatabaseHandler db = new DatabaseHandler(this);
                    db.open();
                    db.clearSabadKharid();

                    final Alert mAlert = new Alert(FistActiivty.this, R.style.mydialog);
                    mAlert.setIcon(android.R.drawable.ic_dialog_alert);
                    mAlert.setMessage((" خرید شما با موفقیت انجام شد . کد رهگیری سفارش شما "
                            + scheme + " میباشد " +
                            "\n" +
                            "با تشکر از خرید شما "));
                    mAlert.setPositveButton("بستن", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlert.dismiss();
                        }
                    });

                    final String finalScheme = scheme;
                    mAlert.setNegativeButton("نمایش فاکتور", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlert.dismiss();
                            final Dialog dialog_fac = new Dialog(FistActiivty.this, R.style.DialogStyler);
                            dialog_fac.setContentView(R.layout.page2);
                            dialog_fac.setTitle("وضعیت سفارش شما");
                            dialog_fac.setCancelable(false);

                            final ProgressBar progress = (ProgressBar) dialog_fac.findViewById(R.id.progress);

                            Button text = (Button) dialog_fac.findViewById(R.id.ok);
                            text.setVisibility(View.VISIBLE);
                            text.setText("بستن");
                            text.setTypeface(IranSans);
                            text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog_fac.dismiss();
                                }
                            });
                            WebView wb = (WebView) dialog_fac.findViewById(R.id.webView1);
                            WebSettings settingswb = wb.getSettings();
                            text.bringToFront();
                            settingswb.setDefaultTextEncodingName("utf-8");
                            settingswb.setCacheMode(WebSettings.LOAD_NO_CACHE);
                            settingswb.setSaveFormData(false);
                            settingswb.setSupportZoom(true);
                            settingswb.setDisplayZoomControls(false);
                            settingswb.setBuiltInZoomControls(true);

                            String code = finalScheme;
                            wb.loadUrl(getString(R.string.url) + "admin/printData.php?id=" + code + "&codRah=" + code + "&fromApp=true");
                            Log.v("this", getString(R.string.url) + "admin/printData.php?id=" + code + "&codRah=" + code + "&fromApp=true");
                            wb.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                                    handler.proceed();
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    view.loadUrl(url);
                                    return true;
                                }

                                public void onPageFinished(WebView view, String url) {
                                    try {
                                        progress.setVisibility(View.GONE);
                                    } catch (final IllegalArgumentException e) {

                                    } catch (final Exception e) {
                                    } finally {

                                    }
                                }
                            });

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog_fac.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            dialog_fac.show();
                            dialog_fac.getWindow().setAttributes(lp);
                        }
                    });
                    mAlert.show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Func.checkSabad(this);

        LeftSabad lsabad=new LeftSabad(this);
        if (drawer != null)
            drawer.update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pishnahadrecycle != null && pishnahadrecycle.getAdapter() != null)
            pishnahadrecycle.getAdapter().notifyDataSetChanged();
        if (topsellrecycle != null && topsellrecycle.getAdapter() != null)
            topsellrecycle.getAdapter().notifyDataSetChanged();
        if (jadidtarinrecycle != null && jadidtarinrecycle.getAdapter() != null)
            jadidtarinrecycle.getAdapter().notifyDataSetChanged();
    }
}
