/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Immobili implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer idimmobile;
    private final String  tipo;
    private final String  via;
    private final String  numcivico;
    private final String  interno;
    private final Integer idzona;
    private final Integer cap;

    public Immobili(Immobili value) {
        this.idimmobile = value.idimmobile;
        this.tipo = value.tipo;
        this.via = value.via;
        this.numcivico = value.numcivico;
        this.interno = value.interno;
        this.idzona = value.idzona;
        this.cap = value.cap;
    }

    public Immobili(
        Integer idimmobile,
        String  tipo,
        String  via,
        String  numcivico,
        String  interno,
        Integer idzona,
        Integer cap
    ) {
        this.idimmobile = idimmobile;
        this.tipo = tipo;
        this.via = via;
        this.numcivico = numcivico;
        this.interno = interno;
        this.idzona = idzona;
        this.cap = cap;
    }

    /**
     * Getter for <code>utenze.immobili.IdImmobile</code>.
     */
    public Integer getIdimmobile() {
        return this.idimmobile;
    }

    /**
     * Getter for <code>utenze.immobili.Tipo</code>.
     */
    public String getTipo() {
        return this.tipo;
    }

    /**
     * Getter for <code>utenze.immobili.Via</code>.
     */
    public String getVia() {
        return this.via;
    }

    /**
     * Getter for <code>utenze.immobili.NumCivico</code>.
     */
    public String getNumcivico() {
        return this.numcivico;
    }

    /**
     * Getter for <code>utenze.immobili.Interno</code>.
     */
    public String getInterno() {
        return this.interno;
    }

    /**
     * Getter for <code>utenze.immobili.IdZona</code>.
     */
    public Integer getIdzona() {
        return this.idzona;
    }

    /**
     * Getter for <code>utenze.immobili.CAP</code>.
     */
    public Integer getCap() {
        return this.cap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Immobili (");

        sb.append(idimmobile);
        sb.append(", ").append(tipo);
        sb.append(", ").append(via);
        sb.append(", ").append(numcivico);
        sb.append(", ").append(interno);
        sb.append(", ").append(idzona);
        sb.append(", ").append(cap);

        sb.append(")");
        return sb.toString();
    }
}
