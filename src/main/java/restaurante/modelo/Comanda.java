package restaurante.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import restaurante.promocao.Promocao;

public class Comanda {

    public static final double TAXA_SERVICO = 0.10;

    private static int proximoNumero = 1;

    private int numero;
    private int numeroMesa;
    private List<ItemPedido> itens;
    private LocalDateTime abertura;
    private LocalDateTime fechamento;
    private boolean aberta;

    // valores gravados no fechamento
    private double subtotal;
    private double totalDescontos;
    private double taxaServico;
    private double total;
    private Map<String, Double> descontosAplicados;
    private String recibo;

    public Comanda(int numeroMesa) {
        if (numeroMesa <= 0) {
            throw new IllegalArgumentException("O número da mesa deve ser maior que zero.");
        }
        this.numero = gerarNumero();
        this.numeroMesa = numeroMesa;
        this.itens = new ArrayList<>();
        this.abertura = LocalDateTime.now();
        this.aberta = true;
        this.descontosAplicados = new LinkedHashMap<>();
    }

    private static synchronized int gerarNumero() {
        return proximoNumero++;
    }

    private void validarAberta() {
        if (!this.aberta) {
            throw new IllegalStateException("A comanda " + this.numero + " já está fechada.");
        }
    }

    // ---------- itens do pedido ----------

    public void adicionarItem(ItemCardapio item, int quantidade) {
        validarAberta();
        if (item == null) {
            throw new IllegalArgumentException("O item do pedido não pode ser nulo.");
        }
        ItemPedido existente = buscarItemPedido(item);
        if (existente != null) {
            existente.setQuantidade(existente.getQuantidade() + quantidade);
        } else {
            this.itens.add(new ItemPedido(item, quantidade));
        }
    }

    // remove todas as unidades do item da comanda
    public void removerItem(ItemCardapio item) {
        validarAberta();
        ItemPedido existente = buscarItemPedido(item);
        if (existente == null) {
            throw new IllegalArgumentException("O item não está nesta comanda.");
        }
        this.itens.remove(existente);
    }

