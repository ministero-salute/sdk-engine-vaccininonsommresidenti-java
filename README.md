
# **1.Introduzione**

## ***1.1 Obiettivi del documento***

Il Ministero della Salute (MdS) metterà a disposizione degli Enti, da cui riceve dati, applicazioni SDK specifiche per flusso logico e tecnologie applicative (Java, PHP e C#) per verifica preventiva (in casa Ente) della qualità del dato prodotto.

![](img/img4.png)

Nel presente documento sono fornite la struttura e la sintassi dei tracciati previsti dalla soluzione SDK per avviare il proprio processo elaborativo e i controlli di merito sulla qualità, completezza e coerenza dei dati.

Gli obiettivi del documento sono:

- fornire una descrizione funzionale chiara e consistente dei tracciati di input a SDK;
- fornire le regole funzionali per la verifica di qualità, completezza e coerenza dei dati;

In generale, la soluzione SDK è costituita da 2 diversi moduli applicativi (Access Layer e Validation Engine) per abilitare

- l’interoperabilità con il contesto tecnologico dell’Ente in cui la soluzione sarà installata;
- la validazione del dato ed il suo successivo invio verso il MdS.

La figura che segue descrive la soluzione funzionale ed i relativi benefici attesi.

![](img/img2.png)

## ***1.2 Acronimi***

Nella tabella riportata di seguito sono elencati tutti gli acronimi e le definizioni adottati nel presente documento.


|**#**|**Acronimo / Riferimento**|**Definizione**|
| - | - | - |
|1|NSIS|Nuovo Sistema Informativo Sanitario|
|2|SDK|Software Development Kit|
|3|AVN|Anagrafe Nazionale Vaccini|
|4|SSN|Sistema Sanitario Nazionale|
|5|CI|Codice Identificativo|
|6|AIC|Autorizzazione alla Immissione in Commercio del vaccino in Italia rilasciata dall’Agenzia Italiana del Farmaco|


# **2. Architettura SDK**

L'architettura degli SDK è disponibile al seguente link [`ARCHITECTURE.md`](https://github.com/ministero-salute/sdk-utilities-regole-properties/blob/main/ARCHITECTURE.md).

# **3 Funzionamento della soluzione SDK**

In questa sezione è descritta le specifica di funzionamento del flusso **VNX**  per l’alimentazione dello stesso


## ***3.1 Input SDK***

In fase di caricamento del file verrano impostati i seguenti parametri che andranno in input al SDK in fase di processamento del file:


|**NOME PARAMETRO**|**DESCRIZIONE**|**LUNGHEZZA**|**DOMINIO VALORI**|
| :- | :- | :- | :- |
|ID CLIENT|Identificativo univoco della trasazione che fa richiesta all'SDK|100|Non definito|
|NOME FILE INPUT|Nome del file per il quale si richiede il processamento lato SDK|256|Non definito|
|ANNO RIFERIMENTO|Stringa numerica rappresentante l’anno di riferimento per cui si intende inviare la fornitura|4|Anno (Es. 2022)|
|TIPO TRASMISSIONE |Indica se la trasmissione dei dati verso MDS avverrà in modalità full (F) o record per record (R). Per questo flusso la valorizzazione del parametro sarà impostata di default a F|1|F/R|
|FINALITA' ELABORAZIONE|Indica se i flussi in output prodotti dal SDK verranno inviati verso MDS (Produzione) oppure se rimarranno all’interno del SDK e il processamento vale solo come test del flusso (Test)|1|Produzione/Test|
|CODICE REGIONE|<p>Individua la Regione a cui afferisce la struttura. Il codice da utilizzare è quello a tre caratteri definito con DM 17 settembre 1986, pubblicato nella Gazzetta Ufficiale n.240 del 15 ottobre 1986, e successive modifiche, utilizzato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali.</p><p></p>|3|Es. 010|

Inoltre è previsto anche il parametro Periodo Riferimento, i cui valori differiscono in base al flusso specifico.
Di seguito la tabella specifica per il flusso VNX:


|**NOME PARAMETRO**|**DESCRIZIONE**|**LUNGHEZZA**|**DOMINIO VALORI**|
| - | :- | :- | :- |
|PERIODO RIFERIMENTO|Stringa alfanumerica rappresentante il periodo per il quale si intende inviare la fornitura|2|Q1 ,Q2, Q3, Q4|

## ***3.2 Tracciato input a SDK***

Il flusso di input avrà formato **csv** posizionale e una naming convention libera a discrezione dell’utente che carica il flusso senza alcun vincolo di nomenclatura specifica (es: nome\_file.csv). Il separatore per il file csv sarà la combinazione di caratteri tra doppi apici: “~“

All’interno della specifica del tracciato sono indicati i dettagli dei campi di business del tracciato di input atteso da SDK, il quale differisce per i sei  flussi dell’area AVN. All’interno di tale file è presente la colonna **Posizione nel file** la quale rappresenta l’ordinamento delle colonne del tracciato di input da caricare all’SDK.


Di seguito la tabella in cui è riportata la specifica del tracciato di input per il flusso in oggetto:


|**Posizione nel File Input**|**Nome campo**|**Key**|**Descrizione**|**Tipo** |**Obbligatorietà**|**Informazioni di Dominio**|**Lunghezza campo**|**XPATH Tracciato Output**|
| :- | :- | :- | :-: | :-: | :-: | :-: | :-: | :-: |
|0|Codice Identificatico dell’Assistito|KEY|Codice identificativo dell’assistito|AN|OBB|Il campo deve avere lunghezza massima di 20 caratteri in input alla procedura di cifratura che produrrà un output di massimo 172 caratteri.<br>Le modalità di alimentazione del presente campo sono descritte nel paragarfo 3.6 Codice Identificativo dell’ Assistito – procedura di cifratura|172|<br>**/vaccinazioniNonEffettuate/Assistito/@IdAssistito**|
|1|Modalita| |Campo tecnico utilizzato per distinguere le modalità di invio di: schede vaccinali di soggetti residenti in regione |A|OBB|Devono essere utilizzati i codici 'RE  per la trasmissione delle informazioni anagarfiche relative alla scheda vaccinale dei **soggetti residenti** nella regione che sta trasmettendo.|2|**/vaccinazioniNonEffettuate/@Modalita**|
|2|Tipo Trasmisione| |Campo tecnico utilizzato per distinguere trasmissioni di informazioni nuove, modificate o eventualmente annullate|A|OBB|Valori ammessi: <br>I: Inserimento (per la trasmissione di informazioni nuove o per la ritrasmissione di informazioni precedentemente scartate dal sistema di acquisizione);<br>V: Variazione (per la trasmissione di informazioni per le quali si intende far effettuare una sovrascrittura dal sistema di acquisizione);<br>C: Cancellazione (per la trasmissione di informazioni per le quali si intende far effettuare una cancellazione dal sistema di acquisizione).|1|**/vaccinazioniNonEffettuate/Assistito/MancataVaccinazione/@TipoTrasmissione**|
|3|Codice Regione|KEY|Individua la Regione che trasmette il dato. |AN|OBB|Il codice da utilizzare è quello a tre caratteri definito con DM 17 settembre 1986, pubblicato nella Gazzetta Ufficiale n.240 del 15 ottobre 1986, e successive modifiche, utilizzato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali.I valori ammessi sono quelli riportati all’**Allegato 1 – Regioni**|3|**/vaccinazioniNonEffettuate/@CodiceRegione** |
|4|Antigene |KEY|Indica il singolo antigene/principio vaccinale che costituisce il vaccino somministrato|AN|OBB|Valorizzare sulla base degli elenchi dei vaccini autorizzati resi disponibili da parte dell’AIFA. <br>In caso di vaccini combinati indicare i singoli antigeni che compongono il vaccino.<br>Ad esempio in caso di somministrazione di vaccino trivalente MPR (Morbillo/Parotite/Rosolia) dovranno essere inseriti tre record.<br>I valori ammessi sono quelli riportati all’Allegato 5 – Antigeni/Principi Vaccinali|2|**/vaccinazioniNonEffettuate/Assistito/MancataVaccinazione/@CodAntigene**|
|5|Dose|KEY|Indica il numero di dose somministrata rispetto al calendario vaccinale per il singolo antigene/principio vaccinale|N|OBB|I valori ammessi sono da 1 a 99|Max 2|**/vaccinazioniNonEffettuate/Assistito/MancataVaccinazione/@Dose**|
|6|Motivazione| |Indica la motivazione per cui la vaccinazione non è stata effettuata|AN|OBB|I valori ammessi sono quelli riportati all’Allegato 6 – Motivi esclusione|2|**/vaccinazioniNonEffettuate/Assistito/MancataVaccinazione/@Motivazione**|
|7|Data non effettuazione| |Data in cui la vaccinazione prevista non è stata effettuata|D|OBB|Formato: AAAA-MM-GG|10|**/vaccinazioniNonEffettuate/Assistito/MancataVaccinazione/@DataNonEffettuazione**|


## ***3.3 Controlli di validazione del dato (business rules)***

Di seguito sono indicati i controlli da configurare sulla componente di Validation Engine e rispettivi error code associati riscontrabili sui dati di input per il flusso VNX.

Gli errori sono solo di tipo scarti (mancato invio del record).

Al verificarsi anche di un solo errore di scarto, tra quelli descritti, il record oggetto di controllo sarà inserito tra i record scartati.

Business Rule non implementabili lato SDK:

- Storiche (Business Rule che effettuano controlli su dati già acquisiti/consolidati che non facciano parte del dato anagrafico)
- Transazionali (Business Rule che effettuano controlli su record, i quali rappresentano transazioni, su cui andrebbe garantito l’ACID (Atomicità-Consistenza-Isolamento-Durabilità))
- Controllo d’integrità (cross flusso) (Business Rule che effettuano controlli sui record utilizzando informazioni estratte da record di altri flussi)


Di seguito le BR per il flusso in oggetto:


|**CAMPO**|**FLUSSO**|**CODICE ERRORE**|**FLAG ATTIVAZIONE**|**DESCRIZIONE ERRORE**|**DESCRIZIONE ALGORITMO** |**TABELLA ANAGRAFICA**|**CAMPI DI COERENZA**|**SCARTI/ANOMALIE**|**TIPOLOGIA BR**|** |
| :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: |
|Tipo Prestazione|VNX|1901.2|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |scarti|Basic| |
|Tipo Prestazione|VNX|1902.2|ATTIVA|Non appartenenza al dominio di riferimento|Valori diversi da quelli ammessi : I,i,V,v,C,c,NM| | |scarti|Basic| |
|Codice Struttura|VNX|3005|ATTIVA|Mancata valorizzazione del codice struttura|Il valore è nullo e il campo Tipologia erogatore è diverso da “6” e da “99”’| | |scarti|Basic| |
|Codice Struttura|VNX|3015|ATTIVA|Codice struttura non presente nell’anagrafica STS11|Se il tipo erogatore è valorizzato con **1** il codice non è presente nei modelli STS11** (di cui al decreto ministeriale 5 dicembre 2006) a 6 cifre|Anagrafiche: STS11| |scarti|Anagrafica| |
|Codice Struttura|VNX|3020|ATTIVA|Codice struttura non presente nell’anagrafica ASL – MRA Fase 1|Se il tipo erogatore è valorizzato con **2 o 3 o 4 o 5 o 7 o 10 o 11 o 12** il codice non è presente nell’anagrafe di riferimento (D.M. 05/12/2006 e successive modifiche - Anagrafica MRA fase 1) – 6 cifre|Anagrafiche: ASL| |scarti|Anagrafica| |
|Codice Regione |VNX|1921|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |scarti|Basic| |
|Codice Regione |VNX|1922|ATTIVA|Non appartenenza al dominio di riferimento|Il valore inserito e controllato non è presente in anagrafica regioni|Anagrafiche: REGIONE| |scarti|Anagrafica| |
|Codice Regione |VNX|1905|ATTIVA|Il codice regione non coincide con la regione inviante.|Il campo Codice Regione non coincide con la regione che sta trasmettendo il file. | | |scarti|Basic| |
|Codice Identificatico dell’Assistito|VNX|1701|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |scarti|Basic| |
|Codice Identificatico dell’Assistito|VNX|1703|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 172 caratteri| | |scarti|Basic| |
|Modalità|VNX|1801|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |scarti|Basic| |
|Modalità|VNX|1802|ATTIVA|Non appartenenza al dominio di riferimento|Valori diversi da quelli ammessi : RE| | |scarti|Basic| |
|Modalità|VNX|1803|ATTIVA|Modalità non coerente con il flusso inviato|In caso di flusso Residenti va indicata la modalità RE| | |scarti|Basic| |
|Antigene|VNX|6141|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |scarti|Basic| |
|Antigene|VNX|6140|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri| | |scarti|Basic| |
|Antigene|VNX|4100|ATTIVA|Antigene non valorizzato in modo corretto |Il valore è composto da caratteri diversi da numeri| | |scarti|Basic| |
|Dose|VNX|6051|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |scarti|Basic| |
|Dose|VNX|6050|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza al massimo 2 caratteri| | |scarti|Basic| |
|Motivazione|VNX|2703|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri| | |scarti|Basic| |
|Motivazione|VNX|6141|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |scarti|Basic| |
|Motivazione|VNX|5000 (Ex. 6142)|ATTIVA|Motivazione non presente nelle anagrafiche di riferimento |Se il codice con è presente nell’anagrafe di riferimento – Allegato 6 delle presenti specifiche|Motivi esclusione| |scarti|Anagrafica| |
|Data non effettuazione|VNX|8012|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |scarti|Basic| |
|Data non effettuazione|VNX|8013|ATTIVA|Tipo dato errato o formato data errato|Il campo deve essere valorizzato con il formato data AAAA-MM-GG| | |scarti|Basic| |



## ***3.4 Accesso alle anagrafiche***

I controlli applicativi saranno implementati a partire dall’acquisizione dei seguenti dati anagrafici disponibili in ambito MdS e recuperati con servizi ad hoc (Service Layer mediante PDI):

- REGIONE
- ASL
- tabella di raccordo regioni-province-comuni-asl
- Codifica Alpha2
- HPS11
- STS11
- MRA
- RIA11
- Condizioni sanitarie a rischio
- Categorie a rischio
- AIFA
- ELENCO MdS
- Tipologie Formulazione
- Antigeni
- Principi Vaccinali
- Comuni Istat


Il dato anagrafico sarà presente sottoforma di tabella composta da tre colonne:

- Valore (in cui è riportato il dato, nel caso di più valori, sarà usato il carattere # come separatore)


- Data inizio validità (rappresenta la data di inizio validità del campo Valore)
 - Formato: AAAA-MM-DD
 - Notazione inizio validità permanente: **1900-01-01**


- Data Fine Validità (rappresenta la data di fine validità del campo Valore)
  - Formato: AAAA-MM-DD
  - Notazione fine validità permanente: **9999-12-31**

Affinchè le Business Rule che usano il dato anagrafico per effettuare controlli siano correttamente funzionanti, occorre controllare che la data di competenza del record su cui si effettua il controllo (la quale varia in base al flusso), sia compresa tra le data di validità.  Tutte le tabelle anagrafiche hanno dei dati con validità permanente ad eccezione delle seguenti per le quali sono previste date di validità specifiche:

- Tabella di raccordo regioni-province-comuni-asl
- Comuni Istat
- Codifica Alpha2
- Anagrafiche: ASL
- Anagrafiche: REGIONE

Di seguito viene mostrato un caso limite di anagrafica in cui sono presenti delle sovrapposizioni temporali e contraddizioni di validità permanente/specifico range:


|ID|VALUE|VALID\_FROM|VALID\_TO|
| - | - | - | - |
|1|VALORE 1|1900-01-01|9999-12-31|
|2|VALORE 1|2015-01-01|2015-12-31|
|3|VALORE 1|2018-01-01|2023-12-31|
|4|VALORE 1|2022-01-01|2024-12-31|


Diremo che il dato presente sul tracciato di input è valido se e solo se:

∃ VALUE\_R = VALUE\_A “tale che” VALID\_FROM<= **DATA\_COMPETENZA** <= VALID\_TO

(Esiste almeno un valore compreso tra le date di validità)

Dove:

- VALUE\_R rappresenta i campi del tracciato di input coinvolti nei controlli della specifica BR

- VALUE\_A rappresenta i campi dell’anagrafica coinvolti nei controlli della specifica BR

- VALID\_FROM/VALID\_TO rappresentano le colonne dell’anagrafica

- DATA\_COMPETENZA data da utilizzare per il filtraggio del dato anagrafico specifica per flusso.


La DATA\_COMPETENZA da utilizzare per filtrare il dato anagrafico per il flusso VNX sulle tabelle con date di validità specifiche riportate in precedenza è l’anno della data non effettuazione del record.
## Istruzioni per l'installazione

Per l'installazione e l'avvio dell'engine seguire la documentazione tecnica dettagliata disponibile all'url [`INSTALL.md`](https://github.com/ministero-salute/sdk-utilities-regole-properties/blob/main/INSTALL.md).

## 📝 Licenza
Questo progetto è rilasciato sotto licenza BSD 3-Clause License così come definita [BSD 3-Clause License](./LICENSE).

## 🤝 Contributi
I contributi sono benvenuti. Si prega di consultare il file [`CONTRIBUTING.md`](CONTRIBUTING.md) per le linee guida su come contribuire al progetto.

## 📞 Contatti
Per ulteriori informazioni, contattare:

- **Service Desk - Ministero della Salute**: servicedesk.mds@medilifegroupspa.com
- **Amministrazione titolare**: [Ministero della Salute](https://www.salute.gov.it)

## mantainer:
 Accenture SpA until January 2026