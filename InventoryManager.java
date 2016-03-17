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

import javax.swing.*;
import java.text.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.awt.Desktop;

public class InventoryManager extends Application {

    static String fileName = "Library.xls";
    static String quantityFromFile;
    static String priceFromFile;

    public static void main(String[] args) {
        launch(args);
    }
    static int files;
    static String[] storedValues;
    static double total;

    public static void calculateTotal() {
      File folder = new File("Library/UPC/");
      File[] listOfFiles = folder.listFiles();
      DecimalFormat f = new DecimalFormat("##.00");

      try {
        files = (int)Files.list(Paths.get("Library/UPC/")).count();
        storedValues = new String[files];
        FileWriter fileWriter = new FileWriter("Output.xls", false);

        for (int i = 0; i < listOfFiles.length; i++) {
          storedValues[i] = String.valueOf(
            f.format(
              Double.parseDouble(Files.readAllLines(Paths.get("Library/UPC/"+listOfFiles[i].getName())).get(0)) *
              Double.parseDouble(Files.readAllLines(Paths.get("Library/UPC/"+listOfFiles[i].getName())).get(1)))
          );
          total += Double.parseDouble(storedValues[i]);

          fileWriter.write(String.valueOf(Files.readAllLines(Paths.get("Library/UPC/"+listOfFiles[i].getName())).get(2)));
          fileWriter.write("\t"+String.valueOf(Files.readAllLines(Paths.get("Library/UPC/"+listOfFiles[i].getName())).get(1)));
          fileWriter.write("\t"+String.valueOf(Files.readAllLines(Paths.get("Library/UPC/"+listOfFiles[i].getName())).get(0)));
          fileWriter.write(System.getProperty( "line.separator" ));
          //fileWriter.close();
        }
        fileWriter.write(System.getProperty( "line.separator" ));
        fileWriter.write("TOTAL INVENTORY: $" +f.format(total));
        fileWriter.close();
      }
      catch(IOException ex) {
        System.out.println("THROWN");

      }
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


        Button addBtn = new Button("Add");
        addBtn.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        HBox addHbBtn = new HBox(10);
        addHbBtn.setAlignment(Pos.BOTTOM_CENTER);
        addHbBtn.getChildren().add(addBtn);
        grid.add(addHbBtn, 1, 8);

        Button calculateBtn = new Button("Calculate Inventory");
        calculateBtn.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        HBox calculateHbBtn = new HBox(10);
        calculateHbBtn.setAlignment(Pos.BOTTOM_CENTER);
        calculateHbBtn.getChildren().add(calculateBtn);
        grid.add(calculateHbBtn, 1, 9);

        // Below contains the eventhandlers/Filters for buttons/Keypresses

        priceTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
          @Override
          public void handle(KeyEvent ke) {
            if (ke.getCode().equals(KeyCode.ENTER)) {
              //System.out.println(upcTextField.getText());
                quantityTextField.requestFocus();
              }
            }
        });

        upcTextField.textProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue.length() > 11) {

            upcTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
              @Override
              public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                  //System.out.println(upcTextField.getText());
                  try {
                    priceFromFile = Files.readAllLines(Paths.get("Library/UPC/"+ upcTextField.getText() +".txt")).get(1);
                    quantityFromFile = Files.readAllLines(Paths.get("Library/UPC/"+ upcTextField.getText() +".txt")).get(0);
                    quantityTextField.requestFocus();
                    priceTextField.setText(priceFromFile);
                  }
                  catch(IOException ex) {
                    priceTextField.requestFocus();
                  }
                }
              }
            });
          }
        });

        quantityTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
          @Override
            public void handle(KeyEvent ke) {
              if (ke.getCode().equals(KeyCode.ENTER)) {
                addBtn.requestFocus();
                addBtn.fire();
            }
          }
        });

        calculateBtn  .setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent e) {
            Desktop desktop = Desktop.getDesktop();
            File file = new File("Output.xls");
            calculateTotal();

            try {
              outputText.setText("");
              desktop.open(file);
            }
            catch(IOException ex) {

            }
          }
        });

        addBtn.setOnAction(new EventHandler<ActionEvent>() {

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
                    //System.out.println(readFile.next());
                      containsString = false;
                      //readFile.close();
                      break;

                  }
                  if(!containsString) {
                      //JOptionPane.showMessageDialog(null,"The UPC already exists,\n "+
                                                  //"please try again.");
                    String temp = String.valueOf((Integer.parseInt(quantityTextField.getText()) + Integer.parseInt(quantityFromFile)));
                    upcWriter.write(temp);
                    upcWriter.write(System.getProperty( "line.separator" ));
                    upcWriter.write(priceTextField.getText());
                    upcWriter.write(System.getProperty( "line.separator" ));
                    upcWriter.write(upcTextField.getText());
                    upcWriter.close();

                  }else {
                    FileWriter fileWriter = new FileWriter(fileName, true);
                    fileWriter.write(System.getProperty( "line.separator" ));
                    fileWriter.write(departmentComboBox.getSelectionModel().getSelectedItem().toString());
                    fileWriter.write("\t"+ String.valueOf(upcTextField.getText()));
                    fileWriter.write("\t"+ priceTextField.getText());
                    fileWriter.write("\t"+ quantityTextField.getText());
                    fileWriter.close();

                    upcWriter.write(quantityTextField.getText());
                    upcWriter.write(System.getProperty( "line.separator" ));
                    upcWriter.write(priceTextField.getText());
                    upcWriter.write(System.getProperty( "line.separator" ));
                    upcWriter.write(upcTextField.getText());
                    upcWriter.close();
                    //JOptionPane.showMessageDialog(null,"Entry added!");

                  }
              }
              catch(IOException ex) {
                //JOptionPane.showMessageDialog(null,"Error writing to file '" + fileName + "', make sure Excel is closed. Folder/File may also not exist!");
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
