-- *********************************************
-- * SQL MySQL generation                      
-- *--------------------------------------------
-- * DB-MAIN version: 11.0.1              
-- * Generator date: Dec  4 2018              
-- * Generation date: Fri Jun 18 19:00:03 2021 
-- * LUN file: C:\Users\Remisse\Documents\Uni\Basi di dati\BD2020-Progetto.lun 
-- * Schema: Relational/1 
-- ********************************************* 

-- Database Section
-- ________________ 

drop database if exists utenze;

create database if not exists utenze;

use utenze;

-- Tables Section
-- _____________ 

create table bollette (
     IdContratto integer not null,
     DataEmissione date not null,
     DataScadenza date not null,
     DataPagamento date default null,
     Importo decimal(20, 2) not null,
     Consumi decimal(20, 6) not null check (not(Consumi < 0.0)),
     Stimata boolean not null,
     ParzialeCanoneRai decimal(20, 2) not null check (ParzialeCanoneRai >= 0.0),
     CostoAttivazione decimal(20, 2) not null check (CostoAttivazione >= 0.0),
	 constraint PK_BOLLETTA primary key (IdContratto, DataEmissione));

create table compatibilità (
     CodiceOfferta integer not null,
     TipoUso varchar(30) not null,
     constraint PK_COMPATIBILITÀ primary key (CodiceOfferta, TipoUso));
     
create table contatori (
    NumeroProgressivo integer not null auto_increment,
    Matricola varchar(20) default null,
    MateriaPrima varchar(20) not null,
    IdImmobile integer not null,
    Potenza decimal(10, 4) not null,
    constraint ELECTR_POWER_REQUIRED check (MateriaPrima in ("Gas", "Acqua") and Potenza = 0.0
          or (MateriaPrima = "Luce" and Potenza >= 1.5 and Potenza <= 10.0)),
    constraint CODE_LENGTH check(Matricola is null
          or (MateriaPrima = "Luce" and (length(Matricola) in (14, 15)))
          or (MateriaPrima = "Gas" and length(Matricola) = 14)
          or (MateriaPrima = "Acqua" and length(Matricola) > 0)),
    constraint PK_CONTATORI primary key (NumeroProgressivo),
    constraint AK_CONTATORI unique (Matricola),
    constraint AK2_CONTATORI unique (IdImmobile, MateriaPrima));

create table contratti (
     IdContratto integer not null auto_increment,
     DataRichiesta date not null,
     DataInizio date,
     DataCessazione date,
     NumeroPersone integer not null default 1,
     Contatore integer not null,
     CodiceAzienda integer,
     CodiceCliente integer not null,
     CodiceOfferta integer not null,
     TipoUso varchar(30) not null,
     NomeAttivazione varchar(20) not null,
     constraint PK_CONTRATTO primary key (IdContratto));

create table distributori (
     Id integer not null auto_increment,
     Nome varchar(50) not null,
     NumeroContatto varchar(15) not null,
     EmailContatto varchar(30) not null,
     constraint PK_DISTRIBUTORE primary key (Id),
     constraint AK_DISTRIBUTORE unique (Nome));

create table distribuzioni (
     MateriaPrima varchar(20) not null,
     IdZona integer not null,
     IdDistributore integer not null,
     constraint PK_DISTRIBUZIONE primary key (MateriaPrima, IdZona, IdDistributore));

create table immobili (
     IdImmobile integer not null auto_increment,
     Tipo char(1) not null check(Tipo in ('F', 'T')),
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     Interno varchar(10),
     IdZona integer not null,
     CAP integer not null,
     check((Tipo = 'F') or (Tipo = 'T' and Interno is null)),
     constraint IDIMMOBILE primary key (IdImmobile),
     constraint IDIMMOBILE_2 unique (Via, NumCivico, Interno, IdZona));
     
create table interruzioni (
	 DataInterruzione date not null,
     DataRiattivazione date,
     IdContratto integer not null,
     check (DataRiattivazione >= DataInterruzione),
     constraint PK_INTERRUZIONI primary key (DataInterruzione, IdContratto)
);

