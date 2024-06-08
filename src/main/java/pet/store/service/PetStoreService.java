package pet.store.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

@Autowired
private PetStoreDao petStoreDao;
@Autowired
private EmployeeDao employeeDao;
@Autowired 
private CustomerDao customerDao;



@Transactional(readOnly = false)
public PetStoreData savePetStore(PetStoreData petStoreData) {
	Long petStoreId = petStoreData.getPetStoreId();
	PetStore petStore = findOrCreatePetStoreDataById(petStoreId);
	
	copyPetStoreFields(petStore, petStoreData);
	return new PetStoreData(petStoreDao.save(petStore));
}

private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
	petStore.setPetStoreName(petStoreData.getPetStoreName());
	petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
	petStore.setPetStoreCity(petStoreData.getPetStoreCity());
	petStore.setPetStoreState(petStoreData.getPetStoreState());
	petStore.setPetStoreZip(petStoreData.getPetStoreZip());
	petStore.setPetStorePhone(petStoreData.getPetStorePhone());
	
	
}

private PetStore findOrCreatePetStoreDataById(Long petStoreId) {
PetStore petStore;
	
	if(Objects.isNull(petStoreId)) {
		petStore = new PetStore();
	}
	else {
	petStore = findPetStoreDataById(petStoreId);
	}
	return petStore;
}

private PetStore findPetStoreDataById(Long petStoreId) {
	// TODO Auto-generated method stub
	return petStoreDao.findById(petStoreId).orElseThrow (() -> new NoSuchElementException( "Contributor with ID " + petStoreId + " was not found"));
}

@Transactional(readOnly = false)
public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
	PetStore petStore = findPetStoreDataById(petStoreId);
	Long employeeId = petStoreEmployee.getEmployeeId();
	Employee employee =  findOrCreateEmployee(petStoreId, employeeId);
	
	copyEmployeeFields(employee, petStoreEmployee);

	employee.setPetStore(petStore);
	petStore.getEmployees().add(employee);
	
	return new PetStoreEmployee(employeeDao.save(employee));
}

private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
	if (Objects.isNull(employeeId)) {
		return new Employee();
	}
	else {
		return findEmployeeById(petStoreId, employeeId);}
	
}

private Employee findEmployeeById(Long petStoreId, Long employeeId) {
	Employee employee = employeeDao.findById(employeeId).orElseThrow(() -> new NoSuchElementException("Employee with ID =" + employeeId + " was not found"));

	if(!employee.getPetStore().getPetStoreId().equals(petStoreId)) {
		throw new IllegalArgumentException("Employee with the ID = " + employeeId +" not a member of the pet store with the ID");
	}

	return employee;
}

private Employee copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
	employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
	employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
	employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
	employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
	
	return employee;
	
}

@Transactional(readOnly = false)
public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreDataById(petStoreId);
		Long customerId = petStoreCustomer.getCustomerId();
		Customer customer =  findOrCreateCustomer(petStoreId, customerId);
		
		copyCustomerFields(customer, petStoreCustomer);

		customer.getPetStores().add(petStore);
		petStore.getCustomers().add(customer);
		
		return new PetStoreCustomer(customerDao.save(customer));
	}

	private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {

		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		
	}

	private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {
		if (Objects.isNull(customerId)) {
			return new Customer();
		}
		else {
			return findCustomerById(petStoreId, customerId);
			}
	}

	private Customer findCustomerById(Long petStoreId, Long customerId) {
		Customer customer = customerDao.findById(customerId).orElseThrow(() -> new NoSuchElementException("Customer with ID =" + customerId + " was not found"));
		boolean found = false;
		
		for(PetStore petStore : customer.getPetStores()) {
			
			if (petStore.getPetStoreId() == petStoreId) {
				found = true;
				break;	
			}
		}
		
		if ( !found ){
			throw new IllegalArgumentException("Customer with the ID = " + customerId +" not a member of the pet store with the ID");
		}

		return customer;
	}
	
	@Transactional(readOnly = true)
	public List<PetStoreData> retrieveAllPetStores() {
		List<PetStore> petStores = petStoreDao.findAll();
		List<PetStoreData> petStoreDataList = new LinkedList();
		for (PetStore petStore : petStores) {
			PetStoreData data = new PetStoreData(petStore);
			data.setEmployees(Collections.emptySet());
			data.setCustomers(Collections.emptySet());
			petStoreDataList.add(data);
			}
			
			return petStoreDataList;
	}
	
	
	@Transactional
	public PetStoreData retrievePetStoreById(Long storeId) {
		PetStore petStore = findPetStoreDataById(storeId);
		
		return new PetStoreData(petStore);
		
	}
	
	public void deletePetStoreById(Long storeId) {
		PetStore petStore = findPetStoreDataById(storeId);
		if (petStore != null) {
			petStoreDao.delete(petStore);
		} else {
			throw new NoSuchElementException("PetStore with ID " + storeId + " was not found");
		}
	}
	
}

		
	
		
		
		
		
		
		
		
		
		
	
	
	


