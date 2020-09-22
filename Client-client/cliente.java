import java.net.*;
import java.util.*;
import java.io.*;
import java.util.Scanner;
import java.util.Random;

public class cliente 
{
    public static int numeroTablero;
    public static String[][][] tableros = {
        {
            { "A", "C", "D", "E" },
            { "E", "B", "F", "H" },
            { "F", "G", "C", "G" },
            { "H", "B", "A", "D" }
        },
        {
            { "D", "B", "C", "A" },
            { "C", "A", "B", "D" },
            { "E", "G", "G", "E" },
            { "F", "F", "H", "H" }
        },
        {
            { "A", "E", "E", "H" },
            { "A", "C", "D", "H" },
            { "G", "C", "D", "B" },
            { "G", "F", "F", "B" }
        },
        {
            { "A", "B", "C", "D" },
            { "E", "F", "G", "H" },
            { "H", "G", "F", "E" },
            { "D", "C", "B", "A" }
        },
        {
            { "A", "E", "F", "B" },
            { "C", "G", "H", "D" },
            { "D", "H", "G", "C" },
            { "B", "F", "E", "A" }
        }
    };

    public static String[][] tableroOponente = {  
        { "X", "X", "X", "X" },
        { "X", "X", "X", "X" },
        { "X", "X", "X", "X" },
        { "X", "X", "X", "X" }
    };

