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
     IdOperatore integer not null,
     IdContratto integer not null,
     check (DataScadenza > DataEmissione),
     check (DataFinePeriodo > DataInizioPeriodo),
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
     MateriaPrima varchar(20) not null check (MateriaPrima in ("Gas", "Acqua")),
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
     constraint PK_CONTRATTO primary key (IdContratto));

create table immobili (
     IdImmobile integer not null auto_increment,
     Tipo varchar(20) not null check (Tipo in ("Fabbricato", "Terreno")),
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
    DataChiusuraRichiesta date default null,
    Stato varchar(30) not null check (Stato in ("In lavorazione", "Approvata", "Respinta")),
    Note varchar(500) not null,
    Consumi decimal(20, 4) not null check (Consumi >= 0),
    IdCliente integer not null,
    constraint PK_LETTURE primary key (NumeroLettura),
    constraint AK_LETTURE unique (MatricolaContatore, DataEffettuazione));

create table offerte (
     CodOfferta integer not null auto_increment,
     Nome varchar(20) not null,
     Descrizione varchar(1000) not null,
     CostoMateriaPrima decimal(10, 4) not null check(CostoMateriaPrima > 0.0),
     Attiva boolean not null default true,
     MateriaPrima varchar(20) not null check (MateriaPrima in ("Gas", "Acqua")),
     constraint PK_OFFERTA primary key (CodOfferta));

create table operatori (
	 IdOperatore integer not null,
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
     Password varchar(32) not null,
     constraint AK_PERSONA unique (Email),
     constraint PK_PERSONA primary key (IdPersona));
     
create table redditi (
	 CodReddito integer not null,
	 Fascia varchar(30) not null,
     Sconto decimal(7, 6) not null check (Sconto > 0.0 and Sconto <= 1.0),
     constraint PK_REDDITI primary key (CodReddito),
     constraint AK_REDDITI unique (Fascia));

create table `tipi attivazione` (
	 CodAttivazione integer not null,
     Nome varchar(20) not null,
     Costo decimal(20, 2) not null,
     check(Costo >= 0),
     constraint PK_TIPO_ATTIVAZIONE primary key (CodAttivazione));

create table `tipologie uso` (
	 CodUso integer not null auto_increment,
     Nome varchar(30) not null,
     StimaPerPersona decimal(20, 2) not null,
     ScontoReddito boolean not null,
     check(StimaPerPersona >= 0.0),
     constraint PK_USO_DEDICATO primary key (CodUso));
     

-- ----------
-- Add records
-- ----------

-- Populate "tipologie_uso"

insert into `tipologie uso`
values(default, "Abitativo residenziale", 0.2, true);

insert into `tipologie uso`
values(default, "Abitativo non residenziale", 0.7, false);


-- Populate "tipi_attivazione"
insert into `tipi attivazione`
values (1, "Nuova attivazione", 85.0);

insert into `tipi attivazione`
values (2, "Subentro", 70.0);

insert into `tipi attivazione`
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


-- Populate "immobili"
insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values ("Fabbricato", "Corso della Repubblica", 12, 1, "Forlì", "47121", "FC");

insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values ("Fabbricato", "Via delle Viole", 55, "", "Sirolo", "60020", "AN");

insert into immobili (Tipo, Via, NumCivico, Interno, Comune, CAP, Provincia)
values ("Fabbricato", "Via Roma", 11, "", "Cesena", "47521", "FC");


-- Populate "contatori"
insert into contatori (Matricola, MateriaPrima, IdImmobile)
values ("83850395028543", "Gas", 1);

insert into contatori (Matricola, MateriaPrima, IdImmobile)
values ("385011111111", "Acqua", 1);

insert into contatori (Matricola, MateriaPrima, IdImmobile)
values ("38256395028277", "Gas", 2);


-- Populate "letture"
insert into letture
values (default, "83850395028543", date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 2 month), "Approvata", "", 18.0, 1);

insert into letture
values (default, "83850395028543", date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), "Approvata", "", 26.0, 1);

insert into letture
values (default, "385011111111", date_sub(curdate(), interval 2 month), date_sub(curdate(), interval 2 month), "Approvata", "", 35.0, 1);

insert into letture
values (default, "385011111111", date_sub(curdate(), interval 1 month), date_sub(curdate(), interval 1 month), "Approvata", "", 41.0, 1);


-- operatori letture
insert into `operatori letture`
values (1, 3);

insert into `operatori letture`
values (2, 3);

insert into `operatori letture`
values (3, 3);

