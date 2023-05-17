package br.com.lucio.service01.repository;

import br.com.lucio.service01.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByNumber(String number);

    List<Invoice> findAllByCustomerName(String customerName);

}
