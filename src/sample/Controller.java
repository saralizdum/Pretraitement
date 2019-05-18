package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
//import org.apache.tika.exception.TikaException;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.parser.AutoDetectParser;
//import org.apache.tika.parser.ParseContext;
//import org.apache.tika.parser.Parser;
//import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller  implements Initializable {

    Statement stm;
    ResultSet Rs;
    DefaultTableModel model=new DefaultTableModel();


    @FXML
    private Label label;
    @FXML
    private Button button;
    //    @FXML
//    private TextArea textArea;
    public void initialize() {
        // TODO
    }
    final FileChooser fileChooser = new FileChooser();
    @FXML
    Stage primaryStage;
    @FXML
    private void handleButtonAction(ActionEvent event) throws Throwable {

//        textArea.clear();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            main2(event,file);
            List<File> files = Arrays.asList(file);
//            printLog(textArea, files);
        } }

    @FXML
    private void handleButtonAction2(ActionEvent event) throws SAXException, IOException {

//        textArea.clear();

        List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);
//        printLog(textArea, list);
        if (list != null) {
            list.stream().forEach((file) -> {

                try {
                    main2(event,file);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            });
        }
    }
    //    private void printLog(TextArea textArea, List<File> files) {
//        if (files == null || files.isEmpty()) {
//            return;
//        }
//        for (File file : files) {
//            textArea.appendText(file.getAbsolutePath() + "\n");
//        }
//    }
    public void main2(ActionEvent event,File file) throws Throwable {

        String path =file.getAbsolutePath() ;
        String[] ext=path.split("\\.");
//        Parser parser=new AutoDetectParser();
        if(ext[0].equalsIgnoreCase("png")||ext[0].equalsIgnoreCase("jpg")||ext[0].equalsIgnoreCase("pdf"))
        {
//
            extractPdfImage(path);
//            extractFromFile(path);

        }else
        {
////
            extractPdfImage(path);
//            extractFromFile(path);
        }
    }
//    public  void extractFromFile(final String fileName) throws Throwable {
//
//
//   }


    public  void extractPdfImage(final String fileName)throws IOException {
        File image = new File(fileName);
        Tesseract tessInst = new Tesseract();
        try {
            tessInst.setDatapath("C:\\Users\\amgsoft\\Desktop\\tess\\tessdata");
            String result = tessInst.doOCR(image);
            System.out.println(result);
            File output=new File(fileName+".txt");
            File targetLocation = new File("C:\\Users\\amgsoft\\Desktop\\Pretraitement\\input\\" + "/"+output.getName());

            PrintStream p=new PrintStream(targetLocation);
            p.println(result);
            TfidfCalculation tfidfCalculation=new TfidfCalculation();
            tfidfCalculation.main4();
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}