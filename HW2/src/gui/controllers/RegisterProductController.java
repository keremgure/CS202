package gui.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utilities.SQLUtilities;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class RegisterProductController implements Initializable {
    public ComboBox registerPro_farmerCBox;
    public ComboBox registerPro_productCBox;
    public TextField registerPro_qtyField;
    public TextField registerPro_priceField;
    public TextField registerPro_ibanField;
    private ArrayList<Integer> fids;
    private ArrayList<Integer> pids;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResultSet farmers = SQLUtilities.getTable(SQLUtilities.TYPE_FARMER);
        fids = new ArrayList<>();
        ResultSet products = SQLUtilities.getTable(SQLUtilities.TYPE_PRODUCT);
        pids = new ArrayList<>();
        try{
            while(farmers.next()){
                registerPro_farmerCBox.getItems().add(farmers.getString("name") + " " + farmers.getString("last_name"));
                fids.add(farmers.getInt("fid"));
            }
            while (products.next()){
                registerPro_productCBox.getItems().add(products.getString("name"));
                pids.add(products.getInt("pid"));
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println("Error adding to cbox");
        }
    }

    public void registerProductAction(){
        int findex = registerPro_farmerCBox.getSelectionModel().getSelectedIndex();
        int pindex = registerPro_productCBox.getSelectionModel().getSelectedIndex();
        int fid = fids.get(findex);
        int pid = pids.get(pindex);
        double amount;
        double price;
        String iban;
        try {
            amount = Double.parseDouble(registerPro_qtyField.getText());
            price = Double.parseDouble(registerPro_priceField.getText());
            iban = registerPro_ibanField.getText();
            if(registerPro_qtyField.getText().length() == 0 || registerPro_priceField.getText().length() == 0 ||
                    registerPro_ibanField.getText().length() == 0)throw new Exception();
        } catch (Exception e) {
            ControllerHelper.notifyParseError();
            return;
        }

        StringJoiner sj = new StringJoiner(",");
        sj.add(""+amount).add(""+price).add(iban);
        int status = SQLUtilities.addEntry(SQLUtilities.TYPE_REGISTERS,sj.toString(),fid,pid);
        if(status == 1){
            TrayNotification success = new TrayNotification();
            success.setTitle("Registered to Website Successfully.");
            success.setMessage("Registering:" + registerPro_farmerCBox.getValue() + ", " + registerPro_productCBox.getValue() + ", " + amount +", " + price + "TL is added to DB.");
            success.setNotificationType(NotificationType.SUCCESS);
            success.setAnimationType(AnimationType.FADE);
            success.showAndDismiss(Duration.seconds(4));
        }
        else{
            TrayNotification failed = new TrayNotification();
            failed.setTitle("Failed registering to Website.");
            failed.setMessage("Register failed:" + registerPro_farmerCBox.getValue() + ", " + registerPro_productCBox.getValue() + ", " + amount +", " + price + "TL to DB.");
            failed.setNotificationType(NotificationType.ERROR);
            failed.setAnimationType(AnimationType.FADE);
            failed.showAndDismiss(Duration.seconds(4));
        }
    }
}
