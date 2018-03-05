package devspinner;

public class SpinnerPosEvent {

    public int selectedPos=0;
    public SpinnerPosEvent(int mPos){
        this.selectedPos=mPos;
    }
    public int getSelectedPos(){
        return  selectedPos;
    }
}
