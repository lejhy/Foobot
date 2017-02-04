
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.TreeSet;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Foobot");
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.getColumnConstraints().add(new ColumnConstraints(60));

		Scene scene = new Scene(grid, 325, 375);
		primaryStage.setScene(scene);
		
		Text scenetitle = new Text("Welcome");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);
		
		HashMap<String, String[]> deviceMap = new HashMap<String, String[]>();
		Properties properties = new Properties();
		File configFile = new File("Foobot.config");
		
		try {
			FileInputStream configFileInputStream = new FileInputStream(configFile);
			properties.loadFromXML(configFileInputStream);
			for (String key: properties.stringPropertyNames()){
				deviceMap.put(key, properties.getProperty(key).split(", "));
			}
			configFileInputStream.close();
			properties.clear();
		} catch (InvalidPropertiesFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		
		Label deviceLabel = new Label("Device");
		grid.add(deviceLabel, 0, 1);
		
		ComboBox<String> deviceComboBox = new ComboBox<String>();
		deviceComboBox.getItems().addAll(new TreeSet<String>(deviceMap.keySet()));
		deviceComboBox.setEditable(true); 
		grid.add(deviceComboBox, 1, 1);
	    
		Label uuidLabel = new Label("UUID");
		grid.add(uuidLabel, 0, 2);
		
		TextField uuidTextField = new TextField("");
		grid.add(uuidTextField, 1, 2);

		Label startLabel = new Label("Start");
		grid.add(startLabel, 0, 3);
		
		TextField startTextField = new TextField("01/01/2017 00:00");
		grid.add(startTextField, 1, 3);
		
		Label endLabel = new Label("End");
		grid.add(endLabel, 0, 4);
		
		TextField endTextField = new TextField("01/02/2017 00:00");
		grid.add(endTextField, 1, 4);
		
		Label serverLabel = new Label("Server");
		grid.add(serverLabel, 0, 5);
		
		TextField serverTextField = new TextField("");
		grid.add(serverTextField, 1, 5);
		
		Label timeZoneLabel = new Label("Time Zone");
		grid.add(timeZoneLabel, 0, 6);
		
		TextField timeZoneTextField = new TextField("");
		grid.add(timeZoneTextField, 1, 6);
		
		Label keyLabel = new Label("Key");
		grid.add(keyLabel, 0, 7);
		
		TextField keyTextField = new TextField("");
		grid.add(keyTextField, 1, 7);
		
		Label fileNameLabel = new Label("File");
		grid.add(fileNameLabel, 0, 8);
		
		TextField fileNameTextField = new TextField("Output.csv");
		grid.add(fileNameTextField, 1, 8);
		
		Button addBtn = new Button("Add");
		Button removeBtn = new Button("Remove");
		Button submitBtn = new Button("Submit");
		HBox hbControls = new HBox(10);
		hbControls.setAlignment(Pos.BOTTOM_RIGHT);
		hbControls.getChildren().add(addBtn);
		hbControls.getChildren().add(removeBtn);
		hbControls.getChildren().add(submitBtn);
		grid.add(hbControls, 0, 9, 2, 1);
		
		final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 10);
        
        deviceComboBox.setOnAction((event) -> {
		    uuidTextField.setText(deviceMap.get(deviceComboBox.getSelectionModel().getSelectedItem())[0]);
		    serverTextField.setText(deviceMap.get(deviceComboBox.getSelectionModel().getSelectedItem())[1]);
		    timeZoneTextField.setText(deviceMap.get(deviceComboBox.getSelectionModel().getSelectedItem())[2]);
		    keyTextField.setText(deviceMap.get(deviceComboBox.getSelectionModel().getSelectedItem())[3]);
		});
        
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
       	 
            @Override
            public void handle(ActionEvent e) {
            	deviceMap.put(deviceComboBox.getValue(), new String[]{uuidTextField.getText(), serverTextField.getText(), timeZoneTextField.getText(), keyTextField.getText()});
            	deviceComboBox.getItems().clear();
            	deviceComboBox.getItems().addAll(new TreeSet<String>(deviceMap.keySet()));
            }
        });
        
        removeBtn.setOnAction(new EventHandler<ActionEvent>() {
         	 
            @Override
            public void handle(ActionEvent e) {
            	if (deviceMap.remove(deviceComboBox.getValue()) == null){
            		actiontarget.setFill(Color.FIREBRICK);
            		actiontarget.setText("Such device never existed");
            	} else {
            		deviceComboBox.getItems().clear();
            	   	deviceComboBox.getItems().addAll(new TreeSet<String>(deviceMap.keySet()));
            	}
            }
        });
        
        submitBtn.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent e) {
            	String[] device = {uuidTextField.getText(), serverTextField.getText(), timeZoneTextField.getText(), keyTextField.getText()};
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText(JsonReader.createFile(
                		device[0],
                		startTextField.getCharacters().toString(),
                		endTextField.getCharacters().toString(),
                		device[1],
                		device[2],
                		device[3],
                		fileNameTextField.getCharacters().toString()
                ));
            }
        });
        
        primaryStage.setOnCloseRequest(event -> {
        	FileOutputStream configFileOutputStream;
			try {
				configFileOutputStream = new FileOutputStream(configFile);
				for(String key: new TreeSet<String>(deviceMap.keySet())){
					properties.setProperty(key, deviceMap.get(key)[0] + ", " + deviceMap.get(key)[1] + ", " + deviceMap.get(key)[2] + ", " + deviceMap.get(key)[3]);
				}
				properties.storeToXML(configFileOutputStream, null);
				configFileOutputStream.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NullPointerException e1){
				// TODO Auto-generated catch block
			} catch (ClassCastException e1) {
				// TODO Auto-generated catch block
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}
        });
        
        primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
