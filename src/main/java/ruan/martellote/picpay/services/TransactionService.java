package ruan.martellote.picpay.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ruan.martellote.picpay.domain.transaction.Transaction;
import ruan.martellote.picpay.domain.transaction.TransactionDTO;
import ruan.martellote.picpay.domain.user.User;
import ruan.martellote.picpay.repositories.TransactionRepository;
import ruan.martellote.picpay.services.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Transaction createTransaction(TransactionDTO transactionDTO) {
        User payer = userService.findById(transactionDTO.payerId());
        User payee = userService.findById(transactionDTO.payeeId());

        userService.validateTransaction(payer, transactionDTO.amount());

        boolean isAuth = this.authorizeTransaction(payer, transactionDTO.amount());

        if (!isAuth) throw new BusinessException("Transação não autorizada");

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.amount());
        transaction.setPayer(payer);
        transaction.setPayee(payee);
        transaction.setTransactionTime(LocalDateTime.now());

        payer.setBalance(payer.getBalance().subtract(transactionDTO.amount()));
        payee.setBalance(payee.getBalance().add(transactionDTO.amount()));

        transactionRepository.save(transaction);
        userService.updateUserBalance(payer);
        userService.updateUserBalance(payee);
        notificationService.sendNotification(payee, "Você recebeu um pagamento de " + transactionDTO.amount());
        notificationService.sendNotification(payer, "Transferência de " + transactionDTO.amount() + " realizada com sucesso");

        return transaction;
    }

    public boolean authorizeTransaction(User payer, BigDecimal value) {
        ResponseEntity<Map> authorizatonResponse = restTemplate.getForEntity("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc", Map.class);
        return authorizatonResponse.getStatusCode() == HttpStatus.OK && authorizatonResponse.getBody().get("message").equals("Autorizado");
    }

}
