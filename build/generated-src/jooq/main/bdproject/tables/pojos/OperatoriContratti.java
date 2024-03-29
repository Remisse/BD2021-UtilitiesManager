/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OperatoriContratti implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer numerorichiesta;
    private final Integer idoperatore;

    public OperatoriContratti(OperatoriContratti value) {
        this.numerorichiesta = value.numerorichiesta;
        this.idoperatore = value.idoperatore;
    }

    public OperatoriContratti(
        Integer numerorichiesta,
        Integer idoperatore
    ) {
        this.numerorichiesta = numerorichiesta;
        this.idoperatore = idoperatore;
    }

    /**
     * Getter for <code>utenze.operatori contratti.NumeroRichiesta</code>.
     */
    public Integer getNumerorichiesta() {
        return this.numerorichiesta;
    }

    /**
     * Getter for <code>utenze.operatori contratti.IdOperatore</code>.
     */
    public Integer getIdoperatore() {
        return this.idoperatore;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OperatoriContratti (");

        sb.append(numerorichiesta);
        sb.append(", ").append(idoperatore);

        sb.append(")");
        return sb.toString();
    }
}
