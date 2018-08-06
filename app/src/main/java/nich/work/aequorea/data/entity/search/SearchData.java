
package nich.work.aequorea.data.entity.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nich.work.aequorea.data.entity.Meta;

public class SearchData {

    @SerializedName("data")
    private List<SearchDatum> mData;
    @SerializedName("meta")
    private Meta mMeta;

    public List<SearchDatum> getData() {
        return mData;
    }

    public void setData(List<SearchDatum> data) {
        mData = data;
    }

    public Meta getMeta() {
        return mMeta;
    }

    public void setMeta(Meta meta) {
        mMeta = meta;
    }

}
