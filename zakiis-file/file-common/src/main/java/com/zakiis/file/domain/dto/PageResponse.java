package com.zakiis.file.domain.dto;

import java.io.Serializable;

public interface PageResponse extends Serializable {

	/** start from 1 */
	Long getPageIndex();
	Integer getPageSize();
	Long getCount();
	default Long getPageCount() {
		return (getCount() + getPageSize() - 1) / getPageSize();
	}
}
