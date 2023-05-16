package com.zakiis.file.core.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class FileControllerTest {

	@Test
	public void testUpload() {
		Path uploadFilePath = Paths.get("D:/soft/archive/eclipse-jee-2021-09-R-win32-x86_64.zip");
		Flux<DataBuffer> dataFlux = DataBufferUtils.read(uploadFilePath, new DefaultDataBufferFactory(), 1024 * 50, StandardOpenOption.READ);
		Mono<String> responseMono = WebClient.builder().build()
			.post()
			.uri("http://localhost:8080/v1/file-core/cimb/00132-12321-1232-12322")
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(BodyInserters.fromDataBuffers(dataFlux))
			.retrieve()
			.bodyToMono(String.class);
		System.out.println(responseMono.block());
	}
	
	public void testUpload2() {
		
	}
}
