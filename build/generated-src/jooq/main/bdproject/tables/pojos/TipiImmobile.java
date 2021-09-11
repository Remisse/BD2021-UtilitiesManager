/*
 * This file is generated by jOOQ.
 */
package bdproject.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TipiImmobile implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer codice;
    private final String  nome;
    private final Byte    hainterno;

    public TipiImmobile(TipiImmobile value) {
        this.codice = value.codice;
        this.nome = value.nome;
        this.hainterno = value.hainterno;
    }

    public TipiImmobile(
        Integer codice,
        String  nome,
        Byte    hainterno
    ) {
        this.codice = codice;
        this.nome = nome;
        this.hainterno = hainterno;
    }

    /**
     * Getter for <code>utenze.tipi_immobile.Codice</code>.
     */
    public Integer getCodice() {
        return this.codice;
    }

    /**
     * Getter for <code>utenze.tipi_immobile.Nome</code>.
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Getter for <code>utenze.tipi_immobile.HaInterno</code>.
     */
    public Byte getHainterno() {
        return this.hainterno;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TipiImmobile (");

        sb.append(codice);
        sb.append(", ").append(nome);
        sb.append(", ").append(hainterno);

        sb.append(")");
        return sb.toString();
    }
}
