package com.deepak.distributed_payment_ledger.controllerAdvice;

import com.deepak.distributed_payment_ledger.exception.AccountNotFound;
import com.deepak.distributed_payment_ledger.exception.InsufficientBalance;
import com.deepak.distributed_payment_ledger.exception.InvalidAmount;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LedgerControllerAdvice {

    @ExceptionHandler(InsufficientBalance.class)
    public ProblemDetail handleInsufficientBalance(InsufficientBalance insufficientBalance) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, insufficientBalance.getMessage());
    }

    @ExceptionHandler(AccountNotFound.class)
    public ProblemDetail handleAccountNotFound(AccountNotFound accountNotFound) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, accountNotFound.getMessage());
    }

    @ExceptionHandler(InvalidAmount.class)
    public ProblemDetail handleInvalidAmount(InvalidAmount invalidAmount) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, invalidAmount.getMessage());
    }

}
