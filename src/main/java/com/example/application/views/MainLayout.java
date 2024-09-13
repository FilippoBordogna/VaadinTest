package com.example.application.views;

import com.example.application.views.lobby.LobbyView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout { 

    private H2 viewTitle; // Vaadin component representing the <h2> HTML element: it will contain the title of the current view

    public MainLayout() {
        setPrimarySection(Section.DRAWER); // Makes the drawer fill the entire height of the screen, moving the navbar to the side of it.
        addNavbarContent();
        addDrawerContent();
    }

    private void addNavbarContent() {
    	var toggle = new DrawerToggle(); // Built-in component for showing and hiding the drawer
        toggle.setAriaLabel("Menu toggle"); // Important for accessibility (for components without captions)
        toggle.setTooltipText("Menu toggle"); // The tooltip is displayed when the mouse pointer hovers over the toggle button

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE, LumoUtility.Flex.GROW); // CSS styling

        var header = new Header(toggle, viewTitle); // Vaadin component representing the <header> HTML element
        header.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,LumoUtility.Padding.End.MEDIUM, LumoUtility.Width.FULL); // CSS styling

        addToNavbar(false, header); // The boolean flag set to false says to keep the header at the top even on mobile devices.
    }

    private void addDrawerContent() {
    	var appName = new Span("Vaadin Chat"); // Vaadin component representing the <span> HTML element
        appName.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX, LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.Height.XLARGE, LumoUtility.Padding.Horizontal.MEDIUM); // CSS styling

        addToDrawer(appName, new Scroller(createSideNav())); // The SideNav is wrapped inside a Scroller component to make sure it scrolls in case it does not fit on the screen.
    }

    private SideNav createSideNav() {
        SideNav nav = new SideNav(); // Side navigation menu component with support for flat and hierarchical navigation items
        // The side navigation menu will contain a single item that navigates the user to the lobby view
        nav.addItem(new SideNavItem("Lobby", LobbyView.class, VaadinIcon.BUILDING.create())); 

        return nav;
    }
    
    private String getCurrentPageTitle() { // Retrieving the page title
        if (getContent() == null) {
            return "";
        } else if (getContent() instanceof HasDynamicTitle titleHolder) { // dynamic page
            return titleHolder.getPageTitle();
        } else { // static page
            var title = getContent().getClass().getAnnotation(PageTitle.class);
            return title == null ? "" : title.value();
        }
    }
    
    @Override
    protected void afterNavigation() { //  Update the user interface when the content changes by setting the page title
        super.afterNavigation(); // Important
        viewTitle.setText(getCurrentPageTitle());
    }
}