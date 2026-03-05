package com.creatival.tag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.creatival.tag.Tag;
import com.creatival.user.Users;



public interface TagRepository extends JpaRepository<Tag, Long> {
	Optional<Tag> findByTagText(String tagText);
}
