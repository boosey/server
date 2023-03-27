

import java.net.URI;
import java.nio.ByteBuffer;

// import java.nio.ByteBuffer;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.RemoteEndpoint.Async;

// import javax.websocket.RemoteEndpoint.Async;
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
            Async remote = session.getAsyncRemote();
            for (int i = 0; i < 7500; i++) {
                sendMessage(remote, i);
            }

            Thread.sleep(1000);
        }
    }

    void sendMessage(Async remote, int msg) {
        ByteBuffer bb = ByteBuffer.allocate(50);
        bb.putInt(msg);
        bb.rewind();
        remote.sendBinary(bb,result ->  {
            if (result.getException() != null) {
                System.out.println("Unable to send test message: " + result.getException());
            }
        });
    }

    @ClientEndpoint
    public static class Client {

        @OnOpen
        public void open(Session session) {
            System.out.println("Client onOpen ");
        }

        @OnMessage
        void message(ByteBuffer bb) {
            int msg = bb.getInt();
            System.out.println("Client onMessage> " + ": " + msg);
        }
    }
}