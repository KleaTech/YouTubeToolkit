//author: Ádám Bozzay
//Build: development only

package hu.kleatech.youtubetoolkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application{

    private ArrayList<String> commandlist = new ArrayList<>(32);
    private int posincommandlist = 0;

    TextArea cmdArea;

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

	cmdArea = new TextArea();
            cmdArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent ke) {
                    KeyCode kc = ke.getCode();
                    switch (kc) {
                        case ENTER:
                            ke.consume();
                            onCommandRequest(cmdArea);
                            break;
                        case UP:
                            ke.consume();
                            if (posincommandlist > 0) { posincommandlist--; }
                            updateTextAreaLastLine(commandlist.get(posincommandlist), cmdArea);
                            break;
                        case DOWN:
                            ke.consume();
                            if (posincommandlist < commandlist.size()-1) { posincommandlist++; }
                            updateTextAreaLastLine(commandlist.get(posincommandlist), cmdArea);
                            break;
                        default:
                            break;
                    }
                }
            });
	layout.setBottom(cmdArea);

	Scene scene = new Scene(layout, 640, 480);
	window.setScene(scene);
	window.show();

        cmdArea.positionCaret(cmdArea.getLength());
    }

    private int getLastLine(TextArea textArea) { return textArea.getText().lastIndexOf('\n'); }

    private void onCommandRequest(TextArea cmdArea) {
        commandlist.add(cmdArea.getText(getLastLine(cmdArea)+1, cmdArea.getLength()));
        posincommandlist = commandlist.size();
        executeCommand("cmd /c" + commandlist.get(posincommandlist-1), cmdArea);
        cmdArea.positionCaret(cmdArea.getLength());
    }

    @SuppressWarnings("NestedAssignment")
    private void executeCommand(String command, TextArea output) {
        Thread thread = new Thread(() -> {
            updateTextArea("\n", output, true);
            Process p;
            try {
                p = Runtime.getRuntime().exec(command);
	        p.waitFor(5, TimeUnit.SECONDS);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
    	        String line;
	        while ((line = reader.readLine())!= null) { updateTextArea(line + "\n", output, true); }
            } catch (Exception e) {
	        System.err.println("Error: " + e.toString());
	    }
	});
	thread.start();
    }

    private void updateTextArea(String text, TextArea textArea, boolean append) {
	if (append) {
            if (Platform.isFxApplicationThread()) { textArea.appendText(text); }
	    else { Platform.runLater(() -> textArea.appendText(text)); }
	}
	else if (!append) {
	    if (Platform.isFxApplicationThread()) { textArea.setText(text); }
	    else { Platform.runLater(() -> textArea.setText(text)); }
	}
    }

    private void updateTextAreaLastLine(String text, TextArea textArea) {
	if (Platform.isFxApplicationThread()) {
            textArea.replaceText(getLastLine(cmdArea)+1, cmdArea.getLength(), text);
	}
	else {
	    Platform.runLater(() -> {
	        textArea.replaceText(getLastLine(cmdArea), cmdArea.getLength(), text);
	    });
	}
    }
}