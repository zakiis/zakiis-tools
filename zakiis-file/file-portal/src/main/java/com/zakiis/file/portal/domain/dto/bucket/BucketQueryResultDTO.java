package com.zakiis.file.portal.domain.dto.bucket;

import java.util.List;

import com.zakiis.file.domain.dto.PageResponse;
import com.zakiis.file.model.Bucket;

public class BucketQueryResultDTO implements PageResponse {

	private static final long serialVersionUID = 3440930078115920214L;

	private List<Bucket> buckets;
	Long pageIndex;
	Integer pageSize;
	Long count;
	
	public Long getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Long pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<Bucket> getBuckets() {
		return buckets;
	}
	public void setBuckets(List<Bucket> buckets) {
		this.buckets = buckets;
	}
	
}
