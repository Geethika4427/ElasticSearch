package com.example.controller;

import com.example.model.CourseDocument;
import com.example.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;


    
//    @GetMapping("/search")
//    public ResponseEntity<List<String>> searchCourseTitlesOnly(
//            @RequestParam(required = false) String q,
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) Integer minAge,
//            @RequestParam(required = false) Integer maxAge,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice,
//            @RequestParam(required = false) String startDate,
//            @RequestParam(defaultValue = "upcoming") String sort,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Instant startInstant = null;
//        if (startDate != null && !startDate.isEmpty()) {
//            startInstant = Instant.parse(startDate);
//        }
//
//        Page<CourseDocument> coursePage = courseService.searchCourses(
//            q, category, type, minAge, maxAge, minPrice, maxPrice, startInstant, sort, page, size
//        );
//
//        // Extract only course titles
//        List<String> titles = coursePage.getContent()
//                                        .stream()
//                                        .map(CourseDocument::getTitle)
//                                        .toList();
//
//        return ResponseEntity.ok(titles);
//    }

//    @GetMapping("/search")
//    public ResponseEntity<Page<CourseDocument>> searchCourses(
//            @RequestParam(required = false) String q,
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) Integer minAge,
//            @RequestParam(required = false) Integer maxAge,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice,
//            @RequestParam(required = false) String startDate,
//            @RequestParam(defaultValue = "upcoming") String sort,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Instant startInstant = null;
//        if (startDate != null && !startDate.isEmpty()) {
//            startInstant = Instant.parse(startDate);
//        }
//
//        Page<CourseDocument> coursePage = courseService.searchCourses(
//            q, category, type, minAge, maxAge, minPrice, maxPrice, startInstant, sort, page, size
//        );
//
//        return ResponseEntity.ok(coursePage);
//    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Instant startInstant = null;
        if (startDate != null && !startDate.isEmpty()) {
            startInstant = Instant.parse(startDate);
        }

        Page<CourseDocument> coursePage = courseService.searchCourses(
            q, category, type, minAge, maxAge, minPrice, maxPrice, startInstant, sort, page, size
        );

        List<Map<String, Object>> simplifiedCourses = coursePage.getContent().stream()
        	    .map(course -> {
        	        Map<String, Object> map = new HashMap<>();
        	        map.put("id", course.getId());
        	        map.put("title", course.getTitle());
        	        map.put("category", course.getCategory());
        	        map.put("price", course.getPrice());
        	        map.put("nextSessionDate", course.getNextSessionDate());
        	        return map;
        	    })
        	    .collect(Collectors.toList());


        Map<String, Object> response = new HashMap<>();
        response.put("total", coursePage.getTotalElements());
        response.put("courses", simplifiedCourses);

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/search/suggest")
    public ResponseEntity<List<String>> suggestCourses(@RequestParam("q") String prefix) {
        List<String> suggestions = courseService.getSuggestions(prefix);
        return ResponseEntity.ok(suggestions);
    }

}
