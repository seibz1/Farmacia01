package br.com.moicano.lojinha.model;

import java.time.LocalDateTime;

public class Pedido {

    private Integer id;
    private LocalDateTime data;
    private Double valorTotal;
    private String clienteNome;
    private String status; // Status para o entregador

    public Pedido() {
    }

    public Pedido(LocalDateTime data, Double valorTotal, String clienteNome, String status) {
        this.data = data;
        this.valorTotal = valorTotal;
        this.clienteNome = clienteNome;
        this.status = status;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Pedido ID: %d | Status: %s | Data: %s | Cliente: %s | Valor Total: R$ %.2f",
                id, status, data.toString(), clienteNome, valorTotal);
    }
}