// FLOW VIEW

package com.example.application.views.todo;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("todo/flow") 
public class TodoView extends VerticalLayout { 

public TodoView() {
    var todosList = new VerticalLayout(); // VerticalLayout: List of task
    var taskField = new TextField(); // text input field: description of the tasks
    var addButton = new Button("Add"); // button for adding new tasks
    addButton.addClickListener(click -> { // create a checkbox and add to the list
      Checkbox checkbox = new Checkbox(taskField.getValue());
      todosList.add(checkbox);
    });
    addButton.addClickShortcut(Key.ENTER); // Shortcut for the add button
    add( // adding elements vertically (because we are in a class that extends VerticalLayout)
      new H1("Vaadin Todo"),
      todosList,
      new HorizontalLayout( // HorizontalLayout: I want 2 elements to be on the same line
        taskField,
        addButton
      )
    );
  }
}