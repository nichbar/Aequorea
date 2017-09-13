
package nich.work.aequorea.model.entity;

import com.google.gson.annotations.SerializedName;

public class AnonymousPurchase {

    @SerializedName("anonymous_ID")
    private String mAnonymousID;
    @SerializedName("avatar")
    private String mAvatar;

    public String getAnonymousID() {
        return mAnonymousID;
    }

    public void setAnonymousID(String anonymousID) {
        mAnonymousID = anonymousID;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

}
