
package nich.work.aequorea.model.mainpage;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Page {

    @SerializedName("data")
    private List<Datum> mData;

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
    }

}
