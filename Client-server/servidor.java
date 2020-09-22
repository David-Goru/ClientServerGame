import java.net.*;
import java.util.*;
import java.io.*;
import java.util.Random;

public class servidor 
{
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

    public static int[] numTableroClientes;
    public static String[][][] tableroClientes = {
        {   
            { "X", "X", "X", "X" },
            { "X", "X", "X", "X" },
            { "X", "X", "X", "X" },
            { "X", "X", "X", "X" }
        },
        {   
            { "X", "X", "X", "X" },
            { "X", "X", "X", "X" },
            { "X", "X", "X", "X" },
            { "X", "X", "X", "X" }
        }
    };


    public static void main(String[] args) throws IOException
    {
        int puerto;

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
                puerto = 0;
                System.out.println("Numero de puerto no valido.");
                return;
            }
        }

        Socket[] clientes = new Socket[2];
        int numClientes = 0;        
        ServerSocket servidor = new ServerSocket(puerto);
        System.out.println("Servidor inicializado.");
        String accion = "Iniciar turno";
        PrintWriter[] clienteEscribir = new PrintWriter[2];
        BufferedReader[] clienteRecibir = new BufferedReader[2];

        while (numClientes < 2)
        {
            clientes[numClientes] = servidor.accept();
            numClientes++;
        }

        clienteEscribir[0] = new PrintWriter(clientes[0].getOutputStream());
        clienteEscribir[1] = new PrintWriter(clientes[1].getOutputStream());
        clienteRecibir[0] = new BufferedReader(new InputStreamReader(clientes[0].getInputStream()));
        clienteRecibir[1] = new BufferedReader(new InputStreamReader(clientes[1].getInputStream()));
        clienteEscribir[0].println("Cliente 1 conectado. Empieza la partida");
        clienteEscribir[0].flush();
        clienteEscribir[1].println("Cliente 0 conectado. Empieza la partida");
        clienteEscribir[1].flush();

        Random rand = new Random();
        numTableroClientes = new int[2];
        numTableroClientes[0] = rand.nextInt(5);
        numTableroClientes[1] = rand.nextInt(5);
        int esperandoCliente = 0;
        int clienteGanador = -1;

        while(true)
        {
            switch (accion)
            {
                case "Iniciar turno":
                    String texto = "Tablero jugador 0\n";
                    for (String[] linea : tableroClientes[0])
                    {
                        for (String s : linea)
                        {
                            texto += (s + " ");
                        }
                        texto += "\n";
                    }
                    texto += "Tablero jugador 1\n";
                    for (String[] linea : tableroClientes[1])
                    {
                        for (String s : linea)
                        {
                            texto += (s + " ");
                        }
                        texto += "\n";
                    }
                    clienteEscribir[0].println(texto);
                    clienteEscribir[0].flush();
                    clienteEscribir[1].println(texto);
                    clienteEscribir[1].flush();
                    accion = "Turno jugador";
                    break;
                case "Turno jugador":
                    for (PrintWriter p : clienteEscribir)
                    {
                        p.println("Turno jugador " + esperandoCliente);
                        p.flush();
                    }

                    // Primera jugada
                    clienteEscribir[esperandoCliente].println("Dame tu primera jugada: ");
                    clienteEscribir[esperandoCliente].flush();
                    String primeraJugada = clienteRecibir[esperandoCliente].readLine();
                    String jugada1;
                    int pos1;
                    int pos2;
                    int pos3;
                    int pos4;
                    try
                    {
                        pos1 = Character.getNumericValue(primeraJugada.charAt(0));
                        pos2 = Character.getNumericValue(primeraJugada.charAt(2));
                        jugada1 = tableros[numTableroClientes[esperandoCliente]][pos1][pos2];
                    }
                    catch(Exception e)
                    {
                        clienteEscribir[esperandoCliente].println("Ha habido un error. Vuelve a intentarlo.");
                        clienteEscribir[esperandoCliente].flush();
                        break;
                    }
                    clienteEscribir[esperandoCliente].println("Es una " + jugada1 + "\n");

                    // Segunda jugada
                    clienteEscribir[esperandoCliente].println("Dame tu segunda jugada: ");
                    clienteEscribir[esperandoCliente].flush();
                    String segundaJugada = clienteRecibir[esperandoCliente].readLine();
                    String jugada2;
                    try
                    {
                        pos3 = Character.getNumericValue(segundaJugada.charAt(0));
                        pos4 = Character.getNumericValue(segundaJugada.charAt(2));
                        jugada2 = tableros[numTableroClientes[esperandoCliente]][pos3][pos4];
                    }
                    catch(Exception e)
                    {
                        clienteEscribir[esperandoCliente].println("Ha habido un error. Vuelve a intentarlo.");
                        clienteEscribir[esperandoCliente].flush();
                        break;
                    }
                    clienteEscribir[esperandoCliente].println("Es una " + jugada2 + "\n");

                    String resultado;
                    if (jugada1 == jugada2 && primeraJugada != segundaJugada)
                    {
                        resultado = "\nAcertaste";
                        clienteEscribir[esperandoCliente].println(resultado);
                        clienteEscribir[esperandoCliente].flush();
                        tableroClientes[esperandoCliente][pos1][pos2] = tableros[numTableroClientes[esperandoCliente]][pos1][pos2];
                        tableroClientes[esperandoCliente][pos3][pos4] = tableros[numTableroClientes[esperandoCliente]][pos3][pos4];

                        boolean acabado = true;
                        for (String[] linea : tableroClientes[esperandoCliente])
                        {
                            for (String s : linea)
                            {
                                if (s == "X") acabado = false;
                            }
                        }

                        if (acabado)
                        {
                            clienteGanador = esperandoCliente;
                            accion = "Acabar";
                            break;
                        }

                        accion = "Continuar turno";
                        break;
                    }

                    resultado = "\nFallaste";   
                    clienteEscribir[esperandoCliente].println(resultado);
                    clienteEscribir[esperandoCliente].flush();
                    esperandoCliente = Math.abs(esperandoCliente - 1);   
                    clienteEscribir[esperandoCliente].println("Nuevo turno");
                    clienteEscribir[esperandoCliente].flush();
                    accion = "Iniciar turno";
                    break;
                case "Continuar turno":
                    String mostrar = "Turno y tablero del jugador " + esperandoCliente + "\n";
                    for (String[] linea : tableroClientes[esperandoCliente])
                    {
                        for (String s : linea)
                        {
                            mostrar += (s + " ");
                        }
                        mostrar += "\n";
                    }
                    
                    clienteEscribir[esperandoCliente].println(mostrar);
                    clienteEscribir[esperandoCliente].flush();
                    accion = "Turno jugador";
                    break;
                case "Acabar":
                    for (PrintWriter p : clienteEscribir)
                    {
                        p.println("Gana el jugador " + clienteGanador);
                        p.flush();
                    }
                    accion = "Nada";
                    break;
            }
        }
    }
}