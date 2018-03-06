# DevSearchSpinner
DevSearchSpinner is a dialog spinner with the search feature in multiple Indian regional languages<br>
Supports data from custom models.<br>
Sort Data according to type of data set in adapter.<br>

Android Versions Support- <b>2.3-Latest</b><br><br>
![Screenshot](screen_one.png)
![Screenshot](screen_two.png)
![Screenshot](screen_three.png)
![Screenshot](screen_four.png)
# Usage
 <devspinner.DevSpinnerControl
        android:id="@+id/spinnerState"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/spinner_custom_background"
           />

 In your java file add these lines of code :
  devspinner.DevCustomSpinnerAdap<CustomModel> adapter = new devspinner.DevCustomSpinnerAdap(mContext, R.layout.spinner_view, mList,comparator);
  spinnerState.setAdapter(adapter);

 For Custom TypeFace:
 adapter.setFontToSpinner(customTypeFace)

 For myComparator:
  // Override your compare method on basis of the Custom Model chosen for Spinner Data.
  // Default is String Type Comparison.

  devspinner.UtilsMethods.MyComparator comparator=new devspinner.UtilsMethods.MyComparator(){
     @Override
     public int compare(Object o1, Object o2) {
         CustomModel w_o1=(CustomModel)o1;
         CustomModel w_o2=(CustomModel)o2;
         return  w_o1.tag.compareTo(w_o2.tag);
     }
  };


