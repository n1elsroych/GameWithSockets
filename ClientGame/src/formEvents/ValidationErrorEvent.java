package formEvents;

import java.util.EventObject;

public class ValidationErrorEvent extends EventObject{
    private String validationError;
    
    public ValidationErrorEvent(Object source, String validationError) {
        super(source);
        this.validationError = validationError;
    }

    public String getValidationError() {
        return validationError;
    }
}
