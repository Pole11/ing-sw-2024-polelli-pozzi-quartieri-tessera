---
title: "GC10 UML"
sub-title: "Peer Review Document"
toc: true
toc-title: Index
author:
- Riccardo Polelli
- Filippo Pozzi
- Federico Quartieri
- Giacomo Tessera
number-sections: true
geometry:
- top=20mm
- left=20mm
---

# Model 

## Card

In this section we will shortly explain how we structured the classes regarding the various types of cards.

```mermaid
classDiagram
  class Card {
    <<abstract>>
    - id : int
  }

  class ObjectiveCard {
    - challenge: Challenge
    - points: int
  }
  Card <|-- ObjectiveCard

  class CornerCard {
    <<asbstract>>
    - frontCorners[] : Corner[N_CORNERS]
    - backCorners[]: Corner[N_CORNERS]
    + getCorners()  Corner[]
    + getLinkedCards()  Integer[]
    + getUncoveredCorners(isFront : int)  Corner[N_CORNERS]
    + ~abstract~getUncoveredElements(isFront : int)  Element[MAX_UNCOVERED_RESOURCES]
  }
  Card <|-- CornerCard

  class Corner {
    - covered : Boolean
    - ~final~element : Element
    - ~final~cardId : int
    - linkedCorner : Corner

  }
  CornerCard "6..8" *-- "1" Corner

  class ResourceCard {
    - ~final~resourceType : Element
    - ~final~points : int
    + getUncoveredElement(isFront : int)  Element[]
  }
  CornerCard <|-- ResourceCard

  class GoldCard {
    - ~final~resourceType : Element
    - ~final~challenge : Challenge
    - ~final~resourceNeeded: Element[MAX_RES_NEEDED]
    - ~final~points: int
    + getUncoveredElements(isFront : int)  Element[]
  }
  CornerCard <|-- GoldCard

  class StarterCard {
    - ~final~centerResource : Element[]
    + getUncoveredElement(isFront : int)  Element[]
  }
  CornerCard <|-- StarterCard

  class Challenge {
    <<abstract>>
  }
  ObjectiveCard "1" <-- "0..1" Challenge
  GoldCard "0..1" <-- "0..12" Challenge

  class StructureChallenge {
    - configuration : Element
  }
  Challenge <|-- StructureChallenge

  class ElementChallenge {
    - elements: Element[MAX_CHAL_RESUORCE]
  }
  Challenge <|-- ElementChallenge

  class CoverageChallenge {

  }
  Challenge <|-- CoverageChallenge
```

Here is a table that shows what all the types of cards have in common, so that it is easier to aggregate them into classes and subclasses.

| Card      | Has Corner (4) | Has Resource Corner (max 4) | Has Item Corner (max 1) | Has Points | Has Challenge | Has Resource Needed (max 5) | Has Back Resource |
|-----------|----------------|-----------------------------|-------------------------|------------|---------------|-----------------------------|-------------------| 
| Resource  | x              | x                           | x                       | x          |               |                             | x                 | 
| Gold      | x              |                             | x                       | x          | x             | x                           | x                 |
| Starter   | x              | x                           |                         |            |               |                             | x (max 3)         |
| Objective |                |                             |                         | x          | x             |                             |                   |  

In the cards package (cards, corner, challenges) everything is final except covered and linkedCorner in Corner

### CornerCard

Note that corner is `null` in the array if the corner is hidden.
The corner that is visible but with no element, is not null in the array and has `EMPTY` in element

- `covered` true if there is another card on top
- `element` is the element, can be the Resource or the Items in the corner. If it is empty the value is `Element.EMPTY`
- `cardId` is the id of the card (card is the son of `CornerCard`)
- `linkedCorner` if there is a card connected to it, it is a reference to that card, `null` if the corner is not connected to any card
- `getUncoveredCorners` returns all the free corners (back and front)
- `getUncoveredElement` returns all the elements (`Resource` and `Item`)

#### GoldCard

If the `challenge` attribute is `null` the points are gained automatically.
The `ResourceType` is the type of resource and it is also used to identify the color.

#### ResourceCard

