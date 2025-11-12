package br.com.moicano.lojinha.view;

import br.com.moicano.lojinha.model.Categoria;
import br.com.moicano.lojinha.model.Produto;

import java.util.List;
import java.util.Scanner;

public class ProdutoView {
    private final Scanner scanner;

    public ProdutoView() {
        this.scanner = new Scanner(System.in);
    }

    // Mostra o sub-menu do "Gerenciamento de Produtos"
    public int exibirMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GERENCIAMENTO DE PRODUTOS");
        System.out.println("=".repeat(50));
        System.out.println("1 - Cadastrar Produto");
        System.out.println("2 - Listar Produtos");
        System.out.println("3 - Buscar Produto por ID");
        System.out.println("4 - Atualizar Produto");
        System.out.println("5 - Remover Produto");
        System.out.println("6 - Listar Produtos por Categoria");
        System.out.println("0 - Voltar");
        System.out.println("=".repeat(50));
        System.out.print("Escolha uma opção: ");

        return lerInteiro();
    }

    // PONTO ESSENCIAL 1: "CRIAR" (Create)
    // Esta função é o "formulário" para criar um produto novo.
    public Produto lerDadosProduto(List<Categoria> categorias) {
        System.out.println("\n--- CADASTRAR PRODUTO ---");

        // 1. Pergunta os dados básicos
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();
        System.out.print("Preço: ");
        double preco = lerDouble();
        System.out.print("Quantidade em estoque: ");
        int quantidade = lerInteiro();

        // 2. PONTO ESSENCIAL (O que adicionámos):
        // Pergunta os campos novos da farmácia
        System.out.print("Dosagem (ex: 500mg, 25ml, ou deixe em branco): ");
        String dosagem = scanner.nextLine();
        System.out.print("Requer receita? (S/N): ");
        String receitaStr = scanner.nextLine();
        // Converte a resposta (S/N) para um 'boolean' (true/false)
        boolean requerReceita = receitaStr.equalsIgnoreCase("S");

        // 3. Pergunta a categoria
        Integer categoriaId = selecionarCategoria(categorias);

        // 4. PONTO ESSENCIAL (O que mudou):
        // Retorna um "molde" de Produto preenchido com a "fábrica" (construtor)
        // nova de 7 argumentos que nós criámos no Produto.java.
        return new Produto(nome, descricao, preco, quantidade, categoriaId, dosagem, requerReceita);
    }

    // PONTO ESSENCIAL 2: "ATUALIZAR" (Update)
    // Esta função é o "formulário" para editar um produto que já existe.
    public Produto lerAtualizacaoProduto(Produto produtoAtual, List<Categoria> categorias) {
        System.out.println("\n--- ATUALIZAR PRODUTO ---");
        System.out.println("Produto atual: " + produtoAtual); // Mostra o produto (usa o toString())
        System.out.println("\nDigite os novos dados (Enter para manter o atual):");

        // PONTO CONFUSO (Lógica de Atualização):
        // Pede o "Nome" novo. Se o utilizador carregar "Enter" (string vazia),
        // o 'if' falha e o nome antigo é mantido.
        System.out.print("Nome [" + produtoAtual.getNome() + "]: ");
        String nome = scanner.nextLine();
        if (!nome.isEmpty()) produtoAtual.setNome(nome);

        System.out.print("Descrição [" + produtoAtual.getDescricao() + "]: ");
        String descricao = scanner.nextLine();
        if (!descricao.isEmpty()) produtoAtual.setDescricao(descricao);

        System.out.print("Preço [" + produtoAtual.getPreco() + "]: ");
        String precoStr = scanner.nextLine();
        if (!precoStr.isEmpty()) produtoAtual.setPreco(Double.parseDouble(precoStr));

        System.out.print("Quantidade [" + produtoAtual.getQuantidade() + "]: ");
        String quantidadeStr = scanner.nextLine();
        if (!quantidadeStr.isEmpty()) produtoAtual.setQuantidade(Integer.parseInt(quantidadeStr));

        // PONTO ESSENCIAL (O que adicionámos):
        // A mesma lógica de atualização para os campos novos
        System.out.print("Dosagem [" + (produtoAtual.getDosagem() != null ? produtoAtual.getDosagem() : "") + "]: ");
        String dosagem = scanner.nextLine();
        if (!dosagem.isEmpty()) produtoAtual.setDosagem(dosagem);

        String receitaAtual = produtoAtual.isRequerReceita() ? "Sim" : "Não";
        System.out.print("Requer receita? [" + receitaAtual + "] (S/N ou Enter para manter): ");
        String receitaStr = scanner.nextLine();

        if (receitaStr.equalsIgnoreCase("S")) {
            produtoAtual.setRequerReceita(true);
        } else if (receitaStr.equalsIgnoreCase("N")) {
            produtoAtual.setRequerReceita(false);
        }

        // Lógica para alterar a categoria
        System.out.print("Deseja alterar a categoria? (S/N): ");
        String alterarCategoria = scanner.nextLine();
        if (alterarCategoria.equalsIgnoreCase("S")) {
            Integer categoriaId = selecionarCategoria(categorias);
            produtoAtual.setCategoriaId(categoriaId);
        }

        return produtoAtual; // Retorna o objeto "produtoAtual" modificado
    }

    // PONTO CONFUSO (Helper Method):
    // Uma função "ajudante" para mostrar as categorias e ler a escolha.
    public Integer selecionarCategoria(List<Categoria> categorias) {
        if (categorias.isEmpty()) {
            System.out.println("\n Nenhuma categoria cadastrada. Produto será criado sem categoria.");
            return null; // Retorna 'null' (vazio) se não há categorias
        }
        System.out.println("\n--- SELECIONAR CATEGORIA ---");
        System.out.println("0 - Sem categoria");
        for (Categoria cat : categorias) {
            System.out.println(cat.getId() + " - " + cat.getNome());
        }
        System.out.print("Digite o ID da categoria: ");
        int id = lerInteiro();

        // PONTO CONFUSO (Operador Ternário):
        // Isto é um "if" numa linha só.
        // Lê-se: "Se 'id' for igual a 0, retorne 'null'. Senão, retorne o 'id'."
        return id == 0 ? null : id;
    }

    // --- Outros métodos (Ler, Apagar, Exibir) ---

    public int lerIdProduto() {
        System.out.print("Digite o ID do produto: ");
        return lerInteiro();
    }

    public int lerIdCategoria() {
        System.out.print("Digite o ID da categoria: ");
        return lerInteiro();
    }

    // "LER" (Read)
    public void exibirListaProdutos(List<Produto> produtos) {
        System.out.println("\n--- LISTA DE PRODUTOS ---");
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            // PONTO ESSENCIAL:
            // O 'toString()' do Produto.java (que fizemos no Baby Step 1)
            // já sabe como imprimir os campos novos (dosagem, etc.)!
            produtos.forEach(System.out::println);
        }
    }

    // "LER" (Read) - Buscar por ID
    public void exibirProduto(Produto produto) {
        if (produto != null) {
            System.out.println("\n" + produto);
        } else {
            exibirErro("Produto não encontrado!");
        }
    }

    // "APAGAR" (Delete)
    public boolean confirmarRemocao(Produto produto) {
        System.out.println("\n--- REMOVER PRODUTO ---");
        System.out.println("Produto: " + produto);
        System.out.print("Confirma a remoção? (S/N): ");
        String confirmacao = scanner.nextLine();
        return confirmacao.equalsIgnoreCase("S");
    }

    // --- Funções "ajudantes" de feedback ---

    public void exibirMensagemSucesso(String mensagem) {
        System.out.println("SUCESSO:" + mensagem);
    }

    public void exibirErro(String mensagem) {
        System.out.println("ERRO: " + mensagem);
    }

    // --- Funções "ajudantes" de leitura ---

    private int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double lerDouble() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}