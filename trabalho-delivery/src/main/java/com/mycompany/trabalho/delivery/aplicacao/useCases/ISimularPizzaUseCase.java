/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.trabalho.delivery.aplicacao.useCases;

import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPreviewDTO;
import java.util.List;

/**
 *
 * @author André
 */
public interface ISimularPizzaUseCase{
    ItemPreviewDTO executar(String sabor, List<String> adicionais);
}