We will later add methods that will return the number of a specific resource/item on the corners of the card, they will all use `getUncoveredElement`. 

#### StarterCard

In the front there are always 4 corners with all the elements, in the back the corners can be hidden or empty and there are `backResources`.

### ObjectiveCard

Note that the `Challenge` is the one you do in order to gain points and `Objective` is the type of card.

### Challenge

This is the one you must do in order to gain points. It is used in `ObjectiveCard` and `GoldCard`. More details later.

#### Structure Challenge
The structure challenge is only for objective cards.

![structure](./img/structure.png){ width=400px }

#### Resource Challenge
The resource challenge is only for objective cards. The type of resources needed in the objective cards can vary.

In the objective cards is like this:

![element-obj](./img/element-obj.png){ width=400px }

#### Item Challenge
The item challenge is only for gold cards.

In the gold cards is like this (the one on the top of the card):

![element-gold](img/element-gold.png){ width=400px }

#### Coverage Challenge
This challenge is only for gold cards.
Is the one on the top of the card.

![coverage](img/coverage.png){ width=400px }  

Here is the UML for both the Card and the Challenge:

`StructureChallenge` and `ElementChallenge` are used in `Objective`.  
`GoldCoverageChallenge` and `ElementChallenge` are used in `GoldCard`.

Note that challenge can be null. `challenge` in the `GoldCard` is the challenge to do in order to do point. 
Also note that for the `GoldCoverageChallenge` the value of `points` is always 2.

The `configuration` is a 3x3 matrix of elements (resources actually), the element refers to the color of the card and the position is determined by the position in the matrix

About element:

- In `goldCards`, `Element.size() == 1`  
- In `obectiveCards`: 
    - example of 3 animal: `element=[animal, animal, animal]`
    - example of 1 Quill and  2 Inkwell: `element = [Quill, Inkwell, Inkwell]` 

Order from top-left to bottom-left \[0-3\]

```
01  
32
```

The arrays have a null value if the corner do not exist.

## GameState 

The first player in the data structure is the black one, `public Player getBlackPlayer() { return player[0] }`. 
Note that a **round** is made up by 4 **turns**.

The players order in game is defined by the order of the players in the array `players[]`.


```mermaid
classDiagram
  class GameState {
    - ~final~ cardsMap : HashMap< Integer,Card >
    - ~final~ mainBoard : Board
    - ~final~ players[] : Player[MAX_PLAYERS]
    - ~final~ chat : Chat
    - currentPlayerIndex : int 
    - currentGamePhase : GamePhase
    - currentGameTurn : GameTurn
    + getMainBoard() Board
    + getPlayers() Player[] : void
    + getCurrentPlayer() Player
    + getBlackPlayer() Player
    + getCurrentGameTurn() GameTurn
    + getCurrentGamePhase() GamePhase
    + getCard(id : int) Card
  }
  
  class Chat {
      - messages : ArrayList<Message>
      - lastId: int
    + addMessage(player : Player, content : String) void
  }

  GameState <-- Chat

  class Message {
    - id : int
    - author : Player
    - dateTime : LocalDateTime
    - content : String
  }

  Chat "0.." *-- "1" Message

  class Player {
    - nickname : String
    - color : Color
    - points : int
    - elements : HashMap< Element, Integer >
    - objectiveCard : ObjectiveCard
    - starterCard : StarterCard
    - board : HashMap< Integer, bool >
    - hand : HashMap< Integer, bool >
    + flipCards(id : Integer) void 
  }
   GameState "1" <-- "2..4" Player

  class Board {
    - sharedGoldCards[] : goldCard[N_SHARED_GOLDS]
    - sharedResourceCard[] : ResourceCard[N_SHARED_RESOURCES]
    - sharedObjectiveCards[] : OjectiveCard[N_SHARED_OBJECTIVES]
    - goldDeck : ArrayList< GoldCard >
    - resourceDeck : ArrayList< ResourceCard >
    + getSharedGoldCard(pos : int) GoldCard
    + getSharedResourceCard(pos : int) ResourceCard
    + getFromResourceDeck() ResourceCard
    + getFromGoldDeck() GoldCard
    + fillSharedCardsGap() void
  }  
  GameState <-- Board
```

