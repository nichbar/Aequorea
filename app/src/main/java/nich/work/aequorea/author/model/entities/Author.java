
package nich.work.aequorea.author.model.entities;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Author {

    @SerializedName("avatar")
    private String mAvatar;
    @SerializedName("data")
    private List<Datum> mData;
    @SerializedName("id")
    private Long mId;
    @SerializedName("introduction")
    private String mIntroduction;
    @SerializedName("meta")
    private Meta mMeta;
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

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
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

    public Meta getMeta() {
        return mMeta;
    }

    public void setMeta(Meta meta) {
        mMeta = meta;
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
