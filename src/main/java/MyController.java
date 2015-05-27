import java.util.*;
import java.security.*;
import javax.sql.*;
import javax.annotation.*;
import javax.persistence.*;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.jdbc.datasource.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.*;

import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

/*
import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.service.*;
*/

@Controller @EnableAutoConfiguration @Configuration
public class MyController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MyController.class, args);
	}
	
	@RequestMapping(value="/hello/{name}")
	public ModelAndView hello(@PathVariable String name) {
		ModelAndView view = new ModelAndView("view");
		view.addObject("message", "Hello ");
		view.addObject("name", name);
		return view;
	}

	@RequestMapping("/text") @ResponseBody
	public String text() {
		System.out.println("text");
		return "text";
	}

	@RequestMapping("/about")
	public String about() {
		return "about";
	}

	@RequestMapping("/pricing")
	public String pricing() {
		return "pricing";
	}

	@RequestMapping("/articles")
	public String articleList() {
		return "articles";
	}

	@RequestMapping("/article/{id}")
	public ModelAndView article(@PathVariable String id) {
		System.out.println("article = " + id);
		ModelAndView view = new ModelAndView("article-" + id);
		// view.addObject("name", name);
		return view;
	}

	@RequestMapping("/contact")
	public String contact() {
		return "contact";
	}

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	/*
	@RequestMapping(value="/error")
	public String error() {
		return "error";
	}
	*/
	
	@Value("${my.jdbc.driver}")
	private String driver;
	@Value("${my.jdbc.server}")
	private String server;
	@Value("${my.jdbc.user}")
	private String user;
	@Value("${my.jdbc.password}")
	private String password;
	
	@RequestMapping("/data") @ResponseBody
	private String testDatabase() {
		String connection = server + "?user=" + user + "&password=" + password;
		String sql = "select * from users where email = ?";
		String result = "";

		try {
			Class.forName(driver).newInstance();
			java.sql.Connection cn = java.sql.DriverManager.getConnection(
				connection);
			java.sql.PreparedStatement ps = cn.prepareStatement(sql);
			ps.setString(1, "cool@programming.language");
			java.sql.ResultSet rs = ps.executeQuery();
			java.sql.ResultSetMetaData md = rs.getMetaData();

			int count = md.getColumnCount();

			/*
			for (int i = 1; i <= count; i++) {
				result += md.getColumnName(i);
			}
			*/
			result += "[";
			while (rs.next()) {
				result += "{";
				for (int i = 1; i <= count; i++) {
					result += md.getColumnName(i) + ":";
					result += "'" + rs.getString(i) + 
						(i == count ? "'" : "',");
				}
				result += "},\n";
			}
			if (result.charAt(result.length()-2) == ',') {
				result = result.substring(0, result.length()-2);
			}
			result += "]";
			rs.close();
			ps.close();
			cn.close();
		} catch (Exception e) {
			return "[]";
		}
		return result;
	}

	/*	
	public void testJdbc() {
		DriverManagerDataSource source = new DriverManagerDataSource();
		source.setDriverClassName(driver);
		source.setUrl(server);
		source.setUsername(user);
		source.setPassword(password); 

		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(source);
		// template.update("update users set name='name'");
	}

	@PostConstruct
	private void readHibernate() {
		Session session = factory.openSession();
		String result = "";
		SQLQuery query = session.createSQLQuery("select * from users");
		List<Object[]> records = query.list();
		for (Object[] record : records)  {
			for (Object field : record) {
				result += "\t" + field;
			}
			result += "\n";
		}
		System.out.println(result);
	}

	private void updateHibernate() {
		Session session = factory.openSession();
		session.getTransaction().begin();
		SQLQuery query = session.createSQLQuery("update users set name='test'");
		query.executeUpdate();
		session.getTransaction().commit();
	}

	private SessionFactory factory = new org.hibernate.cfg.
		Configuration().configure().buildSessionFactory();
	*/

	private String encode(String password) {
		String result = "";
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] bytes = password.getBytes();
			byte[] hash = sha256.digest(bytes);
			for (int i = 0; i < hash.length; i++) {
				result += String.format("%02x", hash[i]);
			}
		} catch (Exception e) {}
		return result;
	}
}
