package restaurante.modelo;

public class ItemPedido {

    private ItemCardapio item;
    private int quantidade;

    public ItemPedido(ItemCardapio item, int quantidade) {
        if (item == null) {
            throw new IllegalArgumentException("O item do pedido não pode ser nulo.");
        }
        validarQuantidade(quantidade);
        this.item = item;
        this.quantidade = quantidade;
    }

    private static void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }

    // setters
    public void setQuantidade(int quantidade) {
        validarQuantidade(quantidade);
        this.quantidade = quantidade;
    }

    // getters
    public ItemCardapio getItem() {
        return this.item;
    }
    public int getQuantidade() {
        return this.quantidade;
    }
    public double getPrecoUnitario() {
        return this.item.calcularPreco();
    }

    // chamada polimórfica: cada item do cardápio informa o próprio preço
    public double calcularSubtotal() {
        return this.item.calcularPreco() * this.quantidade;
    }

    @Override
    public String toString() {
        return this.quantidade + "x " + this.item.getNome();
    }
}
