package com.example.application.views.channel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.application.chat.ChatService;
import com.example.application.chat.Message;
import com.example.application.util.LimitedSortedAppendOnlyList;
import com.example.application.views.lobby.LobbyView;
import com.example.application.views.MainLayout;

import reactor.core.Disposable;

import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

import com.vaadin.flow.component.AttachEvent;

@Route(value = "channel", layout = MainLayout.class) // http://localhost:8080/channel/<channel-id> - MainLayout(AppLayout)
@PermitAll // Access permitted
public class ChannelView extends VerticalLayout implements HasUrlParameter<String>, HasDynamicTitle { // Vertical Layout + Parameter in the URL + Dynamic Title
	// Extending VerticalLayout the items are added (add() function) one below the other
	// Implementing HasUrlParameter<String> we accept an URL parameter of type String (the channel ID)

	private final ChatService chatService;
	private final MessageList messageList;
	private String channelId; // ID of the Channel
	private String channelName; // Name of the Channel
	private static final int HISTORY_SIZE = 20; // Shows only last 20 messages
	private final LimitedSortedAppendOnlyList<Message> receivedMessages; // List of messages received
	private final String currentUserName; // Current user’s username
	
	public ChannelView(ChatService chatService, AuthenticationContext authenticationContext) { // Injected by Spring
	    this.chatService = chatService;
	    setSizeFull(); // Set view width and height to 100%
	    
	    receivedMessages = new LimitedSortedAppendOnlyList<>(HISTORY_SIZE, Comparator.comparing(Message::sequenceNumber)); // Messages will be sorted by their sequence number
	    
	    messageList = new MessageList(); // Built-in component for displaying messages from different users
	    messageList.addClassNames(LumoUtility.Border.ALL); // Adds a thin border to all sides of the message list component
	    messageList.setSizeFull();
	    add(messageList);
	    
	    //var messageInput = new MessageInput(); // Built-in component for entering and sending messages
	    //messageInput.addSubmitListener(event -> sendMessage(event.getValue()));
	    var messageInput = new MessageInput(event -> sendMessage(event.getValue()));
	    messageInput.setWidthFull();
	    add(messageInput);
	    
	    this.currentUserName = authenticationContext.getPrincipalName().orElseThrow(); // Retrieve the principal name and stores it in the field. If there isn't throw an exception
	}

    @Override
    public void setParameter(BeforeEvent event, String channelId) { 
    	// Provided by the HasUrlParameter interface: every time the parameter changes this function is called
    	chatService.channel(channelId).ifPresentOrElse(
                channel -> this.channelName = channel.name(), // If the channel ID is valid, store the name in the channelName field
                () -> event.forwardTo(LobbyView.class) // If the channel ID is invalid, navigate back to the lobby view
        );
        this.channelId = channelId;
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
        
        item.setUserColorIndex(Math.abs(message.author().hashCode() % 7)); // The hash code can be negative and the color index is in range 0-6
        item.addClassNames(LumoUtility.Margin.SMALL, LumoUtility.BorderRadius.MEDIUM); // Add a small margin and a medium border radius to the MessageListItem
        if (message.author().equals(currentUserName)) {
            item.addClassNames(LumoUtility.Background.CONTRAST_5); // Add a darker background to all message items that have been written by the current user.
        }
        
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
        var subscription = chatService.liveMessages(channelId)
        							  .subscribe(this::receiveMessages); // Call receiveMessages function when the Flux emits a new batch of messages 
        
        var lastSeenMessageId = receivedMessages.getLast().map(Message::messageId).orElse(null); // If the list is empty we pass null as the latest message
        receiveMessages(chatService.messageHistory(channelId, HISTORY_SIZE, lastSeenMessageId));
        
        return subscription;  // Reference to the subscription. So we can cancel it when we don’t need it any longer
    }
    
    @Override
    protected void onAttach(AttachEvent attachEvent) { // Method that is called when the component in attached to a UI
        var subscription = subscribe(); 
        addDetachListener(event -> subscription.dispose()); // Delete the subscription when detached from the UI
    }
    
    @Override
    public String getPageTitle() {
        return channelName;
    }
}