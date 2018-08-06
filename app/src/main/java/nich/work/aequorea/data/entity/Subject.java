
package nich.work.aequorea.data.entity;

import com.google.gson.annotations.SerializedName;

public class Subject {

    @SerializedName("id")
    private Long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("price")
    private Long mPrice;
    @SerializedName("purchased")
    private Boolean mPurchased;
    @SerializedName("vip_price")
    private Long mVipPrice;

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

    public Long getPrice() {
        return mPrice;
    }

    public void setPrice(Long price) {
        mPrice = price;
    }

    public Boolean getPurchased() {
        return mPurchased;
    }

    public void setPurchased(Boolean purchased) {
        mPurchased = purchased;
    }

    public Long getVipPrice() {
        return mVipPrice;
    }

    public void setVipPrice(Long vipPrice) {
        mVipPrice = vipPrice;
    }

}
