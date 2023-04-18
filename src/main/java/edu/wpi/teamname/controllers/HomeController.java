package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.Navigation;
import edu.wpi.teamname.Screen;
import edu.wpi.teamname.employees.EmployeeType;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.File;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;

public class HomeController {

  @FXML MFXButton helpButton;
  @FXML MFXButton mapButton;
  @FXML MFXButton directionsButton;
  @FXML MFXButton makeRequestsButton;
  @FXML MFXButton makeRequestsButton1;
  @FXML MFXButton makeRequestsButton2;
  @FXML MFXButton makeRequestsButton3;
  @FXML MFXButton showRequestsButton;
  @FXML MFXButton editMapButton;
  @FXML MFXButton exitButton;
  @FXML MFXButton navigateButton;

  // test push
  @Setter @Getter private static ObservableBooleanValue loggedIn = new SimpleBooleanProperty(false);

  //  @FXML ImageView imageView;
  @FXML private AnchorPane rootPane;
  @FXML MFXButton loginButton;
  @FXML MFXButton logoutButton;
  @FXML MFXButton editMoveButton;

  /** logs the current user out of the application */
  private void logout() {
    loggedIn = new SimpleBooleanProperty(false);
    loginButton.setVisible(true);
    logoutButton.setVisible(false);
    GlobalVariables.logOut();
    disableButtonsWhenLoggedOut();
  }

  /** * Disables all the buttons that can not be accessed without logging in */
  private void disableButtonsWhenLoggedOut() {
    makeRequestsButton.setDisable(true);
    makeRequestsButton1.setDisable(true);
    makeRequestsButton2.setDisable(true);
    makeRequestsButton3.setDisable(true);
    showRequestsButton.setDisable(true);
    editMapButton.setDisable(true);
    editMoveButton.setDisable(true);
  }

  @FXML
  public void initialize() {

    // set the width and height to be bound to the panes width and height
    //    imageView.fitWidthProperty().bind(rootPane.widthProperty());
    //    imageView.fitHeightProperty().bind(rootPane.heightProperty());
    // this allows for the image to stay at the same size of the rootPane, which is the parent pane
    // of the Home.fxml

    // Param is EventHandler
    // Lambda Expression. parameter -> expression
    // Basically just runs the Navigation.navigate Function
    // "event" is a parameter, but there is no
    if (loggedIn.getValue()) {
      loginButton.setVisible(false);
      logoutButton.setVisible(true);
    } else {
      loginButton.setVisible(true);
      logoutButton.setVisible(false);
    }
    loginButton.setOnMouseClicked(event -> Navigation.navigate(Screen.LOGIN));
    logoutButton.setOnMouseClicked(event -> logout());
    //    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    //        helpButton.setOnMouseClicked(event -> Navigation.navigate(Screen));

    disableButtonsWhenLoggedOut();
    if (GlobalVariables.userIsType(EmployeeType.STAFF)) {
      makeRequestsButton.setDisable(false);
      makeRequestsButton1.setDisable(false);
      makeRequestsButton2.setDisable(false);
      makeRequestsButton3.setDisable(false);
      showRequestsButton.setDisable(false);
    }
    if (GlobalVariables.userIsType(EmployeeType.ADMIN)) {
      editMapButton.setDisable(false);
      editMoveButton.setDisable(false);
    }

    /*loggedIn.addListener(
    (loggedIn, old, newV) -> {
      if (newV && GlobalVariables.userIsType(EmployeeType.STAFF)) {
        makeRequestsButton.setDisable(false);
        makeRequestsButton1.setDisable(false);
        makeRequestsButton2.setDisable(false);
        makeRequestsButton3.setDisable(false);
        showRequestsButton.setDisable(false);
      }
      if (newV && GlobalVariables.userIsType(EmployeeType.ADMIN)) {
        editMapButton.setDisable(false);
        editMoveButton.setDisable(false);
      }
      if (!newV) {
        disableButtonsWhenLoggedOut();
      }
    });*/
    mapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP));
    // directionsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    makeRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    makeRequestsButton1.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    makeRequestsButton2.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    makeRequestsButton3.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    showRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST_VIEW));
    editMapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDIT));
    exitButton.setOnMouseClicked(event -> System.exit(0));
    navigateButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP));
    editMoveButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_TABLE));
  }
}
