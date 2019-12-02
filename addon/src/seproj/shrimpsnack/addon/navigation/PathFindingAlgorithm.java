package seproj.shrimpsnack.addon.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import seproj.shrimpsnack.addon.map.CellView;
import seproj.shrimpsnack.addon.map.MapView;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.OptionalBool;
import seproj.shrimpsnack.addon.utility.Pair;

public class PathFindingAlgorithm {
	public static List<Pair> run(Pair start, Pair end, MapView map) {
		
		IndexedNodePriorityQueue q = new IndexedNodePriorityQueue();
		HashMap<Node, Node> prev_node = new HashMap<Node, Node>();
		HashMap<Node, Cost> min_actual_cost = new HashMap<Node, Cost>();
		
		Node start_node = new Node(start, null);
		q.insert_or_lower(start_node, start_node.calculateHeuristic(end));
		prev_node.put(start_node, null);
		min_actual_cost.put(start_node, new Cost(0, 0));
		
		Node node = null; // Dummy initialization to suppress eclipse warning.
		while (!q.isEmpty()) {
			node = q.pop(); // This runs at least once since we're push a node in the beginning.
			Pair pos = node.pos;
			if (pos.equals(end)) {
				break;
			}
			Cost cost = min_actual_cost.get(node);
			for (Direction d : Direction.values()) {
				Pair adj_pos = new Pair(pos.x + d.x(), pos.y + d.y());
				Node adj_node = new Node(adj_pos, d);
				CellView cell;
				try {
					cell = map.get(adj_pos);
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
				
				if (cell.isHazard().equals(OptionalBool.True)) {
					continue;
				}
				
				
				int turn_cost;
				if (node.last_move_dir == null || d.equals(node.last_move_dir)) {
					turn_cost = 0;
				} else {
					turn_cost = 1;
				}
				Cost adj_cost = cost.add(new Cost(1, turn_cost));
				Cost old_cost = min_actual_cost.get(adj_node);
				if (old_cost != null && old_cost.le(adj_cost)) {
					continue;
				}

				min_actual_cost.put(adj_node, adj_cost);
				prev_node.put(adj_node, node);
				q.insert_or_lower(adj_node, adj_cost.add(adj_node.calculateHeuristic(end)));
			}
		}
		
		if (!node.pos.equals(end)) {
			return null;
		}
		
		ArrayList<Pair> path = new ArrayList<Pair>();
		while (node != null) {
			path.add(node.pos);
			node = prev_node.get(node);
		}
		Collections.reverse(path);
		return path;
	}
}

class IndexedNodePriorityQueue {
	private ArrayList<NodeCostPair> heap;
	private HashMap<Node, Integer> index;
	
	public IndexedNodePriorityQueue() {
		this.heap = new ArrayList<NodeCostPair>();
		this.index = new HashMap<Node, Integer>();
	}
	
	public boolean isEmpty() {
		return this.heap.isEmpty();
	}

	public Node pop() {
		this.swap(0, this.heap.size() - 1);
		Node ret = this.heap.remove(this.heap.size() - 1).node;
		this.index.remove(ret);
		this.bubble_down(0);
		return ret;
	}
	
	public void insert_or_lower(Node node, Cost cost) {
		int i = 0;
		
		if (!this.index.containsKey(node)) {
			// Insert new node.
			i = this.heap.size();
			this.index.put(node, i);
			this.heap.add(new NodeCostPair(node, cost));			
		} else {
			// The node is already in this heap; Check its current cost.
			i = this.index.get(node);
			if (this.cost_at(i).le(cost)) {
				// If old cost is lower than new cost, keep it.
				return;
			}
			// New cost is strictly less than old cost; adjust it.
			this.heap.get(i).cost = cost;
		}
		
		this.bubble_up(i);
	}
	
	private Cost cost_at(int idx) {
		return this.heap.get(idx).cost;
	}
	
