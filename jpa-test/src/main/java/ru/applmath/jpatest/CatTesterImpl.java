package ru.applmath.jpatest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.applmath.jpatest.entities.Breed;
import ru.applmath.jpatest.entities.Cat;
import ru.applmath.jpatest.entities.Human;

public class CatTesterImpl implements CatTester {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CatTesterImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public void updateTheCat() {
		// Cat c = em.find(Cat.class, 1L);
		// if(!c.getName().endsWith("!!!")) {
		// c.setName(c.getName() + " !!!");
		// }

		Breed b = em.find(Breed.class, 1L);

		if (b == null) {
			b = new Breed();
			b.setId(1L);
			b.setName("Перс");
			b.setDescription("Пушистый");
			em.persist(b);
			b = new Breed();
			b.setId(2L);
			b.setName("Сфинкс");
			b.setDescription("Страшный");
			em.persist(b);

			Cat c = new Cat();
			c.setId(1L);
			c.setName("Васька");
			c.setAge(3);
			c.setBreed(b);
			em.persist(c);
			c = new Cat();
			c.setId(2L);
			c.setName("Мурка");
			c.setAge(5);
			c.setBreed(b);
			em.persist(c);
		}
	}

	@Override
	@Transactional
	public void display() {
		List<Cat> cats = (List<Cat>) em.createQuery("from Cat").getResultList();

		for (Cat c : cats) {
			LOGGER.info("{}", c);
			Breed b = c.getBreed();
			if (b != null) {
				LOGGER.info("    breed = {}", b);
			}
			List<Human> hl = c.getHumans();
			for(Human h : hl) {
				LOGGER.info("    human = {}", h);
			}
		}

		List<Breed> breeds = (List<Breed>) em.createQuery("from Breed")
				.getResultList();

		for (Breed b : breeds) {
			LOGGER.info("{}", b);
			/* List<Cat> */cats = b.getCats();
			for (Cat c : cats) {
				LOGGER.info("    cat = {}", c);
			}
		}
	}
}
