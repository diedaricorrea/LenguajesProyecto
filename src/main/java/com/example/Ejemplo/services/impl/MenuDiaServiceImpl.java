package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.MenuDia;
import com.example.Ejemplo.repository.MenuDiaRepository;
import com.example.Ejemplo.services.MenuDiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuDiaServiceImpl implements MenuDiaService {

    private final MenuDiaRepository menuDia;

    @Override
    public List<MenuDia> findAllMenuDias() {
        return menuDia.findAll();
    }

    @Override
    public MenuDia saveMenudia(MenuDia menu) {
        return menuDia.save(menu);
    }
}
