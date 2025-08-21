package com.leo.productservice;

import com.leo.productservice.dto.SearchParam;
import com.leo.productservice.dto.SearchResult;
import com.leo.productservice.model.ProductEsModel;
import com.leo.productservice.service.impl.SearchServiceImpl;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 单元测试：SearchServiceImpl（mock ElasticsearchClient）
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class SearchServiceImplTest {

    @Mock
    private ElasticsearchClient esClient;

    @InjectMocks
    private SearchServiceImpl searchService;

    @BeforeEach
    public void setUp() {
        // searchService 已由 @InjectMocks 构造
    }

    @Test
    void productUp_success() throws Exception {
        List<ProductEsModel> list = new ArrayList<>();
        ProductEsModel m = new ProductEsModel();
        m.setSkuId(100L);
        m.setSkuTitle("测试商品100");
        list.add(m);

        BulkResponse mockResp = mock(BulkResponse.class);
        when(mockResp.errors()).thenReturn(false);
        when(esClient.bulk(any(BulkRequest.class))).thenReturn(mockResp);

        boolean ok = searchService.productUp(list);
        assertTrue(ok);

        ArgumentCaptor<BulkRequest> captor = ArgumentCaptor.forClass(BulkRequest.class);
        verify(esClient, times(1)).bulk(captor.capture());
        BulkRequest req = captor.getValue();
        assertNotNull(req);
        assertEquals(1, req.operations().size());
    }

    @Test
    void productDown_success() throws Exception {
        List<Long> ids = List.of(100L, 101L);
        BulkResponse mockResp = mock(BulkResponse.class);
        when(mockResp.errors()).thenReturn(false);
        when(esClient.bulk(any(BulkRequest.class))).thenReturn(mockResp);

        boolean ok = searchService.productDown(ids);
        assertTrue(ok);

        ArgumentCaptor<BulkRequest> captor = ArgumentCaptor.forClass(BulkRequest.class);
        verify(esClient, times(1)).bulk(captor.capture());
        assertEquals(2, captor.getValue().operations().size());
    }

    @Test
    void updateHotScore_callsClient() throws Exception {
        // mock UpdateResponse generic class - we only care that esClient.update 被调用
        when(esClient.update(any(UpdateRequest.class), eq(ProductEsModel.class)))
                .thenReturn(null);

        searchService.updateHotScore(123L, 999L);

        ArgumentCaptor<UpdateRequest> captor = ArgumentCaptor.forClass(UpdateRequest.class);
        verify(esClient, times(1)).update(captor.capture(), eq(ProductEsModel.class));

        UpdateRequest captured = captor.getValue();
        assertEquals("123", captured.id());
    }

    @Test
    void search_basicPaginationAndCall() throws Exception {
        SearchParam param = new SearchParam();
        param.setKeyword("phone");
        param.setPageNum(2);
        param.setPageSize(5);

        // mock a SearchResponse with empty hits
        SearchResponse<ProductEsModel> mockResp = mock(SearchResponse.class);
        HitsMetadata<ProductEsModel> hitsMeta = mock(HitsMetadata.class);
        when(mockResp.hits()).thenReturn(hitsMeta);
        when(hitsMeta.hits()).thenReturn(Collections.emptyList());
        // total might be null or 0; we can let service handle it

        when(esClient.search(any(SearchRequest.class), eq(ProductEsModel.class))).thenReturn(mockResp);

        SearchResult result = searchService.search(param);
        assertNotNull(result);
        assertEquals(2, result.getPageNum());
        assertEquals(5, result.getPageSize());

        // verify search called once
        verify(esClient, times(1)).search(any(SearchRequest.class), eq(ProductEsModel.class));
    }
}
