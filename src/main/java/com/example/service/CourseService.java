package com.example.service;

import com.example.model.CourseDocument;
import lombok.RequiredArgsConstructor;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final RestHighLevelClient client;

    public Page<CourseDocument> searchCourses(
            String q,
            String category,
            String type,
            Integer minAge,
            Integer maxAge,
            Double minPrice,
            Double maxPrice,
            Instant startDate,
            String sort,
            int page,
            int size
    ) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (q != null && !q.isEmpty()) {
            // Full-text search on title and description
//            boolQuery.must(QueryBuilders.multiMatchQuery(q, "title", "description"));
        	boolQuery.must(QueryBuilders.multiMatchQuery(q, "title", "description").fuzziness(Fuzziness.AUTO));

        }

        if (category != null && !category.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category.keyword", category));
        }

        if (type != null && !type.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("type.keyword", type));
        }

        if (minAge != null || maxAge != null) {
            RangeQueryBuilder ageRange = QueryBuilders.rangeQuery("minAge");
            if (minAge != null) ageRange.gte(minAge);
            if (maxAge != null) ageRange.lte(maxAge);
            boolQuery.filter(ageRange);
        }

        if (minPrice != null || maxPrice != null) {
            RangeQueryBuilder priceRange = QueryBuilders.rangeQuery("price");
            if (minPrice != null) priceRange.gte(minPrice);
            if (maxPrice != null) priceRange.lte(maxPrice);
            boolQuery.filter(priceRange);
        }

        if (startDate != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("nextSessionDate").gte(startDate));
        }

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page, size));

        // Sorting logic
        if ("priceAsc".equalsIgnoreCase(sort)) {
            queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        } else {
            // Default: sort by nextSessionDate ascending
            queryBuilder.withSort(SortBuilders.fieldSort("nextSessionDate").order(SortOrder.ASC));
        }

        NativeSearchQuery searchQuery = queryBuilder.build();

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        List<CourseDocument> courses = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // Build Pageable result
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(courses, pageable, searchHits.getTotalHits());
    }
    public List<String> getSuggestions(String prefix) {
        try {
            // Create the search request and suggestion builder
            SearchRequest searchRequest = new SearchRequest("courses");

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            SuggestBuilder suggestBuilder = new SuggestBuilder();

            CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders
                    .completionSuggestion("suggest") // This is the name of the field in your document
                    .prefix(prefix)
                    .skipDuplicates(true)
                    .size(10);

            suggestBuilder.addSuggestion("course-suggest", completionSuggestionBuilder);
            sourceBuilder.suggest(suggestBuilder);
            searchRequest.source(sourceBuilder);

            // Perform the search
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // Get the suggest object
            Suggest suggest = searchResponse.getSuggest();

            // Get the specific suggestion and cast it
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> genericSuggestion =
                    suggest.getSuggestion("course-suggest");

            // Now cast it safely to CompletionSuggestion
            if (genericSuggestion instanceof CompletionSuggestion completionSuggestion) {
                return completionSuggestion.getEntries().stream()
                        .flatMap(entry -> entry.getOptions().stream())
                        .map(option -> option.getText().string())
                        .collect(Collectors.toList());
            }

            return List.of();

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}


