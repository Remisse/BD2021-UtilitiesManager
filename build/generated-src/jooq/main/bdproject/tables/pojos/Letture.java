/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Letture implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer    numerolettura;
    private final String     matricolacontatore;
    private final LocalDate  dataeffettuazione;
    private final BigDecimal consumi;
    private final String     stato;
    private final Integer    idpersona;

    public Letture(Letture value) {
        this.numerolettura = value.numerolettura;
        this.matricolacontatore = value.matricolacontatore;
        this.dataeffettuazione = value.dataeffettuazione;
        this.consumi = value.consumi;
        this.stato = value.stato;
        this.idpersona = value.idpersona;
    }

    public Letture(
        Integer    numerolettura,
        String     matricolacontatore,
        LocalDate  dataeffettuazione,
        BigDecimal consumi,
        String     stato,
        Integer    idpersona
    ) {
        this.numerolettura = numerolettura;
        this.matricolacontatore = matricolacontatore;
        this.dataeffettuazione = dataeffettuazione;
        this.consumi = consumi;
        this.stato = stato;
        this.idpersona = idpersona;
    }

    /**
     * Getter for <code>utenze.letture.NumeroLettura</code>.
     */
    public Integer getNumerolettura() {
        return this.numerolettura;
    }

    /**
     * Getter for <code>utenze.letture.MatricolaContatore</code>.
     */
    public String getMatricolacontatore() {
        return this.matricolacontatore;
    }

    /**
     * Getter for <code>utenze.letture.DataEffettuazione</code>.
     */
    public LocalDate getDataeffettuazione() {
        return this.dataeffettuazione;
    }

    /**
     * Getter for <code>utenze.letture.Consumi</code>.
     */
    public BigDecimal getConsumi() {
        return this.consumi;
    }

    /**
     * Getter for <code>utenze.letture.Stato</code>.
     */
    public String getStato() {
        return this.stato;
    }

    /**
     * Getter for <code>utenze.letture.IdPersona</code>.
     */
    public Integer getIdpersona() {
        return this.idpersona;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Letture (");

        sb.append(numerolettura);
        sb.append(", ").append(matricolacontatore);
        sb.append(", ").append(dataeffettuazione);
        sb.append(", ").append(consumi);
        sb.append(", ").append(stato);
        sb.append(", ").append(idpersona);

        sb.append(")");
        return sb.toString();
    }
}
