module dk.easv.mymovie {
    requires javafx.controls;
    requires javafx.fxml;


    opens dk.easv.mymovie to javafx.fxml;
    exports dk.easv.mymovie;
}