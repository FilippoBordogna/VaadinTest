package com.example.application.views.login;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login") // http://localhost:8080/login
@PageTitle("Chat Login") 
@AnonymousAllowed // Access to all views is denied by default but the login view must be accessible to anonymous users
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm;

    public LoginView() {
        loginForm = new LoginForm(); // Built-in Vaadin component that works nicely with Spring Security
        setSizeFull();
        setAlignItems(Alignment.CENTER); //  Flex layout to center the login form on the screen
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginForm.setAction("login"); // When the user clicks the login button, a POST request will be submitted to /login

        add(new H1("Vaadin Chat"), new Div("You can log in as 'alice', 'bob' or 'admin'. The password for all of them is 'password'."), loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true); // If there’s a query parameter called, "error" (e.g., /login?error), the login form will show an error message
        }
    }
}