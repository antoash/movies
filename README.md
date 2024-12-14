# Movie Review Application

These are notes I created while working on this:

## Dependencies
- Lombok
- Spring Web
- Spring Data MongoDB
- Spring Boot DevTools

## Introduction:
- What is MongoDB?
	- A database management system that stores information as documents.
	- Hence it is a non relational database also known as NoSQL database.
- What is a document?
	- Data that is stored in key value pairs in formats like Json, Bson, XML etc.
	- A group of documents are called Collections.
- What is the benefit of such a database?
	- It is flexible - not all the documents are required to have the same fields. 
	- However, if required, schema validation can be performed which is used to define the fields and data types for a Collection.
	- Ex: in the GRADES collection, all documents must have GPA field (which allows Positive Values only) and Name field (which should be a String Value).
- What are the cons of such a database?
	- Doesn't support ACID transactions
- MongoDB database -> Collection -> Document
	- A mongoDB database contains lots of collections.
	- Each collection contains one or more Document. These Documents in Bson/Json will have data.
- In this application, we are working with two different collections: movie details and reviews.
- Hence we need two different classes that replicate the data records that exists within the database. 
	- A collection called "movies" is created within the movie-api-db which contains details of the movies that have been provided in json format.
	- The "reviews" collection will be created later.


## Backend Dev
### Module 1: Database configuration
- Configuring MongoDB using application.properties
- Adding a .env file to secure the config details of the database
- Adding a dependency from Maven Repository (https://mvnrepository.com/) to be able to use environment variables. 
- It is called Spring Dotenv by Paul Schwarz (https://mvnrepository.com/artifact/me.paulschwarz/spring-dotenv) 

- Error: Factory method 'standardMongoSettingsCustomizer' threw exception with message: Failed looking up TXT record for host cluster0.n46hm9z.mongodb.net

### Module 2: Adding Endpoints   

#### Topic 1: Intro to Spring Data MongoDB   
- Spring has support for MongoDB called Spring Data MongoDB. You can create MongoTemplate and MongoRepository that provides an immediate standard pattern for use.    

- Tips!
	- Check the database for the given data and try to replicate the format of the records by creating appropriate classes for the same. This template class is the first step to deserializing data that is being retrieved from the database.

- What are Spring Data annotations?   
	- There are different types of annotation for JPA, Mongo, etc.    
- What is the `@Document` annotation?    
	- Mark a class as a document type which is the format in which data is stored in MongoDB    
	- It is a part of SPRING DATA MONGODB.   
	- It indicates that this class should be treated as a MongoDB document.   
	- You can specify the name of the MongoDB collection you want to map the class to ("movies" is the name of the collection created on MongoDB, by default the collection name will be the same as that of the class).     
	- Additionally, @Id can be used to specify the primary key/unique identifier for each movie.   
- Why use `@Data` annotation?       
	- [@Data (projectlombok.org)](https://projectlombok.org/features/Data)              
	- It is an annotation from the Lombok project.   
	- It bundles the Getters, Setters, ToString, EqualsAndHashCode annotations into one. No need to explicitly mention the above annotations.   
	- Reduces the need to create the above methods manually or redundancy of code.    
- Why is `@AllArgsConstructor` and `@NoArgsConstructor` used together?   
	- `@AllArgsConstructor`: generates a constructor that requires an argument for every field present within the annotated class   
	- `@NoArgsConstructor`: generates a constructor that doesn't require any argument.   
	- Understand the 'why' reasoning here: [spring boot - Why to use @AllArgsConstructor and @NoArgsConstructor together over an Entity? - Stack Overflow](https://stackoverflow.com/questions/68314072/why-to-use-allargsconstructor-and-noargsconstructor-together-over-an-entity)    
   
#### Topic 2: Spring Data MongoDB - Relation Modelling   
There are two important data models: denormalized vs normalized    
[Data Model Design - MongoDB Manual v5.0](https://www.mongodb.com/docs/v5.0/core/data-model-design/)       

- Denormalized data model
	- Denormalization is a technique to combine from multiple tables into a single table.
	- Pros: All data is kept in one place, hence data can be fetched quickly (querying is faster).
	- Cons: Larger document size, duplication of data, document size constraint
	- Example: 
```Json
{
	_id: 8384,
	username: "3r2434",
	contact: {
				phone: 9889887845
				email:xyz@mail.com
			},
	access: {
				level: 5
				group: "dev"
			}
}
```
Both contact and access consists of embedded sub documents.     
An embedded data is like nesting Json within Json. This is the main method to perform denormalization which reduces the queries and makes updating much faster. Embedding provides better read performance   

Embedding patterns:    
[Model One-to-One Relationships with Embedded Documents - MongoDB Manual v5.0](https://www.mongodb.com/docs/v5.0/tutorial/model-embedded-one-to-one-relationships-between-documents/#std-label-data-modeling-example-one-to-one)    
[Model One-to-Many Relationships with Embedded Documents - MongoDB Manual v5.0](https://www.mongodb.com/docs/v5.0/tutorial/model-embedded-one-to-many-relationships-between-documents/#std-label-data-modeling-example-one-to-many)      

- Normalized data model   
	- Normalization is the process of reorganizing data to remove redundancy and unstructured data.  
	- Pros: All data is standardized and non redundant   
	- Cons: Slower querying bcus of more tables due to reduction of redundancy by splitting tables. Sometimes data can be lost due to over simplification.     
	- Example:   
User Document   
```Json
{
	_id: 1010,
	username: "xyz"
}
```
Contact Document
```Json
{
	_id: C32,
	user_id: 1010,
	phone: 9887545621,
	email: "oio@pop.com"
}
```
Access Document
```Json
{
	_id: A91,
	user_id: 1010,
	level: 5,
	group: "dev-test"
}
```
In this example, there are 3 separate documents and the common link between the document data is the id which is '1010'    
   
- Why is `@DocumentReference` used?   
	- Since a denormalized data model with embedded sub documents is being used, it will indicate a list of type 'Review' is the template for the embedding.    
- Explain the usage of ResponseEntity.   
	- [Using Spring ResponseEntity to Manipulate the HTTP Response | Baeldung](https://www.baeldung.com/spring-response-entity)    
	- It encapsulates the entire HTTP response which includes status code, body and headers.   
	- This allows us to configure the exact response when a request is received.   

#### Topic 3: Creating the logic for HTTP responses   
- Create a class called Service which has all the functions for the Controller.   
- @Autowired can be used instead of initializing the object using a constructor. We can directly instantiate to create the required instance of the object. (Note there is a difference between instantiate and initialize.)   
- @Service is used to indicate business layer logic.    
- @Repository indicates database access layer.   
- @Controller - only deals with mapping of endpoints to receive requests and provide the response. Doesn't deal with any code that modifies the response as per request. This modification is done in the backend.  
- @Repository - the layer that communicates with the database to retrieve data.   
	- In this application, MovieRepository is an interface that inherits from MongoRepository.     
	- Then we use an instance of MovieRepository in MovieService class to support the Controller with logic for the data retrieved.  

- Explain how the MongoRepository<S, T> interface works.     
	- [6. MongoDB repositories (spring.io)](https://docs.spring.io/spring-data/mongodb/docs/1.2.0.RELEASE/reference/html/mongo.repositories.html)   
	- It is a paramterized interface that takes arguments    
	- It inherits CRUD method from other interfaces and is used in the service layer.       
	- You can create custom queries apart from the already defined ones using @Query annotation.  
	- S - this is an Entity Class that is used to map a collection in MongoDB.   
	- T - it is the data type of variable defined as @Id in the above mentioned class.  
```Java 
public interface UserRepository extends MongoRepository<User, String> { 

	@Query("{ 'name' : ?0 }") 
	List<User> findUsersByName(String name); 
} 
```
#### Topic 4: Developing logic to create new reviews  
- Create ReviewService class and ReviewRepository interface.   
 
- What is MongoTemplate and how does it compare with MongoRepository?  
	- [MongoTemplate Spring Boot Example - JavaTechOnline](https://javatechonline.com/mongotemplate-spring-boot-examples/)    
	- MongoTemplate is a lower level abstraction which provides fine grain control to deal with complex custom CRUD based operations.    
	- MongoRepository is a higher level abstractions that has basic CRUD operations for immediate use.   
	- In this application, the creation of a review Document and embedding into the appropriate Movie Document is quite complex and can only be achieved through MongoTemplate.   
	- Using these templates, we don't have to write queries manually.   

Result of GET request after adding a review:    
```Json
{
    "objectId": {
        "timestamp": 1717658732,
        "date": "2024-06-06T07:25:32.000+00:00"
    },
    "imdbId": "tt3447590",
    "title": "Roald Dahl's Matilda the Musical",
    "releaseDate": "2022-11-25",
    "trailerLink": "https://www.youtube.com/watch?v=lroAhsDr2vI",
    "poster": "https://image.tmdb.org/t/p/w500/ga8R3OiOMMgSvZ4cOj8x7prUNYZ.jpg",
    "genres": [
        "Fantasy",
        "Family",
        "Comedy"
    ],
    "backdrops": [
        "https://image.tmdb.org/t/p/original/nWs0auTqn2UaFGfTKtUE5tlTeBu.jpg",
        "https://image.tmdb.org/t/p/original/bPftMelR4N3jUg2LTlEblFz0gWk.jpg",
        "https://image.tmdb.org/t/p/original/u2MLMkGEjJGQDs17Vmoej1RYFph.jpg",
        "https://image.tmdb.org/t/p/original/jG52tsazn04F1fe8hPZfVv7ICKt.jpg",
        "https://image.tmdb.org/t/p/original/4INEI7t7Vcg0cFvze7UIgwYCeSG.jpg",
        "https://image.tmdb.org/t/p/original/krAu6znzW8c54NdJPneNi4bem1l.jpg",
        "https://image.tmdb.org/t/p/original/6TUMppDMrYA4gzoaDUbbSnZFlxW.jpg",
        "https://image.tmdb.org/t/p/original/hacV1h1SWrPlrerF3xpetvEdqT.jpg",
        "https://image.tmdb.org/t/p/original/7iXsB1r9IK17ZFShqoxcHKQ7dLp.jpg",
        "https://image.tmdb.org/t/p/original/dwiRYDLcFyDOkgkSc1JFtTr6Kdk.jpg"
    ],
    "reviewIds": [
        {
            "body": "The movie was excellent! Thoroughly enjoyed watching it!",
            "id": {
                "timestamp": 1719128641,
                "date": "2024-06-23T07:44:01.000+00:00"
            }
        },
        {
            "body": "Great stuff",
            "id": {
                "timestamp": 1719128665,
                "date": "2024-06-23T07:44:25.000+00:00"
            }
        }
    ]
}
```

  

Functionality to add on:  
- Adding authentication and authorization    
- Complex relationships    
- Build an API with Gillete and add more requests.   
   
## Frontend Dev     
### Module 1: Set up   
```PowerShell
npx create-react-app movie-gold-v1
npm install axios
npm install bootstrap
npm i react-bootstrap
npm i @fortawesome/react-fontawesome
npm i @fortawesome/free-solid-svg-icons
npm i react-player
npm i react-router-dom
npm install @mui/material @emotion/react @emotion/styled
npm install react-material-ui-carousel
```
- [ ] Axios   
- [ ] Bootstrap for styling   
- [ ] Font Awesome icons for styling  
- [ ] React-Player for integrating videos into our platform   
- [ ] React-Router-dom for routing requests to endpoints   
- [ ] Carousel type style    
     

- Create an API folder that contains axios config. Configure the axios instance to make HTTP requests to the relevant API.   
- Base URL sets the base address for the API endpoint. Reduces redundancy so we only have to write the path for the endpoints later on.     
- Sets the default header for all requests. This header is set up to unblock HTTP requests that are blocked due to CORS since we are using a different origin (ngrok).      

when setMovies changes, the component App.js will be re rendered.  


## Module 2: Setting up Routes    
  
Changes:    
- Home.js  
- Layout.js - set up Outlet from react-router-dom   
- Updated App.js and Index.js     

App.js   
```JavaScript    
<Routes>
	<Route path='/' element={<Layout/>}>
		<Route path='/' element={<Home/>}></Route>
	</Route>  
</Routes>   
```

Routes are the parent element. Each Route under parent will render an element when the path matches.    
- Child route paths must be a combination of all the parent paths otherwise it won't render.   
So Layout.js is rendered followed by Home.js  
Finally App.js is rendered using a route in Index.js  
  
- Explain how the Outlet component works?    
	- An outlet is used in the parent element to render the child route elements that follow it.    
	- In this application, the order of rendering is as follows:   
		- Layout.js (Set up Outlet element)     
		- App.js (Renders Layout & Home) -> Index.js (Renders App)   
  
## Module 3: Styling   
  
```
<Carousel>    
    
{     
	movies?.map((movie) => {    
	return    
	(   
		<Paper>  
			<div className="movie-card-container">    
				<div className="movie-card">    
					<div className="movie-detail">   
						<div className="movie-poster">    
							<img src={movie.poster} alt="" />    
						</div>   
						<div className="movie-title">     
							<h4>{movie.title}</h4>     
						</div>   
					</div>                                           
				</div>   
			</div>   
		</Paper>   
	)  
})    
}   
</Carousel>   
}    
```    
  
- Carousel:         
	- [React Carousel Component - CoreUI](https://coreui.io/react/docs/components/carousel/)    
	- Used to create a slideshow with elements (images or text)   
	- Here movies is mapped    
- Paper:   
	- [React Paper component - Material UI (mui.com)](https://mui.com/material-ui/react-paper/)   
	- It is a style component to add shadows to the background.      
	- Most of the divs are used for styling in CSS.   
	- Since the movie image link is stored in poster variable, it is extracted for img src  
- Map:  
	- [JavaScript Array map() Method (w3schools.com)](https://www.w3schools.com/jsref/jsref_map.asp)          
	- To create an array of movies.        
	- So an array of movies with the above HTML is created for each movie.       






