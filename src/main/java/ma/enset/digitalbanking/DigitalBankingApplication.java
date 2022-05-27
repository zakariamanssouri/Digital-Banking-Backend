package ma.enset.digitalbanking;

import ma.enset.digitalbanking.dtos.BankAccountDTO;
import ma.enset.digitalbanking.dtos.CurrentBankAccountDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.dtos.SavingBankAccountDTO;
import ma.enset.digitalbanking.entities.*;
import ma.enset.digitalbanking.enums.AccountStatus;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.exceptions.BalanceNotSufficientException;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.repositories.AccountOperationRepository;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import ma.enset.digitalbanking.services.BankAccountService;
import org.omg.CORBA.COMM_FAILURE;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalBankingApplication.class, args);
	}

	//@Bean
	CommandLineRunner start(
			CustomerRepository customerRepository,
			BankAccountRepository bankAccountRepository,
			AccountOperationRepository accountOperationRepository) {
		return args ->  {
			Stream.of("hassan","yassin","aicha").forEach(name-> {
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				customerRepository.save(customer);
			});
			customerRepository.findAll().forEach(customer -> {
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random() * 90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(customer);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);


				SavingAccount savingaccount = new SavingAccount();
				savingaccount.setId(UUID.randomUUID().toString());
				savingaccount.setBalance(Math.random() * 90000);
				savingaccount.setCreatedAt(new Date());
				savingaccount.setStatus(AccountStatus.CREATED);
				savingaccount.setCustomer(customer);
				savingaccount.setInterestRate(4.3);
				bankAccountRepository.save(savingaccount);
			});

			bankAccountRepository.findAll().forEach(account -> {
				for (int i = 0; i < 5; i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random() * 12000);
					accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
					accountOperation.setBankAccount(account);
					accountOperationRepository.save(accountOperation);
				}
			});



        };
	}
	//@Bean
    CommandLineRunner start2(BankAccountRepository bankAccountRepository)  {
        return args -> {
            BankAccount bankAccount = bankAccountRepository.findById("0b42f7ef-a5ad-4ba7-b36d-45a78d818bbe").orElse(null);
            System.out.println("******************************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());

            /*account type*/
            System.out.println("account type = " + bankAccount.getClass().getSimpleName());

            /*check the type of the account SA or CA*/
            if (bankAccount instanceof CurrentAccount) {
                System.out.println("Over draft =>" + ((CurrentAccount) bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount)
                System.out.println("Interest rate" + ((SavingAccount) bankAccount).getInterestRate());
            System.out.println("=============================");
            bankAccount.getAccountOperations().forEach(accountOperation -> {
                System.out.println(
                        accountOperation.getId()+"|"
                                +accountOperation.getOperationDate()+"|"
                                +accountOperation.getAmount()
                );
            });
        };
    }

    @Bean
    CommandLineRunner testservicelayer(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Hassan","Ahmed","Karim").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 12000, 5.5, customer.getId());

                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            for (BankAccountDTO account : bankAccountService.bankAccountsList()) {
                String accountId;
                if (account instanceof SavingBankAccountDTO) {
                    accountId = ((SavingBankAccountDTO) account).getId();
                }
                else {
                    accountId = ((CurrentBankAccountDTO) account).getId();
                }
                for (int i = 0; i < 10; i++) {
                    bankAccountService.credit(accountId, 10000 + Math.random() * 120000, "credit");
                    bankAccountService.debit(accountId, 1000 + Math.random() * 9000, "debit");
                }
            }
        };
    }

}
