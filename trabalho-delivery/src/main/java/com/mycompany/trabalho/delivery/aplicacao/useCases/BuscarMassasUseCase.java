/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalho.delivery.aplicacao.useCases;

import com.mycompany.trabalho.delivery.dominio.port.IProvedorDePrecos;
import java.util.List;

/**
 *
 * @author Yuri
 */
public class BuscarMassasUseCase implements IBuscarMassasUseCase{
    private final IProvedorDePrecos provedor;
    
    public BuscarMassasUseCase(IProvedorDePrecos provedor) {
        this.provedor = provedor;
    }
    
    @Override
    public List<String> executar() {
        return provedor.buscaMassas();
    }
}
