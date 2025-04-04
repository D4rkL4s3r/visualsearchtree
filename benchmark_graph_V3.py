import matplotlib.pyplot as plt
import numpy as np

def parse_file(filename):
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

def get_balanced_data():
    file_tree = "benchmark_results_balanced_tree.txt"
    file_oldtree = "benchmark_results_balanced_oldtree.txt"
    
    max_depths_tree, avg_times_tree = parse_file(file_tree)
    max_depths_oldtree, avg_times_oldtree = parse_file(file_oldtree)
    
    data_tree = sorted(zip(max_depths_tree, avg_times_tree))
    data_oldtree = sorted(zip(max_depths_oldtree, avg_times_oldtree))
    max_depths_tree, avg_times_tree = zip(*data_tree)
    max_depths_oldtree, avg_times_oldtree = zip(*data_oldtree)
    
    nodes_tree = [2**(d+1)-1 for d in max_depths_tree]
    nodes_oldtree = [2**(d+1)-1 for d in max_depths_oldtree]
    
    times_tree = [t / 1e3 for t in avg_times_tree]
    times_oldtree = [t / 1e3 for t in avg_times_oldtree]
    
    return nodes_tree, times_tree, nodes_oldtree, times_oldtree

def get_degenerated_data():
    file_tree = "benchmark_results_degenerated_tree.txt"
    file_oldtree = "benchmark_results_degenerated_oldtree.txt"
    
    nodes_tree, avg_times_tree = parse_file(file_tree)
    nodes_oldtree, avg_times_oldtree = parse_file(file_oldtree)
    
    times_tree = [t / 1e3 for t in avg_times_tree]
    times_oldtree = [t / 1e3 for t in avg_times_oldtree]
    
    return nodes_tree, times_tree, nodes_oldtree, times_oldtree

def get_polynomial_fits(nodes_tree, times_tree, nodes_oldtree, times_oldtree, degree=2):
    coef_tree = np.polyfit(nodes_tree, times_tree, degree)
    poly_tree = np.poly1d(coef_tree)
    fitted_tree = poly_tree(nodes_tree)

    coef_oldtree = np.polyfit(nodes_oldtree, times_oldtree, degree)
    poly_oldtree = np.poly1d(coef_oldtree)
    fitted_oldtree = poly_oldtree(nodes_oldtree)

    return fitted_tree, fitted_oldtree, poly_tree, poly_oldtree

def plot_balanced_data():
    nodes_tree, times_tree, nodes_oldtree, times_oldtree = get_balanced_data()
    
    plt.figure(figsize=(10, 6))
    plt.plot(nodes_tree, times_tree, color='green', marker='o', linestyle='-', label='Balanced Tree.design()')
    plt.plot(nodes_oldtree, times_oldtree, color='orange', marker='s', linestyle='-', label='Balanced OldTree.design()')
    
    plt.xlabel('Nombre de nœuds')
    plt.ylabel('Temps moyen (µs)')
    plt.title('Données Mesurées - Balanced Trees')
    plt.legend()
    plt.grid(True, linestyle="--", linewidth=0.5)
    plt.savefig("benchmark_results_balanced_data.png")
    plt.show()

def plot_balanced_fitted():
    nodes_tree, times_tree, nodes_oldtree, times_oldtree = get_balanced_data()
    fitted_tree, fitted_oldtree, poly_tree, poly_oldtree = get_polynomial_fits(nodes_tree, times_tree, nodes_oldtree, times_oldtree)

    print("Balanced Tree.design() - équation ajustée :")
    print(poly_tree)
    print("Balanced OldTree.design() - équation ajustée :")
    print(poly_oldtree)
    
    plt.figure(figsize=(10, 6))
    plt.plot(nodes_tree, fitted_tree, color='green', linestyle='--', linewidth=2, label='Ajustement Tree (deg2)')
    plt.plot(nodes_oldtree, fitted_oldtree, color='orange', linestyle='--', linewidth=2, label='Ajustement OldTree (deg2)')
    
    plt.xlabel('Nombre de nœuds')
    plt.ylabel('Temps moyen (µs)')
    plt.title('Courbes Ajustées - Balanced Trees')
    plt.legend()
    plt.grid(True, linestyle="--", linewidth=0.5)
    plt.savefig("benchmark_results_balanced_fitted.png")
    plt.show()

def plot_degenerated_data():
    nodes_tree, times_tree, nodes_oldtree, times_oldtree = get_degenerated_data()
    
    plt.figure(figsize=(10, 6))
    plt.scatter(nodes_tree, times_tree, color='blue', marker='o', label='Degenerated Tree.design()')
    plt.scatter(nodes_oldtree, times_oldtree, color='red', marker='s', label='Degenerated OldTree.design()')
    
    plt.xlabel('Nombre de nœuds')
    plt.ylabel('Temps moyen (ms)')
    plt.title('Données Mesurées - Degenerated Trees')
    plt.legend()
    plt.grid(True, linestyle="--", linewidth=0.5)
    plt.savefig("benchmark_results_degenerated_data.png")
    plt.show()

def plot_degenerated_fitted():
    nodes_tree, times_tree, nodes_oldtree, times_oldtree = get_degenerated_data()
    fitted_tree, fitted_oldtree, poly_tree, poly_oldtree = get_polynomial_fits(nodes_tree, times_tree, nodes_oldtree, times_oldtree)

    print("Degenerated Tree.design() - équation ajustée :")
    print(poly_tree)
    print("Degenerated OldTree.design() - équation ajustée :")
    print(poly_oldtree)
    
    plt.figure(figsize=(10, 6))
    plt.plot(nodes_tree, fitted_tree, color='blue', linestyle='--', linewidth=2, label='Ajustement Tree (deg2)')
    plt.plot(nodes_oldtree, fitted_oldtree, color='red', linestyle='--', linewidth=2, label='Ajustement OldTree (deg2)')
    
    plt.xlabel('Nombre de nœuds')
    plt.ylabel('Temps moyen (µs)')
    plt.title('Courbes Ajustées - Degenerated Trees')
    plt.legend()
    plt.grid(True, linestyle="--", linewidth=0.5)
    plt.savefig("benchmark_results_degenerated_fitted.png")
    plt.show()

def main():
    plot_balanced_data()
    plot_balanced_fitted()
    plot_degenerated_data()
    plot_degenerated_fitted()

if __name__ == '__main__':
    main()
