import matplotlib.pyplot as plt
import numpy as np

def parse_file(filename):
    max_depths = []
    average_times = []
    current_max_depth = None
    with open(filename, 'r') as file:
        for line in file:
            line = line.strip()
            if line.startswith("Benchmarking"):
                parts = line.split("with max_depth:")
                if len(parts) >= 2:
                    remainder = parts[1].strip()
                    parts2 = remainder.split(", depth:")
                    if len(parts2) >= 2:
                        try:
                            current_max_depth = float(parts2[0].strip())
                        except ValueError:
                            continue
            elif line.startswith("Average time"):
                parts = line.split(":")
                if len(parts) >= 2 and current_max_depth is not None:
                    try:
                        avg_time = float(parts[1].strip().split()[0])
                        max_depths.append(current_max_depth)
                        average_times.append(avg_time)
                    except ValueError:
                        continue
    return max_depths, average_times

def main():
    tree_file = "benchmark_results_tree.txt"
    oldtree_file = "benchmark_results_oldtree.txt"

    max_depths_tree, avg_times_tree = parse_file(tree_file)
    max_depths_oldtree, avg_times_oldtree = parse_file(oldtree_file)

    tree_data = sorted(zip(max_depths_tree, avg_times_tree))
    oldtree_data = sorted(zip(max_depths_oldtree, avg_times_oldtree))
    max_depths_tree, avg_times_tree = zip(*tree_data)
    max_depths_oldtree, avg_times_oldtree = zip(*oldtree_data)

    max_depths_tree = [2**(d+1)-1 for d in max_depths_tree]
    max_depths_oldtree = [2**(d+1)-1 for d in max_depths_oldtree]
    avg_times_tree = [t / 1e3 for t in avg_times_tree]
    avg_times_oldtree = [t / 1e3 for t in avg_times_oldtree]

    coeffs_tree = np.polyfit(max_depths_tree, avg_times_tree, 1)
    coeffs_oldtree = np.polyfit(max_depths_oldtree, avg_times_oldtree, 1)
    
    poly_tree = np.poly1d(coeffs_tree)
    poly_oldtree = np.poly1d(coeffs_oldtree)
    
    x_range = np.linspace(0, max(max_depths_tree + max_depths_oldtree), 100)
    
    plt.figure(figsize=(10, 6))
    plt.plot(max_depths_tree, avg_times_tree, 'o', label='Tree.design()')
    plt.plot(max_depths_oldtree, avg_times_oldtree, 's', label='OldTree.design()')
    plt.plot(x_range, poly_tree(x_range), '--', label=f'Tree fit: y={coeffs_tree[0]:.2e}x + {coeffs_tree[1]:.2e}')
    plt.plot(x_range, poly_oldtree(x_range), '--', label=f'OldTree fit: y={coeffs_oldtree[0]:.2e}x + {coeffs_oldtree[1]:.2e}')
    
    plt.xlim(left=0)
    plt.ylim(bottom=0)
    plt.ticklabel_format(style='plain', axis='both')
    plt.xlabel('Max Depth (converted to nodes)')
    plt.ylabel('Temps moyen (ms)')
    plt.title('Résultats des benchmarks')
    plt.legend()
    plt.grid(True, which="both", ls="--", lw=0.5)
    plt.tight_layout()

    plt.savefig("benchmark_results.png")
    plt.show()
    
    print(f"Tree.design() fit: y = {coeffs_tree[0]:.6f}x + {coeffs_tree[1]:.6f}")
    print(f"OldTree.design() fit: y = {coeffs_oldtree[0]:.6f}x + {coeffs_oldtree[1]:.6f}")

if __name__ == '__main__':
    main()
