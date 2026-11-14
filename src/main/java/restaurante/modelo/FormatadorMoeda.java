package restaurante.modelo;

import java.util.Locale;

public final class FormatadorMoeda {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private FormatadorMoeda() {
    }

    public static String formatar(double valor) {
        return String.format(PT_BR, "R$ %.2f", valor);
    }

    public static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
