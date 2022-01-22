package bdproject.model;

import bdproject.model.types.StatusType;
import bdproject.tables.pojos.*;
import io.r2dbc.spi.Parameter;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static bdproject.Tables.*;
import static org.jooq.impl.DSL.*;

public class Queries {

    private Queries() {
    }

    public static DSLContext createContext(final Connection conn) {
        return DSL.using(conn, SQLDialect.MYSQL);
    }

    public static Optional<Persone> fetchPersonById(final int personId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select()
                .from(PERSONE)
                .where(PERSONE.IDPERSONA.eq(personId))
                .fetchOptionalInto(Persone.class);
    }

    public static Optional<ClientiDettagliati> fetchClientById(final int clientId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select()
                .from(CLIENTI_DETTAGLIATI)
                .where(CLIENTI_DETTAGLIATI.IDPERSONA.eq(clientId))
                .fetchOptionalInto(ClientiDettagliati.class);
    }

    public static int updatePerson(final String email, final String postcode, final String municipality,
            final String streetNo, final String phone, final String province, final String street, final int personId,
            final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(PERSONE)
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

    public static int updatePersonPassword(final int personId, final String password, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(PERSONE)
                .set(PERSONE.PASSWORD, password)
                .where(PERSONE.IDPERSONA.eq(personId))
                .execute();
    }

    public static int updateClientIncome(final int clientId, final int incomeId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(CLIENTI)
                .set(CLIENTI.FASCIAREDDITO, incomeId)
                .where(CLIENTI.CODICECLIENTE.eq(clientId))
                .execute();
    }

    public static int insertPersonAndReturnId(final String nhsCode, final String name, final String surname,
            final String street, final String streetNo, final String postcode, final String municipality,
            final String province, final LocalDate birthdate, final String phone, final String email,
            final String password, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .insertInto(PERSONE)
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

    public static int insertOperator(final int personId, final BigDecimal salary, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .insertInto(OPERATORI)
                .values(personId, salary)
                .execute();
    }

    public static int insertClient(final int personId, final int income, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .insertInto(CLIENTI, CLIENTI.CODICECLIENTE, CLIENTI.FASCIAREDDITO)
                .values(personId, income)
                .execute();
    }

    public static int insertMeasurement(final String meterId, final BigDecimal consumption, final String notes,
            final int clientId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx
                .insertInto(LETTURE, LETTURE.MATRICOLACONTATORE, LETTURE.DATAEFFETTUAZIONE, LETTURE.CONSUMI, LETTURE.STATO,
                        LETTURE.NOTE, LETTURE.IDCLIENTE)
                .select(ctx.select(val(meterId), val(LocalDate.now()), val(consumption), val(StatusType.REVIEWING.toString()),
                        val(notes), val(clientId))
                        .whereExists(ctx.select(CONTRATTI_ATTIVI.IDCONTRATTO)
                                .from(CONTRATTI_ATTIVI, CONTATORI, IMMOBILI, OFFERTE)
                                .where(CONTATORI.MATRICOLA.eq(meterId))
                                .and(CONTATORI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                                .and(IMMOBILI.IDIMMOBILE.eq(CONTRATTI_ATTIVI.IDIMMOBILE))
                                .and(CONTRATTI_ATTIVI.IDCLIENTE.eq(clientId))
                                .and(CONTRATTI_ATTIVI.OFFERTA.eq(OFFERTE.CODOFFERTA))
                                .and(OFFERTE.MATERIAPRIMA.eq(CONTATORI.MATERIAPRIMA)))
                        .and(notExists(ctx.select(LETTURE.CONSUMI)
                                .from(LETTURE)
                                .where(LETTURE.MATRICOLACONTATORE.eq(meterId))
                                .and(LETTURE.STATO.eq(StatusType.APPROVED.toString()))
                                .and(LETTURE.DATACHIUSURARICHIESTA.isNotNull())
                                .orderBy(LETTURE.DATAEFFETTUAZIONE)
                                .limit(1))
                            .or(ctx.select(LETTURE.CONSUMI)
                                .from(LETTURE)
                                .where(LETTURE.MATRICOLACONTATORE.eq(meterId))
                                .and(LETTURE.STATO.eq(StatusType.APPROVED.toString()))
                                .and(LETTURE.DATACHIUSURARICHIESTA.isNotNull())
                                .orderBy(LETTURE.DATAEFFETTUAZIONE)
                                .limit(1).le(select(val(consumption))))))
                .onDuplicateKeyUpdate()
                .set(LETTURE.CONSUMI, if_(LETTURE.STATO.eq(StatusType.REVIEWING.toString()), consumption, LETTURE.CONSUMI))
                .execute();
    }

    public static Optional<ContrattiApprovati> fetchApprovedSubscriptionById(final int subId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select()
                .from(CONTRATTI_APPROVATI)
                .where(CONTRATTI_APPROVATI.IDCONTRATTO.eq(subId))
                .fetchOptionalInto(ContrattiApprovati.class);
    }

    public static List<ContrattiApprovati> fetchApprovedSubscriptionsByClient(final int clientId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select()
                .from(CONTRATTI_APPROVATI)
                .where(CONTRATTI_APPROVATI.IDCLIENTE.eq(clientId))
                .fetchInto(ContrattiApprovati.class);
    }

    public static List<RichiesteContratto> fetchSubscriptionRequestsByClient(final int clientId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select()
                .from(RICHIESTE_CONTRATTO)
                .where(RICHIESTE_CONTRATTO.IDCLIENTE.eq(clientId))
                .fetchInto(RichiesteContratto.class);
    }

    public static int insertSubscriptionRequest(final LocalDate requestCreationDate, final int peopleNo, final int plan,
                                                final int use, final int activationType, final int premise,
                                                final int clientId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .insertInto(CONTRATTI, CONTRATTI.DATAAPERTURARICHIESTA, CONTRATTI.STATORICHIESTA, CONTRATTI.NOTERICHIESTA,
                        CONTRATTI.NUMEROCOMPONENTI, CONTRATTI.OFFERTA, CONTRATTI.USO, CONTRATTI.TIPOATTIVAZIONE,
                        CONTRATTI.IDIMMOBILE, CONTRATTI.IDCLIENTE)
                .values(requestCreationDate, StatusType.REVIEWING.toString(), "", peopleNo, plan, use, activationType,
                        premise, clientId)
                .execute();
    }

    public static int markSubscriptionRequestAsRejected(final int requestId, final String requestNotes, final int operatorId,
            final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(CONTRATTI)
                .set(CONTRATTI.STATORICHIESTA, StatusType.REJECTED.toString())
                .set(CONTRATTI.NOTERICHIESTA, requestNotes)
                .set(CONTRATTI.DATACHIUSURARICHIESTA, LocalDate.now())
                .where(CONTRATTI.IDCONTRATTO.eq(requestId))
                .and(CONTRATTI.DATACHIUSURARICHIESTA.isNull())
                .andExists(selectFrom(OPERATORI_CONTRATTI)
                        .where(OPERATORI_CONTRATTI.NUMERORICHIESTA.eq(requestId))
                        .and(OPERATORI_CONTRATTI.IDOPERATORE.eq(operatorId)))
                .execute();
    }

    public static int markEndRequestAsApproved(final int requestId, final String requestNotes, final int operatorId,
            final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(CESSAZIONI)
                .set(CESSAZIONI.STATORICHIESTA, StatusType.APPROVED.toString())
                .set(CESSAZIONI.NOTERICHIESTA, requestNotes)
                .set(CESSAZIONI.DATACHIUSURARICHIESTA, LocalDate.now())
                .where(CESSAZIONI.NUMERORICHIESTA.eq(requestId))
                .and(CESSAZIONI.STATORICHIESTA.eq(StatusType.REVIEWING.toString()))
                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNull())
                .andExists(selectFrom(OPERATORI_CESSAZIONI)
                        .where(OPERATORI_CESSAZIONI.NUMERORICHIESTA.eq(requestId))
                        .and(OPERATORI_CESSAZIONI.IDOPERATORE.eq(operatorId)))
                .andNotExists(query.select(CESSAZIONI.NUMERORICHIESTA)
                        .from(CESSAZIONI)
                        .where(CESSAZIONI.IDCONTRATTO.eq(query.select(CESSAZIONI.IDCONTRATTO)
                                .from(CESSAZIONI)
                                .where(CESSAZIONI.NUMERORICHIESTA.eq(requestId))))
                        .and(CESSAZIONI.STATORICHIESTA.eq(StatusType.APPROVED.toString()))
                        .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNotNull()))
                .execute();
    }

    public static int markEndRequestAsRejected(final int requestId, final String requestNotes, final int operatorId,
            final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(CESSAZIONI)
                .set(CESSAZIONI.STATORICHIESTA, StatusType.REJECTED.toString())
                .set(CESSAZIONI.NOTERICHIESTA, requestNotes)
                .set(CESSAZIONI.DATACHIUSURARICHIESTA, LocalDate.now())
                .where(CESSAZIONI.NUMERORICHIESTA.eq(requestId))
                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNull())
                .andExists(selectFrom(OPERATORI_CESSAZIONI)
                        .where(OPERATORI_CESSAZIONI.NUMERORICHIESTA.eq(requestId))
                        .and(OPERATORI_CESSAZIONI.IDOPERATORE.eq(operatorId)))
                .execute();
    }

