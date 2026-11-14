package restaurante;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import restaurante.modelo.Bebida;
import restaurante.modelo.Comanda;
import restaurante.modelo.ItemCardapio;
import restaurante.modelo.PratoPrincipal;
import restaurante.modelo.Sobremesa;
import restaurante.modelo.TamanhoBebida;
import restaurante.promocao.DescontoSobremesa;
import restaurante.promocao.HappyHour;
import restaurante.promocao.Promocao;
import restaurante.sistema.Restaurante;

public class PrincipalRestaurante {

    public static void main(String[] args) {
        Restaurante restaurante = new Restaurante();

        System.out.println("--- Cadastro do cardápio ---");
        PratoPrincipal parmegiana = new PratoPrincipal("Bife à parmegiana", 42.00);
        parmegiana.adicionarAcompanhamento("Arroz", 0);
        parmegiana.adicionarAcompanhamento("Batata frita", 9.00);
        Bebida chopp = new Bebida("Chopp", 12.00, TamanhoBebida.GRANDE, true);
        Bebida suco = new Bebida("Suco de laranja", 8.00, TamanhoBebida.PEQUENO, false);
        Sobremesa pudim = new Sobremesa("Pudim", 14.00, true, true);

        restaurante.cadastrarItem(parmegiana);
        restaurante.cadastrarItem(chopp);
        restaurante.cadastrarItem(suco);
        restaurante.cadastrarItem(pudim);

        // lista polimórfica: cada item calcula o próprio preço
        for (ItemCardapio item : restaurante.listarCardapio()) {
            item.exibirInformacoes();
        }

        System.out.println("\n--- Promoções ---");
        restaurante.adicionarPromocao(new HappyHour(30, LocalTime.of(17, 0), LocalTime.of(19, 0)));
        restaurante.adicionarPromocao(new DescontoSobremesa(15));
        for (Promocao p : restaurante.listarPromocoes()) {
            System.out.println(p.getDescricao());
        }

        System.out.println("\n--- Comanda da mesa 5 (dentro do happy hour) ---");
        Comanda c1 = restaurante.abrirComanda(5);
        c1.adicionarItem(parmegiana, 2);
        c1.adicionarItem(chopp, 3);
        c1.adicionarItem(pudim, 1);
        c1.adicionarItem(suco, 1);
        c1.removerItem(suco);
        System.out.println("Subtotal parcial: " + c1.calcularSubtotal());

        LocalDate hoje = LocalDate.now();
        restaurante.fecharComanda(c1, hoje.atTime(18, 0));
        System.out.println(c1.getRecibo());

        System.out.println("--- Comanda da mesa 7 (fora do happy hour, sem prato) ---");
        Comanda c2 = restaurante.abrirComanda(7);
        c2.adicionarItem(chopp, 2);
        c2.adicionarItem(pudim, 1);
        restaurante.fecharComanda(c2, hoje.atTime(21, 0));
        System.out.println(c2.getRecibo());

        System.out.println("--- Histórico de hoje ---");
        for (Comanda c : restaurante.consultarHistorico(hoje)) {
            System.out.println(c + " | total: " + c.getTotal());
        }
        System.out.println("Faturamento do dia: " + restaurante.calcularFaturamento(hoje));

        System.out.println("\n--- Validações ---");
        testar("mesa duplicada", () -> {
            restaurante.abrirComanda(9);
            restaurante.abrirComanda(9);
        });
        testar("fechar sem itens", () -> restaurante.fecharComanda(restaurante.buscarComandaAberta(9)));
        testar("remover item em comanda aberta", () -> {
            restaurante.buscarComandaAberta(9).adicionarItem(pudim, 1);
            restaurante.removerItem(pudim);
        });
        testar("alterar comanda fechada", () -> c1.adicionarItem(suco, 1));
        testar("preço inválido", () -> new Sobremesa("Torta", -5, false, false));
        testar("quantidade inválida", () -> c2.getItens().get(0).setQuantidade(0));
    }

    private static void testar(String descricao, Runnable acao) {
        try {
            acao.run();
            System.out.println(descricao + ": NÃO lançou exceção (inesperado)");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(descricao + ": " + e.getMessage());
        }
    }
}
