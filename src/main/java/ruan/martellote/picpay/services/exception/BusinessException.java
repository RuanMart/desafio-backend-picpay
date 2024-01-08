package ruan.martellote.picpay.services.exception;

import java.io.Serial;

public class BusinessException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessException(String msg) {
        super(msg);
    }
}

