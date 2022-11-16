import java.io.Serializable;
import java.util.HashMap;

public class Response implements Serializable {
    // status: represents if the request failed or not
    // message: info about what happened in the server
    // args: body of the request
    private boolean status;
    private String message;
    private final HashMap<String, String> args;

    public Response(boolean status, String message) {
        this.status = status;
        this.message = message;
        this.args = new HashMap<>();
    }

    public boolean getStatus() {
        return this.status;
    }

    public String getMessage() {
        return message;
    }

    public String getArgument(String key) {
        return args.get(key);
    }

    public void addArguement(String key, String value) {
        this.args.put(key, value);
    }
}
