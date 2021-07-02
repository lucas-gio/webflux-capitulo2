package com.gioia.capitulo2

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter

@SpringBootApplication
class Application {
	static void main(String[] args) {
		SpringApplication.run(Application, args)
	}

	@Bean
	HiddenHttpMethodFilter hiddenHttpMethodFilter(){
		return new HiddenHttpMethodFilter()
	}
}
