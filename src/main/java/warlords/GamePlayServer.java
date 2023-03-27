package warlords;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

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
    public void onMessage(ByteBuffer bb) {
        int msg = bb.getInt();
        // System.out.println("Server onMessage> " + ": " + msg);
        broadcast(msg);
    }

    private void broadcast(int message) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(message);
        sessions.values().forEach(s -> {
            // System.out.println("Broadcast: " + message);
            bb.rewind();
            s.getAsyncRemote().sendBinary(bb, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

}
