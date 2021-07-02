package com.gioia.capitulo2.images

import groovy.transform.CompileStatic
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import org.springframework.util.FileSystemUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger
import java.util.stream.StreamSupport

@Service
@CompileStatic
class ImageService {
    private static String UPLOAD_ROOT = "/home/pc/upload-dir"
    private final ResourceLoader resourceLoader
    private final static Logger LOG = Logger.getLogger(ImageService.class.getName())

    ImageService(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader
    }

    @Bean
    /**
     * Inicializador con archivos de pruebas.
     * Borra la carpeta, la crea, y crea tres archivos dentro.
     */
    CommandLineRunner setUp() throws IOException{
        CommandLineRunner runnable = (args)->{
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT))
            Files.createDirectory(Paths.get(UPLOAD_ROOT))

            FileCopyUtils.copy("Cubierta primer libro", new FileWriter(UPLOAD_ROOT + "/libro1.jpg"))
            FileCopyUtils.copy("Cubierta segundo libro", new FileWriter(UPLOAD_ROOT + "/libro2.jpg"))
            FileCopyUtils.copy("Cubierta tercer libro", new FileWriter(UPLOAD_ROOT + "/libro3.jpg"))
        }

        return runnable
    }

    /**
     * Lee el directorio upload_root, y por cada elemento crea una instancia Image con su hashcode
     * y su nombre de archivo.
     * @return Un flujo de imágenes
     */
    Flux<Image> findAllImages(){
        try{
            return Flux.fromStream(
                    StreamSupport.stream(Files.newDirectoryStream(Paths.get(UPLOAD_ROOT)).spliterator(), true)
                            .map((Path path) -> new Image([id: String.valueOf(path.hashCode()), name: path.getFileName().toString()])))
        }
        catch (Exception e){
            LOG.severe("Ocurrió un error al buscar todas las imágenes" + e.getMessage())
        }
        return Flux.empty()
    }

    Mono<Resource> findOneImage(String filename){
        // No se crea desde un iterable ya que no tendría sentido al tratarse de un único elemento en lugar de una
        // colección iterable.
        return Mono.fromSupplier(()->{
            resourceLoader.getResource("file:${UPLOAD_ROOT}${FileSystems.getDefault().getSeparator()}${filename}")
        })
    }

    Mono<Void> createImage(Flux<FilePart> files){
        return files.flatMap(file-> file.transferTo(
                Paths.get(UPLOAD_ROOT, file.filename()).toFile()
        )).then()
    }

    Mono<Void> deleteImage(String filename){
        return Mono.fromRunnable(()-> {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename))
            }
            catch (Exception e) {
                LOG.severe("Ocurrió un error al eliminar la imágen ${filename ?: ''}")
                throw e
            }
        })
    }
}
