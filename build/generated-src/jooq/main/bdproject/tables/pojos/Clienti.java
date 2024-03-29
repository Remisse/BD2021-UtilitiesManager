/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Clienti implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer codicecliente;
    private final Integer fasciareddito;

    public Clienti(Clienti value) {
        this.codicecliente = value.codicecliente;
        this.fasciareddito = value.fasciareddito;
    }

    public Clienti(
        Integer codicecliente,
        Integer fasciareddito
    ) {
        this.codicecliente = codicecliente;
        this.fasciareddito = fasciareddito;
    }

    /**
     * Getter for <code>utenze.clienti.CodiceCliente</code>.
     */
    public Integer getCodicecliente() {
        return this.codicecliente;
    }

    /**
     * Getter for <code>utenze.clienti.FasciaReddito</code>.
     */
    public Integer getFasciareddito() {
        return this.fasciareddito;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Clienti (");

        sb.append(codicecliente);
        sb.append(", ").append(fasciareddito);

        sb.append(")");
        return sb.toString();
    }
}
