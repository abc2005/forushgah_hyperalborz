package com.persiandesigners.hyperalborz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by navid on 12/29/2016.
 */
public class Blog extends AppCompatActivity {
    RecyclerView blog;
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    Boolean  loadmore = true,taskrunning;
    int page = 0, in, to;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog);

        declrea();

        blog.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = blog.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loadmore) {
                    if (totalItemCount > previousTotal) {
                        previousTotal = totalItemCount;
                    }
                }
                if ((totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    if(taskrunning!=null && taskrunning==false && loadmore){
                        loadmore = false;
                        page = page + 1;
                        new Html(new OnTaskFinished() {
                            @Override
                            public void onFeedRetrieved(String body) {
                                if (body.equals("errordade")) {

                                } else {
                                    TahlilData(body);
                                }
                            }
                        }, true, Blog.this, "").execute(getString(R.string.url) + "/getBlog.php?page="+page);
                    }

                }
            }

        });


        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                if (body.equals("errordade")) {

                } else {
                    TahlilData(body);
                }
            }
        }, true, Blog.this, "").execute(getString(R.string.url) + "/getBlog.php");
    }

    private void TahlilData(String body) {
        Blog_Adapter adapter=new Blog_Adapter(Blog.this,Func.ParseBlog(body));
        blog.setAdapter(adapter);
    }

    private void declrea() {
        blog = (RecyclerView) findViewById(R.id.blog);
        blog.setLayoutManager(new LinearLayoutManager(this));
    }
}
