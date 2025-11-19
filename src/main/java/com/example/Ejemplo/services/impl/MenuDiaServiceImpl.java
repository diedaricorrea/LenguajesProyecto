package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.MenuDia;
import com.example.Ejemplo.repository.MenuDiaRepository;
import com.example.Ejemplo.services.MenuDiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuDiaServiceImpl implements MenuDiaService {

    private final MenuDiaRepository menuDia;

    @Override
    public List<MenuDia> findAllMenuDias() {
        return menuDia.findAll();
    }
    
    @Override
    public List<MenuDia> findAllOrderByFechaDesc() {
        return menuDia.findAllOrderByFechaDesc();
    }
    
    @Override
    public List<MenuDia> findMenusDelDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return menuDia.findByFechaBetween(inicio, fin);
    }
    
    @Override
    public List<MenuDia> findMenusFuturos() {
        return menuDia.findByFechaAfter(LocalDateTime.now());
    }
    
    @Override
    public Optional<MenuDia> findById(Integer id) {
        return menuDia.findById(id);
    }

    @Override
    @Transactional
    public MenuDia saveMenudia(MenuDia menu) {
        return menuDia.save(menu);
    }
    
    @Override
    @Transactional
    public void deleteById(Integer id) {
        menuDia.deleteById(id);
    }
}
