
package nich.work.aequorea.author.model.entities;

import com.google.gson.annotations.SerializedName;

public class Magazine {

    @SerializedName("cover_url")
    private String mCoverUrl;
    @SerializedName("id")
    private Long mId;
    @SerializedName("listing_name")
    private String mListingName;
    @SerializedName("maga_all_number")
    private Long mMagaAllNumber;
    @SerializedName("maga_year_number")
    private Long mMagaYearNumber;
    @SerializedName("name")
    private String mName;
    @SerializedName("summary")
    private String mSummary;

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

    public String getListingName() {
        return mListingName;
    }

    public void setListingName(String listingName) {
        mListingName = listingName;
    }

    public Long getMagaAllNumber() {
        return mMagaAllNumber;
    }

    public void setMagaAllNumber(Long magaAllNumber) {
        mMagaAllNumber = magaAllNumber;
    }

    public Long getMagaYearNumber() {
        return mMagaYearNumber;
    }

    public void setMagaYearNumber(Long magaYearNumber) {
        mMagaYearNumber = magaYearNumber;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

}
