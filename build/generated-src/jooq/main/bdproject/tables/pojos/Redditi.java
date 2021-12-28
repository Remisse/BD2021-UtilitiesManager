/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Redditi implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer    codreddito;
    private final String     fascia;
    private final BigDecimal sconto;

    public Redditi(Redditi value) {
        this.codreddito = value.codreddito;
        this.fascia = value.fascia;
        this.sconto = value.sconto;
    }

    public Redditi(
        Integer    codreddito,
        String     fascia,
        BigDecimal sconto
    ) {
        this.codreddito = codreddito;
        this.fascia = fascia;
        this.sconto = sconto;
    }

    /**
     * Getter for <code>utenze.redditi.CodReddito</code>.
     */
    public Integer getCodreddito() {
        return this.codreddito;
    }

    /**
     * Getter for <code>utenze.redditi.Fascia</code>.
     */
    public String getFascia() {
        return this.fascia;
    }

    /**
     * Getter for <code>utenze.redditi.Sconto</code>.
     */
    public BigDecimal getSconto() {
        return this.sconto;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Redditi (");

        sb.append(codreddito);
        sb.append(", ").append(fascia);
        sb.append(", ").append(sconto);

        sb.append(")");
        return sb.toString();
    }
}
