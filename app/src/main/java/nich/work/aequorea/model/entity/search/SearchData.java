
package nich.work.aequorea.model.entity.search;

import java.util.List;
import com.google.gson.annotations.SerializedName;

import nich.work.aequorea.model.entity.Meta;

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
