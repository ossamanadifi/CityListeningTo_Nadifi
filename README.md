# Your City Is Listening To

Autore: Ossama Nadifi  
Matricola: 1021258  
Email: ossama.nadifi@studio.unibo.it  
Università: Alma Mater Studiorum – Università di Bologna  
Corso: Laboratorio di Applicazioni Mobili  
Anno accademico: 2023/2024  

## Introduzione

Your City Is Listening To è un'applicazione Android che permette agli utenti di registrare note audio geolocalizzate, caricarle su un server e visualizzare le registrazioni condivise da altri utenti attraverso una mappa interattiva basata su OpenStreetMap.

L'obiettivo del progetto è esplorare i suoni della città e creare una raccolta di contenuti audio associati a specifici luoghi geografici.

## Funzionalità
### Authentication  
- Registrazione di un nuovo account e login.  
- Autenticazione tramite token Bearer restituito dal server.  
- Gestione del profilo utente.  
- Possibilità di eliminare il proprio account e i dati associati sul server.  
- Logout e gestione della sessione.  
### Record
- Registrazione di note audio direttamente dall'applicazione.  
- Possibilità di interrompere e riascoltare la registrazione.  
- Possibilità di ripetere la registrazione prima del caricamento.  
- Associazione delle coordinate geografiche alla traccia audio.  
### Import
- Caricamento delle registrazioni sul backend tramite Retrofit.  
- Salvataggio locale dei metadati restituiti dal server.  
- Gestione delle risposte del backend al termine dell'upload.  
### Import Later
- Possibilità di posticipare il caricamento delle registrazioni quando il dispositivo utilizza la connessione dati.  
- Monitoraggio dei cambiamenti della connettività.  
- Sincronizzazione automatica delle tracce in sospeso quando è disponibile una connessione Wi-Fi.  
- Verifica della validità del token prima di effettuare l'upload.  
- Notifica all'utente al termine della sincronizzazione.  
### Pending Upload
- Visualizzazione delle registrazioni in attesa di caricamento.
- Possibilità di riprodurre le tracce audio.
- Caricamento manuale delle registrazioni rimaste in sospeso.
- Possibilità di eliminare le tracce non più necessarie.
### My Songs
- Riprodurre le tracce audio disponibili localmente.
- Nascondere o rendere nuovamente visibili le registrazioni agli altri utenti.
- Eliminare le tracce dal server.
- Visualizzare le informazioni restituite dal backend durante l'upload.
### Map
- Visualizzazione delle registrazioni geolocalizzate su una mappa interattiva.
- Utilizzo di marker per individuare i luoghi delle registrazioni.
- Consultazione dei metadati associati alle tracce.
- Visualizzazione delle informazioni relative all'utente che ha caricato la registrazione.
### Account
- Visualizzazione dello username e dell'identificativo associato all'utente.
- Possibilità di eliminare il proprio account e le registrazioni associate.
- Gestione dei dati locali per mantenere la consistenza tra dispositivo e server.
### Offline
- Consultazione delle registrazioni salvate nel database locale.
- Accesso alle informazioni disponibili anche in assenza di connessione Internet.
- Riproduzione delle tracce audio presenti sul dispositivo.

## Architettura

L'applicazione segue il pattern architetturale MVVM (Model–View–ViewModel), che permette di separare l'interfaccia utente dalla logica applicativa e dalla gestione dei dati.

L'architettura è suddivisa in tre componenti principali:

- Model: gestisce i dati, la persistenza locale e la comunicazione con il backend.
- View: contiene le schermate e i componenti dell'interfaccia utente.
- ViewModel: gestisce lo stato dell'interfaccia, espone i dati alla View e coordina le interazioni con il Model tramite LiveData.

Questa separazione favorisce la manutenibilità del codice e riduce l'accoppiamento tra interfaccia grafica e logica applicativa.

## Tecnologie utilizzate
- Kotlin:	Linguaggio di programmazione principale
- Android SDK:	Sviluppo dell'applicazione Android
- MVVM:	Pattern architetturale
- LiveData:	Osservazione reattiva dei dati
- Retrofit:	Comunicazione HTTP con il backend
- OkHttp:	Gestione delle richieste HTTP e degli interceptor
- Room:	Persistenza dei dati nel database locale
- osmdroid:	Integrazione delle mappe OpenStreetMap
- RecyclerView:	Visualizzazione delle liste di registrazioni
- SharedPreferences:	Memorizzazione delle preferenze e dei dati di sessione necessari alla sincronizzazione
- BroadcastReceiver:	Rilevamento degli eventi di connettività
- Worker:	Esecuzione delle operazioni di sincronizzazione in background
## Gestione dei dati

L'applicazione utilizza sia un backend remoto sia un database locale per gestire le registrazioni audio.

- Backend remoto: gestisce l'autenticazione, le registrazioni caricate e le operazioni sulle tracce.
- Database locale Room: conserva le informazioni necessarie alla consultazione delle registrazioni sul dispositivo.
- Registrazioni in sospeso: consentono di conservare temporaneamente le tracce che non possono essere caricate immediatamente.

La sincronizzazione permette di completare successivamente gli upload, compatibilmente con la disponibilità della rete e la validità del token di autenticazione.
