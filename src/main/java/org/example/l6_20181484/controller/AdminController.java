package org.example.l6_20181484.controller;

import org.example.l6_20181484.entity.Dispositivos;
import org.example.l6_20181484.repository.DispositivosRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    final DispositivosRepository dispositivosRepository;

    public AdminController(DispositivosRepository dispositivosRepository) {
        this.dispositivosRepository = dispositivosRepository;
    }
    @GetMapping(value = {"/dispositivos"})
    public String listarEmpleados(Model model) {

        List<Dispositivos> lista = dispositivosRepository.findAll();
        model.addAttribute("dispositivoList", lista);

        return "admin/listaDispositivos";
    }


}
