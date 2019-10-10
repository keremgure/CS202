package gui.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utilities.SQLUtilities;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class MarketController implements Initializable {
    public TextField addMarket_nameField;
    public TextField addMarket_addrField;
    public TextField addMarket_cityField;
    public TextField addMarket_zipField;
    public TextField addMarket_phonesField;
    public TextField addMarket_budgetField;
    public Button addMarket_submitBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void addMarketAction(){
        String name;
        String addr;
        String city;
        String zip;
        String phones;
        String budget;
        try {
            name = addMarket_nameField.getText();
            addr = addMarket_addrField.getText();
            city = addMarket_cityField.getText();
            zip = addMarket_zipField.getText();
            phones = addMarket_phonesField.getText();
            budget = addMarket_budgetField.getText();
            if(name.length() == 0 || addr.length() == 0 || city.length() == 0 || budget.length() == 0 ||
                    phones.length() == 0 || addMarket_zipField.getText().length() == 0)throw new Exception();
        } catch (Exception e) {
            ControllerHelper.notifyParseError();
            return;
        }

        StringJoiner sj = new StringJoiner(",");
        sj.add(name).add(addr).add(zip).add(city).add(phones).add(budget);

        int status = SQLUtilities.addEntry(SQLUtilities.TYPE_MARKET,sj.toString());
        if(status == 1){
            TrayNotification success = new TrayNotification();
            success.setTitle("Market added Successfully.");
            success.setMessage("Market:" + name + " ," + addr + " ," + phones + " is added to DB.");
            success.setNotificationType(NotificationType.SUCCESS);
            success.setAnimationType(AnimationType.FADE);
            success.showAndDismiss(Duration.seconds(4));
        }
        else{
            TrayNotification failed = new TrayNotification();
            failed.setTitle("Failed adding Market.");
            failed.setMessage("Market:" + name + " ," + addr + " ," + phones + " is failed to  add to DB.");
            failed.setNotificationType(NotificationType.ERROR);
            failed.setAnimationType(AnimationType.FADE);
            failed.showAndDismiss(Duration.seconds(4));
        }


    }
}
