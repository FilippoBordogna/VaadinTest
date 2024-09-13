package com.example.application.views.channel;

import java.util.ArrayList;
import java.util.List;

import com.example.application.chat.ChatService;
import com.example.application.chat.Message;
import com.example.application.views.lobby.LobbyView;

import reactor.core.Disposable;

import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.AttachEvent;

@Route(value = "channel") 
public class ChannelView extends VerticalLayout implements HasUrlParameter<String> { 
	// Extending VerticalLayout the items are added (add() function) one below the other
	// Implementing HasUrlParameter<String> we accept an URL parameter of type String (the channel ID)

	private final ChatService chatService;
	private final MessageList messageList;
	private String channelId;
	private final List<Message> receivedMessages = new ArrayList<>(); // List of messages received
	
	public ChannelView(ChatService chatService) { // Injected by Spring
	    this.chatService = chatService;
	    setSizeFull(); // Set view width and height to 100%
	    
	    messageList = new MessageList(); // Built-in component for displaying messages from different users
	    messageList.setSizeFull();
	    add(messageList);
	    
	    //var messageInput = new MessageInput(); // Built-in component for entering and sending messages
	    //messageInput.addSubmitListener(event -> sendMessage(event.getValue()));
	    var messageInput = new MessageInput(event -> sendMessage(event.getValue()));
	    messageInput.setWidthFull();
	    add(messageInput);
	}

    @Override
    public void setParameter(BeforeEvent event, String channelId) { 
    	// Provided by the HasUrlParameter interface: every time the parameter changes this function is called
    	if (chatService.channel(channelId).isEmpty()) {
            event.forwardTo(LobbyView.class); 
        } else {
            this.channelId = channelId;
        }
    }
    
    private void sendMessage(String message) {
        if (!message.isBlank()) {
            chatService.postMessage(channelId, message);
        }
    }
    
    private MessageListItem createMessageListItem(Message message) { // Convert form Message to MessageListItem object
        var item = new MessageListItem(
            message.message(),
            message.timestamp(),
            message.author()
        );
        return item;
    }
    
    private void receiveMessages(List<Message> incoming) { // Method called  whenever new messages arrive
    	// The messages arrive in batch to improve performance
        getUI().ifPresent(ui -> ui.access(() -> { // Update Vaadin UI
            receivedMessages.addAll(incoming);
            messageList.setItems(receivedMessages.stream().map(this::createMessageListItem).toList()); // Recreate the full list
        }));
    }
    
    private Disposable subscribe() { // Method that subscribes to the service
        var subscription = chatService
                .liveMessages(channelId)
                .subscribe(this::receiveMessages); // Call receiveMessages function when the Flux emits a new batch of messages 
        return subscription;  // Reference to the subscription. So we can cancel it when we don’t need it any longer
    }
    
    @Override
    protected void onAttach(AttachEvent attachEvent) { // Method that is called when the component in attached to a UI
        var subscription = subscribe(); 
        addDetachListener(event -> subscription.dispose()); // Delete the subscription when detached from the UI
    }
}