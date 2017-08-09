
package nich.work.aequorea.author.model.entities;

import com.google.gson.annotations.SerializedName;

public class Topic {

    @SerializedName("articles_count")
    private Long mArticlesCount;
    @SerializedName("id")
    private Long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("topic_type")
    private String mTopicType;

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

    public String getTopicType() {
        return mTopicType;
    }

    public void setTopicType(String topicType) {
        mTopicType = topicType;
    }

}
