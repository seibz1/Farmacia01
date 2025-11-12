# 🚀 Projeto Lojinha - Sistema de E-commerce de Farmácia

Este é um projeto académico de um sistema de gestão para um e-commerce de farmácia (Biqueira Legal), simulado inteiramente via consola. O sistema foi desenvolvido em Java, utilizando o padrão DAO para acesso a dados e um banco de dados H2 em memória.

O projeto cumpre os requisitos de gerir múltiplos perfis de utilizador, implementando os fluxos de **Administrador (Lojista)**, **Cliente (Usuário)** e **Entregador**.

## 💻 Funcionalidades Implementadas

O sistema está dividido em três perfis principais, acessíveis através do menu inicial:

### 1. 🛒 Perfil de Cliente (Usuário)
O fluxo de compra completo do cliente:
* Visualizar todos os produtos disponíveis (com stock).
* Adicionar produtos a um carrinho de compras temporário.
* Ver o carrinho de compras e o valor total.
* Finalizar a compra, registando um novo **Pedido** com o status "AGUARDANDO".
* O stock dos produtos é atualizado (diminuído) automaticamente após a compra.

### 2. 👨‍💼 Perfil de Administrador (Lojista)
O fluxo de gestão de inventário:
* **Gerenciamento de Produtos (CRUD completo):**
    * Criar, Listar, Atualizar e Remover produtos.
    * Inclui campos personalizados para farmácia, como `dosagem` e `requerReceita`.
* **Gerenciamento de Categorias (CRUD completo):**
    * Criar, Listar, Atualizar e Remover categorias para organizar os produtos.

### 3. 🚚 Perfil de Entregador (Logística)
O fluxo de logística e entrega:
* Visualizar uma lista de todos os pedidos ativos (com status "AGUARDANDO" ou "EM ROTA").
* Atualizar o status de um pedido (de "AGUARDANDO" -> "EM ROTA" -> "ENTREGUE").
* Simula o processo de entrega desde a loja até ao cliente.

## ⚙️ Tecnologias e Ferramentas

* **Java:** Linguagem principal da aplicação.
* **JDBC:** Para a conectividade com o banco de dados.
* **H2 Database (In-Memory):** Banco de dados leve e em memória para facilitar os testes e a execução.
* **Maven:** Gestor de dependências (para incluir o driver H2).
* **Padrão DAO (Data Access Object):** Para separar a lógica de negócio das regras de acesso ao banco de dados.
* **🤖 Assistência de IA:** Uma ferramenta de IA (Gemini) foi utilizada para auxiliar na refatoração, depuração (debug), indentação do código e geração de documentação.

## 📚 Como Executar

1.  Abra o projeto na sua IDE (ex: IntelliJ IDEA).
2.  (Se for a primeira vez) Aguarde o Maven carregar a dependência do H2 (definida no `pom.xml`).
3.  Encontre o ficheiro principal `App.java` na localização:
    `src/main/java/br/com/moicano/lojinha/App.java`
4.  Execute o método `main()` deste ficheiro (clicando no "Play").
5.  O menu principal com os três perfis será exibido na consola.

## 💡 Nota Importante sobre o Banco de Dados H2

O projeto está configurado para usar o H2 no modo **em memória** e a lógica em `DatabaseConnection.java` utiliza `DROP TABLE IF EXISTS...` e `CREATE TABLE...` a cada execução.

**Isto significa que o banco de dados é 100% limpo e recriado do zero toda vez que o `App.java` é iniciado.** Todos os dados de testes (produtos, pedidos, etc.) serão apagados quando o programa fechar. Isto foi feito intencionalmente para garantir um ambiente de testes limpo e facilitar a correção, sem necessidade de configuração externa.