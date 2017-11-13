package ru.applmath.jdbctest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CatRepo {
	
	private Connection conn;
	
	public CatRepo(final Connection conn) {
		this.conn = conn;
	}
	
	public List<Cat> findAllCats() throws Throwable {

		List<Cat> result = new ArrayList<Cat>();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM cat");
		while(rs.next()) {
			Cat c = new Cat();
			c.setId(rs.getInt("id"));
			c.setName(rs.getString("name"));
			c.setAge((Integer)rs.getObject("age"));
			c.setBreedId((Integer)rs.getObject("breed_id"));
			result.add(c);
		}
		rs.close();
		stmt.close();
		return result;
	}
	
	public void updateCat(final Cat cat) throws Throwable {
		PreparedStatement stmt = conn.prepareStatement("update cat set name=?, age=?, breed_id=? where id=?");
		stmt.setString(1, cat.getName());
		stmt.setObject(2, cat.getAge());
		stmt.setObject(3, cat.getBreedId());
		stmt.setInt(4, cat.getId());
		stmt.executeUpdate();
		stmt.close();
	}
}
