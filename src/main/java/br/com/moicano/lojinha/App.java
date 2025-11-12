package br.com.moicano.lojinha;

// Imports do Admin (já existiam)
import br.com.moicano.lojinha.controller.CategoriaController;
import br.com.moicano.lojinha.controller.ProdutoController;
import br.com.moicano.lojinha.database.DatabaseConnection;
import br.com.moicano.lojinha.view.MenuPrincipalView;

// Imports dos perfis novos
import br.com.moicano.lojinha.view.ClienteView;
import br.com.moicano.lojinha.view.EntregadorView;

public class App {
    public static void main(String[] args) {

        // 1. Cria o banco de dados (com TODAS as tabelas corretas)
        DatabaseConnection.initDatabase();

        // 2. Prepara TODOS os "controladores" e "telas" de que vamos precisar
        MenuPrincipalView menuView = new MenuPrincipalView();

        // Perfis de Admin
        ProdutoController produtoController = new ProdutoController();
        CategoriaController categoriaController = new CategoriaController();

        // Perfis novos
        ClienteView clienteView = new ClienteView();
        EntregadorView entregadorView = new EntregadorView();

        int opcao;
        do {
            // 3. Mostra o menu principal (que agora está atualizado)
            opcao = menuView.exibirMenu();

            // 4. Atualiza o "switch" para as novas opções
            switch (opcao) {
                case 1:
                    clienteView.iniciar(); // Inicia a "Loja" do Cliente
                    break;
                case 2:
                    produtoController.iniciar(); // Inicia o "Gerir Produtos" (Admin)
                    break;
                case 3:
                    categoriaController.iniciar(); // Inicia o "Gerir Categorias" (Admin)
                    break;
                case 4:
                    entregadorView.iniciar(); // Inicia o "Painel do Entregador"
                    break;
                case 0:
                    menuView.exibirMensagemEncerramento();
                    break;
                default:
                    menuView.exibirErro("Opção inválida!");
            }

        } while (opcao != 0);

        menuView.fechar();
    }
}