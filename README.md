# Codex Naturalis GC10

Polelli, Pozzi, Quartieri, Tessera

## Implementazione

- TUI + GUI
- RMI + Socket
- Regole complete
- Funzionalità aggiuntive:
    - [x] chat 
    - [x] resilienza alle disconnessioni
    - [x] persistenza

## Come giocare

Per prima cosa scaricare i `.jar` nella cartella `deliverables`, poi procedere nelle istruzioni di ogni componente.

### Server

Una volta scaricato il `.jar` del server, per farlo partire:

```bash
java -jar server.jar [ip del server] [porta rmi] [porta socket] [restart-rescue]
```

Visto che vogliamo migliorare la consapevolezza del server nel caso di un deploy su server connesso a internet, è abilitata la funzione di "am i down right now", che cerca di capire se è connesso a internet o meno, grazie ad un ping a `8.8.8.8`.

Ogni partita genera, ogni volta che viene inviato un evento alle view, un file, chiamato `rescue.json` che contiene lo stato della partita. Nel caso si sposti il server da un computer a un altro o si spenga il server, per riprendere la partita dove si era lasciata basta collocare il file `rescue.json` nella stessa cartella del `server.jar` (viene fatto in automatico). 

Di default restart-rescue è false, si può decidere, mettendolo a true,  di ripulire il rescue per iniziare una nuova partita, in ogni caso quando una partita termina, il rescue viene ripulito automaticamente

### Client

Una volta scaricato il `.jar` del client, per farlo partire:

```bash
java -jar client.jar [rmi/socket] [porta]
```

Da notare che con `porta` si intende la porta corrispondente del server, quindi cambia nel caso di RMI o Socket.

Questa è la configurazione "base", da usare in qualsiasi contesto. Sia creando una propria LAN, che deployando il server su un server con ip pubblico statico. 

Nel caso di RMI, se si riscontrano problemi, potrebbe essere necessario per il client specificare il proprio indirizzo ip. In tal caso basta procedere come segue:

```bash
java -jar client.jar [rmi/socket] [porta] [ip macchina corrente]
```

All'inizio, verrà chiesto all'utente di inserire la propria preferenza sull'utilizzo della GUI o meno.
Per inviare un messaggio in chat ad un utente specifico, mettere come "prefisso" del messaggio, cioè come prima parola, il nome dell'utente destinatario.

#### GUI

La GUI è stato progettata in modo responsive e funziona sia su risoluzioni basse che alte. Questa responsivness viene implementata facendo riferimento all'altezza della finestra e alla risoluzione dello schermo. La schermata ottimale è quella quadrata che si apre all'avvio del gioco, ma l'utente può decidere di cambiarne le dimensioni in ogni momento.  

#### CLI

Per la CLI abbiamo ripreso lo stile di una shell del terminale. Infatti richiederà all'utente di usare dei comandi testuali e di premere invio una volta completato il comando. Le istruzioni per muoversi all'interno del gioco vengono fornite automaticamente. Nel caso ci si senta persi, si può sempre fare riferimento al comando `help`. Se non si capisce come usare un comando, si possono ottenere più informazioni con l'immancabile flag `-h` preceduto dal comando, in questo modo: `COMANDO -h`.

### Bot

Nella fase di testing ci è sembrato necessario creare dei bot che potessero giocare da soli, questi hanno reso la fase di sviluppo molto più veloce.  
