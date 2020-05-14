package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
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
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = -9135576688701595777L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
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
        String name=request.getParameter("name");
        String password = request.getParameter("password");
        
        response.setCharacterEncoding("GBK");
		response.setHeader("Content_Type", "text/html;charset=GBK");
		response.setContentType("text/html;charset=GBK");
        PrintWriter out = response.getWriter();
        
        
        System.out.println("telphone" + "," + telphone);
        System.out.println("name" + "," + name);
        System.out.println("password" + "," + password);

        
        String nesql = "insert into table_user(telphone,name,password) values(?,?,?)";
        String sql=new String(nesql.getBytes(),"GBK");

        /*实现向MySQL中插入username和password*/
        try {
            InitialContext ctx = new InitialContext();

            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/weatherfa");
            /*获得数据库的连接*/
            java.sql.Connection conn =ds.getConnection();
            conn = ds.getConnection();
            /*执行sql*/
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, telphone);
            ps.setString(2,name);
            ps.setString(3, password);
            ps.executeUpdate();
            
            String info="success";
            out.write(info);
        } catch (SQLException se) {
        	out.write("error");
            System.out.println("SQLException: " + se.getMessage());
            
            
        } catch (NamingException ne) {
            System.out.println("NamingException: " + ne.getMessage());
        }
    }

}
