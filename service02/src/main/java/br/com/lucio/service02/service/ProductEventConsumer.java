package br.com.lucio.service02.service;

import br.com.lucio.service02.model.CrudEventDTO;
import br.com.lucio.service02.model.ProductDTO;
import br.com.lucio.service02.model.SnsMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;

@Service
public class ProductEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    private ObjectMapper objectMapper;

    public ProductEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage textMessage) throws JMSException, IOException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
        CrudEventDTO crudEventDTO = objectMapper.readValue(snsMessage.getMessage(), CrudEventDTO.class);
        ProductDTO productDTO = objectMapper.readValue(crudEventDTO.getData(), ProductDTO.class);
        log.info("Product event received - Event: {} - ProductId: {} - MessageI: {}",
                crudEventDTO.getEventType(), productDTO.getId(), snsMessage.getMessageId());
    }

}
