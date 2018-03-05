package devspinner;


import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SpinnerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DevSpinnerControl extends AppCompatSpinner implements View.OnTouchListener, DevSpinnerSearchableDialog.SearchableItem {

    private Context mContext;
    private List items;
    private DevSpinnerSearchableDialog searchSpinnerDialog;
    DevCustomSpinnerAdap<String> arrayAdapter;
    private static final long MIN_DELAY_MS = 500;
    private long mPreviousTouchTime;

    public DevSpinnerControl(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DevSpinnerControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public DevSpinnerControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        items = new ArrayList();
        searchSpinnerDialog = DevSpinnerSearchableDialog.newInstance(items);
        searchSpinnerDialog.setOnSearchableItemClickListener(this);
        setOnTouchListener(this);
    }

    /**
     * Code will execute when user taps on Spinner.
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int pos=this.getSelectedItemPosition();
        if (event.getAction() == MotionEvent.ACTION_UP){
            long lastTouchTime = mPreviousTouchTime;
            long currentTime = System.currentTimeMillis();
            mPreviousTouchTime = currentTime;
            if (currentTime - lastTouchTime < MIN_DELAY_MS) {
                return true;                                     // Do not show dialog for that case- to prevent IllegalStateException //
            } else{
                simulateTouch(pos);
            }
        }
        return true;
    }

    /**
     * function to sort data set and show spinner dialog
     * @param pos
     */
    public void simulateTouch(int pos){
        EventBus.getDefault().postSticky(new SpinnerPosEvent(pos));
        if (null != arrayAdapter) {
            items.clear();
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                items.add(arrayAdapter.getItem(i));
            }
            AppCompatActivity act=checkForActivity(mContext);
            if (!searchSpinnerDialog.isVisible() && !searchSpinnerDialog.isAdded() && act!=null) {
                searchSpinnerDialog.show(act.getSupportFragmentManager(), "TAG");
            }
        }
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        arrayAdapter = (DevCustomSpinnerAdap<String>) adapter;
        super.setAdapter(arrayAdapter);
    }

    @Override
    public void onSearchableItemClicked(Object item, int position) {
        if(position==-1){
            setSelection(0);
        }
        else{
            setSelection(position);
        }
    }

    private AppCompatActivity checkForActivity(Context mContext) {
        if (mContext == null)
            return null;
        else if (mContext instanceof AppCompatActivity)
            return (AppCompatActivity) mContext;
        else if (mContext instanceof ContextWrapper)
            return checkForActivity(((ContextWrapper) mContext).getBaseContext());
        return null;
    }

    @Override
    public int getSelectedItemPosition() {
        return super.getSelectedItemPosition();
    }

    @Override
    public Object getSelectedItem() {
        return super.getSelectedItem();
    }



}