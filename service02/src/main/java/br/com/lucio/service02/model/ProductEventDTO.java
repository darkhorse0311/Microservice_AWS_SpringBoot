package br.com.lucio.service02.model;

import br.com.lucio.service02.enums.EventType;
import br.com.lucio.service02.model.dynamo.ProductEvent;

public class ProductEventDTO {

    private final  String code;
    private final EventType eventType;
    private final long productId;

    private final String messageId;
    private final String username;
    private final long timestamp;

    public ProductEventDTO(ProductEvent productEvent) {
        this.code = productEvent.getPk();
        this.eventType = productEvent.getEventType();
        this.productId = productEvent.getProductId();
        this.messageId = productEvent.getMessageId();
        this.username = productEvent.getUsername();
        this.timestamp = productEvent.getTimestamp();
    }

    public String getCode() {
        return code;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getProductId() {
        return productId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
