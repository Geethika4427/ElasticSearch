package com.example.config;

import com.example.model.CourseDocument;
import com.example.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CourseRepository courseRepository;

    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("📁 Trying to load sample-courses.json");

        ClassPathResource resource = new ClassPathResource("sample-courses.json");

        if (!resource.exists()) {
            throw new IllegalStateException("🚨 sample-courses.json not found in classpath!");
        }

        InputStream inputStream = resource.getInputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

//        List<CourseDocument> courses = mapper.readValue(inputStream, new TypeReference<>() {});
        
        List<CourseDocument> courses = mapper.readValue(inputStream, new TypeReference<>() {});
        courses.forEach(course -> course.setSuggest(new Completion(List.of(course.getTitle()))));


        System.out.println("✅ Loaded " + courses.size() + " courses.");

        courseRepository.saveAll(courses);
        System.out.println("✅ Sample courses indexed.");
    }

}