create table letture (
    Fascia1 decimal(20, 6) not null check(Fascia1 >= 0),
    Fascia2 decimal(20, 6) not null check(Fascia2 >= 0),
    Fascia3 decimal(20, 6) not null check(Fascia3 >= 0),
    Contatore integer not null,
    DataEffettuazione date not null,
    Confermata boolean not null default false,
    constraint PK_LETTURE primary key (Contatore, DataEffettuazione));

create table materie_prime (
     Nome varchar(20) not null,
     constraint PK_MATERIA primary key (Nome));

create table offerte (
     Codice integer not null auto_increment,
     Nome varchar(20) not null,
     Descrizione varchar(1000) not null,
     CostoMateriaPrima decimal(10,4) not null check(CostoMateriaPrima > 0.0),
     Attiva boolean not null default true,
     MateriaPrima varchar(20) not null,
     constraint PK_OFFERTA primary key (Codice));

create table persone_fisiche (
     CodiceCliente integer not null auto_increment,
     Amministratore boolean not null,
     CodiceFiscale varchar(16) not null,
     PartitaIVA varchar(11) default null,
     Nome varchar(50) not null,
     Cognome varchar(50) not null,
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     CAP integer not null,
     Comune varchar(30) not null,
     Provincia varchar(2) not null,
     DataNascita date not null,
     NumeroTelefono varchar(10) not null,
     Email varchar(40) not null,
     Password varchar(30) not null check(length(Password) >= 8),
     FasciaReddito varchar(30),
     constraint AK_PERSONA_FISICA unique (Email),
     constraint PK_PERSONA_FISICA primary key (CodiceCliente));

create table persone_giuridiche (
     CodiceAzienda integer not null auto_increment,
     PartitaIVA varchar(11) not null,
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     CAP integer not null,
     Comune varchar(30) not null,
     Provincia varchar(2) not null,
     FormaGiuridica varchar(50) not null,
     RagioneSociale varchar(30) not null,
     constraint PK_PERSONA_GIURIDICA primary key (CodiceAzienda));
     
create table redditi (
	Fascia varchar(30) not null,
    Sconto decimal(7, 6) not null check (Sconto > 0.0 and Sconto <= 1.0),
    constraint PK_REDDITI primary key (Fascia));

create table tipi_attivazione (
     Nome varchar(20) not null,
     CostoUnaTantum decimal(20, 2) not null,
     check(CostoUnaTantum >= 0),
     constraint PK_TIPO_ATTIVAZIONE primary key (Nome));

create table tipologie_uso (
     Nome varchar(30) not null,
     CanoneRAI decimal(20, 2) not null,
     StimaPerPersona decimal(20, 2) not null,
     ScontoReddito boolean not null,
     check(StimaPerPersona >= 0.0),
     constraint PK_USO_DEDICATO primary key (Nome));

create table zone (
     IdZona integer not null auto_increment,
     Comune varchar(50) not null,
     Provincia varchar(2) not null,
     constraint PK_ZONA primary key (IdZona),
     constraint AK_ZONA unique (Comune, Provincia));

-- ----------
-- Add records
-- ----------

-- Populate materie_prime
insert into materie_prime
values("Luce");

insert into materie_prime
values("Gas");

insert into materie_prime
values("Acqua");


-- Populate zone
insert into zone(Comune, Provincia)
values("Bagno di Romagna", "FC"); -- 1

insert into zone(Comune, Provincia)
values("Bertinoro", "FC"); -- 2

insert into zone(Comune, Provincia)
values("Borghi", "FC"); -- 3

insert into zone(Comune, Provincia)
values("Castrocaro Terme e Terra del Sole", "FC"); -- 4

insert into zone(Comune, Provincia)
values("Cesena", "FC"); -- 5

insert into zone(Comune, Provincia)
values("Cesenatico", "FC"); -- 6

insert into zone(Comune, Provincia)
values("Civitella di Romagna", "FC"); -- 7

