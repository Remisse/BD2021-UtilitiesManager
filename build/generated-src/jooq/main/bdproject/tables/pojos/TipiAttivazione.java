/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TipiAttivazione implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String     nome;
    private final BigDecimal costounatantum;

    public TipiAttivazione(TipiAttivazione value) {
        this.nome = value.nome;
        this.costounatantum = value.costounatantum;
    }

    public TipiAttivazione(
        String     nome,
        BigDecimal costounatantum
    ) {
        this.nome = nome;
        this.costounatantum = costounatantum;
    }

    /**
     * Getter for <code>utenze.tipi_attivazione.Nome</code>.
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Getter for <code>utenze.tipi_attivazione.CostoUnaTantum</code>.
     */
    public BigDecimal getCostounatantum() {
        return this.costounatantum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TipiAttivazione (");

        sb.append(nome);
        sb.append(", ").append(costounatantum);

        sb.append(")");
        return sb.toString();
    }
}