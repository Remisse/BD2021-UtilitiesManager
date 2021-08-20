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
public class Interruzioni implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer   idcontratto;
    private final LocalDate datainterruzione;
    private final LocalDate datariattivazione;
    private final String    descrizione;

    public Interruzioni(Interruzioni value) {
        this.idcontratto = value.idcontratto;
        this.datainterruzione = value.datainterruzione;
        this.datariattivazione = value.datariattivazione;
        this.descrizione = value.descrizione;
    }

    public Interruzioni(
        Integer   idcontratto,
        LocalDate datainterruzione,
        LocalDate datariattivazione,
        String    descrizione
    ) {
        this.idcontratto = idcontratto;
        this.datainterruzione = datainterruzione;
        this.datariattivazione = datariattivazione;
        this.descrizione = descrizione;
    }

    /**
     * Getter for <code>utenze.interruzioni.IdContratto</code>.
     */
    public Integer getIdcontratto() {
        return this.idcontratto;
    }

    /**
     * Getter for <code>utenze.interruzioni.DataInterruzione</code>.
     */
    public LocalDate getDatainterruzione() {
        return this.datainterruzione;
    }

    /**
     * Getter for <code>utenze.interruzioni.DataRiattivazione</code>.
     */
    public LocalDate getDatariattivazione() {
        return this.datariattivazione;
    }

    /**
     * Getter for <code>utenze.interruzioni.Descrizione</code>.
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Interruzioni (");

        sb.append(idcontratto);
        sb.append(", ").append(datainterruzione);
        sb.append(", ").append(datariattivazione);
        sb.append(", ").append(descrizione);

        sb.append(")");
        return sb.toString();
    }
}
