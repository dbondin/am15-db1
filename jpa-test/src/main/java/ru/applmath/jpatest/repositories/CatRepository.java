package ru.applmath.jpatest.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.applmath.jpatest.entities.Cat;

@Repository
public interface CatRepository extends CrudRepository<Cat, Long>{
	Cat findOneByName(String name);
	@Query(value="select id,name,null as age,null as breed_id from cat", nativeQuery=true)
	Iterable<Cat> findAllCustomQuery();
}
