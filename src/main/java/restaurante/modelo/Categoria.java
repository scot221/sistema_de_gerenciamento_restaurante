package restaurante.modelo;

public enum Categoria {
    PRATO_PRINCIPAL("Prato principal"),
    BEBIDA("Bebida"),
    SOBREMESA("Sobremesa");

    private final String descricao;

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    // getters
    public String getDescricao() {
        return this.descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
