/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OperatoriCessazioni implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer numerorichiesta;
    private final Integer idoperatore;

    public OperatoriCessazioni(OperatoriCessazioni value) {
        this.numerorichiesta = value.numerorichiesta;
        this.idoperatore = value.idoperatore;
    }

    public OperatoriCessazioni(
        Integer numerorichiesta,
        Integer idoperatore
    ) {
        this.numerorichiesta = numerorichiesta;
        this.idoperatore = idoperatore;
    }

    /**
     * Getter for <code>utenze.operatori cessazioni.NumeroRichiesta</code>.
     */
    public Integer getNumerorichiesta() {
        return this.numerorichiesta;
    }

    /**
     * Getter for <code>utenze.operatori cessazioni.IdOperatore</code>.
     */
    public Integer getIdoperatore() {
        return this.idoperatore;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OperatoriCessazioni (");

        sb.append(numerorichiesta);
        sb.append(", ").append(idoperatore);

        sb.append(")");
        return sb.toString();
    }
}
