package br.com.lucio.service01.service;

import br.com.lucio.service01.enums.EventType;
import br.com.lucio.service01.model.Product;
import br.com.lucio.service01.model.ProductDTO;
import br.com.lucio.service01.model.CrudEventDTO;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class ProductCrudEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProductCrudEventPublisher.class);

    private final AmazonSNS snsClient;
    private final Topic productEventsTopic;
    private final ObjectMapper objectMapper;


    public ProductCrudEventPublisher(AmazonSNS snsClient,
                                     @Qualifier("productEventsTopic") Topic productEventsTopic,
                                     ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.productEventsTopic = productEventsTopic;
        this.objectMapper = objectMapper;
    }

    public void publishEvent(Product product, EventType eventType) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setCode(product.getCode());
        productDTO.setUsername("rodrigo.lucio@lucio.com.br");

        CrudEventDTO crudEvent = new CrudEventDTO();
        crudEvent.setEventType(eventType);

        try {
            crudEvent.setData(objectMapper.writeValueAsString(productDTO));

            PublishResult publish = snsClient.publish(productEventsTopic.getTopicArn(), objectMapper.writeValueAsString(crudEvent));
            log.info("Product event published - Event: {} - ProductId: {} - MessageId: {}", eventType, productDTO.getId(), publish.getMessageId());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("Erro ao publicar evento de crud " + eventType, e);
        }

    }
}
