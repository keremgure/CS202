package gui.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utilities.SQLUtilities;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class FarmerController implements Initializable {
    //Init AddFarmer tab.
    public TextField addFarmer_nameField;
    public TextField addFarmer_lnameField;
    public TextField addFarmer_cityField;
    public TextField addFarmer_zipField;
    public TextField addFarmer_addrField;
    public TextField addFarmer_phonesField;
    public TextField addFarmer_emailsField;
    public Button addFarmer_submitBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Set addFarmer object params.
        DecimalFormat intFormat = new DecimalFormat( "#####" );
        TextFormatter textFormatter = new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty())
                return c;
            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = intFormat.parse( c.getControlNewText(), parsePosition );
            return (object == null || parsePosition.getIndex() < c.getControlNewText().length() ? null : c);
        });
        addFarmer_zipField.setTextFormatter(textFormatter);
    }

    public void addFarmerAction(){
        String name;
        String last_name;
        int zip;
        String addr;
        String city;
        String emails;
        String phones;
        try {
            name = addFarmer_nameField.getText();
            last_name = addFarmer_lnameField.getText();
            zip = Integer.parseInt(addFarmer_zipField.getText());
            addr = addFarmer_addrField.getText();
            city = addFarmer_cityField.getText();
            emails = addFarmer_emailsField.getText();
            phones = addFarmer_phonesField.getText();
            if(name.length() == 0 || last_name.length() == 0 || addr.length() == 0 || city.length() == 0 ||
                    emails.length() == 0 || phones.length() == 0 || addFarmer_zipField.getText().length() == 0)throw new Exception();
        } catch (Exception e) {
            ControllerHelper.notifyParseError();
            return;
        }

        StringJoiner sj = new StringJoiner(",");
        sj.add(name).add(last_name).add(addr).add(""+zip).add(city).add(phones).add(emails);
        int status = SQLUtilities.addEntry(SQLUtilities.TYPE_FARMER,sj.toString());
        System.out.println(sj.toString()); //TODO REMOVE LATER DEBUG!!

        TrayNotification notification = new TrayNotification();

        if(status == 1){
            notification.setTitle("Farmer added Successfully.");
            notification.setMessage("Farmer:" + name + " " + last_name+ " is added to DB.");
            notification.setNotificationType(NotificationType.SUCCESS);
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(4));
        }
        else{
            notification.setTitle("Failed adding Farmer.");
            notification.setMessage("Farmer: " + name + " " + last_name + " is failed to add to DB.");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(4));
        }
    }
}
