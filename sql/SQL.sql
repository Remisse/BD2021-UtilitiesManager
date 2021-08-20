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
     check((Tipo = 'F') or (Tipo = 'T' and Interno is null)),
     constraint IDIMMOBILE primary key (IdImmobile),
     constraint IDIMMOBILE_2 unique (Via, NumCivico, Interno, IdZona));
     
create table interruzioni (
     IdContratto integer not null,
	 DataInterruzione date not null,
     DataRiattivazione date default null,
     Descrizione varchar(1000) not null,
     check (DataRiattivazione is null or DataRiattivazione >= DataInterruzione),
     constraint PK_INTERRUZIONI primary key (DataInterruzione, IdContratto)
);

create table letture (
    Consumi decimal(20, 6) not null check(Consumi >= 0),
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
     CostoMateriaPrima decimal(10, 4) not null check(CostoMateriaPrima > 0.0),
     Attiva boolean not null default true,
     MateriaPrima varchar(20) not null,
     constraint PK_OFFERTA primary key (Codice));

create table persone (
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
     constraint AK_PERSONA unique (Email),
     constraint PK_PERSONA primary key (CodiceCliente));
     
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
     StimaPerPersona decimal(20, 2) not null,
     ScontoReddito boolean not null,
     check(StimaPerPersona >= 0.0),
     constraint PK_USO_DEDICATO primary key (Nome));

create table zone (
     IdZona integer not null auto_increment,
     Comune varchar(50) not null,
     Provincia varchar(2) not null,
     CAP varchar(5) not null check (length(CAP) = 5),
     constraint PK_ZONA primary key (IdZona),
     constraint AK_ZONA unique (Comune, Provincia));

-- ----------
-- Add records
-- ----------

-- Populate materie_prime
insert into materie_prime
values("Gas");

insert into materie_prime
values("Acqua");


-- Populate zone
insert into zone(Comune, CAP, Provincia)
values("Bagno di Romagna", "47021", "FC"); -- 1

insert into zone(Comune, CAP, Provincia)
values("Bertinoro", "47032", "FC"); -- 2

insert into zone(Comune, CAP, Provincia)
values("Borghi", "47030", "FC"); -- 3

insert into zone(Comune, CAP, Provincia)
values("Castrocaro Terme e Terra del Sole", "47011", "FC"); -- 4

insert into zone(Comune, CAP, Provincia)
values("Cesena", "47521", "FC"); -- 5

insert into zone(Comune, CAP, Provincia)
values("Cesenatico", "47042", "FC"); -- 6

insert into zone(Comune, CAP, Provincia)
values("Civitella di Romagna", "47012", "FC"); -- 7

insert into zone(Comune, CAP, Provincia)
values("Dovadola", "47013", "FC"); -- 8

insert into zone(Comune, CAP, Provincia)
values("Forlì", "47121", "FC"); -- 9

insert into zone(Comune, CAP, Provincia)
values("Forlimpopoli", "47034", "FC"); -- 10

insert into zone(Comune, CAP, Provincia)
values("Galeata", "47010", "FC"); -- 11

insert into zone(Comune, CAP, Provincia)
values("Gambettola", "47035", "FC"); -- 12

insert into zone(Comune, CAP, Provincia)
values("Gatteo", "47043", "FC"); -- 13

insert into zone(Comune, CAP, Provincia)
values("Longiano", "47020", "FC"); -- 14

insert into zone(Comune, CAP, Provincia)
values("Meldola", "47014", "FC"); -- 15

insert into zone(Comune, CAP, Provincia)
values("Mercato Saraceno", "47025", "FC"); -- 16

insert into zone(Comune, CAP, Provincia)
values("Modigliana", "47015", "FC"); -- 17

insert into zone(Comune, CAP, Provincia)
values("Montiano", "47020", "FC"); -- 18

insert into zone(Comune, CAP, Provincia)
values("Portico e San Benedetto", "47010", "FC"); -- 19

insert into zone(Comune, CAP, Provincia)
values("Predappio", "47016", "FC"); -- 20

insert into zone(Comune, CAP, Provincia)
values("Premilcuore", "47010", "FC"); -- 21

insert into zone(Comune, CAP, Provincia)
values("Rocca San Casciano", "47017", "FC"); -- 22

insert into zone(Comune, CAP, Provincia)
values("Roncofreddo", "47020", "FC"); -- 23

insert into zone(Comune, CAP, Provincia)
values("San Mauro Pascoli", "47030", "FC"); -- 24

insert into zone(Comune, CAP, Provincia)
values("Santa Sofia", "47018", "FC"); -- 25

insert into zone(Comune, CAP, Provincia)
values("Sarsina", "47027", "FC"); -- 26

insert into zone(Comune, CAP, Provincia)
values("Savignano sul Rubicone", "47039", "FC"); -- 27

insert into zone(Comune, CAP, Provincia)
values("Sogliano al Rubicone", "47030", "FC"); -- 28

