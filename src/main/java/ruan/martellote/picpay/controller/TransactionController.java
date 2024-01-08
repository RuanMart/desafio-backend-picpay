package ruan.martellote.picpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ruan.martellote.picpay.domain.transaction.Transaction;
import ruan.martellote.picpay.domain.transaction.TransactionDTO;
import ruan.martellote.picpay.services.TransactionService;
import ruan.martellote.picpay.services.exception.BusinessException;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Transaction createTransaction(@RequestBody TransactionDTO transactionDTO)  {
        return transactionService.createTransaction(transactionDTO);
    }
}
