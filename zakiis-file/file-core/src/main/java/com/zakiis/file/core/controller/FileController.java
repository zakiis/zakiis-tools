package com.zakiis.file.core.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zakiis.file.core.exception.ErrorEnum;
import com.zakiis.file.core.service.FileService;
import com.zakiis.file.domain.dto.ResponseDTO;
import com.zakiis.file.exception.ServiceException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/file-core/file")
public class FileController {
	
	@Autowired
	FileService fileService;

	@PostMapping("/upload/{bucket}/{fileKey}")
	public Mono<ResponseDTO<Object>> uploadFile(@PathVariable String bucket, @PathVariable String fileKey, ServerHttpRequest request) throws IOException {
		return fileService.saveFile(request.getBody().switchIfEmpty(Mono.error(new ServiceException(ErrorEnum.FILE_EMPTY))), bucket, fileKey)
			.then(Mono.just(ResponseDTO.ok()));
	}
	
	@GetMapping("/download/{bucket}/{fileKey}")
	public Mono<Void> downloadFile(@PathVariable String bucket, @PathVariable String fileKey, ServerHttpResponse response) {
		Flux<DataBuffer> fileFlux = fileService.downloadFile(bucket, fileKey, response.bufferFactory());
		return response.writeWith(fileFlux);
	}
}
