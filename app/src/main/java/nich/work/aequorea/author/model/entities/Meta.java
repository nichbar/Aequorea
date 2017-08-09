
package nich.work.aequorea.author.model.entities;

import com.google.gson.annotations.SerializedName;

public class Meta {

    @SerializedName("current_page")
    private Long mCurrentPage;
    @SerializedName("next_page")
    private Long mNextPage;
    @SerializedName("total_count")
    private Long mTotalCount;
    @SerializedName("total_pages")
    private Long mTotalPages;

    public Long getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(Long currentPage) {
        mCurrentPage = currentPage;
    }

    public Long getNextPage() {
        return mNextPage;
    }

    public void setNextPage(Long nextPage) {
        mNextPage = nextPage;
    }

    public Long getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(Long totalCount) {
        mTotalCount = totalCount;
    }

    public Long getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Long totalPages) {
        mTotalPages = totalPages;
    }

}
