package sembako.sayunara.android.ui.component.main.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

    public String name;
    public String HMin1;
    public String HMin2;


    public Item() {}

    public Item(Parcel in) {
        name 	= in.readString();
        HMin1	= in.readString();
        HMin2	= in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {

        @Override
        public Item[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Item[size];
        }

        @Override
        public Item createFromParcel(Parcel in) {
            // TODO Auto-generated method stub
            return new Item(in);
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO Auto-generated method stub
        out.writeString(name);
        out.writeString(HMin1);
        out.writeString(HMin2);
    }

}
