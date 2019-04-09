package ru.via.decode.service;

public interface Processable<Rq, Rs> {

    Rs processing(Rq request);
}
