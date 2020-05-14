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
 * Servlet implementation class ChangeNameServlet
 */
@WebServlet("/ChangeNameServlet")
public class ChangeNameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeNameServlet() {
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
        PrintWriter out = response.getWriter();
        
        System.out.println("telphone" + "," + telphone);
        System.out.println("name" + "," + name);
        
        String sql="update table_user set name='"+name+"' where telphone='"+telphone+"'";
        

        /*实现向MySQL中修改name*/
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/weatherfa");
            /*获得数据库的连接*/
            java.sql.Connection conn =ds.getConnection();
            conn = ds.getConnection();
         //   执行sql
            PreparedStatement ps = conn.prepareStatement(sql);
           /* ps.setString(1, name);
            ps.setString(2,telphone);*/
            ps.executeUpdate();
            
            String info="success";
            out.write(info);
            System.out.println(info);
        } catch (SQLException se) {
            System.out.println("SQLException: " + se.getMessage());
        } catch (NamingException ne) {
            System.out.println("NamingException: " + ne.getMessage());
        }
    }

	
}
