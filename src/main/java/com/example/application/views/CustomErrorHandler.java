package com.example.application.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class CustomErrorHandler implements ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorHandler.class);

    @Override
    public void error(ErrorEvent event) {
        log.error("Unexpected error caught", event.getThrowable()); // For debug we log every unexpected exception
        showError("An unexpected error has occurred. Please try again later."); // Don’t reveal any technical information about the error (dangerous doing).
    }

    private void showError(String error) {
        Optional.ofNullable(UI.getCurrent()).ifPresent(ui -> ui.access(() -> { // The error notification will only be shown if the current thread has access to a UI
            var notification = Notification.show(error);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }));
    }
}