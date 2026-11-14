package restaurante.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.Map;
import restaurante.modelo.Bebida;
import restaurante.modelo.Categoria;
import restaurante.modelo.FormatadorMoeda;
import restaurante.modelo.ItemCardapio;
import restaurante.modelo.PratoPrincipal;
import restaurante.modelo.Sobremesa;
import restaurante.modelo.TamanhoBebida;
import restaurante.sistema.Restaurante;

@Route("")
public class CardapioView extends VerticalLayout {

    private Restaurante restaurante = Restaurante.getInstancia();
    private ItemCardapio selecionado;

    private Grid<ItemCardapio> grid = new Grid<>(ItemCardapio.class, false);

    private ComboBox<Categoria> campoCategoria = new ComboBox<>("Categoria");
    private TextField campoNome = new TextField("Nome");
    private NumberField campoPreco = new NumberField("Preço base (R$)");
    private TextField campoAcompanhamentos = new TextField("Acompanhamentos");
    private ComboBox<TamanhoBebida> campoTamanho = new ComboBox<>("Tamanho");
    private Checkbox campoAlcoolica = new Checkbox("Alcoólica");
    private Checkbox campoCalda = new Checkbox("Com calda");
    private Checkbox campoGelada = new Checkbox("Servida gelada");

    public CardapioView() {
        campoCategoria.setItems(Categoria.values());
        campoCategoria.addValueChangeListener(e -> atualizarCamposEspecificos());

        campoTamanho.setItems(TamanhoBebida.values());
        campoTamanho.setValue(TamanhoBebida.MEDIO);

        campoAcompanhamentos.setPlaceholder("Arroz=0; Batata frita=9,50");
        campoAcompanhamentos.setWidth("320px");

        grid.addColumn(ItemCardapio::getCodigo).setHeader("Código");
        grid.addColumn(ItemCardapio::getNome).setHeader("Nome");
        grid.addColumn(item -> item.getCategoria().getDescricao()).setHeader("Categoria");
        grid.addColumn(item -> FormatadorMoeda.formatar(item.calcularPreco())).setHeader("Preço final");
        grid.addColumn(ItemCardapio::getDetalhes).setHeader("Detalhes");
        grid.setWidthFull();
        grid.asSingleSelect().addValueChangeListener(e -> selecionarItem(e.getValue()));

        Button btnSalvar = new Button("Salvar", e -> salvar());
        Button btnRemover = new Button("Remover", e -> remover());
        Button btnLimpar = new Button("Novo / limpar", e -> limparFormulario());

        HorizontalLayout linhaBasica = new HorizontalLayout(campoCategoria, campoNome, campoPreco);
        linhaBasica.setAlignItems(Alignment.END);

        HorizontalLayout linhaEspecifica = new HorizontalLayout(
                campoAcompanhamentos, campoTamanho, campoAlcoolica, campoCalda, campoGelada);
        linhaEspecifica.setAlignItems(Alignment.END);

        HorizontalLayout linhaBotoes = new HorizontalLayout(btnSalvar, btnRemover, btnLimpar);

        setWidthFull();
        add(new MenuNavegacao(), new H1("Cardápio"),
                linhaBasica, linhaEspecifica, linhaBotoes, grid);

        atualizarCamposEspecificos();
        atualizarGrid();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        atualizarGrid();
    }

    // ---------- ações ----------

