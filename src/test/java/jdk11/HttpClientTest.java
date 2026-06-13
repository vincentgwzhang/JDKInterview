package jdk11;

import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {

    @Test
    void syncGet() throws Exception {
        try (LocalHttpServer server = LocalHttpServer.start()) {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(server.uri())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("jdk11-http-client"));
        }
    }

    static final class LocalHttpServer implements AutoCloseable {
        private final HttpServer server;
        private final URI uri;

        private LocalHttpServer(HttpServer server, URI uri) {
            this.server = server;
            this.uri = uri;
        }

        static LocalHttpServer start() throws IOException {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/get", exchange -> {
                byte[] body = """
                        {"message":"jdk11-http-client"}
                        """.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            server.start();
            URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/get");
            return new LocalHttpServer(server, uri);
        }

        URI uri() {
            return uri;
        }

        @Override
        public void close() {
            server.stop(0);
        }
    }
}
