package org.example.l6_20181484.repository;

import org.example.l6_20181484.entity.Dispositivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispositivosRepository extends JpaRepository<Dispositivos, Integer> {

}
