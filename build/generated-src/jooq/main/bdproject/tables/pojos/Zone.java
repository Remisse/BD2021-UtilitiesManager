/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Zone implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer idzona;
    private final String  comune;
    private final String  provincia;
    private final String  cap;

    public Zone(Zone value) {
        this.idzona = value.idzona;
        this.comune = value.comune;
        this.provincia = value.provincia;
        this.cap = value.cap;
    }

    public Zone(
        Integer idzona,
        String  comune,
        String  provincia,
        String  cap
    ) {
        this.idzona = idzona;
        this.comune = comune;
        this.provincia = provincia;
        this.cap = cap;
    }

    /**
     * Getter for <code>utenze.zone.IdZona</code>.
     */
    public Integer getIdzona() {
        return this.idzona;
    }

    /**
     * Getter for <code>utenze.zone.Comune</code>.
     */
    public String getComune() {
        return this.comune;
    }

    /**
     * Getter for <code>utenze.zone.Provincia</code>.
     */
    public String getProvincia() {
        return this.provincia;
    }

    /**
     * Getter for <code>utenze.zone.CAP</code>.
     */
    public String getCap() {
        return this.cap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Zone (");

        sb.append(idzona);
        sb.append(", ").append(comune);
        sb.append(", ").append(provincia);
        sb.append(", ").append(cap);

        sb.append(")");
        return sb.toString();
    }
}
