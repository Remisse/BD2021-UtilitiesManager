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
public class Contratti implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer   idcontratto;
    private final LocalDate dataaperturarichiesta;
    private final LocalDate datachiusurarichiesta;
    private final String    statorichiesta;
    private final String    noterichiesta;
    private final Integer   numerocomponenti;
    private final Integer   uso;
    private final Integer   offerta;
    private final Integer   tipoattivazione;
    private final Integer   idimmobile;
    private final Integer   idcliente;
    private final LocalDate datacessazione;

    public Contratti(Contratti value) {
        this.idcontratto = value.idcontratto;
        this.dataaperturarichiesta = value.dataaperturarichiesta;
        this.datachiusurarichiesta = value.datachiusurarichiesta;
        this.statorichiesta = value.statorichiesta;
        this.noterichiesta = value.noterichiesta;
        this.numerocomponenti = value.numerocomponenti;
        this.uso = value.uso;
        this.offerta = value.offerta;
        this.tipoattivazione = value.tipoattivazione;
        this.idimmobile = value.idimmobile;
        this.idcliente = value.idcliente;
        this.datacessazione = value.datacessazione;
    }

    public Contratti(
        Integer   idcontratto,
        LocalDate dataaperturarichiesta,
        LocalDate datachiusurarichiesta,
        String    statorichiesta,
        String    noterichiesta,
        Integer   numerocomponenti,
        Integer   uso,
        Integer   offerta,
        Integer   tipoattivazione,
        Integer   idimmobile,
        Integer   idcliente,
        LocalDate datacessazione
    ) {
        this.idcontratto = idcontratto;
        this.dataaperturarichiesta = dataaperturarichiesta;
        this.datachiusurarichiesta = datachiusurarichiesta;
        this.statorichiesta = statorichiesta;
        this.noterichiesta = noterichiesta;
        this.numerocomponenti = numerocomponenti;
        this.uso = uso;
        this.offerta = offerta;
        this.tipoattivazione = tipoattivazione;
        this.idimmobile = idimmobile;
        this.idcliente = idcliente;
        this.datacessazione = datacessazione;
    }

    /**
     * Getter for <code>utenze.contratti.IdContratto</code>.
     */
    public Integer getIdcontratto() {
        return this.idcontratto;
    }

    /**
     * Getter for <code>utenze.contratti.DataAperturaRichiesta</code>.
     */
    public LocalDate getDataaperturarichiesta() {
        return this.dataaperturarichiesta;
    }

    /**
     * Getter for <code>utenze.contratti.DataChiusuraRichiesta</code>.
     */
    public LocalDate getDatachiusurarichiesta() {
        return this.datachiusurarichiesta;
    }

    /**
     * Getter for <code>utenze.contratti.StatoRichiesta</code>.
     */
    public String getStatorichiesta() {
        return this.statorichiesta;
    }

    /**
     * Getter for <code>utenze.contratti.NoteRichiesta</code>.
     */
    public String getNoterichiesta() {
        return this.noterichiesta;
    }

    /**
     * Getter for <code>utenze.contratti.NumeroComponenti</code>.
     */
    public Integer getNumerocomponenti() {
        return this.numerocomponenti;
    }

    /**
     * Getter for <code>utenze.contratti.Uso</code>.
     */
    public Integer getUso() {
        return this.uso;
    }

    /**
     * Getter for <code>utenze.contratti.Offerta</code>.
     */
    public Integer getOfferta() {
        return this.offerta;
    }

    /**
     * Getter for <code>utenze.contratti.TipoAttivazione</code>.
     */
    public Integer getTipoattivazione() {
        return this.tipoattivazione;
    }

    /**
     * Getter for <code>utenze.contratti.IdImmobile</code>.
     */
    public Integer getIdimmobile() {
        return this.idimmobile;
    }

    /**
     * Getter for <code>utenze.contratti.IdCliente</code>.
     */
    public Integer getIdcliente() {
        return this.idcliente;
    }

    /**
     * Getter for <code>utenze.contratti.DataCessazione</code>.
     */
    public LocalDate getDatacessazione() {
        return this.datacessazione;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Contratti (");

        sb.append(idcontratto);
        sb.append(", ").append(dataaperturarichiesta);
        sb.append(", ").append(datachiusurarichiesta);
        sb.append(", ").append(statorichiesta);
        sb.append(", ").append(noterichiesta);
        sb.append(", ").append(numerocomponenti);
        sb.append(", ").append(uso);
        sb.append(", ").append(offerta);
        sb.append(", ").append(tipoattivazione);
        sb.append(", ").append(idimmobile);
        sb.append(", ").append(idcliente);
        sb.append(", ").append(datacessazione);

        sb.append(")");
        return sb.toString();
    }
}
