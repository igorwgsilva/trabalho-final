/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalho.delivery.aplicacao.dto;

/**
 *
 * @author André
 */
public class ItemPreviewDTO {
    private String descricao;
    private double preco;

    public ItemPreviewDTO(String descricao, double preco) {
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
}
