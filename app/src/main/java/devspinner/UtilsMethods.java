package devspinner;


import android.content.Context;
import android.graphics.Typeface;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UtilsMethods {



    /**
     * Custom Comparator to sort the data with custom object types
     */
    public static  class MyComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            String w_o1=(String)o1;
            String w_o2=(String)o2;
            return  w_o1.compareTo(w_o2);
        }
    }

    /**
     * Sort the list based on type of arrayList
     * @param items
     */
    public static void sortCustomList(List<String> items,MyComparator myComparator){
        if(myComparator!=null){
            Collections.sort(items.subList(1,items.size()),myComparator);
        }
        else{
            Collections.sort(items.subList(1,items.size()),new MyComparator());
        }

    }

}
