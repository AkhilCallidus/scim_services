package com.calliduscloud.scas.scim_services.response;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Returns an array of SCIM resources into a Query Resource.
 */
@Component
public class GroupListResponse {
    private List<Map> list;
    private int count;
    private Long totalResults;
    private int startIndex;

    public GroupListResponse() {
        this.list = new ArrayList<>();
        this.count = 0;
        this.totalResults = Long.valueOf(0);
        this.startIndex = 1;
    }

    public GroupListResponse(List<Map> list, Optional<Integer> startIndex,
                             Optional<Integer> count, Optional<Long> totalResults) {
        this.list = list;
        this.startIndex = startIndex.orElse(1);
        this.count = count.orElse(0);
        this.totalResults = totalResults.orElse(Long.valueOf(0));

    }

    public List<Map> getList() {
        return list;
    }

    public void setList(List<Map> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalResults() {
        return Math.toIntExact(totalResults);
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = Long.valueOf(totalResults);
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
}