insert into `operatori letture`
values (4, 3);


-- Populate "persone", "clienti" and "operatori"
insert into persone
values (default, "Mario", "Maria Mario", "MRMMRA55R08B963X", "Via Mario", 64, "Forlì", "47121", "FC", 19551005, "3542632412", "mariamario@gmail.com", md5("password"));

insert into persone
values (default, "Bartolomeo", "Bartolucci", "BRTBBB25T87R762U", "Via delle Vie", 12, "Cesena", "47521", "FC", 19860621, "3801985090", "bartolomeo@gmail.com", md5("password"));

insert into persone
values (default, "Osvaldo", "Ostrazio", "GAGGUG92F28U275P", "Viale Vialone", 73, "Catanzaro", "88100", "CZ", 19910717, "3472850772", "operatore@op.com", md5("password"));

insert into persone
values (default, "Giorgio", "Travaglino", "GRGTVG84R01O375L", "Via delle Viole", 55, "Sirolo", "60020", "AN", 19460412, "3339937492", "travaglino@gmail.com", md5("password"));

insert into clienti
values (1, 3);

insert into clienti
values (2, 1);

insert into operatori
values (3, 940.83);

insert into clienti
values (4, 1);


-- Populate "offerte" and "compatibilità"
insert into offerte(Nome, Descrizione, CostoMateriaPrima, Attiva, MateriaPrima)
values ("A tutto gas", "Una generica offerta per la fornitura di gas.", 0.3, true, "Gas");

insert into compatibilità
values (last_insert_id(), 1);

insert into compatibilità
values (last_insert_id(), 2);

insert into offerte(Nome, Descrizione, CostoMateriaPrima, Attiva, MateriaPrima)
values ("Pura", "Una generica offerta per la fornitura di acqua.", 0.18, true, "Acqua");

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
values (date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 122 day), "Approvata", "", 2, 1, 1, 4, 1, 1);

insert into contratti(DataAperturaRichiesta, DataChiusuraRichiesta, StatoRichiesta, NoteRichiesta, Offerta, Uso, TipoAttivazione, NumeroComponenti, IdImmobile, IdCliente)
values (date_sub(curdate(), interval 123 day), date_sub(curdate(), interval 122 day), "Approvata", "", 1, 1, 1, 4, 1, 1);

insert into contratti(DataAperturaRichiesta, DataChiusuraRichiesta, StatoRichiesta, NoteRichiesta, Offerta, Uso, TipoAttivazione, NumeroComponenti, IdImmobile, IdCliente)
values (20210813, 20210816, "Approvata", "", 1, 1, 2, 3, 2, 4);

insert into contratti(DataAperturaRichiesta, DataChiusuraRichiesta, StatoRichiesta, NoteRichiesta, Offerta, Uso, TipoAttivazione, NumeroComponenti, IdImmobile, IdCliente)
values (date_sub(curdate(), interval 2 day), default, "In lavorazione", " ", 2, 1, 1, 1, 3, 2);


-- operatori contratti
insert into `operatori contratti`
values (1, 3);

insert into `operatori contratti`
values (2, 3);

insert into `operatori contratti`
values (3, 3);


-- cessazioni
insert into cessazioni
values (default, curdate(), default, "In lavorazione", "", 1);

-- operatori cessazioni
insert into `operatori cessazioni`
values (1, 3);


-- Populate "bollette"
insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, IdOperatore)
values (1, date_sub(curdate(), interval 120 day), date_sub(curdate(), interval 185 day), date_sub(curdate(), interval 125 day), date_sub(curdate(), interval 90 day), 124.64, 35.0, unhex("54657374"), 3);

insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, IdOperatore)
values (2, date_sub(curdate(), interval 120 day), date_sub(curdate(), interval 185 day), date_sub(curdate(), interval 125 day), date_sub(curdate(), interval 90 day), 133.0, 6.0, unhex("54657374"), 3);

insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, IdOperatore)
values (3, 20211016, 20210816, 20211015, 20211030, 124.64, 18.0, unhex("54657374"), 3);

insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, IdOperatore)
values (1, date_sub(curdate(), interval 60 day), date_sub(curdate(), interval 125 day), date_sub(curdate(), interval 65 day), date_sub(curdate(), interval 30 day), 133.0, 18.0, unhex("54657374"), 3);

insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, IdOperatore)
values (2, date_sub(curdate(), interval 60 day), date_sub(curdate(), interval 125 day), date_sub(curdate(), interval 65 day), date_sub(curdate(), interval 30 day), 19.97, 44.0, unhex("54657374"), 3);