insert into zone(Comune, CAP, Provincia)
values("Tredozio", "47019", "FC"); -- 29

insert into zone(Comune, CAP, Provincia)
values("Verghereto", "47028", "FC"); -- 30


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
values("Abitativo residenziale", 0.2, true);

insert into tipologie_uso
values("Abitativo non residenziale", 0.7, false);


-- Populate "tipi_attivazione"
insert into tipi_attivazione
values ("Nuova attivazione", 85.0);

insert into tipi_attivazione
values ("Subentro", 70.0);

insert into tipi_attivazione
values ("Voltura", 45.0);


-- Populate "redditi"
insert into redditi
values ("0 - 5000", 0.5);

insert into redditi
values ("5001 - 10000", 0.7);

insert into redditi
values ("10001 - 15000", 0.95);

insert into redditi
values ("> 15000", 1.0);


-- Populate "immobili"
insert into immobili (Tipo, Via, NumCivico, Interno, IdZona)
values ('F', "Via Bongo", 69, 1, 9);

insert into immobili (Tipo, Via, NumCivico, Interno, IdZona)
values ('F', "Via Roma", 11, null, 5);


-- Populate "contatori"
insert into contatori (MateriaPrima, Matricola, IdImmobile)
values ("Acqua", "385011111111", 1);

insert into contatori (MateriaPrima, Matricola, IdImmobile)
values ("Gas", "83850395028543", 1);

insert into contatori (MateriaPrima, Matricola, IdImmobile)
values ("Gas", "19287488822211", 2);


-- Populate "letture"
insert into letture
values (18.0, 1, date_sub(curdate(), interval 2 month), true);

insert into letture
values (35.0, 1, curdate(), true);


-- Populate "persone"
insert into persone
values (default, false, "MRMMRA55R08B963X", default, "Mario", "Maria Mario", "Via Mario", 64, 47121, "Forlì", "FC", 19551005, "35426324", "trallallero@boh.it", "ucciucci", "10001 - 15000");

insert into persone
values (default, false, "BRTBBB25T87R762U", default, "Bartolomeo", "Bartolucci", "Via delle Vie", 12, 47521, "Cesena", "FC", 19860621, "38275722", "bartolomeo@gmail.com", "uffiuffi", "0 - 5000");

insert into persone
values (default, true, "GAGGUG92F28U275P", default, "Armando", "Armandini", "Viale Vialone", 73, 88100, "Catanzaro", "CZ", 19951030, "292892992", "amministratore@admin.com", "password", "> 15000");


-- Populate "offerte" and "compatibilità"
insert into offerte(Nome, Descrizione, CostoMateriaPrima, MateriaPrima)
values ("A tutto gas", "Una generica offerta per la fornitura di gas.", 0.3, "Gas");

insert into compatibilità
values (last_insert_id(), "Abitativo residenziale");

insert into compatibilità
values (last_insert_id(), "Abitativo non residenziale");

insert into offerte(Nome, Descrizione, CostoMateriaPrima, MateriaPrima)
values ("Acqua santa", "Una generica offerta per la fornitura di acqua.", 0.18, "Acqua");

insert into compatibilità
values (last_insert_id(), "Abitativo residenziale");

insert into compatibilità
values (last_insert_id(), "Abitativo non residenziale");


-- Populate "contratti"
insert into contratti(DataRichiesta, DataInizio, Contatore, CodiceCliente, CodiceOfferta, TipoUso, NomeAttivazione, NumeroPersone)
values (date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 4 month), 1, 1, 2, "Abitativo residenziale", "Nuova attivazione", 4);

insert into contratti(DataRichiesta, DataInizio, Contatore, CodiceCliente, CodiceOfferta, TipoUso, NomeAttivazione, NumeroPersone)
values (date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 4 month), 2, 1, 1, "Abitativo residenziale", "Nuova attivazione", default);

insert into contratti(DataRichiesta, DataInizio, Contatore, CodiceCliente, CodiceOfferta, TipoUso, NomeAttivazione, NumeroPersone)
values (date_sub(curdate(), interval 2 day), null, 3, 2, 1, "Abitativo residenziale", "Subentro", 2);


-- Populate "bollette"
insert into bollette
values (1, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 3 month), date_sub(curdate(), interval 3 month), 124.64, 31.6, false, 85.0);

insert into bollette
values (2, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 3 month), date_sub(curdate(), interval 3 month), 133.0, 72.0, true, 85.0);

insert into bollette
values (1, date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), 19.97, 27.6, false, 0.0);

insert into bollette
values (2, date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), 48.0, 72.0, true, 0.0);

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
     foreign key (CodiceCliente) references persone (CodiceCliente);

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
     
alter table persone add constraint FK_POSSEDIMENTO
	foreign key (FasciaReddito) references redditi (Fascia) on update cascade;
    

-- View section

-- Index Section
-- _____________ 

