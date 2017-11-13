package ru.applmath.jdbctest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;

public class Main {

	private static Connection conn  = null;

	private static void printRS(final ResultSet rs) throws Throwable {
		ResultSetMetaData rsmd = rs.getMetaData();
		for(int i=0; i<rsmd.getColumnCount(); ++i) {
			System.out.print(rsmd.getColumnName(i+1) + "(" + rsmd.getColumnTypeName(i+1) + ") ");
		}
		System.out.println();
		while(rs.next()) {
			for(int i=0; i<rsmd.getColumnCount(); ++i) {
				System.out.print(rs.getString(i+1) + " ");
			}
			System.out.println();
		}
	}
	
	private static void test1() throws Throwable {
		System.out.println("--- test1 ---");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM CAT");
		while(rs.next()) {
			System.out.println(rs.getInt(1) + " "
					+ rs.getString(2) + " "
					+ rs.getInt(3) + " "
					+ rs.getString(4));
		}
		rs.close();
		stmt.close();
	}
	
	private static void test2() throws Throwable {
		System.out.println("--- test2 ---");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM CAT");
		printRS(rs);
		rs.close();
		stmt.close();
	}
	
	private static void test3() throws Throwable {
		System.out.println("--- test3 ---");
		Statement stmt = conn.createStatement();
		int res = stmt.executeUpdate("update cat set age=age+1 where age is not null"); // update,insert,delete
		System.out.println("res=" + res);
		stmt.close();
	}

	private static PreparedStatement test4Stmt = null;
	
	private static void test4(final String name) throws Throwable {
		System.out.println("--- test4(" + name + ") ---");
		if(test4Stmt == null) {
			System.out.println("Готовим стейтмент тут !!!");
			test4Stmt = conn.prepareStatement("select * from cat where name = ?");
		}
		test4Stmt.setString(1, name);
		ResultSet rs = test4Stmt.executeQuery();
		printRS(rs);
		rs.close();
		// test4Stmt.close();
	}
	
	private static void test5() throws Throwable {
		System.out.println("--- test5() ---");
		boolean ac = conn.getAutoCommit();
		
		conn.setAutoCommit(false);
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		test4("Том");
		test3();
		test4("Том");
		conn.rollback();
		test4("Том");
		
		conn.setAutoCommit(ac);
	}
	
	private static void test6() throws Throwable {
		System.out.println("--- test6() ---");
		
		DatabaseMetaData dbmd = conn.getMetaData();
		
		ResultSet rs = dbmd.getTables("", "public", "", null);
		printRS(rs);
		rs.close();
		System.out.println("--- cat columns ---");
		rs = dbmd.getColumns("", "public", "cat", "");
		printRS(rs);
		rs.close();
	}
	public static void main(String[] args) throws Throwable {

		Class.forName("org.postgresql.Driver");

		conn = DriverManager.getConnection(
				"jdbc:postgresql://gagarine/dbondin", "dbondin", "qwerty");

		System.out.println("autocommit=" + conn.getAutoCommit());

		test1();
		test2();
		test3();
		test4("БАРСИК");
		test4("Пушок");
		test4("Васька");
		test5();
		test6();
		
		System.out.println("--- cat objects ---");
		CatRepo cr = new CatRepo(conn);
		List<Cat> cats = cr.findAllCats();
		for(Cat c : cats) {
			System.out.println(c);
			if(c.getAge() != null && c.getAge() > 100) {
				c.setAge(0);
				cr.updateCat(c);
			}
		}
		
		System.out.println("OK");
	}

}
