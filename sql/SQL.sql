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
     DettaglioBolletta mediumblob not null,
     Stimata boolean not null,
     check (DataScadenza > DataEmissione),
     check (DataPagamento is null or DataPagamento >= DataEmissione),
	 constraint PK_BOLLETTA primary key (IdContratto, DataEmissione));
     
create table clienti (
	 CodiceCliente integer not null,
     FasciaReddito integer not null,
     constraint PK_CLIENTI primary key (CodiceCliente));

create table compatibilità (
     CodiceOfferta integer not null,
     Uso integer not null,
     constraint PK_COMPATIBILITÀ primary key (CodiceOfferta, Uso));
     
create table contatori (
	 Progressivo integer not null auto_increment,
     Matricola varchar(20),
     MateriaPrima varchar(20) not null,
     IdImmobile integer not null,
     constraint PK_CONTATORI primary key (Progressivo),
     constraint AK1_CONTATORI unique (Matricola),
     constraint AK2_CONTATORI unique (IdImmobile, MateriaPrima));

create table contratti (
     IdContratto integer not null,
     DataInizio date not null,
     DataCessazione date default null,
     DataUltimaBolletta date default null,
     constraint PK_CONTRATTO primary key (IdContratto));

create table immobili (
     IdImmobile integer not null auto_increment,
     Tipo integer not null,
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     Interno varchar(10),
	 Comune varchar(50) not null,
     Provincia varchar(2) not null,
     CAP varchar(5) not null check (length(CAP) = 5),
     constraint IDIMMOBILE primary key (IdImmobile),
     constraint IDIMMOBILE_2 unique (Via, NumCivico, Interno, Comune, Provincia));
     
create table interruzioni (
     IdContratto integer not null,
	 DataInterruzione date not null,
     DataRiattivazione date default null,
     Motivazione varchar(1000) not null,
     check (DataRiattivazione is null or DataRiattivazione >= DataInterruzione),
     constraint PK_INTERRUZIONI primary key (DataInterruzione, IdContratto)
);

