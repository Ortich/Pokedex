/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Image;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_DOWN;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Usuario
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    BufferedImage plantilla = null;
    HashMap <String,BufferedImage> tipos = new HashMap();
    private int contador = 0;
    private int anchoIcono = 250, altoIcono = 250;
    private int anchoIconoP = 75, altoIconoP = 75;
    int total_pokemons = 0;
    boolean musica = true;
    
    // conectamos a la base de datos
    private Statement estado;
    private ResultSet resultadoConsulta;
    private Connection conexion;
    
     //hashmap para almacenar el resultado de la consulta
    HashMap <String,Pokemon> listaPokemons = new HashMap();
    
    //musica de fondo
    public Clip musicaFondo;
    public String ruta	="/sonidos/PokemonCenterMusic.wav";
    
    /**
     * Creates new form VentanaPokedex
     */
    private ImageIcon devuelveElPokemonQueEstaEnLaPosicion (int posicion, int ancho, int alto){
        int columna = posicion / 31;
        int fila = posicion % 31;
        return ( new ImageIcon(plantilla.getSubimage(fila*96, columna*96, 96, 96)
                .getScaledInstance(ancho, alto, Image.SCALE_DEFAULT))); 
    }
    
    private ImageIcon devuelveTipo (String tipo){
	try {
	    return new ImageIcon(tipos.get(tipo));
	} catch (Exception e) {
	    return null;
	}
    }
    
    private void reproduceSonido(){
	try {
	    String sonido = "/sonidos/"+(contador+1)+".wav";
	    Clip clip = AudioSystem.getClip();
	    clip.open(AudioSystem.getAudioInputStream(getClass().getResource(sonido)));
	    clip.loop(0);
	} catch (Exception e) {
	}
    }
    
     private void musicaFondo(){
	try {
	    musicaFondo = AudioSystem.getClip();
	    musicaFondo.open(AudioSystem.getAudioInputStream(getClass().getResourceAsStream(ruta)));
	    musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
	    FloatControl gainControl = 
	     (FloatControl) musicaFondo.getControl(FloatControl.Type.MASTER_GAIN);
	    gainControl.setValue(-5.0f);
	    musicaFondo.start();
	    
	} catch (Exception e) {
	}
    }
    
    private void escribeDatos(){
	tablaPokemonScroll.getVerticalScrollBar().setValue(contador * 15);
        Pokemon p = listaPokemons.get(String.valueOf(contador+1));
        if (p != null){
            nombrePokemon.setText(p.nombre);
	    textoGeneracion.setText("Generacion: "+p.generacion);
	    textoPeso.setText("Peso: "+p.peso+" kg");
	    textoAltura.setText("Altura: "+p.altura+" m");
	    textoHabitat.setText("Habitat: "+p.habitat);
	    ataque1.setText(p.movimiento1);
	    ataque2.setText(p.movimiento2);
	    ataque3.setText(p.movimiento3);
	    ataque4.setText(p.movimiento4);
	    textoDescripcion.setText("<html><p align='center'>"+p.descripcion+"</p></html>");
	    tipo1.setIcon(devuelveTipo(p.tipo1));
	    tipo2.setIcon(devuelveTipo(p.tipo2));
	    numeroPokemon.setText("#"+p.id);
        }
        else {
            nombrePokemon.setText("NO HAY DATOS");
        }
	try{
	    imagenPokemonPosterior.setVisible(true);
	    imagenPokemonPosterior.setIcon(devuelveElPokemonQueEstaEnLaPosicion(Integer.valueOf(p.posEvolucion)-1, anchoIconoP , altoIconoP));
	}
	catch(Exception e){
	    imagenPokemonPosterior.setVisible(false);
	}
	try{
	    imagenPokemonAnterior.setVisible(true);
	    imagenPokemonAnterior.setIcon(devuelveElPokemonQueEstaEnLaPosicion(Integer.valueOf(p.preEvolucion)-1, anchoIconoP , altoIconoP));
	}
	catch(Exception e){
	    imagenPokemonAnterior.setVisible(false);
	}
	try {
	    imagenHuella.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/huellas/"+(contador+1)+".png")).getScaledInstance(40, 40, Image.SCALE_DEFAULT)));
	} catch (Exception e) {
	}
	tablaPokemon.setRowSelectionInterval(Integer.valueOf(p.id)-1, Integer.valueOf(p.id)-1);
	
    }
    
    private void ponEnTabla(int id, String nombre){
	DefaultTableModel model = (DefaultTableModel) tablaPokemon.getModel();
	try {
	    model.addRow(new Object[]{id, nombre});
	} catch (Exception e) {
	}
    }
    
    private void anterior(int num){
	int aux = num;
	while(aux>0){
	    aux--;
	    if(contador <= 0){
	    contador = total_pokemons;
	}
	    contador--;
	}
	imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	escribeDatos();	
    }
    
    private void siguiente(int num){
	int aux = num;
	while(aux>0){
	    aux--;
	 if(contador >= total_pokemons-1){
	    contador = -1;
	}
	 contador++;
	}
	imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	escribeDatos();
    }
    
    private void released(){
	botonPequeñoUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg")));
	botonPequeñoPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg")));
	iconoSecreto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/icono.png")));
	botonPequeñoAtrasImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg")));
	flechaArriba.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/flechaArriba.png")));
	botonPequeñoAdelanteImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg")));
	flechaAbajo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/flechaAbajo.png")));
	botonSuma10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonSuma.png")));
	botonResta10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonResta.png")));
	botonPequeñoPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg")));
	botonPequeñoUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg")));
	
    }
    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
	initComponents();
	this.setFocusable(true);
	tablaPokemonScroll.setFocusable(false);
	tablaPokemon.setFocusable(false);
	tablaPokemon.getColumn("Nombre").setPreferredWidth(170);
	tablaPokemon.getColumn("ID").setPreferredWidth(60);
	try{
            plantilla = ImageIO.read(getClass().getResource("/sprites/black-white.png"));
	    tipos.put("Acero", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_acero.gif")));
	    tipos.put("Agua", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_agua.gif")));
	    tipos.put("Bicho", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_bicho.gif")));
	    tipos.put("Dragon", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_dragon.gif")));
	    tipos.put("Electrico", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_electrico.gif")));
	    tipos.put("Fantasma", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_fantasma.gif")));
	    tipos.put("Fuego", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_fuego.gif")));
	    tipos.put("Hada", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_hada.gif")));
	    tipos.put("Hielo", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_hielo.gif")));
	    tipos.put("Lucha", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_lucha.gif")));
	    tipos.put("Normal", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_normal.gif")));	    
	    tipos.put("Planta", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_planta.gif")));
	    tipos.put("Psiquico", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_psiquico.gif")));
	    tipos.put("Roca", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_roca.gif")));
	    tipos.put("Siniestro", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_siniestro.gif")));
	    tipos.put("Tierra", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_tierra.gif")));
	    tipos.put("Veneno", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_veneno.gif")));
	    tipos.put("Volador", ImageIO.read(getClass().getResource("/tiposPokemon/Tipo_volador.gif")));
		    
        }
        catch (IOException e){}	
	
	//conexion a la base de datos//////////////////
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1/pokemondb","root","root");
            estado = conexion.createStatement();
            resultadoConsulta = estado.executeQuery("SELECT * FROM pokemondb.pokemon");
            //cargo el resultado de la query en mi hashmap
            while (resultadoConsulta.next()){
                Pokemon p = new Pokemon();
		p.id = resultadoConsulta.getString(1);
                p.nombre = resultadoConsulta.getString(2);
                p.altura = resultadoConsulta.getString(3);
		p.peso = resultadoConsulta.getString(4);
		p.especie = resultadoConsulta.getString(5);
		p.habitat = resultadoConsulta.getString(6);
		p.tipo1 = resultadoConsulta.getString(7);
		p.tipo2 = resultadoConsulta.getString(8);
		p.habilidad = resultadoConsulta.getString(9);
		p.movimiento1 = resultadoConsulta.getString(10);
		p.movimiento2 = resultadoConsulta.getString(11);
		p.movimiento3 = resultadoConsulta.getString(12);
		p.movimiento4 = resultadoConsulta.getString(13);
		p.preEvolucion = resultadoConsulta.getString(14);
		p.posEvolucion = resultadoConsulta.getString(15);
		p.descripcion = resultadoConsulta.getString(16);
		
		ponEnTabla(Integer.valueOf(p.id), p.nombre);
                listaPokemons.put(resultadoConsulta.getString(1), p);
            }
        }
	catch(SQLException s){
	    System.out.print(s.getMessage());
	}
	catch(ClassNotFoundException c){
	   System.out.print( c.getMessage());
	}
        catch (Exception e){
	    System.out.print(e.getMessage());
        }
        total_pokemons = listaPokemons.size();
        //////////////////////////////////////////////
        imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(0, anchoIcono , altoIcono));
        escribeDatos();
	
	//Musica de fondo
	musicaFondo();	
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botonResta10 = new javax.swing.JLabel();
        botonSuma10 = new javax.swing.JLabel();
        iconoSecreto = new javax.swing.JLabel();
        botonAtrasCaja = new javax.swing.JLabel();
        botonAdelanteCaja = new javax.swing.JLabel();
        botonPrimero = new javax.swing.JLabel();
        botonPequeñoPrimero = new javax.swing.JLabel();
        botonUltimo = new javax.swing.JLabel();
        botonPequeñoUltimo = new javax.swing.JLabel();
        flechaAbajo = new javax.swing.JLabel();
        botonPequeñoAdelanteImagen = new javax.swing.JLabel();
        flechaArriba = new javax.swing.JLabel();
        botonPequeñoAtrasImagen = new javax.swing.JLabel();
        textoHabitat = new javax.swing.JLabel();
        cuadroHabitat = new javax.swing.JLabel();
        textoAltura = new javax.swing.JLabel();
        cuadroAltura = new javax.swing.JLabel();
        textoPeso = new javax.swing.JLabel();
        cuadroPeso = new javax.swing.JLabel();
        textoGeneracion = new javax.swing.JLabel();
        cuadroGeneracion = new javax.swing.JLabel();
        nombrePokemon = new javax.swing.JLabel();
        cuadroNombre = new javax.swing.JLabel();
        ataque1 = new javax.swing.JLabel();
        cuadroataque1 = new javax.swing.JLabel();
        ataque2 = new javax.swing.JLabel();
        cuadroataque2 = new javax.swing.JLabel();
        ataque3 = new javax.swing.JLabel();
        cuadroataque3 = new javax.swing.JLabel();
        ataque4 = new javax.swing.JLabel();
        cuadroataque4 = new javax.swing.JLabel();
        imagenPokemonPosterior = new javax.swing.JLabel();
        imagenPokemonAnterior = new javax.swing.JLabel();
        imagenPokemon = new javax.swing.JLabel();
        cuadroImagenPokemon = new javax.swing.JLabel();
        textoDescripcion = new javax.swing.JLabel();
        cuadroDescripcion = new javax.swing.JLabel();
        tablaPokemonScroll = new javax.swing.JScrollPane();
        tablaPokemon = new javax.swing.JTable();
        listaPokemon = new javax.swing.JLabel();
        encabezado1 = new javax.swing.JLabel();
        encabezado2 = new javax.swing.JLabel();
        encabezado3 = new javax.swing.JLabel();
        tipo1 = new javax.swing.JLabel();
        tipo2 = new javax.swing.JLabel();
        botonSilencio = new javax.swing.JLabel();
        imagenHuella = new javax.swing.JLabel();
        numeroPokemon = new javax.swing.JLabel();
        cuadroNumeroPokemon = new javax.swing.JLabel();
        busquedaError = new javax.swing.JLabel();
        busqueda = new javax.swing.JTextField();
        cuadroBusqueda = new javax.swing.JLabel();
        buscar = new javax.swing.JLabel();
        botonBusqueda1 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setPreferredSize(new java.awt.Dimension(1000, 650));
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        botonResta10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonResta.png"))); // NOI18N
        botonResta10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonResta10MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botonResta10MouseReleased(evt);
            }
        });
        getContentPane().add(botonResta10, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 180, -1, -1));

        botonSuma10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonSuma.png"))); // NOI18N
        botonSuma10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonSuma10MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botonSuma10MouseReleased(evt);
            }
        });
        getContentPane().add(botonSuma10, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 180, -1, -1));

        iconoSecreto.setBackground(new java.awt.Color(255, 0, 51));
        iconoSecreto.setForeground(new java.awt.Color(255, 255, 255));
        iconoSecreto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/icono.png"))); // NOI18N
        iconoSecreto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                iconoSecretoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                iconoSecretoMouseReleased(evt);
            }
        });
        getContentPane().add(iconoSecreto, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 480, 150, 140));

        botonAtrasCaja.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonAtrasCajaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botonAtrasCajaMouseReleased(evt);
            }
        });
        getContentPane().add(botonAtrasCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 60, 100, 50));

        botonAdelanteCaja.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonAdelanteCajaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botonAdelanteCajaMouseReleased(evt);
            }
        });
        getContentPane().add(botonAdelanteCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 290, 100, 50));

        botonPrimero.setFont(new java.awt.Font("Pokemon Classic", 1, 10)); // NOI18N
        botonPrimero.setForeground(new java.awt.Color(0, 0, 0));
        botonPrimero.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        botonPrimero.setText("Primero");
        botonPrimero.setToolTipText("");
        botonPrimero.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonPrimeroMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botonPrimeroMouseReleased(evt);
            }
        });
        getContentPane().add(botonPrimero, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 130, 90, 30));

        botonPequeñoPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg"))); // NOI18N
        botonPequeñoPrimero.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonPequeñoPrimeroMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botonPequeñoPrimeroMouseReleased(evt);
            }
        });
        getContentPane().add(botonPequeñoPrimero, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 120, -1, 50));

        botonUltimo.setFont(new java.awt.Font("Pokemon Classic", 1, 10)); // NOI18N
        botonUltimo.setForeground(new java.awt.Color(0, 0, 0));
        botonUltimo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        botonUltimo.setText("Ultimo");
        botonUltimo.setToolTipText("");
        botonUltimo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonUltimoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botonUltimoMouseReleased(evt);
            }
        });
        getContentPane().add(botonUltimo, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 230, 90, 30));

        botonPequeñoUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg"))); // NOI18N
        botonPequeñoUltimo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonPequeñoUltimoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botonPequeñoUltimoMouseReleased(evt);
            }
        });
        getContentPane().add(botonPequeñoUltimo, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 220, -1, 50));

        flechaAbajo.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        flechaAbajo.setForeground(new java.awt.Color(255, 0, 51));
        flechaAbajo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/flechaAbajo.png"))); // NOI18N
        flechaAbajo.setToolTipText("");
        getContentPane().add(flechaAbajo, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 40, 30));

        botonPequeñoAdelanteImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg"))); // NOI18N
        getContentPane().add(botonPequeñoAdelanteImagen, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 290, -1, 50));

        flechaArriba.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        flechaArriba.setForeground(new java.awt.Color(255, 0, 51));
        flechaArriba.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/flechaArriba.png"))); // NOI18N
        flechaArriba.setToolTipText("");
        getContentPane().add(flechaArriba, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 70, 40, 30));

        botonPequeñoAtrasImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg"))); // NOI18N
        getContentPane().add(botonPequeñoAtrasImagen, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 60, -1, 50));

        textoHabitat.setFont(new java.awt.Font("Pokemon Classic", 0, 9)); // NOI18N
        textoHabitat.setForeground(new java.awt.Color(0, 0, 0));
        textoHabitat.setText("Habitat");
        textoHabitat.setToolTipText("");
        getContentPane().add(textoHabitat, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 210, 170, -1));

        cuadroHabitat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroHabitat, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 200, 230, -1));

        textoAltura.setFont(new java.awt.Font("Pokemon Classic", 0, 9)); // NOI18N
        textoAltura.setForeground(new java.awt.Color(0, 0, 0));
        textoAltura.setText("Altura");
        textoAltura.setToolTipText("");
        getContentPane().add(textoAltura, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, 170, -1));

        cuadroAltura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroAltura, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 150, 230, -1));

        textoPeso.setFont(new java.awt.Font("Pokemon Classic", 0, 9)); // NOI18N
        textoPeso.setForeground(new java.awt.Color(0, 0, 0));
        textoPeso.setText("Peso");
        textoPeso.setToolTipText("");
        getContentPane().add(textoPeso, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 110, 170, -1));

        cuadroPeso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroPeso, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 100, 230, -1));

        textoGeneracion.setFont(new java.awt.Font("Pokemon Classic", 0, 9)); // NOI18N
        textoGeneracion.setForeground(new java.awt.Color(0, 0, 0));
        textoGeneracion.setText("Generacion");
        textoGeneracion.setToolTipText("");
        getContentPane().add(textoGeneracion, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 170, -1));

        cuadroGeneracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroGeneracion, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 230, -1));

        nombrePokemon.setFont(new java.awt.Font("Pokemon Classic", 1, 9)); // NOI18N
        nombrePokemon.setForeground(new java.awt.Color(0, 0, 0));
        nombrePokemon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nombrePokemon.setText("Nombre Pokemon");
        nombrePokemon.setToolTipText("");
        getContentPane().add(nombrePokemon, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, 170, -1));

        cuadroNombre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 230, -1));

        ataque1.setFont(new java.awt.Font("Pokemon Classic", 1, 9)); // NOI18N
        ataque1.setForeground(new java.awt.Color(0, 0, 0));
        ataque1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ataque1.setText("Ataque 1");
        ataque1.setToolTipText("");
        getContentPane().add(ataque1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 540, 170, -1));

        cuadroataque1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroataque1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 530, 230, -1));

        ataque2.setFont(new java.awt.Font("Pokemon Classic", 1, 9)); // NOI18N
        ataque2.setForeground(new java.awt.Color(0, 0, 0));
        ataque2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ataque2.setText("Ataque 2");
        ataque2.setToolTipText("");
        getContentPane().add(ataque2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 540, 170, -1));

        cuadroataque2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroataque2, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 530, 230, -1));

        ataque3.setFont(new java.awt.Font("Pokemon Classic", 1, 9)); // NOI18N
        ataque3.setForeground(new java.awt.Color(0, 0, 0));
        ataque3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ataque3.setText("Ataque 3");
        ataque3.setToolTipText("");
        getContentPane().add(ataque3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 590, 170, -1));

        cuadroataque3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroataque3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 580, 230, -1));

        ataque4.setFont(new java.awt.Font("Pokemon Classic", 1, 9)); // NOI18N
        ataque4.setForeground(new java.awt.Color(0, 0, 0));
        ataque4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ataque4.setText("Ataque 4");
        ataque4.setToolTipText("");
        getContentPane().add(ataque4, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 590, 170, -1));

        cuadroataque4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoPeque.jpg"))); // NOI18N
        getContentPane().add(cuadroataque4, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 580, 230, -1));

        imagenPokemonPosterior.setForeground(new java.awt.Color(255, 0, 51));
        imagenPokemonPosterior.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 0, 0)));
        imagenPokemonPosterior.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                imagenPokemonPosteriorMousePressed(evt);
            }
        });
        getContentPane().add(imagenPokemonPosterior, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 400, 90, 100));

        imagenPokemonAnterior.setForeground(new java.awt.Color(255, 0, 51));
        imagenPokemonAnterior.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 0, 0)));
        imagenPokemonAnterior.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                imagenPokemonAnteriorMousePressed(evt);
            }
        });
        getContentPane().add(imagenPokemonAnterior, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 400, 90, 100));
        getContentPane().add(imagenPokemon, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 250, 260));

        cuadroImagenPokemon.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 0, 0)));
        getContentPane().add(cuadroImagenPokemon, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 250, 260));

        textoDescripcion.setFont(new java.awt.Font("Pokemon Classic", 0, 9)); // NOI18N
        textoDescripcion.setForeground(new java.awt.Color(0, 0, 0));
        textoDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        textoDescripcion.setText("Descripcion");
        getContentPane().add(textoDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 400, 340, 110));

        cuadroDescripcion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDescripcion.jpg"))); // NOI18N
        getContentPane().add(cuadroDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 390, -1, 130));

        tablaPokemonScroll.setHorizontalScrollBar(null);
        tablaPokemonScroll.setVerifyInputWhenFocusTarget(false);

        tablaPokemon.setBackground(new java.awt.Color(248, 248, 248));
        tablaPokemon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaPokemon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaPokemon.setGridColor(new java.awt.Color(248, 248, 248));
        tablaPokemon.setSelectionBackground(new java.awt.Color(232, 48, 48));
        tablaPokemon.setShowHorizontalLines(false);
        tablaPokemon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaPokemonMousePressed(evt);
            }
        });
        tablaPokemonScroll.setViewportView(tablaPokemon);

        getContentPane().add(tablaPokemonScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(813, 51, 170, 280));

        listaPokemon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroDialogoGrande.jpg"))); // NOI18N
        getContentPane().add(listaPokemon, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 40, -1, 300));

        encabezado1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/encabezado.jpg"))); // NOI18N
        getContentPane().add(encabezado1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, 520, 30));

        encabezado2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/encabezado.jpg"))); // NOI18N
        getContentPane().add(encabezado2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 30));

        encabezado3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/encabezado.jpg"))); // NOI18N
        getContentPane().add(encabezado3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 0, 520, 30));

        tipo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tiposPokemon/Tipo_bicho.gif"))); // NOI18N
        getContentPane().add(tipo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 250, -1, 20));

        tipo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tiposPokemon/Tipo_agua.gif"))); // NOI18N
        getContentPane().add(tipo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 280, 50, 20));

        botonSilencio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonSilencio.png"))); // NOI18N
        botonSilencio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonSilencioMousePressed(evt);
            }
        });
        getContentPane().add(botonSilencio, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 550, -1, 50));

        imagenHuella.setForeground(new java.awt.Color(255, 0, 51));
        imagenHuella.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imagenHuella.setIcon(new javax.swing.ImageIcon(getClass().getResource("/huellas/149.png"))); // NOI18N
        imagenHuella.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 0, 0)));
        getContentPane().add(imagenHuella, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 310, 60, 70));

        numeroPokemon.setFont(new java.awt.Font("Pokemon Classic", 1, 24)); // NOI18N
        numeroPokemon.setForeground(new java.awt.Color(51, 51, 51));
        numeroPokemon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numeroPokemon.setText("#");
        getContentPane().add(numeroPokemon, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 280, 120, 60));

        cuadroNumeroPokemon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroNumero.png"))); // NOI18N
        getContentPane().add(cuadroNumeroPokemon, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 270, 180, -1));

        busquedaError.setFont(new java.awt.Font("Pokemon Classic", 0, 10)); // NOI18N
        busquedaError.setForeground(new java.awt.Color(232, 48, 48));
        busquedaError.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        busquedaError.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                busquedaErrorMousePressed(evt);
            }
        });
        getContentPane().add(busquedaError, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 350, -1, -1));

        busqueda.setBackground(new java.awt.Color(248, 248, 248));
        busqueda.setFont(new java.awt.Font("Pokemon Classic", 0, 10)); // NOI18N
        busqueda.setForeground(new java.awt.Color(232, 48, 48));
        busqueda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        busqueda.setText("Buscar");
        busqueda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                busquedaMousePressed(evt);
            }
        });
        getContentPane().add(busqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 390, 170, 30));

        cuadroBusqueda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/cuadroBusqueda.png"))); // NOI18N
        getContentPane().add(cuadroBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 380, -1, -1));

        buscar.setFont(new java.awt.Font("Pokemon Classic", 1, 10)); // NOI18N
        buscar.setForeground(new java.awt.Color(0, 0, 0));
        buscar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buscar.setText("BUSCAR");
        buscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buscarMousePressed(evt);
            }
        });
        getContentPane().add(buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 450, 90, 30));

        botonBusqueda1.setFont(new java.awt.Font("Pokemon Classic", 1, 10)); // NOI18N
        botonBusqueda1.setForeground(new java.awt.Color(0, 0, 0));
        botonBusqueda1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        botonBusqueda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeño.jpg"))); // NOI18N
        botonBusqueda1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonBusqueda1MousePressed(evt);
            }
        });
        getContentPane().add(botonBusqueda1, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 440, 110, -1));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/fondo.jpg"))); // NOI18N
        fondo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fondoMousePressed(evt);
            }
        });
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1010, 640));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonPequeñoUltimoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonPequeñoUltimoMousePressed
	botonPequeñoUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeñoPulsado.jpg")));
	contador = total_pokemons-1;
	imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	escribeDatos();
    }//GEN-LAST:event_botonPequeñoUltimoMousePressed

    private void botonPequeñoUltimoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonPequeñoUltimoMouseReleased
        released();
    }//GEN-LAST:event_botonPequeñoUltimoMouseReleased

    private void botonPequeñoPrimeroMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonPequeñoPrimeroMousePressed
        botonPequeñoPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeñoPulsado.jpg")));
	contador=0;
	imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	escribeDatos();
    }//GEN-LAST:event_botonPequeñoPrimeroMousePressed

    private void botonPequeñoPrimeroMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonPequeñoPrimeroMouseReleased
        released();
    }//GEN-LAST:event_botonPequeñoPrimeroMouseReleased

    private void iconoSecretoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoSecretoMousePressed
        iconoSecreto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/iconoPulsado.png")));
	reproduceSonido();
    }//GEN-LAST:event_iconoSecretoMousePressed

    private void iconoSecretoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoSecretoMouseReleased
        released();
    }//GEN-LAST:event_iconoSecretoMouseReleased

    private void botonSilencioMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonSilencioMousePressed
        if(musica){
	    musicaFondo.stop();
	    musica = false;
	    botonSilencio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonSilencio2.png")));
	}
	else{
	    musica = true;
	    musicaFondo.start();
	    botonSilencio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonSilencio.png")));
	}
    }//GEN-LAST:event_botonSilencioMousePressed

    private void botonAtrasCajaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonAtrasCajaMousePressed
	botonPequeñoAtrasImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeñoPulsado.jpg")));
	flechaArriba.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/flechaArribaP.png")));
	anterior(1);
    }//GEN-LAST:event_botonAtrasCajaMousePressed

    private void botonAtrasCajaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonAtrasCajaMouseReleased
        released();
    }//GEN-LAST:event_botonAtrasCajaMouseReleased

    private void botonAdelanteCajaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonAdelanteCajaMousePressed
	botonPequeñoAdelanteImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeñoPulsado.jpg")));
	flechaAbajo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/flechaAbajoP.png")));
	siguiente(1);
    }//GEN-LAST:event_botonAdelanteCajaMousePressed

    private void botonAdelanteCajaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonAdelanteCajaMouseReleased
        released();
    }//GEN-LAST:event_botonAdelanteCajaMouseReleased

    private void imagenPokemonPosteriorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imagenPokemonPosteriorMousePressed
	contador = Integer.parseInt(listaPokemons.get(String.valueOf(contador+1)).posEvolucion.trim());
	contador--;
	imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	escribeDatos();
    }//GEN-LAST:event_imagenPokemonPosteriorMousePressed

    private void imagenPokemonAnteriorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imagenPokemonAnteriorMousePressed
        contador = Integer.parseInt(listaPokemons.get(String.valueOf(contador+1)).preEvolucion.trim());
	contador--;
	imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	escribeDatos();
    }//GEN-LAST:event_imagenPokemonAnteriorMousePressed

    private void botonSuma10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonSuma10MousePressed
	botonSuma10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonSumaP.png")));
	imagenPokemonPosterior.setIcon(null);
	siguiente(10);
    }//GEN-LAST:event_botonSuma10MousePressed

    private void botonSuma10MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonSuma10MouseReleased
        released();
    }//GEN-LAST:event_botonSuma10MouseReleased

    private void botonResta10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonResta10MousePressed
        botonResta10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonRestaP.png")));
	imagenPokemonPosterior.setIcon(null);
	anterior(10);
    }//GEN-LAST:event_botonResta10MousePressed

    private void botonResta10MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonResta10MouseReleased
        released();
    }//GEN-LAST:event_botonResta10MouseReleased

    private void botonPrimeroMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonPrimeroMousePressed
	botonPequeñoPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeñoPulsado.jpg")));
	contador=0;
	imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	escribeDatos();
    }//GEN-LAST:event_botonPrimeroMousePressed

    private void botonPrimeroMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonPrimeroMouseReleased
       released();
    }//GEN-LAST:event_botonPrimeroMouseReleased

    private void botonUltimoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonUltimoMousePressed
        botonPequeñoUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spritesMenu/botonPequeñoPulsado.jpg")));
	contador = total_pokemons-1;
	imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	escribeDatos();
    }//GEN-LAST:event_botonUltimoMousePressed

    private void botonUltimoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonUltimoMouseReleased
        released();
    }//GEN-LAST:event_botonUltimoMouseReleased

    private void busquedaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_busquedaMousePressed
        busqueda.setText("");
	busqueda.setFocusable(true);
    }//GEN-LAST:event_busquedaMousePressed

    private void buscarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buscarMousePressed
        busquedaError.setText("");
	String palabraBusqueda = busqueda.getText();
	System.out.println(palabraBusqueda);
	    try {
		palabraBusqueda = palabraBusqueda.toLowerCase();
		palabraBusqueda = palabraBusqueda.substring(0, 1).toUpperCase() + palabraBusqueda.substring(1);
		Class.forName("com.mysql.jdbc.Driver");
		conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1/pokemondb", "root", "root");
		estado = conexion.createStatement();
		resultadoConsulta = estado.executeQuery("SELECT buscaNombre('" + palabraBusqueda + "')");
		if (resultadoConsulta.next() && resultadoConsulta.getInt(1) > 0) {
		    contador = (resultadoConsulta.getInt(1) - 1);
		    busqueda.setText(null);
		} else {
		    busquedaError.setText(palabraBusqueda + " no es un pokemon");
		}
	    } catch (Exception e) {
		e.getMessage();
		busquedaError.setText("Introduce un nombre");

	    }
	    imagenPokemon.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, anchoIcono, altoIcono));
	    escribeDatos();
    }//GEN-LAST:event_buscarMousePressed

    private void busquedaErrorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_busquedaErrorMousePressed
        busquedaError.setText("");
    }//GEN-LAST:event_busquedaErrorMousePressed

    private void botonBusqueda1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonBusqueda1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonBusqueda1MousePressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
       switch (evt.getKeyCode()){
	   case KeyEvent.VK_S: siguiente(1); break;
	   case KeyEvent.VK_W: anterior(1); break;
	   case KeyEvent.VK_D: siguiente(10); break;
	   case KeyEvent.VK_A: anterior(10); break;
	   case KeyEvent.VK_DOWN: siguiente(1); break;
	   case KeyEvent.VK_UP: anterior(1); break;
	   case KeyEvent.VK_RIGHT: siguiente(10); break;
	   case KeyEvent.VK_LEFT: anterior(10);break;
       }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        released();
    }//GEN-LAST:event_formKeyReleased

    private void fondoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fondoMousePressed
       busqueda.setFocusable(false);
    }//GEN-LAST:event_fondoMousePressed

    private void tablaPokemonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaPokemonMousePressed
	
    }//GEN-LAST:event_tablaPokemonMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
	/* Set the Nimbus look and feel */
	//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
	/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
	 */
	try {
	    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		if ("Nimbus".equals(info.getName())) {
		    javax.swing.UIManager.setLookAndFeel(info.getClassName());
		    break;
		}
	    }
	} catch (ClassNotFoundException ex) {
	    java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (InstantiationException ex) {
	    java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (IllegalAccessException ex) {
	    java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (javax.swing.UnsupportedLookAndFeelException ex) {
	    java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	}
	//</editor-fold>

	/* Create and display the form */
	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		new VentanaPrincipal().setVisible(true);
	    }
	});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ataque1;
    private javax.swing.JLabel ataque2;
    private javax.swing.JLabel ataque3;
    private javax.swing.JLabel ataque4;
    private javax.swing.JLabel botonAdelanteCaja;
    private javax.swing.JLabel botonAtrasCaja;
    private javax.swing.JLabel botonBusqueda1;
    private javax.swing.JLabel botonPequeñoAdelanteImagen;
    private javax.swing.JLabel botonPequeñoAtrasImagen;
    private javax.swing.JLabel botonPequeñoPrimero;
    private javax.swing.JLabel botonPequeñoUltimo;
    private javax.swing.JLabel botonPrimero;
    private javax.swing.JLabel botonResta10;
    private javax.swing.JLabel botonSilencio;
    private javax.swing.JLabel botonSuma10;
    private javax.swing.JLabel botonUltimo;
    private javax.swing.JLabel buscar;
    private javax.swing.JTextField busqueda;
    private javax.swing.JLabel busquedaError;
    private javax.swing.JLabel cuadroAltura;
    private javax.swing.JLabel cuadroBusqueda;
    private javax.swing.JLabel cuadroDescripcion;
    private javax.swing.JLabel cuadroGeneracion;
    private javax.swing.JLabel cuadroHabitat;
    private javax.swing.JLabel cuadroImagenPokemon;
    private javax.swing.JLabel cuadroNombre;
    private javax.swing.JLabel cuadroNumeroPokemon;
    private javax.swing.JLabel cuadroPeso;
    private javax.swing.JLabel cuadroataque1;
    private javax.swing.JLabel cuadroataque2;
    private javax.swing.JLabel cuadroataque3;
    private javax.swing.JLabel cuadroataque4;
    private javax.swing.JLabel encabezado1;
    private javax.swing.JLabel encabezado2;
    private javax.swing.JLabel encabezado3;
    private javax.swing.JLabel flechaAbajo;
    private javax.swing.JLabel flechaArriba;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel iconoSecreto;
    private javax.swing.JLabel imagenHuella;
    private javax.swing.JLabel imagenPokemon;
    private javax.swing.JLabel imagenPokemonAnterior;
    private javax.swing.JLabel imagenPokemonPosterior;
    private javax.swing.JLabel listaPokemon;
    private javax.swing.JLabel nombrePokemon;
    private javax.swing.JLabel numeroPokemon;
    private javax.swing.JTable tablaPokemon;
    private javax.swing.JScrollPane tablaPokemonScroll;
    private javax.swing.JLabel textoAltura;
    private javax.swing.JLabel textoDescripcion;
    private javax.swing.JLabel textoGeneracion;
    private javax.swing.JLabel textoHabitat;
    private javax.swing.JLabel textoPeso;
    private javax.swing.JLabel tipo1;
    private javax.swing.JLabel tipo2;
    // End of variables declaration//GEN-END:variables
}
