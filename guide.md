# 🐢 Guide Complet — Projet Scratch JavaFX (de ZÉRO)

> **Objectif** : Reconstruire le projet Scratch pas à pas, fonction par fonction.
> Chaque étape explique **POURQUOI** on fait les choses, **COMMENT** ça marche, et **À QUOI** ça sert.

---

## 📋 Table des matières

1. [Comprendre l'architecture MVVM](#1--comprendre-larchitecture-mvvm)
2. [Comprendre les Bindings (LE NERF DU PROJET)](#2--comprendre-les-bindings-le-nerf-du-projet)
3. [ÉTAPE 1 — Le Modèle de base](#étape-1--le-modèle-de-base)
4. [ÉTAPE 2 — Les Actions simples](#étape-2--les-actions-simples)
5. [ÉTAPE 3 — Les Variables](#étape-3--les-variables)
6. [ÉTAPE 4 — Les Boucles](#étape-4--les-boucles)
7. [ÉTAPE 5 — ExecutionContext](#étape-5--executioncontext)
8. [ÉTAPE 6 — Program](#étape-6--program)
9. [ÉTAPE 7 — ProgramFileService](#étape-7--programfileservice)
10. [ÉTAPE 8 — Le ViewModel (ActionDetail, VariableRow)](#étape-8--le-viewmodel-petites-classes)
11. [ÉTAPE 9 — MainViewModel (LE GROS MORCEAU)](#étape-9--mainviewmodel-le-gros-morceau)
12. [ÉTAPE 10 — PaletteView](#étape-10--paletteview)
13. [ÉTAPE 11 — ProgramView](#étape-11--programview)
14. [ÉTAPE 12 — DetailPanelView](#étape-12--detailpanelview)
15. [ÉTAPE 13 — SceneView](#étape-13--sceneview)
16. [ÉTAPE 14 — MainView](#étape-14--mainview)
17. [ÉTAPE 15 — App.java (Point d'entrée)](#étape-15--appjava)

---

# 1 — Comprendre l'architecture MVVM

Avant de coder, imagine ton projet comme une **pizza** découpée en 3 couches :

```
┌──────────────────────────────────────────────────┐
│                    VIEW (Vue)                     │
│  Ce que l'UTILISATEUR voit et touche              │
│  → Boutons, listes, canvas, labels                │
│  → PaletteView, ProgramView, SceneView, etc.     │
├──────────────────────────────────────────────────┤
│                 VIEWMODEL                         │
│  Le CERVEAU qui connecte la vue au modèle         │
│  → Il ne connaît PAS les boutons/labels           │
│  → Il expose des PROPRIÉTÉS que la vue observe    │
│  → MainViewModel                                  │
├──────────────────────────────────────────────────┤
│                   MODEL (Modèle)                  │
│  Les DONNÉES pures et la LOGIQUE métier           │
│  → Il ne sait même pas qu'une interface existe    │
│  → Action, Program, ExecutionContext              │
└──────────────────────────────────────────────────┘
```

**La règle d'or** :
- La **Vue** parle au **ViewModel** (jamais directement au Modèle)
- Le **ViewModel** parle au **Modèle**
- Le **Modèle** ne parle à personne — il fait juste son travail

---

# 2 — Comprendre les Bindings (LE NERF DU PROJET)

> [!IMPORTANT]
> **C'est LA chose la plus importante à comprendre.** Si tu comprends les bindings, tu comprends 80% du projet.

## C'est quoi un Binding ?

Imagine un **thermomètre** accroché dehors et un **écran** dans ta maison qui affiche la température.

- **Sans binding** : tu dois aller dehors, lire le thermomètre, revenir, et écrire la valeur sur l'écran toi-même. À chaque changement !
- **Avec binding** : le thermomètre est **connecté** à l'écran par un fil. Quand la température change dehors, l'écran se met à jour **tout seul**.

En JavaFX, un binding c'est exactement ça : **une connexion automatique entre deux valeurs**.

## Les Property (les "fils")

En JavaFX, une `Property` c'est une variable spéciale qui **prévient tout le monde quand elle change**.

```java
// Variable Java normale (pas de notification)
int age = 25;

// Property JavaFX (notifie quand elle change)
IntegerProperty age = new SimpleIntegerProperty(25);
```

Les types de Property :
| Type Java | Property JavaFX | Comment créer |
|---|---|---|
| `int` | `IntegerProperty` | `new SimpleIntegerProperty(0)` |
| `boolean` | `BooleanProperty` | `new SimpleBooleanProperty(false)` |
| `String` | `StringProperty` | `new SimpleStringProperty("")` |
| `double` | `DoubleProperty` | `new SimpleDoubleProperty(0.0)` |

## Comment on "branche le fil" (bind)

```java
// Dans le ViewModel :
private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);

// Dans la Vue :
// "Quand programLoaded est FALSE, le bouton est DÉSACTIVÉ"
btnNext.disableProperty().bind(programLoaded.not());
```

Ce qui se passe :
1. Au début, `programLoaded = false` → `programLoaded.not()` = `true` → bouton désactivé ✅
2. Quelqu'un appelle `programLoaded.set(true)` → `programLoaded.not()` = `false` → bouton activé ✅
3. **Tu n'as RIEN d'autre à faire.** Le bouton se met à jour tout seul !

## BooleanBinding (binding calculé)

Parfois tu veux une condition plus complexe. Par exemple : "Le bouton Charger est actif SI la liste n'est pas vide ET le programme est valide".

```java
public BooleanBinding canLoad() {
    return Bindings.createBooleanBinding(
        // 1. La FORMULE : quand est-ce qu'on peut charger ?
        () -> !observableActions.isEmpty() && program.isValid(new ExecutionContext()),
        // 2. Les DÉCLENCHEURS : quand est-ce qu'on recalcule la formule ?
        observableActions,        // recalcule quand la liste change
        programChangeCounter      // recalcule quand le compteur change
    );
}
```

**Analogie** : C'est comme une formule Excel.
- La cellule C1 contient `=A1+B1`
- Quand tu changes A1 ou B1, C1 se recalcule tout seul
- Ici, `canLoad()` se recalcule quand `observableActions` ou `programChangeCounter` change

## Listener vs Binding

| | Listener | Binding |
|---|---|---|
| **Quoi** | "Quand X change, fais Y" | "La valeur de A est toujours = B" |
| **Exemple** | Quand on sélectionne une action → afficher ses détails | Le bouton est désactivé quand la liste est vide |
| **Code** | `property.addListener((obs, old, new) -> ...)` | `button.disableProperty().bind(...)` |
| **Quand utiliser** | Actions/effets de bord (afficher, dessiner, etc.) | Synchroniser des propriétés (actif/inactif, texte, etc.) |

## ObservableList (liste qui prévient)

```java
// Liste normale (personne ne sait quand elle change)
List<Action> actions = new ArrayList<>();

// ObservableList (prévient tout le monde quand on ajoute/supprime)
ObservableList<Action> observableActions = FXCollections.observableArrayList();
```

Quand tu fais `observableActions.add(action)`, la `ListView` dans la Vue se met à jour **toute seule** car elle est abonnée à cette liste.

## Schéma récapitulatif des bindings du projet

```
VIEWMODEL                              VUE
─────────                              ───
observableActions ──────────────────── ListView (affiche la liste)
selectedIndex ──────────────────────── ListView.selection (synchronisé)
programLoaded ──────────────────────── btnNext.disable (si false → grisé)
                                       btnReset.text ("Charger" ou "Ré-initialiser")
canLoad() ──────────────────────────── btnReset.disable (si false → grisé)
canExecuteNext() ───────────────────── btnNext.disable
canRemove() ────────────────────────── btnRemove.disable
canMoveUp() ────────────────────────── btnUp.disable
canMoveDown() ──────────────────────── btnDown.disable
canDuplicate() ─────────────────────── btnDuplicate.disable
errorMessage ───────────────────────── lblRuntimeError.text
turtleState ────────────────────────── lblTurtle.text
executionStep ──────────────────────── Canvas (redessiné à chaque changement)
observableVariables ────────────────── TableView (affiche les variables)
```

> [!TIP]
> **Chaque fois que tu vois un `.bind(...)` ou un `.addListener(...)` dans le code, reviens ici pour comprendre ce qu'il connecte à quoi.**

---

# ÉTAPE 1 — Le Modèle de base

## Fichier 1 : `ActionType.java`

📁 `src/main/java/scratch/model/ActionType.java`

### Pourquoi ?
C'est une **liste de tous les types d'actions possibles** dans ton programme Scratch. C'est comme un menu de restaurant : tu listes tous les plats disponibles.

Une `enum` en Java c'est juste une liste de constantes. Ici, chaque constante représente un type d'action que la tortue peut faire.

### Code

```java
package scratch.model;

public enum ActionType {
    VAR_DECLARATION,
    VAR_ASSIGNMENT,
    INCREMENT_VARIABLE,
    MOVE_FORWARD,
    TURN_LEFT,
    TURN_RIGHT,
    REPEAT,
    END_REPEAT,
    PEN_UP,
    PEN_DOWN
}
```

### À quoi ça sert ?
Partout dans le code, au lieu d'écrire des Strings comme `"move_forward"` (risque de faute de frappe !), on utilise `ActionType.MOVE_FORWARD`. Si tu fais une faute, Java te le dit direct en rouge.

---

## Fichier 2 : `ExecutionException.java`

📁 `src/main/java/scratch/model/ExecutionException.java`

### Pourquoi ?
Quand le programme de la tortue fait une erreur (par exemple, utiliser une variable qui n'existe pas), on veut lancer une **exception spécifique** pour qu'on sache que c'est une erreur d'exécution du programme Scratch, pas un bug dans notre code Java.

### Code

```java
package scratch.model;

public class ExecutionException extends RuntimeException {
    public ExecutionException(String message) {
        super("Runtime Error : " + message);
    }
}
```

### Comment ça marche ?
- `extends RuntimeException` = c'est une exception Java qui n'a pas besoin d'être déclarée dans le `throws`
- On passe juste un message d'erreur, par exemple `"Variable non déclarée : nb"`

---

## Fichier 3 : `Segment.java`

📁 `src/main/java/scratch/model/Segment.java`

### Pourquoi ?
Quand la tortue avance avec le stylo baissé, elle dessine un **trait** sur le canvas. Un trait, c'est un point de départ `(x1, y1)` et un point d'arrivée `(x2, y2)`. C'est tout ce que cette classe stocke.

### Code

```java
package scratch.model;

public class Segment {
    private int x1 ;
    private int y1 ;
    private int x2 ;
    private int y2 ;

    public Segment(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }
}
```

---

## Fichier 4 : `Action.java` (classe abstraite)

📁 `src/main/java/scratch/model/Action.java`

### Pourquoi ?
C'est la **classe mère** de toutes les actions. Elle définit un "contrat" : toute action DOIT avoir ces 3 méthodes. C'est comme un formulaire vide que chaque sous-classe remplit à sa façon.

### Code

```java
package scratch.model;

public abstract class  Action {

    public abstract void execute(ExecutionContext e );
    public abstract boolean isValid(ExecutionContext e ) ;
    public abstract ActionType getType();
    public abstract Action duplicate();
    public abstract String format();
    public abstract String getTitle();
    public String getUnit() { return ""; }
    public boolean isValueEditable() { return false; }
    public int getNumericValue() { return 0; }
    public boolean updateValue(int newValue) { return false; }
    public boolean updateVariable(String varName) { return false; }
    public String getExpression() { return String.valueOf(getNumericValue()); }
    public int resolveCount(ExecutionContext ctx) { return 0; }
    public String getTargetVar() { return ""; }
    public boolean isCountIsVar() { return false; }
    public boolean isVisual() { return false; }

}
```

### Explication de chaque méthode

| Méthode | Rôle | Exemple |
|---|---|---|
| `execute(ctx)` | Fait l'action concrètement | `MoveForwardAction` → déplace la tortue |
| `isValid(ctx)` | Vérifie si l'action est valide avant exécution | Valeur dans les bornes ? Variable déclarée ? |
| `getType()` | Retourne le type de l'enum | `ActionType.MOVE_FORWARD` |

### Pourquoi `abstract` ?
Parce que `Action` ne sait pas COMMENT s'exécuter — c'est chaque sous-classe (MoveForward, TurnLeft, etc.) qui sait. Le mot `abstract` dit : "je déclare que cette méthode existe, mais c'est à mes enfants de l'implémenter".

---

# ÉTAPE 2 — Les Actions simples

## Fichier 5 : `ParameterizedAction.java`

📁 `src/main/java/scratch/model/ParameterizedAction.java`

### Pourquoi ?
`MoveForwardAction`, `TurnLeftAction`, et `TurnRightAction` ont toutes un **paramètre numérique** (la distance ou l'angle). Au lieu de copier-coller le code 3 fois, on crée une classe intermédiaire qui gère ce paramètre.

Cette classe gère aussi le mode **variable** : au lieu de "Avancer de 30", on peut dire "Avancer de nb" (où `nb` est une variable déclarée).

### Code

```java
package scratch.model;

public abstract class ParameterizedAction extends Action {
    private int value;
    private boolean isVar = false;
    private String varName = "";

    public ParameterizedAction(int value) {
        this.value = value;
    }

    public int getValue() { return value; }
    public void setValue(int value) {
        this.value = value;
        this.isVar = false;
    }


    public boolean isVar() { return isVar; }
    public String getVarName() { return varName; }

    public void setVarName(String varName) {
        this.varName = varName;
        this.isVar = true;
    }


    public int resolveValue(ExecutionContext e) {
        if (isVar) {
            return e.getVariable(varName);
        }
        return value;
    }
    // ----------------------------------

    protected abstract boolean isValueValid(int value);
    public abstract int getDefaultValue();

    protected void copyStateTo(ParameterizedAction target) {
        if (isVar()) {
            target.setVarName(getVarName());
        } else {
            target.setValue(getValue());
        }
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (isVar()) {
            String name = getVarName();
            if (name == null || name.isBlank()) return false;
            return e.hasVariable(name);
        }
        return isValueValid(getValue());
    }

    @Override
    public String format() {
        if (isVar()) {
            return getType().name() + ";" + getVarName();
        } else {
            return getType().name() + ";" + getValue();
        }
    }


    @Override
    public boolean isValueEditable() { return !isVar(); }

    @Override
    public int getNumericValue() { return getValue(); }

    @Override
    public boolean updateValue(int newValue) {
        if (isValueValid(newValue)) {
            setValue(newValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateVariable(String varName) {
        setVarName(varName);
        return true;
    }

    @Override
    public String getExpression() {
        return isVar() ? getVarName() : String.valueOf(getValue());
    }
}
```

### C'est quoi `resolveValue` ?
C'est la méthode magique qui dit : "Si la valeur vient d'une variable, va la chercher dans le contexte. Sinon, utilise la valeur fixe."

Exemple :
- `Avancer de 30` → `resolveValue()` retourne `30`
- `Avancer de nb` (et nb = 50) → `resolveValue()` va chercher `nb` dans le contexte et retourne `50`

---

## Fichier 6 : `MoveForwardAction.java`

📁 `src/main/java/scratch/model/MoveForwardAction.java`

### Pourquoi ?
C'est l'action "Avancer de X pixels". La tortue se déplace tout droit dans la direction où elle regarde.

### Code

```java
package scratch.model;

public class MoveForwardAction extends ParameterizedAction{
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 100;
    public static final int DEFAULT_VALUE = 30;

    public MoveForwardAction() {
        super(DEFAULT_VALUE);
    }
    public MoveForwardAction(int value){
        super(value);
    }

    @Override
    public void execute(ExecutionContext e) {
        int realValue = resolveValue(e);
        if (realValue < MIN_VALUE || realValue > MAX_VALUE) {
            throw new ExecutionException(
                    "Avancer de " + realValue + " hors plage " + MIN_VALUE + "–" + MAX_VALUE);
        }
        e.move(realValue);
    }
    @Override
    protected boolean isValueValid(int value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    @Override
    public int getDefaultValue() {
        return DEFAULT_VALUE ;
    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE_FORWARD;
    }

    @Override
    public Action duplicate() {
        MoveForwardAction clone = new MoveForwardAction();
        copyStateTo(clone);
        return clone;
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() {
        return  "Avance de ";
    }

    @Override
    public String getUnit() {
        return " Pixels";
    }

}
```

### Comment `isValid` marche ?
1. Si on utilise une variable (`isVar() == true`) → on vérifie juste que la variable existe dans le contexte
2. Si c'est un nombre fixe → on vérifie qu'il est entre 1 et 100

### Comment `execute` marche ?
1. `resolveValue(e)` → donne la vraie valeur (nombre ou variable résolue)
2. `e.move(realValue)` → dit au contexte de déplacer la tortue

---

## Fichier 7 : `TurnLeftAction.java`

📁 `src/main/java/scratch/model/TurnLeftAction.java`

### Pourquoi ?
C'est l'action "Tourner à gauche de X degrés". Même logique que MoveForward mais appelle `e.turnLeft()` au lieu de `e.move()`.

### Code

```java
package scratch.model;

public class TurnLeftAction extends ParameterizedAction {

    public static final int MIN_VALUE = 1 ;
    public static final int MAX_VALUE = 180 ;
    public static final int DEFAULT_VALUE = 90 ;

    public TurnLeftAction(){
        super(DEFAULT_VALUE);
    }
    public TurnLeftAction(int value) {
        super(value);
    }

    @Override
    protected boolean isValueValid(int value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    @Override
    public int getDefaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public void execute(ExecutionContext e) {
        int realValue = resolveValue(e);
        if (realValue < MIN_VALUE || realValue > MAX_VALUE) {
            throw new ExecutionException(
                    "Tourner à gauche de " + realValue + " hors born " + MIN_VALUE + "–" + MAX_VALUE);
        }
        e.turnLeft(realValue);
    }

    @Override
    public ActionType getType() {
        return ActionType.TURN_LEFT;
    }
    @Override
    public Action duplicate() {
        TurnLeftAction clone = new TurnLeftAction();
        copyStateTo(clone);
        return clone;
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() {
        return "Tourner à gauche de ";
    }

    @Override
    public String getUnit() {
        return " Degrés";
    }

}
```

---

## Fichier 8 : `TurnRightAction.java`

📁 `src/main/java/scratch/model/TurnRightAction.java`

### Pourquoi ?
Identique à TurnLeft mais tourne à droite. C'est `e.turnRight()`.

### Code

```java
package scratch.model;

public class TurnRightAction  extends ParameterizedAction{

    public static  final int MIN_VALUE = 1 ;
    public static  final int MAX_VALUE = 180 ;
    public static  final int DEFAULT_VALUE = 90 ;

    public TurnRightAction(){
        super(DEFAULT_VALUE);
    }

    public TurnRightAction(int value) {
        super(value);
    }

    @Override
    protected boolean isValueValid(int value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    @Override
    public int getDefaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public void execute(ExecutionContext e) {
        int realValue = resolveValue(e);
        if (realValue < MIN_VALUE || realValue > MAX_VALUE) {
            throw new ExecutionException(
                    "Tourner à droite de " + realValue + " hors plage " + MIN_VALUE + "–" + MAX_VALUE);
        }
        e.turnRight(realValue);
    }

    @Override
    public ActionType getType() {
        return ActionType.TURN_RIGHT;
    }

    @Override
    public Action duplicate() {
        TurnRightAction clone = new TurnRightAction();
        copyStateTo(clone);
        return clone;
    }


    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() {
        return "Tourner à droite de ";
    }

    @Override
    public String getUnit() {
        return " Degrés";
    }

}
```

---

## Fichier 9 : `PenUpAction.java`

📁 `src/main/java/scratch/model/PenUpAction.java`

### Pourquoi ?
La tortue a un stylo. Quand le stylo est **levé**, elle ne dessine pas en se déplaçant. Cette action lève le stylo.

### Code

```java
package scratch.model;

public class PenUpAction extends Action {

    @Override
    public  void execute(ExecutionContext context ) {
        context.penUp();
    }

    @Override
    public boolean isValid(ExecutionContext context){
        return context.isPenDown() ;
    }

    @Override
    public ActionType getType() { return ActionType.PEN_UP; }

    @Override
    public Action duplicate() {
        return new PenUpAction();
    }

    @Override
    public String format() {
        return "PEN_UP;";
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() { return "Lever le stylo"; }
}
```

### Pourquoi `isValid` vérifie `isPenDown()` ?
On ne peut lever le stylo que s'il est déjà baissé. Si le stylo est déjà levé, ça n'a pas de sens de le lever encore.

---

## Fichier 10 : `PenDownAction.java`

📁 `src/main/java/scratch/model/PenDownAction.java`

### Pourquoi ?
L'inverse de PenUp. Quand le stylo est **baissé**, la tortue dessine en se déplaçant.

### Code

```java
package scratch.model;

public class PenDownAction extends Action {

    @Override
    public  void execute(ExecutionContext context ) {
        context.penDown();

    }

    @Override
    public boolean isValid(ExecutionContext context){
        return !context.isPenDown() ;

    }
    @Override
    public ActionType getType() { return ActionType.PEN_DOWN; }

    @Override
    public Action duplicate() {
        return new PenDownAction();
    }

    @Override
    public String format() {
        return "PEN_DOWN;";
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() { return "Abaisser le stylo"; }
}
```

---

# ÉTAPE 3 — Les Variables

## Fichier 11 : `VarDeclarationAction.java`

📁 `src/main/java/scratch/model/VarDeclarationAction.java`

### Pourquoi ?
Avant d'utiliser une variable comme `nb`, il faut la **déclarer** (la créer). C'est comme dire au programme : "Hey, je vais utiliser une variable qui s'appelle `nb`, prépare-la !"

### Code

```java
package scratch.model;

public class VarDeclarationAction extends Action {

    private String varName;

    public VarDeclarationAction() {
        this.varName = "var";
    }

    public VarDeclarationAction(String varName) {
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public void execute(ExecutionContext e) {
        e.declareVariable(varName);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (varName == null || varName.isBlank()) return false;
        return varName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    @Override
    public ActionType getType() {
        return ActionType.VAR_DECLARATION;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        return new VarDeclarationAction(this.varName);
    }

    @Override
    public String format() {
        return getType().name() + ";" + varName;
    }

    @Override
    public String getTitle() { return "Déclaration variable " + varName; }

}
```

### C'est quoi le `matches("^[a-zA-Z_][a-zA-Z0-9_]*$")` ?
C'est une **expression régulière** (regex) qui vérifie que le nom de la variable est valide :
- Commence par une lettre ou `_` (pas un chiffre)
- Suivi de lettres, chiffres ou `_`
- Exemples valides : `nb`, `score`, `mon_var`, `_test`
- Exemples invalides : `123`, `ma variable` (espace), `nb!`

---

## Fichier 12 : `VarAssignmentAction.java`

📁 `src/main/java/scratch/model/VarAssignmentAction.java`

### Pourquoi ?
Une fois la variable déclarée, on veut lui donner une valeur. Par exemple : `nb = 5` ou `nb = autreVariable`.

### Code

```java
package scratch.model;

public class VarAssignmentAction extends Action{

    private String targetVar;
    private String value;

    public VarAssignmentAction() {
        this.targetVar = "var";
        this.value = "0";
    }

    public VarAssignmentAction(String targetVar, String value) {
        this.targetVar = targetVar;
        this.value = value;
    }

    @Override
    public String getTargetVar() { return targetVar; }
    public void setTargetVar(String targetVar) { this.targetVar = targetVar; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public void execute(ExecutionContext e) {
        int resolvedValue = e.resolveExpression(value);
        if (resolvedValue > 100) {
            throw new ExecutionException(
                    "La variable " + targetVar + " dépasse 100 (" + resolvedValue + ")");
        }
        e.setVariable(targetVar, resolvedValue);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (targetVar == null || targetVar.isBlank()) return false;
        if (!e.hasVariable(targetVar)) return false;
        return e.isValidExpression(value);
    }


    @Override
    public ActionType getType() {
        return ActionType.VAR_ASSIGNMENT;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        return new VarAssignmentAction(this.targetVar, this.value);
    }

    @Override
    public String format() {
        return getType().name() + ";" + targetVar + ";" + value;
    }

    @Override
    public String getTitle() { return "Assignation : " + targetVar + " = " + value; }

}
```

### Pourquoi `value` est un `String` et pas un `int` ?
Parce que la valeur peut être :
- Un nombre : `"5"` → on fait `Integer.parseInt("5")` → 5
- Un nom de variable : `"nb"` → on fait `e.getVariable("nb")` → la valeur de nb

C'est le `resolveValue` qui fait cette distinction.

---

## Fichier 13 : `IncrementVariableAction.java`

📁 `src/main/java/scratch/model/IncrementVariableAction.java`

### Pourquoi ?
Pour faire `nb = nb + 1` (ou `nb = nb - 3`). C'est l'action qui augmente ou diminue une variable.

### Code

```java
package scratch.model;

public class IncrementVariableAction extends Action {

    private String targetVar;
    private String value;

    public IncrementVariableAction() {
        this.targetVar = "var";
        this.value = "1";
    }

    public IncrementVariableAction(String targetVar, String value) {
        this.targetVar = targetVar;
        this.value = value;
    }

    @Override
    public String getTargetVar() { return targetVar; }
    public void setTargetVar(String targetVar) { this.targetVar = targetVar; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public void execute(ExecutionContext e) {
        int step = e.resolveExpression(value);
        int newVal = e.getVariable(targetVar) + step;
        e.setVariable(targetVar, newVal);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (targetVar == null || targetVar.isBlank()) return false;
        if (!e.hasVariable(targetVar)) return false;
        return e.isValidExpression(value);
    }

    @Override
    public ActionType getType() {
        return ActionType.INCREMENT_VARIABLE;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        return new IncrementVariableAction(this.targetVar, this.value);
    }

    @Override
    public String format() {
        return getType().name() + ";" + this.targetVar + ";" + this.value;
    }

    @Override
    public String getTitle() { return "Inc/Dec variable : " + targetVar + " de " + value; }

}
```

---

# ÉTAPE 4 — Les Boucles

## Fichier 14 : `RepeatAction.java`

📁 `src/main/java/scratch/model/RepeatAction.java`

### Pourquoi ?
C'est l'action "Répéter X fois". Elle marque le **début** d'une boucle. Le nombre de répétitions peut être un nombre fixe (`4`) ou une variable (`nb`).

> [!NOTE]
> `RepeatAction` n'hérite PAS de `ParameterizedAction` parce que `ParameterizedAction` gère un `int` avec un mode variable `String`. Ici on a deux modes complètement différents (int OU String pour le compteur), donc on hérite directement d'`Action`.

### Code

```java
package scratch.model;

public class RepeatAction extends Action {

    private int count;
    private String countVarName;
    private boolean countIsVar;

    public RepeatAction(int count) {
        this.count = count;
        this.countIsVar = false;
    }

    public RepeatAction(String countVarName) {
        this.countVarName = countVarName;
        this.countIsVar = true;
    }

    public int resolveCount(ExecutionContext ctx) {
        if (countIsVar) {
            return ctx.getVariable(countVarName);
        }
        return count;
    }


    @Override
    public void execute(ExecutionContext e) {

    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (countIsVar) {
            if (countVarName == null || countVarName.isBlank()) {
                return false;
            }
            return e.hasVariable(countVarName) && e.getVariable(countVarName) > 0;
        }
        return count > 0;
    }

    @Override
    public ActionType getType() {
        return ActionType.REPEAT;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public String getCountVarName() {
        return countVarName;
    }
    public void setCountVarName(String countVarName) {
        this.countVarName = countVarName;
    }

    @Override
    public boolean isCountIsVar() {
        return countIsVar;
    }
    public void setCountIsVar(boolean countIsVar) {
        this.countIsVar = countIsVar;
    }

    @Override
    public String toString() {
        return getTitle() + getExpression() + getUnit();
    }


    @Override
    public Action duplicate() {
        if (countIsVar) {
            return new RepeatAction(this.countVarName);
        } else {
            return new RepeatAction(this.count);
        }
    }

    @Override
    public String format() {
        return getType().name() + ";" + getExpression();
    }

    @Override
    public String getTitle() {
        return "Repeter ";
    }

    @Override
    public String getUnit() {
        return " fois";
    }

    @Override
    public boolean isValueEditable() {
        return true;
    }

    @Override
    public int getNumericValue() {
        return count;
    }

    @Override
    public boolean updateValue(int newValue) {
        if (newValue > 0) {
            this.count = newValue;
            this.countIsVar = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean updateVariable(String varName) {
        this.countVarName = varName;
        this.countIsVar = true;
        return true;
    }

    @Override
    public String getExpression() {
        return isCountIsVar() ? getCountVarName() : String.valueOf(getCount());
    }
}
```

### Pourquoi `execute()` est vide ?
Parce que `RepeatAction` ne fait rien "elle-même". C'est `Program.executeNext()` qui gère toute la logique de la boucle (empiler, compter, sauter). L'action est juste un **marqueur** : "ici commence une boucle".

---

## Fichier 15 : `EndRepeatAction.java`

📁 `src/main/java/scratch/model/EndRepeatAction.java`

### Pourquoi ?
C'est le marqueur **"Fin répéter"**. Il dit à `Program` : "la boucle se termine ici". Comme `RepeatAction`, il ne fait rien tout seul.

### Code

```java
package scratch.model;

public class EndRepeatAction extends Action {
    @Override
    public void execute(ExecutionContext e) {

    }

    @Override
    public boolean isValid(ExecutionContext e) {
        return true;
    }

    @Override
    public ActionType getType() {
        return ActionType.END_REPEAT;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        return new EndRepeatAction();
    }

    @Override
    public String format() {
        return getType().name() + ";";
    }

    @Override
    public String getTitle() { return "Fin repeter"; }
}
```

---

# ÉTAPE 5 — ExecutionContext

## Fichier 16 : `ExecutionContext.java`

📁 `src/main/java/scratch/model/ExecutionContext.java`

### Pourquoi ?
C'est le **monde** dans lequel la tortue vit. Il contient :
- La **position** de la tortue (x, y)
- Sa **direction** (0° = vers le haut)
- L'état du **stylo** (levé ou baissé)
- La liste des **segments** dessinés
- La **pile des boucles** (pour gérer Repeat/EndRepeat imbriqués)
- Les **variables** déclarées et leurs valeurs

### Code

```java
package scratch.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {
    private static final int DEFAULT_X = 250;
    private static final int DEFAULT_Y = 250;
    private static final int DEFAULT_DIRECTION = 0;
    private static final boolean DEFAULT_PEN_DOWN = true;

    private int x, y, direction;
    private boolean penDown;
    private List<Segment> segments;
    private Deque<int[]> repeatStack = new ArrayDeque<>();
    private final Map<String, Integer> variables;

    public ExecutionContext() {
        this.segments = new ArrayList<>();
        this.variables = new HashMap<>();
        reset();
    }



    public void declareVariable(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom de variable vide");
        }

        if (!name.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Nom de variable invalide : " + name);
        }
        if (variables.containsKey(name)) {
            throw new IllegalArgumentException("Variable déjà déclarée : " + name);
        }
        variables.put(name, 0);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    public void setVariable(String name, int val) {
        if (!variables.containsKey(name)) {
            throw new ExecutionException("Variable non déclarée : " + name);
        }
        variables.put(name, val);
    }

    public int getVariable(String name) {
        //getOrDefault faisait croire qu’une var inexistante vaut default
        //on veux verifier le key (var) et envoyer une erreur
        // si elle existe pas  avant de la consulter son value
        if (!variables.containsKey(name)) {
            throw new ExecutionException("Variable indéfinie : " + name);
        }
        return variables.get(name);
    }

    public Map<String, Integer> getVariablesSnapshot() {
        return new HashMap<>(variables);
    }

    public void move(int distance) {
        double radians = Math.toRadians(direction - 90);
        int oldX = x, oldY = y;

        x += (int) Math.round(distance * Math.cos(radians));
        y += (int) Math.round(distance * Math.sin(radians));

        if (penDown) {
            segments.add(new Segment(oldX, oldY, x, y));
        }
    }

    public void turnLeft(int angle) {
        direction = (direction - angle + 360) % 360;
    }

    public void turnRight(int angle) {
        direction = (direction + angle) % 360;
    }

    public void penUp() {
        this.penDown = false;
    }

    public void penDown() {
        this.penDown = true;
    }

    public void reset() {
        x = DEFAULT_X;
        y = DEFAULT_Y;
        direction = DEFAULT_DIRECTION;
        penDown = DEFAULT_PEN_DOWN;
        segments.clear();
        repeatStack.clear();
        variables.clear();
    }

    // GETTERS

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPositionTortueX() {
        return x - DEFAULT_X;
    }

    public int getPositionTortueY() {
        return DEFAULT_Y - y;
    }

    public int getDirection() {
        return direction;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public boolean isPenDown() {
        return penDown;
    }

    // Empiler nouvelle Boucle
    public void pushRepeat(int repeatIndex, int iterations) {
        repeatStack.push(new int[] { repeatIndex, iterations });
    }

    // Regarder la boucle en cours sans retirer
    public int[] peekRepeat() {
        return repeatStack.peek();
    }

    // Retirer la boucle
    public void popRepeat() {
        repeatStack.pop();
    }

    // Y a t il une boucle active
    public boolean hasRepeat() {
        return !repeatStack.isEmpty();
    }

    public int resolveExpression(String expr) {
        try {
            return Integer.parseInt(expr);
        } catch (NumberFormatException e) {
            return getVariable(expr);
        }
    }

    public boolean isValidExpression(String expr) {
        if (expr == null || expr.isBlank()) return false;
        try {
            Integer.parseInt(expr.trim());
            return true;
        } catch (NumberFormatException e) {
            return hasVariable(expr.trim());
        }
    }

}
```

### Comment la pile des boucles fonctionne ?

Imagine que tu lis un livre et tu notes sur des post-it :

```
Programme:
0: Repeat 3 fois     ← pushRepeat(0, 2)  // 3-1 = 2 itérations restantes
1:   Avancer 30      ← execute normalement
2:   Repeat 2 fois   ← pushRepeat(2, 1)  // 2-1 = 1 itération restante
3:     Tourner 90    ← execute normalement
4:   EndRepeat        ← peekRepeat → [2, 1] → il reste 1 → retour à index 3
5: EndRepeat          ← peekRepeat → [0, 2] → il reste 2 → retour à index 1

Pile (comme des post-it empilés):
 ┌──────────┐
 │ [2, 1]   │  ← boucle intérieure (on dépile en premier)
 ├──────────┤
 │ [0, 2]   │  ← boucle extérieure
 └──────────┘
```

### Pourquoi `getPositionTortueX()` et pas juste `getX()` ?
- `getX()` donne la position **absolue** sur le canvas (ex: 280 pixels)
- `getPositionTortueX()` donne la position **relative au centre** (ex: 30 pixels depuis le centre)
- C'est `getPositionTortueX()` qu'on affiche dans la zone info ("Tortue : x = 30, y = 0")

---

# ÉTAPE 6 — Program

## Fichier 17 : `Program.java`

📁 `src/main/java/scratch/model/Program.java`

### Pourquoi ?
C'est le **chef d'orchestre**. Il contient la liste de toutes les actions et sait comment les exécuter une par une, y compris les boucles. C'est la classe la plus importante du modèle.

### Code

```java
package scratch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Program {
    private final List<Action> actions;
    private int currentIndex;

    public Program() {
        this.actions = new ArrayList<>();
        this.currentIndex = 0;
    }


    //Gestion du programme
    public void addAction(Action action){
        if (action == null) throw new IllegalArgumentException("Action null");
        actions.add(action);

    }
    //insertion au "milieu" du program
    public void insertAction(int index, Action action) {
        if (action == null) {
            throw new IllegalArgumentException("Action null");
        }
        if (index < 0 || index >= actions.size()) {
            actions.add(action);
        } else {
            actions.add(index + 1, action);
        }
    }


    public void removeAction(int index) {
       //index valide ?
        if (index >= 0 && index < actions.size()) {

            actions.remove(index);
        }
    }

    public Action getAction(int index){

        return actions.get(index);
    }
    public  List<Action> getActions(){
        return Collections.unmodifiableList(actions);
    }
    public void moveUp(int index){
        if (index > 0)
            Collections.swap(actions , index , index - 1);
    }

    public void moveDown(int index){
        if (index < actions.size() - 1)
            Collections.swap(actions , index , index + 1);
    }

    public void duplicateAt(int index){
        if (index >= 0 && index < actions.size()) {
            Action original = actions.get(index);
            Action duplicate = original.duplicate();

            actions.add(index + 1 , duplicate);
        }
    }

    public void clear(){
        actions.clear();
        resetExecution();
    }

    //Methodes d'execution

    public boolean isValid(ExecutionContext context) {

        // une copie du contexte pour la simulation
        //Sert essentiellemnt a definir  si le boutton chargé va etre actif
        // si on repart d'un contexte initial (reset),
        // et on reinsert tout les action,
        // est-ce que ce programme est cohérent ?
        ExecutionContext tempContext = new ExecutionContext();

        boolean instructionSeen = false;
        int repeatDepth = 0;
        boolean hasVisualAction = false;

        try {
            for (Action action : actions) {
                if (action.getType() == ActionType.VAR_DECLARATION) {
                    if (instructionSeen) {
                        return false;
                    }
                } else {
                    instructionSeen = true;
                }
                if (action.isVisual()) hasVisualAction = true;
                if (action.getType() == ActionType.REPEAT){
                    repeatDepth++;
                } else if (action.getType() == ActionType.END_REPEAT) {
                    repeatDepth--;
                    if (repeatDepth < 0) return false;
                }

                if (!action.isValid(tempContext)) {
                    return false;
                }
                action.execute(tempContext);
            }


            // Vérifier variable repeat n'est pas modifiée dans la boucle
            if (!checkRepeatVarNotModified()) {
                return false;
            }

            return repeatDepth == 0 && hasVisualAction;

        } catch (ExecutionException e) {

            return false;
        }
    }

    private boolean checkRepeatVarNotModified() {
        for (int i = 0; i < actions.size(); i++) {
            Action action = actions.get(i);
            if (action.getType() == ActionType.REPEAT && action.isCountIsVar()) {
                String varName = action.getExpression();
                int endIndex = findEndRepeat(i);
                for (int j = i + 1; j < endIndex; j++) {
                    Action inner = actions.get(j);
                    if (inner.getType() == ActionType.INCREMENT_VARIABLE
                            || inner.getType() == ActionType.VAR_ASSIGNMENT) {
                        if (varName.equals(inner.getTargetVar())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void executeNext(ExecutionContext context){
        if (!hasNext()) throw new IllegalStateException("Pas d'action suivante");


        Action a = actions.get(currentIndex);

        if ( a.getType() == ActionType.REPEAT) {

            int n = a.resolveCount(context);
            if (n <= 0) {
                currentIndex = findEndRepeat(currentIndex) + 1;
            }else {
                context.pushRepeat(currentIndex , n - 1);
                currentIndex++ ;
            }
        } else if (a.getType() == ActionType.END_REPEAT) {
            if (context.hasRepeat()) {
                int[] top = context.peekRepeat();
                if (top[1] > 0) {
                    top[1]--;
                    currentIndex = top[0] + 1;
                } else {
                    context.popRepeat();
                    currentIndex++;
                }
            } else {
                currentIndex++;
            }
        } else {
            a.execute(context);
            currentIndex++;
        }
    }
    private int findEndRepeat(int from) {
        int count = 0 ;
        for (int i = from; i < actions.size(); i++) {
            if (actions.get(i).getType() == ActionType.REPEAT)
                count++ ;
            else if (actions.get(i).getType() == ActionType.END_REPEAT) {
                if (--count == 0)
                    return  i;
            }
        }
        return actions.size() -1;
    }

    public boolean hasNext(){
        return currentIndex < actions.size();
    }

    public void  resetExecution(){
        currentIndex = 0 ;
    }

    public int size() {
        return actions.size();
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
```

### Gestion du programme (ajouter, supprimer, déplacer)

```java
// (Voir le code complet au début de cette section)
```

### La validation du programme (pour le bouton "Charger")

> [!IMPORTANT]
> C'est cette méthode qui décide si le bouton **"Charger"** est actif ou grisé.
> Elle simule l'exécution du programme dans un contexte temporaire pour vérifier que tout est cohérent.

```java
// (Voir le code complet au début de cette section)
```

### L'exécution pas à pas

```java
// (Voir le code complet au début de cette section)
```

### Le helper pour trouver le EndRepeat correspondant

```java
// (Voir le code complet au début de cette section)
```

### Les utilitaires

```java
// (Voir le code complet au début de cette section)
```

### La duplication (copie profonde d'une action)

```java
// (Voir le code complet au début de cette section)
```

### Pourquoi `createDuplicate` au lieu de juste copier la référence ?
Si tu fais `actions.add(original)`, tu ajoutes **le même objet** dans la liste. Si tu modifies l'un, tu modifies l'autre ! `createDuplicate` crée un **nouvel objet** avec les mêmes valeurs → ils sont indépendants.

---

# ÉTAPE 7 — ProgramFileService

## Fichier 18 : `ProgramFileService.java`

📁 `src/main/java/scratch/model/ProgramFileService.java`

### Pourquoi ?
Pour sauvegarder et charger des programmes depuis des fichiers `.scr`. Le format est simple : une ligne par action, avec `TYPE;VALEUR`.

### Code

```java
package scratch.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ProgramFileService {


    public static void save(File file, List<Action> actions) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Action action : actions) {
                writer.println(action.format());
            }
        }
    }


    public static List<Action> load(File file) throws IOException {
        List<Action> actions = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());

        for (String line : lines) {
            if (line.isBlank()) continue;

            String[] parts = line.split(";");
            String type = parts[0].trim();

            actions.add(createFromType(type, parts));
        }
        return actions;
    }

    private static Action createFromType(String type, String[] parts) {
        switch (type) {
            case "VAR_DECLARATION":
                VarDeclarationAction varDecl = new VarDeclarationAction();
                if (parts.length > 1) varDecl.setVarName(parts[1].trim());
                return varDecl;

            case "VAR_ASSIGNMENT":
                VarAssignmentAction varAss = new VarAssignmentAction();
                if (parts.length > 1) varAss.setTargetVar(parts[1].trim());
                if (parts.length > 2) varAss.setValue(parts[2].trim());
                return varAss;

            case "INCREMENT_VARIABLE":
                IncrementVariableAction incVar = new IncrementVariableAction();
                if (parts.length > 1) incVar.setTargetVar(parts[1].trim());
                if (parts.length > 2) incVar.setValue(parts[2].trim());
                return incVar;

            case "REPEAT":
                if (parts.length > 1) {
                    String countStr = parts[1].trim();
                    if (countStr.matches("^-?\\d+$")) {
                        return new RepeatAction(Integer.parseInt(countStr));
                    } else {
                        return new RepeatAction(countStr);
                    }
                }
                return new RepeatAction(0);

            case "END_REPEAT":
                return new EndRepeatAction();

            case "MOVE_FORWARD":
                return fillParameterizedAction(new MoveForwardAction(), parts);

            case "TURN_LEFT":
                return fillParameterizedAction(new TurnLeftAction(), parts);

            case "TURN_RIGHT":
                return fillParameterizedAction(new TurnRightAction(), parts);

            case "PEN_UP":
                return new PenUpAction();

            case "PEN_DOWN":
                return new PenDownAction();

            default:
                throw new IllegalArgumentException("Type inconnu : " + type);
        }
    }

    private static Action fillParameterizedAction(ParameterizedAction action, String[] parts) {
        if (parts.length > 1) {
            String valStr = parts[1].trim();
            if (valStr.matches("^-?\\d+$")) {
                action.setValue(Integer.parseInt(valStr));
            } else {
                action.setVarName(valStr);
            }
        }
        return action;
    }
}
```

---

# ÉTAPE 8 — Le ViewModel (petites classes)

## Fichier 19 : `ActionDetail.java`

📁 `src/main/java/scratch/viewmodel/ActionDetail.java`

### Pourquoi ?
Quand tu sélectionnes une action dans la liste, le **panneau de détails** affiche des infos ("Avancer de [30] Pixels"). `ActionDetail` est un petit objet qui contient ces infos pour que la vue sache quoi afficher.

### Code

```java
package scratch.viewmodel;



public class ActionDetail {
    private final String title;
    private final boolean valueEditable;
    private final String unitText;
    private final int value;

    public ActionDetail(String title, boolean valueEditable, String unitText, int value) {
        this.title = title;
        this.valueEditable = valueEditable;
        this.unitText = unitText;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public boolean isValueEditable() {
        return valueEditable;
    }

    public String getUnitText() {
        return unitText;
    }

    public int getValue() {
        return value;
    }
}
```

### Comment ça s'utilise ?
Quand tu sélectionnes "Avancer de 30" :
```java
// (Voir le code complet au début de cette section)
```
La vue affiche alors : `Avancer de [30] Pixels` (avec `[30]` dans un TextField éditable)

---

## Fichier 20 : `VariableRow.java`

📁 `src/main/java/scratch/viewmodel/VariableRow.java`

### Pourquoi ?
Pour afficher les variables dans le `TableView` de la scène (le petit tableau "Nom | Valeur"). Chaque ligne du tableau est un `VariableRow`.

### Code

```java
package scratch.viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public  class VariableRow {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty value = new SimpleIntegerProperty();

    public VariableRow(String name, int value) {
        this.name.set(name);
        this.value.set(value);
    }

    public StringProperty nameProperty() { return name; }
    public IntegerProperty valueProperty() { return value; }
}
```

### Pourquoi des Property et pas des simples String/int ?
Parce que le `TableView` de JavaFX a besoin de `Property` pour **observer** les changements. Si une variable change de valeur pendant l'exécution, le tableau se met à jour tout seul.

---

# ÉTAPE 9 — MainViewModel (LE GROS MORCEAU)

## Fichier 21 : `MainViewModel.java`

📁 `src/main/java/scratch/viewmodel/MainViewModel.java`

> [!IMPORTANT]
> C'est le fichier **le plus important** du projet. C'est le cerveau qui connecte tout.
> On va le construire **section par section** pour bien comprendre chaque partie.

### Les imports et les champs

```java
package scratch.viewmodel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import scratch.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MainViewModel {

    private final Program program;
    private final ObservableList<Action> observableActions;
    private final ObservableList<VariableRow> observableVariables =
            FXCollections.observableArrayList();
    // -1 = aucune sélection
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(-1);

    private final ExecutionContext executionContext = new ExecutionContext();

    // Compteur d'exécution
    private final IntegerProperty executionStep = new SimpleIntegerProperty(0);

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final StringProperty turtleState = new SimpleStringProperty("");

    private final BooleanProperty autoMode = new SimpleBooleanProperty(false);
    private final DoubleProperty speed = new SimpleDoubleProperty(1.0);
    private Timeline autoTimeline ;
    private final IntegerProperty programChangeCounter = new SimpleIntegerProperty(0);
    private final IntegerProperty executionFaultLineIndex = new SimpleIntegerProperty(-1);

    public MainViewModel(Program program) {
        this.program = program;
        this.observableActions = FXCollections.observableArrayList(program.getActions());
        turtleState.set(buildTurtleStateString());
        refreshVariablesFromContext();

        selectedIndex.addListener((obs, oldVal, newVal) -> {
            int fault = executionFaultLineIndex.get();
            if (fault >= 0 && newVal != null && newVal.intValue() != fault) {
                executionFaultLineIndex.set(-1);
                errorMessage.set("");
            }
        });
    }



    //--------------------------ACTIONS---------------------------

    public void addAction(ActionType type) {
        Action action = createAction(type);

        int idx = selectedIndex.get();

        if (idx >= 0 && idx < observableActions.size()) {
            program.insertAction(idx, action);
            observableActions.add(idx + 1, action);
            selectedIndex.set(idx + 1);
        } else {
            program.addAction(action);
            observableActions.add(action);
            selectedIndex.set(observableActions.size() - 1);
        }
        program.resetExecution();
        executionStep.set(0);
        programLoaded.set(false);
        turtleState.set(buildTurtleStateString());
        notifyProgramChanged();
    }



    public Action getSelectedAction() {
        int idx = selectedIndex.get();
        // quel index est sélectionné ?
        if (idx >= 0 && idx < observableActions.size()) {
            //index valide ?
            return observableActions.get(idx);
            //si oui retourne l'action
        }
        //si non
        return null;

    }




    //Move Up
    public void moveUp() {
        int index = selectedIndex.get();
        if (index > 0 && index < observableActions.size()){
            program.moveUp(index);
            Action action = observableActions.remove(index);
            observableActions.add(index - 1 ,  action);
            this.selectedIndex.set(index - 1);
            program.resetExecution();
            executionStep.set(0);
            programLoaded.set(false);
        }
        notifyProgramChanged();
    }

    //Move Down
    public void moveDown() {
        int index = selectedIndex.get();
        if (index >= 0 && index < observableActions.size() - 1){
            program.moveDown(index);
            Action action = observableActions.remove(index);
            observableActions.add(index + 1 , action);
            this.selectedIndex.set(index + 1);
            program.resetExecution();
            executionStep.set(0);
            programLoaded.set(false);
        }
        notifyProgramChanged();

    }

    //Duplicate
    public void duplicateSelected(){
        int index = selectedIndex.get();
        if (index >= 0 && index < observableActions.size()){
            program.duplicateAt(index);
            Action duplicate = program.getAction(index + 1);
            observableActions.add(index + 1 , duplicate);
            selectedIndex.set(index + 1);
            program.resetExecution();
            executionStep.set(0);
            programLoaded.set(false);
        }
        notifyProgramChanged();
    }


    public void clearProgram(){
        program.clear();
        observableActions.clear();
        selectedIndex.set(-1);
        executionStep.set(0);
        programLoaded.set(false);
        errorMessage.set("");
        executionContext.reset();
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        stopAutoExecution();
        notifyProgramChanged();
    }


    public void removeSelectedAction() {
        int index = selectedIndex.get();


        if (index >= 0 && index < observableActions.size()) {
            //  supp action Modèle
            this.program.removeAction(index);
            //supp de la liste Observable
            this.observableActions.remove(index);

            // Si la liste est maintenant vide, on désélectionne
            if (this.observableActions.isEmpty()) {
                this.selectedIndex.set(-1);
            }
            // Sinon, si on a supprimé le tout dernier élément, on sélectionne le nv dernier
            else if (index >= this.observableActions.size()) {
                this.selectedIndex.set(this.observableActions.size() - 1);
            }
            // Sinon on a supprimé un élément au milieu
            program.resetExecution();
            executionStep.set(0);
            programLoaded.set(false);
        }
        notifyProgramChanged();

    }


    public void executeNext(){
        if (!program.hasNext()) {
            return;


        }
            try {


                program.executeNext(executionContext);
                refreshVariablesFromContext();
       
                turtleState.set(buildTurtleStateString());

                //Declenche le redessin du Canvas
                executionStep.set(executionStep.get() + 1);
                executionFaultLineIndex.set(-1);
                errorMessage.set("");
                if (program.hasNext()) {
                    selectedIndex.set(program.getCurrentIndex());
                }
            } catch (ExecutionException e) {
                stopAutoExecution();
                executionFaultLineIndex.set(program.getCurrentIndex());
                if (observableActions.isEmpty()) {
                    selectedIndex.set(-1);
                } else {
                    selectedIndex.set(program.getCurrentIndex());
                }

                String msg = e.getMessage();
                errorMessage.set(msg != null && !msg.isBlank() ? msg : "Runtime");
            }

    }

    public void loadOnScene() {
        if (!program.isValid(new ExecutionContext())) {
            errorMessage.set("Programme invalide");
            programLoaded.set(false);
            return;
        }
        errorMessage.set("");
        executionContext.reset();
        program.resetExecution();
        executionStep.set(0);
        programLoaded.set(true);
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        if (observableActions.isEmpty()) {
            selectedIndex.set(-1);
        } else {
            selectedIndex.set(program.getCurrentIndex());
        }


    }

    public void resetExecution() {
        program.resetExecution();
        executionContext.reset();
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        executionStep.set(0);
        errorMessage.set("");


     if (observableActions.isEmpty()) {
        selectedIndex.set(-1);
    } else {
        selectedIndex.set(0);
    }
    }

    public void saveToFile(File file) {
        try {

            ProgramFileService.save(file, program.getActions());
            errorMessage.set("");
        } catch (IOException e) {
            errorMessage.set("Erreur de sauvegarde : " + e.getMessage());
        }
    }


    public void loadFromFile(File file) {
        try {
            List<Action> loaded = ProgramFileService.load(file);
            clearProgram();
            for (Action a : loaded) {
                program.addAction(a);
                observableActions.add(a);
            }
            selectedIndex.set(observableActions.isEmpty() ? -1 : 0);
        } catch (Exception e) {
            System.err.println("Erreur chargement : " + e.getMessage());
        }
    }

    // ---------- Zone info --------------
    public StringProperty turtleStateProperty() {
        return turtleState;
    }

    private String buildTurtleStateString() {
        int x = executionContext.getPositionTortueX();
        int y = executionContext.getPositionTortueY();
        int direction = executionContext.getDirection();
        int angleAffiche = Math.min(direction, 360 - direction);

        return "Tortue : x = " + x + ", y = " + y + ", direction = " + angleAffiche + " °";
    }

    //--------------------------BINDINGS----------------------

    public BooleanBinding canRemove() {
        // On peut supprimer si la liste n'est pas vide ET qu'un élément est sélectionné
        return Bindings.isEmpty(observableActions).not()
                .and(selectedIndex.greaterThanOrEqualTo(0));
    }
    public BooleanBinding canExecuteNext() {
        return Bindings.createBooleanBinding(
                () -> {
                    String err = errorMessage.get();
                    boolean hasError = err != null && !err.isBlank();
                    return program.hasNext() && !hasError;
                },
                executionStep,
                errorMessage
        );
    }
    public BooleanBinding canMoveUp() {
        return selectedIndex.greaterThan(0);
    }
    public BooleanBinding canMoveDown() {
        return Bindings.createBooleanBinding(() -> {
            int idx = getSelectedIndex();
            return idx >= 0 && idx < observableActions.size() - 1;
        }, observableActions, selectedIndex);
    }
    public BooleanBinding canDuplicate() {
        return canRemove();
    }

    public BooleanBinding canLoad() {
        return Bindings.createBooleanBinding(
                () -> !observableActions.isEmpty()
                && program.isValid(new ExecutionContext()),
        observableActions,
                programChangeCounter
        );
    }
    private void notifyProgramChanged() {
        programChangeCounter.set(programChangeCounter.get() + 1);
    }


    private Action createAction(ActionType type) {
        return switch (type) {
            case MOVE_FORWARD -> new MoveForwardAction();
            case TURN_LEFT -> new TurnLeftAction();
            case TURN_RIGHT -> new TurnRightAction();
            case PEN_UP -> new PenUpAction();
            case PEN_DOWN -> new PenDownAction();
            case REPEAT -> new RepeatAction(4);
            case END_REPEAT -> new EndRepeatAction();
            case VAR_DECLARATION -> new VarDeclarationAction();
            case VAR_ASSIGNMENT -> new VarAssignmentAction();
            case INCREMENT_VARIABLE -> new IncrementVariableAction();
        };
    }

    public void newProgram() {
        clearProgram();
    }

    public void startAutoExecution(){
        stopAutoExecution();

        autoTimeline = new Timeline(
                new KeyFrame(Duration.seconds(speed.get()), e -> {
                    if (program.hasNext()){
                        executeNext();
                    } else {
                        stopAutoExecution();
                    }
                })
        );
        autoTimeline.setCycleCount(Timeline.INDEFINITE);
        autoTimeline.play();
    }

    public void stopAutoExecution(){
        if (autoTimeline != null){
            autoTimeline.stop();
            autoTimeline = null ;
        }
    }
    public void notifyProgramContentChanged() {
        errorMessage.set("");
        notifyProgramChanged();
    }

//----------------------- GETTERS POUR VUE ------------------------


    public ObservableList<Action> getObservableActions() {
        return observableActions;
    }

    public IntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex.get();
    }

    public ActionDetail getSelectedActionDetail() {
        Action action = getSelectedAction();
        if (action == null) return null;

        return new ActionDetail(
                action.getTitle(),
                action.isValueEditable(),
                action.getUnit(),
                action.getNumericValue()
        );
    }


    public int getExecutionFaultLineIndex() {
        return executionFaultLineIndex.get();
    }
    public ReadOnlyIntegerProperty executionFaultLineIndexProperty() {
        return executionFaultLineIndex;
    }


    public boolean tryUpdateSelectedActionWithText(String text) {
        Action action = getSelectedAction();
        if (action == null) return false;

        if (text.matches("^-?\\d+$")) {
            return action.updateValue(Integer.parseInt(text));
        }

        if (text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return action.updateVariable(text);
        }

        return false;
    }


    private void refreshVariablesFromContext() {
        observableVariables.clear();
        executionContext.getVariablesSnapshot()
                .forEach((name, val) -> observableVariables.add(new VariableRow(name, val)));
    }

    public ObservableList<VariableRow> getObservableVariables() {
        return observableVariables;
    }

    public IntegerProperty executionStepProperty() { return executionStep; }
    public ExecutionContext getExecutionContext()   { return executionContext; }
    public StringProperty errorMessageProperty()   { return errorMessage; }
    public BooleanProperty programLoadedProperty() { return programLoaded; }
    public BooleanProperty autoModeProperty() { return  autoMode; }
    public boolean isAutoMode() { return autoMode.get(); }
    public DoubleProperty speedProperty() { return speed;}
    public double getSpeed() { return speed.get(); }
}
```

**Explication des champs** :
- `program` → le modèle pur (la liste d'actions, la logique d'exécution)
- `observableActions` → une **copie observable** de la liste d'actions. C'est cette liste que la `ListView` de la vue observe. Quand on ajoute/supprime ici, la vue se met à jour toute seule
- `observableVariables` → pareil pour le tableau des variables

```java
// (Voir le code complet au début de cette section)
```

**Explication de chaque Property** :

| Property | Type | Rôle | Qui l'observe ? |
|---|---|---|---|
| `selectedIndex` | `int` | Index de l'action sélectionnée dans la liste (-1 = rien) | ListView, DetailPanel |
| `executionStep` | `int` | Compteur qui change à chaque étape → déclenche le redessin du canvas | SceneView (canvas) |
| `errorMessage` | `String` | Message d'erreur affiché en rouge | DetailPanelView |
| `programLoaded` | `boolean` | `true` quand le programme est chargé sur la scène | Boutons Suivant/Charger |
| `turtleState` | `String` | Texte "Tortue : x = 0, y = 0, direction = 0°" | Label dans SceneView |
| `autoMode` | `boolean` | `true` en mode exécution automatique | Radio buttons |
| `speed` | `double` | Vitesse en secondes entre chaque étape | Slider |
| `programChangeCounter` | `int` | Compteur qu'on incrémente quand le programme change → force le recalcul de `canLoad()` | Binding `canLoad()` |

### Le constructeur

```java
// (Voir le code complet au début de cette section)
```

### Section 1 : Gestion des actions

```java
// (Voir le code complet au début de cette section)
```

**Pourquoi on fait `program.insertAction()` ET `observableActions.add()` ?**
- `program.insertAction()` → modifie le **modèle** (les vraies données)
- `observableActions.add()` → modifie la **liste observable** que la vue affiche

Les deux listes doivent rester **synchronisées**. Si tu oublies l'une, soit la vue ne se met pas à jour, soit le modèle ne contient pas les bonnes données.

**Pourquoi `programLoaded.set(false)` ?**
Quand tu modifies le programme, l'exécution en cours n'est plus valide. On remet `programLoaded` à `false` pour que l'utilisateur doive re-cliquer "Charger" avant de pouvoir exécuter.

**Pourquoi `notifyProgramChanged()` ?**
Ça incrémente `programChangeCounter`, ce qui force le recalcul du binding `canLoad()`. Sans ça, le bouton "Charger" ne se mettrait pas à jour.

```java
// (Voir le code complet au début de cette section)
```

### Section 2 : Exécution

```java
// (Voir le code complet au début de cette section)
```

### Section 3 : Sauvegarde/Chargement fichiers

```java
// (Voir le code complet au début de cette section)
```

### Section 4 : Zone info tortue

```java
// (Voir le code complet au début de cette section)
```

### Section 5 : LES BINDINGS 🔥

> [!IMPORTANT]
> **C'est ICI que tout se connecte.** Chaque binding est une règle qui dit à la vue quand activer/désactiver un bouton.

```java
// (Voir le code complet au début de cette section)
```

**Décortiquons** :
1. `Bindings.isEmpty(observableActions)` → `true` si la liste est vide
2. `.not()` → on inverse : `true` si la liste n'est PAS vide
3. `.and(selectedIndex.greaterThanOrEqualTo(0))` → ET l'index sélectionné est >= 0
4. **Résultat** : `true` seulement si la liste contient des éléments ET quelque chose est sélectionné

```java
// (Voir le code complet au début de cette section)
```

**Décortiquons** :
1. La formule : `program.hasNext()` (vrai s'il reste des actions)
2. Les déclencheurs : `executionStep` et `programLoaded`
3. → Le binding se recalcule chaque fois qu'on exécute une action (`executionStep` change) ou qu'on charge le programme (`programLoaded` change)

```java
// (Voir le code complet au début de cette section)
```

```java
// (Voir le code complet au début de cette section)
```

**Pourquoi `programChangeCounter` ?**
Imagine : tu changes la valeur d'une action de 30 à 50. La liste `observableActions` n'a pas changé (même nombre d'éléments), donc le binding ne se recalculerait pas. Mais le programme est peut-être devenu invalide ! Le `programChangeCounter` sert de "signal manuel" : chaque fois qu'on le modifie, le binding se recalcule.

```java
// (Voir le code complet au début de cette section)
```

### Section 6 : Création d'actions

```java
// (Voir le code complet au début de cette section)
```

### Section 7 : Nouveau programme et exécution auto

```java
// (Voir le code complet au début de cette section)
```

### Section 8 : Détails d'action sélectionnée

```java
// (Voir le code complet au début de cette section)
```

### Section 9 : Mise à jour des valeurs d'action

```java
// (Voir le code complet au début de cette section)
```

### Section 10 : Variables et getters

```java
// (Voir le code complet au début de cette section)
```

---

# ÉTAPE 10 — PaletteView

## Fichier 22 : `PaletteView.java`

📁 `src/main/java/scratch/view/PaletteView.java`

### Pourquoi ?
C'est le panneau à **gauche** qui affiche la liste de toutes les actions disponibles (Avancer, Tourner, etc.). L'utilisateur y sélectionne un type d'action puis clique "Ajouter au programme".

### Code

```java
package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.model.ActionType;
import scratch.viewmodel.MainViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PaletteView extends VBox {
    private final ListView<ActionType> listView = new ListView<>();
    private final MainViewModel viewModel;

    public PaletteView(MainViewModel viewModel) {

        this.viewModel = viewModel;
        setSpacing(8);
        setPadding(new Insets(10));
        setPrefWidth(260);

        Label title = new Label("Palette d'actions");




        listView.setFixedCellSize(28);
        double maxHeight = 28 * 15;
        listView.setPrefHeight(maxHeight);
        listView.setMaxHeight(maxHeight);

        listView.setStyle(
                "-fx-border-color: #3FA9F5;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );

        listView.getItems().addAll(ActionType.values());

        // Cellule personnalisée
        listView.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(ActionType type, boolean empty) {


                    super.updateItem(type, empty);

                    if (empty || type == null) {
                        setGraphic(null);
                    } else {
                        Circle circle = new Circle(5);
                        Label label = new Label(getDisplayName(type));
                        Color color = switch (type) {
                            case MOVE_FORWARD -> Color.BLUE;
                            case TURN_LEFT, TURN_RIGHT -> Color.RED;
                            case PEN_UP, PEN_DOWN -> Color.GREEN;
                            case REPEAT , END_REPEAT , VAR_DECLARATION , VAR_ASSIGNMENT, INCREMENT_VARIABLE -> Color.LIGHTSEAGREEN;
                        };
                        circle.setFill(color);
                        label.setTextFill(color);

                        HBox box = new HBox(10, circle, label);
                        box.setAlignment(Pos.CENTER_LEFT);
                        box.setPadding(new Insets(5, 0, 5, 5));

                        setGraphic(box);
                    }

            }
        });

        Button addButton = new Button("Ajouter au programme");

        addButton.disableProperty().bind(
                listView.getSelectionModel().selectedItemProperty().isNull()
        );

        addButton.setOnAction(e -> {
            ActionType selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                viewModel.addAction(selected);
            }
        });

        // Double_clic
        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ActionType selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null){
                    viewModel.addAction(selected);
                }
            }
        });

        getChildren().addAll(title, listView, addButton);


    }

    public static String getDisplayName(ActionType type) {
        return switch (type) {
            case MOVE_FORWARD -> "Avancer de";
            case TURN_LEFT -> "Tourner à gauche de";
            case TURN_RIGHT -> "Tourner à droite de";
            case PEN_UP -> "Lever le stylo";
            case PEN_DOWN -> "Abaisser le stylo";
            case REPEAT -> "Repeter";
            case END_REPEAT -> "Fin Repeter";
            case VAR_ASSIGNMENT -> "Assignation";
            case VAR_DECLARATION -> "Déclaration variable";
            case INCREMENT_VARIABLE -> "Inc/Dec variable";
        };
    }
}
```

### Le binding expliqué

```java
// (Voir le code complet au début de cette section)
```

Traduit en français : **"Le bouton est désactivé QUAND rien n'est sélectionné dans la liste"**

- `selectedItemProperty()` → la Property qui contient l'élément sélectionné
- `.isNull()` → retourne `true` si rien n'est sélectionné
- `disableProperty().bind(...)` → le bouton `disable` suit cette valeur

---

# ÉTAPE 11 — ProgramView

## Fichier 23 : `ProgramView.java`

📁 `src/main/java/scratch/view/ProgramView.java`

### Pourquoi ?
C'est le panneau au **centre-gauche** qui affiche la liste des actions du programme. Il contient aussi les boutons Monter/Descendre/Dupliquer/Supprimer/Vider et le panneau de détails.

### Code

```java
package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;
import javafx.geometry.Pos;

public class ProgramView extends VBox {


    private final Button btnUp = new Button("Monter");
    private final Button btnDown = new Button("Descendre");
    private final Button btnDuplicate = new Button("Dupliquer");
    private final Button btnRemove = new Button("Supprimer");
    private final Button btnClear = new Button("Vider tout");
    private MainViewModel viewModel ;

    public ProgramView(MainViewModel viewModel) {

        this.viewModel = viewModel;

        setSpacing(10);
        setPadding(new Insets(10));
        setPrefWidth(450);
        setMaxWidth(450);

        Label title = new Label("Programme");

        ListView<Action> programList = new ListView<>();
        programList.setItems(viewModel.getObservableActions());
        addListeners();
        configurationBindings();

        // Quand on clique sur la liste, on met à jour l'index sélectionné dans le ViewModel
        programList.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0) {
                viewModel.selectedIndexProperty().set(newVal.intValue());
            }
        });
        viewModel.selectedIndexProperty().addListener((obs, old, nw) -> {
            programList.getSelectionModel().select(nw.intValue());
        });


        programList.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(Action action, boolean empty) {
                super.updateItem(action, empty);

                if (empty || action == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                    return;
                }

                Circle circle = new Circle(5);
                Label label = new Label();

                switch (action.getType()) {

                    case MOVE_FORWARD -> {
                        MoveForwardAction a = (MoveForwardAction) action;
                        circle.setFill(Color.BLUE);
                        label.setTextFill(Color.BLUE);
                        String displayVal = a.isVar() ? a.getVarName() : String.valueOf(a.getValue());
                        label.setText("Avancer de " + displayVal);
                    }

                    case TURN_LEFT -> {
                        TurnLeftAction a = (TurnLeftAction) action;
                        circle.setFill(Color.RED);
                        label.setTextFill(Color.RED);
                        String displayVal = a.isVar() ? a.getVarName() : String.valueOf(a.getValue());
                        label.setText("Tourner à gauche de " + displayVal);
                    }

                    case TURN_RIGHT -> {
                        TurnRightAction a = (TurnRightAction) action;
                        circle.setFill(Color.RED);
                        label.setTextFill(Color.RED);
                        String displayVal = a.isVar() ? a.getVarName() : String.valueOf(a.getValue());
                        label.setText("Tourner à droite de " + displayVal);
                    }

                    case PEN_UP -> {
                        circle.setFill(Color.GREEN);
                        label.setTextFill(Color.GREEN);
                        label.setText("Lever stylo");
                    }

                    case PEN_DOWN -> {
                        circle.setFill(Color.GREEN);
                        label.setTextFill(Color.GREEN);
                        label.setText("Abaisser stylo");
                    }
                    case REPEAT -> {
                        RepeatAction a = (RepeatAction) action;
                        circle.setFill(Color.LIGHTSEAGREEN);
                        label.setTextFill(Color.LIGHTSEAGREEN);
                        label.setText(a.toString());
                    }
                    case END_REPEAT -> {
                        circle.setFill(Color.LIGHTSEAGREEN);
                        label.setTextFill(Color.LIGHTSEAGREEN);
                        label.setText("Fin Repeter");
                    }
                    case VAR_DECLARATION -> {
                        VarDeclarationAction a = (VarDeclarationAction) action;
                        circle.setFill(Color.LIGHTSEAGREEN);
                        label.setTextFill(Color.LIGHTSEAGREEN);
                        label.setText(a.toString());
                    }
                    case INCREMENT_VARIABLE -> {
                        circle.setFill(Color.LIGHTSEAGREEN);
                        label.setTextFill(Color.LIGHTSEAGREEN);
                        label.setText(action.toString());
                    }
                    case VAR_ASSIGNMENT -> {
                        circle.setFill(Color.LIGHTSEAGREEN);
                        label.setTextFill(Color.LIGHTSEAGREEN);
                        label.setText(action.toString());
                    }
                }
                // Profondeur d indentation
                int depth = 0 ;
                int currentIdx = getIndex();
                for (int i = 0; i < currentIdx; i++) {
                    Action a = getListView().getItems().get(i);
                    if (a.getType() == ActionType.REPEAT)
                        depth++;
                    else if (a.getType() == ActionType.END_REPEAT)
                        depth--;
                }
                // EndRepeat au meme niveau que repeat
                if (action.getType() == ActionType.END_REPEAT)
                    depth--;
                if (depth < 0)
                    depth = 0 ;

                int leftPadding = 5 + (depth * 20);

                HBox box = new HBox(10, circle, label);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(5, 0, 5, leftPadding));

                setGraphic(box);
                setText(null);

                // Encadrer en rouge erreur
                boolean hasError = viewModel.errorMessageProperty().get() != null
                        && !viewModel.errorMessageProperty().get().isEmpty();
                int faultIdx = viewModel.getExecutionFaultLineIndex();
                boolean isFaultLine = faultIdx >= 0 && getIndex() == faultIdx;
                if (hasError && isFaultLine) {
                    setStyle("-fx-border-color: red; -fx-border-width: 2;");
                } else {
                    setStyle("");
                }
                setGraphic(box);
                setText(null);

            }
        });
        // Forcer le rrefresh qd nouvelle erreur
        viewModel.executionFaultLineIndexProperty().addListener((obs, o, n) -> programList.refresh());
        HBox buttons = new HBox(10);


        buttons.getChildren().addAll(
                btnUp,
                btnDown,
                btnDuplicate,
                btnRemove,
                btnClear
        );

        DetailPanelView detailPanel =
                new DetailPanelView(viewModel, programList);

        getChildren().addAll(
                title,
                programList,
                buttons,
                detailPanel
        );

    }
    // Listiners
    private void addListeners(){
        btnRemove.setOnAction(e -> viewModel.removeSelectedAction());
        btnClear.setOnAction(e -> viewModel.clearProgram());
        btnDown.setOnAction(e -> viewModel.moveDown());
        btnUp.setOnAction(e -> viewModel.moveUp());
        btnDuplicate.setOnAction(e -> viewModel.duplicateSelected());
    }

    // Bindins Button
    private void configurationBindings(){
        btnRemove.disableProperty().bind(viewModel.canRemove().not());
        btnClear.disableProperty().bind(Bindings.isEmpty(viewModel.getObservableActions()));
        btnUp.disableProperty().bind(viewModel.canMoveUp().not());
        btnDown.disableProperty().bind(viewModel.canMoveDown().not());
        btnDuplicate.disableProperty().bind(viewModel.canDuplicate().not());
    }
}
```

### Les bindings expliqués un par un

| Bouton | Binding | Traduit en français |
|---|---|---|
| Supprimer | `canRemove().not()` | Désactivé si la liste est vide OU rien sélectionné |
| Vider tout | `isEmpty(observableActions)` | Désactivé si la liste est déjà vide |
| Monter | `canMoveUp().not()` | Désactivé si l'élément sélectionné est déjà le premier |
| Descendre | `canMoveDown().not()` | Désactivé si l'élément sélectionné est le dernier |
| Dupliquer | `canDuplicate().not()` | Même règle que Supprimer |

### L'indentation des boucles expliquée

Pour chaque action affichée, on compte combien de `REPEAT` sont ouverts avant elle :

```
Déclaration variable nb    ← depth=0 → padding=5px
Assignation : nb = 5       ← depth=0 → padding=5px
Répéter nb fois            ← depth=0 → padding=5px  (le Repeat lui-même n'est pas indenté)
  Avancer de 30            ← depth=1 → padding=25px (un Repeat ouvert au-dessus)
  Tourner à gauche de 90   ← depth=1 → padding=25px
Fin répéter                ← depth=0 → padding=5px  (on décrémente avant d'afficher)
```

---

# ÉTAPE 12 — DetailPanelView

## Fichier 24 : `DetailPanelView.java`

📁 `src/main/java/scratch/view/DetailPanelView.java`

### Pourquoi ?
Quand tu sélectionnes une action dans la liste, ce panneau affiche les **détails** pour pouvoir la modifier. Par exemple :
- "Avancer de [30] Pixels" avec un champ éditable
- "Déclaration de la variable [var]" avec un champ texte
- "Assignation de la variable [nb] valeur : [5]" avec deux champs

### Code

```java
package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import scratch.model.Action;
import scratch.model.ParameterizedAction;
import scratch.viewmodel.ActionDetail;
import scratch.viewmodel.MainViewModel;

public class DetailPanelView extends TitledPane {

    private final MainViewModel viewModel;
    private final ListView<Action> programListView;

    private final Label lblDetailTitle = new Label("(aucune action sélectionnée)");
    private final TextField txtValue = new TextField();
    private final Label lblError = new Label("Error valeur");
    private final Label lblPixels = new Label("");
    private final Label lblRuntimeError = new Label();

    private final TextField txtTargetVar;
    private final Label lblAssignValue;
    private final Button btnPlus;
    private final Button btnMinus;

    private VBox detailPane = new VBox(5);
    private javafx.beans.value.ChangeListener<String> currentListener;
    private javafx.beans.value.ChangeListener<String> targetVarListener;

    public DetailPanelView(MainViewModel viewModel, ListView<Action> programListView) {
        this.viewModel = viewModel;
        this.programListView = programListView;

        txtTargetVar = new TextField();
        txtTargetVar.setMaxWidth(40);
        lblAssignValue = new Label(" valeur : ");
        btnPlus = new Button("+");
        btnMinus = new Button("-");

        txtTargetVar.setVisible(false);
        txtTargetVar.setManaged(false);
        lblAssignValue.setVisible(false);
        lblAssignValue.setManaged(false);
        btnPlus.setVisible(false);
        btnPlus.setManaged(false);
        btnMinus.setVisible(false);
        btnMinus.setManaged(false);

        setText("Détails de l'action");
        lblError.setStyle("-fx-text-fill: red;");
        lblError.setVisible(false);
        lblError.setManaged(false);
        txtValue.setVisible(false);
        txtValue.setManaged(false);
        txtValue.setMaxWidth(40);

        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(lblDetailTitle, txtTargetVar, lblAssignValue, txtValue, lblPixels, btnPlus, btnMinus);

        detailPane.getChildren().addAll(row, lblError);
        setContent(detailPane);

        lblRuntimeError.setStyle("-fx-text-fill: red; -fx-font-size: 15px;");
        lblRuntimeError.setWrapText(true);
        lblRuntimeError.textProperty().bind(viewModel.errorMessageProperty());
        detailPane.getChildren().add(lblRuntimeError);

        viewModel.selectedIndexProperty().addListener((obs, old, nw) -> updateDetailPane());
    }

    private void updateDetailPane() {
        detachTargetVarListener();

        if (currentListener != null) {
            txtValue.textProperty().removeListener(currentListener);
            currentListener = null;
        }

        lblError.setVisible(false);
        lblError.setManaged(false);

        // On cache tout par défaut
        txtTargetVar.setVisible(false);
        txtTargetVar.setManaged(false);
        lblAssignValue.setVisible(false);
        lblAssignValue.setManaged(false);
        btnPlus.setVisible(false);
        btnPlus.setManaged(false);
        btnMinus.setVisible(false);
        btnMinus.setManaged(false);

        Action action = viewModel.getSelectedAction();

        // --------DECLARATION --------
        if (action != null && action.getType() == scratch.model.ActionType.VAR_DECLARATION) {
            scratch.model.VarDeclarationAction varAction = (scratch.model.VarDeclarationAction) action;
            lblDetailTitle.setText("Déclaration de la variable ");

            txtValue.setDisable(false);
            txtValue.setVisible(true);
            txtValue.setManaged(true);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }

            txtValue.setText(varAction.getVarName());
            configVarTextField(varAction);
            return;
        }

        // -------- ASSIGNATION --------
        if (action != null && action.getType() == scratch.model.ActionType.VAR_ASSIGNMENT) {
            scratch.model.VarAssignmentAction assignAction = (scratch.model.VarAssignmentAction) action;

            lblDetailTitle.setText("Assignation de la variable ");
            lblAssignValue.setText(" valeur : "); // Texte de l'assignation

            txtTargetVar.setVisible(true);
            txtTargetVar.setManaged(true);
            lblAssignValue.setVisible(true);
            lblAssignValue.setManaged(true);
            txtValue.setVisible(true);
            txtValue.setManaged(true);
            txtValue.setDisable(false);
            btnPlus.setVisible(true);
            btnPlus.setManaged(true);
            btnMinus.setVisible(true);
            btnMinus.setManaged(true);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }

            txtTargetVar.setText(assignAction.getTargetVar());
            txtValue.setText(assignAction.getValue());
            configAssignFields(assignAction);
            return;
        }

        // -------- INCREMENTATION --------
        if (action != null && action.getType() == scratch.model.ActionType.INCREMENT_VARIABLE) {
            scratch.model.IncrementVariableAction incAction = (scratch.model.IncrementVariableAction) action;

            lblDetailTitle.setText("Incrémentation de la variable "); // Texte de ta photo
            lblAssignValue.setText(" de "); // Texte de ta photo

            txtTargetVar.setVisible(true);
            txtTargetVar.setManaged(true);
            lblAssignValue.setVisible(true);
            lblAssignValue.setManaged(true);
            txtValue.setVisible(true);
            txtValue.setManaged(true);
            txtValue.setDisable(false);
            btnPlus.setVisible(true);
            btnPlus.setManaged(true);
            btnMinus.setVisible(true);
            btnMinus.setManaged(true);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }

            txtTargetVar.setText(incAction.getTargetVar());
            txtValue.setText(incAction.getValue());
            configIncFields(incAction);
            return;
        }

        // -------- LE RESTE DES ACTIONS --------
        ActionDetail detail = viewModel.getSelectedActionDetail();

        if (detail == null) {
            lblDetailTitle.setText("(aucune action sélectionnée)");
            txtValue.setText("");
            txtValue.setDisable(true);
            txtValue.setVisible(false);
            txtValue.setManaged(false);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);
            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }
            return;
        }

        lblDetailTitle.setText(detail.getTitle());

        if (!detail.isValueEditable()) {
            txtValue.setText("0");
            txtValue.setDisable(true);
            txtValue.setVisible(false);
            txtValue.setManaged(false);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }
            lblError.setVisible(false);
            lblError.setManaged(false);
            return;
        }

        txtValue.setDisable(false);
        txtValue.setVisible(true);
        txtValue.setManaged(true);
        lblPixels.setVisible(true);
        lblPixels.setManaged(true);
        lblPixels.setText(detail.getUnitText());

        txtValue.setText(action.getExpression());

        configTextField();
    }

    private void configVarTextField(scratch.model.VarDeclarationAction action) {
        lblError.setVisible(false);
        lblError.setManaged(false);

        currentListener = ((obs, old, text) -> {
            if (text == null || text.isBlank() || !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                lblError.setText("Erreur valeur");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setVarName(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        });
        txtValue.textProperty().addListener(currentListener);
    }

    private void configTextField() {
        lblError.setVisible(false);
        lblError.setManaged(false);

        if (currentListener != null){
            txtValue.textProperty().removeListener(currentListener);
        }

        currentListener = ((obs, old, text) -> {
            lblError.setVisible(false);
            lblError.setManaged(false);

            if (text == null || text.isBlank()) {
                lblError.setVisible(true);
                lblError.setManaged(true);
                return;
            }


            boolean ok = viewModel.tryUpdateSelectedActionWithText(text);

            if (!ok) {
                lblError.setText("Erreur : Valeur invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        });
        txtValue.textProperty().addListener(currentListener);
    }

    // --- CONFIGURATION ASSIGNATION ---
    private void configAssignFields(scratch.model.VarAssignmentAction action) {
        lblError.setVisible(false);
        lblError.setManaged(false);

            targetVarListener = (obs, old, text) ->{
            if (text == null || text.isBlank() || !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                lblError.setText("Erreur : Nom cible invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setTargetVar(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        };
        txtTargetVar.textProperty().addListener(targetVarListener);

        currentListener = ((obs, old, text) -> {
            if (text == null || text.isBlank() || (!text.matches("^-?\\d+$") && !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
                lblError.setText("Erreur : Valeur invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setValue(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        });
        txtValue.textProperty().addListener(currentListener);

        btnPlus.setOnAction(null);
        btnMinus.setOnAction(null);
        btnPlus.setOnAction(e -> updateNumericValueAssign(action, 1));
        btnMinus.setOnAction(e -> updateNumericValueAssign(action, -1));
    }

    private void updateNumericValueAssign(scratch.model.VarAssignmentAction action, int delta) {
        try {
            int currentVal = Integer.parseInt(txtValue.getText());
            int newVal = currentVal + delta;
            txtValue.setText(String.valueOf(newVal));
        } catch (NumberFormatException ex) {}
    }

    // --- CONFIGURATION INCREMENTATION ---
    private void configIncFields(scratch.model.IncrementVariableAction action) {
        lblError.setVisible(false);
        lblError.setManaged(false);

        targetVarListener = (obs, old, text) -> {
            if (text == null || text.isBlank() || !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                lblError.setText("Erreur : Nom cible invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setTargetVar(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        };
        txtTargetVar.textProperty().addListener(targetVarListener);

        currentListener = ((obs, old, text) -> {
            if (text == null || text.isBlank() || (!text.matches("^-?\\d+$") && !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
                lblError.setText("Erreur : Valeur invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setValue(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        });
        txtValue.textProperty().addListener(currentListener);

        btnPlus.setOnAction(null);
        btnMinus.setOnAction(null);
        btnPlus.setOnAction(e -> updateNumericValueInc(action, 1));
        btnMinus.setOnAction(e -> updateNumericValueInc(action, -1));
    }

    private void updateNumericValueInc(scratch.model.IncrementVariableAction action, int delta) {
        try {
            int currentVal = Integer.parseInt(txtValue.getText());
            int newVal = currentVal + delta;
            txtValue.setText(String.valueOf(newVal));
        } catch (NumberFormatException ex) {}
    }

    private void detachTargetVarListener() {
        if (targetVarListener != null) {
            txtTargetVar.textProperty().removeListener(targetVarListener);
            targetVarListener = null;
        }
    }
}
```

> [!WARNING]
> **Pourquoi on fait `removeListener` avant d'ajouter un nouveau ?**
> Si tu ne retires pas l'ancien listener, il continue d'écouter ! Quand tu sélectionnes une nouvelle action et que tu tapes dans le champ, l'ancien listener modifie **l'ancienne action** au lieu de la nouvelle. C'est un bug classique de fuite de listener.

---

# ÉTAPE 13 — SceneView

## Fichier 25 : `SceneView.java`

📁 `src/main/java/scratch/view/SceneView.java`

### Pourquoi ?
C'est le panneau à **droite** qui contient :
- Le **canvas** (zone de dessin de la tortue)
- Les infos de la tortue (position, direction)
- Le tableau des variables
- Les boutons Charger/Suivant
- Le mode Manuel/Automatique avec slider de vitesse

### Code

```java
package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import scratch.model.ExecutionContext;
import scratch.model.Segment;
import scratch.viewmodel.*;

public class SceneView extends VBox {

    private static final double SIZE = 500;
    private static final int GRID = 50;
    private final Button btnNext = new Button("Suivant");
    private final Button btnReset = new Button("Charger");
    private final Canvas canvas;
    private final MainViewModel viewModel;
    private final Label lblTurtle = new Label();
    private final Label lblVariablesTitle = new Label("Variables");
    private final TableView<VariableRow> tableVariables = new TableView<>();
    private final Label lblError = new Label();
    private final RadioButton rbManual = new RadioButton("Execution manuelle");
    private final RadioButton rbAuto = new RadioButton("Execution automatique");
    private final ToggleGroup modelGroup = new ToggleGroup();
    private final Slider speedSlider = new Slider(0.01 , 5.0 , 1.0);
    private final Label speedLabel = new Label("1.0 s");
    private final Button btnExecute = new Button("Executer");
    private final Button btnStop = new Button("Arreter");

    public SceneView(MainViewModel viewModel) {
        this.viewModel = viewModel;
        setSpacing(8);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Scène");

        canvas = new Canvas(SIZE, SIZE);

        StackPane canvasBox = new StackPane(canvas);
        canvasBox.setPrefSize(SIZE, SIZE);
        canvasBox.setMaxSize(SIZE, SIZE);
        canvasBox.setMinSize(SIZE, SIZE);
        canvasBox.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        drawGrid();




        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(btnReset, btnNext);
        rbManual.setToggleGroup(modelGroup);
        rbAuto.setToggleGroup(modelGroup);
        rbManual.setSelected(true);
        btnExecute.setVisible(false);
        btnExecute.setManaged(false);
        btnStop.setVisible(false);
        btnStop.setManaged(false);
        speedSlider.setVisible(false);
        speedSlider.setManaged(false);
        speedLabel.setVisible(false);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(speedSlider, javafx.scene.layout.Priority.ALWAYS);


        modelGroup.selectedToggleProperty().addListener((obs, old , nw) ->{
            boolean isAuto = (nw == rbAuto);
            viewModel.autoModeProperty().set(isAuto);

            // Slider vitesse
            speedSlider.setVisible(isAuto);
            speedSlider.setManaged(isAuto);
            speedLabel.setVisible(isAuto);
            speedLabel.setManaged(isAuto);

            // Boutons auto (Exécuter / Arrêter)
            btnExecute.setVisible(isAuto);
            btnExecute.setManaged(isAuto);
            btnStop.setVisible(isAuto);
            btnStop.setManaged(isAuto);

            // Bouton manuel (Suivant)
            btnNext.setVisible(!isAuto);
            btnNext.setManaged(!isAuto);

            if (!isAuto) viewModel.stopAutoExecution();
        });


        speedSlider.valueProperty().addListener((obs , old , nw) ->{
            viewModel.speedProperty().set(nw.doubleValue());
            speedLabel.setText(String.format("%.2f s", nw.doubleValue()));
            if (viewModel.isAutoMode() && viewModel.programLoadedProperty().get()){
                viewModel.startAutoExecution();
            }
        });
        btnExecute.setOnAction(e -> viewModel.startAutoExecution());
        btnStop.setOnAction(e-> viewModel.stopAutoExecution());



        //Zone info
        TableColumn<VariableRow, String> colName = new TableColumn<>("Nom");
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        TableColumn<VariableRow, Number> colValue = new TableColumn<>("Valeur");
        colValue.setCellValueFactory(data -> data.getValue().valueProperty());
        tableVariables.getColumns().addAll(colName, colValue);
        tableVariables.setItems(viewModel.getObservableVariables());
        tableVariables.setPlaceholder(new Label("Aucun contenu dans la table"));

        lblTurtle.textProperty().bind(viewModel.turtleStateProperty());
        lblTurtle.setStyle("-fx-font-size: 12px;");
        lblVariablesTitle.setStyle("-fx-font-weight: bold;");

        HBox modeBox = new HBox(10, rbAuto, rbManual);
        modeBox.setAlignment(Pos.CENTER);

        HBox speedBox = new HBox(speedSlider);
        speedBox.setMaxWidth(Double.MAX_VALUE);


        HBox buttonsBox = new HBox(10, btnReset, btnExecute, btnStop, btnNext);
        buttonsBox.setAlignment(Pos.CENTER);



        getChildren().addAll(
                title,
                canvasBox,
                lblTurtle,
                lblVariablesTitle,
                tableVariables,
                modeBox,
                buttonsBox,
                speedBox);



        configActions();
        configButtonsDisabling();
        viewModel.executionStepProperty().addListener((obs, oldVal, newVal) -> drawGrid());

    }

//Connection excution action
    private void configActions() {
        btnReset.setOnAction(e -> {
            if (viewModel.programLoadedProperty().get()) {
                viewModel.resetExecution();
                viewModel.loadOnScene();
            } else {
               viewModel.loadOnScene();
            }
            drawGrid();


        });
        btnNext.setOnAction(e -> {
            viewModel.executeNext();
            drawGrid();
        });
    }

//connextion Desacitver bouttons
    private void configButtonsDisabling() {
        btnReset.disableProperty().bind(
                viewModel.canLoad().not());
        btnNext.disableProperty().bind(
                viewModel.programLoadedProperty().not()
                        .or(viewModel.canExecuteNext().not()));
        viewModel.programLoadedProperty().addListener((obs, old, nw) ->
                btnReset.setText(nw ? "Ré-initialiser" : "Charger"));
        btnExecute.disableProperty().bind(viewModel.canExecuteNext().not());
        btnStop.disableProperty().bind(viewModel.canExecuteNext().not());
    }



    private void drawGrid() {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        ExecutionContext ctx = viewModel.getExecutionContext();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, SIZE, SIZE);

        gc.setStroke(Color.web("#E6E6E6"));
        gc.setLineWidth(1);

        for (int x = 0; x <= SIZE; x += GRID) {
            gc.strokeLine(x + 0.5, 0, x + 0.5, SIZE);
        }

        for (int y = 0; y <= SIZE; y += GRID) {
            gc.strokeLine(0, y + 0.5, SIZE, y + 0.5);
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, SIZE, SIZE);

        //dessin du segment
        for (Segment seg : ctx.getSegments()) {
            gc.setStroke(Color.RED);
            gc.strokeLine(seg.getX1(), seg.getY1(), seg.getX2(), seg.getY2());

        }
        drawCursor(gc, ctx.getX(), ctx.getY(), ctx.getDirection());
    }

    private void drawCursor(GraphicsContext gc, int x, int y, int direction) {


        gc.save();
        gc.translate(x, y);
        gc.rotate(direction);

        double s = 10;
        gc.setFill(Color.CYAN);
        gc.fillPolygon(
                new double[]{0, -s * 0.8, s * 0.8},
                new double[]{-s, s * 0.8, s * 0.8}, 3);
        gc.setFill(Color.BLACK);
        gc.fillOval(-2, -s - 2, 4, 4);

        gc.restore();


    }
}
```

### Le binding Suivant expliqué en détail

```java
// (Voir le code complet au début de cette section)
```

Traduit : **"Le bouton Suivant est désactivé SI le programme n'est PAS chargé OU s'il n'y a PLUS d'action à exécuter"**

Schéma de vérité :

| programLoaded | hasNext | Bouton |
|---|---|---|
| `false` | peu importe | ❌ Désactivé |
| `true` | `false` | ❌ Désactivé |
| `true` | `true` | ✅ Actif |

### Le listener qui change le texte du bouton

```java
// (Voir le code complet au début de cette section)
```

- Quand le programme **n'est pas** chargé → bouton dit "Charger"
- Quand le programme **est** chargé → bouton dit "Ré-initialiser"

---

# ÉTAPE 14 — MainView

## Fichier 26 : `MainView.java`

📁 `src/main/java/scratch/view/MainView.java`

### Pourquoi ?
C'est la vue **principale** qui assemble tout le layout. Elle utilise un `BorderPane` :
- **Haut** : la barre de menu (Fichier > Nouveau, Ouvrir, Sauvegarder, Quitter)
- **Gauche** : la Palette
- **Centre** : le Programme + la Scène côte à côte

### Code

```java
package scratch.view;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import scratch.model.Action;
import scratch.viewmodel.MainViewModel;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.File;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

    private final MainViewModel viewModel;

    private final ListView<Action> programListView = new ListView<>();


    public MainView(MainViewModel viewModel) {
        this.viewModel = viewModel;

        PaletteView palette = new PaletteView(viewModel);
        ProgramView program = new ProgramView(viewModel);
        SceneView scene = new SceneView(viewModel);

        HBox center = new HBox(20);
        center.getChildren().addAll(program, scene);

        programListView.setItems(viewModel.getObservableActions());

        programListView.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, nw) ->
                        viewModel.selectedIndexProperty().setValue(nw.intValue()));

        viewModel.selectedIndexProperty()
                .addListener((obs, old, nw) ->
                        programListView.getSelectionModel().select(nw.intValue()));

        setTop(createMenuBar());
        setLeft(palette);
        setCenter(center);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New...");
        newItem.setOnAction(e -> viewModel.newProgram());

        MenuItem openItem = new MenuItem("Open...");
        openItem.setOnAction(e -> {
            FileChooser fileChooser = createScratchFileChooser("Ouvrir");
            File file = fileChooser.showOpenDialog(getScene().getWindow());

            if (file != null) {
                viewModel.loadFromFile(file);
            }
        });

        MenuItem saveItem = new MenuItem("Save As...");
        saveItem.setOnAction(e -> {
            FileChooser fileChooser = createScratchFileChooser("Enregistrer sous");
            fileChooser.setInitialFileName("programme.scr");

            File file = fileChooser.showSaveDialog(getScene().getWindow());

            if (file != null) {
                if (!file.getName().toLowerCase().endsWith(".scr")) {
                    file = new File(file.getAbsolutePath() + ".scr");
                }
                viewModel.saveToFile(file);
            }
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    private FileChooser createScratchFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Scratch files (*.scr)", "*.scr")
        );

        File defaultDir = new File("data");

        if (defaultDir.exists() && defaultDir.isDirectory()) {
            fileChooser.setInitialDirectory(defaultDir);
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        return fileChooser;
    }
}
```

---

# ÉTAPE 15 — App.java

## Fichier 27 : `App.java`

📁 `src/main/java/scratch/App.java`

### Pourquoi ?
C'est le **point d'entrée** de l'application. Il crée les 3 couches dans l'ordre :
1. **Model** : `Program`
2. **ViewModel** : `MainViewModel(program)`
3. **View** : `MainView(viewModel)`

### Code

```java
package scratch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scratch.model.Program;
import scratch.view.MainView;
import scratch.viewmodel.MainViewModel;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        Program program = new Program();

        MainViewModel viewModel = new MainViewModel(program);

        MainView mainView = new MainView(viewModel);

        Scene scene = new Scene(mainView, 1400, 800);

        primaryStage.setTitle("Scratch");
        primaryStage.setScene(scene);

        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

---

# 🎯 Récapitulatif : Tous les bindings du projet

## Schéma de connexion complet

```
┌─────────────── VIEWMODEL ───────────────┐      ┌─────────────── VUE ─────────────────┐
│                                          │      │                                      │
│  observableActions ──────────────────────┼──→──┤  ListView (ProgramView)               │
│                                          │      │  ListView (PaletteView)               │
│                                          │      │                                      │
│  selectedIndex ──────────────────────────┼──↔──┤  ListView.selection (bidirectionnel)  │
│                                          │      │                                      │
│  canRemove() ────────────────────────────┼──→──┤  btnRemove.disable                   │
│  canMoveUp() ────────────────────────────┼──→──┤  btnUp.disable                       │
│  canMoveDown() ──────────────────────────┼──→──┤  btnDown.disable                     │
│  canDuplicate() ─────────────────────────┼──→──┤  btnDuplicate.disable                │
│  canLoad() ──────────────────────────────┼──→──┤  btnReset.disable                    │
│  canExecuteNext() ───────────────────────┼──→──┤  btnNext.disable (+ programLoaded)   │
│                                          │      │                                      │
│  programLoaded ──────────────────────────┼──→──┤  btnReset.text (Charger/Réinit)      │
│                  ────────────────────────┼──→──┤  btnExecute.disable                  │
│                                          │      │                                      │
│  errorMessage ───────────────────────────┼──→──┤  lblRuntimeError.text                │
│  turtleState ────────────────────────────┼──→──┤  lblTurtle.text                      │
│  executionStep ──────────────────────────┼──→──┤  Canvas (drawGrid redessine)         │
│  observableVariables ────────────────────┼──→──┤  TableView (variables)               │
│                                          │      │                                      │
└──────────────────────────────────────────┘      └──────────────────────────────────────┘
```

---

# ✅ Ordre de création des fichiers

| # | Fichier | Package |
|---|---|---|
| 1 | `ActionType.java` | model |
| 2 | `ExecutionException.java` | model |
| 3 | `Segment.java` | model |
| 4 | `Action.java` | model |
| 5 | `ParameterizedAction.java` | model |
| 6 | `MoveForwardAction.java` | model |
| 7 | `TurnLeftAction.java` | model |
| 8 | `TurnRightAction.java` | model |
| 9 | `PenUpAction.java` | model |
| 10 | `PenDownAction.java` | model |
| 11 | `VarDeclarationAction.java` | model |
| 12 | `VarAssignmentAction.java` | model |
| 13 | `IncrementVariableAction.java` | model |
| 14 | `RepeatAction.java` | model |
| 15 | `EndRepeatAction.java` | model |
| 16 | `ExecutionContext.java` | model |
| 17 | `Program.java` | model |
| 18 | `ProgramFileService.java` | model |
| 19 | `ActionDetail.java` | viewmodel |
| 20 | `VariableRow.java` | viewmodel |
| 21 | `MainViewModel.java` | viewmodel |
| 22 | `PaletteView.java` | view |
| 23 | `ProgramView.java` | view |
| 24 | `DetailPanelView.java` | view |
| 25 | `SceneView.java` | view |
| 26 | `MainView.java` | view |
| 27 | `App.java` | scratch |
