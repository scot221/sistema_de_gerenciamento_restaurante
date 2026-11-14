package restaurante.modelo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PratoPrincipal extends ItemCardapio {

    private Map<String, Double> acompanhamentos;

    public PratoPrincipal(String nome, double precoBase) {
        super(nome, precoBase);
        this.acompanhamentos = new LinkedHashMap<>();
    }

    public void adicionarAcompanhamento(String nome, double valorAdicional) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do acompanhamento não pode ser vazio.");
        }
        if (valorAdicional < 0) {
            throw new IllegalArgumentException("O valor do acompanhamento não pode ser negativo.");
        }
        this.acompanhamentos.put(nome.trim(), valorAdicional);
    }

    public boolean removerAcompanhamento(String nome) {
        return this.acompanhamentos.remove(nome) != null;
    }

    public void limparAcompanhamentos() {
        this.acompanhamentos.clear();
    }

    public double calcularValorAcompanhamentos() {
        double soma = 0;
        for (double valor : this.acompanhamentos.values()) {
            soma += valor;
        }
        return soma;
    }

    // getters
    public Map<String, Double> getAcompanhamentos() {
        return Collections.unmodifiableMap(this.acompanhamentos);
    }

    @Override
    public double calcularPreco() {
        return getPrecoBase() + calcularValorAcompanhamentos();
    }

    @Override
    public Categoria getCategoria() {
        return Categoria.PRATO_PRINCIPAL;
    }

    @Override
    protected String obterInformacoesEspecificas() {
        if (this.acompanhamentos.isEmpty()) {
            return "Sem acompanhamentos";
        }
        StringBuilder sb = new StringBuilder("Acompanhamentos: ");
        boolean primeiro = true;
        for (Map.Entry<String, Double> a : this.acompanhamentos.entrySet()) {
            if (!primeiro) {
                sb.append(", ");
            }
            sb.append(a.getKey()).append(" (+").append(FormatadorMoeda.formatar(a.getValue())).append(")");
            primeiro = false;
        }
        return sb.toString();
    }
}
