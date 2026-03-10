package exception;

import java.util.Collections;
import java.util.Map;

public class ValidationException extends Exception {

    private Map<String,IllegalArgumentException> exceptionMap;

    public ValidationException(Map<String,IllegalArgumentException> exceptionMap){
        super();
        this.exceptionMap = exceptionMap;
    }

    public Map<String,IllegalArgumentException> getExceptionMap(){
        return Collections.unmodifiableMap(exceptionMap);
    }
}
