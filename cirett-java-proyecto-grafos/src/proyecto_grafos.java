import java.io.IOException;
import java.util.Scanner;
 
/**
 * Punto de entrada del programa. Muestra el menu, lo repite hasta que se
 * elija Salir, y por ahora solo tiene implementada la opcion de Cargar
 * laberinto (opciones 2-5 quedan como pendientes hasta el siguiente paso,
 * cuando se agreguen el grafo y los algoritmos).
 */
public class proyecto_grafos {
 
    private static final String RUTA_LABERINTO =
            "C:\\Users\\adria\\Downloads\\tengo que salvar el semestre\\cirett-java-proyecto-grafos\\src\\laberintos\\LABERINTO_1.txt";
 
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
 
        // Mientras no se haya cargado nada, este valor permanece null.
        // Las opciones 2 a 5 deben revisar esto antes de hacer cualquier cosa.
        Laberinto laberintoActual = null;
 
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String textoOpcion = teclado.nextLine();
            int opcion;
 
            // Si el usuario no escribe un numero, no debe tronar el programa:
            // se informa el error y se vuelve a mostrar el menu.
            try {
                opcion = Integer.parseInt(textoOpcion.trim());
            } catch (NumberFormatException e) {
                System.out.println("Opcion invalida: debe ingresar un numero del 1 al 6.\n");
                continue;
            }
 
            switch (opcion) {
                case 1:
                    // Cargar laberinto: si tiene exito, laberintoActual se reemplaza
                    // (esto automaticamente "descarta" cualquier resultado anterior,
                    // porque los resultados de DFS/BFS/Dijkstra se guardaran junto
                    // con el laberinto al que pertenecen, en el siguiente paso).
                    laberintoActual = cargarLaberinto(laberintoActual);
                    break;
 
                case 2: // Resolver con DFS
                case 3: // Resolver con BFS
                case 4: // Resolver con Dijkstra
                case 5: // Mostrar las tres soluciones
                    if (laberintoActual == null) {
                        System.out.println("Primero debe cargar un laberinto (opcion 1).\n");
                    } else {
                        // TODO: se implementara cuando agreguemos Grafo, DFS, BFS y Dijkstra.
                        System.out.println("Esta opcion todavia no esta implementada.\n");
                    }
                    break;
 
                case 6:
                    salir = true;
                    System.out.println("Hasta luego.");
                    break;
 
                default:
                    System.out.println("Opcion invalida: debe ingresar un numero del 1 al 6.\n");
            }
        }
 
        teclado.close();
    }
 
    /**
     * Imprime las opciones del menu principal.
     */
    private static void mostrarMenu() {
        System.out.println("===== Menu del laberinto =====");
        System.out.println("1. Cargar laberinto");
        System.out.println("2. Resolver laberinto con DFS");
        System.out.println("3. Resolver laberinto con BFS");
        System.out.println("4. Resolver laberinto con Dijkstra (camino mas corto)");
        System.out.println("5. Mostrar las tres soluciones");
        System.out.println("6. Salir");
        System.out.print("Elija una opcion: ");
    }
 
    /**
     * Intenta cargar el laberinto desde RUTA_LABERINTO (por ahora fija en el
     * codigo; no se le pide al usuario que la escriba).
     *
     * Si falla, informa el motivo y regresa el MISMO laberinto que ya
     * estaba cargado (sin modificarlo), tal como pide el enunciado.
     * Si tiene exito, muestra el laberinto cargado (tal cual, sin marcas) y
     * reporta por separado las coordenadas de entrada y salida.
     */
    private static Laberinto cargarLaberinto(Laberinto laberintoAnterior) {
        try {
            Laberinto nuevo = Laberinto.cargarDesdeArchivo(RUTA_LABERINTO);
            System.out.println("Laberinto cargado correctamente (" + nuevo.getNumFilas()
                    + " renglones x " + nuevo.getNumColumnas() + " columnas).");
            nuevo.imprimir();
            int[] entrada = nuevo.getEntrada();
            int[] salida = nuevo.getSalida();
            System.out.println("Entrada: fila " + entrada[0] + ", columna " + entrada[1]);
            System.out.println("Salida:  fila " + salida[0] + ", columna " + salida[1]);
            System.out.println();
            return nuevo;
        } catch (IOException e) {
            System.out.println("No se pudo cargar el laberinto: " + e.getMessage());
            System.out.println("Se conserva el laberinto anterior.\n");
            return laberintoAnterior;
        }
    }
}
 