    public static void main(String[] args) throws IOException
    {
        int puerto;
        int puertoConexion = -1;
        String direccion;
        boolean conectado1 = false;
        boolean conectado2 = false;

        if (args.length < 1)
        {
            System.out.println("Se necesita introducir un numero de puerto.");
            return;
        }
        else
        {
            try 
            {
                puerto = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Numero de puerto no valido.");
                return;
            }
        }

        Scanner escribe = new Scanner(System.in);
        System.out.println("Introduce la direccion ip del cliente a conectar: ");
        direccion = escribe.nextLine();
        System.out.println("Introduce el puerto del cliente a conectar: ");
        while (puertoConexion == -1)
        {
            try 
            {
                puertoConexion = Integer.parseInt(escribe.nextLine());
            }
            catch (NumberFormatException e)
            {
                puertoConexion = -1;
                System.out.println("Numero de puerto no valido. Vuelve a introducirlo.");
                return;
            }
        }

        ServerSocket socket_servidor1;
        Socket socket_servidor2 = null;
        PrintWriter escribir = null;
        BufferedReader recibir = null;
        try
        {
            socket_servidor2 = new Socket(direccion, puertoConexion);
            System.out.println("Conectado al otro cliente.");
            conectado1 = true;

            socket_servidor1 = new ServerSocket(puerto);
            socket_servidor2 = socket_servidor1.accept();
            conectado2 = true;
        }
        catch (IOException ex)
        {
            conectado1 = false;

            socket_servidor1 = new ServerSocket(puerto);
            socket_servidor1.accept();
            conectado2 = true;
        }

        boolean tieneTurno = false;
        if (conectado2 == true && conectado1 == false)
        {
            socket_servidor2 = new Socket(direccion, puertoConexion);
            System.out.println("Conectado al otro cliente.");
            conectado1 = true;      
            tieneTurno = true;      
        }

        escribir = new PrintWriter(socket_servidor2.getOutputStream());
        recibir = new BufferedReader(new InputStreamReader(socket_servidor2.getInputStream()));

        if (escribir == null || recibir == null)
        {
            System.out.println("Error. No se ha podido realizar la conexion.");
            return;
        }
        
        Random rand = new Random();
        numeroTablero = rand.nextInt(5);

        String accion = "Iniciar turno";

        while(true)
        {
            switch (accion)
            {
                case "Iniciar turno":
                    clearScreen();
                    String texto = "Tablero\n";
                    String tuTablero = "";
                    for (String[] linea : tableroOponente)
                    {
                        for (String s : linea)
                        {
                            tuTablero += (s + " ");
                        }
                        tuTablero += "\n";
                    }
                    escribir.println(tuTablero + "\nFin");
                    escribir.flush();

                    texto += recibir.readLine();
                    for (String line = recibir.readLine(); !line.endsWith("Fin"); line = recibir.readLine())
                    {
                        texto += "\n";
                        texto += line;
                    }
                    texto += "\nTablero del oponente\n";
                    texto += tuTablero;
                    System.out.println(texto);

                    if (tieneTurno) accion = "Turno jugador";
                    else
                    {
                        System.out.println("\nTurno del oponente.");

                        // Coger primera jugada del oponente
                        String primeraJugada = recibir.readLine();
                        int pos1 = Character.getNumericValue(primeraJugada.charAt(0));
                        int pos2 = Character.getNumericValue(primeraJugada.charAt(2));
                        String jugada1 = tableros[numeroTablero][pos1][pos2];
                        escribir.println(jugada1);
                        escribir.flush();

                        // Coger segunda jugada del oponente
                        String segundaJugada = recibir.readLine();
                        int pos3 = Character.getNumericValue(segundaJugada.charAt(0));
                        int pos4 = Character.getNumericValue(segundaJugada.charAt(2));
                        String jugada2 = tableros[numeroTablero][pos3][pos4];
                        escribir.println(jugada2);
                        escribir.flush();

                        if (jugada1 == jugada2 && primeraJugada != segundaJugada)
                        {
                            escribir.println("Acertaste");
                            escribir.flush();

                            tableroOponente[pos1][pos2] = jugada1;
                            tableroOponente[pos3][pos4] = jugada2;

                            boolean acabado = true;
                            for (String[] linea : tableroOponente)
                            {
                                for (String s : linea)
                                {
                                    if (s == "X") acabado = false;
                                }
                            }

                            if (acabado)
                            {
                                clearScreen();
                                accion = "Perder";
                                escribir.println("Has ganado");
                                escribir.flush();
                                break;
                            }
                            else
                            {
                                escribir.println("No has ganado aun");
                                escribir.flush();
                            }
                        }
                        else
                        {
                            escribir.println("Fallaste");
                            escribir.flush();
                            tieneTurno = true;
                        }
                        accion = "Iniciar turno";
                    }
                    break;
                case "Turno jugador":
                    System.out.println("\nTu turno.\n");

                    // Primera jugada
                    System.out.println("Dame tu primera jugada: ");
                    String primeraJugada = escribe.nextLine();
                    int pos1;
                    int pos2;
                    try
                    {
                        pos1 = Character.getNumericValue(primeraJugada.charAt(0));
                        pos2 = Character.getNumericValue(primeraJugada.charAt(2));

                        escribir.println(primeraJugada);
                        escribir.flush();
                    }
                    catch(Exception e)
                    {
                        System.out.println("Ha habido un error. Vuelve a intentarlo.");
                        break;
                    }
                    System.out.println("Es una " + recibir.readLine() + "\n");

                    // Segunda jugada
                    System.out.println("Dame tu segunda jugada: ");
                    String segundaJugada = escribe.nextLine();
                    int pos3;
                    int pos4;
                    try
                    {
                        pos3 = Character.getNumericValue(segundaJugada.charAt(0));
                        pos4 = Character.getNumericValue(segundaJugada.charAt(2));

                        escribir.println(segundaJugada);
                        escribir.flush();
                    }
                    catch(Exception e)
                    {
                        System.out.println("Ha habido un error. Vuelve a intentarlo.");
                        break;
                    }
                    System.out.println("Es una " + recibir.readLine() + "\n");

                    if (recibir.readLine().endsWith("Acertaste"))
                    {
                        if (recibir.readLine().endsWith("Has ganado"))
                        {
                            clearScreen();
                            accion = "Ganar";
                            break;
                        }
                        else
                        {
                            System.out.println("Has acertado.");
                            esperar(1);
                        }
                    }
                    else
                    {
                        System.out.println("Has fallado.");
                        tieneTurno = false;
                        esperar(1);
                    }
                    accion = "Iniciar turno";
                    break;
                case "Ganar":
                    System.out.println("Has ganado! Enhorabuena.");
                    accion = "Nada";
                    break;
                case "Perder":
                    System.out.println("El oponente ha ganado.");
                    accion = "Nada";
                    break;
            }
        }
    }

    public static void clearScreen() 
    { 
        System.out.print("\033[H\033[2J");  
        System.out.flush();
        
        try
        {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        catch(IOException ex){}
        catch (InterruptedException ex){}
    }

    public static void esperar(int segs)
    {
        try
        {
            Thread.sleep(segs*1000);
        }
        catch(Exception e){}
    }
}