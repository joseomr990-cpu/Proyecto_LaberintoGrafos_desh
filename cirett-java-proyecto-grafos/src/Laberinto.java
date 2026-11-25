import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un laberinto cargado desde un archivo de texto.
 *
 * En este paso del proyecto, esta clase SOLO se encarga de:
 *   - Leer el archivo linea por linea, conservando espacios.
 *   - Validar dimensiones y caracteres.
 *   - Determinar la entrada y la salida (por marcas E/S o por perimetro).
 *
 * La construccion del grafo (Vertice, Vertices, Arcos, Grafo) y los
 * algoritmos de busqueda (DFS, BFS, Dijkstra) se agregaran en pasos
 * posteriores, una vez que la lectura este resuelta y probada.
 */
public class Laberinto {

    // Conjunto de caracteres permitidos dentro del archivo del laberinto.
    // Cualquier caracter que no este aqui (incluyendo tabulaciones) es invalido.
    // Ya no se incluyen 'E' ni 'S': la entrada y la salida se determinan
    // unicamente por el mecanismo de perimetro.
    private static final String CARACTERES_VALIDOS = "+-| #";

    // Matriz de caracteres tal cual se leyeron del archivo (conserva espacios).
    private char[][] mapa;

    // Dimensiones del laberinto. Pueden ser distintas entre si (rectangular,
    // no necesariamente cuadrado).
    private int numFilas;
    private int numColumnas;

    // Posicion (fila, columna) de la entrada y la salida.
    private int[] entrada; // entrada[0] = fila, entrada[1] = columna
    private int[] salida;  // salida[0]  = fila, salida[1]  = columna

    // Constructor privado: solo se crean instancias a traves de cargarDesdeArchivo,
    // que es quien garantiza que el objeto quede completamente validado.
    private Laberinto() {
    }

    /**
     * Carga y valida un laberinto desde la ruta indicada.
     *
     * Si algo falla, lanza una IOException con un mensaje explicando el motivo.
     * Importante: si esta llamada falla, no se modifica nada del objeto que
     * llama (el metodo no regresa un Laberinto a medio construir); quien
     * invoca este metodo debe conservar el laberinto anterior en ese caso.
     */
    public static Laberinto cargarDesdeArchivo(String ruta) throws IOException {
        List<String> lineas = leerLineas(ruta);

        if (lineas.isEmpty()) {
            throw new IOException("El archivo esta vacio.");
        }

        int filas = lineas.size();
        int columnas = lineas.get(0).length();

        if (columnas == 0) {
            throw new IOException("Los renglones del laberinto no pueden tener longitud cero.");
        }

        // Todos los renglones deben medir exactamente lo mismo que el primero.
        for (int f = 0; f < filas; f++) {
            if (lineas.get(f).length() != columnas) {
                throw new IOException("El renglon " + (f + 1) + " tiene longitud " + lineas.get(f).length()
                        + ", pero el renglon 1 tiene longitud " + columnas
                        + ". Todos los renglones deben medir lo mismo.");
            }
        }

        // Construir la matriz de caracteres, validando que cada uno sea valido.
        char[][] mapaTemp = new char[filas][columnas];
        for (int f = 0; f < filas; f++) {
            String linea = lineas.get(f);
            for (int c = 0; c < columnas; c++) {
                char ch = linea.charAt(c);
                if (CARACTERES_VALIDOS.indexOf(ch) < 0) {
                    throw new IOException("Caracter invalido '" + ch + "' en el renglon " + (f + 1)
                            + ", columna " + (c + 1) + ".");
                }
                mapaTemp[f][c] = ch;
            }
        }

        // Unico mecanismo soportado: perimetro. Se buscan las dos primeras
        // posiciones transitables del borde, recorriendolo de arriba hacia
        // abajo y, en cada renglon, de izquierda a derecha.
        int[][] aberturas = encontrarAberturasDePerimetro(mapaTemp, filas, columnas);
        if (aberturas.length < 2) {
            throw new IOException("El perimetro no tiene al menos dos posiciones transitables "
                    + "para usar como entrada y salida.");
        }
        int[] entradaTemp = aberturas[0];
        int[] salidaTemp = aberturas[1];

        Laberinto laberinto = new Laberinto();
        laberinto.mapa = mapaTemp;
        laberinto.numFilas = filas;
        laberinto.numColumnas = columnas;
        laberinto.entrada = entradaTemp;
        laberinto.salida = salidaTemp;
        return laberinto;
    }

