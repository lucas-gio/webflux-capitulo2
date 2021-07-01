package com.gioia.capitulo2.images

import groovy.transform.CompileStatic
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import java.util.logging.Logger

@RestController
@CompileStatic
class ImageController {
    private final static Logger LOGGER = Logger.getLogger(ImageController.class.getName())

    @GetMapping("app/images")
    Flux<Image> images(){
        return Flux.just(
            new Image([id:"1", name:"imagen1"]),
            new Image([id:"2", name:"imagen2"]),
            new Image([id:"3", name:"imagen3"]),
            new Image([id:"4", name:"imagen4"]),
            new Image([id:"5", name:"imagen5"]),
        )
    }

    @PostMapping("app/images")
    Mono<Void> createImages(@RequestBody Flux<Image> images){
        return images.map(image -> {
            LOGGER.info("Se almacenarán las imágenes en la bd. ${image?.id ?: ""} ${image?.name ?: ''}")
            return image
        }).then()
    }
}
