package bdproject.model;

import bdproject.tables.pojos.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.annotation.concurrent.Immutable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

import static bdproject.Tables.BOLLETTE;
import static bdproject.Tables.PERSONE;
import static bdproject.Tables.CONTATORI;
import static bdproject.Tables.CONTRATTI;
import static bdproject.Tables.IMMOBILI;
import static bdproject.Tables.INTERRUZIONI;
import static bdproject.Tables.LETTURE;
import static bdproject.Tables.MATERIE_PRIME;
import static bdproject.Tables.OFFERTE;
import static bdproject.Tables.REDDITI;
import static bdproject.Tables.ZONE;
import static org.jooq.impl.DSL.*;

public class Queries {

    private Queries() {}

    public static Persone getClientRecordFromSession(final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(PERSONE)
                .where(PERSONE.CODICECLIENTE.eq(SessionHolder.get().orElseThrow().getUserId()))
                .fetchOneInto(Persone.class);
    }

    public static int updateClientNoEmail(final Persone client, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(PERSONE)
                .set(PERSONE.CAP, client.getCap())
                .set(PERSONE.COMUNE, client.getComune())
                .set(PERSONE.FASCIAREDDITO, client.getFasciareddito())
                .set(PERSONE.NUMCIVICO, client.getNumcivico())
                .set(PERSONE.NUMEROTELEFONO, client.getNumerotelefono())
                .set(PERSONE.PROVINCIA, client.getProvincia())
                .set(PERSONE.VIA, client.getVia())
                .set(PERSONE.PASSWORD, client.getPassword())
                .set(PERSONE.NUMEROTELEFONO, client.getNumerotelefono())
                .where(PERSONE.CODICECLIENTE.eq(client.getCodicecliente()))
                .execute();
    }

    public static int updateClientAndEmail(final Persone client, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(PERSONE)
                .set(PERSONE.EMAIL, client.getEmail())
                .set(PERSONE.CAP, client.getCap())
                .set(PERSONE.COMUNE, client.getComune())
                .set(PERSONE.FASCIAREDDITO, client.getFasciareddito())
                .set(PERSONE.NUMCIVICO, client.getNumcivico())
                .set(PERSONE.NUMEROTELEFONO, client.getNumerotelefono())
                .set(PERSONE.PROVINCIA, client.getProvincia())
                .set(PERSONE.VIA, client.getVia())
                .set(PERSONE.PASSWORD, client.getPassword())
                .set(PERSONE.NUMEROTELEFONO, client.getNumerotelefono())
                .where(PERSONE.CODICECLIENTE.eq(client.getCodicecliente()))
                .execute();
    }

    public static void insertMeasurement(final Letture m, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        query.insertInto(LETTURE)
                .values(m.getConsumi(), m.getContatore(), m.getDataeffettuazione(), m.getConfermata())
                .onDuplicateKeyUpdate()
                .set(LETTURE.CONSUMI, m.getConsumi())
                .execute();
    }

    public static Optional<Letture> getLastMeasurement(final Contratti sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(LETTURE.asterisk())
                .from(LETTURE, CONTATORI)
                .where(LETTURE.CONTATORE.eq(CONTATORI.NUMEROPROGRESSIVO))
                .and(CONTATORI.NUMEROPROGRESSIVO.eq(sub.getContatore()))
                .orderBy(LETTURE.DATAEFFETTUAZIONE.desc())
                .limit(1)
                .fetchOptionalInto(Letture.class);
    }

