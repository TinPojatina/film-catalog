package com.filmcatalog;

import org.springframework.boot.SpringApplication;

public class TestFilmcatalogApplication {

	public static void main(String[] args) {
		SpringApplication.from(FilmcatalogApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
