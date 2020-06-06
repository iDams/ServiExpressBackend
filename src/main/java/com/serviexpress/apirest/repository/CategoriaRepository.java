package com.serviexpress.apirest.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.serviexpress.apirest.entity.category.*;


@Repository("repositoriocategoria")
public interface  CategoriaRepository extends JpaRepository<Categoria, Serializable>, PagingAndSortingRepository<Categoria, Serializable>{
    Optional<Categoria> findById(Long id);
    public abstract Categoria findByNombre(String nombre);
    public abstract Page<Categoria> findAll(Pageable pageable);
}