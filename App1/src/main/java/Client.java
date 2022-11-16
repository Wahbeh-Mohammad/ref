import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private final Scanner sc = new Scanner(System.in);
    // User information
    private int studentId;
    private String username, password;

    public Client() throws IOException, ClassNotFoundException {
        socket = new Socket("localhost", 8081);
        boolean authorized = login();
        if(authorized) {
            System.out.println("Hello " + username + ", Welcome to the grading system.");
            listen();
        } else {
            System.out.println("Invalid login credentials");
        }
    }

    private Response receiveResponse() throws IOException, ClassNotFoundException {
        // Read request object from Server
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        return (Response)objectInputStream.readObject();
    }

    private void sendRequest(Request req) throws IOException {
        // Send request object to Server
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(req);
        objectOutputStream.flush();
    }

    private void listen() throws IOException, ClassNotFoundException {
        boolean run = true;
        while(run) {
            System.out.println("\nGrading System Dashboard :");
            System.out.println("\t1. Display Student Marks");
            System.out.println("\t2. Specific Course Details");
            System.out.println("\t3. Display All Courses");
            System.out.println("\t4. Logout");
            System.out.print("Enter choice (1..4) : ");
            int choice = sc.nextInt();
            Request request;
            Response response;
            switch (choice) {
                case 1:
                    request = new Request("marks");
                    request.addArguement("studentId", String.valueOf(studentId));
                    sendRequest(request);
                    response = receiveResponse();
                    if (response.getStatus())
                        System.out.print(response.getArgument("data"));
                    break;
                case 2:
                    sc.nextLine(); // throw away the \n after reading choice.
                    System.out.print("Which course do you want to examine : ");
                    String course = sc.nextLine();
                    request = new Request("courseDetails");
                    request.addArguement("course", course);
                    sendRequest(request);
                    response = receiveResponse();
                    if (response.getStatus())
                        System.out.print(response.getArgument("data"));
                    break;
                case 3:
                    sendRequest(new Request("courses"));
                    response = receiveResponse();
                    if (response.getStatus())
                        System.out.print(response.getArgument("data"));
                    break;
                case 4:
                    System.out.println("See you soon " + username);
                    run = false;
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
        terminateResources();
    }

    private boolean login() throws IOException, ClassNotFoundException {
        System.out.print("Enter username : ");
        username = sc.nextLine();
        System.out.print("Enter password : ");
        password = sc.nextLine();
        // Prepare request and send it to the server
        Request req = new Request("auth");
        req.addArguement("studentName", username);
        req.addArguement("password", password);
        sendRequest(req);
        Response res = receiveResponse();
        System.out.println(res.getMessage());
        if(res.getStatus()) {
            // authorized;
            studentId = Integer.parseInt(res.getArgument("studentId"));
            return true;
        } else {
            // not authorized;
            return false;
        }
    }

    private void terminateResources() throws IOException {
        if(socket != null)  socket.close();
        if(sc != null)  sc.close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
    }
}