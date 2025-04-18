import json

def generate_binary_tree_json(depth):
    nodes = []
    current_id = 1

    # Ajouter la racine
    nodes.append({
        "id": current_id,
        "parentId": None,
        "type": "INNER",
        "info": f"Node{current_id}"
    })

    # Générer les nœuds niveau par niveau
    queue = [1]
    while queue and depth > 0:
        level_size = len(queue)
        for _ in range(level_size):
            parent_id = queue.pop(0)
            # Ajouter deux enfants
            left_id = current_id + 1
            right_id = current_id + 2
            current_id += 2

            # Type aléatoire ou INNER pour les nœuds internes
            node_type = "INNER"
            if depth == 1:  # Dernier niveau : ajouter des nœuds FAIL ou SOLUTION
                node_type = "FAIL" if left_id % 2 == 0 else "SOLUTION"

            nodes.append({
                "id": left_id,
                "parentId": parent_id,
                "type": node_type,
                "info": f"Node{left_id}"
            })
            nodes.append({
                "id": right_id,
                "parentId": parent_id,
                "type": node_type,
                "info": f"Node{right_id}"
            })

            queue.append(left_id)
            queue.append(right_id)

        depth -= 1

    return nodes

# Générer un arbre de profondeur 10 (1023 nœuds)
tree_nodes = generate_binary_tree_json(10)

# Écrire dans un fichier JSON
with open("large_tree.json", "w") as f:
    json.dump(tree_nodes, f, indent=4)

print(f"Arbre généré avec {len(tree_nodes)} nœuds")