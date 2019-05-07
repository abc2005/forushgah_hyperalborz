package com.persiandesigners.hyperalborz;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cats extends AppCompatActivity {
    Toolbar toolbar;
    Typeface typeface2;
    CustomProgressDialog mCustomProgressDialog;
    private List<CatItems> catItems;
    private CatsAdapter adapter;
    RecyclerView recycle;

    GridView grid_cat;
    ListAdapterCat ladap_cat;
    String title;
    Bundle bl;
    LinearLayout hide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cats);

        declare();

        if (Func.isInternet(getApplicationContext()) ||
                Func.hasOffline(getApplicationContext(), "Cats", 0)) {
            if(getResources().getBoolean(R.bool.show_cats_as_grid)==false)
                new getXml().execute();
            else {
                bl = getIntent().getExtras();
                if (bl != null && bl.getString("catId") != null) {
                    getCats(bl.getString("catId"));
                    title = bl.getString("onvan");
                } else {
                    getCats("0");
                    title = getString(R.string.cats);
                }
            }
        } else {
            startActivity(new Intent(this, NoInternet.class));
            finish();
        }
        actionbar();
    }

    private void getCats(String subcat) {
        mCustomProgressDialog.show("");
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this", body);
                if (body.equals("errordade")) {
                    MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                }else{
                    hide.setVisibility(View.GONE);
                    final ArrayList<HashMap<String, String>> dataC = new ArrayList<HashMap<String, String>>();
                    try {
                        JSONObject jsonObj = new JSONObject(body);
                        JSONArray contacts = jsonObj.getJSONArray("contacts");
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            HashMap<String, String> contact = new HashMap<String, String>();
                            contact.put("id", new String(c.getString("id")));
                            contact.put("img", new String(c.getString("thumb")));
                            contact.put("name", new String(c.getString("name")));
                            contact.put("hasSubCats", new String(c.getString("hsc")));
                            dataC.add(contact);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (ladap_cat == null) {
                        ladap_cat = new ListAdapterCat(Cats.this, dataC, typeface2);
                        grid_cat.setAdapter(ladap_cat);

                        if(dataC.size()==0){
                            Intent in = new Intent(Cats.this, Products.class);
                            in.putExtra("catId",bl.getString("catId"));
                            in.putExtra("onvan",bl.getString("onvan"));
                            startActivity(in);
                            overridePendingTransition(0, 0);
                            finish();
                        }else {
                            mCustomProgressDialog.dismiss("");
                            grid_cat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent in =null;
                                    if(dataC.get(position).get("hasSubCats").equals("0"))
                                        in=new Intent(Cats.this, Products.class);
                                    else
                                        in=new Intent(Cats.this, Cats.class);
                                    in.putExtra("catId", dataC.get(position).get("id"));
                                    in.putExtra("onvan", dataC.get(position).get("name"));
                                    startActivity(in);
                                }
                            });
                            LinearLayout lnsabadbottom = (LinearLayout) findViewById(R.id.lnsabadbottom);
                            lnsabadbottom.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }, false, Cats.this, "").execute(getString(R.string.url) + "/getAllCats.php?subcat="+subcat);
    }

    public class getXml extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Bundle bl = getIntent().getExtras();
            if (Func.isInternet(getApplicationContext())) {
                StringBuilder response = new StringBuilder();
                try {
                    String search = "0";
                    if (bl != null) {
                        search = bl.getString("search");
                    }
                    URL url = new URL(getString(R.string.url) + "getCats.php");
                    Log.v("this", getString(R.string.url) + "getCats.php");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("search", search);
                    ;
                    String query = builder.build().getEncodedQuery();

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();

                    int statusCode = conn.getResponseCode();
                    if (statusCode == 200) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }
                    }
                } catch (Exception e) {
                    Log.d("this", e.getLocalizedMessage());
                }
                if (bl == null)//search nabud
                    Func.insertOffline(getApplicationContext(), response.toString(), "Cats", 0);
                return response.toString();
            } else {
                return Func.getOffline(getApplicationContext(), "Cats", 0);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mCustomProgressDialog.dismiss("");

//            adapter = new CatsAdapter(Cats.this,
//                    Func.parseResultCats(result.replaceAll(getString(R.string.qoteJson), "]").replaceAll(",,", ","), ""));
            recycle.setAdapter(adapter);

            grid_cat.setVisibility(View.GONE);
        }
    }

    private void declare() {
        hide=(LinearLayout) findViewById(R.id.hideit);

        mCustomProgressDialog = new CustomProgressDialog(this);
        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");
        recycle = (RecyclerView) findViewById(R.id.RecyclerView);
        LinearLayoutManager linearCats = new LinearLayoutManager(this);
        recycle.setLayoutManager(linearCats);
        grid_cat = (GridView) findViewById(R.id.grid_cat);
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(title);

        Func.checkSabad(Cats.this);
        ImageView img_sabad = (ImageView)findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);

        Func.checkSabad(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LeftSabad lsabad=new LeftSabad(this);
        actionbar();
        Func.checkSabad(this);
    }
}
