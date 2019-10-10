package gui.controllers;

import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

public abstract class ControllerHelper {
    public static void notifyParseError(){
        TrayNotification notification = new TrayNotification();
        notification.setTitle("Error while parsing!");
        notification.setMessage("Please fill all the fields with proper characteristics.");
        notification.setNotificationType(NotificationType.ERROR);
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(4));

    }
}
