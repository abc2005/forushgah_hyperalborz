package com.persiandesigners.hyperalborz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by navid on 7/24/2017.
 */
public class Drawer {
    Activity act;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    public RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    ActionBarDrawerToggle mDrawerToggle;
    String TITLES[];
    int ICONS[];
    Boolean activeKifPul = false;
    SharedPreferences settings;
    Typeface IranSans;

    public Drawer(final Activity act) {
        this.act = act;
        IranSans = Typeface.createFromAsset(act.getAssets(), "IRAN Sans Bold.ttf");
        settings = act.getSharedPreferences("settings", act.MODE_PRIVATE);
        if (act.getResources().getBoolean(R.bool.isActiveKif)) {
            activeKifPul = true;
        }
//        ICONS = new int[]{R.drawable.cat, //R.drawable.icon_light_share,
//                R.drawable.sabad_dark, R.drawable.ic_action_info, R.drawable.ic_timer_grey600_24dp
//                , R.drawable.ic_gift_grey600_24dp, R.drawable.ic_phone,
//                R.drawable.ic_action_action_label, R.drawable.favorits_dark, R.drawable.aboutus_dark, R.drawable.contactus,
//                R.drawable.ic_action_help, R.drawable.rules,
//                R.drawable.telegram, R.drawable.money2, R.drawable.money2
//                , R.drawable.ic_user_gray
//                , R.drawable.ic_action_remove_dark,
//                R.drawable.ic_action_remove_dark};
        if (settings.getString("uid", "0").equals("0")) {
            TITLES = act.getResources().getStringArray(R.array.itemnologin);
            ICONS = new int[]{R.drawable.homeicon, R.drawable.catss, //R.drawable.icon_light_share,
                    R.drawable.sabadss, R.drawable.infos,R.drawable.orders
                    , R.drawable.vijes, R.drawable.topsell,
                    R.drawable.recently, R.drawable.fav, R.drawable.talar, R.drawable.ctus,
                    R.drawable.edu, R.drawable.guidline,
                    R.drawable.telegrams, R.drawable.khadamat,
                    R.drawable.exit};
        } else {
            if (activeKifPul) {
                TITLES = act.getResources().getStringArray(R.array.itemloginKifPul);
                ICONS = new int[]{R.drawable.homeicon, R.drawable.catss, //R.drawable.icon_light_share,
                        R.drawable.sabadss, R.drawable.infos,R.drawable.orders
                        , R.drawable.vijes, R.drawable.topsell,
                        R.drawable.recently, R.drawable.fav, R.drawable.talar, R.drawable.ctus,
                        R.drawable.edu, R.drawable.guidline,
                        R.drawable.telegrams, R.drawable.kifpul, R.drawable.kifpul
                        , R.drawable.khadamat
                        , R.drawable.exit,
                        R.drawable.exit};
            } else {
                TITLES = act.getResources().getStringArray(R.array.itemlogin);
                ICONS = new int[]{R.drawable.homeicon, R.drawable.catss, //R.drawable.icon_light_share,
                        R.drawable.sabadss, R.drawable.infos,R.drawable.orders
                        , R.drawable.vijes, R.drawable.topsell,
                        R.drawable.recently, R.drawable.fav, R.drawable.talar, R.drawable.ctus,
                        R.drawable.edu, R.drawable.guidline,
                        R.drawable.telegrams, R.drawable.khadamat,
                        R.drawable.exit};
            }
        }

        mRecyclerView = (RecyclerView) act.findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(TITLES, ICONS, "", "", R.drawable.drawer, act);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(act);
        mRecyclerView.setLayoutManager(mLayoutManager);
        Drawer = (DrawerLayout) act.findViewById(R.id.DrawerLayout);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(act,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (Func.isInternet(act) == false) {
                                    MyToast.makeText(act, "اتصال اینترنت را بررسی کنید");
                                } else {
                                    Intent in = null;
                                    switch (position) {
                                        case 0:
                                            if (settings.getString("uid", "0").equals("0")) {
                                                act.startActivity(new Intent(act, Login.class));
                                            } else if (activeKifPul) {
                                                KifPul();
                                            }else{
                                                act.startActivity(new Intent(act, Profile.class));
                                            }
                                            break;
                                        case 1:
                                            act.startActivity(new Intent(act, FistActiivty.class));
                                            break;
                                        case 2:
                                            act.startActivity(new Intent(act, Cats.class));
                                            break;
//                            case 2:
//                                if (Build.VERSION.SDK_INT >= 23) {
//                                    if (act.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                            == PackageManager.PERMISSION_GRANTED) {
//                                        shareApp();
//                                    } else {
//                                        ActivityCompat.requestPermissions(act, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                                    }
//                                } else
//                                    shareApp();
//
//                                break;
                                        case 3:
                                            if (!NetworkAvailable.isNetworkAvailable(act)) {
                                                MyToast.makeText(act, (act.getString(R.string.nointernet)));
                                            } else {
                                                if (Func.isEmptySabadKharid(act)) {
                                                    MyToast.makeText(act, "سبد خرید خالی است");
                                                } else {

                                                    in = new Intent(act, SabadKharid_s2.class);
                                                    act.startActivity(in);
                                                    act.overridePendingTransition(R.anim.go_up,R.anim.go_down);
                                                }
                                            }
                                            break;
                                        case 4:
                                            if (!NetworkAvailable.isNetworkAvailable(act)) {
                                                MyToast.makeText(act, (act.getString(R.string.nointernet)));
                                            } else {
                                                dialogRahgiri();
                                            }
                                            break;
                                        case 5:
                                            if (!NetworkAvailable.isNetworkAvailable(act)) {
                                                MyToast.makeText(act, (act.getString(R.string.nointernet)));
                                            } else if (settings.getString("uid", "0").equals("0")) {
                                                MyToast.makeText(act, act.getString(R.string.onlyusers));
                                            } else {
                                                act.startActivity(new Intent(act, Order.class));
                                            }
                                            break;
                                        case 6:
                                            in = new Intent(act, Products.class);
                                            in.putExtra("for", "haraj");
                                            in.putExtra("title", "فروش ویژه");
                                            act.startActivity(in);
                                            break;
                                        case 7:
                                            in = new Intent(act, Products.class);
                                            in.putExtra("for", "porForush");
                                            in.putExtra("title", "پرفروش ترین ها");
                                            act.startActivity(in);
                                            break;

                                        case 8:
                                            in = new Intent(act, Products.class);
                                            in.putExtra("for", "jadidtarinha");
                                            in.putExtra("title", "جدیدترین ها");
                                            act.startActivity(in);
                                            break;
                                        case 9:
                                            DatabaseHandler db = new DatabaseHandler(act);
                                            if (!db.isOpen())
                                                db.open();
                                            Cursor cursor = db.getFavs();
                                            if (cursor.getCount() > 0) {
                                                in = new Intent(act, Products.class);
                                                in.putExtra("title", "علاقه مندی ها");
                                                in.putExtra("fav", "fav");
                                                act.startActivity(in);
                                            } else
                                                MyToast.makeText(act, "محصولی به علاقه مندی های خود اضافه نکرده اید");
                                            break;
                                        case 10:
                                            if (!NetworkAvailable.isNetworkAvailable(act)) {
                                                MyToast.makeText(act, (act.getString(R.string.nointernet)));
                                            } else {
                                                in = new Intent(act, Page.class);
                                                in.putExtra("w", "aboutus");
                                                in.putExtra("onvan", "درباره ما");
                                                act.startActivity(in);
                                            }
                                            break;
                                        case 11:
                                            if (!NetworkAvailable.isNetworkAvailable(act)) {
                                                MyToast.makeText(act, (act.getString(R.string.nointernet)));
                                            } else {
                                                in = new Intent(act, Contactus.class);
                                                in.putExtra("onvan", "تماس ما");
                                                act.startActivity(in);
                                            }
                                            break;
                                        case 12:
                                            if (!NetworkAvailable.isNetworkAvailable(act)) {
                                                MyToast.makeText(act, (act.getString(R.string.nointernet)));
                                            } else {
                                                in = new Intent(act, Page.class);
                                                in.putExtra("w", "shoppingGuide");
                                                in.putExtra("onvan", "راهنمای خرید");
                                                act.startActivity(in);
                                            }
                                            break;
                                        case 13:
                                            if (!NetworkAvailable.isNetworkAvailable(act)) {
                                                MyToast.makeText(act, (act.getString(R.string.nointernet)));
                                            } else {
                                                in = new Intent(act, Page.class);
                                                in.putExtra("w", "questions");
                                                in.putExtra("onvan", "پرسش های متداول");
                                                act.startActivity(in);
                                            }
                                            break;
                                        case 14:
                                            try {
                                                long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                                                new Html(new OnTaskFinished() {
                                                    @Override
                                                    public void onFeedRetrieved(String body) {
                                                        Log.v("this", "Sd " + body);
                                                        if(body.contains("http")) {
                                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(body));
                                                            act.startActivity(browserIntent);
                                                        }else{
                                                            MyToast.makeText(act,"آدرس کانال تعریف نشده است");
                                                        }
                                                    }
                                                }, true, act, "").execute(act.getString(R.string.url) + "/getTelegram.php?n=" + number);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case 15:
                                            if (activeKifPul)
                                                if (Func.getUid(act).equals("0"))
                                                    MyToast.makeText(act, "جهت دسترسی به این بخش ابتدا وارد شوید");
                                                else
                                                    KifPul();
                                            else
                                                act.startActivity(new Intent(act, Profile.class));
                                            break;
                                        case 16:
                                            if (activeKifPul) {
                                                if (Func.getUid(act).equals("0"))
                                                    MyToast.makeText(act, "جهت دسترسی به این بخش ابتدا وارد شوید");
                                                else {
                                                    Intent ina = new Intent(act, PageTextView.class);
                                                    ina.putExtra("url", act.getString(R.string.url) + "getKifSabeghe.php?uid=" + Func.getUid(act));
                                                    ina.putExtra("title", "سابقه کیف پول");
                                                    act.startActivity(ina);
                                                }
                                            } else {//exit
                                                if (settings.getString("uid", "0").equals("0")) {
                                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                                    intent.addCategory(Intent.CATEGORY_HOME);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    act.startActivity(intent);
                                                } else {
                                                    settings.edit().clear().commit();
                                                    act.startActivity(new Intent(act, FistActiivty.class));
                                                    act.finish();
                                                    break;
                                                }
                                            }
                                            break;
                                        case 17:
                                            if (activeKifPul)
                                                act.startActivity(new Intent(act, Profile.class));
                                            else {//exit
                                                if (settings.getString("uid", "0").equals("0")) {
                                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                                    intent.addCategory(Intent.CATEGORY_HOME);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    act.startActivity(intent);
                                                } else {
                                                    settings.edit().clear().commit();
                                                    act.startActivity(new Intent(act, FistActiivty.class));
                                                    act.finish();
                                                    break;
                                                }
                                            }
                                            break;
                                        case 18:
                                            if (settings.getString("uid", "0").equals("0")) {
                                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                                intent.addCategory(Intent.CATEGORY_HOME);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                act.startActivity(intent);
                                            } else {
                                                settings.edit().clear().commit();
                                                act.startActivity(new Intent(act, FistActiivty.class));
                                                act.finish();
                                                break;
                                            }
                                    }
                                }
                                Drawer.closeDrawers();
                            }
                        })
        );

        ImageView drawer = (ImageView) act.findViewById(R.id.drawer);
        drawer.setVisibility(View.VISIBLE);
        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawer.openDrawer(Gravity.RIGHT);
            }
        });
        //hide back button
        ImageView back = (ImageView) act.findViewById(R.id.back);
        if (back != null)
            back.setVisibility(View.GONE);
    }

    private void KifPul() {
        final long numbers = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        final EditText txtUrls = new EditText(act);
        txtUrls.setGravity(Gravity.RIGHT);
        txtUrls.setTypeface(IranSans);
        txtUrls.setHint("مبلغ به تومان");
        txtUrls.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(act).setTitle(("کیف پول"))
                .setMessage(("افزایش موجودی کیف پول")).setView(txtUrls)
                .setPositiveButton(("پرداخت"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String mabda = txtUrls.getText().toString();
                        if (mabda.trim().length() > 0) {
                            //                            new Html(new OnTaskFinished() {
//                                @Override
//                                public void onFeedRetrieved(String body) {
//                                    Log.v("this", body);
//                                    if (body.equals("errordade")) {
//                                        MyToast.makeText(act.getApplicationContext(), "اشکالی پیش آمده است");
//                                    }
//                                }
//                            }, true, act, "").execute(act.getString(R.string.url) + "/kifpul.php?uid=" + Func.getUid(act) + "&p=" + mabda.trim() + "&n=" + numbers);

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(act.getString(R.string.url) + "/kifpul.php?uid=" + Func.getUid(act) + "&p=" + mabda.trim() + "&n=" + numbers));
                            act.startActivity(browserIntent);
                            act.finish();
                        }
                    }
                }).setNegativeButton(("لغو"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }).show();
    }

    private void shareApp() {
        try {
            ArrayList<Uri> uris = new ArrayList<Uri>();
            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            sendIntent.setType("application/vnd.android.package-archive");
            //uris.add(Uri.fromFile(new File(getBaseContext().getApplicationInfo().publicSourceDir)));

            File f1 = new File(act.getBaseContext().getApplicationInfo().publicSourceDir);
            File f2 = new File(Environment.getExternalStorageDirectory().toString());
            f2.mkdirs();
            f2 = new File(f2.getPath() + "/" + act.getString(R.string.app_eng_name) + ".apk");
            f2.createNewFile();
            InputStream inp = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inp.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            inp.close();
            out.close();

            uris.add(Uri.fromFile(f2));
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            act.startActivity(Intent.createChooser(sendIntent, null));
        } catch (Exception e) {
            Log.v("this", e.getMessage());
        }
    }

    private void dialogRahgiri() {
        final EditText txtUrl = new EditText(act);
//        final EditText txtUrl = new EditText(new ContextThemeWrapper(act, R.style.edittexts), null, 0);
        txtUrl.setGravity(Gravity.RIGHT);
        txtUrl.setTypeface(IranSans);
        txtUrl.setRawInputType(InputType.TYPE_CLASS_NUMBER);
//        txtUrl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InputMethodManager inputMethodManager =
//                        (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInputFromWindow(
//                        txtUrl.getApplicationWindowToken(),
//                        InputMethodManager.SHOW_FORCED, 0);
//            }
//        });

        new AlertDialog.Builder(act, R.style.MyAlertDialogStyle2)
                .setMessage(("لطفا کد رهگیری سفارش را وارد کنید")).setView(txtUrl)
                .setCancelable(true)
                .setPositiveButton(("بررسی"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        View view = act.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                        String mabda = txtUrl.getText().toString();
                        if (mabda.length() > 0) {
                            Uri.Builder builder = new Uri.Builder()
                                    .appendQueryParameter("codePeygiri", mabda.toString());
                            String query = builder.build().getEncodedQuery();

                            new HtmlPost(new OnTaskFinished() {
                                @Override
                                public void onFeedRetrieved(String body) {
                                    Log.v("this", body);
                                    if (body.equals("errordade")) {
                                        MyToast.makeText(act.getApplicationContext(), "اشکالی پیش آمده است");
                                    } else {
                                        final Alert mAlert = new Alert(act, R.style.mydialog);
                                        mAlert.setIcon(android.R.drawable.ic_dialog_alert);
                                        mAlert.setMessage(body);
                                        mAlert.setPositveButton("بازگشت", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                mAlert.dismiss();
                                            }
                                        });
                                        mAlert.show();

                                    }
                                }
                            }, true, act, "", query).execute("http://hyperalborz.ir//peygiriSefaresh2.php?n=" + number + "&order=" + mabda);
                        }
                    }
                }).setNegativeButton(("لغو"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        }).show();

    }

    public boolean isDrawerOpen() {
        return Drawer.isDrawerOpen(Gravity.RIGHT);
    }

    public void closeDrawers() {
        Drawer.closeDrawers();
    }

    public void update() {
        mAdapter.notifyDataSetChanged();
    }
}
