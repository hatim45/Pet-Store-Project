package pet.store.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import pet.store.entity.Customer;
import pet.store.entity.Employee;

public interface CustomerDao extends JpaRepository<Customer, Long>{

}
