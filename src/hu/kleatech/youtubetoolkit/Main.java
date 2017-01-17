//author: Ádám Bozzay
//build: development only

package hu.kleatech.youtubetoolkit;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Stage window = primaryStage;
        window.setTitle("Title");
        window.setOnCloseRequest(e -> {
            e.consume();
	    window.close();
	});

	BorderPane layout = new BorderPane();
	layout.setPadding(new Insets(10));

	CmdArea cmdArea = new CmdArea();
	layout.setBottom(cmdArea);

	Scene scene = new Scene(layout, 640, 480);
	window.setScene(scene);
	window.show();

        cmdArea.positionCaret(cmdArea.getLength());
    }
}