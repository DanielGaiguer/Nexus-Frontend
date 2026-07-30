package com.main.nexus_frontend.model;

import java.util.List;

public class ProfessionalDirectoryPageDTO {
    private List<ProfessionalDirectoryItemDTO> content;
    private boolean hasMore;

    public ProfessionalDirectoryPageDTO() {}

    public List<ProfessionalDirectoryItemDTO> getContent() {
        return content;
    }

    public void setContent(List<ProfessionalDirectoryItemDTO> content) {
        this.content = content;
    }

    public boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
