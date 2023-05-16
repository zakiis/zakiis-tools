package com.zakiis.file.portal.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.zakiis.file.domain.dto.ResponseDTO;
import com.zakiis.file.model.Channel;
import com.zakiis.file.portal.domain.constants.FilePortalConstants;
import com.zakiis.file.portal.domain.constants.FunctionCode;
import com.zakiis.file.portal.domain.dto.channel.ChannelAddDTO;
import com.zakiis.file.portal.domain.dto.channel.ChannelQueryDTO;
import com.zakiis.file.portal.domain.dto.channel.ChannelQueryResultDTO;
import com.zakiis.file.portal.domain.dto.channel.ChannelUpdateDTO;
import com.zakiis.file.portal.service.ChannelService;
import com.zakiis.security.annotation.Permission;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/file-portal/channel")
public class ChannelController {
	
	@Autowired
	ChannelService channelService;

	@Permission(functions = {FunctionCode.CHANNEL_MANAGE_VALUE})
	@PostMapping("/add/{name}")
	public Mono<ResponseDTO<Channel>> addChannel(@PathVariable String name, @RequestBody ChannelAddDTO channelAddDTO, ServerWebExchange exchange) {
		channelAddDTO.setName(name);
		return channelService.addChannel(channelAddDTO, exchange.getAttribute(FilePortalConstants.CONTEXT_USERNAME))
				.map(ResponseDTO::ok);
	}
	
	@Permission(functions = {FunctionCode.CHANNEL_MANAGE_VALUE})
	@PostMapping("/update/{ak}")
	public Mono<ResponseDTO<Long>> addChannel(@PathVariable String ak, @RequestBody ChannelUpdateDTO channelUpdateDTO, ServerWebExchange exchange) {
		channelUpdateDTO.setAk(ak);
		return channelService.updateChannel(channelUpdateDTO, exchange.getAttribute(FilePortalConstants.CONTEXT_USERNAME))
				.map(ResponseDTO::ok);
	}
	
	@Permission(functions = {FunctionCode.CHANNEL_MANAGE_VALUE})
	@PostMapping("/delete/{ak}")
	public Mono<ResponseDTO<Long>> deleteChannel(@PathVariable String ak) {
		return channelService.deleteChannel(ak)
				.map(ResponseDTO::ok);
	}
	
	@Permission(functions = {FunctionCode.CHANNEL_QUERY_VALUE})
	@PostMapping("/query")
	public Mono<ResponseDTO<ChannelQueryResultDTO>> queryChannel(@RequestBody ChannelQueryDTO channelQueryDTO) {
		return channelService.queryChannel(channelQueryDTO)
				.map(ResponseDTO::ok);
	}
}