insert into zone(Comune, Provincia)
values("Dovadola", "FC"); -- 8

insert into zone(Comune, Provincia)
values("Forlì", "FC"); -- 9

insert into zone(Comune, Provincia)
values("Forlimpopoli", "FC"); -- 10

insert into zone(Comune, Provincia)
values("Galeata", "FC"); -- 11

insert into zone(Comune, Provincia)
values("Gambettola", "FC"); -- 12

insert into zone(Comune, Provincia)
values("Gatteo", "FC"); -- 13

insert into zone(Comune, Provincia)
values("Longiano", "FC"); -- 14

insert into zone(Comune, Provincia)
values("Meldola", "FC"); -- 15

insert into zone(Comune, Provincia)
values("Mercato Saraceno", "FC"); -- 16

insert into zone(Comune, Provincia)
values("Modigliana", "FC"); -- 17

insert into zone(Comune, Provincia)
values("Montiano", "FC"); -- 18

insert into zone(Comune, Provincia)
values("Portico e San Benedetto", "FC"); -- 19

insert into zone(Comune, Provincia)
values("Predappio", "FC"); -- 20

insert into zone(Comune, Provincia)
values("Premilcuore", "FC"); -- 21

insert into zone(Comune, Provincia)
values("Rocca San Casciano", "FC"); -- 22

insert into zone(Comune, Provincia)
values("Roncofreddo", "FC"); -- 23

insert into zone(Comune, Provincia)
values("San Mauro Pascoli", "FC"); -- 24

insert into zone(Comune, Provincia)
values("Santa Sofia", "FC"); -- 25

insert into zone(Comune, Provincia)
values("Sarsina", "FC"); -- 26

insert into zone(Comune, Provincia)
values("Savignano sul Rubicone", "FC"); -- 27

insert into zone(Comune, Provincia)
values("Sogliano al Rubicone", "FC"); -- 28

insert into zone(Comune, Provincia)
values("Tredozio", "FC"); -- 29

insert into zone(Comune, Provincia)
values("Verghereto", "FC"); -- 30


-- Populate distributori
insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Dianacomm S.p.A.", "1282341", "pepignoimpastagno@bonobbo.com"); -- 1

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Svevogas Reti S.p.A.", "6247372", "eeeeeooooo@bonobbo.com"); -- 2

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Z-Distribuzione S.p.A.", "7372836", "woooooooooo@bonobbo.com"); -- 3

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Medigas S.p.A.", "462674567", "eaaaaaaaaaa@bonobbo.com"); -- 4

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Fuorirete Distribuzione Energia S.p.A.", "7537353", "wuuuuuuuuuuuuu@bonobbo.com"); -- 5

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("3P Rete Gas S.p.A.", "827828", "oaoaoaoaoaoao@bonobbo.com"); -- 6

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Gitan Gas S.p.A.", "9199282", "pepepepepep@bonobbo.com"); -- 7

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Solidogas S.p.A.", "55343292", "ooooiiiooo@bonobbo.com"); -- 8

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("SAS Oli S.p.A.", "9322828", "yeyeyeyeyey@bonobbo.com"); -- 9


-- Populate distribuzioni
-- Gas
insert into distribuzioni
values("Gas", 3, 4);

insert into distribuzioni
values("Gas", 3, 5);

insert into distribuzioni
values("Gas", 5, 5);

insert into distribuzioni
values("Gas", 9, 5);

insert into distribuzioni
values("Gas", 13, 5);

insert into distribuzioni
values("Gas", 11, 5);

insert into distribuzioni
values("Gas", 1, 4);

insert into distribuzioni
values("Gas", 1, 5);

insert into distribuzioni
values("Gas", 2, 6);

insert into distribuzioni
values("Gas", 2, 5);

insert into distribuzioni
values("Gas", 4, 5);

insert into distribuzioni
values("Gas", 6, 6);

insert into distribuzioni
values("Gas", 6, 5);

insert into distribuzioni
values("Gas", 7, 5);

