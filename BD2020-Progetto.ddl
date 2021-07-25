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
     Importo integer not null default 0,
     DataEmissione date not null,
     DataScadenza date not null,
     DataPagamento date,
     check(Importo >= 0),
     constraint PK_BOLLETTA primary key (IdContratto, DataEmissione));

create table compatibilità (
     CodiceOfferta integer not null,
     TipoUso varchar(30) not null,
     constraint PK_COMPATIBILITÀ primary key (CodiceOfferta, TipoUso));

create table contratti (
     IdContratto integer not null auto_increment,
     DataRichiesta date not null,
     DataInizio date,
     DataCessazione date,
     MateriaPrima varchar(20) not null,
     IdImmobile integer not null,
     CodiceAzienda integer,
     DataUltimaBolletta date,
     PotenzaRichiesta decimal(10,4),
     ConsumiF1 decimal(10,4) not null default 0.0,
     ConsumiF2 decimal(10,4) default null,
     ConsumiF3 decimal(10,4) default null,
     FontiRinnovabili boolean not null default true,
     FattoreCostoDerivato decimal(10,4) not null,
     Interrotto boolean not null,
     CodiceCliente integer not null,
     CodiceOfferta integer not null,
     TipoUso varchar(30) not null,
     NomeAttivazione varchar(20) not null,
     constraint PK_CONTRATTO primary key (IdContratto),
     constraint AK_CONTRATTO unique (MateriaPrima, IdImmobile, DataCessazione));

create table distributori (
     Id integer not null auto_increment,
     Nome varchar(30) not null,
     NumeroContatto integer not null,
     EmailContatto varchar(30) not null,
     constraint PK_DISTRIBUTORE primary key (Id),
     constraint AK_DISTRIBUTORE unique (Nome));

create table distribuzioni (
     MateriaPrima varchar(20) not null,
     CAP integer not null,
     IdDistributore integer not null,
     constraint PK_DISTRIBUZIONE primary key (MateriaPrima, CAP));

create table immobili (
     IdImmobile integer not null,
     Tipo char(1) not null,
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     Interno integer,
     CodicePOD varchar(20),
     CodicePDR varchar(20),
     MatricolaContatoreAcqua varchar(20),
     CAP integer not null,
     check(Tipo in ('A', 'E')),
     constraint IDIMMOBILE primary key (IdImmobile),
     constraint IDIMMOBILE_1 unique (CodicePOD),
     constraint IDIMMOBILE_2 unique (CodicePDR),
     constraint IDIMMOBILE_3 unique (MatricolaContatoreAcqua),
     constraint IDIMMOBILE_4 unique (CAP, Via, NumCivico, Interno));

create table materie_prime (
     Nome varchar(20) not null,
     constraint PK_MATERIA primary key (Nome));

create table metodi_pagamento (
     CodiceMetodo integer not null auto_increment,
     Nome varchar(30) not null,
     constraint PK_METODO primary key (CodiceMetodo));

create table offerte (
     Codice integer not null auto_increment,
     Nome varchar(20) not null,
     CostoF1 decimal(10,4) not null check(CostoF1 >= 0.0),
     CostoF2 decimal(10,4) check(CostoF2 >= 0.0 or CostoF2 is null),
     CostoF3 decimal(10,4) check(CostoF3 >= 0.0 or CostoF3 is null),
     SoloNuoviClienti boolean not null default false,
     CostoBloccatoMesi integer not null default 0,
     IncrementoMensile decimal(4, 4) not null default 0,
     Attiva boolean not null default true,
     MateriaPrima varchar(20) not null,
     check(CostoBloccatoMesi >= 0),
     check(IncrementoMensile >= 0.0),
     check((CostoBloccatoMesi = 0 and IncrementoMensile = 0.0) or (CostoBloccatoMesi <> 0 and IncrementoMensile <> 0.0)),
     constraint IDOFFERTA_ID primary key (Codice));

create table persone_fisiche (
     CodiceCliente integer not null auto_increment,
     CodiceFiscale varchar(16) not null,
     PartitaIVA varchar(11),
     Nome varchar(50) not null,
     Cognome varchar(50) not null,
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     CAP integer not null,
     Comune varchar(30) not null,
     DataNascita date not null,
     NumeroTelefono integer not null,
     Email varchar(40) not null,
     Password varchar(30) not null,
     Reddito integer,
     CodiceMetodo integer not null,
     check(Reddito >= 0),
     constraint AK_PERSONA_FISICA unique (Email),
     constraint PK_PERSONA_FISICA primary key (CodiceCliente));

create table persone_giuridiche (
     CodiceCliente integer not null auto_increment,
     CodiceFiscale varchar(16) not null,
     PartitaIVA varchar(11),
     Nome varchar(50) not null,
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     CAP integer not null,
     Comune varchar(30) not null,
     FormaGiuridica varchar(30) not null,
     RagioneSociale varchar(30) not null,
     constraint PK_PERSONA_GIURIDICA primary key (CodiceCliente));