create table letture (
    Consumi decimal(20, 6) not null check(Consumi >= 0),
    Contatore integer not null,
    DataEffettuazione date not null,
    Confermata boolean not null default false,
    Cliente integer not null,
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

create table operatori (
	 CodiceOperatore integer not null,
     constraint PK_OPERATORE primary key (CodiceOperatore));

create table persone (
     Identificativo integer not null auto_increment,
     CodiceFiscale varchar(16) not null,
     Nome varchar(50) not null,
     Cognome varchar(50) not null,
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     CAP varchar(5) not null,
     Comune varchar(30) not null,
     Provincia varchar(2) not null,
     DataNascita date not null,
     NumeroTelefono varchar(10) not null,
     Email varchar(40) not null,
     Password varchar(30) not null check(length(Password) >= 8),
     constraint AK_PERSONA unique (Email),
     constraint PK_PERSONA primary key (Identificativo));
     
create table richieste_attivazione (
     Numero integer not null auto_increment,
     DataRichiesta date not null,
     NumeroComponenti integer not null,
     Stato char not null default "N" check (Stato in ("N", "E", "A", "R")),
     Note varchar(200) not null default "",
     Contatore integer not null,
     Cliente integer not null,
     Operatore integer default null,
     Offerta integer not null,
     Uso integer not null,
     Attivazione integer not null,
     constraint PK_RIC_ATTIVAZIONE primary key (Numero));
     
create table richieste_cessazione (
     Numero integer not null auto_increment,
     DataRichiesta date not null,
     Stato char not null default "N" check (Stato in ("N", "E", "A", "R")),
     Note varchar(200) not null default "",
     IdContratto integer not null,
     Operatore integer default null,
     constraint PK_RIC_CESSAZIONE primary key (Numero));
     
create table redditi (
	 Codice integer not null,
	 Fascia varchar(30) not null,
     Sconto decimal(7, 6) not null check (Sconto > 0.0 and Sconto <= 1.0),
     constraint PK_REDDITI primary key (Codice),
     constraint AK_REDDITI unique (Fascia));

create table tipi_attivazione (
	 Codice integer not null,
     Nome varchar(20) not null,
     Costo decimal(20, 2) not null,
     check(Costo >= 0),
     constraint PK_TIPO_ATTIVAZIONE primary key (Codice));
     
create table tipi_immobile ( 
	 Codice integer not null auto_increment,
     Nome varchar(30) not null,
     HaInterno boolean not null,
     constraint PK_TIPO_IMMOBILE primary key (Codice));

create table tipologie_uso (
	 Codice integer not null auto_increment,
     Nome varchar(30) not null,
     StimaPerPersona decimal(20, 2) not null,
     ScontoReddito boolean not null,
     check(StimaPerPersona >= 0.0),
     constraint PK_USO_DEDICATO primary key (Codice));
     

-- ----------
-- Add records
-- ----------

-- Populate materie_prime
insert into materie_prime
values("Gas");

insert into materie_prime
values("Acqua");


-- Populate "tipologie_uso"

insert into tipologie_uso
values(default, "Abitativo residenziale", 0.2, true);

insert into tipologie_uso
values(default, "Abitativo non residenziale", 0.7, false);


-- Populate "tipi_attivazione"
insert into tipi_attivazione
values (1, "Nuova attivazione", 85.0);

insert into tipi_attivazione
values (2, "Subentro", 70.0);

insert into tipi_attivazione
values (3, "Voltura", 45.0);


-- Populate "redditi"
insert into redditi
values (1, "0 - 5.000", 0.5);

insert into redditi
values (2, "5.001 - 10.000", 0.7);

insert into redditi
values (3, "10.001 - 15.000", 0.95);

insert into redditi
values (4, "15.001 o più", 1.0);


-- Populate "tipi_immobile"
insert into tipi_immobile
values (default, "Terreno", false);

insert into tipi_immobile
values (default, "Edificio", false);

insert into tipi_immobile
values (default, "Appartamento", true);


-- Populate "immobili"
insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values (3, "Via Bongo", 69, 1, "Forlì", "47121", "FC");

insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values (2, "Via Roma", 11, null, "Cesena", "47521", "FC");


-- Populate "contatori"
insert into contatori (MateriaPrima, Matricola, IdImmobile)
values ("Gas", "83850395028543", 1);

insert into contatori (MateriaPrima, Matricola, IdImmobile)
values ("Acqua", "385011111111", 1);

insert into contatori (MateriaPrima, IdImmobile)
values ("Gas", 2);


-- Populate "letture"
insert into letture
values (18.0, 1, date_sub(curdate(), interval 2 month), true, 1);

insert into letture
values (35.0, 2, curdate(), true, 1);


-- Populate "persone", "clienti" and "operatori"
insert into persone
values (default, "MRMMRA55R08B963X", "Mario", "Maria Mario", "Via Mario", 64, "47121", "Forlì", "FC", 19551005, "35426324", "trallallero@boh.it", "ucciucci");

insert into persone
values (default, "BRTBBB25T87R762U", "Bartolomeo", "Bartolucci", "Via delle Vie", 12, "47521", "Cesena", "FC", 19860621, "38275722", "bartolomeo@gmail.com", "uffiuffi");

insert into persone
values (default, "GAGGUG92F28U275P", "Armando", "Armandini", "Viale Vialone", 73, "88100", "Catanzaro", "CZ", 19951030, "292892992", "amministratore@admin.com", "password");

insert into clienti
values (1, 3);

insert into clienti
values (2, 1);

insert into operatori
values (3);


-- Populate "offerte" and "compatibilità"
insert into offerte(Nome, Descrizione, CostoMateriaPrima, MateriaPrima)
values ("A tutto gas", "Una generica offerta per la fornitura di gas.", 0.3, "Gas");

insert into compatibilità
values (last_insert_id(), 1);

insert into compatibilità
values (last_insert_id(), 2);

insert into offerte(Nome, Descrizione, CostoMateriaPrima, MateriaPrima)
values ("Acqua santa", "Una generica offerta per la fornitura di acqua.", 0.18, "Acqua");

insert into compatibilità
values (last_insert_id(), 1);

insert into compatibilità
values (last_insert_id(), 2);

insert into offerte(Nome, Descrizione, CostoMateriaPrima, MateriaPrima)
values ("GAAAAS", "Tanto, tanto gas.", 0.45, "Gas");

insert into compatibilità
values (last_insert_id(), 1);


-- richieste_attivazione
insert into richieste_attivazione(DataRichiesta, Contatore, Offerta, Uso, Attivazione, NumeroComponenti, Cliente, Operatore, Stato)
values (date_sub(curdate(), interval 123 day), 2, 2, 1, 1, 4, 1, 3, "A");

insert into richieste_attivazione(DataRichiesta, Contatore, Offerta, Uso, Attivazione, NumeroComponenti, Cliente, Operatore, Stato)
values (date_sub(curdate(), interval 123 day), 1, 1, 1, 1, 4, 1, 3, "A");

insert into richieste_attivazione(DataRichiesta, Contatore, Offerta, Uso, Attivazione, NumeroComponenti, Cliente)
values (date_sub(curdate(), interval 2 day), 3, 2, 1, 1, 1, 2);


-- contratti
insert into contratti(IdContratto, DataInizio, DataUltimaBolletta)
values (1, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 2 month));

