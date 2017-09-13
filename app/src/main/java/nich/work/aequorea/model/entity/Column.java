
package nich.work.aequorea.model.entity;

import com.google.gson.annotations.SerializedName;

public class Column {

    @SerializedName("articles_count")
    private Long mArticlesCount;
    @SerializedName("id")
    private Long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("parent_column")
    private ParentColumn mParentColumn;
    @SerializedName("weight")
    private Long mWeight;

    public Long getArticlesCount() {
        return mArticlesCount;
    }

    public void setArticlesCount(Long articlesCount) {
        mArticlesCount = articlesCount;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ParentColumn getParentColumn() {
        return mParentColumn;
    }

    public void setParentColumn(ParentColumn parentColumn) {
        mParentColumn = parentColumn;
    }

    public Long getWeight() {
        return mWeight;
    }

    public void setWeight(Long weight) {
        mWeight = weight;
    }

}
