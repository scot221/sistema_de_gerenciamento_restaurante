package restaurante.modelo;

public class Sobremesa extends ItemCardapio {

    private static final double ADICIONAL_CALDA = 3.50;

    private boolean comCalda;
    private boolean servidaGelada;

    public Sobremesa(String nome, double precoBase, boolean comCalda, boolean servidaGelada) {
        super(nome, precoBase);
        this.comCalda = comCalda;
        this.servidaGelada = servidaGelada;
    }

    // setters
    public void setComCalda(boolean comCalda) {
        this.comCalda = comCalda;
    }
    public void setServidaGelada(boolean servidaGelada) {
        this.servidaGelada = servidaGelada;
    }

    // getters
    public boolean isComCalda() {
        return this.comCalda;
    }
    public boolean isServidaGelada() {
        return this.servidaGelada;
    }

    @Override
    public double calcularPreco() {
        return getPrecoBase() + (this.comCalda ? ADICIONAL_CALDA : 0);
    }

    @Override
    public Categoria getCategoria() {
        return Categoria.SOBREMESA;
    }

    @Override
    protected String obterInformacoesEspecificas() {
        return "Com calda: " + (this.comCalda ? "Sim" : "Não")
                + " | Gelada: " + (this.servidaGelada ? "Sim" : "Não");
    }
}
