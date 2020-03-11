
package visualizealgorithms.gui;

//Java imports
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

//Project imports
import visualizealgorithms.bll.algorithm.sorting.BubbleSort;
import visualizealgorithms.bll.algorithm.IAlgorithm;
import visualizealgorithms.bll.algorithm.sorting.RadixSort;
import visualizealgorithms.util.NumberGenerator;

/**
 *
 * @author Søren Spangsberg
 */
public class MainWindowController implements Initializable {

    @FXML
    private Button btnStartAction;
    @FXML
    private ListView lvAlgorithms;
    @FXML
    private AnchorPane rightAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //only enable button if user clicked an algorithm
        lvAlgorithms.setOnMouseClicked((event) -> {
            btnStartAction.setDisable(lvAlgorithms.getSelectionModel().getSelectedIndex() == -1);
        });

        lvAlgorithms.getItems().add(new BubbleSort());
        lvAlgorithms.getItems().add(new RadixSort());
        
        //add other algorithms
    }

    /**
     * Convenience method that measures the time it takes the algorithm to do
     * its work.
     *
     * @param algorithm
     * @param data
     * @param series
     */
    private void runTask(IAlgorithm algorithm, int[] data, XYChart.Series series) {

        long startTime = System.nanoTime();

        algorithm.setData(data);
        algorithm.doWork();

        long endTime = System.nanoTime();

        long durationInNano = (endTime - startTime);
        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(durationInNano);

        series.getData().add(new XYChart.Data(data.length, elapsedTime));
        
        //create and setup XY plot
        XYChart.Data<Number, Number> xyPlot = new XYChart.Data(data.length, elapsedTime);
        xyPlot.setNode(new HoveredThresholdNode(data.length, elapsedTime)); //create tooltip in the chart
        series.getData().add(xyPlot);   
        
    }

    /**
     * 
     * @return 
     */
    private LineChart<?,?> buildChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("# of elements");
        yAxis.setLabel("ms");

        //creating the chart
        LineChart<?, ?> lineChart = new LineChart<Number, Number>(xAxis, yAxis);        
        lineChart.setLegendVisible(false);

        //setup chart to fit parent (anchor pane)
        AnchorPane.setTopAnchor(lineChart, 0d);
        AnchorPane.setLeftAnchor(lineChart, 0d);
        AnchorPane.setRightAnchor(lineChart, 0d);
        AnchorPane.setBottomAnchor(lineChart, 0d);
        
        return lineChart;
    }

    
    /**
     * Do action and build line chart with result
     *
     * @param event
     */
    @FXML
    private void handleStartAction(ActionEvent event) {
        IAlgorithm selectedAlgorithm = (IAlgorithm) lvAlgorithms.getSelectionModel().getSelectedItem();
        NumberGenerator ng = NumberGenerator.getInstance();

        if (selectedAlgorithm != null) {

            LineChart<?, ?> lineChart = buildChart();
            XYChart.Series series = new XYChart.Series();
            
            runTask(selectedAlgorithm, ng.generateRandomNumbers(1000, 1), series);
            runTask(selectedAlgorithm, ng.generateRandomNumbers(2000, 1), series);
            //add more runs...
            
            //Optimizations:
            //FIXME: allow user to input # of elements from GUI... (1000;2000;4000;8000;16000) etc.
            //FIXME: print out precise execution times for each
            //FIXME: include support for sorting/searching BE-instances
            //FIXME: GUI-freeze. Implement a second Thread for the algorithm and use platform.runLater()...
            
            lineChart.getData().add(series);

            rightAnchorPane.getChildren().clear();
            rightAnchorPane.getChildren().add(lineChart);
        }
    }

    
    @FXML
    private void handleClose(ActionEvent event) {
        System.exit(0);
    }
}
