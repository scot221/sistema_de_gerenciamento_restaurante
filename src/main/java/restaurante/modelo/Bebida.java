package restaurante.modelo;

public class Bebida extends ItemCardapio {

    private TamanhoBebida tamanho;
    private boolean alcoolica;

    public Bebida(String nome, double precoBase, TamanhoBebida tamanho, boolean alcoolica) {
        super(nome, precoBase);
        validarTamanho(tamanho);
        this.tamanho = tamanho;
        this.alcoolica = alcoolica;
    }

    private static void validarTamanho(TamanhoBebida tamanho) {
        if (tamanho == null) {
            throw new IllegalArgumentException("O tamanho da bebida deve ser informado.");
        }
    }

    // setters
    public void setTamanho(TamanhoBebida tamanho) {
        validarTamanho(tamanho);
        this.tamanho = tamanho;
    }
    public void setAlcoolica(boolean alcoolica) {
        this.alcoolica = alcoolica;
    }

    // getters
    public TamanhoBebida getTamanho() {
        return this.tamanho;
    }
    public boolean isAlcoolica() {
        return this.alcoolica;
    }

    @Override
    public double calcularPreco() {
        return getPrecoBase() * this.tamanho.getFator();
    }

    @Override
    public Categoria getCategoria() {
        return Categoria.BEBIDA;
    }

    @Override
    protected String obterInformacoesEspecificas() {
        return "Tamanho: " + this.tamanho.getDescricao()
                + " | Alcoólica: " + (this.alcoolica ? "Sim" : "Não");
    }
}
