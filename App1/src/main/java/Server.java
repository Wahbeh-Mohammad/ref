import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    private final DBDao dao = new DBDao();
    private final ServerSocket serverSocket;
    private final Socket socket;

    public Server() throws IOException, SQLException, ClassNotFoundException {
        serverSocket = new ServerSocket(8081);
        socket = serverSocket.accept();
        listen();
    }

    public Request parseRequest() throws IOException, ClassNotFoundException {
        // read Request object from client
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return (Request) objectInputStream.readObject();
        } catch (EOFException eofException) {
            // no more requests.
            return null;
        }
    }

    public void sendResponse(Response res) throws IOException {
        // send Response object to client
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(res);
        objectOutputStream.flush();
    }

    private void listen() throws IOException, SQLException, ClassNotFoundException {
        while(true) {
            Request request = parseRequest();
            if(request == null) break;
            Response response;
            String path = request.getPath();
            switch(path) {
                case "auth":
                    String studentName = request.getArgument("studentName"), password = request.getArgument("password");
                    boolean authorized = dao.authenticateUser(studentName, password);
                    String message, studentId = null;
                    if(authorized) {
                        message = "Authorized";
                        studentId = String.valueOf(dao.fetchStudentId(studentName, password));
                    } else {
                        message = "Not authorized";
                    }
                    response = new Response(authorized, message);
                    response.addArguement("studentId", studentId);
                    sendResponse(response);
                    break;
                case "marks":
                    String marks = dao.fetchStudentMarks(request.getArgument("studentId"));
                    response = new Response(true, "Success");
                    response.addArguement("data", marks);
                    sendResponse(response);
                    break;
                case "courseDetails":
                    String courseDetails = dao.fetchCourseDetails(request.getArgument("course"));
                    response = new Response(true, "Success");
                    response.addArguement("data", courseDetails);
                    sendResponse(response);
                    break;
                case "courses":
                    String courses = dao.fetchAllCourses();
                    response = new Response(true, "Success");
                    response.addArguement("data", courses);
                    sendResponse(response);
                    break;
            }
        }
    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        Server server = new Server();
    }
}
