package ru.applmath.jdbctest;

public class Cat {
	
	private int id;
	private String name;
	private Integer age;
	private Integer breedId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Integer getBreedId() {
		return breedId;
	}
	public void setBreedId(Integer breedId) {
		this.breedId = breedId;
	}
	
	@Override
	public String toString() {
		return String.format("[id=%d,name=%s,age=%d,breedId=%d]",
				id,name,age,breedId); 
	}
}
