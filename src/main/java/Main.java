import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/MyMovieView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MyMovie");

        ListView<String> listView = new ListView<>();
        for (int i = 1; i <= 20 ; i++) {
            String item = "Item "+i ;
            listView.getItems().add(item);
        }

        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) ->
                        System.out.println("Check box for "+item+" changed from "+wasSelected+" to "+isNowSelected)
                );
                return observable ;
            }
        }));

        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
