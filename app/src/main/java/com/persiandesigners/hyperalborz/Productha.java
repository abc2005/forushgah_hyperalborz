package com.persiandesigners.hyperalborz;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navid on 11/14/2017.
 */
public class Productha extends AppCompatActivity implements ShopOptionAdapter_cat,
        ShopOptionAdapter_sub_cat, MyAdapterCallBack {
    Typeface typeface;
    RecyclerView rc_cats, rc_subcat, rc_products;
    TextView loading;
    List<CatsItems> items, mainCats, subCats;
    ProgressBar progressBar;
    SubCatsAdapter subcatadapter;
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount, subCatPos = 0;
    GridLayoutManager mLayoutManager;
    Boolean loadmore = true, taskrunning;
    int page = 0, in, to;
    String urls, catId = "0", chooseId;
    MyAdapter2 productAdaper;
    List<FeedItem> items_products;
    AsyncTask<String, Void, String> AsyncProduct;
    Boolean FirstTime = true;
    Toolbar toolbar;
    Bundle bl;
    Boolean show_maincats_on_page2;
	CatsAdapter adapter ;
	
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productha);

        declare();
        if (bl != null && bl.getString("catId") != null) {
            catId = bl.getString("catId");
            if (bl.getString("chooseId") != null)
                chooseId = bl.getString("chooseId");
        }

        if (Func.isInternet(this) == false) {
            Func.NoNet(this);
            loading.setVisibility(View.GONE);
        } else {
            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            String url = getString(R.string.url) + "/getCatsTezol.php?catId=" + catId + "&n=" + number;
            if (show_maincats_on_page2)
                url = getString(R.string.url) + "/getCatsTezol.php?catId=0&n=" + number;

            new Html(new OnTaskFinished() {
                @Override
                public void onFeedRetrieved(String body) {
                    Log.v("this", body);
                    if (body.equals("errordade")) {
                        MyToast.makeText(Productha.this.getApplicationContext(), "اتصال اینترنت خود را بررسی کنید");
                    } else {
                        LinearLayout lnsabadbottom = (LinearLayout) findViewById(R.id.lnsabadbottom);
                        lnsabadbottom.setVisibility(View.VISIBLE);

                        loading.setVisibility(View.GONE);
                        TahlilData(body);
                    }
                }
            }, false, Productha.this, "").execute(url);

            rc_products.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = rc_products.getChildCount();
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
                            getProducts(page);
                        }

                    }
                }
            });
        }
        actionbar();
    }


    private void TahlilData(String body) {
        items = Func.ParseCats(body);
        mainCats = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (bl != null && bl.getString("catId") != null && show_maincats_on_page2 == false) {//az cat umade bud
                    if (items.get(i).getParrent().equals(catId))
                        mainCats.add(items.get(i));
                } else if (items.get(i).getParrent().equals("0")) {
                    mainCats.add(items.get(i));
                }
            }
            adapter = new CatsAdapter(Productha.this, mainCats, Productha.this, catId, chooseId);
            rc_cats.setAdapter(adapter);
            for (int i = 0; i < mainCats.size(); i++) {
                if (mainCats.get(i).getId().equals(chooseId))
                    rc_cats.scrollToPosition(i);
            }


            if (items.size() > 0 && mainCats.size() > 0) {
                if (FirstTime) {
                    FirstTime = false;
                    //                    if(!catId.equals("0"))
//                        changeCats(catId);
//                    else
                    if (bl != null && bl.getString("chooseId") != null)
                        changeCats(bl.getString("chooseId"));
                    else
                        changeCats(mainCats.get(0).getId());
                } else {
//                    if(!catId.equals("0"))
//                        changeCats(catId);
//                    else
                    changeCats(mainCats.get(0).getId());
                }
            } else {
                try {
                    urls = getString(R.string.url) + "/getProductsTezol.php?id=" + mainCats.get(0).getId() + "&from=cats&page=0";
                } catch (Exception e) {
                    urls = getString(R.string.url) + "/getProductsTezol.php?id=" +catId + "&from=cats&page=0";
                }
                getProducts(0);
            }
        }
        rc_products.setAdapter(null);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void changeCats(String id) {
        page = 0;
        subCats = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getParrent().equals(id)) {
                subCats.add(items.get(i));
            }
        }
        if (subCats != null && subCats.size() > 0) {
            subcatadapter = new SubCatsAdapter(Productha.this, subCats, Productha.this);
            rc_subcat.setAdapter(subcatadapter);
            if (subCats != null && subCats.size() > 0) {
                while (subCatPos >= subCats.size() && subCatPos >= 0) {
                    subCatPos--;
                }
//            changeSubCats(subCats.get(subCatPos).getId());
            } else {
//            changeSubCats(id);//subcat nadarad
            }
            changeSubCats(id);
            rc_products.setAdapter(null);
            progressBar.setVisibility(View.GONE);
        }else{
            rc_subcat.setAdapter(null);
            rc_products.setAdapter(null);
			changeSubCats(id);
        }
    }


    private void declare() {
        show_maincats_on_page2 = getResources().getBoolean(R.bool.show_maincats_on_page2);
        bl = getIntent().getExtras();
        FirstTime = true;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rc_cats = (RecyclerView) findViewById(R.id.cats);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Productha.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(true);
        rc_cats.setLayoutManager(layoutManager);

        rc_subcat = (RecyclerView) findViewById(R.id.subcats);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(Productha.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2.setReverseLayout(true);
        rc_subcat.setLayoutManager(layoutManager2);

        rc_products = (RecyclerView) findViewById(R.id.products);
        Display display = Productha.this.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth / 140);
        mLayoutManager = new GridLayoutManager(Productha.this, columns);
        rc_products.setLayoutManager(mLayoutManager);

        typeface = Func.getTypeface(this);
        loading = (TextView) findViewById(R.id.loading);
        loading.setTypeface(typeface);
    }

    @Override
    public void changeSubCats(String id) {
        page = 0;
        progressBar.setVisibility(View.VISIBLE);
        rc_products.setAdapter(null);
        productAdaper = null;

        changeTitleName(id);

        urls = "http://hyperalborz.ir//getProductsTezol.php?id=" + id + "&from=cats&page=0";
        getProducts(0);

        //agar zirdaste sevom dasht, ba click ruye subcat, subcat taghir kone be zir daste 3
        changeSubCatsThird(id);
    }

    private void changeTitleName(String id) {
        TextView title_toolbar = (TextView) findViewById(R.id.title_toolbar);
        title_toolbar.setTypeface(typeface);

        for (int i=0; i<items.size();i++){
            if(items.get(i).getId().equals(id)){
                title_toolbar.setText(items.get(i).getName());
                break;
            }
        }
    }

    private void changeSubCatsThird(String id) {
        page = 0;
        subCats = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getParrent().equals(id)) {
                subCats.add(items.get(i));
            }
        }
        if (subCats != null && subCats.size() > 0) {
            subcatadapter = new SubCatsAdapter(Productha.this, subCats, Productha.this);
            rc_subcat.setAdapter(subcatadapter);
        }
    }

    private void getProducts(int p) {
        if (AsyncProduct != null)
            AsyncProduct.cancel(true);

        taskrunning = true;
        AsyncProduct = new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                taskrunning = false;
                if (body.equals("errordade")) {
                    MyToast.makeText(Productha.this, "اتصال اینترنت خود را بررسی کنید");
                } else {
                    items_products = Func.parseResult(body);
                    if (items_products != null && items_products.size() < 20) {
                        loadmore = false;
                    } else {
                        loadmore = true;
                    }

                    if (productAdaper == null) {
                        productAdaper = new MyAdapter2(Productha.this, items_products);
                        rc_products.setAdapter(productAdaper);
                        productAdaper.setFullWidth(true);
                    } else {
                        productAdaper.addAll(items_products);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, false, Productha.this, "").execute(urls + p);
    }

    public void changeSubCatsAppend(String id) {
        progressBar.setVisibility(View.VISIBLE);
        urls = getString(R.string.url) + "/getProductsTezol.php?id=" + id + "&from=cats&page=0";
        getProducts(0);
        subcatadapter.nextPosMarket();
    }

    public void NotifyAdapters() {
        if (productAdaper != null) {
            Log.v("this", "not ");
            productAdaper.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (productAdaper != null) {
            productAdaper.notifyDataSetChanged();
        }
    }

    @Override
    public void checkSabad() {
        Func.checkSabad(this);
        LeftSabad lsabad=new LeftSabad(this);
        productAdaper.notifyDataSetChanged();
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        if (bl.getString("onvan") != null)
            action.MakeActionBar(bl.getString("onvan"));
        Func.checkSabad(this);
        action.HideSearch();
        action.hideDrawer();
        action.showBack();

        ImageView img_sabad = (ImageView) findViewById(R.id.img_sabad);
        img_sabad.setVisibility(View.GONE);
        TextView tvnumsabad = (TextView) findViewById(R.id.text_numkharid);
        tvnumsabad.setVisibility(View.GONE);
        ImageView imglogo = (ImageView) findViewById(R.id.imglogo);
        imglogo.setVisibility(View.GONE);
    }
	
 @Override
    public void onBackPressed() {
        try {
            if(subcatadapter!=null && subcatadapter.getPos()>=0){
                changeCats(adapter.getChooseCatId());
                subcatadapter.setPos(-1);
            }else
                super.onBackPressed();
        } catch (Exception e) {
            super.onBackPressed();
        }
    }
	
	   @Override
    protected void onStart() {
        super.onStart();
           LeftSabad lsabad=new LeftSabad(this);
        try {
            Func.checkSabad(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
