package com.zakiis.file.domain.dto;

import java.io.Serializable;

public interface PageRequest extends Serializable {

	/** start from 1 */
	Long getPageIndex();
	Integer getPageSize();
	void setPageIndex(Long pageIndex);
	void setPageSize(Integer pageSize);
}
