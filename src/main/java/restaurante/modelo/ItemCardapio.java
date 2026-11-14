package restaurante.modelo;

public abstract class ItemCardapio {

    private static int proximoCodigo = 1;

    private int codigo;
    private String nome;
    private double precoBase;

    protected ItemCardapio(String nome, double precoBase) {
        validarNome(nome);
        validarPreco(precoBase);

        this.codigo = gerarCodigo();
        this.nome = nome.trim();
        this.precoBase = precoBase;
    }

    private static synchronized int gerarCodigo() {
        return proximoCodigo++;
    }

    // ---------- validações ----------

    protected static void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do item não pode ser vazio.");
        }
    }

    protected static void validarPreco(double preco) {
        if (preco <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
    }

    // setters
    public void setNome(String nome) {
        validarNome(nome);
        this.nome = nome.trim();
    }
    public void setPrecoBase(double precoBase) {
        validarPreco(precoBase);
        this.precoBase = precoBase;
    }

    // getters
    public int getCodigo() {
        return this.codigo;
    }
    public String getNome() {
        return this.nome;
    }
    public double getPrecoBase() {
        return this.precoBase;
    }

    // cada categoria calcula o próprio preço final
    public abstract double calcularPreco();

    public abstract Categoria getCategoria();

    protected abstract String obterInformacoesEspecificas();

    public String getDetalhes() {
        return obterInformacoesEspecificas();
    }

    public String gerarDescricao() {
        return "[" + this.codigo + "] " + this.nome
                + " (" + getCategoria().getDescricao() + ") - "
                + FormatadorMoeda.formatar(calcularPreco())
                + " - " + obterInformacoesEspecificas();
    }

    public void exibirInformacoes() {
        System.out.println(gerarDescricao());
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
