package triviagame.handlers;

import java.net.URI;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

// import javafx.application.Platform;
import triviagame.controllers.PageChooseTopic;
import triviagame.controllers.PageConnect;
// import triviagame.controllers.PageTriviaGame;
import triviagame.controllers.PageWaitTopic;
import triviagame.interfaces.HasChatBox;

public class SocketHandler {

    private StageHandler stageHandler;
    private Socket socket;

    public SocketHandler(StageHandler stageHandler){
        this.stageHandler = stageHandler;
    }

    public void init(){
        //crio socket
        URI uri = URI.create("http://localhost:5000");
        IO.Options options = IO.Options.builder().build();
        socket = IO.socket(uri, options);

        // conecto no socket
        socket.connect();

        //handshake
        socket.once("connectReply", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                //antibug
                String arg = (String) args[0];                
                for (Object object : args) {
                    String obj = (String) object;
                    if(obj.charAt(0) == '{'){
                        arg = obj;
                        break;
                    }
                }
                JSONObject response = new JSONObject(arg);
                //end antibug

                try {
                    stageHandler.changeSceneSynchronous("/pages/pageConnect.fxml");
                    PageConnect controller = (PageConnect)stageHandler.getSceneController();
                    // controller.updateTimer(seconds);

                    controller.startTimer();

                    int playerCount = response.getInt("playerCount");

                    controller.updatePlayerCount(playerCount);
                    controller.updateTimer(response.getInt("countdown"));                      

                    for(int i=1; i<=playerCount; i++){
                        String key = "player" + Integer.toString(i);
                        String name = response.getString(key);
                        controller.printLine(name + " entrou no jogo...");
                    }
                    
                    System.out.println("Conectado ao servidor.");

                } catch(Exception e){
                    System.out.println("Exception Error: " + e);
                }
            }
        });

        socket.on("newEvent", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject event = new JSONObject((String)args[0]);

                //get controller
                HasChatBox controller = (HasChatBox) stageHandler.getSceneController();

                //type join
                if(event.getString("type").equals("join")){
                    controller.printLine(event.getString("username") + " entrou no jogo...");
                    controller.updatePlayerCount(event.getInt("playerCount"));

                }

                System.out.println((String)args[0]);

                // JSONObject reply = new JSONObject((String)args[0]);
                              
                // controller.printLine("alo");
                // controller.getClass().
            }
        });
               
        System.out.println("socket handler iniciado");
    }

    public void terminate(){
        // terminate sockets
        socket.off();
        socket.disconnect();
        // socket.close();

        System.out.println("socket handler terminado");
    }

    public void emit(String event, Object message){
        socket.emit(event, message);
    }

    public Socket getSocket(){
        return socket;
    }

    public void initGameListeners(){
       
        socket.on("startRound", new Emitter.Listener() {
            @Override
            public void call(Object... args) {             
                JSONObject reply = new JSONObject((String)args[0]);

                try {
                    if(reply.get("playerType").equals("master")){
                        stageHandler.changeSceneSynchronous("/pages/pageChooseTopic.fxml");
                        PageChooseTopic controller = (PageChooseTopic)stageHandler.getSceneController();
                        // controller.updateTimer(segundos);
                        controller.startTimer();

                    }else{
                        stageHandler.changeSceneSynchronous("/pages/pageWaitTopic.fxml");
                        PageWaitTopic controller = (PageWaitTopic)stageHandler.getSceneController();
                        // controller.updateTimer(segundos);
                        controller.startTimer();
                    }
                
                    
                } catch(Exception e){
                    System.out.println("Exception Error: " + e);
                
                } finally {
                    // PageConnect controller = (PageConnect) stageHandler.getSceneController();
                    // controller.olamundo();

                }
                
            }
        });

        socket.on("startGameplay", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // JSONObject reply = new JSONObject((String)args[0]);
            }
        });


        //emitir sinal de que o player está pronto'
        socket.emit("gameReady", "success");

        System.out.println("init game listeners");
    }
}


        // new Socket

        // //conectar ao servidor
        // try {
        //     //crio socket
        //     URI uri = URI.create("http://localhost:5000");
        //     IO.Options options = IO.Options.builder().build();
        //     socket = IO.socket(uri, options);

        //     //conecto no socket
        //     socket.connect();

        //     //recebo mensagens
        //     socket.once("connectReply", new Emitter.Listener() {
        //         @Override
        //         public void call(Object... args) {
        //             System.out.println("Conectado.");
        //         }
        //     });

        // } catch(Exception ex) {
        //     if(socket != null){
        //         socket.disconnect();
        //         System.out.println("Disconectado. " + socket);
        //     }
        //     System.out.println("Exception Error: " + ex);

        // } finally {
        //     // if(socket != null){
        //     //     socket.disconnect();
        //     //     System.out.println("Disconectado. " + socket);
        //     // }
        // }

        
        // app.stageHandler.changeScene();
        // // //crio socket
        // // URI uri = URI.create("http://localhost:5000");
        // // IO.Options options = IO.Options.builder().build();
        // // Socket socket = IO.socket(uri, options);

        // // //conecto no socket
        // // socket.connect();
        // System.out.println("fechando.");

        // //emito mensagens
        // socket.emit("my_message", "testando 123");
        // socket.emit("hello", "world");

        // //recebo mensagens
        // socket.on("world", new Emitter.Listener() {
        //     @Override
        //     public void call(Object... args) {
        //         System.out.println("hello world!!!!!. -> " + args[0]);
        //     }
        // });


        //abrir pagina de conectar

        // import org.json.JSONObject;

                // //enviar objeto json
                // JSONObject json = new JSONObject();
                // json.put("heeello", "wooolrd");
                // socket.emit("hello", json);

                
                // //receber e ler objeto json
                // socket.once("world", new Emitter.Listener() {
                //     @Override
                //     public void call(Object... args) {
                //         //receber e ler objeto json
                //         JSONObject my_obj = new JSONObject((String)args[0]);
                //         System.out.println("my_obj.hello: " + my_obj.get("hello"));
                //     }
                // });