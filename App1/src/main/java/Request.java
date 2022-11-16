import java.io.Serializable;
import java.util.HashMap;

public class Request implements Serializable {
    // path: route in the server that will handle the request
    // args: body of the request
    private final String path;
    private final HashMap<String, String> args;

    public Request(String path) {
        this.path = path;
        this.args = new HashMap<>();
    }

    public String getPath() {
        return path;
    }

    public String getArgument(String key) {
        return args.get(key);
    }

    public void addArguement(String key, String value) {
        this.args.put(key, value);
    }
}
