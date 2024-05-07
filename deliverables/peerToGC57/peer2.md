# Valutazione della parte di network del gruppo GC57 da parte del gruppo GC10.

## Lati positivi

- chiarezza nell'implementazione
- struttura scomposta del controller con relativa interfaccia che permette di mantenere coerenza e semplicità di implementazione

## Lati negativi

- Non capiamo come viene gestita l'inizializzazione del server, in quanto sembra diviso in rmiServer e socketServer.
- Ci sembra scorretto (a meno di un obiettivo specifico) porre tutti gli attributi delle classi Message a public, dovrebbero essere private.

### Lati che non ho capito
- VirtualView viene implementata solo da SocketClientHandler e RmiClient (che però hanno metodi in comune che non sono nell'interfaccia) e SocketClient non implementan VirtualView
- ServerSocket e ServerRmi sembrano completamente diversi(ServerRmi non ha neanche controller e main)
- Ci sembra superfluo un Message di tipo EndMessage, in quanto un turno termina necessariamente con il pescaggio di una carta


## Confronto tra le architetture

- interessante l'utilizzo di classi specifiche per ogni messaggio. Nella nostra implementazione ciò non avviene. In generale ci sembra un approccio valido e semplice da implementare ma al contempo molto specifico.

;