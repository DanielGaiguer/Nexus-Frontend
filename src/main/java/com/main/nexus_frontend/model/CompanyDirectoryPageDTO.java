package com.main.nexus_frontend.model;

import java.util.List;

public class CompanyDirectoryPageDTO {
    private List<CompanyDirectoryItemDTO> content;
    private boolean hasMore;

    public CompanyDirectoryPageDTO() {}

    public List<CompanyDirectoryItemDTO> getContent() {
        return content;
    }

    public void setContent(List<CompanyDirectoryItemDTO> content) {
        this.content = content;
    }

    public boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
