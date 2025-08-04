# Course Search API with Elasticsearch

## Overview
This Spring Boot project integrates with Elasticsearch to provide a powerful course search API with filtering, sorting, pagination, autocomplete, and fuzzy search capabilities.

### Setup Instructions
1. Clone the Repository <br>
git clone https://github.com/Geethika4427/ElasticSearch.git <br>
cd ElasticSearch

2. Start Elasticsearch <br>
docker-compose up -d <br>

Verify Elasticsearch is running:<br>
http://localhost:9200 <br>
You should see a JSON response with cluster information.

### Application Configuration
Spring Boot will connect to Elasticsearch on localhost:9200. No authentication is required. <br>

src/main/resources/application.properties <br>

spring.elasticsearch.uris=http://localhost:9200

### Sample Data
File: src/main/resources/sample-courses.json <br>

Contains 50+ course objects with fields like: <br>

. id, title, description

. category, type (ONE_TIME, COURSE, CLUB)

. gradeRange, minAge, maxAge

. price (decimal), nextSessionDate (ISO-8601)

## Indexing Sample Data

The data is automatically indexed into Elasticsearch at application startup.

To verify:<br>
http://localhost:9200/courses/_search

### API Endpoints

## Course Search (Assignment A)

1) Full text search <br>
GET http://localhost:8080/api/courses/search <br>

2) Filtering by category <br>
GET http://localhost:8080/api/courses/search?q=math <br>

3) Filtering by category and type <br>
GET http://localhost:8080/api/courses/search?category=Science&type=CLUB <br>

4) Filtering by Age <br>
GET http://localhost:8080/api/courses/search?minAge=6&maxAge=10 <br>
 
5) Filtering by PriceRange <br>
GET http://localhost:8080/api/courses/search?minPrice=30&maxPrice=60 <br>

6) Filtering by Startdate <br>
GET http://localhost:8080/api/courses/search?startDate=2025-06-01T00:00:00Z <br>

7) Sorting <br>
GET http://localhost:8080/api/courses/search?sort=priceAsc <br>

8) Combined Filters, Pagination & Sorting <br>
GET http://localhost:8080/api/courses/api/search?
    q=science&
    category=Science&
    type=CLUB&
    minAge=7&
    maxAge=10&
    minPrice=40&
    maxPrice=70&
    startDate=2025-06-01T00:00:00Z&
    sort=priceAsc&
    page=1&
    size=10 <br>
   
### Autocomplete (Assignment B)

1) Autocomplete Suggestions <br>
GET http://localhost:8080/api/courses/search/suggest?q=mat <br>
Response : "Math Basics", "Matrix Algebra" <br>

2) Fuzzy Search <br>
GET http://localhost:8080/api/courses/search?q=physscs <br>
Response : Physics Crash Course
   
### Testing - Postman 








