package com.leo.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.commoncore.response.R;
import com.leo.productservice.controller.SearchController;
import com.leo.productservice.dto.SearchParam;
import com.leo.productservice.dto.SearchResult;
import com.leo.productservice.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postSearch_returnsOkAndData() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("phone");

        SearchResult sr = new SearchResult();
        sr.setPageNum(1);
        sr.setPageSize(10);

        when(searchService.search(any(SearchParam.class))).thenReturn(sr);

        mockMvc.perform(post("/search/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));

        verify(searchService, times(1)).search(any(SearchParam.class));
    }
}
