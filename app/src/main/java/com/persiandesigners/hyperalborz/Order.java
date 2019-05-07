package com.persiandesigners.hyperalborz;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by navid on 7/5/2016.
 */
public class Order extends AppCompatActivity {
    ProgressDialog ringProgressDialog;
    Toolbar toolbar;
    Typeface typeface2;
    CustomProgressDialog mCustomProgressDialog;

    private static String url;
    RecyclerView recycle;
    Boolean loadmore = true, taskrunning;
    int page = 0, in, to;
    private List<CatItems> catItems;
    private CatsAdapter adapter;
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    Boolean confirmButton=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);

        declare();
        actionbar();


        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        url = getString(R.string.url) + "/getOrdes.php?uid=" + settings.getString("uid", "0") + "&page=0";
        new AsyncHttpTask(1).execute(url);

        recycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recycle.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loadmore) {
                    if (totalItemCount > previousTotal) {
                        previousTotal = totalItemCount;
                    }
                }
                if ((totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    if (taskrunning != null && taskrunning == false && loadmore) {
                        loadmore = false;
                        page = page + 1;
                        new AsyncHttpTask(1).execute(url);
                    }

                }
            }

        });

    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        Boolean showProgressBar;

        public AsyncHttpTask(int i) {
            if (i == 0) {
                showProgressBar = false;
                page = 0;
            } else
                showProgressBar = true;

        }

        @Override
        protected Integer doInBackground(String... params) {
            loadmore = false;
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                Log.v("this", params[0] + page);
                URL url = new URL(params[0] + page);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    String res = response.toString();
                    if (!res.contains("#non#")) {
                        parseResult("{contacts:[" + res.substring(res.indexOf("{"), res.length()));
                        result = 1;
                    } else
                        result = -1;
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.v("this", e.getMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            taskrunning = false;
            Log.v("this", "result " + result);
            if (result == 1) {
                if (adapter == null) {
                    adapter = new CatsAdapter(Order.this, catItems);
                    recycle.setAdapter(adapter);
                } else {
                    adapter.addAll(catItems);
                }

                if (showProgressBar == false) {
                    adapter = null;
                    recycle.setAdapter(null);
                }


            } else if (result == -1) {
                Log.v("this", "mom");
                recycle.setVisibility(View.GONE);
                TextView textView1 = (TextView) findViewById(R.id.textView1ss);
                textView1.setVisibility(View.VISIBLE);
                textView1.setTypeface(typeface2);
            } else {
                MyToast.makeText(Order.this, DariGlyphUtils.reshapeText(getResources().getString(R.string.problemload)));
            }

            mCustomProgressDialog.dismiss("");
        }
    }

    public class CatsAdapter extends RecyclerView.Adapter<CatsAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private List<CatItems> list;

        public CatsAdapter(Context context, List<CatItems> feedItemList) {
            inflater = LayoutInflater.from(context);
            this.list = feedItemList;
        }


        public void addAll(List<CatItems> catItems) {
            if (this.list == null) {
                this.list = catItems;
            } else {
                this.list.addAll(catItems);
            }
            notifyDataSetChanged();
            notifyItemInserted(list.size());
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parrent, int i) {
            View view = inflater.inflate(R.layout.row_orders, parrent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
            final CatItems item = list.get(position);
            viewHolder.orders.setText(android.text.Html.fromHtml(item.getorders()));
            viewHolder.orders.setMovementMethod(LinkMovementMethod.getInstance());

            viewHolder.price.setText("مبلغ کل : " + item.getprice() + " تومان");

            viewHolder.dates.setText(item.getdates());
            viewHolder.stat.setText("وضعیت سفارش : " + item.getstat());

            viewHolder.show_factor.setTag(item.getRahgiri());
            viewHolder.show_factor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id=viewHolder.show_factor.getTag().toString();
                    final Dialog dialog_fac = new Dialog(Order.this, R.style.DialogStyler);
                    dialog_fac.setContentView(R.layout.page2);
                    dialog_fac.setTitle("وضعیت سفارش شما");

                    Button text = (Button) dialog_fac.findViewById(R.id.ok);
                    text.setVisibility(View.VISIBLE);
                    text.setText("باشه");
                    text.setTypeface(typeface2);
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_fac.dismiss();
                        }
                    });
                    WebView wb=(WebView)dialog_fac.findViewById(R.id.webView1);
                    WebSettings settingswb = wb.getSettings();
                    settingswb.setDefaultTextEncodingName("utf-8");
                    settingswb.setCacheMode(WebSettings.LOAD_NO_CACHE);
                    settingswb.setSaveFormData(false);
                    settingswb.setSupportZoom(true);
                    settingswb.setDisplayZoomControls(false);
                    settingswb.setBuiltInZoomControls(true);
                    String uri=getString(R.string.url)+"admin/printData.php?id="+id+"&codRah="+id+"&fromApp=true";
                    Log.v("this",uri);
                    wb.loadUrl(uri);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog_fac.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog_fac.show();
                    dialog_fac.getWindow().setAttributes(lp);
                }
            });

            viewHolder.delete.setTag(item.getRahgiri());
            if (!item.getStat_code().equals("30") && !item.getPaymentId().equals("2"))
                viewHolder.delete.setVisibility(View.VISIBLE);
            else
                viewHolder.delete.setVisibility(View.GONE);

            if(confirmButton){
                if(item.getIs_deliverd().equals("1")){
                    viewHolder.confirm.setVisibility(View.GONE);
                }else {
                    viewHolder.confirm.setVisibility(View.VISIBLE);
                    viewHolder.confirm.setTag(item.getRahgiri());
                    viewHolder.confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder a = new AlertDialog.Builder(Order.this);
                            a.setMessage("آیا این سفارش را دریافت کردید؟");
                            a.setPositiveButton(("بلی"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                                    new Html(new OnTaskFinished() {
                                        @Override
                                        public void onFeedRetrieved(String body) {
                                            Log.v("this", body);
                                            if (body.equals("errordade")) {
                                                MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                                            } else if (body.equals("ok")) {
                                                for (int i = 0; i < catItems.size(); i++) {
                                                    if (catItems.get(i).getRahgiri().equals(viewHolder.delete.getTag().toString())) {
                                                        catItems.get(i).setIs_deliverd("1");
                                                        adapter.notifyDataSetChanged();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }, true, Order.this, "").execute(getString(R.string.url) +
                                            "/confrimDelOrder.php?id=" + viewHolder.delete.getTag().toString() + "&n=" + number + "&uid=" + Func.getUid(Order.this));
                                }
                            });
                            a.setNegativeButton(("خیر"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = a.show();
                            TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                            messageText.setGravity(Gravity.RIGHT);
                            messageText.setTypeface(typeface2);
                        }
                    });
                }
            }

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder a = new AlertDialog.Builder(Order.this);
                    a.setMessage("آیا از حذف این سفارش مطمئن هستید؟");
                    a.setPositiveButton(("بلی"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                            new Html(new OnTaskFinished() {
                                @Override
                                public void onFeedRetrieved(String body) {
                                    Log.v("this", body);
                                    if (body.equals("errordade")) {
                                        MyToast.makeText(getApplicationContext(), "اشکالی پیش آمده است");
                                    }else if(body.equals("ok")){
                                        for (int i=0;i<catItems.size();i++){
                                            if(catItems.get(i).getRahgiri().equals(viewHolder.delete.getTag().toString())){
                                                catItems.remove(i);
                                                adapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }, true, Order.this, "").execute(getString(R.string.url) +
                                    "/delOrder.php?id="+viewHolder.delete.getTag().toString()+"&n="+number+"&uid="+Func.getUid(Order.this));
                        }
                    });
                    a.setNegativeButton(("خیر"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = a.show();
                    TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.RIGHT);
                    messageText.setTypeface(typeface2);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (list == null)
                return 0;
            else
                return list.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView orders, price, dates, stat;
            Button show_factor,delete,confirm;

            public MyViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                orders = (TextView) itemView.findViewById(R.id.orders);
                orders.setTypeface(typeface2);
                price = (TextView) itemView.findViewById(R.id.price);
                price.setTypeface(typeface2);
                dates = (TextView) itemView.findViewById(R.id.dates);
                dates.setTypeface(typeface2);
                stat = (TextView) itemView.findViewById(R.id.stat);
                stat.setTypeface(typeface2);
                show_factor = (Button) itemView.findViewById(R.id.show_factor);
                show_factor.setTypeface(typeface2);
                delete = (Button) itemView.findViewById(R.id.delete);
                delete.setTypeface(typeface2);
                confirm = (Button) itemView.findViewById(R.id.confirm);
                confirm.setTypeface(typeface2);
            }

            @Override
            public void onClick(View v) {
                CatItems item = list.get(getAdapterPosition());

            }
        }
    }


    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("contacts");
            catItems = new ArrayList<>();
            if (posts.length() < 20)
                loadmore = false;

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                CatItems item = new CatItems();
                item.setorders(post.optString("orders"));
                item.setprice(post.optString("price"));
                item.setstat(post.optString("stat"));
                item.setdates(post.optString("dates"));
                item.setRahgiri(post.optString("rahgiri"));
                item.setStat_code(post.optString("stat_code"));
                item.setIs_deliverd(post.optString("delivered"));
                item.setPaymentId(post.optString("paymentID"));
                catItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("this", e.getMessage());
        }
    }


    public class CatItems {
        private String orders, price, stat, dates,rahgiri,stat_code, is_deliverd,paymentId;

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public String getIs_deliverd() {
            return is_deliverd;
        }

        public void setIs_deliverd(String is_deliverd) {
            this.is_deliverd = is_deliverd;
        }

        public String getStat_code() {
            return stat_code;
        }

        public void setStat_code(String stat_code) {
            this.stat_code = stat_code;
        }

        public String getRahgiri() {
            return rahgiri;
        }

        public void setRahgiri(String rahgiri) {
            this.rahgiri = rahgiri;
        }

        public String getdates() {
            return dates;
        }

        public void setdates(String dates) {
            this.dates = dates;
        }

        public String getorders() {
            return orders;
        }

        public void setorders(String orders) {
            this.orders = orders;
        }

        public void setprice(String price) {
            this.price = price;
        }

        public String getprice() {
            return price;
        }

        public void setstat(String stat) {
            this.stat = stat;
        }

        public String getstat() {
            return stat;
        }

    }


    private void declare() {
        mCustomProgressDialog = new CustomProgressDialog(this);
        mCustomProgressDialog.show("");

        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");

        recycle = (RecyclerView) findViewById(R.id.RecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recycle.setLayoutManager(mLayoutManager);
        confirmButton=getResources().getBoolean(R.bool.show_confirm_delivery_bt_in_orders);
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(getString(R.string.orders));

        Func.checkSabad(Order.this);
        ImageView img_sabad = (ImageView)findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);

    }
}
