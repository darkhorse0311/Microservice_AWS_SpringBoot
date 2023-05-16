package br.com.lucio.service01.controller;

import br.com.lucio.service01.model.UrlResponseDTO;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceUploadFileController {

    @Value("${aws.s3.bucket.invoice.name}")
    private String bucketName;

    private AmazonS3 amazonS3;

    public InvoiceUploadFileController(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @PostMapping
    public ResponseEntity<UrlResponseDTO> createInvoiceUrl(){
        UrlResponseDTO urlResponseDTO = new UrlResponseDTO();
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(5));
        String processId = UUID.randomUUID().toString();

        GeneratePresignedUrlRequest presignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, processId)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(Date.from(expirationTime));

        urlResponseDTO.setExpirationTime(expirationTime.getEpochSecond());
        urlResponseDTO.setUrl(amazonS3.generatePresignedUrl(presignedUrlRequest).toString());
        return new ResponseEntity<UrlResponseDTO>(urlResponseDTO, HttpStatus.OK);

    }
}
