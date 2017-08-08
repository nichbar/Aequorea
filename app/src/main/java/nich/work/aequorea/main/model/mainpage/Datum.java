
package nich.work.aequorea.main.model.mainpage;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

    @SerializedName("anonymous_purchase_total_count")
    private Long mAnonymousPurchaseTotalCount;
    @SerializedName("anonymous_purchases")
    private List<AnonymousPurchase> mAnonymousPurchases;
    @SerializedName("anonymous_share_price")
    private String mAnonymousSharePrice;
    @SerializedName("article_type")
    private String mArticleType;
    @SerializedName("authors")
    private List<Author> mAuthors;
    @SerializedName("column")
    private Column mColumn;
    @SerializedName("comment_times")
    private Long mCommentTimes;
    @SerializedName("data")
    private List<Datum> mData;
    @SerializedName("display_time")
    private String mDisplayTime;
    @SerializedName("editor_choice_comments")
    private List<Object> mEditorChoiceComments;
    @SerializedName("is_favorited")
    private Boolean mIsFavorited;
    @SerializedName("is_like")
    private Boolean mIsLike;
    @SerializedName("like_times")
    private Long mLikeTimes;
    @SerializedName("magazine")
    private Magazine mMagazine;
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
    @SerializedName("type")
    private String mType;
    @SerializedName("visit_times")
    private Long mVisitTimes;
    @SerializedName("articles_count")
    private Long mArticlesCount;
    @SerializedName("cover_url")
    private String mCoverUrl;
    @SerializedName("description")
    private Object mDescription;
    @SerializedName("id")
    private Long mId;
    @SerializedName("illustration")
    private String mIllustration;
    @SerializedName("name")
    private String mName;
    @SerializedName("total_read_times")
    private Long mTotalReadTimes;
    @SerializedName("total_visit_times")
    private Long mTotalVisitTimes;

    public Long getArticlesCount() {
        return mArticlesCount;
    }

    public void setArticlesCount(Long articlesCount) {
        mArticlesCount = articlesCount;
    }


    public Object getDescription() {
        return mDescription;
    }

    public void setDescription(Object description) {
        mDescription = description;
    }

    public String getIllustration() {
        return mIllustration;
    }

    public void setIllustration(String illustration) {
        mIllustration = illustration;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getTotalReadTimes() {
        return mTotalReadTimes;
    }

    public void setTotalReadTimes(Long totalReadTimes) {
        mTotalReadTimes = totalReadTimes;
    }

    public Long getTotalVisitTimes() {
        return mTotalVisitTimes;
    }

    public void setTotalVisitTimes(Long totalVisitTimes) {
        mTotalVisitTimes = totalVisitTimes;
    }


    public Long getAnonymousPurchaseTotalCount() {
        return mAnonymousPurchaseTotalCount;
    }

    public void setAnonymousPurchaseTotalCount(Long anonymousPurchaseTotalCount) {
        mAnonymousPurchaseTotalCount = anonymousPurchaseTotalCount;
    }

    public List<AnonymousPurchase> getAnonymousPurchases() {
        return mAnonymousPurchases;
    }

    public void setAnonymousPurchases(List<AnonymousPurchase> anonymousPurchases) {
        mAnonymousPurchases = anonymousPurchases;
    }

    public String getAnonymousSharePrice() {
        return mAnonymousSharePrice;
    }

    public void setAnonymousSharePrice(String anonymousSharePrice) {
        mAnonymousSharePrice = anonymousSharePrice;
    }

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

    public Column getColumn() {
        return mColumn;
    }

    public void setColumn(Column column) {
        mColumn = column;
    }

    public Long getCommentTimes() {
        return mCommentTimes;
    }

    public void setCommentTimes(Long commentTimes) {
        mCommentTimes = commentTimes;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
    }

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
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

    public Magazine getMagazine() {
        return mMagazine;
    }

    public void setMagazine(Magazine magazine) {
        mMagazine = magazine;
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

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Long getVisitTimes() {
        return mVisitTimes;
    }

    public void setVisitTimes(Long visitTimes) {
        mVisitTimes = visitTimes;
    }

}
