package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
    Author findAuthorById(Long id);
}