    private void salvar() {
        try {
            if (this.selecionado == null) {
                criarNovo();
            } else {
                atualizarSelecionado();
            }
            limparFormulario();
            atualizarGrid();
            Notification.show("Item salvo.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Notification.show(ex.getMessage());
        }
    }

    private void criarNovo() {
        Categoria categoria = campoCategoria.getValue();
        if (categoria == null) {
            throw new IllegalArgumentException("Escolha a categoria do item.");
        }
        String nome = campoNome.getValue();
        double preco = obterPreco();

        ItemCardapio novo = switch (categoria) {
            case PRATO_PRINCIPAL -> {
                PratoPrincipal prato = new PratoPrincipal(nome, preco);
                preencherAcompanhamentos(prato);
                yield prato;
            }
            case BEBIDA -> new Bebida(nome, preco, campoTamanho.getValue(), campoAlcoolica.getValue());
            case SOBREMESA -> new Sobremesa(nome, preco, campoCalda.getValue(), campoGelada.getValue());
        };
        restaurante.cadastrarItem(novo);
    }

    private void atualizarSelecionado() {
        this.selecionado.setNome(campoNome.getValue());
        this.selecionado.setPrecoBase(obterPreco());

        if (this.selecionado instanceof PratoPrincipal prato) {
            prato.limparAcompanhamentos();
            preencherAcompanhamentos(prato);
        } else if (this.selecionado instanceof Bebida bebida) {
            bebida.setTamanho(campoTamanho.getValue());
            bebida.setAlcoolica(campoAlcoolica.getValue());
        } else if (this.selecionado instanceof Sobremesa sobremesa) {
            sobremesa.setComCalda(campoCalda.getValue());
            sobremesa.setServidaGelada(campoGelada.getValue());
        }
    }

    private void remover() {
        if (this.selecionado == null) {
            Notification.show("Selecione um item na tabela.");
            return;
        }
        try {
            restaurante.removerItem(this.selecionado);
            limparFormulario();
            atualizarGrid();
            Notification.show("Item removido.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Notification.show(ex.getMessage());
        }
    }

    // ---------- formulário ----------

    private double obterPreco() {
        if (campoPreco.getValue() == null) {
            throw new IllegalArgumentException("Informe o preço base.");
        }
        return campoPreco.getValue();
    }

    private void preencherAcompanhamentos(PratoPrincipal prato) {
        String texto = campoAcompanhamentos.getValue();
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }
        for (String parte : texto.split(";")) {
            if (parte.trim().isEmpty()) {
                continue;
            }
            String[] campos = parte.split("=");
            try {
                double valor = campos.length > 1 ? Double.parseDouble(campos[1].trim().replace(',', '.')) : 0;
                prato.adicionarAcompanhamento(campos[0], valor);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Valor de acompanhamento inválido: " + parte.trim());
            }
        }
    }

    private void selecionarItem(ItemCardapio item) {
        this.selecionado = item;
        if (item == null) {
            return;
        }
        campoCategoria.setReadOnly(false);
        campoCategoria.setValue(item.getCategoria());
        campoCategoria.setReadOnly(true);
        campoNome.setValue(item.getNome());
        campoPreco.setValue(item.getPrecoBase());

        if (item instanceof PratoPrincipal prato) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Double> a : prato.getAcompanhamentos().entrySet()) {
                if (sb.length() > 0) {
                    sb.append("; ");
                }
                sb.append(a.getKey()).append("=").append(a.getValue());
            }
            campoAcompanhamentos.setValue(sb.toString());
        } else if (item instanceof Bebida bebida) {
            campoTamanho.setValue(bebida.getTamanho());
            campoAlcoolica.setValue(bebida.isAlcoolica());
        } else if (item instanceof Sobremesa sobremesa) {
            campoCalda.setValue(sobremesa.isComCalda());
            campoGelada.setValue(sobremesa.isServidaGelada());
        }
    }

    private void limparFormulario() {
        this.selecionado = null;
        grid.asSingleSelect().clear();
        campoCategoria.setReadOnly(false);
        campoCategoria.clear();
        campoNome.clear();
        campoPreco.clear();
        campoAcompanhamentos.clear();
        campoTamanho.setValue(TamanhoBebida.MEDIO);
        campoAlcoolica.setValue(false);
        campoCalda.setValue(false);
        campoGelada.setValue(false);
    }

    private void atualizarCamposEspecificos() {
        Categoria categoria = campoCategoria.getValue();
        campoAcompanhamentos.setVisible(categoria == Categoria.PRATO_PRINCIPAL);
        campoTamanho.setVisible(categoria == Categoria.BEBIDA);
        campoAlcoolica.setVisible(categoria == Categoria.BEBIDA);
        campoCalda.setVisible(categoria == Categoria.SOBREMESA);
        campoGelada.setVisible(categoria == Categoria.SOBREMESA);
    }

    private void atualizarGrid() {
        grid.setItems(new ArrayList<>(restaurante.listarCardapio()));
    }
}
