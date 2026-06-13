package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.response.NewsResponse;
import com.example.Qlyhocsinh.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/education")
    public ApiResponse<List<NewsResponse>> getEducationNews() {
        return ApiResponse.<List<NewsResponse>>builder()
                .result(newsService.getEducationNews())
                .build();
    }
}