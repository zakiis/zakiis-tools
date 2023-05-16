package com.zakiis.file.domain.constants;

/**
 * You can use file phase to make file store in different storage for costing purpose
 * hot - represents the data that may read/write frequently, better store it in SSD disk.
 * warm - represents the data that may read not frequently, forbidden write, better store it in HDD disk.
 * cold - represents the data that may read rarely, forbidden write, better store it in cheapest solution. 
 */
public enum FilePhase {

	HOT,
	WARM,
	COLD,
	;
}
