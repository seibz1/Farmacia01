package br.com.moicano.lojinha.view;

import br.com.moicano.lojinha.model.Categoria;

import java.util.List;
import java.util.Scanner;

/**
 * Classe 'View' (Visão) para a entidade Categoria.
 * Esta classe é responsável por toda a interação com o usuário (console)
 * relacionada ao gerenciamento de categorias.
 * Ela NÃO contém regras de negócio nem acesso a dados (DAO).
 * Sua única função é:
 * 1. Exibir menus e informações para o usuário.
 * 2. Ler dados (input) do usuário.
 */
public class CategoriaView {

    // Objeto Scanner para ler a entrada do console.
    // É final porque será inicializado uma vez no construtor e nunca mudará.
    private final Scanner scanner;

    /**
     * Construtor da View.
     * Inicializa o Scanner para que a classe possa ler do System.in (console).
     */
    public CategoriaView() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Exibe o menu principal de gerenciamento de categorias.
     * @return A opção (int) escolhida pelo usuário.
     */
    public int exibirMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GERENCIAMENTO DE CATEGORIAS");
        System.out.println("=".repeat(50));
        System.out.println("1 - Cadastrar Categoria");
        System.out.println("2 - Listar Categorias");
        System.out.println("3 - Buscar Categoria por ID");
        System.out.println("4 - Atualizar Categoria");
        System.out.println("5 - Remover Categoria");
        System.out.println("0 - Voltar");
        System.out.println("=".repeat(50));
        System.out.print("Escolha uma opção: ");

        // Usa o método helper 'lerInteiro' para ler a entrada de forma segura.
        return lerInteiro();
    }

    /**
     * Solicita ao usuário os dados para criar uma NOVA categoria.
     * @return Um objeto Categoria (novo) preenchido com os dados lidos.
     */
    public Categoria lerDadosCategoria() {
        System.out.println("\n--- CADASTRAR CATEGORIA ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        // Retorna um novo objeto Categoria usando o construtor
        // que recebe nome e descrição (o ID ainda não existe).
        return new Categoria(nome, descricao);
    }

    /**
     * Método auxiliar genérico para solicitar um ID de categoria.
     * Usado por "Buscar", "Atualizar" e "Remover".
     * @return O ID (int) lido.
     */
    public int lerIdCategoria() {
        System.out.print("Digite o ID da categoria: ");
        return lerInteiro();
    }

    /**
     * Solicita ao usuário os dados para ATUALIZAR uma categoria existente.
     * @param categoriaAtual O objeto Categoria com os dados atuais (antes da edição).
     * @return O *mesmo* objeto Categoria, mas agora modificado com os novos dados.
     */
    public Categoria lerAtualizacaoCategoria(Categoria categoriaAtual) {
        System.out.println("\n--- ATUALIZAR CATEGORIA ---");
        // Mostra os dados atuais (usando o toString() da Categoria).
        System.out.println("Categoria atual: " + categoriaAtual);
        System.out.println("\nDigite os novos dados (Enter para manter o atual):");

        // Lógica de atualização:
        // Pede o novo nome, mostrando o atual entre colchetes.
        System.out.print("Nome [" + categoriaAtual.getNome() + "]: ");
        String nome = scanner.nextLine();
        // Se o usuário digitou algo (a string não está vazia), atualiza o objeto.
        if (!nome.isEmpty()) categoriaAtual.setNome(nome);

        // Repete a lógica para a descrição.
        System.out.print("Descrição [" + categoriaAtual.getDescricao() + "]: ");
        String descricao = scanner.nextLine();
        if (!descricao.isEmpty()) categoriaAtual.setDescricao(descricao);

        // Retorna o objeto que foi passado como parâmetro, agora modificado.
        return categoriaAtual;
    }

    /**
     * Pede confirmação (S/N) ao usuário antes de uma operação destrutiva (remoção).
     * @param categoria A categoria que será removida.
     * @return true se o usuário digitou "S" (sim), false caso contrário.
     */
    public boolean confirmarRemocao(Categoria categoria) {
        System.out.println("\n--- REMOVER CATEGORIA ---");
        System.out.println("Categoria: " + categoria);
        System.out.print("Confirma a remoção? (S/N): ");
        String confirmacao = scanner.nextLine();
        // Retorna o resultado da comparação (ignorando maiúsculas/minúsculas).
        return confirmacao.equalsIgnoreCase("S");
    }

    /**
     * Exibe os dados de uma única categoria.
     * @param categoria O objeto Categoria a ser exibido.
     */
    public void exibirCategoria(Categoria categoria) {
        // Verifica se a categoria não é nula (ex: se a busca por ID falhou).
        if (categoria != null) {
            // Usa o método toString() da Categoria para imprimi-la.
            System.out.println("\n" + categoria);
        } else {
            // Informa ao usuário que a categoria não foi encontrada.
            exibirErro("Categoria não encontrada!");
        }
    }

    /**
     * Exibe uma lista de categorias.
     * @param categorias A lista (List<Categoria>) a ser exibida.
     */
    public void exibirListaCategorias(List<Categoria> categorias) {
        System.out.println("\n--- LISTA DE CATEGORIAS ---");
        // Verifica se a lista está vazia.
        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
        } else {
            // Se não estiver vazia, itera sobre a lista (usando forEach)
            // e imprime cada categoria (usando System.out::println,
            // que chama o toString() de cada objeto).
            categorias.forEach(System.out::println);
        }
    }

    /**
     * Exibe uma mensagem de sucesso padronizada.
     * @param mensagem A mensagem de sucesso específica (ex: "Categoria cadastrada!").
     */
    public void exibirMensagemSucesso(String mensagem) {
        System.out.println("\nSUCESSO: " + mensagem);
    }

    /**
     * Exibe uma mensagem de erro padronizada.
     * @param mensagem A mensagem de erro específica (ex: "ID inválido!").
     */
    public void exibirErro(String mensagem) {
        System.out.println("\nERRO: " + mensagem);
    }

    /**
     * Método auxiliar (privado) para ler um número inteiro do console de forma segura.
     * @return O número inteiro lido. Se o usuário digitar um texto inválido
     * (ex: "abc"), retorna -1 para evitar que o programa quebre.
     */
    private int lerInteiro() {
        try {
            // Tenta converter a linha lida (String) para um Inteiro (int).
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            // Se a conversão falhar (ex: usuário digitou "abc"),
            // captura a exceção, exibe um erro e retorna -1.
            exibirErro("Entrada inválida. Por favor, digite um número.");
            return -1; // -1 é uma opção inválida no menu, forçando o usuário a tentar de novo.
        }
    }
}