package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.MenuDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuDiaRepository extends JpaRepository<MenuDia, Integer> {
}