    /**
     * Lee el archivo linea por linea SIN usar trim(), para conservar los
     * espacios iniciales y finales de cada renglon (son parte del laberinto).
     */
    private static List<String> leerLineas(String ruta) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            // Re-lanzamos con un mensaje mas claro para el usuario final.
            throw new IOException("No se pudo leer el archivo '" + ruta + "': " + e.getMessage());
        }
        return lineas;
    }

    /**
     * Indica si un caracter representa una posicion transitable.
     * Para efectos de calculo, '#' se trata igual que un espacio (no es pared
     * ni indica un camino obligatorio a seguir).
     */
    private static boolean esTransitable(char ch) {
        return ch == ' ' || ch == '#';
    }

    /**
     * Recorre el perimetro del laberinto en el orden exigido por el enunciado:
     * de arriba hacia abajo y, dentro de cada renglon, de izquierda a derecha.
     * Regresa, en ese orden, las posiciones transitables encontradas en el
     * borde. Cada esquina se cuenta una sola vez.
     */
    private static int[][] encontrarAberturasDePerimetro(char[][] mapa, int filas, int columnas) {
        List<int[]> aberturas = new ArrayList<>();

        if (filas == 1) {
            // Caso especial: un solo renglon, todo el renglon es borde.
            for (int c = 0; c < columnas; c++) {
                if (esTransitable(mapa[0][c])) {
                    aberturas.add(new int[]{0, c});
                }
            }
            return aberturas.toArray(new int[0][]);
        }

        // 1) Renglon superior completo, de izquierda a derecha (incluye esquinas).
        for (int c = 0; c < columnas; c++) {
            if (esTransitable(mapa[0][c])) {
                aberturas.add(new int[]{0, c});
            }
        }

        // 2) Renglones intermedios: solo columna izquierda y columna derecha.
        for (int f = 1; f < filas - 1; f++) {
            if (esTransitable(mapa[f][0])) {
                aberturas.add(new int[]{f, 0});
            }
            // Si columnas == 1, la columna izquierda y la derecha son la misma
            // celda; evitamos contarla dos veces.
            if (columnas > 1 && esTransitable(mapa[f][columnas - 1])) {
                aberturas.add(new int[]{f, columnas - 1});
            }
        }

        // 3) Renglon inferior completo, de izquierda a derecha (incluye esquinas).
        for (int c = 0; c < columnas; c++) {
            if (esTransitable(mapa[filas - 1][c])) {
                aberturas.add(new int[]{filas - 1, c});
            }
        }

        return aberturas.toArray(new int[0][]);
    }

    // ---- Getters utiles para el resto del programa (menu, impresion, grafo, etc.) ----

    public int getNumFilas() {
        return numFilas;
    }

    public int getNumColumnas() {
        return numColumnas;
    }

    public char[][] getMapa() {
        return mapa;
    }

    public int[] getEntrada() {
        return entrada;
    }

    public int[] getSalida() {
        return salida;
    }

    /**
     * Imprime el laberinto exactamente como quedo cargado desde el archivo,
     * sin modificar ningun caracter. La entrada y la salida NO se marcan
     * aqui: se consultan por separado con getEntrada()/getSalida(), ya que
     * son solo coordenadas (fila, columna) y no alteran el mapa original.
     */
    public void imprimir() {
        for (int f = 0; f < numFilas; f++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < numColumnas; c++) {
                sb.append(mapa[f][c]);
            }
            System.out.println(sb.toString());
        }
    }
}