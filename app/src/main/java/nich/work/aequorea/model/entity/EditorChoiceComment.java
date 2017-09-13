
package nich.work.aequorea.model.entity;

import com.google.gson.annotations.SerializedName;

public class EditorChoiceComment {

    @SerializedName("content")
    private String mContent;
    @SerializedName("display_time")
    private String mDisplayTime;
    @SerializedName("id")
    private Long mId;
    @SerializedName("is_like")
    private Boolean mIsLike;
    @SerializedName("like_times")
    private Long mLikeTimes;
    @SerializedName("user")
    private User mUser;

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getDisplayTime() {
        return mDisplayTime;
    }

    public void setDisplayTime(String displayTime) {
        mDisplayTime = displayTime;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
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

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

}
