package warlords;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
// import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
// import java.io.IOException;

// import static java.util.Objects.requireNonNull;

@ServerEndpoint("/start-websocket/{name}")
@ApplicationScoped
public class GamePlayServer {

    Map<String, Session> sessions = new ConcurrentHashMap<>(); 

    @OnOpen
    public void onOpen(Session session, @PathParam("name") String name) {
        System.out.println("onOpen> " + name);
        sessions.put(name, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("name") String name) {
        System.out.println("onClose> " + name);
        sessions.remove(name);
    }

    @OnError
    public void onError(Session session, @PathParam("name") String name, Throwable throwable) {
        System.out.println("onError> " + name + ": " + throwable);
        sessions.remove(name);

    }

    @OnMessage
    public void onMessage(String message, @PathParam("name") String name) {
        System.out.println("onMessage> " + name + ": " + message);
        if (message.equalsIgnoreCase("_ready_")) {
            broadcast("User " + name + " joined");
        } else {
            broadcast(">> " + name + ": " + message);
        }
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            System.out.println("Broadcast: " + message);
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
}
