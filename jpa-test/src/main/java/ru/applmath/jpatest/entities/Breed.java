package ru.applmath.jpatest.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "breed")
public class Breed {

	@Id
	@Column
	private Long id;

	@Column
	private String name;

	@Column(name = "descr")
	private String description;
	
	@OneToMany(mappedBy="breed", targetEntity=Cat.class, fetch=FetchType.LAZY)
	List<Cat> cats = new ArrayList<Cat>();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return String.format("[id=%d,name=%s,description=%s]", id, name,
				description);
	}
}
