# 💊 Biqueira Legal - Pharmacy Management System

Sistema completo de gestão e e-commerce farmacêutico desenvolvido em Java. A aplicação oferece uma solução integrada via terminal (CLI) para gerenciar o ciclo de vida de vendas, desde o controle de estoque até a logística de entrega.

O projeto foi arquitetado utilizando o padrão **DAO (Data Access Object)** e **MVC**, garantindo modularidade, escalabilidade e persistência de dados eficiente.

## 🚀 Funcionalidades

O sistema opera através de um ecossistema de três perfis integrados:

### 1. 🛒 Módulo do Cliente (Storefront)
Interface dedicada ao consumidor final.
* **Catálogo Inteligente:** Visualização de medicamentos com detalhes regulatórios (dosagem, retenção de receita).
* **Carrinho de Compras:** Gestão de itens em memória antes do checkout.
* **Gestão de Favoritos:** Persistência de produtos preferidos pelo usuário.
* **Checkout Transacional:** Processamento de pedidos com baixa automática de estoque e geração de ordens de serviço.

### 2. 👨‍💼 Módulo Administrativo (Backoffice)
Painel de controle para gestores e farmacêuticos.
* **Controle de Estoque (WMS):** Cadastro e auditoria de produtos com validação de dados críticos.
* **Taxonomia:** Gestão de categorias para organização do inventário.
* **Monitoramento:** Visualização em tempo real da disponibilidade de produtos.

### 3. 🚚 Módulo Logístico (Last Mile)
Interface para parceiros de entrega.
* **Fila de Pedidos:** Visualização filtrada de ordens com status `AGUARDANDO`.
* **Workflow de Entrega:** Atualização de status em tempo real:
    1.  `AGUARDANDO` (Pedido processado)
    2.  `EM ROTA` (Em trânsito)
    3.  `ENTREGUE` (Finalizado)

---

## 🛠️ Stack Tecnológico

* **Core:** Java JDK 17 (LTS).
* **Persistência:** JDBC (Java Database Connectivity).
* **Database:** H2 Database Engine (In-Memory mode para alta performance em desenvolvimento).
* **Build System:** Apache Maven.
* **Arquitetura:** MVC + DAO Pattern + Singleton.

 ## 👥 Time de Desenvolvimento

Responsáveis pela implementação e arquitetura da solução:

* LUCAS PATRICK- Arquiteto Líder & DBA*
* LUCCA SEIB - Desenvolvedor Backend (Estoque)*
* LUIS GUSTAVO - Desenvolvedor Frontend (Experiência do Cliente)*
* MARCOS MIGUEL - Engenheiro de Dados (Transações & Logística)*
* RODRIGO CASTRO - Engenheiro de QA & Tech Lead*


## 📚 Instalação e Execução

### Pré-requisitos
* Java 17 ou superior.
* Maven instalado e configurado.

### Passos
1.  **Clonar o repositório:**
    ```bash
    git clone [https://github.com/SEU-USUARIO/lojinha.git](https://github.com/SEU-USUARIO/lojinha.git)
    ```
2.  **Importar o projeto:**
    Abra o diretório na sua IDE de preferência (IntelliJ IDEA, Eclipse, VS Code).
3.  **Executar:**
    Localize a classe principal em `src/main/java/br/com/moicano/lojinha/App.java` e execute o método `main`.

> **Nota de Infraestrutura:** A aplicação utiliza o H2 em modo memória volátil. O esquema de banco de dados (DDL) é recriado automaticamente a cada inicialização (`DatabaseConnection.initDatabase`) para garantir um ambiente limpo e consistente para testes e demonstrações.
