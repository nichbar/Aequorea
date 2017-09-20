
package nich.work.aequorea.model.entity.search;

import com.google.gson.annotations.SerializedName;

public class SearchDatum {

    @SerializedName("content")
    private Content mContent;
    @SerializedName("searchable_id")
    private Long mSearchableId;
    @SerializedName("searchable_type")
    private String mSearchableType;

    public Content getContent() {
        return mContent;
    }

    public void setContent(Content content) {
        mContent = content;
    }

    public Long getSearchableId() {
        return mSearchableId;
    }

    public void setSearchableId(Long searchableId) {
        mSearchableId = searchableId;
    }

    public String getSearchableType() {
        return mSearchableType;
    }

    public void setSearchableType(String searchableType) {
        mSearchableType = searchableType;
    }

}
