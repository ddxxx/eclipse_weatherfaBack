package servlet;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.*;
/**
 * Servlet implementation class getHistoryWeatherServlet
 */
@WebServlet(description = "锟斤拷询锟斤拷取锟斤拷史锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷状图", urlPatterns = { "/getHistoryWeatherServlet" })
public class getHistoryWeatherServlet extends HttpServlet {

	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getHistoryWeatherServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

        //String cityName = new String(request.getParameter("cityName").getBytes(),"GBK");
		String nesql="";
		String flag="1";
		flag=request.getParameter("flag");//--------
		//System.out.println("flag="+flag);
		
		if(flag.equals(null)) {
			return;
		}
		//请求温度（最高，最低，温差）
		else if(flag.contains("1")) {
			String cityName = request.getParameter("cityName");
	        String date1=request.getParameter("date1");
	        String date2=request.getParameter("date2");
	        System.out.println(cityName+","+date1+date2);
	        nesql="SELECT date AS date,d_temp AS d_temp,n_temp as n_temp,d_temp-n_temp as dif "
	        		+ "FROM table_history "
	        		+ "WHERE date >= \""+date1+"\" AND date < \""+date2+"\" "
	        		+ "AND city = \""+ cityName+"\"";
		}
		//请求天气类型统计（天气类型，总数）
		else if(flag.contains("2")) {
	        String cityName = request.getParameter("cityName");
	        String date1=request.getParameter("date1");
	        String date2=request.getParameter("date2");
	        System.out.println(cityName+","+date1+date2);
	        nesql="SELECT weather AS weather,COUNT(weather) AS total "//涓滃钩
	                + "FROM table_history "
	        		+ "WHERE date >= \""+date1+"\" AND date < \""+date2+"\" "
	        		+ "AND city = \""+ cityName+"\""
	        		+  "GROUP BY weather  ORDER BY total DESC";
        }
		//请求风向统计
		else if(flag.contains("3")) {
			String cityName = request.getParameter("cityName");
	        String date1=request.getParameter("date1");
	        String date2=request.getParameter("date2");
	        System.out.println(cityName+","+date1+date2);
	        nesql="SELECT SUBSTRING_INDEX(wind,\" \",1) AS windt, COUNT(wind) AS total "//涓滃钩
	                + "FROM table_history "
	        		+ "WHERE date >= \""+date1+"\" AND date < \""+date2+"\" "
	        		+ "AND city = \""+ cityName+"\""
	        		+  "GROUP BY windt  ORDER BY total DESC";
		}
		//请求风力统计
		else if(flag.contains("4")) {
			String cityName = request.getParameter("cityName");
	        String date1=request.getParameter("date1");
	        String date2=request.getParameter("date2");
	        System.out.println(cityName+","+date1+date2);
	        nesql="SELECT SUBSTRING_INDEX(wind,\" \",-1) AS windp, COUNT(wind) AS total "//涓滃钩
	                + "FROM table_history "
	        		+ "WHERE date >= \""+date1+"\" AND date < \""+date2+"\" "
	        		+ "AND city = \""+ cityName+"\""
	        		+  "GROUP BY windp  ORDER BY total DESC";
		}
		//空处理
//		else {
//        	return;
//        }
		
//        cityName="东平";
//        date1="2018-01-01";
//        date2="2018-02-01";
        
        
		response.setCharacterEncoding("GBK");
		response.setHeader("Content_Type", "text/html;charset=GBK");
		response.setContentType("text/html;charset=GBK");
        PrintWriter out = response.getWriter();
        
        
        
        //String nesql="select name as name,骞撮緞  as age from testperson" ;
        String sql=new String(nesql.getBytes(),"GBK");
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
     
        try {
        	//String connectionStr="jdbc:mysql://localhost/educate?user=root&password=root&useUnicode=true&characterEncoding=utf8";
        	
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/weatherfa");

            conn =ds.getConnection();
            
            ps = conn.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            
            List<Map<String,String>> list=new ArrayList<Map<String,String>>();
            while(rs.next()) {
            	//System.out.println(rs.toString());
            	Map<String,String> map=new HashMap<String,String>();
            	
            	/*map.put:
            	 * 1.温度   xdata:date  ydata:d_temp  ydata1:n_temp  ydata2:dif
            	 * 2.天气   xdata:weathertype  ydata:total
            	 */
            	map.put("xdata",rs.getString(1));
            	map.put("ydata", rs.getString(2));
            	//请求温度趋势分析时会多两个参数
            	if(flag.contains("1")) {
            		map.put("ydata1",rs.getString(3));
            		map.put("ydata2",rs.getString(4));
            	}
            	
            	list.add(map);
            }
            
            String info="success";
            Gson gson=new Gson();
            String jsonstr=null;
            jsonstr=gson.toJson(list);
            if(list.size()!=0) {
                out.write("{\"success\":\"1\",\"result\":");
                out.write(jsonstr.toString()+"}");
                System.out.println(jsonstr.toString());
                System.out.println(info);
            }else {
            	out.write("{\"success\":\"0\"}");
            	System.out.println("error");
            }

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
        	if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        }
	}

}
