import javax.swing.*;
import java.text.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.text.DecimalFormat;

public class CompileInventory {
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

    for (int i = 0; i < files; i++) {
      System.out.println(storedValues[i]);

    }
    System.out.println("Total: $"+f.format(total));

  }

  public static void main(String[] args) {
    calculateTotal();
  }
}
