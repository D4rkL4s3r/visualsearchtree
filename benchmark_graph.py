import matplotlib.pyplot as plt

def parse_file(filename):
    """
    Lit le fichier de benchmark et extrait pour chaque bloc la profondeur et le temps moyen (en ns).
    On s'attend à trouver dans le fichier des lignes de la forme :
      "Benchmarking ... with depth: <depth>"
    et
      "Average time for ... at depth <depth>: <average_time> ns"
    """
    depths = []
    average_times = []
    current_depth = None
    with open(filename, 'r') as file:
        for line in file:
            line = line.strip()
            # Détection de la profondeur utilisée
            if line.startswith("Benchmarking"):
                # Exemple de ligne : "Benchmarking Tree.design() with depth: 100"
                parts = line.split("with depth:")
                if len(parts) >= 2:
                    try:
                        current_depth = float(parts[1].strip())
                    except ValueError:
                        continue
            elif line.startswith("Average time"):
                # Exemple de ligne : "Average time for Tree.design() at depth 100: 123456 ns"
                parts = line.split(":")
                if len(parts) >= 2 and current_depth is not None:
                    try:
                        avg_time = float(parts[1].strip().split()[0])
                        depths.append(current_depth)
                        average_times.append(avg_time)
                    except ValueError:
                        continue
    return depths, average_times

def main():
    # Fichiers de résultats (assurez-vous qu'ils se trouvent dans le même répertoire que ce script)
    tree_file = "benchmark_results_tree.txt"
    oldtree_file = "benchmark_results_oldtree.txt"

    # Extraction des données
    depths_tree, avg_times_tree = parse_file(tree_file)
    depths_oldtree, avg_times_oldtree = parse_file(oldtree_file)

    # On trie les données par profondeur (si ce n'est pas déjà le cas)
    tree_data = sorted(zip(depths_tree, avg_times_tree))
    oldtree_data = sorted(zip(depths_oldtree, avg_times_oldtree))
    depths_tree, avg_times_tree = zip(*tree_data)
    depths_oldtree, avg_times_oldtree = zip(*oldtree_data)

    # Création du graphique
    plt.figure(figsize=(10, 6))

    # Tracé des courbes pour chaque benchmark
    plt.plot(depths_tree, avg_times_tree, marker='o', linestyle='-', label='Tree.design()')
    plt.plot(depths_oldtree, avg_times_oldtree, marker='s', linestyle='-', label='OldTree.design()')

    # Utilisation d'échelles logarithmiques
    plt.xscale('log')
    plt.yscale('log')

    plt.xlabel('Profondeur')
    plt.ylabel('Temps moyen (ns)')
    plt.title('Résultats des benchmarks')
    plt.legend()
    plt.grid(True, which="both", ls="--", lw=0.5)
    plt.tight_layout()

    # Sauvegarde du graphique dans un fichier PNG
    plt.savefig("benchmark_results.png")
    plt.show()

if __name__ == '__main__':
    main()