In the methods of Board class, `pos` means the position of the card (commonly 1 if it's the first, 2 if it's the second one)

## Enumerations

We will use some enumerations to define our data types. 
We cannot show all the association with the other classes because graphically it would be confused. Just remind that these object are actually associated with the classes that make use of them.

```mermaid
classDiagram

class ElementTypeInterface {
      <<interface>>
      + getDisplayableType() String
 }
 
 class ElementType {
   <<Enumeration>>
   - Resource = "Resource"
   - Item = "Item"
   - Empty = "Empty"
   - ~final~ type : String
   - ElementType(~final~ type : String)
   + getDisplayableType() String
 }
 
 class Element {
   <<Enumeration>>
   - Quill(ElementType.Item)
   - Manuscript(ElementType.Item)
   - Inkwell(ElementType.Item)
   - Fungi(ElementType.Resource)
   - Animal(ElementType.Resource)
   - Insect(ElementType.Resource)
   - Plant(ElementType.Resource)
   - Empty(ElementType.Empty)
   - Element(type : ElementType)
   + getDisplayableType() String
 }

  class Color {
    <<Enumeration>>
    - Black
    - Green
    - Yellow
    - Red
  }

  class GamePhase {
    <<Enumeration>>
    - MainPhase
    - EndPhase
  }

  class TurnPhase {
    <<Enumeration>>
    - PlacingPhase
    - DrawPhase
  }
  ElementType <-- Element
  ElementTypeInterface <.. ElementType
  ElementTypeInterface <.. Element
```

# Controller

We started implementing the Controller while we are still studying how to implement the View, so it is still *wip*.

- `drawCard` takes a card from the deck of cards, you can understand from which deck to draw based on the class of the `card` 
- In `placeCard` you can retrieve the information of a multiple corner placed card by checking in the method, you just need to place it on one single card, even if it will affect another card.
- `getGameState` is the method to get all the method game state information, we can later decide to subdivide it into multiple methods
- `fliCard` is used to flip the card the player has in hand, then in `placeCard` it will be placed using the side that this method defines. It is implemented changing the value in the hash map

```mermaid
classDiagram

  class Controller {
    + drawCard(player: Player, card: Card)  void
    + placeCard(player: Player, placingCardId: int, tableCardId: int, int: configuration)
    + flipCard(player: Player, cardId: int) void

    - getGameState()
  }
```

# Exceptions

- `InvalidHandException` (more than 3 cards in the hand)
- `NotUniquePlayerColorException`
- `NotUniquePlayerException`
- `NotUniquePlayerNicknameException`
- `WrongStructureConfigurationSizeException` (structure is the one in challenge)

# View

*wip*

