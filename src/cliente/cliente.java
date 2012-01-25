package cliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.swing.*;


public class cliente{	
    public static void main(String[] args) throws IOException {
    	//chat c = new chat();
    	//c.introCon(); 
    	Thread chat = new Thread(new chat());
    	chat.start();
	}    
}
class chat extends ventPrincipal implements Runnable{
	
	//Declaraciones generales.
	Socket s;
    Scanner in;
    static PrintWriter outd;
    int filas = 0;
	
    public void run(){
		//Bienvenida para manejar la conexion.
		imprimirMsj("Introduce direccion del servidor, McFly");
		cEnv.setEnabled(true);
		while(!conectado){
			try{
				Thread.sleep(2000);				
			} catch(Exception ec) {}
		}
		clearChat();
		conectServidor();
    }
    
		
	void conectServidor() {
		try{
			// Realiza la conexion
			s = new Socket(dirServ, puertoServ);
			// Abre los canales
	        in = new Scanner(s.getInputStream()); // In
	        outd = new PrintWriter(s.getOutputStream(), true); // Out
	        // Permite ecribir porque se ha realizado la coneixion.
	        conectado = true;
	        cEnv.setEnabled(true);			
		} catch(Exception e){ e.printStackTrace(); }
		
        //Imprime las lineas que envie el servidor.
        while(true){
        	imprimirMsj(in.nextLine());
		}
	}
	
	
	static void enviarMsj(String msj){
		outd.println(msj);
	}
	
	void imprimirMsj(String msj){
		try{
			cRec.insert(msj+"\n", cRec.getLineStartOffset(filas));
			filas++;
		} catch(Exception ex) { ex.printStackTrace(); }
	}
	
	void clearChat(){
		cRec.setText("");
		filas = 0;
	}
}

//GUI
//Ventana principal.
class ventPrincipal extends JFrame implements ActionListener, KeyListener{
	
	//Definicion de cosillas.
	double cVers = 0.1; //version
	String dirServ = "";
	int puertoServ = 1398; //puerto
	
	JTextArea cEnv;
	JTextArea cRec;

	String msj;
	
	//Variable de conexion.
	boolean conectado = false;
	
	
	// Ventana principal.
	ventPrincipal(){
		//Caracteristicas generales.
		this.setSize(new Dimension(350,500));
		this.setTitle("Chat Underground");
		
		//Objetos de la ventana.
		JLabel cVerLab = new JLabel("Chat underground v"+ cVers, JLabel.CENTER );
			cVerLab.setVerticalAlignment(JLabel.TOP);
		
		//Cuadro de texto de mensajes entrantes.
		cRec = new JTextArea();
			cRec.setEditable(false);
			cRec.setSize(new Dimension(330,370));
			cRec.setLineWrap(true);
			cRec.setWrapStyleWord(true);
		JScrollPane cRecScroll = new JScrollPane(cRec);
			cRecScroll.setBounds(10,20,330,370);
			cRecScroll.setViewportView(cRec);
			cRecScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			
		
		//Cuadro de texto de escritura.
		cEnv = new JTextArea();
			cEnv.setBounds(10,395,330,65);
			cEnv.setLineWrap(true);
			cEnv.setWrapStyleWord(true);
			cEnv.addKeyListener(this);
			cEnv.setEnabled(false);
			cEnv.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		JScrollPane cEnvScroll = new JScrollPane(cEnv);
			cEnvScroll.setBounds(10,395,330,65);
			cEnvScroll.setViewportView(cEnv);
			cEnvScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		//Se añaden los objetos.
		this.getContentPane().add(cRecScroll,BorderLayout.CENTER);
		this.getContentPane().add(cEnvScroll,BorderLayout.CENTER);
		this.getContentPane().add(cVerLab);
		
		//Si la ventana se cierra, finaliza la aplicacion.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Hace la ventana visible y su tamaño fijo.
		setResizable(false);
		setVisible(true);
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			//Si pulsa enter, envia el mensaje.
			msj = cEnv.getText();
			//Si el mensaje es nulo, no lo envia.
			if((!msj.equals(""))){
				if(conectado){
					chat.enviarMsj(msj);
				} else {
					//Para cuando aun no esta conectado.
					dirServ = msj;
					conectado = true;
				}
				cEnv.setText("");
			}
		}
	}


	@Override
	public void keyReleased(KeyEvent e) { }
	
	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void actionPerformed(ActionEvent e) { }
}