create table tipi_attivazione (
     Nome varchar(20) not null,
     CostoUnaTantum decimal(10,4) not null,
     check(CostoUnaTantum >= 0),
     constraint PK_TIPO_ATTIVAZIONE primary key (Nome));

create table tipologie_uso (
     Nome varchar(30) not null,
     CanoneRAI boolean not null,
     ModificatoreCosto decimal(10, 4) not null,
     check(ModificatoreCosto >= 0.0),
     constraint PK_USO_DEDICATO primary key (Nome));

create table zone (
     CAP integer not null,
     Comune varchar(50) not null,
     Provincia varchar(2) not null,
     constraint PK_ZONA primary key (CAP));

-- Populate materie_prime
insert into materie_prime
values("Luce");

insert into materie_prime
values("Gas");

insert into materie_prime
values("Acqua");


-- Populate zone
insert into zone
values(47521, "Cesena", "FC");

insert into zone
values(47522, "Cesena", "FC");

insert into zone
values(47121, "Forlì", "FC");

insert into zone
values(47122, "Forlì", "FC");

insert into zone
values(88100, "Catanzaro", "CZ");


-- Populate distributori
insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Dianacomm S.p.A.", 1282341, "pepignoimpastagno@bonobbo.com");

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Sudgas Reti S.p.A.", 6247372, "eeeeeooooo@bonobbo.com");

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Z-Distribuzioni S.p.A.", 7372836, "woooooooooo@bonobbo.com");

insert into distributori (Nome, NumeroContatto, EmailContatto)
values("Comune di Catanzaro", 1212113, "alalalalala@bonobbo.com");



-- Populate distribuzioni
-- Gas
insert into distribuzioni
values("Gas", 88100, 2);

insert into distribuzioni
values("Gas", 47521, 1);

insert into distribuzioni
values("Gas", 47522, 1);

insert into distribuzioni
values("Gas", 47121, 1);

insert into distribuzioni
values("Gas", 47122, 1);

-- Luce
insert into distribuzioni
values("Luce", 88100, 3);

insert into distribuzioni
values("Luce", 47521, 3);

insert into distribuzioni
values("Luce", 47522, 3);

insert into distribuzioni
values("Luce", 47121, 3);

insert into distribuzioni
values("Luce", 47122, 3);

-- Acqua
insert into distribuzioni
values("Acqua", 88100, 4);

insert into distribuzioni
values("Acqua", 47521, 1);

insert into distribuzioni
values("Acqua", 47522, 1);

insert into distribuzioni
values("Acqua", 47121, 1);

insert into distribuzioni
values("Acqua", 47122, 1);


-- Populate 'metodi_pagamento'
insert into metodi_pagamento(Nome)
values("Addebito su conto corrente");

insert into metodi_pagamento(Nome)
values("Bollettino postale");

-- Constraints Section
-- ___________________ 

alter table bollette add constraint FK_CONTRATTO
     foreign key (IdContratto) references contratti (IdContratto);

alter table compatibilità add constraint FK_USOOFFERTA
     foreign key (TipoUso) references tipologie_uso (Nome);

alter table compatibilità add constraint FK_OFFERTAUSO
     foreign key (CodiceOfferta) references offerte (Codice);

alter table contratti add constraint FK_SOTTOSCRIZIONE
     foreign key (CodiceCliente) references persone_fisiche (CodiceCliente);

alter table contratti add constraint FK_REFERENZA
     foreign key (CodiceAzienda) references persone_giuridiche (CodiceCliente);

alter table contratti add constraint FK_AFFERENZA
     foreign key (CodiceOfferta) references offerte (Codice);

alter table contratti add constraint FK_USO
     foreign key (TipoUso) references tipologie_uso (Nome);

alter table contratti add constraint FK_FORNITURA
     foreign key (MateriaPrima) references materie_prime (Nome);

alter table contratti add constraint FK_ATTIVAZIONE_TRAMITE
     foreign key (NomeAttivazione) references tipi_attivazione (Nome);

alter table contratti add constraint FK_PRESSO
     foreign key (IdImmobile) references immobili (IdImmobile);

alter table distribuzioni add constraint FK_RIFERIMENTO
     foreign key (CAP) references zone (CAP);

alter table distribuzioni add constraint FK_DISTRIBUITA
     foreign key (MateriaPrima) references materie_prime (Nome);

alter table distribuzioni add constraint FK_OPERAZIONE
     foreign key (IdDistributore) references distributori (Id);

alter table immobili add constraint FK_SITO_IN
     foreign key (CAP) references zone (CAP);

alter table offerte add constraint FKINTERESSE
     foreign key (MateriaPrima) references materie_prime (Nome);

alter table persone_fisiche add constraint FKSCELTA
     foreign key (CodiceMetodo) references metodi_pagamento (CodiceMetodo);

-- Index Section
-- _____________ 

