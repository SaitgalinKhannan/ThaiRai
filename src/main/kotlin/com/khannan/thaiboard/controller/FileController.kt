package com.khannan.thaiboard.controller

import com.khannan.thaiboard.repository.PhotoRepository
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.math.min

@RestController
@RequestMapping(path = ["/files"])
class FileController(
    private val photoRepository: PhotoRepository
) {
    @GetMapping("/download/{id}")
    fun downloadMovieMedia(
        @PathVariable id: Long, @RequestHeader headers: HttpHeaders
    ): ResponseEntity<StreamingResponseBody> {
        val photo = photoRepository.findById(id)
        val file = File(photo.imageUrl)

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        try {
            val streamingResponseBody = StreamingResponseBody { outputStream1: OutputStream ->
                val inputStream = Files.newInputStream(file.toPath())
                inputStream.use { input ->
                    outputStream1.use { output ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int

                        while (true) {
                            bytesRead = input.read(buffer)
                            if (bytesRead == -1) {
                                break
                            }
                            output.write(buffer, 0, bytesRead)
                        }
                    }
                }
            }

            val filename = file.name
            val httpHeaders = HttpHeaders()
            httpHeaders.contentDisposition =
                ContentDisposition.builder("attachment").filename(filename, StandardCharsets.UTF_8).build()
            httpHeaders.contentType = MediaType.APPLICATION_OCTET_STREAM
            httpHeaders.contentLength = file.length()

            return ResponseEntity.ok().headers(httpHeaders).body(streamingResponseBody)
        } catch (e: IOException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/{id}")
    fun movieMedia(@RequestHeader headers: HttpHeaders, @PathVariable id: Long): ResponseEntity<ResourceRegion> {
        val photo = photoRepository.findById(id)
        val media: Resource = FileSystemResource(photo.imageUrl)
        val region = resourceRegion(media, headers)

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).contentType(
            MediaTypeFactory.getMediaType(media).orElse(MediaType.APPLICATION_OCTET_STREAM)
        ).body(region)
    }

    private fun resourceRegion(media: Resource, headers: HttpHeaders): ResourceRegion {
        var contentLength = 0L

        try {
            contentLength = media.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val range = headers.range
        return if (range.isNotEmpty()) {
            val start = range.first().getRangeStart(contentLength)
            val end = if (range.first().getRangeEnd(contentLength) > 1) range.first()
                .getRangeEnd(contentLength) else contentLength - 1
            val rangeLength = min((end - start + 1).toDouble(), contentLength.toDouble()).toLong()
            ResourceRegion(media, start, rangeLength)
        } else {
            val rangeLength = min((1024 * 1024).toDouble(), contentLength.toDouble()).toLong()
            ResourceRegion(media, 0, rangeLength)
        }
    }
}