    public static Optional<Letture> getLastMeasurement(final Contatori meter, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(LETTURE.asterisk())
                .from(LETTURE)
                .where(LETTURE.CONTATORE.eq(meter.getNumeroprogressivo()))
                .orderBy(LETTURE.DATAEFFETTUAZIONE.desc())
                .limit(1)
                .fetchOptionalInto(Letture.class);
    }
/*

    public static void forcePublishReportByMeasurement(final Contratti sub, final Connection conn) {
        BigDecimal total = BigDecimal.ZERO;

        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        Optional<Letture> lastMeasurement = getLastMeasurement(sub, conn);
        long daysPassed = sub.getDataultimabolletta() != null
                          ? ChronoUnit.DAYS.between(sub.getDataultimabolletta(), LocalDate.now())
                          : ChronoUnit.DAYS.between(sub.getDatainizio(), LocalDate.now());
        if (sub.getDatainizio() != null && lastMeasurement.isPresent()
                && lastMeasurement.get().getDataeffettuazione().compareTo(LocalDate.now().minusDays(5)) >= 0) {

            Optional<Letture> secondLast = query.select(LETTURE.asterisk())
                    .from(LETTURE, CONTATORI)
                    .where(LETTURE.CONTATORE.eq(sub.getContatore()))
                    .and(LETTURE.DATAEFFETTUAZIONE.lessThan(lastMeasurement.get().getDataeffettuazione()))
                    .orderBy(LETTURE.DATAEFFETTUAZIONE.desc())
                    .limit(1)
                    .fetchOptionalInto(Letture.class);
            BigDecimal consumed =
                    lastMeasurement.get().getFascia1().add(lastMeasurement.get().getFascia2()).add(lastMeasurement.get().getFascia3());
            if (secondLast.isPresent()) {
                Letture s = secondLast.orElseThrow();
                consumed = consumed.subtract(s.getFascia1()).add(s.getFascia2()).add(s.getFascia3());
            }

            Offerte plan = query.select()
                    .from(OFFERTE)
                    .where(OFFERTE.CODICE.eq(sub.getCodiceofferta()))
                    .fetchOneInto(Offerte.class);

            TipologieUso use = query.select()
                    .from(TIPOLOGIE_USO)
                    .where(TIPOLOGIE_USO.NOME.eq(sub.getTipouso()))
                    .fetchOneInto(TipologieUso.class);

            TipiAttivazione activation = query.select()
                    .from(TIPI_ATTIVAZIONE)
                    .where(TIPI_ATTIVAZIONE.NOME.eq(sub.getNomeattivazione()))
                    .fetchOneInto(TipiAttivazione.class);

            Redditi income = query.select(REDDITI.asterisk())
                    .from(REDDITI, PERSONE)
                    .where(PERSONE.CODICECLIENTE.eq(sub.getCodicecliente()))
                    .and(PERSONE.FASCIAREDDITO.eq(REDDITI.FASCIA))
                    .fetchOneInto(Redditi.class);
*/
            // ([CanoneRAI * GiorniTrascorsi/360] + ConsumiBimestre * CostoOfferta + [CostoAttivazione]) * ScontoReddito
    /*
            if (plan == null || use == null || activation == null || income == null) {
                throw new IllegalStateException("Some mandatory parts of the subscription process are null!");
            }
            total = total.add(consumed.multiply(plan.getCostomateriaprima()));
            BigDecimal partialRAI = BigDecimal.ZERO;
            if (plan.getMateriaprima().equals("Luce")) {
                System.out.println(daysPassed);
                partialRAI = partialRAI.add(use.getCanonerai().multiply(BigDecimal.valueOf(daysPassed / 360.0)));
                total = total.add(partialRAI);
            }
            if (sub.getDataultimabolletta() == null) {
                total = total.add(activation.getCostounatantum());
            }
            if (use.getScontoreddito() == 1) {
                total = total.multiply(income.getSconto());
            }

            query.insertInto(BOLLETTE)
                    .values(
                            sub.getIdcontratto(),
                            LocalDate.now(),
                            LocalDate.now().plusMonths(1),
                            null,
                            total,
                            consumed,
                            partialRAI,
                            sub.getDataultimabolletta() == null ? activation.getCostounatantum() : BigDecimal.ZERO
                    )
                    .execute();

            updateRedundantLastReport(conn);
        }
    }
*/
    public static void ceaseSubscription(final Contratti sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        query.update(CONTRATTI)
                .set(CONTRATTI.DATACESSAZIONE, LocalDate.now())
                .where(CONTRATTI.IDCONTRATTO.eq(sub.getIdcontratto()))
                .execute();
    }

    public static void insertSubscription(final SubscriptionProcess process, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        query.insertInto(CONTRATTI)
            .columns(CONTRATTI.CODICECLIENTE, CONTRATTI.DATARICHIESTA, CONTRATTI.CODICEOFFERTA,
                    CONTRATTI.CONTATORE, CONTRATTI.TIPOUSO, CONTRATTI.NOMEATTIVAZIONE, CONTRATTI.NUMEROPERSONE)
            .select(
                select(
                    value(process.getClientId()),
                    value(LocalDate.now()),
                    value(process.getPlan().orElseThrow().getCodice()),
                    value(process.getMeter().orElseThrow().getNumeroprogressivo()),
                    value(process.getUse().orElseThrow()),
                    value(process.getActivationMethod().orElseThrow().getNome()),
                    value(process.getPeopleNo()))
                .from(CONTRATTI)
                .whereNotExists(
                    selectFrom(CONTRATTI)
                    .where(CONTRATTI.CONTATORE.eq(process.getMeter().get().getNumeroprogressivo()))
                    .and(CONTRATTI.DATACESSAZIONE.isNull())))
            .execute();
    }