insert into distribuzioni
values("Gas", 8, 5);

insert into distribuzioni
values("Gas", 10, 5);

insert into distribuzioni
values("Gas", 12, 5);

insert into distribuzioni
values("Gas", 14, 5);

insert into distribuzioni
values("Gas", 15, 5);

insert into distribuzioni
values("Gas", 16, 4);

insert into distribuzioni
values("Gas", 17, 2);

insert into distribuzioni
values("Gas", 18, 5);

insert into distribuzioni
values("Gas", 19, 5);

insert into distribuzioni
values("Gas", 20, 5);

insert into distribuzioni
values("Gas", 21, 5);

insert into distribuzioni
values("Gas", 22, 5);

insert into distribuzioni
values("Gas", 23, 2);

insert into distribuzioni
values("Gas", 23, 8);

insert into distribuzioni
values("Gas", 23, 9);

insert into distribuzioni
values("Gas", 23, 5);

insert into distribuzioni
values("Gas", 24, 5);

insert into distribuzioni
values("Gas", 25, 5);

insert into distribuzioni
values("Gas", 26, 4);

insert into distribuzioni
values("Gas", 27, 5);

insert into distribuzioni
values("Gas", 28, 4);

insert into distribuzioni
values("Gas", 29, 2);

insert into distribuzioni
values("Gas", 30, 4);

insert into distribuzioni
values("Gas", 30, 7);

-- Luce
insert into distribuzioni
values("Luce", 1, 3);

insert into distribuzioni
values("Luce", 2, 3);

insert into distribuzioni
values("Luce", 3, 3);

insert into distribuzioni
values("Luce", 4, 3);

insert into distribuzioni
values("Luce", 5, 3);

insert into distribuzioni
values("Luce", 6, 3);

insert into distribuzioni
values("Luce", 7, 3);

insert into distribuzioni
values("Luce", 8, 3);

insert into distribuzioni
values("Luce", 9, 3);

insert into distribuzioni
values("Luce", 10, 3);

insert into distribuzioni
values("Luce", 11, 3);

insert into distribuzioni
values("Luce", 12, 3);

insert into distribuzioni
values("Luce", 13, 3);

insert into distribuzioni
values("Luce", 14, 3);

insert into distribuzioni
values("Luce", 15, 3);

insert into distribuzioni
values("Luce", 16, 3);

insert into distribuzioni
values("Luce", 17, 3);

insert into distribuzioni
values("Luce", 18, 3);

insert into distribuzioni
values("Luce", 19, 3);

insert into distribuzioni
values("Luce", 20, 3);

insert into distribuzioni
values("Luce", 21, 3);

insert into distribuzioni
values("Luce", 22, 3);

insert into distribuzioni
values("Luce", 23, 3);

insert into distribuzioni
values("Luce", 24, 3);

insert into distribuzioni
values("Luce", 25, 3);

insert into distribuzioni
values("Luce", 26, 3);

insert into distribuzioni
values("Luce", 27, 3);

insert into distribuzioni
values("Luce", 28, 3);

insert into distribuzioni
values("Luce", 29, 3);

insert into distribuzioni
values("Luce", 30, 3);

-- Acqua
insert into distribuzioni
values("Acqua", 1, 1);

insert into distribuzioni
values("Acqua", 2, 1);

insert into distribuzioni
values("Acqua", 3, 1);

insert into distribuzioni
values("Acqua", 4, 1);

insert into distribuzioni
values("Acqua", 5, 1);

insert into distribuzioni
values("Acqua", 6, 1);

insert into distribuzioni
values("Acqua", 7, 1);

insert into distribuzioni
values("Acqua", 8, 1);

insert into distribuzioni
values("Acqua", 9, 1);

insert into distribuzioni
values("Acqua", 10, 1);

insert into distribuzioni
values("Acqua", 11, 1);

insert into distribuzioni
values("Acqua", 12, 1);

insert into distribuzioni
values("Acqua", 13, 1);

insert into distribuzioni
values("Acqua", 14, 1);

