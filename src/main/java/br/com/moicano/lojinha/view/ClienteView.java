package br.com.moicano.lojinha.view;

// Importa todos os "trabalhadores" (DAOs) e "moldes" (Models) necessários
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

    // PONTO ESSENCIAL: A tela do Cliente precisa de conhecer
    // TODOS os "trabalhadores" (DAOs) para funcionar.
    private final ProdutoDAO produtoDAO;
    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;

    // PONTO CONFUSO: O "Carrinho" não é guardado no banco.
    // É apenas uma lista temporária (ArrayList) que vive
    // dentro desta tela, enquanto o cliente está a comprar.
    private final List<ItemPedido> carrinho;

    public ClienteView() {
        this.scanner = new Scanner(System.in);

        // Quando a tela "nasce", ela cria as suas próprias instâncias dos DAOs
        this.produtoDAO = new ProdutoDAO();
        this.pedidoDAO = new PedidoDAO();
        this.itemPedidoDAO = new ItemPedidoDAO();

        // E cria um carrinho vazio
        this.carrinho = new ArrayList<>();
    }

    // O loop principal da "Loja"
    public void iniciar() {
        int opcao;
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("BEM-VINDO À FARMÁCIA BIQUEIRA LEGAL!");
            System.out.println("=".repeat(50));
            System.out.println("1 - Ver produtos e Adicionar ao Carrinho");
            System.out.println("2 - Ver Carrinho");
            System.out.println("3 - Finalizar Compra");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.println("=".repeat(50));
            System.out.print("Escolha uma opção: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1 -> adicionarAoCarrinho();
                case 2 -> verCarrinho();
                case 3 -> finalizarCompra();
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("ERRO: Opção inválida!");
            }
        } while (opcao != 0);
    }

    // Passo 1: O cliente vê os produtos e escolhe
    private void adicionarAoCarrinho() {
        System.out.println("\n--- NOSSOS PRODUTOS ---");
        // 1. Usa o "trabalhador" de produtos para buscar tudo do banco
        List<Produto> produtos = produtoDAO.buscarTodos();
        if (produtos.isEmpty()) {
            System.out.println("Desculpe, estamos sem estoque no momento.");
            return;
        }
        produtos.forEach(System.out::println); // Imprime cada produto
        System.out.println("-------------------------");

        // 2. Pede ao cliente o ID e a quantidade
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

        // 3. PONTO ESSENCIAL: Verifica se temos stock suficiente
        if (quantidade > produtoEscolhido.getQuantidade()) {
            System.out.println("ERRO: Desculpe, só temos " + produtoEscolhido.getQuantidade() + " em estoque.");
            return;
        }

        // 4. Cria um "Item" (linha do recibo) temporário
        ItemPedido item = new ItemPedido(null, idProduto, quantidade, produtoEscolhido.getPreco());
        item.setProdutoNome(produtoEscolhido.getNome()); // Guarda o nome para mostrar no carrinho

        // 5. Adiciona o item à lista (carrinho) temporária
        carrinho.add(item);

        System.out.println("SUCESSO: " + quantidade + "x " + produtoEscolhido.getNome() + " adicionado(s) ao carrinho!");
    }

    // Passo 2: O cliente vê o que já escolheu
    private void verCarrinho() {
        if (carrinho.isEmpty()) {
            System.out.println("\nO seu carrinho está vazio.");
            return;
        }

        System.out.println("\n--- MEU CARRINHO ---");
        double total = 0;
        // Passa por cada item no carrinho e calcula o subtotal
        for (ItemPedido item : carrinho) {
            System.out.println(item.toString()); // Usa o toString() do ItemPedido.java
            total += (item.getPrecoUnitario() * item.getQuantidade());
        }
        System.out.println("-------------------------");
        System.out.printf("VALOR TOTAL: R$ %.2f\n", total);
    }

    // PONTO MAIS IMPORTANTE E CONFUSO DO PROJETO:
    // O "Checkout" (Finalizar a Compra)
    private void finalizarCompra() {
        if (carrinho.isEmpty()) {
            System.out.println("ERRO: Seu carrinho está vazio. Adicione produtos primeiro.");
            return;
        }

        verCarrinho(); // Mostra o resumo

        System.out.print("\nDigite seu nome para o pedido: ");
        String nomeCliente = scanner.nextLine();

        // Calcula o valor total final
        double valorTotal = 0;
        for (ItemPedido item : carrinho) {
            valorTotal += (item.getPrecoUnitario() * item.getQuantidade());
        }

        try {
            // --- INÍCIO DA "TRANSAÇÃO" (A MÁGICA) ---

            // 1. Define o status inicial (para o Entregador)
            String statusInicial = "AGUARDANDO";

            // 2. Cria o "Molde" do Recibo (Pedido) principal
            Pedido novoPedido = new Pedido(LocalDateTime.now(), valorTotal, nomeCliente, statusInicial);

            // 3. Salva o Recibo no banco e Pega o ID (ex: 123)
            //    (Aqui usamos o método especial do PedidoDAO)
            Integer pedidoId = pedidoDAO.criar(novoPedido);

            // 4. Se o ID não foi criado, dá erro.
            if (pedidoId == null) {
                throw new RuntimeException("Não foi possível criar o pedido.");
            }

            // 5. Loop: Passa por cada item no "carrinho" temporário
            for (ItemPedido item : carrinho) {
                // 5a. "Liga" o item ao recibo (usando o ID 123)
                item.setPedidoId(pedidoId);
                // 5b. Salva a "linha" (ItemPedido) no banco
                itemPedidoDAO.criar(item);

                // 5c. BÓNUS ESSENCIAL: Atualiza o stock do produto original
                Produto produtoComprado = produtoDAO.buscarPorId(item.getProdutoId());
                int novoEstoque = produtoComprado.getQuantidade() - item.getQuantidade();
                produtoComprado.setQuantidade(novoEstoque);
                produtoDAO.atualizar(produtoComprado); // Salva o produto com o novo stock
            }

            // --- FIM DA "TRANSAÇÃO" ---

            System.out.println("\n" + "=".repeat(50));
            System.out.println("COMPRA FINALIZADA COM SUCESSO!");
            System.out.println("Obrigado, " + nomeCliente + "!");
            System.out.println("Seu pedido (Nº " + pedidoId + ") foi registrado com o status: " + statusInicial);
            System.out.println("=".repeat(50));

            carrinho.clear(); // Esvazia o carrinho para a próxima compra

        } catch (Exception e) {
            System.out.println("ERRO CRÍTICO AO FINALIZAR COMPRA: " + e.getMessage());
        }
    }

    // Função "ajudante" para ler números
    private int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Retorna -1 se o utilizador digitar "abc"
        }
    }
}