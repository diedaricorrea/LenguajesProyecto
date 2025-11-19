package com.example.Ejemplo.services;

import com.example.Ejemplo.models.MenuDia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MenuDiaService {
    List<MenuDia> findAllMenuDias();
    List<MenuDia> findAllOrderByFechaDesc();
    List<MenuDia> findMenusDelDia(LocalDate fecha);
    List<MenuDia> findMenusFuturos();
    Optional<MenuDia> findById(Integer id);
    MenuDia saveMenudia(MenuDia menu);
    void deleteById(Integer id);
}
