package br.com.moicano.lojinha.model;

public class Produto {
    private Integer id;
    private String nome;
    private String descricao;
    private Double preco;
    private Integer quantidade;
    private Integer categoriaId;

    // Campos novos da farmácia
    private String dosagem;
    private boolean requerReceita;

    private String categoriaNome;

    public Produto() {}

    public Produto(String nome, String descricao, Double preco, Integer quantidade, Integer categoriaId, String dosagem, boolean requerReceita) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade;
        this.categoriaId = categoriaId;
        this.dosagem = dosagem;
        this.requerReceita = requerReceita;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }
    public String getDosagem() { return dosagem; }
    public void setDosagem(String dosagem) { this.dosagem = dosagem; }
    public boolean isRequerReceita() { return requerReceita; }
    public void setRequerReceita(boolean requerReceita) { this.requerReceita = requerReceita; }

    @Override
    public String toString() {
        String categoria = categoriaNome != null ? categoriaNome : "Sem categoria";
        String infoDosagem = (dosagem != null && !dosagem.isEmpty()) ? String.format(" (%s)", dosagem) : "";
        String infoReceita = requerReceita ? " [Receita Obrigatória]" : "";

        return String.format("ID: %d | Produto: %s%s | Preço: R$ %.2f | Estoque: %d | Categoria: %s%s",
                id, nome, infoDosagem, preco, quantidade, categoria, infoReceita);
    }
}