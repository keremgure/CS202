package gui.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utilities.SQLUtilities;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class ProductController implements Initializable {
    public TextField addProduct_nameField;
    public ComboBox addProduct_pDateCBox;
    public ComboBox addProduct_hDateCBox;
    public TextField addProduct_altField;
    public TextField addProduct_mTempField;
    public TextField addProduct_hardnessField;
    public Button addProduct_submitBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addProduct_pDateCBox.getItems().addAll("January","February","March","April","May","June","July","August","September","October","November","December");
        addProduct_hDateCBox.getItems().addAll("January","February","March","April","May","June","July","August","September","October","November","December");
    }

    public void addProductAction(){
        String name;
        String pDate;
        String hDate;
        int alt;
        int min_temp;
        int hardness;
        try {
            name = addProduct_nameField.getText();
            pDate = (String) addProduct_pDateCBox.getValue();
            hDate = (String) addProduct_hDateCBox.getValue();
            alt = Integer.parseInt(addProduct_altField.getText());
            min_temp = Integer.parseInt(addProduct_mTempField.getText());
            hardness = Integer.parseInt(addProduct_hardnessField.getText());
            if(name.length() == 0 || pDate.length() == 0 || hDate.length() == 0 || addProduct_altField.getText().length() == 0 ||
                    addProduct_mTempField.getText().length() == 0 || addProduct_hardnessField.getText().length() == 0)throw new Exception();
        } catch (Exception e) {
            ControllerHelper.notifyParseError();
            return;
        }

        StringJoiner sj = new StringJoiner(",");
        sj.add(name).add(pDate).add(hDate).add(""+alt).add(""+min_temp).add(""+hardness);

        int status = SQLUtilities.addEntry(SQLUtilities.TYPE_PRODUCT,sj.toString());
        if(status == 1){
            TrayNotification success = new TrayNotification();
            success.setTitle("Product added Successfully.");
            success.setMessage("Product:" + name + " " + pDate + " : " + hDate + " is added to DB.");
            success.setNotificationType(NotificationType.SUCCESS);
            success.setAnimationType(AnimationType.FADE);
            success.showAndDismiss(Duration.seconds(4));
        }
        else{
            TrayNotification failed = new TrayNotification();
            failed.setTitle("Failed adding Product.");
            failed.setMessage("Product:" + name + " " + pDate + " : " + hDate + " is failed to add to DB.");
            failed.setNotificationType(NotificationType.ERROR);
            failed.setAnimationType(AnimationType.FADE);
            failed.showAndDismiss(Duration.seconds(4));
        }
    }
}
