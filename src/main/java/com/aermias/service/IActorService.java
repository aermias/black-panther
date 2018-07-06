package com.aermias.service;

import java.util.List;

import com.aermias.entity.Actor;

public interface IActorService {
    List<Actor> getAllActors();
    Actor getActorById(long actorId);
    boolean addActor(Actor actor);
    void updateActor(Actor actor);
    void deleteActor(Actor actor);
}
