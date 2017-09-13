
package nich.work.aequorea.model.entity;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private Long mId;
    @SerializedName("nickname")
    private String mNickname;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

}
