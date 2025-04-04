# Changement apporté au code

## Simplification de la classe Extent

- **Ancien code**

La classe *Extent* était une liste de Pair représentant les contours des sous arbres à diffèrents niveaux. Pour un arbre de profondeur d, cela pouvait contenir jusqu'à d paire.

- **Nouveau code**

Maintenant la classe Extent est une classe simple avec trois champs : *basePosition*, *left* et *right*, représentant une seule pair de limites relatives de base.

**Impact** : Cette simplification réduit la taille de chaque Extent à une copnstante, rendant les opérations associées plus rapides.

## Optimisation des méthodes *fit* et *merge*

- **Ancien code**

Ces méthodes devaient parcourir les listes de paires, avec une compléxité proportionnelle à la taille de ces listes pouvant atteindre O(n) dans le pire cas.

- **Nouveau code**

Maintenant, la méthode *fit* calcule directement la distance nécéssaire entre deux Extent en utilisant leurs champs *left*, *right* et *basePosition* en un temps constant (O(1)). La méthode *merge*  combine les deux Extent en une seule paire englobante en comparants les limites, ce qui se fait aussi en temps constant.

**Impact** : Ces opérations sont maintenant constantes, ce qui est essentiel pour une complexité globale linéaire.