package bdproject.model;

import bdproject.tables.pojos.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

import static bdproject.Tables.*;
import static org.jooq.impl.DSL.*;

public class Queries {

    private Queries() {}

    public static Optional<Persone> fetchPersonById(final int personId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(PERSONE)
                .where(PERSONE.IDENTIFICATIVO.eq(personId))
                .fetchOptionalInto(Persone.class);
    }

    public static Optional<ClientiDettagliati> fetchClientById(final int clientId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CLIENTI_DETTAGLIATI)
                .where(CLIENTI_DETTAGLIATI.IDENTIFICATIVO.eq(clientId))
                .fetchOptionalInto(ClientiDettagliati.class);
    }

    public static int updatePerson(final String email, final String postcode, final String municipality,
            final String streetNo, final String phone, final String province, final String street, final int personId,
            final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(PERSONE)
                .set(PERSONE.EMAIL, email)
                .set(PERSONE.CAP, postcode)
                .set(PERSONE.COMUNE, municipality)
                .set(PERSONE.NUMCIVICO, streetNo)
                .set(PERSONE.NUMEROTELEFONO, phone)
                .set(PERSONE.PROVINCIA, province)
                .set(PERSONE.VIA, street)
                .where(PERSONE.IDENTIFICATIVO.eq(personId))
                .execute();
    }

    public static int updatePersonPassword(final String password, final int personId, final Connection conn) {
        return 0;
    }

    public static int insertPersonAndReturnId(final String nhsCode, final String name, final String surname,
            final String street, final String streetNo, final String postcode, final String municipality,
            final String province, final LocalDate birthdate, final String phone, final String email,
            final String password, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(PERSONE)
                .values(defaultValue(),
                        nhsCode,
                        name,
                        surname,
                        street,
                        streetNo,
                        postcode,
                        municipality,
                        province,
                        birthdate,
                        phone,
                        email,
                        password)
                .returningResult(PERSONE.IDENTIFICATIVO)
                .execute();
    }

    public static int insertOperator(final int personId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(OPERATORI)
                .values(personId)
                .execute();
    }

    public static int insertClient(final int personId, final String income, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(CLIENTI)
                .values(personId, income)
                .execute();
    }

    public static void insertMeasurement(final Letture m, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        query.insertInto(LETTURE)
                .values(m.getConsumi(), m.getContatore(), m.getDataeffettuazione(), m.getConfermata(), m.getCliente())
                .onDuplicateKeyUpdate()
                .set(LETTURE.CONSUMI, m.getConsumi())
                .execute();
    }

    public static Optional<Letture> fetchLastMeasurement(final ContrattiDettagliati sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(LETTURE.asterisk())
                .from(LETTURE, CONTATORI)
                .where(LETTURE.CONTATORE.eq(CONTATORI.PROGRESSIVO))
                .and(CONTATORI.PROGRESSIVO.eq(sub.getContatore()))
                .orderBy(LETTURE.DATAEFFETTUAZIONE.desc())
                .limit(1)
                .fetchOptionalInto(Letture.class);
    }

    public static Optional<Letture> getLastMeasurement(final Contatori meter, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(LETTURE.asterisk())
                .from(LETTURE)
                .where(LETTURE.CONTATORE.eq(meter.getProgressivo()))
                .orderBy(LETTURE.DATAEFFETTUAZIONE.desc())
                .limit(1)
                .fetchOptionalInto(Letture.class);
    }

    public static Optional<ContrattiDettagliati> fetchSubscriptionFromId(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CONTRATTI_DETTAGLIATI)
                .where(CONTRATTI_DETTAGLIATI.IDCONTRATTO.eq(subId))
                .fetchOptionalInto(ContrattiDettagliati.class);
    }

    /**
     * TODO TO BE COMPLETED
     * @param subId
     * @param conn
     */
    public static int ceaseSubscription(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(CONTRATTI)
                .set(CONTRATTI.DATACESSAZIONE, LocalDate.now())
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .execute();
    }

    public static int insertActivationRequest(final int clientId, final LocalDate date, final int plan,
            final int meterNumber, final int usage, final int method, final int peopleNo, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(RICHIESTE_ATTIVAZIONE, RICHIESTE_ATTIVAZIONE.CLIENTE, RICHIESTE_ATTIVAZIONE.DATARICHIESTA,
                        RICHIESTE_ATTIVAZIONE.OFFERTA, RICHIESTE_ATTIVAZIONE.CONTATORE, RICHIESTE_ATTIVAZIONE.USO,
                        RICHIESTE_ATTIVAZIONE.ATTIVAZIONE, RICHIESTE_ATTIVAZIONE.NUMEROCOMPONENTI)
                .values(clientId, date, plan, meterNumber, usage, method, peopleNo)
                .execute();
    }

    public static int createSubscriptionFromRequest(final int reqNumber, final int meterNumber, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(CONTRATTI, CONTRATTI.IDCONTRATTO, CONTRATTI.DATAINIZIO)
                .select(select(
                        value(reqNumber),
                        value(LocalDate.now()))
                        .whereNotExists(selectFrom(CONTRATTI_DETTAGLIATI)
                                .where(CONTRATTI_DETTAGLIATI.CONTATORE.eq(meterNumber))
                                .and(CONTRATTI_DETTAGLIATI.DATACESSAZIONE.isNull()))
                )
                .execute();
    }

    public static MateriePrime fetchUtilityFromSubscription(final ContrattiDettagliati currentSub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(MATERIE_PRIME.asterisk())
                .from(CONTRATTI_DETTAGLIATI, MATERIE_PRIME, OFFERTE)
                .where(CONTRATTI_DETTAGLIATI.IDCONTRATTO.eq(currentSub.getIdcontratto()))
                .and(OFFERTE.CODICE.eq(CONTRATTI_DETTAGLIATI.OFFERTA))
                .and(MATERIE_PRIME.NOME.eq(OFFERTE.MATERIAPRIMA))
                .fetchOneInto(MateriePrime.class);
    }

    public static TipologieUso fetchUsageFromSub(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(TIPOLOGIE_USO.asterisk())
                .from(CONTRATTI_DETTAGLIATI, TIPOLOGIE_USO)
                .where(CONTRATTI_DETTAGLIATI.IDCONTRATTO.eq(subId))
                .and(TIPOLOGIE_USO.CODICE.eq(CONTRATTI_DETTAGLIATI.USO))
                .fetchOneInto(TipologieUso.class);
    }

    public static TipologieUso fetchUsageFromRequest(final int reqNumber, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(TIPOLOGIE_USO.asterisk())
                .from(RICHIESTE_ATTIVAZIONE, TIPOLOGIE_USO)
                .where(RICHIESTE_ATTIVAZIONE.NUMERO.eq(reqNumber))
                .and(TIPOLOGIE_USO.CODICE.eq(RICHIESTE_ATTIVAZIONE.USO))
                .fetchOneInto(TipologieUso.class);
    }

    public static TipiAttivazione fetchActivationFromSub(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(TIPI_ATTIVAZIONE.asterisk())
                .from(CONTRATTI_DETTAGLIATI, TIPI_ATTIVAZIONE)
                .where(CONTRATTI_DETTAGLIATI.IDCONTRATTO.eq(subId))
                .and(TIPI_ATTIVAZIONE.CODICE.eq(CONTRATTI_DETTAGLIATI.ATTIVAZIONE))
                .fetchOneInto(TipiAttivazione.class);
    }

    public static TipiAttivazione fetchActivationFromRequest(final int reqNumber, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(TIPI_ATTIVAZIONE.asterisk())
                .from(RICHIESTE_ATTIVAZIONE, TIPI_ATTIVAZIONE)
                .where(RICHIESTE_ATTIVAZIONE.NUMERO.eq(reqNumber))
                .and(TIPI_ATTIVAZIONE.CODICE.eq(RICHIESTE_ATTIVAZIONE.ATTIVAZIONE))
                .fetchOneInto(TipiAttivazione.class);
    }

    public static <T> List<T> fetchAll(final DSLContext ctx, final Table<?> table, final Class<T> pojo) {
        return ctx.select()
                .from(table)
                .fetchInto(pojo);
    }

    public static Immobili fetchPremisesFromMeterId(final String meterId, final DataSource dataSource) {
        Immobili premises = null;
        try (Connection conn = dataSource.getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            premises = query.select(IMMOBILI.asterisk())
                    .from(IMMOBILI, CONTATORI)
                    .where(CONTATORI.MATRICOLA.eq(meterId))
                    .and(CONTATORI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                    .fetchOneInto(Immobili.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (premises == null) {
            throw new IllegalStateException("Premises fetched from meter should not be null!");
        }
        return premises;
    }

    public static List<Interruzioni> fetchInterruptions(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(INTERRUZIONI)
                .where(INTERRUZIONI.IDCONTRATTO.eq(subId))
                .fetchInto(Interruzioni.class);
    }

    public static Optional<Interruzioni> getLastInterruption(final ContrattiDettagliati sub, final Connection conn) {
        Objects.requireNonNull(sub);
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(INTERRUZIONI)
                .where(INTERRUZIONI.IDCONTRATTO.eq(sub.getIdcontratto()))
                .orderBy(INTERRUZIONI.DATAINTERRUZIONE.desc())
                .limit(1)
                .fetchOptionalInto(Interruzioni.class);
    }

    public static boolean hasOngoingInterruption(final ContrattiDettagliati sub, final Connection conn) {
        var interruption = getLastInterruption(sub, conn);
        return interruption.isPresent() && interruption.get().getDatariattivazione() == null;
    }

    public static List<Bollette> fetchSubscriptionReports(final ContrattiDettagliati sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(BOLLETTE)
                .where(BOLLETTE.IDCONTRATTO.eq(sub.getIdcontratto()))
                .fetchInto(Bollette.class);
    }

    public static Optional<Bollette> getLastReport(final ContrattiDettagliati sub, final Connection conn) {
        return fetchSubscriptionReports(sub, conn)
                .stream()
                .max(Comparator.comparing(Bollette::getDataemissione));
    }

    public static boolean allReportsPaid(final ContrattiDettagliati sub, final Connection conn) {
        return fetchSubscriptionReports(sub, conn).stream()
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

    public static BigDecimal avgConsumptionPerZone(final Immobili premises, final String utility, final LocalDate start,
            final LocalDate end, final Connection conn) {
        final bdproject.tables.Bollette B1 = BOLLETTE;
        final bdproject.tables.ContrattiDettagliati C1 = CONTRATTI_DETTAGLIATI;
        final bdproject.tables.Contatori M1 = CONTATORI;
        final bdproject.tables.Contatori M = CONTATORI;
        final bdproject.tables.Immobili I = IMMOBILI;

        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final Table<Record2<String, BigDecimal>> T = table(query.select(M1.MATRICOLA, sum(B1.IMPORTO)
                        .as("SommaImporti"))
                .from(B1, C1, M1)
                .where(B1.IDCONTRATTO.eq(C1.IDCONTRATTO))
                .and(C1.CONTATORE.eq(M1.PROGRESSIVO))
                .and(C1.CONTATORE.in(selectDistinct(M.PROGRESSIVO)
                        .from(M, I)
                        .where(M.IDIMMOBILE.eq(I.IDIMMOBILE))
                        .and(M.MATERIAPRIMA.eq(utility))
                        .and(I.COMUNE.eq(premises.getComune()))
                        .and(I.PROVINCIA.eq(premises.getProvincia()))))
                .and(B1.DATAEMISSIONE.greaterOrEqual(start))
                .and(B1.DATAEMISSIONE.lessOrEqual(end))
                .groupBy(M1.MATRICOLA)).as("T");

        final var sums = query.select(T.field("SommaImporti"))
                .from(T)
                .fetchInto(BigDecimal.class);

        return averageBigDecimal(sums);
    }

    public static List<String> fetchAllIncomeBrackets(final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
            .from(REDDITI)
            .fetchStreamInto(Redditi.class)
            .map(Redditi::getFascia)
            .collect(Collectors.toList());
    }

    public static Map<ContrattiDettagliati, Bollette> fetchAllSubscriptionsWithLastReport(final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final var lastSubTable = query.select(
                                        CONTRATTI_DETTAGLIATI.IDCONTRATTO,
                                        max(BOLLETTE.DATAEMISSIONE).as(BOLLETTE.DATAEMISSIONE))
                                    .from(CONTRATTI_DETTAGLIATI, BOLLETTE)
                                    .where(CONTRATTI_DETTAGLIATI.IDCONTRATTO.eq(BOLLETTE.IDCONTRATTO))
                                    .groupBy(CONTRATTI_DETTAGLIATI.IDCONTRATTO)
                                    .asTable();
        return query.select(CONTRATTI_DETTAGLIATI.asterisk(), BOLLETTE.asterisk())
                .from(CONTRATTI_DETTAGLIATI.leftJoin(lastSubTable)
                        .on(CONTRATTI_DETTAGLIATI.IDCONTRATTO.eq(lastSubTable.field(CONTRATTI_DETTAGLIATI.IDCONTRATTO)))
                        .leftJoin(BOLLETTE)
                        .on(BOLLETTE.IDCONTRATTO.eq(lastSubTable.field(CONTRATTI_DETTAGLIATI.IDCONTRATTO))
                                .and(BOLLETTE.DATAEMISSIONE.eq(lastSubTable.field(BOLLETTE.DATAEMISSIONE)))))
                .stream()
                .collect(Collectors.toMap(
                        r -> new ContrattiDettagliati(
                                r.get(CONTRATTI_DETTAGLIATI.IDCONTRATTO),
                                r.get(CONTRATTI_DETTAGLIATI.DATAINIZIO),
                                r.get(CONTRATTI_DETTAGLIATI.DATACESSAZIONE),
                                r.get(CONTRATTI_DETTAGLIATI.DATARICHIESTA),
                                r.get(CONTRATTI_DETTAGLIATI.CLIENTE),
                                r.get(CONTRATTI_DETTAGLIATI.OFFERTA),
                                r.get(CONTRATTI_DETTAGLIATI.ATTIVAZIONE),
                                r.get(CONTRATTI_DETTAGLIATI.USO),
                                r.get(CONTRATTI_DETTAGLIATI.CONTATORE),
                                r.get(CONTRATTI_DETTAGLIATI.NUMEROCOMPONENTI)),
                        r -> new Bollette(
                                r.get(BOLLETTE.IDCONTRATTO),
                                r.get(BOLLETTE.DATAEMISSIONE),
                                r.get(BOLLETTE.DATASCADENZA),
                                r.get(BOLLETTE.DATAPAGAMENTO),
                                r.get(BOLLETTE.IMPORTO),
                                r.get(BOLLETTE.DETTAGLIOBOLLETTA),
                                r.get(BOLLETTE.STIMATA)))
                );
    }

    public static BigDecimal avgConsumptionFromSub(final ContrattiDettagliati sub, final LocalDate start, final LocalDate end,
            final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final bdproject.tables.Bollette B1 = BOLLETTE;
        final bdproject.tables.Contratti C1 = CONTRATTI;
        final Table<Record1<BigDecimal>> T = table(query.select(sum(B1.IMPORTO).as("SommaImporti"))
                .from(B1, C1)
                .where(C1.IDCONTRATTO.eq(sub.getIdcontratto()))
                .and(B1.IDCONTRATTO.eq(C1.IDCONTRATTO))
                .and(B1.DATAEMISSIONE.greaterOrEqual(start))
                .and(B1.DATAEMISSIONE.lessOrEqual(end)));

        final var sum = query.select(T.field("SommaImporti"))
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

    public static int interruptSubscription(final int subId, final String reason, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final var I1 = INTERRUZIONI;

        return query.insertInto(INTERRUZIONI, INTERRUZIONI.DATAINTERRUZIONE, INTERRUZIONI.IDCONTRATTO, INTERRUZIONI.MOTIVAZIONE)
                .select(query.select(
                                val(LocalDate.now()),
                                val(subId),
                                val(reason))
                        .from(CONTRATTI)
                        .where(CONTRATTI.IDCONTRATTO.eq(subId))
                        .and(CONTRATTI.DATACESSAZIONE.isNull())
                        .andNotExists(query.select()
                                .from(I1)
                                .where(I1.IDCONTRATTO.eq(subId))
                                .and(I1.DATARIATTIVAZIONE.isNull())))
                .execute();
    }

    public static int reactivateSubscription(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(INTERRUZIONI)
                .set(INTERRUZIONI.DATARIATTIVAZIONE, LocalDate.now())
                .where(INTERRUZIONI.IDCONTRATTO.eq(subId))
                .and(INTERRUZIONI.DATARIATTIVAZIONE.isNull()) // There can be only one ongoing interruption
                .execute();
    }

    public static List<Letture> fetchMeasurements(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(LETTURE.asterisk())
                .from(CONTRATTI_DETTAGLIATI, CONTATORI, LETTURE)
                .where(CONTRATTI_DETTAGLIATI.IDCONTRATTO.eq(subId))
                .and(CONTATORI.PROGRESSIVO.eq(CONTRATTI_DETTAGLIATI.CONTATORE))
                .and(LETTURE.CONTATORE.eq(CONTATORI.PROGRESSIVO))
                .and(LETTURE.DATAEFFETTUAZIONE.ge(CONTRATTI_DETTAGLIATI.DATAINIZIO))
                .and(LETTURE.DATAEFFETTUAZIONE.le(coalesce(CONTRATTI_DETTAGLIATI.DATACESSAZIONE, LETTURE.DATAEFFETTUAZIONE)))
                .fetchInto(Letture.class);
    }

    public static int confirmMeasurement(final int meterNumber, final LocalDate measurementDate, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(LETTURE)
                .set(LETTURE.CONFERMATA, (byte) 1)
                .where(LETTURE.CONTATORE.eq(meterNumber))
                .and(LETTURE.DATAEFFETTUAZIONE.eq(measurementDate))
                .and(LETTURE.CONFERMATA.eq((byte) 0))
                .execute();
    }

    public static int deleteReport(final int subId, final LocalDate publishDate, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.delete(BOLLETTE)
                .where(BOLLETTE.IDCONTRATTO.eq(subId))
                .and(BOLLETTE.DATAEMISSIONE.eq(publishDate))
                .and(BOLLETTE.DATAPAGAMENTO.isNull())
                .execute();
    }

    public static int publishReport(final int subId, final int monthsToDeadline, final BigDecimal finalCost,
                                    final byte[] reportFile, final byte estimated, final Connection conn) {
        Objects.requireNonNull(finalCost);
        Objects.requireNonNull(reportFile);

        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(BOLLETTE)
                .columns(BOLLETTE.IDCONTRATTO, BOLLETTE.DATAEMISSIONE, BOLLETTE.DATASCADENZA, BOLLETTE.IMPORTO,
                        BOLLETTE.DETTAGLIOBOLLETTA, BOLLETTE.STIMATA)
                .select(query.select(
                            val(subId),
                            val(LocalDate.now()),
                            val(LocalDate.now().plus(Period.ofMonths(monthsToDeadline))),
                            val(finalCost),
                            val(reportFile),
                            val(estimated))
                        .from(CONTRATTI)
                        .where(CONTRATTI.IDCONTRATTO.eq(subId))
                        .and(CONTRATTI.DATACESSAZIONE.isNull()))
                .execute();
    }

    public static Optional<Offerte> fetchPlanById(final int planId, final DataSource dataSource) {
        Optional<Offerte> plan = Optional.empty();
        try (Connection conn = dataSource.getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            plan = query.select()
                    .from(OFFERTE)
                    .where(OFFERTE.CODICE.eq(planId))
                    .fetchOptionalInto(Offerte.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan;
    }

    public static int deleteActivationRequest(final int number, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.delete(RICHIESTE_ATTIVAZIONE)
                .where(RICHIESTE_ATTIVAZIONE.NUMERO.eq(number))
                .and(RICHIESTE_ATTIVAZIONE.STATO.in("N", "E"))
                .execute();
    }

    public static int insertEndRequest(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(RICHIESTE_CESSAZIONE, RICHIESTE_CESSAZIONE.DATARICHIESTA, RICHIESTE_CESSAZIONE.IDCONTRATTO)
                .select(select(
                        val(LocalDate.now()),
                        val(subId))
                        .whereNotExists(selectFrom(RICHIESTE_CESSAZIONE)
                                .where(RICHIESTE_CESSAZIONE.IDCONTRATTO.eq(subId))
                                .and(RICHIESTE_CESSAZIONE.STATO.in("N", "E", "A"))))
                .execute();
    }

    public static Optional<Contatori> fetchMeterByIdAndUtility(final String meterId, final String utility,
            final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CONTATORI)
                .where(CONTATORI.MATRICOLA.eq(meterId))
                .and(CONTATORI.MATERIAPRIMA.eq(utility))
                .fetchOptionalInto(Contatori.class);
    }

    public static Optional<ContrattiDettagliati> fetchSubscriptionForChange(final String meterId, final int clientId,
                                                                            final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(CONTRATTI_DETTAGLIATI.asterisk())
                .from(CONTRATTI_DETTAGLIATI, CONTATORI)
                .where(CONTRATTI_DETTAGLIATI.CONTATORE.eq(CONTATORI.PROGRESSIVO))
                .and(CONTATORI.MATRICOLA.eq(meterId))
                .and(CONTRATTI_DETTAGLIATI.CLIENTE.eq(clientId))
                .and(CONTRATTI_DETTAGLIATI.DATACESSAZIONE.isNull())
                .fetchOptionalInto(ContrattiDettagliati.class);
    }

    public static Optional<Immobili> fetchPremisesById(final int premisesId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(IMMOBILI)
                .where(IMMOBILI.IDIMMOBILE.eq(premisesId))
                .fetchOptionalInto(Immobili.class);
    }

    public static boolean isOperator(final int personId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(OPERATORI)
                .where(OPERATORI.CODICEOPERATORE.eq(personId))
                .fetchOneInto(Operatori.class) != null;
    }

    public static Optional<TipologieUso> fetchUsageById(final int useCode, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(TIPOLOGIE_USO)
                .where(TIPOLOGIE_USO.CODICE.eq(useCode))
                .fetchOptionalInto(TipologieUso.class);
    }

    public static List<RichiesteCessazione> fetchEndRequestsFor(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(RICHIESTE_CESSAZIONE)
                .where(RICHIESTE_CESSAZIONE.IDCONTRATTO.eq(subId))
                .fetchInto(RichiesteCessazione.class);
    }

    public static int deleteEndRequest(final int requestNumber, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.delete(RICHIESTE_CESSAZIONE)
                .where(RICHIESTE_CESSAZIONE.NUMERO.eq(requestNumber))
                .and(RICHIESTE_CESSAZIONE.STATO.in("N", "E"))
                .execute();
    }

    public static int setRequestStatus(final Table<?> type, final int number, final String status,
            final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        if (!(type.equals(RICHIESTE_ATTIVAZIONE) || type.equals(RICHIESTE_CESSAZIONE))
                || !(Objects.equals(status, "E") || Objects.equals(status, "A") || Objects.equals(status, "R"))) {
            throw new IllegalArgumentException();
        }
        return query.update(type)
                .set(field("STATO"), value(status))
                .where(field("NUMERO").eq(number))
                .and(field("STATO").notIn("A", "R"))
                .execute();
    }

    public static int setRequestNotes(final Table<?> type, final int number, final String notes, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(type)
                .set(field("NOTE"), notes)
                .where(field("NUMERO").eq(number))
                .execute();
    }

    public static Integer insertPremisesReturningId(final String tipo, final String via, final String numcivico, final String comune,
                                      final String cap, final String provincia, final String interno, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(IMMOBILI, IMMOBILI.TIPO, IMMOBILI.VIA, IMMOBILI.NUMCIVICO, IMMOBILI.INTERNO,
                        IMMOBILI.COMUNE, IMMOBILI.PROVINCIA, IMMOBILI.CAP)
                .values(tipo, via, numcivico, interno, comune, provincia, cap)
                .onDuplicateKeyIgnore()
                .execute();
    }

    public static Integer insertMeterReturningId(final String matricola, final String materiaprima, final int idimmobile,
                                   final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(CONTATORI, CONTATORI.MATRICOLA, CONTATORI.MATERIAPRIMA, CONTATORI.IDIMMOBILE)
                .values(matricola, materiaprima, idimmobile)
                .onDuplicateKeyIgnore()
                .execute();
    }

    public static Optional<Immobili> fetchPremisesByCandidateKey(final String street, final String streetNo,
            @Nullable final String apartmentNumber, final String municipality, final String province, final DSLContext ctx) {
        return ctx.select()
                .from(IMMOBILI)
                .where(IMMOBILI.VIA.eq(street))
                .and(IMMOBILI.NUMCIVICO.eq(streetNo))
                .and(condition(apartmentNumber == null).and(IMMOBILI.INTERNO.isNull()).or(IMMOBILI.INTERNO.eq(apartmentNumber)))
                .and(IMMOBILI.COMUNE.eq(municipality))
                .and(IMMOBILI.PROVINCIA.eq(province))
                .fetchOptionalInto(Immobili.class);
    }

    public static Immobili fetchPremisesFromMeterNumber(final int meterNumber, final DataSource dataSource) {
        Immobili premises = null;
        try (Connection conn = dataSource.getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            premises = query.select(IMMOBILI.asterisk())
                    .from(IMMOBILI, CONTATORI)
                    .where(CONTATORI.PROGRESSIVO.eq(meterNumber))
                    .and(CONTATORI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                    .fetchOneInto(Immobili.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (premises == null) {
            throw new IllegalStateException("Premises fetched from meter should not be null!");
        }
        return premises;
    }

    public static <T, K> Optional<T> fetchByKey(final Table<?> table, final Field<K> keyField, final K keyValue,
            final Class<T> pojo, final DataSource dataSource) {
        Optional<T> item = Optional.empty();
        try (Connection conn = dataSource.getConnection()) {
            DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            item = ctx.select()
                    .from(table)
                    .where(table.field(keyField).eq(keyValue))
                    .fetchOptionalInto(pojo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public static int updateMeterId(final int meterNumber, final String meterId, final DSLContext ctx) {
        return ctx.update(CONTATORI)
                .set(CONTATORI.MATRICOLA, meterId)
                .where(CONTATORI.PROGRESSIVO.eq(meterNumber))
                .execute();
    }
}
