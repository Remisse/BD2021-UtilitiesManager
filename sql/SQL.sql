drop database if exists utenze;

create database if not exists utenze;

use utenze;

-- Tables Section
-- _____________ 

create table bollette (
	 NumeroBolletta integer auto_increment not null,
     DataEmissione date not null,
     DataInizioPeriodo date not null,
     DataFinePeriodo date not null,
     DataScadenza date not null,
     Importo decimal(20, 2) not null,
     Consumi decimal(20, 4) not null,
     DocumentoDettagliato mediumblob not null,
     Stimata boolean not null,
     IdOperatore integer not null,
     IdContratto integer not null,
     check (DataScadenza > DataEmissione),
	 constraint PK_BOLLETTA primary key (NumeroBolletta));
     
create table cessazioni (
     NumeroRichiesta integer not null auto_increment,
     DataAperturaRichiesta date not null,
     DataChiusuraRichiesta date default null,
     StatoRichiesta varchar(30) not null check (StatoRichiesta in ("In lavorazione", "Approvata", "Respinta")),
     NoteRichiesta varchar(500) not null,
     IdContratto integer not null,
     constraint PK_RIC_CESSAZIONE primary key (NumeroRichiesta));
     
create table clienti (
	 CodiceCliente integer not null,
     FasciaReddito integer not null,
     constraint PK_CLIENTI primary key (CodiceCliente));

create table compatibilità (
     Offerta integer not null,
     Uso integer not null,
     constraint PK_COMPATIBILITÀ primary key (Offerta, Uso));
     
create table contatori (
	 Matricola varchar(20) not null,
     MateriaPrima varchar(20) not null,
     IdImmobile integer not null,
     constraint PK_CONTATORI primary key (Matricola),
     constraint AK_CONTATORI unique (IdImmobile, MateriaPrima));

create table contratti (
     IdContratto integer not null auto_increment,
     DataAperturaRichiesta date not null,
     DataChiusuraRichiesta date default null,
     StatoRichiesta varchar(30) not null check (StatoRichiesta in ("In lavorazione", "Approvata", "Respinta")),
     NoteRichiesta varchar(500) not null,
     NumeroComponenti integer not null check (NumeroComponenti > 0),
     Uso integer not null,
     Offerta integer not null,
     TipoAttivazione integer not null,
     IdImmobile integer not null,
     IdCliente integer not null,
     DataCessazione date default null,
     constraint PK_CONTRATTO primary key (IdContratto));

create table immobili (
     IdImmobile integer not null auto_increment,
     Tipo varchar(20) not null check (Tipo = "Fabbricato" or Tipo = "Terreno"),
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     Interno varchar(10) not null default "",
	 Comune varchar(50) not null,
     Provincia varchar(2) not null,
     CAP varchar(5) not null check (length(CAP) = 5),
     constraint TERRAIN_NO_UNIT check ((Tipo = "Terreno" and Interno = "") or (Tipo = "Fabbricato")),
     constraint IDIMMOBILE primary key (IdImmobile),
     constraint AK_IMMOBILE unique (Tipo, Via, NumCivico, Interno, Comune, Provincia, CAP));

create table letture (
    NumeroLettura integer auto_increment not null,
    MatricolaContatore varchar(20) not null,
    DataEffettuazione date not null,
    Consumi decimal(20, 4) not null check (Consumi >= 0),
    Stato varchar(30) not null check (Stato in ("In lavorazione", "Approvata", "Respinta")),
    IdPersona integer not null,
    constraint PK_LETTURE primary key (NumeroLettura),
    constraint AK_LETTURE unique (MatricolaContatore, DataEffettuazione));

create table materie_prime (
     Nome varchar(20) not null,
     constraint PK_MATERIA primary key (Nome));

create table offerte (
     CodOfferta integer not null auto_increment,
     Nome varchar(20) not null,
     Descrizione varchar(1000) not null,
     CostoMateriaPrima decimal(10, 4) not null check(CostoMateriaPrima > 0.0),
     Attiva boolean not null default true,
     MateriaPrima varchar(20) not null,
     constraint PK_OFFERTA primary key (CodOfferta));

create table operatori (
	 IdOperatore integer not null,
     Tipo varchar(30) not null check (Tipo in ("Amministratore", "Operatore")),
     Stipendio decimal(20, 2) not null check (Stipendio >= 0),
     constraint PK_OPERATORE primary key (IdOperatore));
     