    public static MateriePrime getUtility(final Contratti currentSub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(MATERIE_PRIME.asterisk())
                .from(CONTRATTI, MATERIE_PRIME, OFFERTE)
                .where(CONTRATTI.IDCONTRATTO.eq(currentSub.getIdcontratto()))
                .and(OFFERTE.CODICE.eq(CONTRATTI.CODICEOFFERTA))
                .and(MATERIE_PRIME.NOME.eq(OFFERTE.MATERIAPRIMA))
                .fetchOneInto(MateriePrime.class);
    }

    public static Offerte getPlan(final Contratti sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(OFFERTE)
                .where(OFFERTE.CODICE.eq(sub.getCodiceofferta()))
                .fetchOneInto(Offerte.class);
    }

    public static Immobili getPremises(final Contratti sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(IMMOBILI.asterisk())
                .from(IMMOBILI, CONTATORI)
                .where(CONTATORI.NUMEROPROGRESSIVO.eq(sub.getContatore()))
                .and(CONTATORI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .fetchOneInto(Immobili.class);
    }

    public static List<Interruzioni> getInterruptions(final Contratti sub, final Connection conn) {
        Objects.requireNonNull(sub);
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(INTERRUZIONI)
                .where(INTERRUZIONI.IDCONTRATTO.eq(sub.getIdcontratto()))
                .fetchInto(Interruzioni.class);
    }

    public static Optional<Interruzioni> getLastInterruption(final Contratti sub, final Connection conn) {
        if (sub != null) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            return query.select()
                    .from(INTERRUZIONI)
                    .where(INTERRUZIONI.IDCONTRATTO.eq(sub.getIdcontratto()))
                    .orderBy(INTERRUZIONI.DATAINTERRUZIONE)
                    .limit(1)
                    .fetchOptionalInto(Interruzioni.class);
        }
        return Optional.empty();
    }

    public static boolean hasOngoingInterruption(final Contratti sub, final Connection conn) {
        Objects.requireNonNull(sub);
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        var interruption = getLastInterruption(sub, conn);
        return interruption.isPresent() && interruption.get().getDatariattivazione() == null;
    }

    public static List<Bollette> getReports(final Contratti sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(BOLLETTE)
                .where(BOLLETTE.IDCONTRATTO.eq(sub.getIdcontratto()))
                .fetchInto(Bollette.class);
    }

    public static Optional<Bollette> getLastReport(final Contratti sub, final Connection conn) {
        return getReports(sub, conn)
                .stream()
                .max(Comparator.comparing(Bollette::getDataemissione));
    }

    public static boolean allReportsPaid(final Contratti sub, final Connection conn) {
        return getReports(sub, conn).stream()
                .allMatch(r -> r.getDatapagamento() != null);
    }

    // From https://stackoverflow.com/a/31882656
    private static BigDecimal averageBigDecimal(List<BigDecimal> list) {
        var totalWithCount
                = list.stream()
                .filter(Objects::nonNull)
                .map(bd -> new BigDecimal[]{bd, BigDecimal.ONE})
                .reduce((a, b) -> new BigDecimal[]{a[0].add(b[0]), a[1].add(BigDecimal.ONE)});
        return totalWithCount.map(bigDecimals -> bigDecimals[0].divide(bigDecimals[1], RoundingMode.HALF_EVEN))
                .orElse(BigDecimal.ZERO);
    }

