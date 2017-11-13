package ru.applmath.jpatest.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "human")
public class Human {

	public static enum Sex {
		MALE, FEMALE;
	}

	@Id
	@Column
	private Long id;

	@Column(nullable = false, length = 128)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private Sex sex;

	@JoinTable(name="cat_human", joinColumns={@JoinColumn(name="human_id")}, inverseJoinColumns={@JoinColumn(name="cat_id")})
	@ManyToMany(fetch=FetchType.EAGER)
	private List<Cat> cats = new ArrayList<Cat>();
	
	public List<Cat> getCats() {
		return cats;
	}
	
	public void setCats(List<Cat> cats) {
		this.cats = cats;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return String.format("[id=%d,name=%s,sex=%s]", id, name, sex);
	}
}