create table `operatori contratti` (
	 NumeroRichiesta integer not null,
     IdOperatore integer not null,
     constraint PK_OPCONTR primary key (NumeroRichiesta));
     
create table `operatori cessazioni` (
	 NumeroRichiesta integer not null,
     IdOperatore integer not null,
     constraint PK_OPCONTR primary key (NumeroRichiesta));
     
create table `operatori letture` (
	 Lettura integer not null,
     IdOperatore integer not null,
     constraint PK_OPCONTR primary key (Lettura));
     
create table pagamenti (
	 NumeroBolletta integer not null,
     DataPagamento date not null,
     constraint PK_OPCONTR primary key (NumeroBolletta));

create table persone (
     IdPersona integer not null auto_increment,
     Nome varchar(50) not null,
     Cognome varchar(50) not null,
     CodiceFiscale varchar(16) not null,
     Via varchar(50) not null,
     NumCivico varchar(10) not null,
     Comune varchar(30) not null,
     CAP varchar(5) not null,
     Provincia varchar(2) not null,
     DataNascita date not null,
     NumeroTelefono varchar(10) not null,
     Email varchar(40) not null,
     Password varchar(30) not null check(length(Password) >= 8),
     constraint AK_PERSONA unique (Email),
     constraint PK_PERSONA primary key (IdPersona));
     
create table redditi (
	 CodReddito integer not null,
	 Fascia varchar(30) not null,
     Sconto decimal(7, 6) not null check (Sconto > 0.0 and Sconto <= 1.0),
     constraint PK_REDDITI primary key (CodReddito),
     constraint AK_REDDITI unique (Fascia));

create table tipi_attivazione (
	 CodAttivazione integer not null,
     Nome varchar(20) not null,
     Costo decimal(20, 2) not null,
     RichiedeVecchioIntestatario bool not null,
     RichiedeMatricolaContatore bool not null,
     RichiedeLettura bool not null,
     check(Costo >= 0),
     constraint PK_TIPO_ATTIVAZIONE primary key (CodAttivazione));

create table tipologie_uso (
	 CodUso integer not null auto_increment,
     Nome varchar(30) not null,
     StimaPerPersona decimal(20, 2) not null,
     ScontoReddito boolean not null,
     check(StimaPerPersona >= 0.0),
     constraint PK_USO_DEDICATO primary key (CodUso));
     

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
values (1, "Nuova attivazione", 85.0, false, false, false);

insert into tipi_attivazione
values (2, "Subentro", 70.0, false, true, false);

insert into tipi_attivazione
values (3, "Voltura", 45.0, true, true, true);


-- Populate "redditi"
insert into redditi
values (1, "0 - 5.000", 0.5);

insert into redditi
values (2, "5.001 - 10.000", 0.7);

insert into redditi
values (3, "10.001 - 15.000", 0.95);

insert into redditi
values (4, "15.001 o più", 1.0);


-- Populate "immobili"
insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values ("Fabbricato", "Via Bongo", 69, 1, "Forlì", "47121", "FC");

insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values ("Fabbricato", "Via Roma", 11, "", "Cesena", "47521", "FC");


-- Populate "contatori"
insert into contatori (Matricola, MateriaPrima, IdImmobile)
values ("83850395028543", "Gas", 1);

insert into contatori (Matricola, MateriaPrima, IdImmobile)
values ("385011111111", "Acqua", 1);


-- Populate "letture"
insert into letture
values (default, "83850395028543", date_sub(curdate(), interval 2 month), 18.0, "Approvata", 1);

insert into letture
values (default, "385011111111", date_sub(curdate(), interval 2 month), 35.0, "Approvata", 1);


-- operatori letture
insert into `operatori letture`
values (1, 4);

insert into `operatori letture`
values (2, 4);


-- Populate "persone", "clienti" and "operatori"
insert into persone
values (default, "Mario", "Maria Mario", "MRMMRA55R08B963X", "Via Mario", 64, "Forlì", "47121", "FC", 19551005, "3542632412", "trallallero@boh.it", "ucciucci");

insert into persone
values (default, "Bartolomeo", "Bartolucci", "BRTBBB25T87R762U", "Via delle Vie", 12, "Cesena", "47521", "FC", 19860621, "3801985090", "bartolomeo@gmail.com", "uffiuffi");

