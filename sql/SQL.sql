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
     EmessaDa integer not null,
     check (DataScadenza > DataEmissione),
     check (DataPagamento is null or DataPagamento >= DataEmissione),
	 constraint PK_BOLLETTA primary key (IdContratto, DataEmissione));
     
create table clienti (
	 CodiceCliente integer not null,
     FasciaReddito varchar(30) not null,
     constraint PK_CLIENTI primary key (CodiceCliente));

create table compatibilità (
     CodiceOfferta integer not null,
     Uso integer not null,
     constraint PK_COMPATIBILITÀ primary key (CodiceOfferta, Uso));
     
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
     NumeroRichiesta integer not null,
     DataInizio date not null,
     DataCessazione date default null,
     DataUltimaBolletta date default null,
     constraint PK_CONTRATTO primary key (IdContratto),
     constraint AK_CONTRATTO unique (NumeroRichiesta));

create table immobili (
     IdImmobile integer not null auto_increment,
     Tipo char(1) not null check(Tipo in ('F', 'T')),
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     Interno varchar(10),
	 Comune varchar(50) not null,
     Provincia varchar(2) not null,
     CAP varchar(5) not null check (length(CAP) = 5),
     check((Tipo = 'F') or (Tipo = 'T' and Interno is null)),
     constraint IDIMMOBILE primary key (IdImmobile),
     constraint IDIMMOBILE_2 unique (Via, NumCivico, Interno, Comune, Provincia));
     
create table interruzioni (
     IdContratto integer not null,
	 DataInterruzione date not null,
     DataRiattivazione date default null,
     Motivazione varchar(1000) not null,
     IndettaDa integer not null,
     AnnullataDa integer default null,
     check (DataRiattivazione is null or DataRiattivazione >= DataInterruzione),
     constraint PK_INTERRUZIONI primary key (DataInterruzione, IdContratto)
);

create table letture (
    Consumi decimal(20, 6) not null check(Consumi >= 0),
    Contatore integer not null,
    DataEffettuazione date not null,
    Confermata boolean not null default false,
    Operatore integer default null,
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
     Esito char default null check (Esito is null or Esito in ("A", "R")),
     Note varchar(200) not null default "",
     Operatore integer default null,
     Contatore integer not null,
     Cliente integer not null,
     Offerta integer not null,
     Uso integer not null,
     Attivazione integer not null,
     constraint PK_RIC_ATTIVAZIONE primary key (Numero));
     
create table richieste_cessazione (
     Numero integer not null auto_increment,
     DataRichiesta date not null,
     Esito char default null check (Esito is null or Esito in ("A", "R")),
     Note varchar(200) not null default "",
     Operatore integer default null,
     IdContratto integer not null,
     Cliente integer not null,
     constraint PK_RIC_CESSAZIONE primary key (Numero));
     
create table redditi (
	Fascia varchar(30) not null,
    Sconto decimal(7, 6) not null check (Sconto > 0.0 and Sconto <= 1.0),
    constraint PK_REDDITI primary key (Fascia));

create table tipi_attivazione (
	 Codice integer not null,
     Nome varchar(20) not null,
     Costo decimal(20, 2) not null,
     check(Costo >= 0),
     constraint PK_TIPO_ATTIVAZIONE primary key (Codice));

create table tipologie_uso (
	 Codice integer not null,
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
values(1, "Abitativo residenziale", 0.2, true);

insert into tipologie_uso
values(2, "Abitativo non residenziale", 0.7, false);


-- Populate "tipi_attivazione"
insert into tipi_attivazione
values (1, "Nuova attivazione", 85.0);

insert into tipi_attivazione
values (2, "Subentro", 70.0);

insert into tipi_attivazione
values (3, "Voltura", 45.0);


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
insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values ('F', "Via Bongo", 69, 1, "Forlì", "47121", "FC");

insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values ('F', "Via Roma", 11, null, "Cesena", "47521", "FC");


-- Populate "contatori"
insert into contatori (MateriaPrima, Matricola, IdImmobile)
values ("Acqua", "385011111111", 1);

insert into contatori (MateriaPrima, Matricola, IdImmobile)
values ("Gas", "83850395028543", 1);

insert into contatori (MateriaPrima, Matricola, IdImmobile)
values ("Gas", "19287488822211", 2);


-- Populate "letture"
insert into letture
values (18.0, 1, date_sub(curdate(), interval 2 month), true, 3, 1);

insert into letture
values (35.0, 1, curdate(), true, 3, 1);


-- Populate "persone", "clienti" and "operatori"
insert into persone
values (default, "MRMMRA55R08B963X", "Mario", "Maria Mario", "Via Mario", 64, "47121", "Forlì", "FC", 19551005, "35426324", "trallallero@boh.it", "ucciucci");

insert into persone
values (default, "BRTBBB25T87R762U", "Bartolomeo", "Bartolucci", "Via delle Vie", 12, "47521", "Cesena", "FC", 19860621, "38275722", "bartolomeo@gmail.com", "uffiuffi");

insert into persone
values (default, "GAGGUG92F28U275P", "Armando", "Armandini", "Viale Vialone", 73, "88100", "Catanzaro", "CZ", 19951030, "292892992", "amministratore@admin.com", "password");

insert into clienti
values (1, "10001 - 15000");

insert into clienti
values (2, "0 - 5000");

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


-- richieste_attivazione
insert into richieste_attivazione(DataRichiesta, Contatore, Offerta, Uso, Attivazione, NumeroComponenti, Cliente, Esito, Operatore)
values (date_sub(curdate(), interval 123 day), 1, 2, 1, 1, 4, 1, "A", 3);

insert into richieste_attivazione(DataRichiesta, Contatore, Offerta, Uso, Attivazione, NumeroComponenti, Cliente, Esito, Operatore)
values (date_sub(curdate(), interval 123 day), 2, 1, 1, 1, 4, 1, "A", 3);

insert into richieste_attivazione(DataRichiesta, Contatore, Offerta, Uso, Attivazione, NumeroComponenti, Cliente, Esito, Operatore)
values (date_sub(curdate(), interval 2 day), 3, 2, 1, 1, 1, 2, null, null);


-- contratti
insert into contratti(NumeroRichiesta, DataInizio, DataUltimaBolletta)
values (1, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 2 month));

