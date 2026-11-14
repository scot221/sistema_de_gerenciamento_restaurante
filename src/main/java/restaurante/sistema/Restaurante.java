package restaurante.sistema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import restaurante.modelo.Bebida;
import restaurante.modelo.Categoria;
import restaurante.modelo.Comanda;
import restaurante.modelo.ItemCardapio;
import restaurante.modelo.PratoPrincipal;
import restaurante.modelo.Sobremesa;
import restaurante.modelo.TamanhoBebida;
import restaurante.promocao.DescontoSobremesa;
import restaurante.promocao.HappyHour;
import restaurante.promocao.Promocao;

public class Restaurante {

    private static final Restaurante INSTANCIA = new Restaurante();

    private List<ItemCardapio> cardapio;
    private List<Comanda> comandasAbertas;
    private List<Comanda> historico;
    private List<Promocao> promocoes;

    public Restaurante() {
        this.cardapio = new ArrayList<>();
        this.comandasAbertas = new ArrayList<>();
        this.historico = new ArrayList<>();
        this.promocoes = new ArrayList<>();
    }

    // instância única usada pelas telas
    public static Restaurante getInstancia() {
        return INSTANCIA;
    }

    // ---------- cardápio ----------

    public void cadastrarItem(ItemCardapio item) {
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }
        for (ItemCardapio existente : this.cardapio) {
            if (existente != item
                    && existente.getCategoria() == item.getCategoria()
                    && existente.getNome().equalsIgnoreCase(item.getNome())) {
                throw new IllegalArgumentException("Já existe um item com esse nome nessa categoria.");
            }
        }
        if (!this.cardapio.contains(item)) {
            this.cardapio.add(item);
        }
    }

    public void removerItem(ItemCardapio item) {
        for (Comanda c : this.comandasAbertas) {
            if (c.contemItem(item)) {
                throw new IllegalStateException("O item está em uma comanda aberta (" + c + ").");
            }
        }
        if (!this.cardapio.remove(item)) {
            throw new IllegalArgumentException("O item não está no cardápio.");
        }
    }

    public ItemCardapio buscarItem(int codigo) {
        for (ItemCardapio item : this.cardapio) {
            if (item.getCodigo() == codigo) {
                return item;
            }
        }
        return null;
    }

    public List<ItemCardapio> listarCardapio() {
        return Collections.unmodifiableList(this.cardapio);
    }

    public List<ItemCardapio> listarPorCategoria(Categoria categoria) {
        List<ItemCardapio> resultado = new ArrayList<>();
        for (ItemCardapio item : this.cardapio) {
            if (item.getCategoria() == categoria) {
                resultado.add(item);
            }
        }
        return resultado;
    }

    // ---------- comandas ----------

    public Comanda abrirComanda(int numeroMesa) {
        if (buscarComandaAberta(numeroMesa) != null) {
            throw new IllegalStateException("A mesa " + numeroMesa + " já possui uma comanda aberta.");
        }
        Comanda comanda = new Comanda(numeroMesa);
        this.comandasAbertas.add(comanda);
        return comanda;
    }

    public Comanda buscarComandaAberta(int numeroMesa) {
        for (Comanda c : this.comandasAbertas) {
            if (c.getNumeroMesa() == numeroMesa) {
                return c;
            }
        }
        return null;
    }

    public List<Comanda> listarComandasAbertas() {
        return Collections.unmodifiableList(this.comandasAbertas);
    }

    public void fecharComanda(Comanda comanda) {
        fecharComanda(comanda, LocalDateTime.now());
    }

    public void fecharComanda(Comanda comanda, LocalDateTime momento) {
        if (!this.comandasAbertas.contains(comanda)) {
            throw new IllegalArgumentException("A comanda não está aberta neste restaurante.");
        }
        comanda.fechar(this.promocoes, momento);
        this.comandasAbertas.remove(comanda);
        this.historico.add(comanda);
    }

    // ---------- promoções ----------

    public void adicionarPromocao(Promocao promocao) {
        if (promocao == null) {
            throw new IllegalArgumentException("A promoção não pode ser nula.");
        }
        this.promocoes.add(promocao);
    }

    public void removerPromocao(Promocao promocao) {
        this.promocoes.remove(promocao);
    }

    public List<Promocao> listarPromocoes() {
        return Collections.unmodifiableList(this.promocoes);
    }

    // ---------- histórico ----------

    public List<Comanda> consultarHistorico(LocalDate data) {
        List<Comanda> resultado = new ArrayList<>();
        for (Comanda c : this.historico) {
            if (c.getFechamento().toLocalDate().equals(data)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public double calcularFaturamento(LocalDate data) {
        double soma = 0;
        for (Comanda c : consultarHistorico(data)) {
            soma += c.getTotal();
        }
        return soma;
    }

    // ---------- dados de exemplo ----------

    public void carregarDadosExemplo() {
        PratoPrincipal parmegiana = new PratoPrincipal("Bife à parmegiana", 42.00);
        parmegiana.adicionarAcompanhamento("Arroz", 0);
        parmegiana.adicionarAcompanhamento("Batata frita", 9.00);

        PratoPrincipal peixe = new PratoPrincipal("Tilápia grelhada", 48.00);
        peixe.adicionarAcompanhamento("Pirão", 6.00);

        cadastrarItem(parmegiana);
        cadastrarItem(peixe);
        cadastrarItem(new Bebida("Suco de laranja", 8.00, TamanhoBebida.MEDIO, false));
        cadastrarItem(new Bebida("Chopp", 12.00, TamanhoBebida.MEDIO, true));
        cadastrarItem(new Sobremesa("Pudim", 14.00, true, true));
        cadastrarItem(new Sobremesa("Sorvete", 11.00, false, true));

        adicionarPromocao(new HappyHour(30, LocalTime.of(17, 0), LocalTime.of(19, 0)));
        adicionarPromocao(new DescontoSobremesa(15));
    }
}
