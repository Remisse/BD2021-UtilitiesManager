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
public class RichiesteCessazione implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer   numero;
    private final LocalDate datarichiesta;
    private final String    stato;
    private final String    note;
    private final Integer   idcontratto;
    private final Integer   operatore;

    public RichiesteCessazione(RichiesteCessazione value) {
        this.numero = value.numero;
        this.datarichiesta = value.datarichiesta;
        this.stato = value.stato;
        this.note = value.note;
        this.idcontratto = value.idcontratto;
        this.operatore = value.operatore;
    }

    public RichiesteCessazione(
        Integer   numero,
        LocalDate datarichiesta,
        String    stato,
        String    note,
        Integer   idcontratto,
        Integer   operatore
    ) {
        this.numero = numero;
        this.datarichiesta = datarichiesta;
        this.stato = stato;
        this.note = note;
        this.idcontratto = idcontratto;
        this.operatore = operatore;
    }

    /**
     * Getter for <code>utenze.richieste_cessazione.Numero</code>.
     */
    public Integer getNumero() {
        return this.numero;
    }

    /**
     * Getter for <code>utenze.richieste_cessazione.DataRichiesta</code>.
     */
    public LocalDate getDatarichiesta() {
        return this.datarichiesta;
    }

    /**
     * Getter for <code>utenze.richieste_cessazione.Stato</code>.
     */
    public String getStato() {
        return this.stato;
    }

    /**
     * Getter for <code>utenze.richieste_cessazione.Note</code>.
     */
    public String getNote() {
        return this.note;
    }

    /**
     * Getter for <code>utenze.richieste_cessazione.IdContratto</code>.
     */
    public Integer getIdcontratto() {
        return this.idcontratto;
    }

    /**
     * Getter for <code>utenze.richieste_cessazione.Operatore</code>.
     */
    public Integer getOperatore() {
        return this.operatore;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RichiesteCessazione (");

        sb.append(numero);
        sb.append(", ").append(datarichiesta);
        sb.append(", ").append(stato);
        sb.append(", ").append(note);
        sb.append(", ").append(idcontratto);
        sb.append(", ").append(operatore);

        sb.append(")");
        return sb.toString();
    }
}