insert into contratti(NumeroRichiesta, DataInizio, DataUltimaBolletta)
values (2, date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 2 month));


-- Populate "bollette"
insert into bollette
values (1, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 3 month), date_sub(curdate(), interval 3 month), 124.64, unhex("54657374"), false, 3);

insert into bollette
values (2, date_sub(curdate(), interval 4 month), date_sub(curdate(), interval 3 month), date_sub(curdate(), interval 3 month), 133.0, unhex("54657374"), true, 3);

insert into bollette
values (1, date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), 19.97, unhex("54657374"), false, 3);

insert into bollette
values (2, date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), 48.0, unhex("54657374"), true, 3);

-- Foreign keys
-- ___________________ 

alter table bollette add constraint FK_CONTRATTO
     foreign key (IdContratto) references contratti (IdContratto);
     
alter table bollette add constraint FK_EMISSIONE
	 foreign key (EmessaDa) references operatori (CodiceOperatore);
     
alter table clienti add constraint FK_CODICECLIENTE
	 foreign key (CodiceCliente) references persone (Identificativo);
     
alter table clienti add constraint FK_POSSEDIMENTO
	foreign key (FasciaReddito) references redditi (Fascia) on update cascade;

alter table compatibilità add constraint FK_USOOFFERTA
     foreign key (Uso) references tipologie_uso (Codice);

alter table compatibilità add constraint FK_OFFERTAUSO
     foreign key (CodiceOfferta) references offerte (Codice);
     
alter table contatori add constraint FK_MISURAZIONE
	foreign key (MateriaPrima) references materie_prime (Nome);

alter table contatori add constraint FK_INSTALLAZIONE
     foreign key (IdImmobile) references immobili (IdImmobile);
     
alter table contratti add constraint FK_DEFINIZIONE
	 foreign key (NumeroRichiesta) references richieste_attivazione (Numero);
     
alter table interruzioni add constraint FK_SUBITA
	 foreign key (IdContratto) references contratti (IdContratto);
     
alter table interruzioni add constraint FK_INDETTA
	 foreign key (IndettaDa) references operatori (CodiceOperatore);
     
alter table interruzioni add constraint FK_ANNULLAMENTO
	 foreign key (AnnullataDa) references operatori (CodiceOperatore);

alter table letture add constraint FK_CORRISPONDENZA
     foreign key (Contatore) references contatori (NumeroProgressivo);
	
alter table letture add constraint FK_CONFERMA
	 foreign key (Operatore) references operatori (CodiceOperatore);
     
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

alter table richieste_attivazione add constraint FK_ATTIVAZIONE_TRAMITE
     foreign key (Attivazione) references tipi_attivazione (Codice);

alter table richieste_attivazione add constraint FK_COLLEGAMENTO
     foreign key (Contatore) references contatori (NumeroProgressivo);

alter table richieste_attivazione add constraint FK_ATTIVAZIONE
	 foreign key (Operatore) references operatori (CodiceOperatore);
     
alter table richieste_cessazione add constraint FK_RICHIESTA_CESS
	 foreign key (Cliente) references clienti (CodiceCliente);
     
alter table richieste_cessazione add constraint FK_RICHIESTA_ESITO
	 foreign key (Operatore) references operatori (CodiceOperatore);
     
alter table richieste_cessazione add constraint FK_TERMINAZIONE
	 foreign key (IdContratto) references contratti (IdContratto);
    

-- View section
-- _____________ 

create view contratti_dettagliati as select C.IdContratto, C.DataInizio, C.DataCessazione,
											R.DataRichiesta, R.Cliente, R.Offerta,
                                            R.Attivazione, R.Uso, R.Contatore,
                                            R.NumeroComponenti
									   from contratti C, richieste_attivazione R
									  where C.NumeroRichiesta = R.Numero;
                                      
create view clienti_dettagliati as select P.*, C.FasciaReddito
									 from persone P, clienti C
									where P.Identificativo = C.CodiceCliente;
                                    
create view operatori_dettagliati as select P.*
									   from persone P, operatori O
									  where P.Identificativo = O.CodiceOperatore;
			

-- Index Section
-- _____________ 

