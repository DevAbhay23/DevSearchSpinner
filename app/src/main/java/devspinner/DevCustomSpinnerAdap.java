package devspinner;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.devspinner.R;

import java.util.List;

public class DevCustomSpinnerAdap<T> extends ArrayAdapter<String> {

    Context mContext;
    TextView openSpinnerTextView,closedSpinnerTextView;
       /**
     * @param context
     * @param resource
     * @param items
     */
    public DevCustomSpinnerAdap(Context context, int resource, List<String> items,UtilsMethods.MyComparator myComparator) {
        super(context, resource, android.R.id.text1, items);
        this.mContext = context;
        if(items.size()>1){
            UtilsMethods.sortCustomList(items,myComparator);
        }
    }

    /**
     * Affects default (closed) state of the spinner
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        closedSpinnerTextView = (TextView) super.getView(position, convertView, parent);
        closedSpinnerTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            closedSpinnerTextView.setAllCaps(true);
        }
        return closedSpinnerTextView;
    }

    /**
     * Affects opened state of the spinner
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.spinner_dialog_textview, null);
        }
        View view = super.getDropDownView(position, convertView, parent);
        openSpinnerTextView = (TextView) view.findViewById(android.R.id.text1);
        openSpinnerTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_activated}, // pressed
                new int[]{android.R.attr.state_focused}, // focused
                new int[]{}
        };
        int[] colors = new int[]{
                ContextCompat.getColor(mContext, R.color.colorWhite),
                ContextCompat.getColor(mContext, R.color.dark_grey),
                ContextCompat.getColor(mContext, R.color.dark_grey)
        };
        ColorStateList list = new ColorStateList(states, colors);
        openSpinnerTextView.setTextColor(list);
        return view;
    }

    /**
     * setting custom typeface to spinner dialog values.
     * @param tf
     */
    public void setTypeFaceToSpinnerOpen(Typeface tf){
        if(openSpinnerTextView!=null){
            openSpinnerTextView.setTypeface(tf);
        }

    }


    /**
     *setting custom typeface to spinner closed textview.
     * @param tf
     */
    public void setTypeFaceToSpinnerClosed(Typeface tf){
        if(closedSpinnerTextView!=null){
            closedSpinnerTextView.setTypeface(tf);
        }
    }

}