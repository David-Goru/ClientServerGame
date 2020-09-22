import java.net.*;
import java.util.*;
import java.io.*;
import java.util.Scanner;

public class cliente 
{
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

        Socket servidor = new Socket("localhost", puerto);
        System.out.println("Conectado al servidor.");

        BufferedReader recibe = new BufferedReader(new InputStreamReader(servidor.getInputStream()));
        PrintWriter envia = new PrintWriter(servidor.getOutputStream());
        Scanner escribe = new Scanner(System.in);

        while(true)
        {
            for (String line = recibe.readLine(); line != null; line = recibe.readLine())
            {
                if (line.startsWith("Gana"))
                {
                    System.out.println(line);
                }
                else if (line.startsWith("Dame"))
                {
                    System.out.println(line);
                    envia.println(escribe.nextLine());
                    envia.flush();
                }
                else if (line.startsWith("Nuevo turno")) clearScreen();
                else if (line.endsWith("Fallaste") || line.endsWith("Acertaste"))
                {
                    System.out.println(line);
                    esperar(1);
                    clearScreen();
                }
                else System.out.println(line);
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