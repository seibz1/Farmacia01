package br.com.moicano.lojinha.view;

import java.util.Scanner;

public class MenuPrincipalView {
    private final Scanner scanner; // Objeto que "lê" o que o utilizador digita

    public MenuPrincipalView() {
        this.scanner = new Scanner(System.in);
    }

    // PONTO ESSENCIAL:
    // Este é o método principal que o App.java chama.
    // Ele apenas imprime o menu e espera um número.
    public int exibirMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("E-COMMERCE - SISTEMA DE GERENCIAMENTO (BIQUEIRA LEGAL)");
        System.out.println("=".repeat(50));

        // --- As 4 opções que representam os 3 perfis ---
        System.out.println("--- PERFIL DO CLIENTE ---");
        System.out.println("1 - Acessar a Loja (Cliente)");
        System.out.println("\n--- PERFIL DE ADMINISTRAÇÃO ---");
        System.out.println("2 - Gerenciar Produtos (Admin)");
        System.out.println("3 - Gerenciar Categorias (Admin)");
        System.out.println("4 - Painel do Entregador (Logística)");

        System.out.println("\n" + "=".repeat(50));
        System.out.println("0 - Sair");
        System.out.println("=".repeat(50));
        System.out.print("Escolha uma opção: ");

        return lerInteiro(); // Chama a função "ajudante" para ler um número
    }

    public void exibirMensagemEncerramento() {
        System.out.println("\nSaindo...");
    }

    public void exibirErro(String mensagem) {
        System.out.println("ERRO " + mensagem);
    }

    // PONTO CONFUSO (Helper Method):
    // Esta função "ajudante" é usada para ler um número de forma segura.
    private int lerInteiro() {
        try {
            // Tenta "converter" o texto que o utilizador digitou (ex: "1") para um número (1)
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            // Se o utilizador digitar "abc", o 'try' falha e o 'catch' é ativado.
            // Retornamos -1, que é uma "opção inválida" no nosso menu.
            return -1;
        }
    }

    // Fecha o 'scanner' quando o programa termina
    public void fechar() {
        scanner.close();
    }
}