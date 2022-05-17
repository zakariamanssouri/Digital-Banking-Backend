package ma.enset.digitalbanking.web;

import jdk.internal.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.entities.Customer;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {
    private BankAccountService bankAccountService;


    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long id) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(id);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomerDTO(@RequestBody CustomerDTO customer) {
        return bankAccountService.saveCustomer(customer);
    }

    @PutMapping("/customers/{customersId}")
    public CustomerDTO updateCustomer(@RequestBody CustomerDTO customerDTO,@PathVariable(name = "customersId") Long id) {
        customerDTO.setId(id);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    void deleteCustomer(@PathVariable(name = "id") Long id) {
        bankAccountService.deleteCustomer(id);
    }

}
