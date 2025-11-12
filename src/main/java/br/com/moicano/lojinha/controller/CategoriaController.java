package br.com.moicano.lojinha.controller;

import br.com.moicano.lojinha.dao.CategoriaDAO;
import br.com.moicano.lojinha.model.Categoria;
import br.com.moicano.lojinha.view.CategoriaView;

import java.util.List;

public class CategoriaController {
    private final CategoriaDAO categoriaDAO;
    private final CategoriaView view;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
        this.view = new CategoriaView();
    }

    public void iniciar() {
        int opcao;
        do {
            opcao = view.exibirMenu();

            switch (opcao) {
                case 1 -> cadastrarCategoria();
                case 2 -> listarCategorias();
                case 3 -> buscarCategoria();
                case 4 -> atualizarCategoria();
                case 5 -> removerCategoria();
                case 0 -> {} // Voltar
                default -> view.exibirErro("Opção inválida!");
            }

        } while (opcao != 0);
    }

    private void cadastrarCategoria() {
        try {
            Categoria categoria = view.lerDadosCategoria();
            categoriaDAO.criar(categoria);
            view.exibirMensagemSucesso("Categoria cadastrada com sucesso!");
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void listarCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.buscarTodas();
            view.exibirListaCategorias(categorias);
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void buscarCategoria() {
        try {
            int id = view.lerIdCategoria();
            Categoria categoria = categoriaDAO.buscarPorId(id);
            view.exibirCategoria(categoria);
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void atualizarCategoria() {
        try {
            int id = view.lerIdCategoria();
            Categoria categoria = categoriaDAO.buscarPorId(id);

            if (categoria == null) {
                view.exibirErro("Categoria não encontrada!");
                return;
            }

            Categoria categoriaAtualizada = view.lerAtualizacaoCategoria(categoria);
            categoriaDAO.atualizar(categoriaAtualizada);
            view.exibirMensagemSucesso("Categoria atualizada com sucesso!");
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }

    private void removerCategoria() {
        try {
            int id = view.lerIdCategoria();
            Categoria categoria = categoriaDAO.buscarPorId(id);

            if (categoria == null) {
                view.exibirErro("Categoria não encontrada!");
                return;
            }

            if (view.confirmarRemocao(categoria)) {
                categoriaDAO.deletar(id);
                view.exibirMensagemSucesso("Categoria removida com sucesso!");
            } else {
                view.exibirErro("Operação cancelada.");
            }
        } catch (Exception e) {
            view.exibirErro(e.getMessage());
        }
    }
}