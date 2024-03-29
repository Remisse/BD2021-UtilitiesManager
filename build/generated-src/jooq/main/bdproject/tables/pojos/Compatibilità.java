/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Compatibilità implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer offerta;
    private final Integer uso;

    public Compatibilità(Compatibilità value) {
        this.offerta = value.offerta;
        this.uso = value.uso;
    }

    public Compatibilità(
        Integer offerta,
        Integer uso
    ) {
        this.offerta = offerta;
        this.uso = uso;
    }

    /**
     * Getter for <code>utenze.compatibilità.Offerta</code>.
     */
    public Integer getOfferta() {
        return this.offerta;
    }

    /**
     * Getter for <code>utenze.compatibilità.Uso</code>.
     */
    public Integer getUso() {
        return this.uso;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Compatibilità (");

        sb.append(offerta);
        sb.append(", ").append(uso);

        sb.append(")");
        return sb.toString();
    }
}
