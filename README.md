# Sistema de Gerenciamento de um Restaurante (Pedidos)

Sistema para gerenciar o cardápio, as comandas e o fechamento de contas de um restaurante, com promoções (happy hour e desconto em sobremesas). Projeto da disciplina de Programação Orientada a Objetos (IFMT).

## Integrantes
- Davi (@scot221)
- Pietro (@zpietrohenrique-alt)
- Samuel (@SaMuZsp)

## Tecnologias e versões
- Java (JDK 25)
- Maven (wrapper incluso: mvnw)
- Spring Boot
- Vaadin (interface web)

## Como instalar e executar
1. Instale o JDK e o Git.
2. Clone o repositório:

        git clone git@github.com:scot221/sistema_de_gerenciamento_restaurante.git
        cd sistema_de_gerenciamento_restaurante

3. Execute o projeto:

        ./mvnw spring-boot:run

   No Windows (prompt de comando): mvnw.cmd spring-boot:run
4. Abra no navegador: http://localhost:8080

## Exemplo de uso
<img width="1873" height="1011" alt="image" src="https://github.com/user-attachments/assets/6c81ce5f-13e6-4ddc-af7e-736bb06d8b93" />


## Estrutura das pastas

    src/main/java/restaurante/
      modelo/     classes do domínio (itens, comanda, bebidas, pratos)
      promocao/   regras de promoção
      sistema/    classe Restaurante
      view/       telas (Vaadin)
    docs/         prints e documentação
    pom.xml       configuração Maven
