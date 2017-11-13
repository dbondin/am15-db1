package ru.applmath.jpatest.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cat")
public class Cat {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "age")
	private Integer age;
	
	@JoinColumn(name="breed_id")
	@ManyToOne(optional=true)
	private Breed breed;
	
	@JoinTable(name="cat_human", joinColumns={@JoinColumn(name="cat_id")}, inverseJoinColumns={@JoinColumn(name="human_id")})
	@ManyToMany(fetch=FetchType.LAZY)
	private List<Human> humans = new ArrayList<Human>();

	public List<Human> getHumans() {
		return humans;
	}
	
	public void setHumans(List<Human> humans) {
		this.humans = humans;
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Breed getBreed() {
		return breed;
	}
	
	public void setBreed(Breed breed) {
		this.breed = breed;
	}
	
	@Override
	public String toString() {
		return String.format("[id=%d,name=%s,age=%d]", id, name, age);
	}
}
