package com.creatival.tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface TagRepository extends JpaRepository<Tag, Long> {
	Optional<Tag> findByTagText(String tagText);
}
