package com.zakiis.file.portal.domain.dto.channel;

import java.util.List;

import com.zakiis.file.domain.dto.PageResponse;
import com.zakiis.file.model.Channel;

public class ChannelQueryResultDTO implements PageResponse {

	private static final long serialVersionUID = 4092936661602654317L;
	
	Long pageIndex;
	private Integer pageSize;
	private Long count;
	List<Channel> channels;
	
	public Long getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Long pageIndex) {
		this.pageIndex = pageIndex;
	}
	public List<Channel> getChannels() {
		return channels;
	}
	public void setChannels(List<Channel> channels) {
		this.channels = channels;
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
}
