---
title: "UML Codex Naturalis GC10"
author: Riccardo Polelli, Filippo Pozzi, Federico Quartieri, Giacomo Tessera
date: "2024"
geometry: "left=1cm,right=1cm,top=2cm,bottom=2cm"
output: pdf_document
numbersections: true
toc: true
toc-title: "Indice"
header-includes:
    - \usepackage{multicol}
    - \newcommand{\hideFromPandoc}[1]{#1}
    - \hideFromPandoc{
        \let\Begin\begin
        \let\End\end}
---

\newpage 

# **M**odel 

## Card
In this section we will shortly explain how we structured the classes regarding the various types of cards.

Here is a table that shows what all the types of cards have in common, so that it is easier to aggregate them into classes and subclasses.

| Card | Has Corner (4) | Has Resource Corner (max 4) | Has Item Corner (max 1) | Has Points | Has Challenge | Has Resource Needed (max 5) | Has Back Resource |
| --- | --- | --- | --- | --- | --- | --- | --- | 
|Resource | x | x | x | x | | | x | 
|Gold | x | | x | x | x | x | x |
|Starter | x | x | | | | | x (max 3) |
|Objective | | | | x | x | | |  

The Cards are never discarded, they are always used for something. 

### Challenge

\Begin{multicols}{2}
#### Structure Challenge
The structure challenge is only for objective cards.

![structure](img/structure.png){ width=250px }  


#### Resource Challenge
The resource challenge is only for objective cards. The type of resources needed in the objective cards can vary.

In the objective cards is like this:

![element-obj](img/element-obj.png){ width=250px }  

#### Item Challenge
The item challenge is only for gold cards.

In the gold cards is like this (the one on the top of the card):

![element-gold](img/element-gold.png){ width=250px }    

#### Coverage Challenge
This challenge is only for gold cards.
Is the one on the top of the card.

![coverage](img/coverage.png){ width=250px }    

\End{multicols}

Here is the UML for both the Card and the Challenge:

![card](img/card.svg) 

`StructureChallenge` and `ElementChallenge` are used in `Objective`.  
`GoldCoverageChallenge` and `ElementChallenge` are used in `GoldCard`.

Note that challenge can be null. `challenge` in the `GoldCard` is the challenge to do in order to do point. 
Also note that for the `GoldCoverageChallenge` the value of `points` is always 2.

The `configuration` can have any type since the number of configuration is limited, `int` is probably the most practical.

About element:

- In `goldCards`, `Element.size() == 1`  
- In `obectiveCards`: 
    - example of 3 animal: `element=[animal, animal, animal]`
    - example of 1 Quill and  2 Inkwell: `element = [Quill, Inkwell, Inkwell]` 

Probably we should give an id to all of the cards so that we can render them with the right texture and we can track which one has been used or not.

`covered` is true only if is under another corner.

Order from top-left to bottom-left \[0-3\]

```
01  
32
```

The arrays have a null value if the corner does not exists.

## GameState 

The first player in the data structure is the black one, something like `public Player blackPlayer() { return player[0] }`. 
Note that a **round** is made up by 4 **turns**.

![gamestate](img/gamestate.svg){ width=600px }

\newpage

## Enumerations

We will use some enumerations to define our data types. 
We cannot show all the association with the other classes because graphically it would too confusing. Just remind that these object are actually associated with the classes that make use of them.

![enumerations](img/enumerations.svg)

# **C**ontroller

We started implementing the Controller while we are still studying how to implement the View, so it is still *wip*.

In `placeCard` you can retrieve the infomation of a multiple corner placed card by checking in the method, you just need to place it on one single card, even if it will affect another card.

![controller](img/controller.svg){ width=500px }

# **View**

*wip*

# Complete UML 

![all](img/all.svg)

