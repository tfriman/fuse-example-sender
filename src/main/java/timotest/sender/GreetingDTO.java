package timotest.sender;

import java.io.Serializable;
import java.util.Objects;

public class GreetingDTO implements Serializable {

    private String message;

    public GreetingDTO() {
    }

    public GreetingDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GreetingDTO{" +
                "message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GreetingDTO that = (GreetingDTO) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
