package ru.epavlov.raspip.serial;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.epavlov.raspip.gui.Images;
import ru.epavlov.raspip.gui.Presenter;
import ru.epavlov.raspip.gui.Resources;

/**
 * Created by Eugene on 27.12.2016.
 */
public class GUI extends Application {
    private Presenter presenter = new Presenter();
    private String fontPath="file:"+Images.path+"/font.ttf";
    private Text status = new Text();
    private Text labelX = new Text("Координата Х: 4587.89");
    private Text labelY = new Text("Координата Y: 255.03");
    private Text labelZ = new Text("Координата Z: 9632.333");
    private String fontName = "Neogrey Medium";
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("rasPIP");
        BorderPane group = new BorderPane();
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        presenter.getBackground().filter(background -> background != null).subscribe(group::setBackground);
//        presenter.getStatus().filter(imageView -> imageView != null).subscribe(imageView1 -> {
//            Platform.runLater(() -> {
//                imageView1.setLayoutX(185);
//                imageView1.setLayoutY(92);
//                Pane bp = new BorderPane();
//                bp.getChildren().addAll(imageView1);
//                group.setTop(bp);
//            });
//        });
        presenter.getStatus().subscribe(s1 -> {
            Platform.runLater(()->{
                status.setLayoutX(187);
                status.setLayoutY(107);
                status.setFill(Color.rgb(254,59,0));
                status.setText(s1);

                status.setFont(Font.loadFont(fontPath,20));
                //status.setFont(Font.loadFont(fontPath,20));
                Pane pane =new BorderPane();
                pane.getChildren().addAll(status);
                group.setTop(pane);
            });
        });
        presenter.getCenterImage().filter(imageView -> imageView != null).subscribe(imageView1 -> {
            Platform.runLater(() -> {
                imageView1.setLayoutX(412);
                imageView1.setLayoutY(350);
                Pane bp = new BorderPane();
                bp.getChildren().addAll(imageView1);
                group.setCenter(bp);
            });
        });
        presenter.getError().subscribe(s -> {
            Platform.runLater(() -> {
                if (new Resources().getDebug()) {
                    Text text = new Text();
                    text.setText(s);
                    text.setLayoutX(650);
                    text.setLayoutY(200);
                    text.setFont(Font.loadFont(fontPath,30));
                    //text.setFont(Font.loadFont(fontPath, 30));
                    text.setFill(Color.RED);
                    Pane pane = new BorderPane();
                    pane.getChildren().addAll(text);
                    group.setLeft(pane);
                }
            });
        });
        presenter.getCoords().subscribe(s->{
            Platform.runLater(()->{
                Text text =new Text(s);
                text.setLayoutX(73);
                text.setLayoutY(-550);
                text.setFont(Font.loadFont(fontPath,20));
                //text.setFont(Font.loadFont(fontPath, 20)); //101,201,139
                text.setFill(Color.rgb(101,201,139));
                Pane pane = new BorderPane();
                pane.getChildren().addAll(text);
                group.setBottom(pane);
            });
        });
        Scene scene = new Scene(group);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //    private void initText(BorderPane group){
//        Pane bp = new BorderPane();
//        labelX.setLayoutX(200);
//        labelX.setLayoutY(200);
//        labelX.setFill(Color.rgb(51,204,102));
//        labelX.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
//        bp.getChildren().addAll(labelX);
//        group.setLeft(bp);
//
//    }
    public static void main(String[] args) {
        launch(args);
    }
}
