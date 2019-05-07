package com.persiandesigners.hyperalborz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class SabadKharid_s2 extends AppCompatActivity {
    Toolbar toolbar;
    Typeface typeface2;
    TimeListAdapter adapter;
    ListView lv;
    TextView jamKharid, takmil;
    DatabaseHandler db;
    String urlimg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sabadkharid);

        declare();
        actionbar();

        Cursor cursor = db.getSabadkharid();
        adapter = new TimeListAdapter(this, cursor);
        lv.setAdapter(adapter);

        takmil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int j=Integer.parseInt(db.getSabadMablaghKolWithTakhfif());
//                if(j>=20000) {
////                    if(Func.getUid(SabadKharid_s2.this).equals("0")){
////                        MyToast.makeText(SabadKharid_s2.this,"تنها کاربران عضو میتوانند خرید انجام دهند");
////                        Intent in=new Intent (SabadKharid_s2.this,Login.class);
////                        in.putExtra("sabad","true");
////                        startActivity(in);
////                    }else {
                        Intent in = new Intent(SabadKharid_s2.this, SabadKharid_s1.class);
                        startActivity(in);
////                    }
//                }else{
//                    final Alert mAlert = new Alert(SabadKharid_s2.this, R.style.mydialog);
//                    mAlert.setIcon(android.R.drawable.ic_dialog_alert);
//                    mAlert.setMessage("خرید شما حداقل باید 20 هزارتومان باشد");
//                    mAlert.setPositveButton("باشه", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            mAlert.dismiss();
//                        }
//                    });
//                    mAlert.show();
//                }
            }
        });

        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished() {
            @Override
            public void onFeedRetrieved(String body) {
                Log.v("this","len "+body.length());
                if(body.length()>2 && !body.contains("errordade")){
                    AlertDialog.Builder a = new AlertDialog.Builder(SabadKharid_s2.this);
                    a.setMessage(body);
                    a.setCancelable(false);
                    a.setPositiveButton(("بستن"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(SabadKharid_s2.this,FistActiivty.class));
                            finish();
                        }
                    });
                    AlertDialog dialog = a.show();
                    TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.RIGHT);
                    messageText.setTypeface(typeface2);

                }
            }
        }, false, SabadKharid_s2.this, "").execute(getString(R.string.url) + "/getIsShopClose.php?n="+number);
    }

    public class TimeListAdapter extends CursorAdapter {
        public TimeListAdapter(Context context, Cursor c) {
            super(context, c);
        }

        public class ViewHolder {
            TextView tvTitle, gheymat, tvtedad, del, gheymatkol;
            ImageView img, plus, minus;

            public ViewHolder(View row) {
                tvTitle = (TextView) row.findViewById(R.id.title);
                tvTitle.setTypeface(typeface2);
                tvtedad = (TextView) row.findViewById(R.id.tvtedad);
                tvtedad.setTypeface(typeface2);
                del = (TextView) row.findViewById(R.id.del);
                del.setTypeface(typeface2);
                gheymat = (TextView) row.findViewById(R.id.gheymat);
                gheymat.setTypeface(typeface2);
                gheymatkol = (TextView) row.findViewById(R.id.gheymatkol);
                gheymatkol.setTypeface(typeface2);
                img = (ImageView) row.findViewById(R.id.img);
                plus = (ImageView) row.findViewById(R.id.plus);
                minus = (ImageView) row.findViewById(R.id.minus);
            }
        }

        @Override
        public View newView(Context context, Cursor arg1, ViewGroup arg2) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.sabad_row, arg2, false);
            ViewHolder holder = new ViewHolder(row);
            row.setTag(holder);
            return row;
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {
            final ViewHolder holder = (ViewHolder) v.getTag();
            holder.tvtedad.setText(c.getInt(4) + "");
            holder.plus.setTag(c.getInt(0));
            holder.plus.setTag(-1,c.getInt(5));//maxcount
            holder.minus.setTag(c.getInt(0));
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Id = holder.plus.getTag().toString();
                    int n=db.getNumProd(Id) + 1;
                    int max=Integer.parseInt(holder.plus.getTag(-1).toString());
                    if(n<=max){
                        db.updateTedad(n, Id);
                        adapter.updateUI();
                        calTop();
                    }else
                        MyToast.makeText(SabadKharid_s2.this,"محصول بیشتر از این تعداد موجود نیست");
                }
            });
            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Id = holder.minus.getTag().toString();
                    int n=db.getNumProd(Id) - 1;
                    if(n>0){
                        db.updateTedad(n, Id);
                        adapter.updateUI();
                        calTop();
                    }

                }
            });

            holder.tvTitle.setText(c.getString(1));
            holder.del.setTag(c.getInt(0));
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteSabad(holder.del.getTag().toString());
                    if (db.getcountsabad() == 0) {
                        startActivity(new Intent(SabadKharid_s2.this, FistActiivty.class));
                        finish();
                    } else {
                        adapter.updateUI();
                        calTop();
                    }
                }
            });

            int num=c.getInt(4);
            int numOmde=c.getInt(9);
            String prices=c.getString(3);
            if(numOmde>0 && num>=numOmde){
                prices=c.getString(10);
            }

            int price = c.getInt(4) * Integer.parseInt(prices);


            holder.gheymat.setText(Func.getCurrency(prices) + getString(R.string.toman));
            holder.gheymatkol.setText(Func.getCurrency(price + "") + getString(R.string.toman));

            if(!c.getString(2).contains("Opitures"))
                Glide.with(SabadKharid_s2.this).load(urlimg+"Opitures/"+ c.getString(2)).into(holder.img);
            else
                Glide.with(SabadKharid_s2.this).load(urlimg+ c.getString(2)).into(holder.img);
        }

        private void updateUI() {
            swapCursor(db.getSabadkharid());
            //notifyDataSetChanged();
        }

    }

    private void calTop() {
        jamKharid.setText(Func.getCurrency(db.getSabadMablaghKolWithTakhfif()) + getString(R.string.toman));
    }

    private void declare() {
        urlimg=getString(R.string.url);

        typeface2 = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");

        lv = (ListView) findViewById(R.id.list);

        jamKharid = (TextView) findViewById(R.id.jamkharid);
        jamKharid.setTypeface(typeface2);
        takmil = (TextView) findViewById(R.id.takmil);
        takmil.setTypeface(typeface2);

        TextView tvjamkharid = (TextView) findViewById(R.id.tvjamkharid);
        tvjamkharid.setTypeface(typeface2);

        db = new DatabaseHandler(this);
        if (!db.isOpen())
            db.open();

        db.check();

        if (db.getcountsabad() == 0) {
            LinearLayout noitem = (LinearLayout) findViewById(R.id.noitem);
            noitem.setVisibility(View.VISIBLE);
            TextView tvnoitem = (TextView) findViewById(R.id.tvnoitem);
            tvnoitem.setTypeface(typeface2);
        }

        calTop();
//        ImageView deleteall=(ImageView)findViewById(R.id.deleteall);
//        deleteall.setVisibility(View.VISIBLE);
//        deleteall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder a = new AlertDialog.Builder(SabadKharid_s2.this);
//                a.setMessage("آیا از حذف کالاهای خود از سبد خرید اطمینان دارید؟");
//                a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        db.clearSabadKharid();
//                        onBackPressed();
//                    }
//                });
//                a.setNegativeButton(("انصراف"),  new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                    }
//                });
//
//                AlertDialog dialog = a.show();
//                TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
//                messageText.setGravity(Gravity.RIGHT);
//                messageText.setTypeface(typeface2);
//
//            }
//        });

        TextView removeall = (TextView) findViewById(R.id.removeall);
        removeall.setTypeface(typeface2);
        removeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Alert mAlert = new Alert(SabadKharid_s2.this, R.style.mydialog);
                mAlert.setIcon(android.R.drawable.ic_dialog_alert);
                mAlert.setMessage("ایا از حذف کلیه اجناس سبد خرید مطمئن هستید؟");
                mAlert.setPositveButton("بله", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlert.dismiss();
                        db.clearSabadKharid();
                        startActivity(new Intent(SabadKharid_s2.this, FistActiivty.class));
                        finish();
                    }
                });

                mAlert.setNegativeButton("خیر", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlert.dismiss();
                    }
                });

                mAlert.show();

//                AlertDialog.Builder a = new AlertDialog.Builder(SabadKharid_s2.this);
//                a.setMessage("ایا از حذف کلیه اجناس سبد خرید مطمئن هستید؟");
//                a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        db.clearSabadKharid();
//                        startActivity(new Intent(SabadKharid_s2.this,FistActiivty.class));
//                        finish();
//                    }
//                });
//                a.setNegativeButton(("نه"),  new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                AlertDialog dialog = a.show();
//                TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
//                messageText.setGravity(Gravity.RIGHT);
//                messageText.setTypeface(typeface2);
            }
        });
    }

    private void actionbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Func action = new Func(this);
        action.MakeActionBar(getString(R.string.sabadkhrid));
        action.hideSabadKharidIcon();;
//        action.hideSearch();

        ImageView imgSearch = (ImageView) findViewById(R.id.imgsearch);
        if (imgSearch != null) {
            imgSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SabadKharid_s2.this, Search.class));
                    finish();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(db!=null){
            Cursor cursor = db.getSabadkharid();
            adapter = new TimeListAdapter(this, cursor);
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_down,R.anim.go_up);
    }
}
