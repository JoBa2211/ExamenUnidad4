// Importa las clases necesarias para la interfaz, el controlador, el modelo y utilidades
import view.MainFrame;
import controller.MainController;
import model.EntityModel;
import util.JsonExport;
import java.util.ArrayList;
import java.util.List;
import model.MainModel;

public class Main {
    public static void main(String[] args) {
        // Crea la ventana principal de la aplicación (la vista)
        MainFrame view = new MainFrame();

        // Crea el modelo principal, que carga los datos desde el JSON al iniciar
        MainModel mainModel = MainModel.getInstance();

        // Crea el controlador principal, que conecta la vista y el modelo
        MainController controller = new MainController(view, mainModel);

        // Agrega un listener para que, al cerrar la ventana, se guarde la lista en el JSON
        view.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // Guarda los datos actuales de la lista en el archivo JSON
                mainModel.save();
            }
        });

        // Hace visible la ventana principal
        view.setVisible(true);
    }
}
