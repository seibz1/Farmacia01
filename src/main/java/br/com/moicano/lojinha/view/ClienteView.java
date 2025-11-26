// Localização: src/main/java/br/com/moicano/lojinha/view/ClienteView.java
// VERSÃO FINAL: COM FAVORITOS E COMENTADA

package br.com.moicano.lojinha.view;

// Importa todos os "trabalhadores" (DAOs) e "moldes" (Models) necessários
import br.com.moicano.lojinha.dao.FavoritoDAO; // PONTO ESSENCIAL: Import do novo DAO
import br.com.moicano.lojinha.dao.ItemPedidoDAO;
import br.com.moicano.lojinha.dao.PedidoDAO;
import br.com.moicano.lojinha.dao.ProdutoDAO;
import br.com.moicano.lojinha.model.ItemPedido;
import br.com.moicano.lojinha.model.Pedido;
import br.com.moicano.lojinha.model.Produto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClienteView {
    private final Scanner scanner;

    // A tela do Cliente precisa de conhecer TODOS os "trabalhadores"
    private final ProdutoDAO produtoDAO;
    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final FavoritoDAO favoritoDAO; // PONTO ESSENCIAL: Novo trabalhador de favoritos

    // O "Carrinho" é uma lista temporária que vive apenas na memória desta tela
    private final List<ItemPedido> carrinho;

    public ClienteView() {
        this.scanner = new Scanner(System.in);
        // Inicializa todos os DAOs (Trabalhadores)
        this.produtoDAO = new ProdutoDAO();
        this.pedidoDAO = new PedidoDAO();
        this.itemPedidoDAO = new ItemPedidoDAO();
        this.favoritoDAO = new FavoritoDAO(); // Inicializa o FavoritoDAO

        this.carrinho = new ArrayList<>(); // Cria o carrinho vazio
    }

    // Loop principal do menu do Cliente
    public void iniciar() {
        int opcao;
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("BEM-VINDO À FARMÁCIA BIQUEIRA LEGAL!");
            System.out.println("=".repeat(50));
            System.out.println("1 - Ver produtos / Adicionar ao Carrinho");
            System.out.println("2 - Ver Carrinho");
            System.out.println("3 - Finalizar Compra");
            System.out.println("4 - Meus Favoritos ⭐"); // PONTO ESSENCIAL: Nova opção no menu
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.println("=".repeat(50));
            System.out.print("Escolha uma opção: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1 -> adicionarAoCarrinho();
                case 2 -> verCarrinho();
                case 3 -> finalizarCompra();
                case 4 -> gerenciarFavoritos(); // Chama o sub-menu de favoritos
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("ERRO: Opção inválida!");
            }
        } while (opcao != 0);
    }

    // --- PONTO ESSENCIAL: NOVO SUB-MENU DE FAVORITOS ---
    private void gerenciarFavoritos() {
        int opcao;
        do {
            System.out.println("\n--- ⭐ MEUS FAVORITOS ⭐ ---");

            // 1. Pede ao DAO para buscar a lista do banco
            List<Produto> favs = favoritoDAO.listarFavoritos();

            if (favs.isEmpty()) {
                System.out.println("(Sua lista de favoritos está vazia)");
            } else {
                // 2. Mostra cada produto favoritado
                favs.forEach(System.out::println);
            }

            System.out.println("\n1 - Adicionar Produto aos Favoritos");
            System.out.println("2 - Remover Produto dos Favoritos");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1 -> adicionarFavorito();
                case 2 -> removerFavorito();
                case 0 -> System.out.println("Voltando para a loja...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void adicionarFavorito() {
        System.out.print("Digite o ID do produto para favoritar: ");
        int id = lerInteiro();

        // Verifica se o produto existe antes de favoritar
        Produto p = produtoDAO.buscarPorId(id);
        if (p != null) {
            favoritoDAO.adicionar(id); // Chama o trabalhador para salvar
            System.out.println("SUCESSO: " + p.getNome() + " foi adicionado aos favoritos!");
        } else {
            System.out.println("ERRO: Produto não encontrado.");
        }
    }

    private void removerFavorito() {
        System.out.print("Digite o ID do produto para remover dos favoritos: ");
        int id = lerInteiro();
        favoritoDAO.remover(id); // Chama o trabalhador para apagar
        System.out.println("Produto removido dos favoritos (se existia).");
    }
    // --- FIM DA LÓGICA DE FAVORITOS ---

    // Lógica de Compra (Carrinho)
    private void adicionarAoCarrinho() {
        System.out.println("\n--- NOSSOS PRODUTOS ---");
        List<Produto> produtos = produtoDAO.buscarTodos();
        if (produtos.isEmpty()) {
            System.out.println("Desculpe, estamos sem estoque no momento.");
            return;
        }
        produtos.forEach(System.out::println);
        System.out.println("-------------------------");

        System.out.print("Digite o ID do produto que deseja adicionar (0 para cancelar): ");
        int idProduto = lerInteiro();
        if (idProduto == 0) return;

        Produto produtoEscolhido = produtoDAO.buscarPorId(idProduto);
        if (produtoEscolhido == null) {
            System.out.println("ERRO: Produto não encontrado!");
            return;
        }

        System.out.print("Digite a quantidade: ");
        int quantidade = lerInteiro();
        if (quantidade <= 0) {
            System.out.println("ERRO: Quantidade inválida.");
            return;
        }

        // Verifica o estoque disponível
        if (quantidade > produtoEscolhido.getQuantidade()) {
            System.out.println("ERRO: Desculpe, só temos " + produtoEscolhido.getQuantidade() + " em estoque.");
            return;
        }

        // Cria um item temporário e adiciona ao carrinho (na memória)
        ItemPedido item = new ItemPedido(null, idProduto, quantidade, produtoEscolhido.getPreco());
        item.setProdutoNome(produtoEscolhido.getNome());
        carrinho.add(item);

        System.out.println("SUCESSO: " + quantidade + "x " + produtoEscolhido.getNome() + " adicionado(s) ao carrinho!");
    }

    private void verCarrinho() {
        if (carrinho.isEmpty()) {
            System.out.println("\nO seu carrinho está vazio.");
            return;
        }

        System.out.println("\n--- MEU CARRINHO ---");
        double total = 0;
        for (ItemPedido item : carrinho) {
            System.out.println(item.toString());
            total += (item.getPrecoUnitario() * item.getQuantidade());
        }
        System.out.println("-------------------------");
        System.out.printf("VALOR TOTAL: R$ %.2f\n", total);
    }

    // PONTO MAIS IMPORTANTE: O Checkout (Finalizar Compra)
    private void finalizarCompra() {
        if (carrinho.isEmpty()) {
            System.out.println("ERRO: Seu carrinho está vazio. Adicione produtos primeiro.");
            return;
        }

        verCarrinho();

        System.out.print("\nDigite seu nome para o pedido: ");
        String nomeCliente = scanner.nextLine();

        double valorTotal = 0;
        for (ItemPedido item : carrinho) {
            valorTotal += (item.getPrecoUnitario() * item.getQuantidade());
        }

        try {
            // --- INÍCIO DA TRANSAÇÃO ---

            // 1. Define o status inicial para o Entregador ver depois
            String statusInicial = "AGUARDANDO";

            // 2. Cria o Recibo (Pedido) e salva no banco
            Pedido novoPedido = new Pedido(LocalDateTime.now(), valorTotal, nomeCliente, statusInicial);
            Integer pedidoId = pedidoDAO.criar(novoPedido); // Retorna o ID gerado (ex: 123)

            if (pedidoId == null) {
                throw new RuntimeException("Não foi possível criar o pedido.");
            }

            // 3. Loop para salvar cada item do carrinho
            for (ItemPedido item : carrinho) {
                // Liga o item ao ID do pedido (123)
                item.setPedidoId(pedidoId);
                itemPedidoDAO.criar(item);

                // PONTO CRÍTICO: Atualiza o estoque do produto
                Produto produtoComprado = produtoDAO.buscarPorId(item.getProdutoId());
                int novoEstoque = produtoComprado.getQuantidade() - item.getQuantidade();
                produtoComprado.setQuantidade(novoEstoque);
                produtoDAO.atualizar(produtoComprado); // Salva a nova quantidade no banco
            }

            // --- FIM DA TRANSAÇÃO ---

            System.out.println("\n" + "=".repeat(50));
            System.out.println("COMPRA FINALIZADA COM SUCESSO!");
            System.out.println("Obrigado, " + nomeCliente + "!");
            System.out.println("Seu pedido (Nº " + pedidoId + ") foi registrado com o status: " + statusInicial);
            System.out.println("=".repeat(50));

            carrinho.clear(); // Limpa o carrinho para a próxima compra

        } catch (Exception e) {
            System.out.println("ERRO CRÍTICO AO FINALIZAR COMPRA: " + e.getMessage());
        }
    }

    private int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}