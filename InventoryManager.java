/*

NOTES:

Clear the buffer or something, idk

Scanner is taking in that the value is what is in the file and not updating
with the reflected changes

i.e: it has a value of 2 saved on file, you add 4, it becomes 6.
Another instance of the same UPC pops up, you add 8 this time, it becomes 10.
This is because the Scanner thinks it's still 2 and not updating.


EDIT - It's because I'm reading from the excel file

*/






import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import java.awt.*;
import javax.swing.*;
import java.beans.*;
import java.awt.event.*;
import java.text.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;


public class InventoryManager extends Application {
    static String fileName = "Library.xls";
    static String storage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        setUserAgentStylesheet(STYLESHEET_MODENA);

        primaryStage.setTitle("Inventory Manager");
        GridPane grid = new GridPane();
        //grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(7);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Inventory Manager");
        scenetitle.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label upcLabel = new Label("UPC:");
        upcLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        grid.add(upcLabel, 0, 3);

        TextField upcTextField = new TextField();
        upcTextField.setMaxWidth(95);
        grid.add(upcTextField, 3, 3);


        Label priceLabel = new Label("Price:");
        priceLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        grid.add(priceLabel, 0, 4);

        TextField priceTextField = new TextField();
        priceTextField.setMaxWidth(60);
        grid.add(priceTextField, 3, 4);

        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        grid.add(quantityLabel, 0, 5);

        TextField quantityTextField = new TextField();
        quantityTextField.setMaxWidth(45);
        grid.add(quantityTextField, 3, 5);

        upcTextField.textProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue.length() > 11) {

            upcTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
              @Override
              public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                  System.out.println(upcTextField.getText());
                  try {
                  String testy = Files.readAllLines(Paths.get("Library/UPC/"+ upcTextField.getText() +".txt")).get(1);
                  storage = Files.readAllLines(Paths.get("Library/UPC/"+ upcTextField.getText() +".txt")).get(0);
                                    quantityTextField.requestFocus();
                                    priceTextField.setText(testy);
                  }
                  catch(IOException ex) {
                    priceTextField.requestFocus();
                  }
                }
              }
            });
          }
        });

        Button btn = new Button("Add");
        btn.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 9);

        quantityTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
          @Override
            public void handle(KeyEvent ke) {
              if (ke.getCode().equals(KeyCode.ENTER)) {
                btn.requestFocus();
                btn.fire();
            }
          }
        });

        final Text outputText = new Text();
        outputText.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        grid.add(outputText, 0, 10);

        final ComboBox departmentComboBox = new ComboBox();
        departmentComboBox.getItems().addAll(
            "Grocery",
            "Dairy",
            "Frozen"
        );

        Label departmentLabel = new Label("Department:                           ");
        departmentLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        grid.add(departmentLabel, 0, 1);
        grid.add(departmentComboBox, 3, 1);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
              outputText.setFill(Color.FIREBRICK);
              outputText.setText("Added: " +upcTextField.getText()+ " > "+departmentComboBox.getSelectionModel().getSelectedItem().toString());

              try {
                boolean containsString = true;
                String searchWord = upcTextField.getText();
                Scanner readFile = new Scanner(new File("Library.xls"));
                String upcFileName = "Library/UPC/"+ upcTextField.getText() +".txt";
                FileWriter upcWriter = new FileWriter(upcFileName, false);

                readFile.useDelimiter("\\t+");

                while(readFile.hasNext())
                  if(searchWord.equals(readFile.next())) {
                    System.out.println(readFile.next());
                      containsString = false;
                      //readFile.close();
                      break;

                  }
                  if(!containsString) {
                      JOptionPane.showMessageDialog(null,"The UPC already exists,\n "+
                                                  "please try again.");
                    String temp = String.valueOf(
                    (Integer.parseInt(quantityTextField.getText()) + Integer.parseInt(storage))
                    );

                  //  Scanner readUPC = new Scanner(new File(upcFileName));
                  //  String test = readUPC.next();
                    //System.out.println(test);
                    //readUPC.close();
                    upcWriter.write(temp);
                    upcWriter.write(System.getProperty( "line.separator" ));
                    upcWriter.write(priceTextField.getText());
                    upcWriter.close();

                  }else {
                    FileWriter fileWriter = new FileWriter(fileName, true);
                    fileWriter.write(System.getProperty( "line.separator" ));
                    fileWriter.write(departmentComboBox.getSelectionModel().getSelectedItem().toString());
                    fileWriter.write("\t"+ upcTextField.getText());
                    fileWriter.write("\t"+ priceTextField.getText());
                    fileWriter.write("\t"+ quantityTextField.getText());
                    fileWriter.close();


                    upcWriter.write(quantityTextField.getText());
                    upcWriter.write(System.getProperty( "line.separator" ));
                    upcWriter.write(priceTextField.getText());
                    upcWriter.close();
                    JOptionPane.showMessageDialog(null,"Entry added!");

                  }
              }
              catch(IOException ex) {

                  JOptionPane.showMessageDialog(null,"Error writing to file '" + fileName + "', make sure Excel is closed. Folder/File may also not exist!");
              }

                upcTextField.clear();
                priceTextField.clear();
                quantityTextField.clear();
                upcTextField.requestFocus();
            }
        });

        Scene scene = new Scene(grid, 550, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