insert into contratti(IdContratto, DataInizio, DataUltimaBolletta)
values (2, date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 2 month));


-- Populate "bollette"
insert into bollette
values (1, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 3 month), date_sub(curdate(), interval 3 month), 124.64, unhex("54657374"), false);

insert into bollette
values (2, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 3 month), date_sub(curdate(), interval 3 month), 133.0, unhex("54657374"), true);

insert into bollette
values (1, date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), 19.97, unhex("54657374"), false);

insert into bollette
values (2, date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), 48.0, unhex("54657374"), true);

-- Foreign keys
-- ___________________ 

alter table bollette add constraint FK_CONTRATTO
     foreign key (IdContratto) references contratti (IdContratto);
     
alter table clienti add constraint FK_CODICECLIENTE
	 foreign key (CodiceCliente) references persone (Identificativo);
     
alter table clienti add constraint FK_POSSEDIMENTO
	foreign key (FasciaReddito) references redditi (Codice);

alter table compatibilità add constraint FK_USOOFFERTA
     foreign key (Uso) references tipologie_uso (Codice);

alter table compatibilità add constraint FK_OFFERTAUSO
     foreign key (CodiceOfferta) references offerte (Codice);
     
alter table contatori add constraint FK_MISURAZIONE
	foreign key (MateriaPrima) references materie_prime (Nome);

alter table contatori add constraint FK_INSTALLAZIONE
     foreign key (IdImmobile) references immobili (IdImmobile);
     
alter table contratti add constraint FK_DEFINIZIONE
	 foreign key (IdContratto) references richieste_attivazione (Numero);
     
alter table immobili add constraint FK_TIPO
	 foreign key (Tipo) references tipi_immobile (Codice);

alter table letture add constraint FK_CORRISPONDENZA
     foreign key (Contatore) references contatori (Progressivo);
     
alter table letture add constraint FK_EFFETTUAZIONE
     foreign key (Cliente) references clienti (CodiceCliente);

alter table offerte add constraint FK_INTERESSE
     foreign key (MateriaPrima) references materie_prime (Nome);
     
alter table operatori add constraint FK_DATIANAGRAFICI
	foreign key (CodiceOperatore) references persone (Identificativo);
    
alter table richieste_attivazione add constraint FK_RICHIESTA
     foreign key (Cliente) references clienti (CodiceCliente);

alter table richieste_attivazione add constraint FK_SOTTOSCRIZIONE
     foreign key (Offerta) references offerte (Codice);

alter table richieste_attivazione add constraint FK_USO
     foreign key (Uso) references tipologie_uso (Codice);
     
alter table richieste_attivazione add constraint FK_OPERATORE_ATT
     foreign key (Operatore) references operatori (CodiceOperatore);

alter table richieste_attivazione add constraint FK_ATTIVAZIONE_TRAMITE
     foreign key (Attivazione) references tipi_attivazione (Codice);

alter table richieste_attivazione add constraint FK_COLLEGAMENTO
     foreign key (Contatore) references contatori (Progressivo);
     
alter table richieste_cessazione add constraint FK_TERMINAZIONE
	 foreign key (IdContratto) references contratti (IdContratto);
     
alter table richieste_cessazione add constraint FK_OPERATORE_CES
	 foreign key (Operatore) references operatori (CodiceOperatore);
    

-- View section
-- _____________ 

create view contratti_dettagliati as select C.IdContratto, C.DataInizio, C.DataCessazione,
											R.DataRichiesta, R.Cliente, R.Offerta,
                                            R.Attivazione, R.Uso, R.Contatore,
                                            R.NumeroComponenti
									   from contratti C, richieste_attivazione R
									  where C.IdContratto = R.Numero;
                                      
create view clienti_dettagliati as select P.*, C.FasciaReddito
									 from persone P, clienti C
									where P.Identificativo = C.CodiceCliente;
                                    
create view operatori_dettagliati as select P.*
									   from persone P, operatori O
									  where P.Identificativo = O.CodiceOperatore;
			

-- Index Section
-- _____________ 

