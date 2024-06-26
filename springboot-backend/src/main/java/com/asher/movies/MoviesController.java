package com.asher.movies;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "*")
public class MoviesController {

    @Autowired
    private MovieService movieService;

    @GetMapping()
    public ResponseEntity<List<Movie>> getAllMovies() {
        return new ResponseEntity<List<Movie>>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Movie>> getById(@PathVariable ObjectId id) {
        return new ResponseEntity<Optional<Movie>>(movieService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/imdb/{imdbId}")
    public ResponseEntity<Optional<Movie>> findByImdbId(@PathVariable String imdbId) {
        return new ResponseEntity<Optional<Movie>>(movieService.findByImdbId(imdbId), HttpStatus.OK);

    }
}
