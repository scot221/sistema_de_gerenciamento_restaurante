package restaurante.modelo;

public enum TamanhoBebida {
    PEQUENO("Pequeno (300 ml)", 0.80),
    MEDIO("Médio (500 ml)", 1.00),
    GRANDE("Grande (700 ml)", 1.30);

    private final String descricao;
    private final double fator;

    TamanhoBebida(String descricao, double fator) {
        this.descricao = descricao;
        this.fator = fator;
    }

    // getters
    public String getDescricao() {
        return this.descricao;
    }
    public double getFator() {
        return this.fator;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
