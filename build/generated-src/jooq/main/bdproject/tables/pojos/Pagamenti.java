/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Pagamenti implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer   numerobolletta;
    private final LocalDate datapagamento;

    public Pagamenti(Pagamenti value) {
        this.numerobolletta = value.numerobolletta;
        this.datapagamento = value.datapagamento;
    }

    public Pagamenti(
        Integer   numerobolletta,
        LocalDate datapagamento
    ) {
        this.numerobolletta = numerobolletta;
        this.datapagamento = datapagamento;
    }

    /**
     * Getter for <code>utenze.pagamenti.NumeroBolletta</code>.
     */
    public Integer getNumerobolletta() {
        return this.numerobolletta;
    }

    /**
     * Getter for <code>utenze.pagamenti.DataPagamento</code>.
     */
    public LocalDate getDatapagamento() {
        return this.datapagamento;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pagamenti (");

        sb.append(numerobolletta);
        sb.append(", ").append(datapagamento);

        sb.append(")");
        return sb.toString();
    }
}
