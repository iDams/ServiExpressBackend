package com.serviexpress.apirest.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.serviexpress.apirest.entity.Satifaccion;



@Repository("repositorioSatifaccion")
public interface  SatifaccionRepository extends JpaRepository<Satifaccion, Serializable>, PagingAndSortingRepository<Satifaccion, Serializable>{

        long countByNombre(String nombre);
}