insert into distribuzioni
values("Acqua", 15, 1);

insert into distribuzioni
values("Acqua", 16, 1);

insert into distribuzioni
values("Acqua", 17, 1);

insert into distribuzioni
values("Acqua", 18, 1);

insert into distribuzioni
values("Acqua", 19, 1);

insert into distribuzioni
values("Acqua", 20, 1);

insert into distribuzioni
values("Acqua", 21, 1);

insert into distribuzioni
values("Acqua", 22, 1);

insert into distribuzioni
values("Acqua", 23, 1);

insert into distribuzioni
values("Acqua", 24, 1);

insert into distribuzioni
values("Acqua", 25, 1);

insert into distribuzioni
values("Acqua", 26, 1);

insert into distribuzioni
values("Acqua", 27, 1);

insert into distribuzioni
values("Acqua", 28, 1);

insert into distribuzioni
values("Acqua", 29, 1);

insert into distribuzioni
values("Acqua", 30, 1);


-- Populate "tipologie_uso"

insert into tipologie_uso
values("Abitativo residenziale", 90.0, 0.2, true);

insert into tipologie_uso
values("Abitativo non residenziale", 0.0, 0.7, false);

insert into tipologie_uso
values("Commerciale", 0.0, 0.1, false);

insert into tipologie_uso
values("Condominiale", 0.0, 0.5, false);


-- Populate "tipi_attivazione"
insert into tipi_attivazione
values ("Nuova attivazione", 85.0);

insert into tipi_attivazione
values ("Subentro", 70.0);

insert into tipi_attivazione
values ("Voltura", 45.0);

insert into tipi_attivazione
values ("Cambio offerta", 0.0);


-- Populate "redditi"
insert into redditi
values ("0 - 5.000", 0.5);

insert into redditi
values ("5.001 - 10.000", 0.7);

insert into redditi
values ("10.001 - 15.000", 0.95);

insert into redditi
values ("> 15.000", 1.0);


-- Populate "immobili"
insert into immobili (Tipo, Via, NumCivico, Interno, CAP, IdZona)
values ('F', "Via Bongo", 69, 1, 47121, 9);


-- Populate "contatori"
insert into contatori (MateriaPrima, Matricola, IdImmobile, Potenza)
values ("Luce", "38501111111111", 1, 3.0);

insert into contatori (MateriaPrima, Matricola, IdImmobile, Potenza)
values ("Gas", "83850395028543", 1, 0.0);


-- Populate "letture"
insert into letture
values (18.0, 11.0, 2.6, 1, date_sub(curdate(), interval 2 month), true);

insert into letture
values (35.0, 19.0, 5.2, 1, curdate(), true);


-- Populate "persone_fisiche"
insert into persone_fisiche
values (default, false, "MRMMRA55R08B963X", default, "Mario", "Maria Mario", "Via Mario", 64, 47121, "Forlì", "FC", 19551005, "35426324", "longlivecrts@bonobbo.com", "aeo1001012", "10.001 - 15.000");

insert into persone_fisiche
values (default, false, "BRTBBB25T87R762U", default, "Bartolomeo", "Bababbo", "Via delle Vie", 12, 47521, "Cesena", "FC", 19860621, "38275722", "bartolomeo@gmail.com", "uffiuffi", "0 - 5.000");

insert into persone_fisiche
values (default, true, "GAGGUG92F28U275P", default, "Armando", "Armandini", "Viale Vialone", 73, 88100, "Catanzaro", "CZ", 19951030, "292892992", "amministratore@admin.com", "password", "> 15.000");

-- Populate "offerte" and "compatibilità"
insert into offerte(Nome, Descrizione, CostoMateriaPrima, MateriaPrima)
values ("A tutto gas", "Una generica offerta per la fornitura di gas.", 0.3, "Gas");

insert into compatibilità
values (last_insert_id(), "Abitativo residenziale");

insert into compatibilità
values (last_insert_id(), "Abitativo non residenziale");

