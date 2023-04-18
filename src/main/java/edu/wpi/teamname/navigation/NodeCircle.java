package edu.wpi.teamname.navigation;

import edu.wpi.teamname.App;
import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.database.DataManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

public class NodeCircle {

  public Pane p;
  private Circle inner;
  private Circle outer;

  public Label label = new Label();

  private Point2D nodeCords;
  private int nodeID;
  private String firstShortName;

  private VBox changeBox;

  /**
   * A NodeCircle is a circle that represents a Node in the GUI. It is composed of two circles, an
   * inner one that is filled with a color that represents the Node type, and an outer one that
   * serves as a border. It also contains a label that displays the Node's name. When the user
   * hovers over a NodeCircle, the outer circle and the label become visible. When the user clicks
   * on a NodeCircle, a VBox containing information about the Node is displayed.
   *
   * @param n The Node that this NodeCircle represents.
   * @throws IOException If there was an error reading from the FXML file for the Node editing menu.
   */
  public NodeCircle(MapNode n, boolean isMapPage, String firstShortName)
      throws IOException, SQLException {
    float shiftX = 0; // circleR;
    float shiftY = 0; // circleR;

    this.firstShortName = firstShortName;

    float circleRCopy = GlobalVariables.getCircleR();
    float scaleDown;

    nodeCords = new Point2D(n.getX(), n.getY());
    nodeID = n.getId();

    ArrayList<LocationName> locations = null;
    // DataManager.getLocationNameByNode(nodeID, Timestamp.from(Instant.now()));

    p = new Pane();

    //    if (locations.size() > 0 && locations.get(0).getNodeType().equals("HALL")) {
    //      if (isMapPage) {
    //        //        System.out.println("HM");
    //        //        p.getChildren().addAll(this.label);
    //        return;
    //      } else {
    //        //        System.out.println("HME");
    //        scaleDown = 0.5f;
    //      }
    //      //      scaleDown = 0.5f;
    //    } else {
    //      scaleDown = 0.75f;
    //    }

    scaleDown = 0.75f;

    this.outer =
        new Circle(
            shiftX, shiftY, (circleRCopy * scaleDown) + GlobalVariables.getStrokeThickness());
    this.inner = new Circle(shiftX, shiftY, (circleRCopy * scaleDown));
    outer.setFill(GlobalVariables.getBorderColor());
    inner.setFill(GlobalVariables.getInsideColor());
    // Visible By default

    // Get short name(s) from table

    if (!(locations == null) && locations.size() > 0) {
      label.setText(locations.get(0).getShortName());
    } else {
      //      label.setText(" " + nodeID);
      label.setText(firstShortName);
    }

    CornerRadii corn = new CornerRadii(7);
    label.setBackground(
        new Background(new BackgroundFill(GlobalVariables.getLabelColor(), corn, Insets.EMPTY)));
    label.setTextFill(GlobalVariables.getLabelTextColor());
    label.setTranslateX(-GlobalVariables.getCircleR());
    label.setTranslateY(-30);

    float boxW = circleRCopy;
    float boxH = circleRCopy;

    p.setTranslateX(n.getX() - shiftX);
    p.setTranslateY(n.getY() - shiftY);
    p.setMaxWidth(boxW);
    p.setMaxHeight(boxH);

    // Scale all to like 0.75
    // Scale halls to make them smaller.

    if (isMapPage) {
      // Map Page

    } else {
      // Map Edit Page
      // Display All nodes and edges (Edges will have to be made somewhere else)

      p.setOnMouseEntered(makeVisible);
      p.setOnMouseExited(hide);
      p.setOnMouseClicked(boxVisible);
    }

    p.getChildren().addAll(this.outer, this.inner, this.label);
  }

  /**
   * A mouse event handler that hides the VBox containing information about a NodeCircle when the
   * user's mouse exits the NodeCircle.
   */
  EventHandler<MouseEvent> hide =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Pane p = ((Pane) event.getSource());
          for (javafx.scene.Node n : p.getChildren()) {
            if (n.getClass() == VBox.class) {
              p.getChildren().remove(n);
              break;
            }
          }