    public static BigDecimal avgConsumptionPerZone(final Zone zone, final String utility, final LocalDate start,
            final LocalDate end, final Connection conn) {
        final bdproject.tables.Bollette B1 = BOLLETTE;
        final bdproject.tables.Contratti C1 = CONTRATTI;
        final bdproject.tables.Contatori M1 = CONTATORI;
        final bdproject.tables.Contatori M = CONTATORI;
        final bdproject.tables.Immobili I = IMMOBILI;

        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final Table<Record2<Integer, BigDecimal>> T = table(query.select(M1.NUMEROPROGRESSIVO, sum(B1.CONSUMI).as("SommaConsumi"))
                .from(B1, C1, M1)
                .where(B1.IDCONTRATTO.eq(C1.IDCONTRATTO))
                .and(C1.CONTATORE.eq(M1.NUMEROPROGRESSIVO))
                .and(C1.CONTATORE.in(selectDistinct(M.NUMEROPROGRESSIVO)
                        .from(M, I)
                        .where(M.IDIMMOBILE.eq(I.IDIMMOBILE))
                        .and(M.MATERIAPRIMA.eq(utility))
                        .and(I.IDZONA.eq(zone.getIdzona()))))
                .and(B1.DATAEMISSIONE.greaterOrEqual(start))
                .and(B1.DATAEMISSIONE.lessOrEqual(end))
                .groupBy(M1.NUMEROPROGRESSIVO)).as("T");

        final var sums = query.select(T.field("SommaConsumi"))
                .from(T)
                .fetchInto(BigDecimal.class);

        return averageBigDecimal(sums);
    }

    public static List<String> getIncomeBrackets(final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
            .from(REDDITI)
            .fetchStreamInto(Redditi.class)
            .map(Redditi::getFascia)
            .collect(Collectors.toList());
    }

    public static List<Contratti> getAllSubscriptions(final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CONTRATTI)
                .fetchInto(Contratti.class);
    }

    public static Map<Contratti, Bollette> getAllSubscriptionsWithLastReport(final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final var lastSubTable = select(CONTRATTI.IDCONTRATTO, max(BOLLETTE.DATAEMISSIONE).as("DataEmissione"))
                                    .from(CONTRATTI, BOLLETTE)
                                    .where(CONTRATTI.IDCONTRATTO.eq(BOLLETTE.IDCONTRATTO))
                                    .groupBy(CONTRATTI.IDCONTRATTO)
                                    .asTable();
        return query.select(CONTRATTI.asterisk(), BOLLETTE.asterisk())
                .from(CONTRATTI.leftJoin(lastSubTable)
                        .on(CONTRATTI.IDCONTRATTO.eq(lastSubTable.field(CONTRATTI.IDCONTRATTO)))
                        .leftJoin(BOLLETTE)
                        .on(BOLLETTE.IDCONTRATTO.eq(lastSubTable.field(CONTRATTI.IDCONTRATTO))
                                .and(BOLLETTE.DATAEMISSIONE.eq(Objects.requireNonNull(lastSubTable.field("DataEmissione")).cast(LocalDate.class)))))
                .fetchMap(Contratti.class, Bollette.class);
    }

    public static Zone getZone(final Contratti sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(ZONE.COMUNE)
                .from(ZONE, IMMOBILI, CONTATORI)
                .where(CONTRATTI.IDCONTRATTO.eq(sub.getIdcontratto()))
                .and(CONTATORI.NUMEROPROGRESSIVO.eq(CONTRATTI.CONTATORE))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTATORI.IDIMMOBILE))
                .and(ZONE.IDZONA.eq(ZONE.IDZONA))
                .fetchOneInto(Zone.class);
    }

    public static BigDecimal avgConsumptionFromSub(final Contratti sub, final LocalDate start, final LocalDate end,
            final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final bdproject.tables.Bollette B1 = BOLLETTE;
        final bdproject.tables.Contratti C1 = CONTRATTI;
        final Table<Record1<BigDecimal>> T = table(query.select(sum(B1.CONSUMI).as("SommaConsumi"))
                .from(B1, C1)
                .where(C1.IDCONTRATTO.eq(sub.getIdcontratto()))
                .and(B1.IDCONTRATTO.eq(C1.IDCONTRATTO))
                .and(B1.DATAEMISSIONE.greaterOrEqual(start))
                .and(B1.DATAEMISSIONE.lessOrEqual(end)));

        final var sum = query.select(T.field("SommaConsumi"))
                .from(T)
                .fetchInto(BigDecimal.class);
        return averageBigDecimal(sum);
    }

    public static int payReport(final int subId, final LocalDate reportDate, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(BOLLETTE)
                .set(BOLLETTE.DATAPAGAMENTO, LocalDate.now())
                .where(BOLLETTE.IDCONTRATTO.eq(subId))
                .and(BOLLETTE.DATAEMISSIONE.eq(reportDate))
                .execute();
    }
}
