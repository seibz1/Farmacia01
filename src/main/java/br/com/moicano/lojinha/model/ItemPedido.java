package br.com.moicano.lojinha.model;

public class ItemPedido {

    private Integer id;
    private Integer pedidoId;    // A qual "recibo" (Pedido) esta linha pertence
    private Integer produtoId;   // Qual "produto" é esta linha
    private Integer quantidade;  // Quantos o cliente comprou
    private Double precoUnitario; // Qual era o preço na hora da compra

    private String produtoNome;

    public ItemPedido() {
    }

    public ItemPedido(Integer pedidoId, Integer produtoId, Integer quantidade, Double precoUnitario) {
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }
    public Integer getProdutoId() { return produtoId; }
    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }
    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    @Override
    public String toString() {
        String nome = (produtoNome != null) ? produtoNome : "ID Produto: " + produtoId;

        return String.format("  - Item: %s | Qtd: %d | Preço Unit.: R$ %.2f | Subtotal: R$ %.2f",
                nome, quantidade, precoUnitario, (quantidade * precoUnitario));
    }
}