package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public Pane importPane;
    public AnchorPane mainPane;

    public void setPane(ActionEvent event){
        System.out.println(event.getSource());
        Button tmp = (Button) event.getSource();
        Pane newLoadedPane = null;
        try {
            switch (tmp.getId()){
                case "farmerBtn":
                    newLoadedPane = FXMLLoader.load(getClass().getResource("../fxml/addFarmer.fxml"));
                    break;
                case "marketBtn":
                    newLoadedPane = FXMLLoader.load(getClass().getResource("../fxml/addMarket.fxml"));
                    break;
                case "productBtn":
                    newLoadedPane = FXMLLoader.load(getClass().getResource("../fxml/addProduct.fxml"));
                    break;
                case "registerBtn":
                    newLoadedPane = FXMLLoader.load(getClass().getResource("../fxml/registerProduct.fxml"));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            importPane.getChildren().clear();
            importPane.getChildren().add(newLoadedPane);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Region n = (Region)loader.load();
//        mainPane.getChildren().add(n);
//        mainPane.

    }
}