insert into persone
values (default, "Armando", "Armandini", "GAGGUG92F28U275P", "Viale Vialone", 73, "Catanzaro", "88100", "CZ", 19910717, "3472850772", "amministratore@admin.com", "password");

insert into persone
values (default, "Osvaldo", "Ostrazio", "OSVOST29I17F762P", "Corso Corsini", 18, "Godo", "48026", "RA", 19890131, "3339920275", "operatore@op.com", "password");

insert into clienti
values (1, 3);

insert into clienti
values (2, 1);

insert into operatori
values (3, "Amministratore", 1640.83);

insert into operatori
values (4, "Operatore", 900.15);


-- Populate "offerte" and "compatibilità"
insert into offerte(Nome, Descrizione, CostoMateriaPrima, Attiva, MateriaPrima)
values ("A tutto gas", "Una generica offerta per la fornitura di gas.", 0.3, true, "Gas");

insert into compatibilità
values (last_insert_id(), 1);

insert into compatibilità
values (last_insert_id(), 2);

insert into offerte(Nome, Descrizione, CostoMateriaPrima, Attiva, MateriaPrima)
values ("Acqua santa", "Una generica offerta per la fornitura di acqua.", 0.18, true, "Acqua");

insert into compatibilità
values (last_insert_id(), 1);

insert into compatibilità
values (last_insert_id(), 2);

insert into offerte(Nome, Descrizione, CostoMateriaPrima, Attiva, MateriaPrima)
values ("GAAAAS", "Tanto, tanto gas.", 0.45, true, "Gas");

insert into compatibilità
values (last_insert_id(), 1);


-- contratti
insert into contratti(DataAperturaRichiesta, DataChiusuraRichiesta, StatoRichiesta, NoteRichiesta, Offerta, Uso, TipoAttivazione, NumeroComponenti, IdImmobile, IdCliente)
values (date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 122 day), "Approvata", " ", 2, 1, 1, 4, 1, 1);

insert into contratti(DataAperturaRichiesta, DataChiusuraRichiesta, StatoRichiesta, NoteRichiesta, Offerta, Uso, TipoAttivazione, NumeroComponenti, IdImmobile, IdCliente)
values (date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 122 day), "Approvata", " ", 1, 1, 1, 4, 1, 1);

insert into contratti(DataAperturaRichiesta, DataChiusuraRichiesta, StatoRichiesta, NoteRichiesta, Offerta, Uso, TipoAttivazione, NumeroComponenti, IdImmobile, IdCliente)
values (date_sub(curdate(), interval 2 day), default, "In lavorazione", " ", 2, 1, 1, 1, 2, 2);


-- operatori contratti
insert into `operatori contratti`
values (1, 4);

insert into `operatori contratti`
values (2, 4);


-- cessazioni
insert into cessazioni
values (default, curdate(), default, "In lavorazione", "", 1);


-- operatori cessazioni
insert into `operatori cessazioni`
values (1, 4);

-- Populate "bollette"
insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, Stimata, IdOperatore)
values (1, date_sub(curdate(), interval 120 day), date_sub(curdate(), interval 185 day), date_sub(curdate(), interval 125 day), date_sub(curdate(), interval 90 day), 124.64, 18.0, unhex("54657374"), false, 4);

insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, Stimata, IdOperatore)
values (2, date_sub(curdate(), interval 120 day), date_sub(curdate(), interval 185 day), date_sub(curdate(), interval 125 day), date_sub(curdate(), interval 90 day), 133.0, 35.0, unhex("54657374"), false, 4);

insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, Stimata, IdOperatore)
values (1, date_sub(curdate(), interval 60 day), date_sub(curdate(), interval 125 day), date_sub(curdate(), interval 65 day), date_sub(curdate(), interval 30 day), 19.97, 33.0, unhex("54657374"), true, 4);

insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, Stimata, IdOperatore)
values (2, date_sub(curdate(), interval 60 day), date_sub(curdate(), interval 125 day), date_sub(curdate(), interval 65 day), date_sub(curdate(), interval 30 day), 48.0, 50.0, unhex("54657374"), true, 4);


-- Populate "pagamenti"
insert into pagamenti
values (1, date_sub(curdate(), interval 3 month));

insert into pagamenti
values (2, date_sub(curdate(), interval 3 month));

insert into pagamenti
values (3, date_sub(curdate(), interval 1 month));

