
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import org.junit.jupiter.api.Test;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ServerTest {

    @TestHTTPResource("/start-websocket/john")
    URI uri;

    @Test
    public void testWebsocketServer() throws Exception {
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            session.getAsyncRemote().sendText("hello world");
            Thread.sleep(1);              
        }
    }

    @ClientEndpoint
    public static class Client {

        @OnOpen
        public void open(Session session) {
            // Send a message to indicate that we are ready,
            // as the message handler may not be registered immediately after this callback.
            session.getAsyncRemote().sendText("_ready_");
        }

        @OnMessage
        void message(String msg) {
            System.out.println("Client onMessage> " + ": " + msg);
        }

    }

}