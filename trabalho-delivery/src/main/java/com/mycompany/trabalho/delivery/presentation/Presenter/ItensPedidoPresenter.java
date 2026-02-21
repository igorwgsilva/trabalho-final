package com.mycompany.trabalho.delivery.presentation.Presenter;

import com.mycompany.trabalho.delivery.dominio.port.IProvedorDePrecos;
import java.util.List;

public class ItensPedidoPresenter {
    
    private final IProvedorDePrecos provedor;

    public ItensPedidoPresenter(IProvedorDePrecos provedor) {
        this.provedor = provedor;
    }

    public List<String> getPizzas() {
        return provedor.buscaPizzas();
    }

    public List<String> getBebidas() {
        return provedor.buscaBebidas();
    }

    public List<String> getIngredientes() {
        return provedor.buscaIngredientes();
    }
    
    public double buscarPreco(String nomeDoItem) {
        return provedor.buscaPreco(nomeDoItem);
    }
}