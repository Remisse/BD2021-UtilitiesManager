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
public class RichiesteAttivazione implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer   numero;
    private final LocalDate datarichiesta;
    private final Integer   numerocomponenti;
    private final String    stato;
    private final String    note;
    private final Integer   contatore;
    private final Integer   cliente;
    private final Integer   operatore;
    private final Integer   offerta;
    private final Integer   uso;
    private final Integer   attivazione;

    public RichiesteAttivazione(RichiesteAttivazione value) {
        this.numero = value.numero;
        this.datarichiesta = value.datarichiesta;
        this.numerocomponenti = value.numerocomponenti;
        this.stato = value.stato;
        this.note = value.note;
        this.contatore = value.contatore;
        this.cliente = value.cliente;
        this.operatore = value.operatore;
        this.offerta = value.offerta;
        this.uso = value.uso;
        this.attivazione = value.attivazione;
    }

    public RichiesteAttivazione(
        Integer   numero,
        LocalDate datarichiesta,
        Integer   numerocomponenti,
        String    stato,
        String    note,
        Integer   contatore,
        Integer   cliente,
        Integer   operatore,
        Integer   offerta,
        Integer   uso,
        Integer   attivazione
    ) {
        this.numero = numero;
        this.datarichiesta = datarichiesta;
        this.numerocomponenti = numerocomponenti;
        this.stato = stato;
        this.note = note;
        this.contatore = contatore;
        this.cliente = cliente;
        this.operatore = operatore;
        this.offerta = offerta;
        this.uso = uso;
        this.attivazione = attivazione;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Numero</code>.
     */
    public Integer getNumero() {
        return this.numero;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.DataRichiesta</code>.
     */
    public LocalDate getDatarichiesta() {
        return this.datarichiesta;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.NumeroComponenti</code>.
     */
    public Integer getNumerocomponenti() {
        return this.numerocomponenti;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Stato</code>.
     */
    public String getStato() {
        return this.stato;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Note</code>.
     */
    public String getNote() {
        return this.note;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Contatore</code>.
     */
    public Integer getContatore() {
        return this.contatore;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Cliente</code>.
     */
    public Integer getCliente() {
        return this.cliente;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Operatore</code>.
     */
    public Integer getOperatore() {
        return this.operatore;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Offerta</code>.
     */
    public Integer getOfferta() {
        return this.offerta;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Uso</code>.
     */
    public Integer getUso() {
        return this.uso;
    }

    /**
     * Getter for <code>utenze.richieste_attivazione.Attivazione</code>.
     */
    public Integer getAttivazione() {
        return this.attivazione;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RichiesteAttivazione (");

        sb.append(numero);
        sb.append(", ").append(datarichiesta);
        sb.append(", ").append(numerocomponenti);
        sb.append(", ").append(stato);
        sb.append(", ").append(note);
        sb.append(", ").append(contatore);
        sb.append(", ").append(cliente);
        sb.append(", ").append(operatore);
        sb.append(", ").append(offerta);
        sb.append(", ").append(uso);
        sb.append(", ").append(attivazione);

        sb.append(")");
        return sb.toString();
    }
}
