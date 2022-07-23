/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reproductor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

/**
 *
 * @author Julio Hernandez, Pedro sabas
 */
public class Reproductor extends Application {

    private MediaPlayer mp;
    private double volumen;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream netIn;
    private HBox tStatus;
    private Button btnPlay;
    private Button btnPuse;
    private Button btnStop;
    private Slider slider;
    private String pedir;
    private CheckBox video1;
    private CheckBox video2;
    private CheckBox video3;
    private CheckBox video4;

    @Override
    public void start(Stage stage) {
        BorderPane borderPane = new BorderPane();
        HBox toolbar = new HBox();
        // HBox hbox = new HBox(8);
        Label lserver = new Label("Server");
        TextField tserver = new TextField();
        Label lport = new Label("Puerto");
        TextField tport = new TextField();
        Label luser = new Label("Usuario");
        TextField tuser = new TextField();
        Button btnt = new Button();
        btnt.setText("Conectar");
        btnt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //CODIGO BTN CONECTAR
                if (tserver.getText().length() > 0 && tport.getText().length() > 0 && tuser.getText().length() > 0) {
                    try {

                        try {
                            socket = new Socket(tserver.getText(), Integer.parseInt(tport.getText()));
                            out = new DataOutputStream(socket.getOutputStream());
                            netIn = new DataInputStream(socket.getInputStream());
                            Stage xx = new Stage();

                            String respuesta = "";
                            String msg = tserver.getText() + "/" + tport.getText() + "/" + tuser.getText();
                            out.writeUTF(msg);
                            boolean r = false;
                            //---------------------------------------------------------------------//
                            if (video1.isSelected()) {

                                try {
                                    out.writeUTF("a.mp4");
                                } catch (IOException ex) {
                                    Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                System.out.println("video1");

                            } else {
                                if (video2.isSelected()) {
                                    try {
                                        out.writeUTF("v.mp4");
                                    } catch (IOException ex) {
                                        Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    System.out.println("video2");

                                } else {
                                    if (video3.isSelected()) {
                                        try {
                                           out.writeUTF("sex.mp4");
                                        } catch (IOException ex) {
                                            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        System.out.println("video3");

                                    } else {
                                        if (video4.isSelected()) {
                                            try {
                                                out.writeUTF("sex.mp4");
                                            } catch (IOException ex) {
                                                Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            System.out.println("video4");

                                        } else {
                                            System.out.println("Seleccione un video");

                                        }

                                    }
                                }

                            }

                            //-------------------------------------------------------------------------------//
                            r = netIn.readBoolean();
                            System.err.println(r);
                            if (r) {
                                respuesta = netIn.readUTF();
                                File f = new File(respuesta);
                                f.delete();
                                System.out.println(respuesta);

                                byte[] buffer = new byte[1024];
                                int longitud = netIn.read(buffer, 0, buffer.length);
                                System.out.println(longitud);
                                FileOutputStream archivo = new FileOutputStream(f);
                                boolean val = false;
                                int con = 0;
                                while (longitud > 0) {
                                    if (longitud < buffer.length) {
                                        val = true;
                                    }
                                    archivo.write(buffer, 0, longitud);
                                    longitud = netIn.read(buffer, 0, buffer.length);
                                }
                                System.out.println("Termino" + val);
                                archivo.close();
                                socket.close();

                                if (val == true) {
                                    volumen = .4;
                                    BorderPane borderPane = new BorderPane();
                                    Media m = new Media(f.toURI().toString());
                                    mp = new MediaPlayer(m);
                                    mp.setVolume(volumen);
                                    MediaView mv = new MediaView(mp);
                                    borderPane.setCenter(mv);

                                    HBox toolbar = new HBox();
                                    // HBox hbox = new HBox(8);
                                    Button play = new Button("Reproducir");
                                    play.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent event) {
                                            
                                            
                                            mp.play();

                                        }
                                    });
                                    
                                    Slider sVolumen = new Slider(0, 1, 0.4);

                                    mp.volumeProperty().bind(sVolumen.valueProperty());
                                    Label actualVolumen = new Label("40%");
                                    actualVolumen.textProperty().bind(mp.volumeProperty().multiply(100.0).asString("%.2f %%"));
                                    Button detener = new Button("Detener");
                                    detener.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent event) {
                                            mp.stop();
                                            f.deleteOnExit();
                                            xx.close();
                                            stage.close();

                                        }
                                    });

                                    toolbar.getChildren().addAll(play,detener, sVolumen, actualVolumen);
                                    borderPane.setBottom(toolbar);
                                    StackPane root = new StackPane();
                                    root.getChildren().add(borderPane);
                                    xx.setScene(new Scene(root, 900, 800));
                                    xx.setFullScreen(true);
                                    xx.setTitle("Video");
                                    xx.show();
                                    
                                }
                            }

                            //--------------------------------------------------------------------//
                        } catch (UnknownHostException uhe) {
                            System.out.println("No se encuentra el servidor");
                        } catch (IOException ioe) {
                            System.out.println("No tengo idea porque paso esto");
                            ioe.printStackTrace();
                        } catch (NumberFormatException nfe) {
                            System.out.println("El puerto  debe ser un valor entero");
                            nfe.printStackTrace();
                        }

                    } catch (NumberFormatException nfe) {
                        System.out.println("El puerto  debe ser un valor entero");
                        nfe.printStackTrace();
                    }
                }
                System.out.println("finalizo");
                //_-------------------------------------------//

            }

        });
        toolbar.getChildren().addAll(lserver, tserver, lport, tport, luser, tuser, btnt);

        HBox tolbar = new HBox();
        video1 = new CheckBox();
        Label v1 = new Label("Video1");
        video2 = new CheckBox();
        Label v2 = new Label("Video2");
        video3 = new CheckBox();
        Label v3 = new Label("Video3");
        video4 = new CheckBox();
        Label v4 = new Label("Video4");
        tolbar.getChildren().addAll(video1, v1, video2, v2, video3, v3, video4, v4);
        Label v = new Label("Selecciona el video que quieres ver");
        borderPane.setTop(toolbar);
        borderPane.setCenter(v);
        borderPane.setBottom(tolbar);
        /*
        tStatus = new HBox();
        borderPane.setCenter(tStatus);
        borderPane.setBottom(statusbar); */

        Scene scene = new Scene(borderPane, 650, 80);

        stage.setTitle("Video");
        stage.setScene(scene);
        stage.show();

    }

    /*  private void elegir() throws IOException {

        Stage todo = new Stage();

        BorderPane borderPane = new BorderPane();
        Button n = new Button("Aceptar");
        HBox toolbar = new HBox();
        CheckBox video1 = new CheckBox();
        Label v1 = new Label("Video1");
        CheckBox video2 = new CheckBox();
        Label v2 = new Label("Video2");
        CheckBox video3 = new CheckBox();
        Label v3 = new Label("Video3");
        CheckBox video4 = new CheckBox();
        Label v4 = new Label("Video4");
        toolbar.getChildren().addAll(video1, v1, video2, v2, video3, v3, video4, v4, n);

        StackPane root = new StackPane();
        root.getChildren().add(toolbar);
        todo.setScene(new Scene(root, 300, 400));
        //stage.setFullScreen(true);
        todo.setTitle("Video");
        todo.show();

        n.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                if (video1.isSelected()) {

                    try {
                        out.writeBoolean(true);
                        out.writeInt(1);
                    } catch (IOException ex) {
                        Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("video1");

                } else {
                    if (video2.isSelected()) {
                        try {
                            out.writeBoolean(true);
                            out.writeInt(2);
                        } catch (IOException ex) {
                            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("video2");

                    } else {
                        if (video3.isSelected()) {
                            try {
                                out.writeBoolean(true);
                                out.writeInt(3);
                            } catch (IOException ex) {
                                Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("video3");

                        } else {
                            if (video4.isSelected()) {
                                try {
                                    out.writeBoolean(true);
                                    out.writeInt(4);
                                } catch (IOException ex) {
                                    Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                System.out.println("video4");

                            } else {
                                System.out.println("Seleccione un video");

                            }

                        }
                    }

                }

            }

        });
        //out.close();
    }*/
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
