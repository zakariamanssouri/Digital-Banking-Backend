package ma.enset.digitalbanking.repositories;

import ma.enset.digitalbanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long>{
}
