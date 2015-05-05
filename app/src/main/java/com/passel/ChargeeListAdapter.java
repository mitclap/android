package com.passel;

/**
 * Created by Carlos Henriquez on 5/4/2015.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ChargeeListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final View.OnClickListener listener;

    public ChargeeListAdapter(Context context, String[] values, View.OnClickListener listener) {
        super(context, R.layout.charge_row_layout, values);
        this.context = context;
        this.values = values;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.charge_row_layout, parent, false);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        TextView descView = (TextView) rowView.findViewById(R.id.description);
        Button chargeButton = (Button) rowView.findViewById(R.id.charge_button);
        //nameView.setText(values[position]);
        // change the icon for Windows and iPhone
        /**String s = values[position];
        if (s.startsWith("iPhone")) {
            imageView.setImageResource(R.drawable.no);
        } else {
            imageView.setImageResource(R.drawable.ok);
        }**/
        //TODO: Make not canned
        nameView.setText(values[0]); //Settext to CANNED Ben Bitdiddle
        descView.setText(values[1]); //settext to CANNED 38 mins late

        chargeButton.setOnClickListener(this.listener);

        return rowView;
    }
}
/** For reference//
 * public void myClickHandler(View v)
    {

        //reset all the listView items background colours

        //before we set the clicked one..


        ListView lvItems = getListView();
        for (int i=0; i < lvItems.getChildCount(); i++)
        {
            lvItems.getChildAt(i).setBackgroundColor(Color.BLUE);
        }


        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        Button btnChild = (Button)vwParentRow.getChildAt(1);
        btnChild.setText(child.getText());
        btnChild.setText("I've been clicked!");

        int c = Color.CYAN;

        vwParentRow.setBackgroundColor(c);
        vwParentRow.refreshDrawableState();
    }
**/