package com.gioia.capitulo2.home

import com.gioia.capitulo2.images.ImageService
import groovy.transform.CompileStatic
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@CompileStatic
class HomeController {
    private final ImageService imageService

    HomeController(ImageService imageService){
        this.imageService = imageService
    }

    @GetMapping(value = "/images/{filename:.+}/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    Mono<ResponseEntity<?>> oneRawImage(@PathVariable String filename){
        return imageService.findOneImage(filename)
            .map(resource->{
                try{
                    return ResponseEntity
                        .ok()
                        .contentLength(resource.contentLength())
                        .body(new InputStreamResource(resource.getInputStream()))
                }
                catch(Exception e){
                    return ResponseEntity
                        .internalServerError()
                        .body("No se pudo encontrar la imágen ${filename?:''}. ${e.getMessage()}")
                }
            })
    }

    @PostMapping(value="/images")
    Mono<String> createFile(@RequestPart(name="file") Flux<FilePart> files){
        return imageService.createImage(files)
            .then(Mono.just("redirect:/"))
    }

    @DeleteMapping("/images/{filename:.+}/raw")
    Mono<String> deleteFile(@PathVariable String filename){
        return imageService.deleteImage(filename)
            .then(Mono.just("redirect:/"))
    }

    @GetMapping("/")
    Mono<String> index(Model model){
        model.addAttribute("images", imageService.findAllImages())
        return Mono.just("index")
    }
}
