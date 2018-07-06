package com.aermias.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.aermias.entity.Actor;

public interface ActorRepository extends CrudRepository<Actor, Long> {
	Actor findByActorId(Long id);
    List<Actor> findByName(String name);
}
