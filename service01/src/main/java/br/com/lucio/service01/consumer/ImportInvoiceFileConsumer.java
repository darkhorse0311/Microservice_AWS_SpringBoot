package br.com.lucio.service01.consumer;

import br.com.lucio.service01.model.Invoice;
import br.com.lucio.service01.model.SnsMessage;
import br.com.lucio.service01.repository.InvoiceRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


@Service
public class ImportInvoiceFileConsumer {

    private static final Logger log = LoggerFactory.getLogger(ImportInvoiceFileConsumer.class);

    private final ObjectMapper objectMapper;
    private final InvoiceRepository invoiceRepository;
    private final AmazonS3 amazonS3;

    public ImportInvoiceFileConsumer(ObjectMapper objectMapper, InvoiceRepository invoiceRepository, AmazonS3 amazonS3) {
        this.objectMapper = objectMapper;
        this.invoiceRepository = invoiceRepository;
        this.amazonS3 = amazonS3;
    }

    @JmsListener(destination = "${aws.sqs.queue.invoice.events.name}")
    public void receiveS3Event(TextMessage textMessage) throws JMSException, IOException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        S3EventNotification s3EventNotification = objectMapper.readValue(snsMessage.getMessage(), S3EventNotification.class);
        processInvoiceNotification(s3EventNotification);
    }

    private void processInvoiceNotification(S3EventNotification s3EventNotification) throws IOException {
        for (S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord : s3EventNotification.getRecords()) {
            S3EventNotification.S3Entity s3 = s3EventNotificationRecord.getS3();
            String bucketName = s3.getBucket().getName();
            String objectKey = s3.getObject().getKey();

            String invoiceFile = downloadObject(bucketName, objectKey);
            Invoice invoice = objectMapper.readValue(invoiceFile, Invoice.class);
            log.info("Invoice file received: {}", invoice.toString());

            invoiceRepository.save(invoice);

            amazonS3.deleteObject(bucketName, objectKey);
        }
    }

    private String downloadObject(String bucketName, String objectKey) throws IOException {
        S3Object s30bject = amazonS3.getObject(bucketName, objectKey);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s30bject.getObjectContent()));
        String content;
        while((content = bufferedReader.readLine()) != null){
            stringBuilder.append(content);
        }
        return stringBuilder.toString();
    }
}
