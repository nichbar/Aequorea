
package nich.work.aequorea.main.model.article;

import com.google.gson.annotations.SerializedName;

public class Article {

    @SerializedName("data")
    private Data mData;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

}