    public static int updateSubscriptionRequestNotes(final int requestId, final String requestNotes, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(CONTRATTI)
                .set(CONTRATTI.NOTERICHIESTA, requestNotes)
                .where(CONTRATTI.IDCONTRATTO.eq(requestId))
                .and(CONTRATTI.DATACHIUSURARICHIESTA.isNull())
                .execute();
    }

    public static int updateEndRequestNotes(final int requestId, final String requestNotes, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(CESSAZIONI)
                .set(CESSAZIONI.NOTERICHIESTA, requestNotes)
                .where(CESSAZIONI.NUMERORICHIESTA.eq(requestId))
                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNull())
                .execute();
    }

    public static String fetchUtilityFromSubscription(final int subId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select(OFFERTE.MATERIAPRIMA)
                .from(CONTRATTI, OFFERTE)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(OFFERTE.CODOFFERTA.eq(CONTRATTI.OFFERTA))
                .fetchOneInto(String.class);
    }

    public static TipologieUso fetchUsageFromSub(final int subId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select(TIPOLOGIE_USO.asterisk())
                .from(CONTRATTI, TIPOLOGIE_USO)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(TIPOLOGIE_USO.CODUSO.eq(CONTRATTI.USO))
                .fetchOneInto(TipologieUso.class);
    }

    public static TipologieUso fetchUsageFromRequest(final int reqNumber, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select(TIPOLOGIE_USO.asterisk())
                .from(CONTRATTI, TIPOLOGIE_USO)
                .where(CONTRATTI.IDCONTRATTO.eq(reqNumber))
                .and(TIPOLOGIE_USO.CODUSO.eq(CONTRATTI.USO))
                .fetchOneInto(TipologieUso.class);
    }

    public static TipiAttivazione fetchActivationFromSub(final int subId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select(TIPI_ATTIVAZIONE.asterisk())
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
            final DSLContext query = createContext(conn);

            premise = query
                    .select(IMMOBILI.asterisk())
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
            final DSLContext query = createContext(conn);

            premise = query
                    .select(IMMOBILI.asterisk())
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

    public static List<Bollette> fetchSubscriptionReports(final int subId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select()
                .from(BOLLETTE)
                .where(BOLLETTE.IDCONTRATTO.eq(subId))
                .fetchInto(Bollette.class);
    }

    public static Optional<Pagamenti> fetchReportPayment(final int reportNumber, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select()
                .from(PAGAMENTI)
                .where(PAGAMENTI.NUMEROBOLLETTA.eq(reportNumber))
                .fetchOptionalInto(Pagamenti.class);
    }

    public static boolean areAllReportsPaid(final int subId, final Connection conn) {
        final DSLContext query = createContext(conn);

        /*
         * False positive
         */
        return query
                .selectCount()
                .from(BOLLETTE)
                .leftJoin(PAGAMENTI).on(BOLLETTE.NUMEROBOLLETTA.eq(PAGAMENTI.NUMEROBOLLETTA))
                .where(BOLLETTE.IDCONTRATTO.eq(subId))
                .and(PAGAMENTI.DATAPAGAMENTO.isNull())
                .fetchOne(0, int.class) == 0;
    }

    public static BigDecimal avgConsumptionPerZone(final Immobili premise, final String utility, final LocalDate start,
                                                   final LocalDate end, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .select(avg(BOLLETTE.CONSUMI))
                .from(BOLLETTE, CONTRATTI_APPROVATI, IMMOBILI, OFFERTE)
                .where(OFFERTE.MATERIAPRIMA.eq(utility))
                .and(CONTRATTI_APPROVATI.OFFERTA.eq(OFFERTE.CODOFFERTA))
                .and(IMMOBILI.COMUNE.eq(premise.getComune()))
                .and(IMMOBILI.PROVINCIA.eq(premise.getProvincia()))
                .and(CONTRATTI_APPROVATI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .and(CONTRATTI_APPROVATI.IDCONTRATTO.eq(BOLLETTE.IDCONTRATTO))
                .and(BOLLETTE.DATAEMISSIONE.greaterOrEqual(start))
                .and(BOLLETTE.DATAEMISSIONE.lessOrEqual(end))
                .fetchOneInto(BigDecimal.class);
    }

    public static BigDecimal avgConsumptionFromSub(final int subId, final LocalDate start, final LocalDate end,
                                                   final Connection conn) {
        final DSLContext query = createContext(conn);
        final var B1 = BOLLETTE;
        final var C1 = CONTRATTI_APPROVATI;

        return query
                .select(avg(B1.CONSUMI))
                .from(B1, C1)
                .where(C1.IDCONTRATTO.eq(subId))
                .and(B1.IDCONTRATTO.eq(C1.IDCONTRATTO))
                .and(B1.DATAEMISSIONE.greaterOrEqual(start))
                .and(B1.DATAEMISSIONE.lessOrEqual(end))
                .fetchOneInto(BigDecimal.class);
    }

    public static Map<ContrattiApprovati, Bollette> fetchAllSubscriptionsWithLastReport(final Connection conn) {
        final DSLContext query = createContext(conn);

        final var lastSubTable = query
                .select(CONTRATTI_APPROVATI.IDCONTRATTO, max(BOLLETTE.DATAEMISSIONE)
                        .as(BOLLETTE.DATAEMISSIONE))
                .from(CONTRATTI_APPROVATI, BOLLETTE)
                .where(CONTRATTI_APPROVATI.IDCONTRATTO.eq(BOLLETTE.IDCONTRATTO))
                .groupBy(CONTRATTI_APPROVATI.IDCONTRATTO)
                .asTable();

        return query
                .select(CONTRATTI_APPROVATI.asterisk(), BOLLETTE.asterisk())
                .from(CONTRATTI_APPROVATI.leftJoin(lastSubTable)
                        .on(CONTRATTI_APPROVATI.IDCONTRATTO.eq(lastSubTable.field(CONTRATTI_APPROVATI.IDCONTRATTO)))
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
                                r.get(CONTRATTI_APPROVATI.IDCLIENTE)),
                        r -> new Bollette(
                                r.get(BOLLETTE.NUMEROBOLLETTA),
                                r.get(BOLLETTE.DATAEMISSIONE),
                                r.get(BOLLETTE.DATAINIZIOPERIODO),
                                r.get(BOLLETTE.DATAFINEPERIODO),
                                r.get(BOLLETTE.DATASCADENZA),
                                r.get(BOLLETTE.IMPORTO),
                                r.get(BOLLETTE.CONSUMI),
                                r.get(BOLLETTE.DOCUMENTODETTAGLIATO),
                                r.get(BOLLETTE.IDOPERATORE),
                                r.get(BOLLETTE.IDCONTRATTO)))
                );
    }

    public static int markReportAsPaid(final int reportId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .insertInto(PAGAMENTI)
                .values(reportId, LocalDate.now())
                .onDuplicateKeyIgnore()
                .execute();
    }

    public static List<Letture> fetchMeasurements(final int subId, final Connection conn) {
        final DSLContext query = createContext(conn);

        final Optional<Cessazioni> approvedEnd = Queries.fetchApprovedEndRequestBySubscription(subId, conn);

        return query
                .select(LETTURE.asterisk())
                .from(CONTRATTI_APPROVATI, OFFERTE, IMMOBILI, CONTATORI, LETTURE)
                .where(CONTRATTI_APPROVATI.IDCONTRATTO.eq(subId))
                .and(CONTRATTI_APPROVATI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTATORI.IDIMMOBILE))
                .and(OFFERTE.CODOFFERTA.eq(CONTRATTI_APPROVATI.OFFERTA))
                .and(CONTATORI.MATERIAPRIMA.eq(OFFERTE.MATERIAPRIMA))
                .and(LETTURE.MATRICOLACONTATORE.eq(CONTATORI.MATRICOLA))
                .and(LETTURE.DATAEFFETTUAZIONE.ge(CONTRATTI_APPROVATI.DATACHIUSURARICHIESTA))
                .and(LETTURE.DATAEFFETTUAZIONE.le(approvedEnd.isPresent() ?
                                                  approvedEnd.orElseThrow().getDatachiusurarichiesta() :
                                                  LocalDate.now()))
                .fetchInto(Letture.class);
    }

    public static int approveMeasurement(final int measurementId, final String notes, final int opId,
            final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(LETTURE)
                .set(LETTURE.STATO, StatusType.APPROVED.toString())
                .set(LETTURE.DATACHIUSURARICHIESTA, LocalDate.now())
                .set(LETTURE.NOTE, notes)
                .where(LETTURE.NUMEROLETTURA.eq(measurementId))
                .and(LETTURE.STATO.eq(StatusType.REVIEWING.toString()))
                .and(LETTURE.DATACHIUSURARICHIESTA.isNull())
                .andExists(select()
                        .from(OPERATORI_LETTURE)
                        .where(OPERATORI_LETTURE.LETTURA.eq(measurementId))
                        .and(OPERATORI_LETTURE.IDOPERATORE.eq(opId)))
                .execute();
    }

    public static int rejectMeasurement(final int measurementId, final String notes, final int opId,
            final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .update(LETTURE)
                .set(LETTURE.STATO, StatusType.REJECTED.toString())
                .set(LETTURE.DATACHIUSURARICHIESTA, LocalDate.now())
                .set(LETTURE.NOTE, notes)
                .where(LETTURE.NUMEROLETTURA.eq(measurementId))
                .and(LETTURE.STATO.eq(StatusType.REVIEWING.toString()))
                .and(LETTURE.DATACHIUSURARICHIESTA.isNull())
                .andExists(select()
                        .from(OPERATORI_LETTURE)
                        .where(OPERATORI_LETTURE.LETTURA.eq(measurementId))
                        .and(OPERATORI_LETTURE.IDOPERATORE.eq(opId)))
                .execute();
    }

    public static int deleteReport(final int reportId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query
                .delete(BOLLETTE)
                .where(BOLLETTE.NUMEROBOLLETTA.eq(reportId))
                .andNotExists(select()
                        .from(PAGAMENTI)
                        .where(PAGAMENTI.NUMEROBOLLETTA.eq(reportId)))
                .execute();
    }

    public static int publishReport(final LocalDate intervalStartDate, final LocalDate intervalEndDate,
                                    final LocalDate deadline, final BigDecimal finalCost, final BigDecimal consumption,
                                    final byte[] reportFile, final int operatorId, final int subId, final Connection conn) {
        Objects.requireNonNull(finalCost);
        Objects.requireNonNull(reportFile);
        final DSLContext query = createContext(conn);

        return query
                .insertInto(BOLLETTE)
                .columns(BOLLETTE.DATAEMISSIONE, BOLLETTE.DATAINIZIOPERIODO, BOLLETTE.DATAFINEPERIODO, BOLLETTE.DATASCADENZA,
                        BOLLETTE.IMPORTO, BOLLETTE.CONSUMI, BOLLETTE.DOCUMENTODETTAGLIATO, BOLLETTE.IDOPERATORE,
                        BOLLETTE.IDCONTRATTO)
                .select(select(val(LocalDate.now()),
                            val(intervalStartDate),
                            val(intervalEndDate),
                            val(deadline),
                            val(finalCost),
                            val(consumption),
                            val(reportFile),
                            val(operatorId),
                            val(subId))
                        .from(CONTRATTI_ATTIVI)
                        .where(CONTRATTI_ATTIVI.IDCONTRATTO.eq(subId)))
                .execute();
    }

    public static Optional<Offerte> fetchPlanById(final int planId, final DataSource dataSource) {
        Optional<Offerte> plan = Optional.empty();
        try (Connection conn = dataSource.getConnection()) {
            final DSLContext query = createContext(conn);
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
        DSLContext query = createContext(conn);
        return query.delete(CONTRATTI)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(CONTRATTI.DATACHIUSURARICHIESTA.isNull())
                .execute();
    }

    public static int insertEndRequest(final int subId, final Connection conn) {
        DSLContext query = createContext(conn);
        return query.insertInto(CESSAZIONI, CESSAZIONI.DATAAPERTURARICHIESTA, CESSAZIONI.STATORICHIESTA,
                        CESSAZIONI.NOTERICHIESTA, CESSAZIONI.IDCONTRATTO)
                .select(select(
                        val(LocalDate.now()),
                        val(StatusType.REVIEWING.toString()),
                        val(""),
                        val(subId))
                        .whereNotExists(selectFrom(CESSAZIONI)
                                .where(CESSAZIONI.IDCONTRATTO.eq(subId))
                                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNull())
                                .or(CESSAZIONI.STATORICHIESTA.eq(StatusType.APPROVED.toString())
                                        .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNotNull()))))
                .execute();
    }

    public static Optional<Contatori> fetchMeterBySubscription(final int subId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query.select(CONTATORI.asterisk())
                .from(OFFERTE, CONTRATTI, IMMOBILI, CONTATORI)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(CONTRATTI.OFFERTA.eq(OFFERTE.CODOFFERTA))
                .and(CONTRATTI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTATORI.IDIMMOBILE))
                .and(CONTATORI.MATERIAPRIMA.eq(OFFERTE.MATERIAPRIMA))
                .fetchOptionalInto(Contatori.class);
    }

    public static Optional<Contatori> fetchMeterById(final String meterId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query.select()
                .from(CONTATORI)
                .where(CONTATORI.MATRICOLA.eq(meterId))
                .fetchOptionalInto(Contatori.class);
    }

    public static Optional<Contratti> fetchSubscriptionForChange(final String meterId, final int clientId,
                                                                 final Connection conn) {
        final DSLContext query = createContext(conn);

        return query.select(CONTRATTI_ATTIVI.asterisk())
                .from(CONTRATTI_ATTIVI, IMMOBILI, CONTATORI, OFFERTE)
                .where(CONTRATTI_ATTIVI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTATORI.IDIMMOBILE))
                .and(CONTATORI.MATRICOLA.eq(meterId))
                .and(CONTRATTI_ATTIVI.IDCLIENTE.eq(clientId))
                .and(OFFERTE.CODOFFERTA.eq(CONTRATTI_ATTIVI.OFFERTA))
                .and(OFFERTE.MATERIAPRIMA.eq(CONTATORI.MATERIAPRIMA))
                .fetchOptionalInto(Contratti.class);
    }

    public static Optional<Immobili> fetchPremiseById(final int premiseId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query.select()
                .from(IMMOBILI)
                .where(IMMOBILI.IDIMMOBILE.eq(premiseId))
                .fetchOptionalInto(Immobili.class);
    }

    public static boolean isOperator(final int personId, final Connection conn) {
        final DSLContext query = createContext(conn);

        return query.select()
                .from(OPERATORI)
                .where(OPERATORI.IDOPERATORE.eq(personId))
                .fetchOneInto(Operatori.class) != null;
    }

    public static Optional<TipologieUso> fetchUsageById(final int useCode, final Connection conn) {
        DSLContext query = createContext(conn);
        return query.select()
                .from(TIPOLOGIE_USO)
                .where(TIPOLOGIE_USO.CODUSO.eq(useCode))
                .fetchOptionalInto(TipologieUso.class);
    }

    public static List<Cessazioni> fetchEndRequestsFor(final int subId, final Connection conn) {
        DSLContext query = createContext(conn);
        return query.select()
                .from(CESSAZIONI)
                .where(CESSAZIONI.IDCONTRATTO.eq(subId))
                .fetchInto(Cessazioni.class);
    }

    public static int deleteEndRequest(final int requestNumber, final Connection conn) {
        DSLContext query = createContext(conn);
        return query.delete(CESSAZIONI)
                .where(CESSAZIONI.NUMERORICHIESTA.eq(requestNumber))
                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNull())
                .execute();
    }

    public static int insertPremise(final String type, final String street, final String civic,
                                    final String unit, final String municipality, final String postcode,
                                    final String province, final Connection conn) {
        DSLContext query = createContext(conn);
        return query.insertInto(IMMOBILI, IMMOBILI.TIPO, IMMOBILI.VIA, IMMOBILI.NUMCIVICO, IMMOBILI.INTERNO,
                        IMMOBILI.COMUNE, IMMOBILI.PROVINCIA, IMMOBILI.CAP)
                .values(type, street, civic, unit, municipality, province, postcode)
                .onDuplicateKeyIgnore()
                .execute();
    }

    public static int insertMeter(final String meterId, final String utility, final int premiseId, final Connection conn) {
        DSLContext query = createContext(conn);
        return query.insertInto(CONTATORI, CONTATORI.MATRICOLA, CONTATORI.MATERIAPRIMA, CONTATORI.IDIMMOBILE)
                .values(meterId, utility, premiseId)
                .onDuplicateKeyUpdate()
                .set(CONTATORI.MATRICOLA, meterId)
                .execute();
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

    public static Optional<Integer> fetchLastInsertId(final Connection conn) {
        final DSLContext ctx = createContext(conn);
        return ctx.select(field("last_insert_id()", SQLDataType.INTEGER))
                .fetchOptionalInto(int.class);
    }

    public static List<RichiesteContratto> fetchSubscriptionRequestsAssignedToOperator(final int operatorId,
                                                                                       final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.select(RICHIESTE_CONTRATTO.asterisk())
                .from(RICHIESTE_CONTRATTO, OPERATORI_CONTRATTI)
                .where(OPERATORI_CONTRATTI.IDOPERATORE.eq(operatorId))
                .and(RICHIESTE_CONTRATTO.IDCONTRATTO.eq(OPERATORI_CONTRATTI.NUMERORICHIESTA))
                .fetchInto(RichiesteContratto.class);
    }

    public static List<Cessazioni> fetchEndRequestsAssignedToOperator(final int operatorId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.select(CESSAZIONI.asterisk())
                .from(CESSAZIONI, OPERATORI_CESSAZIONI)
                .where(OPERATORI_CESSAZIONI.IDOPERATORE.eq(operatorId))
                .and(CESSAZIONI.NUMERORICHIESTA.eq(OPERATORI_CESSAZIONI.NUMERORICHIESTA))
                .fetchInto(Cessazioni.class);
    }

    public static int approveSubscriptionRequest(final int requestId, final int operatorId, final String notes,
            final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.update(CONTRATTI)
                .set(CONTRATTI.DATACHIUSURARICHIESTA, LocalDate.now())
                .set(CONTRATTI.STATORICHIESTA, StatusType.APPROVED.toString())
                .set(CONTRATTI.NOTERICHIESTA, notes)
                .where(CONTRATTI.IDCONTRATTO.eq(requestId))
                .and(CONTRATTI.STATORICHIESTA.eq(StatusType.REVIEWING.toString()))
                .and(CONTRATTI.DATACHIUSURARICHIESTA.isNull())
                .andExists(selectFrom(OPERATORI_CONTRATTI)
                        .where(OPERATORI_CONTRATTI.NUMERORICHIESTA.eq(requestId))
                        .and(OPERATORI_CONTRATTI.IDOPERATORE.eq(operatorId)))
                .andExists(select(CONTATORI.MATRICOLA)
                        .from(CONTRATTI, IMMOBILI, CONTATORI, OFFERTE)
                        .where(CONTRATTI.IDCONTRATTO.eq(requestId))
                        .and(IMMOBILI.IDIMMOBILE.eq(CONTRATTI.IDIMMOBILE))
                        .and(CONTATORI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                        .and(CONTRATTI.OFFERTA.eq(OFFERTE.CODOFFERTA))
                        .and(OFFERTE.MATERIAPRIMA.eq(CONTATORI.MATERIAPRIMA)))
                .andNotExists(select(CONTRATTI.IDCONTRATTO)
                        .from(CONTRATTI, IMMOBILI, OFFERTE, CONTATORI)
                        .where(CONTRATTI.IDCONTRATTO.eq(requestId))
                        .and(CONTRATTI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                        .and(CONTATORI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                        .and(OFFERTE.CODOFFERTA.eq(CONTRATTI.OFFERTA))
                        .and(OFFERTE.MATERIAPRIMA.eq(CONTATORI.MATERIAPRIMA))
                        .and(CONTRATTI.STATORICHIESTA.eq(StatusType.APPROVED.toString()))
                        .and(CONTRATTI.DATACHIUSURARICHIESTA.isNotNull())
                        .andNotExists(select(CESSAZIONI.NUMERORICHIESTA)
                                .from(CESSAZIONI)
                                .where(CESSAZIONI.IDCONTRATTO.eq(requestId))
                                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNotNull())
                                .and(CESSAZIONI.STATORICHIESTA.eq(StatusType.APPROVED.toString()))))
                .execute();
    }

    public static int updatePremise(final int premiseId, final String type, final String street, final String civic,
                                    final String unit, final String municipality, final String postcode,
                                    final String province, final Connection conn) {
        final DSLContext ctx = createContext(conn);
        return ctx.update(IMMOBILI)
                .set(IMMOBILI.TIPO, type)
                .set(IMMOBILI.VIA, street)
                .set(IMMOBILI.NUMCIVICO, civic)
                .set(IMMOBILI.INTERNO, unit)
                .set(IMMOBILI.COMUNE, municipality)
                .set(IMMOBILI.CAP, postcode)
                .set(IMMOBILI.PROVINCIA, province)
                .where(IMMOBILI.IDIMMOBILE.eq(premiseId))
                .execute();
    }

    public static Optional<Immobili> fetchPremiseByCandidateKey(final String type, final String street, final String civic,
                                                                final String unit, final String municipality,
                                                                final String postcode, final String province,
                                                                final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.select()
                .from(IMMOBILI)
                .where(IMMOBILI.TIPO.eq(type))
                .and(IMMOBILI.VIA.eq(street))
                .and(IMMOBILI.NUMCIVICO.eq(civic))
                .and(IMMOBILI.INTERNO.eq(unit))
                .and(IMMOBILI.COMUNE.eq(municipality))
                .and(IMMOBILI.CAP.eq(postcode))
                .and(IMMOBILI.PROVINCIA.eq(province))
                .fetchOptionalInto(Immobili.class);
    }

    public static Optional<Contatori> fetchMeterByPremiseIdAndUtility(final int premiseId, final String utility,
                                                                      final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.select(CONTATORI.asterisk())
                .from(IMMOBILI, CONTATORI)
                .where(IMMOBILI.IDIMMOBILE.eq(premiseId))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTATORI.IDIMMOBILE))
                .and(CONTATORI.MATERIAPRIMA.eq(utility))
                .fetchOptionalInto(Contatori.class);
    }

    public static int insertSubRequestAssignment(final int employeeId, final int requestId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.insertInto(OPERATORI_CONTRATTI, OPERATORI_CONTRATTI.IDOPERATORE, OPERATORI_CONTRATTI.NUMERORICHIESTA)
                .values(employeeId, requestId)
                .onDuplicateKeyIgnore()
                .execute();
    }

    public static int insertMeasurementAssignment(final int employeeId, final int measurementId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.insertInto(OPERATORI_LETTURE, OPERATORI_LETTURE.IDOPERATORE, OPERATORI_LETTURE.LETTURA)
                .values(employeeId, measurementId)
                .onDuplicateKeyIgnore()
                .execute();
    }

    public static int insertEndRequestAssignment(final int employeeId, final int requestId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.insertInto(OPERATORI_CESSAZIONI, OPERATORI_CESSAZIONI.IDOPERATORE, OPERATORI_CESSAZIONI.NUMERORICHIESTA)
                .values(employeeId, requestId)
                .onDuplicateKeyIgnore()
                .execute();
    }

    public static List<TipiAttivazione> fetchActivationMethods(final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.select()
                .from(TIPI_ATTIVAZIONE)
                .fetchInto(TipiAttivazione.class);
    }

    public static Optional<Cessazioni> fetchApprovedEndRequestBySubscription(final int subId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx.select()
                .from(CESSAZIONI)
                .where(CESSAZIONI.IDCONTRATTO.eq(subId))
                .and(CESSAZIONI.STATORICHIESTA.eq(StatusType.APPROVED.toString()))
                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNotNull())
                .fetchOptionalInto(Cessazioni.class);
    }

    /**
     *
     * Returns true if all three sub-queries succeed.
     */
    public static boolean forceSubscriptionEnd(final int subId, final String notes, final int opId, final Connection conn) {
        int result = Queries.insertEndRequest(subId, conn);
        if (result == 0) {
            throw new IllegalStateException("End request insertion failed.");
        }

        final int endRequestId = Queries.fetchLastInsertId(conn).orElseThrow();
        result += Queries.insertEndRequestAssignment(opId, endRequestId, conn);
        if (result == 0) {
            throw new IllegalStateException("Assignment insertion failed.");
        }

        result += Queries.markEndRequestAsApproved(endRequestId, notes, opId, conn);

        return result == 3;
    }

    public static int countSubRequestsClosedByOperator(final int operatorId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        /*
         * False positive
         */
        return ctx
                .selectCount()
                .from(OPERATORI_CONTRATTI, CONTRATTI)
                .where(OPERATORI_CONTRATTI.NUMERORICHIESTA.eq(CONTRATTI.IDCONTRATTO))
                .and(OPERATORI_CONTRATTI.IDOPERATORE.eq(operatorId))
                .and(CONTRATTI.DATACHIUSURARICHIESTA.isNotNull())
                .and(CONTRATTI.STATORICHIESTA.notEqual(StatusType.REVIEWING.toString()))
                .fetchOne(0, int.class);
    }

    public static int countEndRequestsClosedByOperator(final int operatorId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        /*
         * False positive
         */
        return ctx
                .selectCount()
                .from(OPERATORI_CESSAZIONI, CESSAZIONI)
                .where(OPERATORI_CESSAZIONI.NUMERORICHIESTA.eq(CESSAZIONI.NUMERORICHIESTA))
                .and(OPERATORI_CESSAZIONI.IDOPERATORE.eq(operatorId))
                .and(CESSAZIONI.DATACHIUSURARICHIESTA.isNotNull())
                .and(CESSAZIONI.STATORICHIESTA.notEqual(StatusType.REVIEWING.toString()))
                .fetchOne(0, int.class);
    }

    public static int countSubscriptionsActivatedInYear(final int year, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        /*
         * False positive
         */
        return ctx
                .selectCount()
                .from(CONTRATTI_APPROVATI)
                .where(year(CONTRATTI_APPROVATI.DATACHIUSURARICHIESTA).eq(year))
                .fetchOne(0, int.class);
    }

    /**
     *
     * Returns a list of records with 3 fields: the first one contains OFFERTE.CODOFFERTA,
     * the second one contains OFFERTE.NOME and the last one contains the number of
     * subscriptions for which the plan was chosen.
     */
    public static List<Record3<Integer, String, Integer>> fetchMostRequestedPlan(final String utility, final Connection conn) {
        final DSLContext ctx = createContext(conn);
        final CommonTableExpression<Record3<Integer, String, Integer>> t1 = name("t1").as(ctx
                .select(OFFERTE.CODOFFERTA.as("cod"), OFFERTE.NOME.as("nome"), count().as("NumeroContratti"))
                .from(OFFERTE, CONTRATTI)
                .where(CONTRATTI.OFFERTA.eq(OFFERTE.CODOFFERTA))
                .and(OFFERTE.MATERIAPRIMA.eq(utility))
                .groupBy(OFFERTE.CODOFFERTA));

        return ctx
                .select(field("cod").cast(int.class), field("nome").cast(String.class),
                        field("NumeroContratti").cast(int.class))
                .from(ctx.with(t1)
                        .select(t1.asterisk(), rank().over(orderBy(field("NumeroContratti").desc())).as("rk"))
                        .from(t1))
                .where(field("rk").eq(1))
                .fetchStream()
                .collect(Collectors.toList());
    }

    public static List<Offerte> fetchPlansByUtilityAndUse(final String utility, final int useId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx
                .select(OFFERTE.asterisk())
                .from(OFFERTE, COMPATIBILITÀ)
                .where(OFFERTE.MATERIAPRIMA.eq(utility))
                .and(OFFERTE.ATTIVA.eq((byte) 1))
                .and(COMPATIBILITÀ.USO.eq(useId))
                .and(OFFERTE.CODOFFERTA.eq(COMPATIBILITÀ.OFFERTA))
                .fetchInto(Offerte.class);
    }

    public static Map<LocalDate, BigDecimal> fetchReportsWithConsumptionInYear(final int subId, final int year, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        return ctx
                .select(BOLLETTE.DATAFINEPERIODO, BOLLETTE.CONSUMI)
                .from(BOLLETTE)
                .where(BOLLETTE.IDCONTRATTO.eq(subId))
                .and(year(BOLLETTE.DATAFINEPERIODO).eq(year))
                .fetchStream()
                .collect(Collectors.toMap(Record2::component1, Record2::component2));
    }

    public static Map<Integer, Integer> fetchMeasurementsBySubscriptionWithAssignment(final int subId, final Connection conn) {
        final DSLContext ctx = createContext(conn);

        final Optional<Cessazioni> approvedEnd = Queries.fetchApprovedEndRequestBySubscription(subId, conn);

        return ctx
                .select(LETTURE.NUMEROLETTURA, OPERATORI_LETTURE.IDOPERATORE)
                .from(CONTRATTI, OFFERTE, IMMOBILI, CONTATORI, LETTURE, OPERATORI_LETTURE)
                .where(CONTRATTI.IDCONTRATTO.eq(subId))
                .and(CONTRATTI.IDIMMOBILE.eq(IMMOBILI.IDIMMOBILE))
                .and(IMMOBILI.IDIMMOBILE.eq(CONTRATTI.IDIMMOBILE))
                .and(OFFERTE.CODOFFERTA.eq(CONTRATTI.OFFERTA))
                .and(CONTATORI.MATERIAPRIMA.eq(OFFERTE.MATERIAPRIMA))
                .and(LETTURE.MATRICOLACONTATORE.eq(CONTATORI.MATRICOLA))
                .and(LETTURE.DATAEFFETTUAZIONE.ge(CONTRATTI.DATACHIUSURARICHIESTA))
                .and(LETTURE.DATAEFFETTUAZIONE.le(approvedEnd.isPresent() ?
                                                  approvedEnd.orElseThrow().getDatachiusurarichiesta() :
                                                  LocalDate.now()))
                .and(LETTURE.NUMEROLETTURA.eq(OPERATORI_LETTURE.LETTURA))
                .fetchStream()
                .collect(Collectors.toMap(Record2::component1, Record2::component2));
    }
}
