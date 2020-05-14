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
 * Servlet implementation class ChangePwdServlet
 */
@WebServlet("/ChangePwdServlet")
public class ChangePwdServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangePwdServlet() {
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
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		
		String telphone = request.getParameter("telphone");
        String password=request.getParameter("password");
        String opassword=request.getParameter("opassword");
        
        PrintWriter out = response.getWriter();
        
        System.out.println("telphone" + "," + telphone);
        System.out.println("password" + "," + password);
        System.out.println("opassword" + "," + opassword);
        
        String sql="update table_user set password='"+password+"' where telphone='"+telphone+"' and "
        		+ "password="+opassword;
        /*实现向MySQL中修改name*/
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/weatherfa");
            /*获得数据库的连接*/
            conn =ds.getConnection();
         //   执行sql
            ps = conn.prepareStatement(sql);
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
        }finally {
        	if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	if(conn!=null) {
        		try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    }

}
