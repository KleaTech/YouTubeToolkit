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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application{

	private Button button;

	private ArrayList<String> lastcmd = new ArrayList<>(32);
	private int lastcmdposition = 0;

	TextArea cmdArea;

	public static void main(String[] args) {launch(args);}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Stage window = primaryStage;
		window.setTitle("Title");
		window.setOnCloseRequest(e -> {
			e.consume();
		    System.err.println("Program closed");
		    window.close();});

		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(10));

		//Need fixing
		cmdArea = new TextArea("ping 192.168.1.1");
		    cmdArea.setFont(Font.font("Consolas"));
			cmdArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
		        @Override
		        public void handle(KeyEvent ke) {
                    if (ke.getCode().equals(KeyCode.ENTER)) {
						onCommandRequest(cmdArea);
					}
					else if (ke.getCode().equals(KeyCode.UP)) {
						ke.consume();
						if (lastcmdposition > 0) {lastcmdposition--;}
						updateTextAreaLastLine(lastcmd.get(lastcmdposition), cmdArea);
					}
					else if (ke.getCode().equals(KeyCode.DOWN)) {
						ke.consume();
						if (lastcmdposition < lastcmd.size()-1) { lastcmdposition++; }
						updateTextAreaLastLine(lastcmd.get(lastcmdposition), cmdArea);
					}
                }
            });
		layout.setBottom(cmdArea);

		button = new Button("OK");
		button.setOnAction(e -> onCommandRequest(cmdArea));
		layout.setRight(button);

		Scene scene = new Scene(layout, 640, 480);
		window.setScene(scene);
		window.show();
	}

	private int getLastLine(TextArea textArea) {
		int lastline = cmdArea.getText().lastIndexOf('\n');
		if (lastline == -1) { lastline = 0; }
		return lastline;
	}

	private void onCommandRequest(TextArea cmdArea) {
		cmdArea.setEditable(false);

		lastcmd.add(cmdArea.getText(getLastLine(cmdArea), cmdArea.getLength()));
		lastcmdposition = lastcmd.size()-1;
		executeCommand("cmd /c" + lastcmd.get(lastcmdposition), cmdArea);
		cmdArea.positionCaret(cmdArea.getLength());
		cmdArea.setEditable(true);
	}

	@SuppressWarnings("NestedAssignment")
	private void executeCommand(String command, TextArea output) {
		Thread thread = new Thread(() -> {
		    updateTextArea("\n", output, true);
		    Process p;
		    try {
			    p = Runtime.getRuntime().exec(command);
			    p.waitFor(1, TimeUnit.SECONDS);
			    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
    			String line;
	    		while ((line = reader.readLine())!= null) { updateTextArea(line + "\n", output, true); }
		    } catch (Exception e) {
			    System.err.println("Error: " + e.toString());
		    }
		});
		thread.start();
	}

	private void updateTextArea(String text, TextArea outputTextArea, boolean append) {
		if (Platform.isFxApplicationThread()) {
			if (append) { outputTextArea.appendText(text); }
			else { outputTextArea.setText(text); }
		}
		else {
			Platform.runLater(() -> {
		        if (append) { outputTextArea.appendText(text); }
			    else { outputTextArea.setText(text); }}
			);
		}
	}

	private void updateTextAreaLastLine(String command, TextArea outputTextArea) {
		if (Platform.isFxApplicationThread()) {
			outputTextArea.replaceText(getLastLine(cmdArea)+1, cmdArea.getLength(), command);
		}
		else {
			Platform.runLater(() -> {
		        outputTextArea.replaceText(getLastLine(cmdArea), cmdArea.getLength(), command);
			});
		}
	}
}
