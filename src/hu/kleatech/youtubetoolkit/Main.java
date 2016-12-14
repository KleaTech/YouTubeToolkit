package hu.kleatech.youtubetoolkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
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
		layout.setBottom(input);

		TextArea output = new TextArea();
		output.setEditable(false);
		layout.setCenter(output);

		button = new Button("OK");
		button.setOnAction(e -> output.setText(executeCommand("cmd /c " + input.getText())));
		layout.setRight(button);

		Scene scene = new Scene(layout, 640, 480);
		window.setScene(scene);
		window.show();
	}

	@SuppressWarnings({"NestedAssignment", "StringConcatenationInsideStringBufferAppend", "StringBufferWithoutInitialCapacity"})
	private String executeCommand(String command) {
		StringBuilder output = new StringBuilder();
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = reader.readLine())!= null) { output.append(line + "\n"); }
		} catch (Exception e) {
			return "Error: " + e.toString();
		}
		return output.toString();
	}
}
