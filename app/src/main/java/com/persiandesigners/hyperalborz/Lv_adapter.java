package com.persiandesigners.hyperalborz;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Created by navid on 9/18/2016.
 */
public class Lv_adapter extends ArrayAdapter<String> {
    Context context ;
    String[] titlearray;
    Typeface typeface;

    public Lv_adapter(Context c, String[] titles){
        super(c, R.layout.ostan_shahrestan_ln,R.id.title,titles);
        this.context=c;
        this.titlearray=titles;
        typeface= Typeface.createFromAsset(c.getAssets(), "IRAN Sans Bold.ttf");
   }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row =inflater.inflate(R.layout.ostan_shahrestan_row,parent, false) ; //parent mishe haman listview  - yek object java hast ke bayad dar view zakhire behse

        TextView title=(TextView) row.findViewById(R.id.onvan);
        title.setTypeface(typeface);
        title.setText(titlearray[position]);

        return row ;
    }

}

