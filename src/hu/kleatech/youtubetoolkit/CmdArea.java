//author: Ádám Bozzay
//Build: development only

package hu.kleatech.youtubetoolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CmdArea extends TextArea{
    
    private final ArrayList<String> commandlist = new ArrayList<>(32);
    private int posincommandlist = 0;
    
    public CmdArea() {
        initialize();
    }
    
    private void initialize() { 
        positionCaret(getLength());
        setOnKeyPressed((KeyEvent ke) -> {
            KeyCode kc = ke.getCode();
            switch (kc) {
                case ENTER:
                    ke.consume();
                    commandRequest();
                    break;
                case UP:
                    ke.consume();
                    if (posincommandlist > 0) { posincommandlist--; }
                    updateTextArea(commandlist.get(posincommandlist), UpdateMode.LASTLINE);
                    break;
                case DOWN:
                    ke.consume();
                    if (posincommandlist < commandlist.size()-1) { posincommandlist++; }
                    updateTextArea(commandlist.get(posincommandlist), UpdateMode.LASTLINE);
                    break;
                default:
                    break;
            }
        });
    }
    
    public int getLastLine() { return getText().lastIndexOf('\n'); }

    public void commandRequest() {
        commandlist.add(getText(getLastLine()+1, getLength()));
        posincommandlist = commandlist.size();
        executeCommand("cmd /c" + commandlist.get(posincommandlist-1));
        positionCaret(getLength());
    }

    @SuppressWarnings("NestedAssignment")
    private void executeCommand(String command) {
        Thread thread = new Thread(() -> {
            updateTextArea("\n", UpdateMode.APPEND);
            Process p;
            try {
                p = Runtime.getRuntime().exec(command);
	        p.waitFor(5, TimeUnit.SECONDS);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
    	        String line;
	        while ((line = reader.readLine())!= null) { updateTextArea(line + "\n", UpdateMode.APPEND); }
            } catch (IOException | InterruptedException e) {
	        System.err.println("Error: " + e.toString());
	    }
	});
	thread.start();
    }

    private void updateTextArea(String text, UpdateMode mode) {
	switch (mode) {
            case APPEND:
                appendText(text);
                break;
            case REWRITE:
                setText(text);
                break;
            case LASTLINE:
                replaceText(getLastLine(), getLength(), text);
                break;
            default:
                break;
        }
    }
    
    private enum UpdateMode { APPEND, REWRITE, LASTLINE }
}