insert into bollette(IdContratto, DataEmissione, DataInizioPeriodo, DataFinePeriodo, DataScadenza, Importo, Consumi, DocumentoDettagliato, IdOperatore)
values (3, 20211216, 20211016, 20211215, 20211230, 48.0, 50.0, unhex("54657374"), 3);


-- Populate "pagamenti"
insert into pagamenti
values (1, date_sub(curdate(), interval 3 month));

insert into pagamenti
values (2, date_sub(curdate(), interval 3 month));

insert into pagamenti
values (3, 20211030);

insert into pagamenti
values (4, date_sub(curdate(), interval 1 month));

insert into pagamenti
values (5, date_sub(curdate(), interval 1 month));


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
    foreign key (Uso) references `tipologie uso` (CodUso) on delete cascade;

alter table compatibilità add constraint FK_OFFERTAUSO
    foreign key (Offerta) references offerte (CodOfferta) on delete cascade;

alter table contatori add constraint FK_INSTALLAZIONE
    foreign key (IdImmobile) references immobili (IdImmobile);
     
alter table contratti add constraint FK_RICHIESTA
    foreign key (IdCliente) references clienti (CodiceCliente);

alter table contratti add constraint FK_SOTTOSCRIZIONE
    foreign key (Offerta) references offerte (CodOfferta);

alter table contratti add constraint FK_USO
    foreign key (Uso) references `tipologie uso` (CodUso);

alter table contratti add constraint FK_ATTIVAZIONE_TRAMITE
    foreign key (TipoAttivazione) references `tipi attivazione` (CodAttivazione);

alter table contratti add constraint FK_COLLEGAMENTO
    foreign key (IdImmobile) references immobili (IdImmobile);

alter table letture add constraint FK_CORRISPONDENZA
    foreign key (MatricolaContatore) references contatori (Matricola) on update cascade;
     
alter table letture add constraint FK_EFFETTUAZIONE
    foreign key (IdCliente) references clienti (CodiceCliente);
     
alter table operatori add constraint FK_DATIANAGRAFICI
    foreign key (IdOperatore) references persone (IdPersona);
    
alter table `operatori cessazioni` add constraint FK_GESTIONE_CC1
    foreign key (NumeroRichiesta) references cessazioni (NumeroRichiesta) on delete cascade;
    
alter table `operatori cessazioni` add constraint FK_GESTIONE_CO1
    foreign key (IdOperatore) references operatori (IdOperatore) on delete cascade;

alter table `operatori contratti` add constraint FK_GESTIONE_CC2
    foreign key (NumeroRichiesta) references contratti (IdContratto) on delete cascade;
    
alter table `operatori contratti` add constraint FK_GESTIONE_CO2
    foreign key (IdOperatore) references operatori (IdOperatore) on delete cascade;

alter table `operatori letture` add constraint FK_GESTIONE_LC
    foreign key (Lettura) references letture (NumeroLettura) on delete cascade;
    
alter table `operatori letture` add constraint FK_GESTIONE_LO
    foreign key (IdOperatore) references operatori (IdOperatore) on delete cascade;

alter table pagamenti add constraint FK_PAGAMENTO
    foreign key (NumeroBolletta) references bollette (NumeroBolletta);
    

-- View section
-- _____________ 

create view `clienti dettagliati` as select P.*, C.FasciaReddito
									   from persone P, clienti C
									  where P.IdPersona = C.CodiceCliente;
                                    
create view `operatori dettagliati` as select P.*, O.Stipendio
									     from persone P, operatori O
									    where P.IdPersona = O.IdOperatore;
                                      
create view `richieste contratto` as select IdContratto, DataAperturaRichiesta, DataChiusuraRichiesta, StatoRichiesta, NoteRichiesta, NumeroComponenti,
												Uso, Offerta, TipoAttivazione, IdImmobile, IdCliente
									   from contratti
									  where DataChiusuraRichiesta is null or (DataChiusuraRichiesta is not null and StatoRichiesta = "Respinta");
                                      
create view `contratti approvati` as select C.*
								       from contratti C
								      where C.DataChiusuraRichiesta is not null and C.StatoRichiesta = "Approvata";
                                      
create view `contratti attivi` as select C.*
								    from `contratti approvati` C
								   where not exists (select E.NumeroRichiesta
													   from cessazioni E
													  where E.IdContratto = C.IdContratto
													    and E.DataChiusuraRichiesta is not null
													    and E.StatoRichiesta = "Approvata");
