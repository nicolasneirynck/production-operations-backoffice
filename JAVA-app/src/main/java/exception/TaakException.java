package exception;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TaakException extends Exception {

        private static final String MESSAGE =
                "Taak kan niet worden aangemaakt, gelieve de nodige gegevens aan te passen.";

        private Map<String,IllegalArgumentException> exceptionMap;

        public TaakException(Map<String,IllegalArgumentException> exceptionMap){
            super(MESSAGE);
            this.exceptionMap = exceptionMap;
        }

        public Map<String,IllegalArgumentException> getExceptionMap(){
            return Collections.unmodifiableMap(exceptionMap);
        }
}
