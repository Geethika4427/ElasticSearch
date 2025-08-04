package com.example.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.model.CourseDocument;

public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
}
