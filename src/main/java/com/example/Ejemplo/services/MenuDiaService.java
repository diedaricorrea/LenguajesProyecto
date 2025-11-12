package com.example.Ejemplo.services;

import com.example.Ejemplo.models.MenuDia;

import java.util.List;

public interface MenuDiaService {
    List<MenuDia> findAllMenuDias();

    MenuDia saveMenudia(MenuDia menu);
}