insert into pagamenti
values (4, date_sub(curdate(), interval 1 month));


-- Foreign keys
-- ___________________ 

alter table bollette add constraint FK_CONTRATTO
     foreign key (IdContratto) references contratti (IdContratto);
     
alter table bollette add constraint FK_EMISSIONE
     foreign key (IdOperatore) references operatori (IdOperatore);
     
alter table cessazioni add constraint FK_RIFERIMENTO
	 foreign key (IdContratto) references contratti (IdContratto);
     
alter table clienti add constraint FK_CODICECLIENTE
	 foreign key (CodiceCliente) references persone (IdPersona);
     
alter table clienti add constraint FK_POSSEDIMENTO
	foreign key (FasciaReddito) references redditi (CodReddito);

alter table compatibilità add constraint FK_USOOFFERTA
     foreign key (Uso) references tipologie_uso (CodUso);

alter table compatibilità add constraint FK_OFFERTAUSO
     foreign key (Offerta) references offerte (CodOfferta);
     
alter table contatori add constraint FK_MISURAZIONE
	foreign key (MateriaPrima) references materie_prime (Nome);

alter table contatori add constraint FK_INSTALLAZIONE
     foreign key (IdImmobile) references immobili (IdImmobile);
     
alter table contratti add constraint FK_RICHIESTA
     foreign key (IdCliente) references clienti (CodiceCliente);

alter table contratti add constraint FK_SOTTOSCRIZIONE
     foreign key (Offerta) references offerte (CodOfferta);

alter table contratti add constraint FK_USO
     foreign key (Uso) references tipologie_uso (CodUso);

alter table contratti add constraint FK_ATTIVAZIONE_TRAMITE
     foreign key (TipoAttivazione) references tipi_attivazione (CodAttivazione);

alter table contratti add constraint FK_COLLEGAMENTO
     foreign key (IdImmobile) references immobili (IdImmobile);

alter table letture add constraint FK_CORRISPONDENZA
     foreign key (MatricolaContatore) references contatori (Matricola);
     
alter table letture add constraint FK_EFFETTUAZIONE
     foreign key (IdPersona) references persone (IdPersona);

alter table offerte add constraint FK_INTERESSE
     foreign key (MateriaPrima) references materie_prime (Nome);
     
alter table operatori add constraint FK_DATIANAGRAFICI
	foreign key (IdOperatore) references persone (IdPersona);
    
alter table `operatori cessazioni` add constraint FK_GESTIONE_CC1
	foreign key (NumeroRichiesta) references cessazioni (NumeroRichiesta);
    
alter table `operatori cessazioni` add constraint FK_GESTIONE_CO1
	foreign key (IdOperatore) references operatori (IdOperatore);

alter table `operatori contratti` add constraint FK_GESTIONE_CC2
	foreign key (NumeroRichiesta) references contratti (IdContratto);
    
alter table `operatori contratti` add constraint FK_GESTIONE_CO2
	foreign key (IdOperatore) references operatori (IdOperatore);

alter table `operatori letture` add constraint FK_GESTIONE_LC
	foreign key (Lettura) references letture (NumeroLettura);
    
alter table `operatori letture` add constraint FK_GESTIONE_LO
	foreign key (IdOperatore) references operatori (IdOperatore);

alter table pagamenti add constraint FK_PAGAMENTO
	foreign key (NumeroBolletta) references bollette (NumeroBolletta);
    

-- View section
-- _____________ 

create view `clienti dettagliati` as select P.*, C.FasciaReddito
									   from persone P, clienti C
									  where P.IdPersona = C.CodiceCliente;
                                    
create view `operatori dettagliati` as select P.*, O.Tipo, O.Stipendio
									     from persone P, operatori O
									    where P.IdPersona = O.IdOperatore;
                                      
create view `richieste contratto` as select IdContratto, DataAperturaRichiesta, DataChiusuraRichiesta, StatoRichiesta, NoteRichiesta, NumeroComponenti,
												Uso, Offerta, TipoAttivazione, IdImmobile, IdCliente
									   from contratti
									  where DataChiusuraRichiesta is null or (DataChiusuraRichiesta is not null and StatoRichiesta = "Respinta");
                                      
create view `contratti approvati` as select C.*
								       from contratti C
								      where DataChiusuraRichiesta is not null and StatoRichiesta = "Approvata";