          for (javafx.scene.Node n : p.getChildren()) {
            if (n.getClass() == VBox.class) {
              n.setOpacity(0);
            }
          }
          //          p.setOpacity(0);
        }
      };

  /**
   * An event handler to make a node visible on mouse click.
   *
   * <p>This handler sets the opacity of the clicked node's parent pane and all its children to 1,
   * except for any VBoxes which are excluded. This is used to show a previously hidden node and its
   * contents.
   *
   * @param event the mouse event triggering the handler
   */
  EventHandler<MouseEvent> makeVisible =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Pane p = ((Pane) event.getSource());
          p.setOpacity(1);
          for (javafx.scene.Node n : p.getChildren()) {
            if (!(n.getClass() == VBox.class)) {
              n.setOpacity(1);
            }
          }
        }
      };

  /**
   * EventHandler for saving changes made to a node in the system. This EventHandler is triggered
   * when the "Save Changes" button is clicked on the edit node screen. It retrieves the updated
   * information from the relevant TextFields and constructs a new Node object with the updated
   * information. It then calls the DataManager to update the information in the system database,
   * and prints a message to the console to confirm that the synchronization has been completed.
   *
   * @param event The MouseEvent that triggered the EventHandler.
   * @throws RuntimeException if an SQL exception occurs during the data synchronization process.
   */
  EventHandler<MouseEvent> boxVisible =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Pane p = ((Pane) event.getSource());
          p.setOpacity(1);

          final var resource = App.class.getResource("views/ChangeNode.fxml");
          final FXMLLoader loader = new FXMLLoader(resource);
          try {
            changeBox = loader.load();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          // Set Location Name
          TextField Location =
              (TextField) ((Pane) (changeBox.getChildren().get(0))).getChildren().get(1);

          HashMap<Integer, ArrayList<LocationName>> map;
          try {
            map =
                DataManager.getAllLocationNamesMappedByNode(
                    new Timestamp(System.currentTimeMillis()));
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }

          //          for (Integer key : map.keySet()) {
          //            System.out.println("Key: " + key + " Val: " + map.get(key));
          //          }
          //          System.out.println(map);

          String location;
          if (map.get(nodeID).size() > 0) {
            location = map.get(nodeID).get(0).getLongName();
          } else {
            location = "" + nodeID;
          }
          Location.setText(location);

          // Set X
          TextField XText =
              (TextField) ((Pane) (changeBox.getChildren().get(1))).getChildren().get(1);
          XText.setText("" + nodeCords.getX());
          // Set Y
          TextField YText =
              (TextField) ((Pane) (changeBox.getChildren().get(2))).getChildren().get(1);
          YText.setText("" + nodeCords.getY());

          MapNode currMapNode;

          try {
            //            System.out.println("Add");
            currMapNode = DataManager.getNode(nodeID);
          } catch (Exception e2) {
            //            System.out.println("RTE");
            throw new RuntimeException(e2);
          }

          TextField floorText =
              (TextField) ((Pane) (changeBox.getChildren().get(3))).getChildren().get(1);

          TextField buildingText =
              (TextField) ((Pane) (changeBox.getChildren().get(4))).getChildren().get(1);

          if (currMapNode != null) {
            floorText.setText(currMapNode.getFloor());
            buildingText.setText(currMapNode.getBuilding());
          }

          // Set Remove Node. On Click
          MFXButton removeNodeButton =
              (MFXButton) ((Pane) (changeBox.getChildren().get(7))).getChildren().get(0);
          removeNodeButton.setOnMouseClicked(removeNode);
          // Set Submit
          MFXButton submitButton =
              (MFXButton) ((Pane) (changeBox.getChildren().get(7))).getChildren().get(1);
          submitButton.setOnMouseClicked(saveNodeChanges);

          changeBox.getChildren().remove(6);
          changeBox.getChildren().remove(5);
          //          changeBox.getChildren().remove(0);

          System.out.println("AddBox");

          p.getChildren().addAll(changeBox);
        }
      };

  /**
   * An event handler to remove a node on mouse click.
   *
   * <p>This handler removes the node associated with the clicked button from the parent VBox and
   * deletes it from the database using the node ID. If an exception occurs during the deletion
   * process, it is caught and printed to the console.
   *
   * @param event the mouse event triggering the handler
   */
  EventHandler<MouseEvent> removeNode =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          System.out.println("REM");

          // Only the Node ID is important for Deletion
          MapNode n = new MapNode(nodeID, 0, 0, "", "");

          try {
            DataManager.deleteNode(n);
          } catch (SQLException ex) {
            System.out.println(ex);
          }
        }
      };

  /**
   * An event handler for saving changes to a node when the save button is clicked. This handler is
   * triggered by a MouseEvent and updates the node's x and y coordinates, floor, and building
   * information based on the text fields in the parent VBox. The updated node is then saved to the
   * database using DataManager.syncNode().
   *
   * @param event The MouseEvent triggered by clicking the save button.
   * @throws RuntimeException if there is an SQLException while getting or synchronizing node data.
   */
  EventHandler<MouseEvent> saveNodeChanges =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          MFXButton SubmitButton = ((MFXButton) event.getSource());
          VBox v = (VBox) ((HBox) SubmitButton.getParent()).getParent();

          TextField xText = (TextField) ((Pane) (v.getChildren().get(0))).getChildren().get(1);
          TextField yText = (TextField) ((Pane) (v.getChildren().get(1))).getChildren().get(1);
          TextField floorText = (TextField) ((Pane) (v.getChildren().get(2))).getChildren().get(1);
          TextField buildingText =
              (TextField) ((Pane) (v.getChildren().get(3))).getChildren().get(1);

          MapNode currMapNode = null;
          try {
            currMapNode = DataManager.getNode(nodeID);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }

          int xPos = currMapNode.getX();
          if (!(xText.getText().equals(""))) {
            xPos = (int) Double.parseDouble(xText.getText());
          }
          int yPos = currMapNode.getY();
          if (!(yText.getText().equals(""))) {
            yPos = (int) Double.parseDouble(yText.getText());
          }
          String floor = currMapNode.getFloor();
          if (!(floorText.getText().equals(""))) {
            floor = floorText.getText();
          }
          String building = currMapNode.getBuilding();
          if (!(buildingText.getText().equals(""))) {
            building = buildingText.getText();
          }

          ArrayList<MapNode> allMapNodes;
          try {
            allMapNodes = DataManager.getAllNodes();
          } catch (SQLException ex) {
            throw new RuntimeException(ex);
          }

          // This is working on the assumption that We still want all id's to be a difference of 5
          // and that the last one in the table is the biggest ID
          int highestID = allMapNodes.get(allMapNodes.size() - 1).getId();

          MapNode n = new MapNode(nodeID, xPos, yPos, floor, building);

          try {
            DataManager.syncNode(n);
          } catch (SQLException ex) {
            System.out.println(ex);
          }

          System.out.println("DONE SYNC");

          // Update Based On text
        }
      };
}
