package br.com.moicano.lojinha.view;

import br.com.moicano.lojinha.dao.PedidoDAO;
import br.com.moicano.lojinha.model.Pedido;

import java.util.List;
import java.util.Scanner;

public class EntregadorView {

    private final Scanner scanner;
    private final PedidoDAO pedidoDAO; // O Entregador só precisa mexer com Pedidos

    public EntregadorView() {
        this.scanner = new Scanner(System.in);
        this.pedidoDAO = new PedidoDAO();
    }

    // Este é o loop principal do Painel do Entregador
    public void iniciar() {
        int opcao;
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("PAINEL DO ENTREGADOR");
            System.out.println("=".repeat(50));
            System.out.println("1 - Ver Pedidos Ativos (Aguardando / Em Rota)");
            System.out.println("2 - Atualizar Status de um Pedido");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.println("=".repeat(50));
            System.out.print("Escolha uma opção: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1 -> listarPedidosAtivos();
                case 2 -> atualizarStatusPedido();
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("ERRO: Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void listarPedidosAtivos() {
        // Usa o método novo que criamos no PedidoDAO
        List<Pedido> pedidos = pedidoDAO.buscarPedidosAtivos();

        if (pedidos.isEmpty()) {
            System.out.println("\nNenhum pedido ativo no momento.");
            return;
        }

        System.out.println("\n--- PEDIDOS ATIVOS ---");
        // O toString() do Pedido.java já mostra o status!
        pedidos.forEach(System.out::println);
    }

    private void atualizarStatusPedido() {
        System.out.print("\nDigite o ID do pedido que deseja atualizar: ");
        int pedidoId = lerInteiro();
        if (pedidoId == -1) {
            System.out.println("ERRO: ID inválido.");
            return;
        }

        // Mostra as opções para o entregador
        System.out.println("\n--- ATUALIZAR STATUS ---");
        System.out.println("1 - Marcar como EM ROTA");
        System.out.println("2 - Marcar como ENTREGUE");
        System.out.println("0 - Cancelar");
        System.out.print("Escolha o novo status: ");

        int opcao = lerInteiro();
        String novoStatus = null;

        switch (opcao) {
            case 1 -> novoStatus = "EM ROTA";
            case 2 -> novoStatus = "ENTREGUE";
            case 0 -> {
                System.out.println("Atualização cancelada.");
                return;
            }
            default -> {
                System.out.println("ERRO: Opção inválida.");
                return;
            }
        }

        // Usa o método novo que criamos no PedidoDAO
        try {
            pedidoDAO.atualizarStatus(pedidoId, novoStatus);
            System.out.println("\nSUCESSO: Pedido ID " + pedidoId + " foi atualizado para " + novoStatus + ".");
        } catch (Exception e) {
            System.out.println("ERRO: Não foi possível atualizar o pedido: " + e.getMessage());
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