# Complete UML
```mermaid
classDiagram
  class Card {
    <<abstract>>
    - id : int
  }

  class ObjectiveCard {
    - challenge: Challenge
    - points: int
  }
  Card <|-- ObjectiveCard

  class CornerCard {
    <<asbstract>>
    - frontCorners[] : Corner[N_CORNERS]
    - backCorners[]: Corner[N_CORNERS]
    + getCorners()  Corner[]
    + getLinkedCards()  Integer[]
    + getUncoveredCorners(isFront : int)  Corner[N_CORNERS]
    + ~abstract~getUncoveredElements(isFront : int)  Element[MAX_UNCOVERED_RESOURCES]
  }
  Card <|-- CornerCard

  class Corner {
    - covered : Boolean
    - ~final~element : Element
    - ~final~cardId : int
    - linkedCorner : Corner

  }
  CornerCard "6..8" *-- "1" Corner

  class ResourceCard {
    - ~final~resourceType : Element
    - ~final~points : int
    + getUncoveredElement(isFront : int)  Element[]
  }
  CornerCard <|-- ResourceCard

  class GoldCard {
    - ~final~resourceType : Element
    - ~final~challenge : Challenge
    - ~final~resourceNeeded: Element[MAX_RES_NEEDED]
    - ~final~points: int
    + getUncoveredElements(isFront : int)  Element[]
  }
  CornerCard <|-- GoldCard

  class StarterCard {
    - ~final~centerResource : Element[]
    + getUncoveredElement(isFront : int)  Element[]
  }
  CornerCard <|-- StarterCard

  class Challenge {
    <<abstract>>
  }
  ObjectiveCard "1" <-- "0..1" Challenge
  GoldCard "0..1" <-- "0..12" Challenge

  class StructureChallenge {
    - configuration : Element
  }
  Challenge <|-- StructureChallenge

  class ElementChallenge {
    - elements: Element[MAX_CHAL_RESUORCE]
  }
  Challenge <|-- ElementChallenge

  class CoverageChallenge {

  }
  Challenge <|-- CoverageChallenge


  class GameState {
    - ~final~ cardsMap : HashMap< Integer,Card >
    - ~final~ mainBoard : Board
    - ~final~ players[] : Player[MAX_PLAYERS]
    - ~final~ chat : Chat
    - currentPlayerIndex : int 
    - currentGamePhase : GamePhase
    - currentGameTurn : GameTurn
    + getMainBoard() Board
    + getPlayers() Player[] : void
    + getCurrentPlayer() Player
    + getBlackPlayer() Player
    + getCurrentGameTurn() GameTurn
    + getCurrentGamePhase() GamePhase
    + getCard(id : int) Card
  }
  
  class Chat {
      - messages : ArrayList<Message>
      - lastId: int
    + addMessage(player : Player, content : String) void
  }

  GameState <-- Chat

  class Message {
    - id : int
    - author : Player
    - dateTime : LocalDateTime
    - content : String
  }

  Chat "0.." *-- "1" Message

  class Player {
    - nickname : String
    - color : Color
    - points : int
    - elements : HashMap< Element, Integer >
    - objectiveCard : ObjectiveCard
    - starterCard : StarterCard
    - board : HashMap< Integer, bool >
    - hand : HashMap< Integer, bool >
    + flipCards(id : Integer) void 
  }
  GameState "1" <-- "2..4" Player

  class Board {
    - sharedGoldCards[] : goldCard[N_SHARED_GOLDS]
    - sharedResourceCard[] : ResourceCard[N_SHARED_RESOURCES]
    - sharedObjectiveCards[] : OjectiveCard[N_SHARED_OBJECTIVES]
    - goldDeck : ArrayList< GoldCard >
    - resourceDeck : ArrayList< ResourceCard >
    + getSharedGoldCard(pos : int) GoldCard
    + getSharedResourceCard(pos : int) ResourceCard
    + getFromResourceDeck() ResourceCard
    + getFromGoldDeck() GoldCard
    + fillSharedCardsGap() void
  }  
  GameState <-- Board


class ElementTypeInterface {
<<interface>>
+ getDisplayableType() String
}

class ElementType {
<<Enumeration>>
- Resource = "Resource"
- Item = "Item"
- Empty = "Empty"
- ~final~ type : String
- ElementType(~final~ type : String)
+ getDisplayableType() String
}

class Element {
<<Enumeration>>
- Quill(ElementType.Item)
- Manuscript(ElementType.Item)
- Inkwell(ElementType.Item)
- Fungi(ElementType.Resource)
- Animal(ElementType.Resource)
- Insect(ElementType.Resource)
- Plant(ElementType.Resource)
- Empty(ElementType.Empty)
- Element(type : ElementType)
+ getDisplayableType() String
}

class Color {
<<Enumeration>>
- Black
- Green
- Yellow
- Red
}

class GamePhase {
<<Enumeration>>
- MainPhase
- EndPhase
}

class TurnPhase {
<<Enumeration>>
- PlacingPhase
- DrawPhase
}
ElementType <-- Element
ElementTypeInterface <.. ElementType
ElementTypeInterface <.. Element

class Controller {
    + drawCard(player: Player, card: Card)  void
    + placeCard(player: Player, placingCardId: int, tableCardId: int, int: configuration)
    + flipCard(player: Player, cardId: int) void

    - getGameState()
  }
```