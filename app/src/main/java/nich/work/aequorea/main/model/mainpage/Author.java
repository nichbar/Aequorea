
package nich.work.aequorea.main.model.mainpage;

import com.google.gson.annotations.SerializedName;

public class Author {

    @SerializedName("avatar")
    private String mAvatar;
    @SerializedName("id")
    private Long mId;
    @SerializedName("introduction")
    private String mIntroduction;
    @SerializedName("name")
    private String mName;
    @SerializedName("role")
    private Object mRole;

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getIntroduction() {
        return mIntroduction;
    }

    public void setIntroduction(String introduction) {
        mIntroduction = introduction;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Object getRole() {
        return mRole;
    }

    public void setRole(Object role) {
        mRole = role;
    }

}
