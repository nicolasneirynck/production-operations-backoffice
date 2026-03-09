package exception;

import java.util.Collections;
import java.util.Map;

public class TeamException extends Exception {
    private static final String MESSAGE =
            "Team kan niet worden aangemaakt, gelieve de nodige gegevens aan te passen.";

    private Map<String,IllegalArgumentException> exceptionMap;

    public TeamException(Map<String,IllegalArgumentException> exceptionMap){
        super(MESSAGE);
        this.exceptionMap = exceptionMap;
    }

    public Map<String,IllegalArgumentException> getExceptionMap(){
        return Collections.unmodifiableMap(exceptionMap);
    }
}



