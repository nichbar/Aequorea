
package nich.work.aequorea.main.model.article;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("article_type")
    private String mArticleType;
    @SerializedName("authors")
    private List<Author> mAuthors;
    @SerializedName("comment_times")
    private Long mCommentTimes;
    @SerializedName("content")
    private String mContent;
    @SerializedName("cover_url")
    private String mCoverUrl;
    @SerializedName("display_time")
    private String mDisplayTime;
    @SerializedName("editor_choice_comments")
    private List<Object> mEditorChoiceComments;
    @SerializedName("id")
    private Long mId;
    @SerializedName("is_favorited")
    private Boolean mIsFavorited;
    @SerializedName("is_like")
    private Boolean mIsLike;
    @SerializedName("like_times")
    private Long mLikeTimes;
    @SerializedName("probation")
    private Boolean mProbation;
    @SerializedName("read_time")
    private Long mReadTime;
    @SerializedName("share_url")
    private String mShareUrl;
    @SerializedName("share_visit_limit")
    private Long mShareVisitLimit;
    @SerializedName("share_visit_times")
    private Long mShareVisitTimes;
    @SerializedName("summary")
    private String mSummary;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("topics")
    private List<Topic> mTopics;
    @SerializedName("visit_times")
    private Long mVisitTimes;

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

    public Long getCommentTimes() {
        return mCommentTimes;
    }

    public void setCommentTimes(Long commentTimes) {
        mCommentTimes = commentTimes;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
    }

    public String getDisplayTime() {
        return mDisplayTime;
    }

    public void setDisplayTime(String displayTime) {
        mDisplayTime = displayTime;
    }

    public List<Object> getEditorChoiceComments() {
        return mEditorChoiceComments;
    }

    public void setEditorChoiceComments(List<Object> editorChoiceComments) {
        mEditorChoiceComments = editorChoiceComments;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public Boolean getIsFavorited() {
        return mIsFavorited;
    }

    public void setIsFavorited(Boolean isFavorited) {
        mIsFavorited = isFavorited;
    }

    public Boolean getIsLike() {
        return mIsLike;
    }

    public void setIsLike(Boolean isLike) {
        mIsLike = isLike;
    }

    public Long getLikeTimes() {
        return mLikeTimes;
    }

    public void setLikeTimes(Long likeTimes) {
        mLikeTimes = likeTimes;
    }

    public Boolean getProbation() {
        return mProbation;
    }

    public void setProbation(Boolean probation) {
        mProbation = probation;
    }

    public Long getReadTime() {
        return mReadTime;
    }

    public void setReadTime(Long readTime) {
        mReadTime = readTime;
    }

    public String getShareUrl() {
        return mShareUrl;
    }

    public void setShareUrl(String shareUrl) {
        mShareUrl = shareUrl;
    }

    public Long getShareVisitLimit() {
        return mShareVisitLimit;
    }

    public void setShareVisitLimit(Long shareVisitLimit) {
        mShareVisitLimit = shareVisitLimit;
    }

    public Long getShareVisitTimes() {
        return mShareVisitTimes;
    }

    public void setShareVisitTimes(Long shareVisitTimes) {
        mShareVisitTimes = shareVisitTimes;
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

    public List<Topic> getTopics() {
        return mTopics;
    }

    public void setTopics(List<Topic> topics) {
        mTopics = topics;
    }

    public Long getVisitTimes() {
        return mVisitTimes;
    }

    public void setVisitTimes(Long visitTimes) {
        mVisitTimes = visitTimes;
    }

}