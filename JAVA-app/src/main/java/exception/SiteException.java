package exception;

import java.util.Collections;
import java.util.Map;

public class SiteException extends Exception {

    private static final String MESSAGE =
            "Site kan niet worden aangemaakt, gelieve de nodige gegevens aan te passen.";

    private Map<String,IllegalArgumentException> exceptionMap;

    public SiteException(Map<String,IllegalArgumentException> exceptionMap){
        super(MESSAGE);
        this.exceptionMap = exceptionMap;
    }

    public Map<String,IllegalArgumentException> getExceptionMap(){
        return Collections.unmodifiableMap(exceptionMap);
    }
}
