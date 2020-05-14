package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 /*获取请求的数据，并向控制台输出*/
        String telphone = request.getParameter("telphone");
        String password = request.getParameter("password");
        PrintWriter out = response.getWriter();

        
        System.out.println("telphone" + "," + telphone);
        System.out.println("password" + "," + password);

      //  int rs ;
       // String sql = "insert into table_user(telphone,name,password) values(?,?,?)";
		String sql= "select * from table_user where telphone = '"+telphone+"' and password = '"+password+"'";

        /*实现向MySQL中插入username和password*/
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/weatherfa");
            /*获得数据库的连接*/
            java.sql.Connection conn =ds.getConnection();
            conn = ds.getConnection();
            /*执行sql*/
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rSet = ps.executeQuery(sql);
			if(rSet.next()) {
				out.write("success");
            System.out.println("success");
			}
			else {
				out.write("cannot");
				System.out.println("can not login!");
			}
			conn.close();
        } catch (SQLException se) {
            System.out.println("SQLException: " + se.getMessage());
        } catch (NamingException ne) {
            System.out.println("NamingException: " + ne.getMessage());
        }
	}

}
