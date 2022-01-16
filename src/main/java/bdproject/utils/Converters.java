package bdproject.utils;

import bdproject.tables.pojos.Contratti;
import bdproject.tables.pojos.ContrattiApprovati;

public class Converters {

    private Converters() {
    }

    public static Contratti approvedToOrdinarySub(final ContrattiApprovati approved) {
        return new Contratti(approved.getIdcontratto(),
                approved.getDataaperturarichiesta(),
                approved.getDatachiusurarichiesta(),
                approved.getStatorichiesta(),
                approved.getNoterichiesta(),
                approved.getNumerocomponenti(),
                approved.getUso(),
                approved.getOfferta(),
                approved.getTipoattivazione(),
                approved.getIdimmobile(),
                approved.getIdcliente());
    }
}
