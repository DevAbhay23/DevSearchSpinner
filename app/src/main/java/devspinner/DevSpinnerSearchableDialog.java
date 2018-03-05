package devspinner;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.devspinner.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DevSpinnerSearchableDialog extends DialogFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String ITEMS = "items";
    private static final String SEARCHABLE_ITEM = "searched_item";
    private SpinnerAdapterStateModel<Object> listAdapter;
    private ListView itemsListView;
    private SearchableItem mSearchableItem;
    private SearchView mSearchView;
    private TextView txtNoRecords;
    private int selectedPos = 0;

    public DevSpinnerSearchableDialog() {
    }

    /**
     *
     * @param items

     * @return
     */
    public static DevSpinnerSearchableDialog newInstance(List items) {
        DevSpinnerSearchableDialog searchDialogFrag = new DevSpinnerSearchableDialog();
        Bundle args = new Bundle();
        args.putSerializable(ITEMS, (Serializable) items);
        searchDialogFrag.setArguments(args);
        return searchDialogFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (savedInstanceState != null) {
            mSearchableItem = (SearchableItem) savedInstanceState.getSerializable(SEARCHABLE_ITEM);
        }
        View rootView = inflater.inflate(R.layout.spinner_search_dialog, null);
        setData(rootView);

        txtNoRecords=(TextView)rootView.findViewById(R.id.txtNoRecords);
        txtNoRecords.setVisibility(View.GONE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(rootView);

        final AlertDialog dialog = alertDialog.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog ;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SEARCHABLE_ITEM, mSearchableItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * To Get Current Selected Spinner Position from Event Bus.
     * @param mPos
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(SpinnerPosEvent mPos) {
        selectedPos = mPos.getSelectedPos();
    }

    /**
     *
     * @param searchableItem
     */
    public void setOnSearchableItemClickListener(SearchableItem searchableItem) {
        this.mSearchableItem = searchableItem;
    }

    /**
     * Function to set data to list in Dialog
     * @param rootView
     */
    private void setData(View rootView) {

        final Context mContext = getActivity();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) rootView.findViewById(R.id.search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.clearFocus();
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

        List items = (List) getArguments().getSerializable(ITEMS);
        itemsListView = (ListView) rootView.findViewById(R.id.listItems);
        listAdapter = new SpinnerAdapterStateModel<>(mContext, R.layout.spinner_root_textview, items);
        listAdapter.setSelectedPos(selectedPos);
        itemsListView.setAdapter(listAdapter);
        itemsListView.setSelection(selectedPos);
        itemsListView.setTextFilterEnabled(true);

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = listAdapter.getItem(position);
                int pos = getSpinnerIndex(obj, listAdapter.getClonedList());
                mSearchableItem.onSearchableItemClicked(obj, pos);
                getDialog().dismiss();
            }
        });
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            ((SpinnerAdapterStateModel) itemsListView.getAdapter()).getFilter().filter(null);
        } else {
            ((SpinnerAdapterStateModel) itemsListView.getAdapter()).getFilter().filter(s);
        }
        return true;
    }

    public interface SearchableItem<T> extends Serializable {
        void onSearchableItemClicked(T item, int position);
    }

    /**
     *
     * @param obj
     * @param items
     * @return
     */
    public int getSpinnerIndex(Object obj, List<Object> items) {
         /*if (obj instanceof StringWithTag) {
             for (int i = 0; i < items.size(); i++) {
                 StringWithTag itemObj = (StringWithTag) items.get(i);
                 if (((StringWithTag) obj).tag.equalsIgnoreCase(itemObj.tag)) {
                     return i;
                 }
             }
         }
         else if (obj instanceof String) {
             return items.indexOf(obj);
         }*/
        return items.indexOf(obj);
    }

    public class SpinnerAdapterStateModel<T> extends ArrayAdapter<T> implements Filterable {

        private final Context context;
        List<T> objList;
        List<T> clonedList;
        int selectedPos = 0;

        public SpinnerAdapterStateModel(Context context, int resource, List<T> items) {
            super(context, resource, android.R.id.text1, items);
            this.context = context;
            this.objList = items;
            this.clonedList = new ArrayList<>();
            this.clonedList.addAll(items);
        }

        @Override
        public int getCount() {
            if(!(objList.size()>0)){
                if(txtNoRecords!=null){
                    txtNoRecords.setVisibility(View.VISIBLE);
                    txtNoRecords.setText("No Records Found");
                }
            }
            else{
                if(txtNoRecords!=null) {
                    txtNoRecords.setVisibility(View.GONE);
                }
            }
            return objList.size();
        }

        @Override
        public T getItem(int position) {
            return objList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextVH holder = null;
            if (convertView == null) {
                holder = new TextVH();
                convertView = LayoutInflater.from(context).inflate(R.layout.spinner_root_textview, parent, false);
                holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
                holder.textView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                convertView.setTag(holder);
            } else {
                holder = (TextVH) convertView.getTag();
            }

            holder.textView.setText(objList.get(position).toString());
            if (position == selectedPos) {
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                holder.textView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCamarone));
            } else {
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.dark_grey));
                holder.textView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
            }
            return convertView;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint != null) {
                        List<T> FilteredArrayNames = new ArrayList<>();
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < clonedList.size(); i++) {
                            String dataNames = clonedList.get(i).toString();
                            if (dataNames.toLowerCase().contains(constraint.toString())) {
                                FilteredArrayNames.add(clonedList.get(i));
                            }
                        }
                        results.count = FilteredArrayNames.size();
                        results.values = FilteredArrayNames;
                    } else {
                        results.count = clonedList.size();
                        results.values = clonedList;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    objList.clear();
                    objList.addAll((List<T>) results.values);
                    notifyDataSetChanged();
                }
            };
        }

        public void setSelectedPos(int pos) {
            selectedPos = pos;
        }

        private class TextVH {
            TextView textView;
        }

        public List<T> getClonedList() {
            return clonedList;
        }

    }

}