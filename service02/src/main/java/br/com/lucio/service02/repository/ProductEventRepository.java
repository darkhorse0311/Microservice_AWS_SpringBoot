package br.com.lucio.service02.repository;

import br.com.lucio.service02.model.dynamo.ProductEvent;
import br.com.lucio.service02.model.dynamo.ProductEventKey;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface ProductEventRepository extends CrudRepository<ProductEvent, ProductEventKey> {

    List<ProductEvent> findAllByPk(String code);

    List<ProductEvent> findAllByPkAndSkStartsWith(String code, String eventType);

}
