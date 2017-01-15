//author: Ádám Bozzay
//Build: development only

package hu.kleatech.youtubetoolkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application{

	Button button;

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

		TextArea input = new TextArea("dir");
		    input.setFont(Font.font("Consolas"));
		layout.setBottom(input);

		TextArea output = new TextArea();
		    output.setEditable(false);
			output.setFont(Font.font("Consolas"));
		layout.setCenter(output);

		button = new Button("OK");
		button.setOnAction(e -> executeCommand("cmd /c" + input.getText(), output));
		layout.setRight(button);

		Scene scene = new Scene(layout, 640, 480);
		window.setScene(scene);
		window.show();
	}

	@SuppressWarnings("NestedAssignment")
	private void executeCommand(String command, TextArea output) {
		Thread thread = new Thread(() -> {
		    updateTextArea("", output, false);
		    Process p;
		   try {
			    p = Runtime.getRuntime().exec(command);
			    p.waitFor(1, TimeUnit.SECONDS);
			    System.err.println("Line reached");
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
}
