package edu.wpi.teamname.navigation.AlgoStrategy;

import edu.wpi.teamname.navigation.Graph;
import edu.wpi.teamname.navigation.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import lombok.Getter;
import lombok.Setter;

public class AStarAlgo implements IStrategyAlgo {

  @Getter @Setter private boolean wheelChair = true;

  @Override
  public ArrayList<Node> getPathBetween(Graph g, int startNodeId, int targetNodeId) {
    System.out.println("ASTAR T");

    ArrayList<Node> nodes = g.getNodes();

    Node s = nodes.get(Node.idToIndex(startNodeId));
    Node t = nodes.get(Node.idToIndex(targetNodeId));

    for (Node j : nodes) {
      j.setParent(null);
    }

    g.setAllG(s, t);
    // if wheelchair accessible setting is on, change edge weights between stair nodes to high
    // number so algorithm skips over
    //    if (wheelChair == true) {
    //      for (Edge e : g.getEdges()) {
    //        try {
    //          if (DataManager.isNodeType(e.getStartNodeID()).equals("STAI")
    //              && DataManager.isNodeType(e.getEndNodeID()).equals("STAI")) {
    //            e.setWeight(100000);
    //          }
    //        } catch (SQLException ex) {
    //          throw new RuntimeException(ex);
    //        }
    //      }
    //    }

    Node start = s;
    Node target = t;

    PriorityQueue<Node> closedList = new PriorityQueue<>();
    PriorityQueue<Node> openList = new PriorityQueue<>();

    start.setF(start.getG() + start.calculateHeuristic(target));
    openList.add(start);

    while (!openList.isEmpty()) {
      Node ex = openList.peek();

      if (ex == target) {
        System.out.println(closedList.size());
        return getPath(ex);
      }

      for (Node nei : ex.getNeighbors()) {

        //        if (wheelChair) {
        //          LocationName a = GlobalVariables.getHMap().get(ex.getId()).get(0);
        //          LocationName b = GlobalVariables.getHMap().get(nei.getId()).get(0);
        //
        //          if (a.getNodeType().equals("STAI") && b.getNodeType().equals("STAI")) {
        //            System.out.print("Continued");
        //            continue;
        //          }
        //        }

        double totalWeight = ex.getG() + nei.findWeight(ex, wheelChair);

        //        System.out.println(closedList.size());

        if (!openList.contains(nei) && !closedList.contains(nei)) {
          nei.setParent(ex);
          nei.setG(totalWeight);
          nei.setF(nei.getG() + nei.calculateHeuristic(target));
          openList.add(nei);
        } else {
          if (totalWeight < nei.getG()) {
            nei.setParent(ex);
            nei.setG(totalWeight);
            nei.setF(nei.getG() + nei.calculateHeuristic(target));

            if (closedList.contains(nei)) {
              closedList.remove(nei);
              openList.add(nei);
            }
          }
        }
      }
      openList.remove(ex);
      closedList.add(ex);
      // System.out.println(closedList.size());
    }
    return null;
  }

  /**
   * This method returns the path from the target node to the start node.
   *
   * @param target The target node to start the path from
   * @return The path from the target node to the start node
   */
  public ArrayList<Node> getPath(Node target) {
    System.out.println("Path");
    Node n = target;

    ArrayList<Node> ids = new ArrayList<>();
    if (n == null) return ids;

    while (n.getParent() != null) {
      ids.add(n);
      n = n.getParent();
    }
    ids.add(n);
    Collections.reverse(ids);

    System.out.println("Path LEn:" + ids.size());
    return ids;
  }
}
