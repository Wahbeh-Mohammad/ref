import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import java.util.ArrayList;

public class DBDao {
    private Connection conn;

    public DBDao() {
        try {
            // create DataSource & Connection
            MysqlDataSource ds = new MysqlDataSource();
            ds.setServerName("localhost");
            ds.setDatabaseName("Atypon");
            ds.setUser("mohammad");
            ds.setPassword("mohammad");
            ds.setUseSSL(false);
            ds.setAllowPublicKeyRetrieval(true);
            conn = ds.getConnection();
        } catch ( SQLException exception ) {
            System.out.println(exception.getMessage());
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String sqlStatement = "SELECT COUNT(*) FROM students WHERE studentName = ? AND password = ?;";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next())
            return resultSet.getInt(1) == 1;
        else
            return false;
    }

    public int fetchStudentId(String username, String password) throws SQLException {
        String sqlStatement = "SELECT * FROM students WHERE studentName = ? AND password = ?;";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("studentId");
    }

    public String fetchStudentMarks(String studentId) throws SQLException {
        String sqlStatement = "SELECT * FROM grades WHERE studentId = ?;";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
        preparedStatement.setString(1, studentId);
        ResultSet resultSet = preparedStatement.executeQuery();
        // build response text.
        StringBuilder sb = new StringBuilder();
        while(resultSet.next())
            sb.append(resultSet.getString("courseId")).append(" ").append(resultSet.getString("grade")).append("\n");
        return sb.toString();
    }

    public String fetchAllCourses() throws SQLException {
        String sqlStatement = "SELECT * FROM courses;";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlStatement);
        // build response text.
        StringBuilder sb = new StringBuilder();
        while(resultSet.next())
            sb.append(resultSet.getString("courseId")).append(" : ").append(resultSet.getString("name")).append('\n');
        return sb.toString();
    }

    public String fetchCourseDetails(String course) throws SQLException {
        // prepare sql statement
        String sqlStatement = "SELECT * FROM grades WHERE courseId = ?;";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement);
        preparedStatement.setString(1, course);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Integer> arr = new ArrayList<>();
        int sum = 0, count = 0, min = 101, max = -1;
        double median;
        while(resultSet.next()) {
            int curGrade = resultSet.getInt("grade");
            sum += curGrade;
            count++;
            if(curGrade < min) min = curGrade;
            if(curGrade > max) max = curGrade;
            arr.add(curGrade); // store numbers to find median later
        }
        arr.sort((n1, n2) -> (n1 < n2 ? n1 : n2));
        median = calculateMedian(arr);
        // build response text.
        StringBuilder sb = new StringBuilder();
        sb.append("Grades of ").append(course).append(" : ").append(arr).append('\n');
        sb.append("Average of course : ").append((double)sum / (double)count).append('\n');
        sb.append("Minimum grade : ").append(min).append('\n');
        sb.append("Maximum grade : ").append(max).append('\n');
        sb.append("Median of grades : ").append(median).append('\n');
        return sb.toString();
    }

    private double calculateMedian(ArrayList<Integer> arr) {
        int len = arr.size();
        if( len%2 == 0 )
            return (double)(arr.get(len / 2) + arr.get(len/2 - 1))/2;
        else
            return (double)arr.get(len / 2);
    }
}
