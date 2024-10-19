package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/BDCrunchy";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Método simulado para limpiar la consola
    public static void limpiarConsola() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return conexion;
    }// fin conectar()

    public static void insertarProducto(String codigo, String nombre, double precio, int cantidad, String fecha) {
        String query = "INSERT INTO producto (codigoProducto, nombreProducto, precioUnitario, cantidadProducto, fechaVencimiento) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigo);
            pst.setString(2, nombre);
            pst.setDouble(3, precio);
            pst.setInt(4, cantidad);

            // Validación de la fecha
            try {
                LocalDate fechaVencimiento = LocalDate.parse(fecha);
                pst.setDate(5, java.sql.Date.valueOf(fechaVencimiento));
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha inválido. Debe ser YYYY-MM-DD.");
                return;
            }

            pst.executeUpdate();
            System.out.println("\nProducto insertado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
        }
    }// fin insertarProducto()

    public static void listarProductos() {
        String query = "SELECT * FROM producto;";
        try (Connection con = ConexionBD.conectar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            boolean hayResultados = false;
            System.out.println("\n---- Lista de Productos ----\n");
            while (rs.next()) {
                hayResultados = true;
                System.out.println("Código: " + rs.getString("codigoProducto"));
                System.out.println("Nombre: " + rs.getString("nombreProducto"));
                System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                System.out.println("Fecha de Vencimiento: " + rs.getDate("fechaVencimiento"));
                System.out.println("-------------------------------\n");
            }
            if (!hayResultados) {
                System.out.println("No hay productos disponibles.");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
    }// fin listarProductos()

    public static void buscarProducto(String codigoProducto) {
        String query = "SELECT * FROM producto WHERE codigoProducto = ?";
        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigoProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nProducto encontrado:");
                    System.out.println("Código: " + rs.getString("codigoProducto"));
                    System.out.println("Nombre: " + rs.getString("nombreProducto"));
                    System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                    System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                    System.out.println("Fecha de Vencimiento: " + rs.getDate("fechaVencimiento"));
                    System.out.println("-------------------------------\n");
                } else {
                    System.out.println("Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar producto: " + e.getMessage());
        }
    }// fin buscarProducto()

    public static void actualizarProducto(String codigoProducto) {
        Scanner scanner = new Scanner(System.in);
        String query = "SELECT * FROM producto WHERE codigoProducto = ?";
        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigoProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nProducto a actualizar:");
                    System.out.println("Código: " + rs.getString("codigoProducto"));
                    System.out.println("Nombre: " + rs.getString("nombreProducto"));
                    System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                    System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                    System.out.println("Fecha de Vencimiento: " + rs.getDate("fechaVencimiento"));
                    System.out.println("-------------------------------\n");

                    // Preguntar si desea actualizar cada campo
                    System.out.print("¿Desea actualizar el nombre? (Sí/No): ");
                    String respuesta = scanner.nextLine();
                    String nuevoNombre = rs.getString("nombreProducto");
                    if (respuesta.equalsIgnoreCase("sí") || respuesta.equalsIgnoreCase("si")) {
                        System.out.print("Nuevo nombre: ");
                        nuevoNombre = scanner.nextLine();
                    }

                    System.out.print("¿Desea actualizar el precio? (Sí/No): ");
                    respuesta = scanner.nextLine();
                    double nuevoPrecio = rs.getDouble("precioUnitario");
                    if (respuesta.equalsIgnoreCase("sí") || respuesta.equalsIgnoreCase("si")) {
                        System.out.print("Nuevo precio: ");
                        nuevoPrecio = scanner.nextDouble();
                        scanner.nextLine(); // Consumir nueva línea
                    }

                    System.out.print("¿Desea actualizar la cantidad? (Sí/No): ");
                    respuesta = scanner.nextLine();
                    int nuevaCantidad = rs.getInt("cantidadProducto");
                    if (respuesta.equalsIgnoreCase("sí") || respuesta.equalsIgnoreCase("si")) {
                        System.out.print("Nueva cantidad: ");
                        nuevaCantidad = scanner.nextInt();
                        scanner.nextLine(); // Consumir nueva línea
                    }

                    System.out.print("¿Desea actualizar la fecha de vencimiento? (Sí/No): ");
                    respuesta = scanner.nextLine();
                    LocalDate nuevaFecha = rs.getDate("fechaVencimiento").toLocalDate();
                    if (respuesta.equalsIgnoreCase("sí") || respuesta.equalsIgnoreCase("si")) {
                        System.out.print("Nueva fecha de vencimiento (YYYY-MM-DD): ");
                        String nuevaFechaString = scanner.nextLine();
                        try {
                            nuevaFecha = LocalDate.parse(nuevaFechaString);
                        } catch (DateTimeParseException e) {
                            System.out.println("Formato de fecha inválido. Debe ser YYYY-MM-DD.");
                            return;
                        }
                    }

                    // Actualizar el producto
                    query = "UPDATE producto SET nombreProducto = ?, precioUnitario = ?, cantidadProducto = ?, fechaVencimiento = ? WHERE codigoProducto = ?";
                    try (PreparedStatement pstUpdate = con.prepareStatement(query)) {
                        pstUpdate.setString(1, nuevoNombre);
                        pstUpdate.setDouble(2, nuevoPrecio);
                        pstUpdate.setInt(3, nuevaCantidad);
                        pstUpdate.setDate(4, java.sql.Date.valueOf(nuevaFecha));
                        pstUpdate.setString(5, codigoProducto);
                        pstUpdate.executeUpdate();
                        System.out.println("\nProducto actualizado correctamente.");
                    }

                } else {
                    System.out.println("Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
        }
    }// fin actualizarProducto()

    public static void eliminarProducto(String codigoProducto) {
        Scanner scanner = new Scanner(System.in);
        String query = "SELECT * FROM producto WHERE codigoProducto = ?";
        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigoProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nProducto a eliminar:");
                    System.out.println("Código: " + rs.getString("codigoProducto"));
                    System.out.println("Nombre: " + rs.getString("nombreProducto"));
                    System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                    System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                    System.out.println("Fecha de Vencimiento: " + rs.getDate("fechaVencimiento"));
                    System.out.println("-------------------------------\n");

                    System.out.print("¿Está seguro de eliminar este producto? Esta acción es irrevertible (Sí/No): ");
                    String confirmacion = scanner.nextLine();
                    if (confirmacion.equalsIgnoreCase("sí") || confirmacion.equalsIgnoreCase("si")) {
                        query = "DELETE FROM producto WHERE codigoProducto = ?";
                        try (PreparedStatement pstDelete = con.prepareStatement(query)) {
                            pstDelete.setString(1, codigoProducto);
                            pstDelete.executeUpdate();
                            System.out.println("\nProducto eliminado correctamente.");
                        }
                    } else {
                        System.out.println("\nEliminación cancelada.");
                    }
                } else {
                    System.out.println("Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }
    }// fin eliminarProducto()

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            limpiarConsola(); // Limpiar la consola antes de mostrar el menú
            System.out.println("------ Menú CRUD de Productos ------");
            System.out.println("1. Insertar un nuevo producto");
            System.out.println("2. Listar todos los productos");
            System.out.println("3. Buscar un producto por código");
            System.out.println("4. Actualizar un producto");
            System.out.println("5. Eliminar un producto");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir nueva línea

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el código del producto: ");
                    String codigo = scanner.nextLine();
                    System.out.print("Ingrese el nombre del producto: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Ingrese el precio del producto: ");
                    double precio = scanner.nextDouble();
                    System.out.print("Ingrese la cantidad del producto: ");
                    int cantidad = scanner.nextInt();
                    scanner.nextLine(); // Consumir nueva línea
                    System.out.print("Ingrese la fecha de vencimiento del producto (YYYY-MM-DD): ");
                    String fecha = scanner.nextLine();
                    insertarProducto(codigo, nombre, precio, cantidad, fecha);
                    break;
                case 2:
                    listarProductos();
                    break;
                case 3:
                    System.out.print("Ingrese el código del producto a buscar: ");
                    String codigoBuscar = scanner.nextLine();
                    buscarProducto(codigoBuscar);
                    break;
                case 4:
                    System.out.print("Ingrese el código del producto a actualizar: ");
                    String codigoActualizar = scanner.nextLine();
                    actualizarProducto(codigoActualizar);
                    break;
                case 5:
                    System.out.print("Ingrese el código del producto a eliminar: ");
                    String codigoEliminar = scanner.nextLine();
                    eliminarProducto(codigoEliminar);
                    break;
                case 6:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida, por favor seleccione una opción válida.");
                    break;
            }

            if (opcion != 6) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine(); // Esperar a que el usuario presione Enter
            }
        } while (opcion != 6);
    }
}
