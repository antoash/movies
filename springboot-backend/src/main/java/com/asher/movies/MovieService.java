package com.asher.movies;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getById(ObjectId id) { // using the Optional class to deal with cases which returns NULL
        return movieRepository.findById(id);
    }

    public Optional<Movie> findByImdbId(String imdbId) {
        return movieRepository.findMovieByImdbId(imdbId);
    }


}
