module dk.easv.mymovie {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.microsoft.sqlserver.jdbc;
    requires java.sql;


    opens dk.easv.mymovie to javafx.fxml;
    exports dk.easv.mymovie;
}