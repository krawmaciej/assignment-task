## Simple search engine

### Running search engine spring boot application

Prerequisites:
 - mongodb instance/cluster
 - setting the following environment variables
    - MONGODB_HOST
    - MONGODB_PORT
    - MONGODB_USERNAME
    - MONGODB_PASSWORD
    - MONGODB_DB_NAME
    - MONGODB_AUTH_DB_NAME

#### Quickstart configuration
Launch mongodb docker container with:
```shell
docker run -d -p 27017:27017 \
-e MONGO_INITDB_ROOT_USERNAME=user \
-e MONGO_INITDB_ROOT_PASSWORD=password \
-e MONGO_INITDB_DATABASE=docs \
mongo:6.0.4-focal
```
Launch spring boot app with:
```shell
java -jar simple-search-app.jar \
--MONGODB_HOST=localhost \
--MONGODB_PORT=27017 \
--MONGODB_USERNAME=user \
--MONGODB_PASSWORD=password \
--MONGODB_DB_NAME=docs \
--MONGODB_AUTH_DB_NAME=admin
```

### Available endpoints

**POST /text/_doc**  
**Description:** Indexes and persists a text. ID for provided document will be generated and returned in response body.  
**Request Body:** Plain JSON string, e.g.:
```json
"the brown fox jumped over the brown dog"
```
**Response:** HTTP Status: 201 CREATED and body containing JSON with document's ID and text, e.g.:  
```json
{
   "_id": "63d6f9c8b6bf76245610e79f",
   "_source": "the brown fox jumped over the brown dog"
}
```

**POST/PUT /text/_create/:id**  
**Description:** Indexes and persists a text under the :id provided in the path.
Two documents with the same id cannot exist and making such request will result in HTTP 400 response.  
**Request Body:** Plain JSON string, e.g.:
```json
"the brown fox jumped over the brown dog"
```
**Response:** HTTP Status: 201 CREATED and body containing JSON with document's ID and text, e.g.:
```json
{
   "_id": "document1",
   "_source": "the brown fox jumped over the brown dog"
}
```

**GET /text/_doc/:id**  
**Description:** Retrieves a document with the requested :id.
If document does not exist then HTTP 404 response is returned.  
**Request Body:** Not applicable  
**Response:** HTTP Status: 200 OK and body containing JSON with document's ID and text, e.g.:
```json
{
   "_id": "document1",
   "_source": "the brown fox jumped over the brown dog"
}
```

**GET /text/_search?q=:term**  
**Description:** Retrieves total count and a list of all documents that contain the searched :term.
The list is sorted by TF-IDF.  
**Request Body:** Not applicable  
**Response:** HTTP Status: 200 OK and body containing JSON with document's ID and text, e.g.:
```json
{
   "hits": {
      "total": 2,
      "hits": [
         {
            "_id": "document3",
            "_score": 0.025155894150811604,
            "_source": "the red fox bit the lazy dog"
         },
         {
            "_id": "document1",
            "_score": 0.022011407381960155,
            "_source": "the brown fox jumped over the brown dog"
         }
      ]
   }
}
```
