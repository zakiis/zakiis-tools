package com.zakiis.file.portal.controller;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.zakiis.file.domain.dto.ResponseDTO;
import com.zakiis.file.exception.ServiceException;
import com.zakiis.file.model.Bucket;
import com.zakiis.file.portal.domain.constants.FilePortalConstants;
import com.zakiis.file.portal.domain.constants.FunctionCode;
import com.zakiis.file.portal.domain.dto.bucket.BucketAddDTO;
import com.zakiis.file.portal.domain.dto.bucket.BucketQueryDTO;
import com.zakiis.file.portal.domain.dto.bucket.BucketQueryResultDTO;
import com.zakiis.file.portal.domain.dto.bucket.BucketUpdateDTO;
import com.zakiis.file.portal.exception.ErrorEnum;
import com.zakiis.file.portal.service.BucketService;
import com.zakiis.file.portal.service.tool.ValidationTool;
import com.zakiis.security.annotation.Permission;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/file-portal/bucket")
public class BucketController {
	
	@Autowired
	BucketService bucketService;
	Pattern bucketNamePattern = Pattern.compile("^[_a-zA-Z]+\\d*$");

	@Permission(functions = {FunctionCode.BUCKET_MANAGE_VALUE})
	@PostMapping("/add/{name}")
	public Mono<ResponseDTO<Bucket>> addBucket(@PathVariable String name, @RequestBody BucketAddDTO bucketAddDTO, ServerWebExchange exchange) {
		if (!bucketNamePattern.matcher(name).find()) {
			throw new ServiceException(ErrorEnum.BUCKET_NAME_INVALID);
		}
		bucketAddDTO.setName(name);
		ValidationTool.notNull(bucketAddDTO.getAccessMode(), "Bucket access mode");
		return bucketService.addBucket(bucketAddDTO, exchange.getAttribute(FilePortalConstants.CONTEXT_USERNAME))
				.map(ResponseDTO::ok);
	}
	
	@Permission(functions = {FunctionCode.BUCKET_MANAGE_VALUE})
	@PostMapping("/update/{name}")
	public Mono<ResponseDTO<Long>> updateBucket(@PathVariable String name, @RequestBody BucketUpdateDTO bucketUpdateDTO, ServerWebExchange exchange) {
		bucketUpdateDTO.setName(name);
		return bucketService.updateBucket(bucketUpdateDTO, exchange.getAttribute(FilePortalConstants.CONTEXT_USERNAME))
				.map(ResponseDTO::ok);
	}
	
	@Permission(functions = {FunctionCode.BUCKET_MANAGE_VALUE})
	@PostMapping("/delete/{name}")
	public Mono<ResponseDTO<Long>> deleteBucket(@PathVariable String name) {
		return bucketService.deleteBucket(name)
				.map(ResponseDTO::ok);
	}
	
	@Permission(functions = {FunctionCode.BUCKET_QUERY_VALUE})
	@PostMapping("/query")
	public Mono<ResponseDTO<BucketQueryResultDTO>> queryBucket(@RequestBody BucketQueryDTO bucketQueryDTO) {
		return bucketService.queryBucket(bucketQueryDTO)
				.map(ResponseDTO::ok);
	}
}
