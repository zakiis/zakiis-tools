package com.zakiis.file.portal.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.zakiis.file.domain.dto.ResponseDTO;
import com.zakiis.file.portal.domain.constants.FunctionCode;
import com.zakiis.file.portal.service.BucketService;
import com.zakiis.file.portal.service.FileService;
import com.zakiis.file.portal.util.AuthorizeUtil;
import com.zakiis.security.annotation.Permission;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/file-portal/file")
public class FileController {
	
	@Autowired
	BucketService bucketService;
	@Autowired
	FileService fileService;

	@Permission(functions = {FunctionCode.FILE_WRITE_VALUE})
	@PostMapping("/upload/{bucket}/{fileKey}")
	public Mono<ResponseDTO<Object>> uploadFile(@PathVariable String bucket, @PathVariable String fileKey, ServerHttpRequest request) throws IOException {
		String ak = AuthorizeUtil.getAk(request);
		bucketService.checkChannelCanWrite(ak, bucket);
		return fileService.uploadFile(bucket, fileKey, request.getBody());
	}
	
	@Permission(functions = {FunctionCode.FILE_READ_VALUE})
	@GetMapping("/download/{bucket}/{fileKey}")
	public Mono<Void> downloadFile(@PathVariable String bucket, @PathVariable String fileKey, ServerWebExchange exchange) {
		String ak = AuthorizeUtil.getAk(exchange.getRequest());
		bucketService.checkChannelCanRead(ak, bucket);
		Flux<DataBuffer> fileDataBuffer = fileService.downloadFile(bucket, fileKey);
		return exchange.getResponse().writeWith(fileDataBuffer);
	}
}
