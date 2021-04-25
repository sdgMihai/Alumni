package com.licentamihai.alumni.socket;


import io.socket.emitter.Emitter;
import io.socket.engineio.server.EngineIoServer;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoServer;
import io.socket.socketio.server.SocketIoSocket;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// TODO: https://github.com/wjd123/socket/blob/main/src/main/java/com/wjd/mynote/common/SocketIoServlet.java
@WebServlet("/socket.io/*")
public class SocketIoServlet extends HttpServlet {
    private final EngineIoServer mEngineIoServer = new EngineIoServer();
    private final SocketIoServer mSocketIoServer = new SocketIoServer(mEngineIoServer);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        mEngineIoServer.handleRequest(request, response);

        SocketIoNamespace namespace = mSocketIoServer.namespace("/chat");

        namespace.on("connection", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SocketIoSocket socket = (SocketIoSocket) args[0];
                // Do something with socket
            }
        });

        // Attaching to 'foo' event
        mEngineIoServer.on("foo", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("  ");
                SocketIoSocket socket = (SocketIoSocket) args[0];
                socket.send("foo", "bar arg", 1);
                // Arugments from client available in 'args'
            }
        });


        namespace.broadcast("room", "foo", "bar arg");
    }
}