insert into offerte(Nome, Descrizione, CostoMateriaPrima, MateriaPrima)
values ("Luce divina", "Una generica offerta per la fornitura di luce.", 0.18, "Luce");

insert into compatibilità
values (last_insert_id(), "Abitativo residenziale");

insert into compatibilità
values (last_insert_id(), "Abitativo non residenziale");


-- Populate "contratti"
insert into contratti(DataRichiesta, DataInizio, Contatore, CodiceCliente, CodiceOfferta, TipoUso, NomeAttivazione, NumeroPersone)
values (date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 4 month), 1, 1, 2, "Abitativo residenziale", "Nuova attivazione", 4);

insert into contratti(DataRichiesta, DataInizio, Contatore, CodiceCliente, CodiceOfferta, TipoUso, NomeAttivazione, NumeroPersone)
values (date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 4 month), 2, 1, 1, "Abitativo residenziale", "Nuova attivazione", default);


-- Populate "bollette"
insert into bollette
values (1, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 3 month), date_sub(curdate(), interval 3 month), 124.64, 31.6, false, 90 * 60.0/360.0, 85.0);

insert into bollette
values (2, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 3 month), date_sub(curdate(), interval 3 month), 133.0, 72.0, true, 0.0, 85.0);

insert into bollette
values (1, date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), 19.97, 27.6, false, 90 * 60.0/360.0, 0.0);

insert into bollette
values (2, date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), 48.0, 72.0, true, 0.0, 0.0);

-- Foreign keys
-- ___________________ 

alter table bollette add constraint FK_CONTRATTO
     foreign key (IdContratto) references contratti (IdContratto);

alter table compatibilità add constraint FK_USOOFFERTA
     foreign key (TipoUso) references tipologie_uso (Nome) on update cascade;

alter table compatibilità add constraint FK_OFFERTAUSO
     foreign key (CodiceOfferta) references offerte (Codice);
     
alter table contatori add constraint FK_MISURAZIONE
	foreign key (MateriaPrima) references materie_prime (Nome);

alter table contatori add constraint FK_INSTALLAZIONE
     foreign key (IdImmobile) references immobili (IdImmobile);

alter table contratti add constraint FK_SOTTOSCRIZIONE
     foreign key (CodiceCliente) references persone_fisiche (CodiceCliente);

alter table contratti add constraint FK_REFERENZA
     foreign key (CodiceAzienda) references persone_giuridiche (CodiceAzienda);

alter table contratti add constraint FK_PROPOSTA
     foreign key (CodiceOfferta) references offerte (Codice);

alter table contratti add constraint FK_USO
     foreign key (TipoUso) references tipologie_uso (Nome) on update cascade;

alter table contratti add constraint FK_ATTIVAZIONE_TRAMITE
     foreign key (NomeAttivazione) references tipi_attivazione (Nome) on update cascade;

alter table contratti add constraint FK_COLLEGAMENTO
     foreign key (Contatore) references contatori (NumeroProgressivo);

alter table distribuzioni add constraint FK_RIFERIMENTO
     foreign key (IdZona) references zone (IdZona) on delete cascade;

alter table distribuzioni add constraint FK_DISTRIBUITA
     foreign key (MateriaPrima) references materie_prime (Nome);

alter table distribuzioni add constraint FK_OPERAZIONE
     foreign key (IdDistributore) references distributori (Id) on delete cascade;

alter table immobili add constraint FK_UBICAZIONE
     foreign key (IdZona) references zone (IdZona);
     
alter table interruzioni add constraint FK_ATTINENZA
	 foreign key (IdContratto) references contratti (IdContratto);

alter table letture add constraint FK_EFFETTUAZIONE
     foreign key (Contatore) references contatori (NumeroProgressivo);

alter table offerte add constraint FK_INTERESSE
     foreign key (MateriaPrima) references materie_prime (Nome);
     
alter table persone_fisiche add constraint FK_POSSEDIMENTO
	foreign key (FasciaReddito) references redditi (Fascia) on update cascade;
    

-- View section

-- Index Section
-- _____________ 

