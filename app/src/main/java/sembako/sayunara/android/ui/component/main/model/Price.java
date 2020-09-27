package sembako.sayunara.android.ui.component.main.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Price implements Parcelable {

    public String lokasi;
    public String name;
    public String HMin1;
    public String HMin2;

    public ArrayList<Item> komoditas = new ArrayList<Item>();

    public Price() {
    }

    public Price(Parcel in) {
        lokasi = in.readString();
        name 	= in.readString();
        HMin1	= in.readString();
        HMin2	= in.readString();

        in.readTypedList(komoditas, Item.CREATOR);
    }

    public static final Creator<Price> CREATOR = new Creator<Price>() {

        @Override
        public Price createFromParcel(Parcel in) {
            // TODO Auto-generated method stub
            return new Price(in);
        }

        @Override
        public Price[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Price[size];
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
        out.writeString(lokasi);
        out.writeString(name);
        out.writeString(HMin1);
        out.writeString(HMin2);

        out.writeTypedList(komoditas);
    }

}
