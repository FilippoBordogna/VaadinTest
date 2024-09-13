package com.example.application.views.lobby;

import com.example.application.chat.Channel;
import com.example.application.chat.ChatService;
import com.example.application.views.channel.ChannelView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;

@Route(value = "") // Default view
@PageTitle("Lobby") // Page title
public class LobbyView extends VerticalLayout {

    private final ChatService chatService;
    private final VirtualList<Channel> channels;
    private final TextField channelNameField;
    private final Button addChannelButton;

    public LobbyView(ChatService chatService) {
    	this.chatService = chatService;
        setSizeFull();

        channels = new VirtualList<>();
        channels.setRenderer(new ComponentRenderer<>(this::createChannelComponent));
        add(channels);
        expand(channels); // Expand the size of the channels list to use all available space in the page

        channelNameField = new TextField();
        channelNameField.setPlaceholder("New channel name"); // Placeholder of the field value

        addChannelButton = new Button("Add channel", event -> addChannel()); // Onclick the addChannel() function is called
        addChannelButton.setDisableOnClick(true); // The button is disabled after the first click to avoid triggering the button more than once
        addChannelButton.addClickShortcut(Key.ENTER);

        var toolbar = new HorizontalLayout(channelNameField, addChannelButton); // Displays components horizontally next to each other.
        toolbar.setWidthFull();
        toolbar.expand(channelNameField);
        add(toolbar);
    }
    
    private void refreshChannels() { // OnAttach called function
        channels.setItems(chatService.channels()); // Set the list of channels
    }
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        refreshChannels();
    }
    
    private void addChannel() {
        try {
            var nameOfNewChannel = channelNameField.getValue(); // If the field is empty, its value will be an empty string, not null
            if (!nameOfNewChannel.isBlank()) {
                chatService.createChannel(nameOfNewChannel);
                channelNameField.clear(); // Clears the name field if the channel was created successfully
                refreshChannels(); // Refresh the entire list of channels
            }
        } finally {
            addChannelButton.setEnabled(true); // Re-enable the add button
        }
    }
    
    private Component createChannelComponent(Channel channel) { // Renderer of VirtualList
    	// Create a link with the channel’s name. When clicked, it will navigate to the channel view and pass the channel’s ID as a URL parameter.
        return new RouterLink(channel.name(), ChannelView.class, channel.id());
    }
}