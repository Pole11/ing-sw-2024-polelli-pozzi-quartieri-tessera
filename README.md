# Codex Naturalis
Polelli, Pozzi, Quartieri, Tessera

## Implementation
- TUI + GUI
- RMI + Socket
- Complete rules
- Additional features:
  - [x] chat
  - [x] resilience to disconnections
  - [x] persistence


## How to play
First, download the `.jar` files from the `deliverables` folder, then follow the instructions for each component.

## Server
Once you've downloaded the server `.jar`, start it with the following command:

```bash
java -jar server.jar [server IP] [socket port] [RMI port] [restart-rescue]
```

To improve server awareness when deployed on an internet-connected server, the "am I down right now" feature is enabled, which checks internet connectivity by pinging 8.8.8.8.

Each match generates a rescue.json file whenever an event is sent to the views, which contains the game's state. If the server is moved to another machine or shut down, the match can be resumed from where it left off.

By default, `restart-rescue` is set to false. You can set it to true to clear the rescue file and start a new game. In any case, when a game ends, the rescue file is automatically cleared.

### Client
Once you've downloaded the client `.jar`, start it with the following command:

```bash
java -jar client.jar [rmi/socket] [server IP] [port]
```
Note that `port` refers to the corresponding port on the server, so it changes depending on whether you're using RMI or Socket.

This is the "base" configuration, suitable for any context, whether you're setting up a LAN or deploying the server on a public static IP.

In the case of RMI, if issues arise, the client may need to specify its own IP address. In this case, use the following command:

```bash
java -jar client.jar [rmi/socket] [server IP] [port] [current machine IP]
```

At the start, the user will be prompted to choose whether to use the GUI or not.
To send a chat message to a specific user, prefix the message with the recipient's name as the first word.

### GUI
The GUI is designed to be responsive and works on both low and high resolutions. This responsiveness is implemented based on the window's height and the screen resolution. The optimal layout is the square window that opens when the game starts, but the user can resize the window at any time.

### CLI
The CLI is inspired by a terminal shell, where users type commands and press enter to execute them. Instructions for navigating within the game are provided automatically. If you ever feel lost, you can always refer to the help command. If you're unsure how to use a command, you can get more information by using the -h flag after the command, like so: COMMAND -h.

### Bot
You can also find the `.jar` for playing against a bot, moreover in src/test/bots there is the file configuration

---

NOTE: Codex Naturalis is a board game developed and published by Cranio Creations Srl. The graphic content of this project related to the board game is used with the prior approval of Cranio Creations Srl solely for educational purposes. Distribution, copying, or reproduction of the content and images in any form outside the project is prohibited, as is the redistribution and publication of the content and images for purposes other than those mentioned above. The commercial use of the aforementioned content is also prohibited.
