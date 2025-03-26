import matplotlib.pyplot as plt
import numpy as np

def parse_file(filename):
    """
    Lit le fichier de benchmark et extrait pour chaque bloc la valeur utilisée (max_depth ou max_nodes)
    et le temps moyen (en ns).
    
    On s'attend à trouver dans le fichier des lignes de la forme :
      "Benchmarking ... with max_depth: <value>, depth: <...>"
    ou
      "Benchmarking ... with max_nodes: <value>, depth: <...>"
    et
      "Average time for ... at depth <...>: <average_time> ns"
    """
    xs = []
    average_times = []
    current_x = None
    with open(filename, 'r') as file:
        for line in file:
            line = line.strip()
            if line.startswith("Benchmarking"):
                if "max_depth:" in line:
                    parts = line.split("max_depth:")
                elif "max_nodes:" in line:
                    parts = line.split("max_nodes:")
                else:
                    continue
                if len(parts) >= 2:
                    remainder = parts[1].strip()
                    parts2 = remainder.split(",")
                    try:
                        current_x = float(parts2[0].strip())
                    except ValueError:
                        continue
            elif line.startswith("Average time"):
                parts = line.split(":")
                if len(parts) >= 2 and current_x is not None:
                    try:
                        avg_time = float(parts[1].strip().split()[0])
                        xs.append(current_x)
                        average_times.append(avg_time)
                    except ValueError:
                        continue
    return xs, average_times

def plot_balanced():
    # Fichiers pour arbres équilibrés
    file_tree = "benchmark_results_balanced_tree.txt"
    file_oldtree = "benchmark_results_balanced_oldtree.txt"
    
    max_depths_tree, avg_times_tree = parse_file(file_tree)
    max_depths_oldtree, avg_times_oldtree = parse_file(file_oldtree)
    
    # Tri des données par max_depth
    data_tree = sorted(zip(max_depths_tree, avg_times_tree))
    data_oldtree = sorted(zip(max_depths_oldtree, avg_times_oldtree))
    max_depths_tree, avg_times_tree = zip(*data_tree)
    max_depths_oldtree, avg_times_oldtree = zip(*data_oldtree)
    
    # Conversion de max_depth en nombre de nœuds pour les arbres équilibrés: n = 2^(d+1)-1
    nodes_tree = [2**(d+1)-1 for d in max_depths_tree]
    nodes_oldtree = [2**(d+1)-1 for d in max_depths_oldtree]
    
    # Conversion des temps de ns en µs (1 µs = 1e3 ns)
    times_tree = [t / 1e3 for t in avg_times_tree]
    times_oldtree = [t / 1e3 for t in avg_times_oldtree]
    
    # Régression polynomiale de degré 2 pour chaque série
    degree = 2
    coef_tree = np.polyfit(nodes_tree, times_tree, degree)
    poly_tree = np.poly1d(coef_tree)
    fitted_tree = poly_tree(nodes_tree)
    
    coef_oldtree = np.polyfit(nodes_oldtree, times_oldtree, degree)
    poly_oldtree = np.poly1d(coef_oldtree)
    fitted_oldtree = poly_oldtree(nodes_oldtree)
    
    # Affichage des équations dans la console
    print("Balanced Tree.design() - équation ajustée (degré 2) :")
    print(poly_tree)
    print("Balanced OldTree.design() - équation ajustée (degré 2) :")
    print(poly_oldtree)
    
    # Tracé du graphique
    plt.figure(figsize=(10, 6))
    plt.plot(nodes_tree, times_tree, marker='o', linestyle='-', label='Balanced Tree.design()')
    plt.plot(nodes_tree, fitted_tree, 'r--', label='Ajustement Tree (deg2)')
    plt.plot(nodes_oldtree, times_oldtree, marker='s', linestyle='-', label='Balanced OldTree.design()')
    plt.plot(nodes_oldtree, fitted_oldtree, 'b--', label='Ajustement OldTree (deg2)')
    
    plt.xlim(left=0)
    plt.ylim(bottom=0)
    plt.ticklabel_format(style='plain', axis='both')
    plt.xlabel('Nombre de nœuds')
    plt.ylabel('Temps moyen (µs)')
    plt.title('Benchmark Balanced Trees')
    plt.legend()
    plt.grid(True, which="both", ls="--", lw=0.5)
    plt.tight_layout()
    plt.savefig("benchmark_results_balanced.png")
    plt.show()

def plot_degenerated():
    # Fichiers pour arbres dégénérés
    file_tree = "benchmark_results_degenerated_tree.txt"
    file_oldtree = "benchmark_results_degenerated_oldtree.txt"
    
    xs_tree, avg_times_tree = parse_file(file_tree)
    xs_oldtree, avg_times_oldtree = parse_file(file_oldtree)
    
    # Ici, xs représentent directement le nombre de nœuds.
    nodes_tree = xs_tree
    nodes_oldtree = xs_oldtree
    
    # Conversion des temps de ns en µs
    times_tree = [t / 1e3 for t in avg_times_tree]
    times_oldtree = [t / 1e3 for t in avg_times_oldtree]
    
    # Régression polynomiale de degré 2 pour chaque série
    degree = 2
    coef_tree = np.polyfit(nodes_tree, times_tree, degree)
    poly_tree = np.poly1d(coef_tree)
    fitted_tree = poly_tree(nodes_tree)
    
    coef_oldtree = np.polyfit(nodes_oldtree, times_oldtree, degree)
    poly_oldtree = np.poly1d(coef_oldtree)
    fitted_oldtree = poly_oldtree(nodes_oldtree)
    
    # Affichage des équations dans la console
    print("Degenerated Tree.design() - équation ajustée (degré 2) :")
    print(poly_tree)
    print("Degenerated OldTree.design() - équation ajustée (degré 2) :")
    print(poly_oldtree)
    
    # Tracé du graphique : points et courbes ajustées en pointillé
    plt.figure(figsize=(10, 6))
    plt.scatter(nodes_tree, times_tree, marker='o', label='Degenerated Tree.design()')
    plt.plot(nodes_tree, fitted_tree, 'g--', label='Ajustement Tree (deg2)')
    
    plt.scatter(nodes_oldtree, times_oldtree, marker='s', label='Degenerated OldTree.design()')
    plt.plot(nodes_oldtree, fitted_oldtree, 'm--', label='Ajustement OldTree (deg2)')
    
    plt.xlim(left=0)
    plt.ylim(bottom=0)
    plt.ticklabel_format(style='plain', axis='both')
    plt.xlabel('Nombre de nœuds')
    plt.ylabel('Temps moyen (µs)')
    plt.title('Benchmark Degenerated Trees')
    plt.legend()
    plt.grid(True, which="both", ls="--", lw=0.5)
    plt.tight_layout()
    plt.savefig("benchmark_results_degenerated.png")
    plt.show()

def main():
    plot_balanced()
    plot_degenerated()

if __name__ == '__main__':
    main()
