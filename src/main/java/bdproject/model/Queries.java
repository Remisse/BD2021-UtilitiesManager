package bdproject.model;

import bdproject.controller.types.StatusType;
import bdproject.tables.pojos.*;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static bdproject.Tables.*;
import static org.jooq.impl.DSL.*;

public class Queries {

    private Queries() {}

    public static Optional<Persone> fetchPersonById(final int personId, final Connection conn) {
        DSLContext query = using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(PERSONE)
                .where(PERSONE.IDPERSONA.eq(personId))
                .fetchOptionalInto(Persone.class);
    }

    public static Optional<ClientiDettagliati> fetchClientById(final int clientId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CLIENTI_DETTAGLIATI)
                .where(CLIENTI_DETTAGLIATI.IDPERSONA.eq(clientId))
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
                .where(PERSONE.IDPERSONA.eq(personId))
                .execute();
    }

    public static <K, V> int updateOneFieldWhere(final Table<?> table, final Field<K> keyField, final K keyValue,
            final Field<V> updatedField, final V updatedValue, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(table)
                .set(table.field(updatedField), updatedValue)
                .where(table.field(keyField).eq(keyValue))
                .execute();
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
                .returningResult(PERSONE.IDPERSONA)
                .execute();
    }

    public static int insertOperator(final int personId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(OPERATORI)
                .values(personId)
                .execute();
    }

    public static int insertClient(final int personId, final int income, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(CLIENTI, CLIENTI.CODICECLIENTE, CLIENTI.FASCIAREDDITO)
                .values(personId, income)
                .execute();
    }

    public static int insertMeasurement(final Letture m, final Connection conn) {
        DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
        return ctx.insertInto(LETTURE)
                .columns(LETTURE.MATRICOLACONTATORE, LETTURE.DATAEFFETTUAZIONE, LETTURE.CONSUMI, LETTURE.STATO, LETTURE.IDPERSONA)
                .select(ctx.select(
                            val(m.getMatricolacontatore()),
                            val(LocalDate.now()),
                            val(m.getConsumi()),
                            val(m.getStato()),
                            val(m.getIdpersona()))
                        .from(LETTURE)
                        .where(ctx.select(LETTURE.CONSUMI)
                                .from(LETTURE)
                                .where(LETTURE.MATRICOLACONTATORE.eq(m.getMatricolacontatore()))
                                .and(LETTURE.DATAEFFETTUAZIONE.lessThan(LocalDate.now()))
                                .orderBy(LETTURE.CONSUMI.desc())
                                .limit(1).lessThan(select(val(m.getConsumi())))))
                .onDuplicateKeyUpdate()
                .set(LETTURE.CONSUMI, if_(LETTURE.STATO.eq(StatusType.REVIEWING.toString()), m.getConsumi(), LETTURE.CONSUMI))
                .execute();
    }

    public static Optional<ContrattiApprovati> fetchApprovedSubscriptionById(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CONTRATTI_APPROVATI)
                .where(CONTRATTI_APPROVATI.IDCONTRATTO.eq(subId))
                .fetchOptionalInto(ContrattiApprovati.class);
    }

    public static List<ContrattiApprovati> fetchApprovedSubscriptionsByClient(final int clientId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CONTRATTI_APPROVATI)
                .where(CONTRATTI_APPROVATI.IDCLIENTE.eq(clientId))
                .fetchInto(ContrattiApprovati.class);
    }

    public static List<RichiesteContratto> fetchSubscriptionRequestsByClient(final int clientId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(RICHIESTE_CONTRATTO)
                .where(RICHIESTE_CONTRATTO.IDCLIENTE.eq(clientId))
                .fetchInto(RichiesteContratto.class);
    }

    public static int ceaseSubscription(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(CONTRATTI)
                .set(CONTRATTI.DATACESSAZIONE, LocalDate.now())
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .execute();
    }

    public static int insertSubscriptionRequest(final LocalDate requestCreationDate, final int peopleNo, final int plan,
                                                final int use, final int activationType, final int premise,
                                                final int clientId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(CONTRATTI, CONTRATTI.DATAAPERTURARICHIESTA, CONTRATTI.STATORICHIESTA, CONTRATTI.NOTERICHIESTA,
                        CONTRATTI.NUMEROCOMPONENTI, CONTRATTI.OFFERTA, CONTRATTI.USO, CONTRATTI.TIPOATTIVAZIONE,
                        CONTRATTI.IDIMMOBILE, CONTRATTI.IDCLIENTE)
                .values(requestCreationDate, StatusType.REVIEWING.toString(), "", peopleNo, plan, use, activationType,
                        premise, clientId)
                .execute();
    }

    public static int updateSubscriptionRequest(final int requestId, final String requestStatus, final String requestNotes,
                                                final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(CONTRATTI)
                .set(CONTRATTI.STATORICHIESTA, requestStatus)
                .set(CONTRATTI.NOTERICHIESTA, requestNotes)
                .where(CONTRATTI.IDCONTRATTO.eq(requestId))
                .execute();
    }

    public static int updateEndRequest(final int requestId, final String requestStatus, final String requestNotes,
                                       final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(CESSAZIONI)
                .set(CESSAZIONI.STATORICHIESTA, requestStatus)
                .set(CESSAZIONI.NOTERICHIESTA, requestNotes)
                .where(CESSAZIONI.NUMERORICHIESTA.eq(requestId))
                .execute();
    }

    public static int closeSubscriptionRequest(final int subId, final LocalDate requestCloseDate, final String requestStatus,
                                               final String requestNotes, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(CONTRATTI)
                .set(CONTRATTI.DATACHIUSURARICHIESTA, requestCloseDate)
                .set(CONTRATTI.STATORICHIESTA, requestStatus)
                .set(CONTRATTI.NOTERICHIESTA, requestNotes)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .execute();
    }

    public static int endActiveSubscription(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(CONTRATTI)
                .set(CONTRATTI.DATACESSAZIONE, LocalDate.now())
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .execute();
    }

    public static MateriePrime fetchUtilityFromSubscription(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(MATERIE_PRIME.asterisk())
                .from(CONTRATTI, MATERIE_PRIME, OFFERTE)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(OFFERTE.CODOFFERTA.eq(CONTRATTI.OFFERTA))
                .and(MATERIE_PRIME.NOME.eq(OFFERTE.MATERIAPRIMA))
                .fetchOneInto(MateriePrime.class);
    }

    public static TipologieUso fetchUsageFromSub(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(TIPOLOGIE_USO.asterisk())
                .from(CONTRATTI, TIPOLOGIE_USO)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(TIPOLOGIE_USO.CODUSO.eq(CONTRATTI.USO))
                .fetchOneInto(TipologieUso.class);
    }

    public static TipologieUso fetchUsageFromRequest(final int reqNumber, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(TIPOLOGIE_USO.asterisk())
                .from(CONTRATTI, TIPOLOGIE_USO)
                .where(CONTRATTI.IDCONTRATTO.eq(reqNumber))
                .and(TIPOLOGIE_USO.CODUSO.eq(CONTRATTI.USO))
                .fetchOneInto(TipologieUso.class);
    }

    public static TipiAttivazione fetchActivationFromSub(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(TIPI_ATTIVAZIONE.asterisk())
                .from(CONTRATTI, TIPI_ATTIVAZIONE)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(TIPI_ATTIVAZIONE.CODATTIVAZIONE.eq(CONTRATTI.TIPOATTIVAZIONE))
                .fetchOneInto(TipiAttivazione.class);
    }

    public static <T> List<T> fetchAll(final DSLContext ctx, final Table<?> table, final Class<T> pojo) {
        return ctx.select()
                .from(table)
                .fetchInto(pojo);
    }

    public static Immobili fetchPremiseFromMeterId(final String meterId, final DataSource dataSource) {
        Immobili premise = null;
        try (Connection conn = dataSource.getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            premise = query.select(IMMOBILI.asterisk())
                    .from(IMMOBILI, CONTATORI)
                    .where(CONTATORI.MATRICOLA.eq(meterId))
                    .and(CONTATORI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                    .fetchOneInto(Immobili.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (premise == null) {
            throw new IllegalStateException("premise fetched from meter should not be null!");
        }
        return premise;
    }

    public static Immobili fetchPremiseFromSubscription(final int subId, final DataSource dataSource) {
        Immobili premise = null;
        try (Connection conn = dataSource.getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            premise = query.select(IMMOBILI.asterisk())
                    .from(CONTRATTI, IMMOBILI)
                    .where(CONTRATTI.IDCONTRATTO.eq(subId))
                    .and(CONTRATTI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                    .fetchOneInto(Immobili.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (premise == null) {
            throw new IllegalStateException("premise fetched from meter should not be null!");
        }
        return premise;
    }

    public static List<Bollette> fetchSubscriptionReports(final ContrattiApprovati sub, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(BOLLETTE)
                .where(BOLLETTE.IDCONTRATTO.eq(sub.getIdcontratto()))
                .fetchInto(Bollette.class);
    }

    public static Optional<Pagamenti> fetchReportPayment(final int reportNumber, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(PAGAMENTI)
                .where(PAGAMENTI.NUMEROBOLLETTA.eq(reportNumber))
                .fetchOptionalInto(Pagamenti.class);
    }

    public static boolean allReportsPaid(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(count(BOLLETTE.NUMEROBOLLETTA).as("bolletteNonPagate"))
                .from(BOLLETTE)
                .leftJoin(PAGAMENTI).on(BOLLETTE.NUMEROBOLLETTA.eq(PAGAMENTI.NUMEROBOLLETTA))
                .where(BOLLETTE.IDCONTRATTO.eq(subId))
                .and(PAGAMENTI.DATAPAGAMENTO.isNull())
                .fetchOne()
                .component1() == 0;
    }

    // From https://stackoverflow.com/a/31882656
    private static BigDecimal averageBigDecimal(List<BigDecimal> list) {
        Optional<BigDecimal[]> totalWithCount = list.stream()
                .filter(Objects::nonNull)
                .map(bd -> new BigDecimal[]{bd, BigDecimal.ONE})
                .reduce((a, b) -> new BigDecimal[]{a[0].add(b[0]), a[1].add(BigDecimal.ONE)});
        return totalWithCount.map(bigDecimals -> bigDecimals[0].divide(bigDecimals[1], RoundingMode.HALF_EVEN))
                .orElse(BigDecimal.ZERO);
    }

    public static BigDecimal avgConsumptionPerZone(final Immobili premise, final String utility, final LocalDate start,
            final LocalDate end, final Connection conn) {
        final bdproject.tables.Bollette B1 = BOLLETTE;
        final bdproject.tables.Contratti C1 = CONTRATTI;
        final bdproject.tables.Contatori M1 = CONTATORI;
        final bdproject.tables.Contatori M = CONTATORI;
        final bdproject.tables.Immobili I = IMMOBILI;
        final bdproject.tables.Immobili I1 = IMMOBILI;

        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final Table<Record2<String, BigDecimal>> T = table(query.select(M1.MATRICOLA, sum(B1.CONSUMI)
                        .as("SommaConsumi"))
                .from(B1, C1, I1, M1)
                .where(B1.IDCONTRATTO.eq(C1.IDCONTRATTO))
                .and(C1.IDIMMOBILE.eq(I1.IDIMMOBILE))
                .and(I1.IDIMMOBILE.eq(M1.IDIMMOBILE))
                .and(M1.MATRICOLA.in(selectDistinct(M.MATRICOLA)
                        .from(M, I)
                        .where(M.IDIMMOBILE.eq(I.IDIMMOBILE))
                        .and(M.MATERIAPRIMA.eq(utility))
                        .and(I.COMUNE.eq(premise.getComune()))
                        .and(I.PROVINCIA.eq(premise.getProvincia()))))
                .and(B1.DATAEMISSIONE.greaterOrEqual(start))
                .and(B1.DATAEMISSIONE.lessOrEqual(end))
                .groupBy(M1.MATRICOLA)).as("T");

        final var sums = query.select(T.field("SommaConsumi"))
                .from(T)
                .fetchInto(BigDecimal.class);

        return averageBigDecimal(sums);
    }

    public static Map<ContrattiApprovati, Bollette> fetchAllSubscriptionsWithLastReport(final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final var lastSubTable = query.select(
                                        CONTRATTI_APPROVATI.IDCONTRATTO,
                                        max(BOLLETTE.DATAEMISSIONE).as(BOLLETTE.DATAEMISSIONE))
                                    .from(CONTRATTI_APPROVATI, BOLLETTE)
                                    .where(CONTRATTI_APPROVATI.IDCONTRATTO.eq(BOLLETTE.IDCONTRATTO))
                                    .groupBy(CONTRATTI_APPROVATI.IDCONTRATTO)
                                    .asTable();
        return query.select(CONTRATTI_APPROVATI.asterisk(), BOLLETTE.asterisk())
                .from(CONTRATTI_APPROVATI.leftJoin(lastSubTable).on(CONTRATTI_APPROVATI.IDCONTRATTO.eq(lastSubTable.field(CONTRATTI_APPROVATI.IDCONTRATTO)))
                               .leftJoin(BOLLETTE).on(BOLLETTE.IDCONTRATTO.eq(lastSubTable.field(CONTRATTI_APPROVATI.IDCONTRATTO))
                                    .and(BOLLETTE.DATAEMISSIONE.eq(lastSubTable.field(BOLLETTE.DATAEMISSIONE)))))
                .stream()
                .collect(Collectors.toMap(
                        r -> new ContrattiApprovati(
                                r.get(CONTRATTI_APPROVATI.IDCONTRATTO),
                                r.get(CONTRATTI_APPROVATI.DATAAPERTURARICHIESTA),
                                r.get(CONTRATTI_APPROVATI.DATACHIUSURARICHIESTA),
                                r.get(CONTRATTI_APPROVATI.STATORICHIESTA),
                                r.get(CONTRATTI_APPROVATI.NOTERICHIESTA),
                                r.get(CONTRATTI_APPROVATI.NUMEROCOMPONENTI),
                                r.get(CONTRATTI_APPROVATI.USO),
                                r.get(CONTRATTI_APPROVATI.OFFERTA),
                                r.get(CONTRATTI_APPROVATI.TIPOATTIVAZIONE),
                                r.get(CONTRATTI_APPROVATI.IDIMMOBILE),
                                r.get(CONTRATTI_APPROVATI.IDCLIENTE),
                                r.get(CONTRATTI_APPROVATI.DATACESSAZIONE)),
                        r -> new Bollette(
                                r.get(BOLLETTE.NUMEROBOLLETTA),
                                r.get(BOLLETTE.DATAEMISSIONE),
                                r.get(BOLLETTE.DATAINIZIOPERIODO),
                                r.get(BOLLETTE.DATAFINEPERIODO),
                                r.get(BOLLETTE.DATASCADENZA),
                                r.get(BOLLETTE.IMPORTO),
                                r.get(BOLLETTE.CONSUMI),
                                r.get(BOLLETTE.DOCUMENTODETTAGLIATO),
                                r.get(BOLLETTE.STIMATA),
                                r.get(BOLLETTE.IDOPERATORE),
                                r.get(BOLLETTE.IDCONTRATTO)))
                );
    }

    public static BigDecimal avgConsumptionFromSub(final int subId, final LocalDate start, final LocalDate end,
            final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        final bdproject.tables.Bollette B1 = BOLLETTE;
        final bdproject.tables.Contratti C1 = CONTRATTI;
        final Table<Record1<BigDecimal>> T = table(query.select(sum(B1.IMPORTO).as("SommaImporti"))
                .from(B1, C1)
                .where(C1.IDCONTRATTO.eq(subId))
                .and(B1.IDCONTRATTO.eq(C1.IDCONTRATTO))
                .and(B1.DATAEMISSIONE.greaterOrEqual(start))
                .and(B1.DATAEMISSIONE.lessOrEqual(end)));

        final var sum = query.select(T.field("SommaImporti"))
                .from(T)
                .fetchInto(BigDecimal.class);
        return averageBigDecimal(sum);
    }

    public static int payReport(final int reportId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(PAGAMENTI)
                .values(reportId, LocalDate.now())
                .execute();
    }

    public static List<Letture> fetchMeasurements(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(LETTURE.asterisk())
                .from(CONTRATTI_APPROVATI, OFFERTE, IMMOBILI, CONTATORI, LETTURE)
                .where(CONTRATTI_APPROVATI.IDCONTRATTO.eq(subId))
                .and(CONTRATTI_APPROVATI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTRATTI_APPROVATI.IDIMMOBILE))
                .and(OFFERTE.CODOFFERTA.eq(CONTRATTI_APPROVATI.OFFERTA))
                .and(CONTATORI.MATERIAPRIMA.eq(OFFERTE.MATERIAPRIMA))
                .and(LETTURE.MATRICOLACONTATORE.eq(CONTATORI.MATRICOLA))
                .and(LETTURE.DATAEFFETTUAZIONE.ge(CONTRATTI_APPROVATI.DATACHIUSURARICHIESTA))
                .and(LETTURE.DATAEFFETTUAZIONE.le(coalesce(CONTRATTI_APPROVATI.DATACESSAZIONE, LETTURE.DATAEFFETTUAZIONE)))
                .fetchInto(Letture.class);
    }

    public static int setMeasurementStatus(final int measurementId, final String status, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.update(LETTURE)
                .set(LETTURE.STATO, status)
                .where(LETTURE.NUMEROLETTURA.eq(measurementId))
                .execute();
    }

    public static int deleteReport(final int reportId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.delete(BOLLETTE)
                .where(BOLLETTE.NUMEROBOLLETTA.in(select(PAGAMENTI.NUMEROBOLLETTA)
                        .from(PAGAMENTI)
                        .rightJoin(BOLLETTE).on(PAGAMENTI.NUMEROBOLLETTA.eq(BOLLETTE.NUMEROBOLLETTA))
                        .where(PAGAMENTI.NUMEROBOLLETTA.eq(reportId))
                        .and(PAGAMENTI.DATAPAGAMENTO.isNull())))
                .execute();
    }

    public static int publishReport(final LocalDate intervalStartDate, final LocalDate intervalEndDate,
                                    final int monthsToDeadline, final BigDecimal finalCost, final BigDecimal consumption,
                                    final byte[] reportFile, final byte estimated, final int operatorId, final int subId,
                                    final Connection conn) {
        Objects.requireNonNull(finalCost);
        Objects.requireNonNull(reportFile);

        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(BOLLETTE)
                .columns(BOLLETTE.DATAEMISSIONE, BOLLETTE.DATAINIZIOPERIODO, BOLLETTE.DATAFINEPERIODO, BOLLETTE.DATASCADENZA,
                        BOLLETTE.IMPORTO, BOLLETTE.CONSUMI, BOLLETTE.DOCUMENTODETTAGLIATO, BOLLETTE.STIMATA,
                        BOLLETTE.IDOPERATORE, BOLLETTE.IDCONTRATTO)
                .select(query.select(
                            val(LocalDate.now()),
                            val(intervalStartDate),
                            val(intervalEndDate),
                            val(LocalDate.now().plus(Period.ofMonths(monthsToDeadline))),
                            val(finalCost),
                            val(consumption),
                            val(reportFile),
                            val(estimated),
                            val(operatorId),
                            val(subId))
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
                    .where(OFFERTE.CODOFFERTA.eq(planId))
                    .fetchOptionalInto(Offerte.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan;
    }

    public static int deleteSubscriptionRequest(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.delete(CONTRATTI)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(CONTRATTI.DATACHIUSURARICHIESTA.isNull())
                .execute();
    }

    public static int insertEndRequest(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(CESSAZIONI, CESSAZIONI.DATAAPERTURARICHIESTA, CESSAZIONI.STATORICHIESTA,
                        CESSAZIONI.NOTERICHIESTA, CESSAZIONI.IDCONTRATTO)
                .select(select(
                        val(LocalDate.now()),
                        val(StatusType.REVIEWING.toString()),
                        val(""),
                        val(subId))
                        .whereNotExists(selectFrom(CESSAZIONI)
                                .where(CESSAZIONI.IDCONTRATTO.eq(subId))
                                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNull())))
                .execute();
    }

    public static Optional<Contatori> fetchMeterBySubscription(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(CONTATORI.asterisk())
                .from(CONTRATTI, IMMOBILI, CONTATORI, OFFERTE)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTRATTI.IDIMMOBILE))
                .and(CONTATORI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .and(OFFERTE.CODOFFERTA.eq(CONTRATTI.OFFERTA))
                .and(OFFERTE.MATERIAPRIMA.eq(CONTATORI.MATERIAPRIMA))
                .fetchOptionalInto(Contatori.class);
    }

    public static Optional<Contatori> fetchMeterById(final String meterId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CONTATORI)
                .where(CONTATORI.MATRICOLA.eq(meterId))
                .fetchOptionalInto(Contatori.class);
    }

    public static Optional<Contratti> fetchSubscriptionForChange(final String meterId, final int clientId,
                                                                 final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select(CONTRATTI.asterisk())
                .from(CONTRATTI, IMMOBILI, CONTATORI)
                .where(CONTRATTI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTATORI.IDIMMOBILE))
                .and(CONTATORI.MATRICOLA.eq(meterId))
                .and(CONTRATTI.IDCLIENTE.eq(clientId))
                .and(CONTRATTI.DATACESSAZIONE.isNull())
                .fetchOptionalInto(Contratti.class);
    }

    public static Optional<Immobili> fetchPremiseById(final int estateId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(IMMOBILI)
                .where(IMMOBILI.IDIMMOBILE.eq(estateId))
                .fetchOptionalInto(Immobili.class);
    }

    public static boolean isOperator(final int personId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(OPERATORI)
                .where(OPERATORI.IDOPERATORE.eq(personId))
                .fetchOneInto(Operatori.class) != null;
    }

    public static Optional<TipologieUso> fetchUsageById(final int useCode, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(TIPOLOGIE_USO)
                .where(TIPOLOGIE_USO.CODUSO.eq(useCode))
                .fetchOptionalInto(TipologieUso.class);
    }

    public static List<Cessazioni> fetchEndRequestsFor(final int subId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.select()
                .from(CESSAZIONI)
                .where(CESSAZIONI.IDCONTRATTO.eq(subId))
                .fetchInto(Cessazioni.class);
    }

    public static int deleteEndRequest(final int requestNumber, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.delete(CESSAZIONI)
                .where(CESSAZIONI.NUMERORICHIESTA.eq(requestNumber))
                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNull())
                .execute();
    }

    public static int insertPremise(final String type, final String street, final String civic,
                                    final String unit, final String municipality, final String postcode,
                                    final String province, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(IMMOBILI, IMMOBILI.TIPO, IMMOBILI.VIA, IMMOBILI.NUMCIVICO, IMMOBILI.INTERNO,
                        IMMOBILI.COMUNE, IMMOBILI.PROVINCIA, IMMOBILI.CAP)
                .values(type, street, civic, unit, municipality, province, postcode)
                .onDuplicateKeyIgnore()
                .execute();
    }

    public static int insertMeter(final String meterId, final String utility, final int premiseId, final Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
        return query.insertInto(CONTATORI, CONTATORI.MATRICOLA, CONTATORI.MATERIAPRIMA, CONTATORI.IDIMMOBILE)
                .values(meterId, utility, premiseId)
                .onDuplicateKeyIgnore()
                .execute();
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

    public static Optional<Record2<Integer, String>> fetchPersonIdAndName(final String email, final String password,
            final DSLContext ctx) {
       return ctx.select(PERSONE.IDPERSONA, PERSONE.NOME)
                .from(PERSONE)
                .where(PERSONE.EMAIL.eq(email))
                .and(PERSONE.PASSWORD.eq(password))
                .fetchOptional();
    }

    public static int insertPlan(final String name, final String description, final String utility,
            final BigDecimal cost, final boolean active, final DSLContext ctx) {
        return ctx.insertInto(OFFERTE, OFFERTE.NOME, OFFERTE.DESCRIZIONE, OFFERTE.MATERIAPRIMA, OFFERTE.COSTOMATERIAPRIMA,
                OFFERTE.ATTIVA)
                .values(name, description, utility, cost, active ? (byte) 1 : (byte) 0)
                .execute();
    }

    public static int updatePlan(final int id, final String name, final String description, final boolean active,
                                 final DSLContext ctx) {
        return ctx.update(OFFERTE)
                .set(OFFERTE.NOME, name)
                .set(OFFERTE.DESCRIZIONE, description)
                .set(OFFERTE.ATTIVA, active ? (byte) 1 : (byte) 0)
                .where(OFFERTE.CODOFFERTA.eq(id))
                .execute();
    }

    public static <K> int deleteGeneric(final Table<?> table, final Field<K> keyField, final K keyValue, final DSLContext ctx) {
        return ctx.delete(table)
                .where(table.field(keyField).eq(keyValue))
                .execute();
    }

    /*
    public static int updateLastReportRedundant(final int subId, final LocalDate lastReport, final Connection conn) {
        DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
        return ctx.update(CONTRATTI)
                .set(CONTRATTI.DATAULTIMABOLLETTA, lastReport)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .execute();
    } */

    public static int insertUse(final String name, final BigDecimal estimate, final byte discount, final DSLContext ctx) {
        return ctx.insertInto(TIPOLOGIE_USO, TIPOLOGIE_USO.NOME, TIPOLOGIE_USO.STIMAPERPERSONA, TIPOLOGIE_USO.SCONTOREDDITO)
                .values(name, estimate, discount)
                .execute();
    }

    public static int updateUse(final int useId, final String name, final BigDecimal estimate, final byte discount, final DSLContext ctx) {
        return ctx.update(TIPOLOGIE_USO)
                .set(TIPOLOGIE_USO.NOME, name)
                .set(TIPOLOGIE_USO.STIMAPERPERSONA, estimate)
                .set(TIPOLOGIE_USO.SCONTOREDDITO, discount)
                .where(TIPOLOGIE_USO.CODUSO.eq(useId))
                .execute();
    }

    public static List<Record4<Integer, String, Integer, String>> fetchCompatibilities(final DSLContext ctx) {
        return ctx.select(TIPOLOGIE_USO.CODUSO, TIPOLOGIE_USO.NOME, OFFERTE.CODOFFERTA, OFFERTE.NOME)
                .from(TIPOLOGIE_USO, OFFERTE, COMPATIBILITÀ)
                .where(TIPOLOGIE_USO.CODUSO.eq(COMPATIBILITÀ.USO))
                .and(OFFERTE.CODOFFERTA.eq(COMPATIBILITÀ.OFFERTA))
                .fetchStream()
                .collect(Collectors.toList());
    }

    public static int insertCompatibility(final int useId, final int planId, final DSLContext ctx) {
        return ctx.insertInto(COMPATIBILITÀ, COMPATIBILITÀ.USO, COMPATIBILITÀ.OFFERTA)
                .values(useId, planId)
                .execute();
    }

    public static int deleteCompatibility(final int useId, final int planId, final DSLContext ctx) {
        return ctx.delete(COMPATIBILITÀ)
                .where(COMPATIBILITÀ.USO.eq(useId))
                .and(COMPATIBILITÀ.OFFERTA.eq(planId))
                .execute();
    }

    public static int fetchLastInsertId(final Connection conn) {
        final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
        return ctx.select(field("last_insert_id()", SQLDataType.INTEGER))
                .fetchOne()
                .component1();
    }

    public static Optional<OperatoriContratti> fetchSubscriptionRequestAssignment(final int requestId, final Connection conn) {
        final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);

        return ctx.select()
                .from(OPERATORI_CONTRATTI)
                .where(OPERATORI_CONTRATTI.NUMERORICHIESTA.eq(requestId))
                .fetchOptionalInto(OperatoriContratti.class);
    }

    public static Optional<OperatoriCessazioni> fetchEndRequestAssignment(final int requestId, final Connection conn) {
        final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);

        return ctx.select()
                .from(OPERATORI_CESSAZIONI)
                .where(OPERATORI_CESSAZIONI.NUMERORICHIESTA.eq(requestId))
                .fetchOptionalInto(OperatoriCessazioni.class);
    }

    public static List<RichiesteContratto> fetchSubscriptionRequestsAssignedToOperator(final int operatorId, final Connection conn) {
        final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);

        return ctx.select(RICHIESTE_CONTRATTO.asterisk())
                .from(RICHIESTE_CONTRATTO, OPERATORI_CONTRATTI)
                .where(OPERATORI_CONTRATTI.IDOPERATORE.eq(operatorId))
                .and(RICHIESTE_CONTRATTO.IDCONTRATTO.eq(OPERATORI_CONTRATTI.IDOPERATORE))
                .fetchInto(RichiesteContratto.class);
    }

    public static List<Cessazioni> fetchEndRequestsAssignedToOperator(final int operatorId, final Connection conn) {
        final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);

        return ctx.select(CESSAZIONI.asterisk())
                .from(CESSAZIONI, OPERATORI_CESSAZIONI)
                .where(OPERATORI_CESSAZIONI.IDOPERATORE.eq(operatorId))
                .and(CESSAZIONI.IDCONTRATTO.eq(OPERATORI_CESSAZIONI.IDOPERATORE))
                .fetchInto(Cessazioni.class);
    }

    public static int activateSubscription(final int requestId, final Connection conn) {
        final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);

        return ctx.update(CONTRATTI)
                .set(CONTRATTI.DATACHIUSURARICHIESTA, LocalDate.now())
                .set(CONTRATTI.STATORICHIESTA, StatusType.APPROVED.toString())
                .where(CONTRATTI.IDCONTRATTO.eq(requestId))
                .execute();
    }
}