	private void swap(int i, int j) {
		if (i != j) {
			this.index.replace(this.heap.get(i).node, j);
			this.index.replace(this.heap.get(j).node, i);
			
			NodeCostPair tmp = this.heap.get(i);
			this.heap.set(i, this.heap.get(j));
			this.heap.set(j, tmp);
		}
	}
	
	private void bubble_up(int i) {
		while (i != 0) {
			int pi = (i - 1) / 2;
			if (this.cost_at(pi).le(this.cost_at(i))) {
				break;
			}
			this.swap(pi, i);
			i = pi;
		}
	}
	
	private void bubble_down(int i) {
		while (2 * i + 2 < this.heap.size()) {
			int li = 2 * i + 1;
			int ri = 2 * i + 2;
			int si;
			if (this.cost_at(li).le(this.cost_at(ri))) {
				si = li;
			} else {
				si = ri;
			}
			if (this.cost_at(i).le(this.cost_at(si))) {
				return;
			}
			this.swap(i, si);
			i = si;
		}
		int li = 2 * i + 1;
		if (li < this.heap.size() && !this.cost_at(i).le(this.cost_at(li))) {
			this.swap(i, li);
		}
	}
}

class Node {
	public final Pair pos;
	public final Direction last_move_dir;
	
	public Node(Pair pos, Direction last_move_dir) {
		this.pos = pos;
		this.last_move_dir = last_move_dir;
	}
	
	public Cost calculateHeuristic(Pair end) {
		// Minimum forward-moves required.
		int moves = Math.abs(end.x - this.pos.x) + Math.abs(end.y - this.pos.y);
		
		if (this.last_move_dir == null) {
			if (end.x == this.pos.x || end.y == this.pos.y) {
				return new Cost(moves, 0);
			} else {
				return new Cost(moves, 1);
			}
		}
		
		/*
		 *  Calculate minimum number of non-zero degree turns required.
		 *  Assuming the robot is at cell ^ and looking upwards,
		 *  the number of minimum turns to reach each cell equals the following:
		 *  +-----+---+-----+
		 *  |     |   |     |
		 *  |     | 0 |     |
		 *  |  1  |   |  1  |
		 *  |     +---+     |
		 *  |     | ^ |     |
		 *  +-----+---+-----+
		 *  |     |   |     |
		 *  |  2  | 1 |  2  |
		 *  |     |   |     |
		 *  +-----+---+-----+
		 */
		
		/*
		 * Difference of coordinate in 'forward' direction.
		 * To reach cells that are 'behind' the robot,
		 * We need to perform at least one turn, either a 90 or 180 degrees one.
		 */
		int d_forward;
		
		/*
		 * Difference of coordinate in 'side' direction.
		 * To reach cells that are to the side, we'll have to perform
		 * at least one more turn.
		 */
		int d_side;
		
		if (this.last_move_dir.x() == 0) { // N or S
			d_forward = (end.y - this.pos.y) * this.last_move_dir.y();
			d_side = end.x - this.pos.x;
		} else { // E or W
			d_forward = (end.x - this.pos.x) * this.last_move_dir.x();
			d_side = end.y - this.pos.y;
		}
		
		int turns = 0;
		if (d_side != 0) {
			turns++;
		}
		if (d_forward < 0) {
			turns++;
		}
		
		return new Cost(moves, turns);
	}
}

// We want to minimize moves, and for equal moves, minimize turns.
class Cost {
	public final int moves;
	public final int turns;
	
	public Cost(int moves, int turns) {
		this.moves = moves;
		this.turns = turns;
	}
	
	public Cost add(Cost other) {
		return new Cost(this.moves + other.moves, this.turns + other.turns);
	}
	
	public boolean le(Cost other) {
		if (this.moves != other.moves) {
			return this.moves <= other.moves;
		}
		return this.turns <= other.turns;
	}
}

class NodeCostPair {
	public final Node node;
	public Cost cost;
	
	public NodeCostPair(Node node, Cost cost) {
		this.node = node;
		this.cost = cost;
	}
}