package br.com.moicano.lojinha.controller;

import br.com.moicano.lojinha.dao.CategoriaDAO;
import br.com.moicano.lojinha.dao.ProdutoDAO;
import br.com.moicano.lojinha.model.Categoria;
import br.com.moicano.lojinha.model.Produto;
import br.com.moicano.lojinha.view.ProdutoView;

import java.util.List;

public class ProdutoController {
    private final ProdutoDAO produtoDAO;
    private final CategoriaDAO categoriaDAO;
    private final ProdutoView view;

    public ProdutoController() {
        this.produtoDAO = new ProdutoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.view = new ProdutoView();
    }

    public void iniciar() {
        int opcao;
        do {
            opcao = view.exibirMenu();

            switch (opcao) {
                case 1 -> cadastrarProduto();
                case 2 -> listarProdutos();
                case 3 -> buscarProduto();
                case 4 -> atualizarProduto();
                case 5 -> removerProduto();
                case 6 -> listarProdutosPorCategoria();
                case 0 -> {} // Voltar
                default -> view.exibirErro("Opção inválida!");
            }

        } while (opcao != 0);
    }

    private void cadastrarProduto() {
        try {
            List<Categoria> categorias = categoriaDAO.buscarTodas();
            Produto produto = view.lerDadosProduto(categorias);
            produtoDAO.criar(produto);
            view.exibirMensagemSucesso("Produto cadastrado com sucesso!");
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void listarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.buscarTodos();
            view.exibirListaProdutos(produtos);
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void buscarProduto() {
        try {
            int id = view.lerIdProduto();
            Produto produto = produtoDAO.buscarPorId(id);
            view.exibirProduto(produto);
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void atualizarProduto() {
        try {
            int id = view.lerIdProduto();
            Produto produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                view.exibirErro("Produto não encontrado!");
                return;
            }

            List<Categoria> categorias = categoriaDAO.buscarTodas();
            Produto produtoAtualizado = view.lerAtualizacaoProduto(produto, categorias);
            produtoDAO.atualizar(produtoAtualizado);
            view.exibirMensagemSucesso("Produto atualizado com sucesso!");
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void removerProduto() {
        try {
            int id = view.lerIdProduto();
            Produto produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                view.exibirErro("Produto não encontrado!");
                return;
            }

            if (view.confirmarRemocao(produto)) {
                produtoDAO.deletar(id);
                view.exibirMensagemSucesso("Produto removido com sucesso!");
            } else {
                view.exibirErro("Operação cancelada.");
            }
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void listarProdutosPorCategoria() {
        try {
            int categoriaId = view.lerIdCategoria();
            List<Produto> produtos = produtoDAO.buscarPorCategoria(categoriaId);
            view.exibirListaProdutos(produtos);
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }
}