    // remove apenas parte das unidades; se zerar, o item sai da comanda
    public void removerItem(ItemCardapio item, int quantidade) {
        validarAberta();
        ItemPedido existente = buscarItemPedido(item);
        if (existente == null) {
            throw new IllegalArgumentException("O item não está nesta comanda.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (quantidade >= existente.getQuantidade()) {
            this.itens.remove(existente);
        } else {
            existente.setQuantidade(existente.getQuantidade() - quantidade);
        }
    }

    private ItemPedido buscarItemPedido(ItemCardapio item) {
        for (ItemPedido ip : this.itens) {
            if (ip.getItem() == item) {
                return ip;
            }
        }
        return null;
    }

    public boolean contemItem(ItemCardapio item) {
        return buscarItemPedido(item) != null;
    }

    public boolean temCategoria(Categoria categoria) {
        for (ItemPedido ip : this.itens) {
            if (ip.getItem().getCategoria() == categoria) {
                return true;
            }
        }
        return false;
    }

    // ---------- cálculos ----------

    // percorre a lista polimórfica: cada item calcula o próprio preço
    public double calcularSubtotal() {
        double soma = 0;
        for (ItemPedido ip : this.itens) {
            soma += ip.calcularSubtotal();
        }
        return soma;
    }

    // aplica cada promoção (via interface) sobre cada item do pedido
    private Map<String, Double> apurarDescontos(List<Promocao> promocoes, LocalDateTime momento) {
        Map<String, Double> descontos = new LinkedHashMap<>();

        for (ItemPedido ip : this.itens) {
            double restante = ip.calcularSubtotal();

            for (Promocao promocao : promocoes) {
                double desconto = Math.min(promocao.calcularDesconto(ip, this, momento), restante);
                if (desconto > 0) {
                    restante -= desconto;
                    descontos.merge(promocao.getDescricao(), desconto, Double::sum);
                }
            }
        }
        return descontos;
    }

    public double calcularDescontos(List<Promocao> promocoes, LocalDateTime momento) {
        double soma = 0;
        for (double valor : apurarDescontos(promocoes, momento).values()) {
            soma += valor;
        }
        return FormatadorMoeda.arredondar(soma);
    }

    // total estimado enquanto a comanda ainda está aberta
    public double calcularTotalParcial(List<Promocao> promocoes) {
        double base = calcularSubtotal() - calcularDescontos(promocoes, LocalDateTime.now());
        return FormatadorMoeda.arredondar(base + base * TAXA_SERVICO);
    }

    // ---------- fechamento ----------

    public void fechar(List<Promocao> promocoes) {
        fechar(promocoes, LocalDateTime.now());
    }

    public void fechar(List<Promocao> promocoes, LocalDateTime momento) {
        validarAberta();
        if (this.itens.isEmpty()) {
            throw new IllegalStateException("Não é possível fechar uma comanda sem itens.");
        }

        this.fechamento = momento;
        this.subtotal = FormatadorMoeda.arredondar(calcularSubtotal());
        this.descontosAplicados = apurarDescontos(promocoes, momento);

        double somaDescontos = 0;
        for (double valor : this.descontosAplicados.values()) {
            somaDescontos += valor;
        }
        this.totalDescontos = FormatadorMoeda.arredondar(somaDescontos);

        double base = this.subtotal - this.totalDescontos;
        this.taxaServico = FormatadorMoeda.arredondar(base * TAXA_SERVICO);
        this.total = FormatadorMoeda.arredondar(base + this.taxaServico);

        this.aberta = false;
        this.recibo = montarRecibo();
    }

    private String montarRecibo() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();

        sb.append("=========== RECIBO ===========\n");
        sb.append("Comanda nº ").append(this.numero).append(" - Mesa ").append(this.numeroMesa).append("\n");
        sb.append("Abertura:   ").append(this.abertura.format(formato)).append("\n");
        sb.append("Fechamento: ").append(this.fechamento.format(formato)).append("\n");
        sb.append("------------------------------\n");

        for (ItemPedido ip : this.itens) {
            sb.append(String.format("%dx %s%n    %s cada = %s%n",
                    ip.getQuantidade(),
                    ip.getItem().getNome(),
                    FormatadorMoeda.formatar(ip.getPrecoUnitario()),
                    FormatadorMoeda.formatar(ip.calcularSubtotal())));
        }

        sb.append("------------------------------\n");
        sb.append("Subtotal: ").append(FormatadorMoeda.formatar(this.subtotal)).append("\n");

        if (!this.descontosAplicados.isEmpty()) {
            sb.append("Promoções aplicadas:\n");
            for (Map.Entry<String, Double> d : this.descontosAplicados.entrySet()) {
                sb.append("  ").append(d.getKey()).append(": - ")
                        .append(FormatadorMoeda.formatar(d.getValue())).append("\n");
            }
            sb.append("Total de descontos: - ").append(FormatadorMoeda.formatar(this.totalDescontos)).append("\n");
        }

        sb.append("Taxa de serviço (").append((int) (TAXA_SERVICO * 100)).append("%): ")
                .append(FormatadorMoeda.formatar(this.taxaServico)).append("\n");
        sb.append("TOTAL A PAGAR: ").append(FormatadorMoeda.formatar(this.total)).append("\n");
        sb.append("==============================\n");

        return sb.toString();
    }

    // getters
    public int getNumero() {
        return this.numero;
    }
    public int getNumeroMesa() {
        return this.numeroMesa;
    }
    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(this.itens);
    }
    public LocalDateTime getAbertura() {
        return this.abertura;
    }
    public LocalDateTime getFechamento() {
        return this.fechamento;
    }
    public boolean isAberta() {
        return this.aberta;
    }
    public double getSubtotal() {
        return this.subtotal;
    }
    public double getTotalDescontos() {
        return this.totalDescontos;
    }
    public double getTaxaServico() {
        return this.taxaServico;
    }
    public double getTotal() {
        return this.total;
    }
    public Map<String, Double> getDescontosAplicados() {
        return Collections.unmodifiableMap(this.descontosAplicados);
    }
    public String getRecibo() {
        if (this.aberta) {
            throw new IllegalStateException("O recibo só é emitido após o fechamento da comanda.");
        }
        return this.recibo;
    }

    @Override
    public String toString() {
        return "Comanda " + this.numero + " - Mesa " + this.numeroMesa;
    }
}
