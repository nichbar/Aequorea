
package nich.work.aequorea.model.entity.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nich.work.aequorea.model.entity.Author;

public class Content {

    @SerializedName("article_type")
    private String mArticleType;
    @SerializedName("authors")
    private List<Author> mAuthors;
    @SerializedName("cover_url")
    private String mCoverUrl;
    @SerializedName("id")
    private Long mId;
    @SerializedName("publish_at")
    private String mPublishAt;
    @SerializedName("summary")
    private String mSummary;
    @SerializedName("title")
    private String mTitle;

    public String getArticleType() {
        return mArticleType;
    }

    public void setArticleType(String articleType) {
        mArticleType = articleType;
    }

    public List<Author> getAuthors() {
        return mAuthors;
    }

    public void setAuthors(List<Author> authors) {
        mAuthors = authors;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getPublishAt() {
        return mPublishAt;
    }

    public void setPublishAt(String publishAt) {
        mPublishAt = publishAt;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
