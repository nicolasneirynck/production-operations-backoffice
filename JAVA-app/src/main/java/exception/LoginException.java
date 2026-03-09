package exception;

import java.util.Collections;
import java.util.Map;

public class LoginException extends Exception {

    private static final String MESSAGE =
            "Ongeldige gegevens, inloggen is niet mogelijk met deze gegevens.";

    private Map<String,IllegalArgumentException> exceptionMap;

    public LoginException(Map<String,IllegalArgumentException> exceptionMap){
        super(MESSAGE);
        this.exceptionMap = exceptionMap;
    }

    public Map<String,IllegalArgumentException> getExceptionMap(){
        return Collections.unmodifiableMap(exceptionMap);
    }
}
