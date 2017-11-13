package ru.applmath.jpatest;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ru.applmath.jpatest.entities.Breed;
import ru.applmath.jpatest.entities.Cat;
import ru.applmath.jpatest.entities.Human;
import ru.applmath.jpatest.repositories.CatRepository;

@SpringBootApplication
public class JpaTestApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(JpaTestApplication.class);
	
	@Autowired
	private CatRepository catRepository;
	
	@Autowired
	private EntityManager em;
	
	public static void main(String[] args) {
		SpringApplication.run(JpaTestApplication.class, args);
	}

	@Bean
	public CatTester catTester() {
		return new CatTesterImpl();
	}
	
	@Transactional
	@Bean
	public CommandLineRunner run(final CatTester catTester) {
		return new CommandLineRunner() {
			
			@Override
			public void run(String... arg0) throws Exception {
				LOGGER.info("*** started ***");
				
				catTester.updateTheCat();
				catTester.display();
				
				List<Human> hl = (List<Human>) em.createQuery("from Human").getResultList();
				
				for(Human h : hl) {
					LOGGER.info("{}", h);
					List<Cat> cl = h.getCats();
					for(Cat c : cl) {
						LOGGER.info("    cat = {}", c);
					}
				}
				
				catRepository.findAll().forEach(c -> {
					LOGGER.info("MAGIC>> {}", c);
				});
				
				Cat vaska = catRepository.findOneByName("Васька");
				LOGGER.info("Сильное колдунство: {}", vaska);
				
				catRepository.findAllCustomQuery().forEach(c -> {
					LOGGER.info("QUERY>> {}", c);
				});
				
				for(Annotation a : Cat.class.getAnnotations()) {
					LOGGER.info("ANNOTATION: {}", a.annotationType());
				}
			}
		};
	}
}
