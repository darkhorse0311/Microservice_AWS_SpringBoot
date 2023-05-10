package br.com.lucio.service02.service;

import br.com.lucio.service02.model.*;
import br.com.lucio.service02.model.dynamo.ProductEvent;
import br.com.lucio.service02.repository.ProductEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Service
public class ProductEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    private ObjectMapper objectMapper;

    private ProductEventRepository productEventRepository;

    public ProductEventConsumer(ObjectMapper objectMapper, ProductEventRepository productEventRepository) {
        this.objectMapper = objectMapper;
        this.productEventRepository = productEventRepository;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage textMessage) throws JMSException, IOException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        CrudEventDTO crudEventDTO = objectMapper.readValue(snsMessage.getMessage(), CrudEventDTO.class);
        ProductDTO productDTO = objectMapper.readValue(crudEventDTO.getData(), ProductDTO.class);

        ProductEvent productEvent = buildProductEvent(crudEventDTO, productDTO);
        productEventRepository.save(productEvent);

        log.info("Product event received - Event: {} - ProductId: {} - MessageId: {}",
                crudEventDTO.getEventType(), productDTO.getId(), snsMessage.getMessageId());
    }

    private ProductEvent buildProductEvent(CrudEventDTO crudEventDTO, ProductDTO productDTO) {

        ProductEvent productEvent = new ProductEvent();
        long timestamp = Instant.now().toEpochMilli();
        productEvent.setPk(productDTO.getCode());
        productEvent.setSk(crudEventDTO.getEventType() + "_" + timestamp);
        productEvent.setEventType(crudEventDTO.getEventType());
        productEvent.setProductId(productDTO.getId());
        productEvent.setUsername(productDTO.getUsername());
        productEvent.setTimestamp(timestamp);
        productEvent.setTtl(Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond()); //Em aprox 10 minutos o dynamo vai remover esse registro
        log.info("Product event builded: {}", productEvent.toString());
        return productEvent;
    }

}
