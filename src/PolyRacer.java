/*	Copyright (c) 2013, Micros. All rights reserved.
*	Redistribution and use in source and binary forms, 
*	with or without modification, are not permitted.
*/
/**
*@author Konrad Pfundner
	*/
import java.applet.*;
import java.awt.*;
import java.util.*;
import java.awt.geom.Line2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
public class PolyRacer extends Applet
 implements MouseListener, MouseMotionListener, KeyListener, Runnable, WindowListener
{
   /**
 * 
 */
   private static final long serialVersionUID = 1L;
   static Random random = new Random ();//used to create random numbers
   Thread th, l, s;//threads
   Font font;
   Dimension dim;
   private static final int NO_DELAYS_PER_YIELD = 16;
   private static int MAX_FRAME_SKIPS = 5;
   static int pWidth = 600, pHeight = 400;
   Graphics bufferGraphics; 
   BufferedImage bf = new BufferedImage (pWidth, pHeight,BufferedImage.TYPE_INT_RGB);
   int menu = 0;
   Point mouse = new Point(0, 0), thing = new Point(50, 50);
   Image mainMenu, highscores, extras;
   boolean right = false, left = false, up = false, down = false, play = false, limbo = false, textBoxRunning = false;
   int framesPerSecond = 60;
   Point[] data = new Point[10000];
   long period = ((long)(1000/framesPerSecond))*1000000L;
   
   public Image getImage(String f)
   {
      Image img = null;
      try
      {
         java.io.DataInputStream in =
            new java.io.DataInputStream(
            getClass().getResourceAsStream(f));
         byte[] data = new byte[in.available()];
         in.readFully(data);
         in.close();
         img = Toolkit.getDefaultToolkit().createImage(data);
      }
      catch(Exception e){img = getImage(getCodeBase(), f);}
   
      MediaTracker mt = new MediaTracker(this);
      mt.addImage(img, 0);
      try{mt.waitForID(0);}
      catch(InterruptedException e){}
   
      return img;
   }
   
   /**initializes applet
	*/
   public void init()
   {
      boolean dipp = false;
      int dataMin;
      int dataMax;

      setSize(pWidth, pHeight);
      setBackground(Color.black);

      dim = getSize(); 
      bufferGraphics = bf.getGraphics();

      addKeyListener(this);
      addMouseListener(this);
      addMouseMotionListener (this);

      font = new Font ("Impact", Font.PLAIN, 20);

      for(int i = 0, j = 0; i < 10000; i++)
      {
         int switchVar = random.nextInt(25)+1;

         if(j%switchVar == 0)
         {
            dipp = random.nextBoolean();
         }

         if(j==0)
         {
            j=1;
         }

         if(dipp)
         {
            int randomValue = random.nextInt(50);
            int randomOffset = random.nextInt(70);
            dataMax = randomValue + randomOffset + 200;

            //Main dipped dots
            data[i] = new Point((int)(600.0/10000*i), randomValue + 200);

            if(i < 99999)
            {
               //Random Extra dots
               data[++i] = new Point((int)(600.0/10000*i), dataMax);
            }

         }
         else
         {
            int randomValue = random.nextInt(50);
            int randomOffset = random.nextInt(70);
            dataMax = randomValue - randomOffset + 200;
            
            data[i] = new Point((int)(600.0/10000*i), randomValue + 150);

            if(i < 99999)
            {
               //Random Extra dots
               data[++i] = new Point((int)(600.0/10000*i), dataMax);
            }

         }

      }

      start();//starts main thread
   }

   /**when something changes it calls this to update the screen
	*@param g used to print graphics
	*/
   public void update(Graphics g)
   {
      paint(g);
   }

         /**draws the ground
	*@param g used to print graphics
	*@param c the colour to print the ground
	*/
   public void drawGround (Graphics g, Color c)
   {
      Graphics2D g2 = (Graphics2D)g.create();//for printing gradients
      g2.setColor(c);
      int [] groundX = {0, 600, 600, 0};//creates gradient that goes from light grey to the colour passed in the parameter
      GradientPaint gradient = new GradientPaint(0,200,Color.lightGray,0,0,c,true);//light grey to make the fading effect
      g2.setPaint(gradient);
      int [] groundY = {5, 5};//(int)(195-(((la+(ra*(-1)))*2.8)*320)), (int)(195+(((la+(ra*(-1)))*2.8)*320)), 400, 400};//calculate x/y values
      Polygon ground = new Polygon(groundX, groundY, 4);
      g2.fillPolygon(ground);//prints the polygon
      //}
   }
   
   /**This method is used to print everything to the screen
	*@param g used to print graphics
	*/
   public void paint(Graphics g)
   {
      if (bf == null){
         bf = new BufferedImage (pWidth, pHeight,BufferedImage.TYPE_INT_RGB);
         if (bf == null) {
            System.out.println("dbImage is null");
            return;
         }
         else
            bufferGraphics = bf.getGraphics();
      }
       Graphics2D g2 = (Graphics2D) bufferGraphics;
      g2.setFont (font);

      g2.setColor(Color.black);
      g2.fillRect(0, 0, 600, 400);
      g2.setColor(Color.white);
      for(int i = 0; i < 10000; i++)
          g2.drawRect((int)data[i].getX(), (int)data[i].getY(), 1, 1);
   
      g2.fillRect((int)mouse.getX(), (int)mouse.getY(), 10, 10);
      g2.fillRect((int)thing.getX(), (int)thing.getY(), 10, 10);
      //for double buffer, when it is done printing everything
      //bufferGraphics.drawString (powerups.size() + "", 100, 100);
      g.drawImage(bf,0,0,this);
   }
      
   public void gameUpdate()
   {
      if(left)
         thing.translate(-1, 0);
      if(right)
         thing.translate(1, 0);
      if(up)
         thing.translate(0, -1);
      if(down)
         thing.translate(0, 1);
   }
   
   /**Main thread that is started at the start of the program and calls everything to make the game smooth
	*/
   public void run()
   {
      long beforeTime, afterTime, timeDiff, sleepTime;
      long overSleepTime = 0L;
      int noDelays = 0;
      long excess = 0L;
      while (true)
      {
         beforeTime = System.nanoTime();
         gameUpdate(); 
         repaint();
         afterTime = System.nanoTime();
         timeDiff = afterTime - beforeTime;
         sleepTime = (period - timeDiff) - overSleepTime;  
         if (sleepTime > 0) {   // some time left in this cycle
            try {
               Thread.sleep(sleepTime/1000000L);  // nano -> ms
            }
            catch(InterruptedException ex){}
            overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
         }
         else {    // sleepTime <= 0; the frame took longer than the period
            excess -= sleepTime;  // store excess time value
            overSleepTime = 0L;
            if (++noDelays >= NO_DELAYS_PER_YIELD) {
               Thread.yield();   // give another thread a chance to run
               noDelays = 0;
            }
         }
         beforeTime = System.nanoTime();
         int skips = 0;
         while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
            excess -= period;
            gameUpdate();    // update state but don't render
            skips++;
         }
      }
   }
   
   /**for the keylisteners to get key events
	*@param e info about the whereabouts of the mouse
	*/
   public void mouseEntered (MouseEvent e)
   {
      requestFocusInWindow(); 
   }
   public void mouseExited (MouseEvent e)
   {}
   public void mouseClicked (MouseEvent e)
   {}
   public void mouseDragged (MouseEvent e)
   {}
   /**changes the cursor to the hand when you can click on a button
	*@param e info about the whereabouts of the mouse
	*/
   public void mouseMoved (MouseEvent e)
   {
      mouse.setLocation(e.getX(), e.getY());
      
   }
   /**used for main menu to check when they click buttons
	*@param e contains mouse position
	*/
   public void mousePressed (MouseEvent e)
   {
   }
   public void mouseReleased (MouseEvent e)
   {}
   public void keyTyped (KeyEvent e)
   {}
   /**when you release the arrow keys it resets the vaiables
	*@param e contains which key you released
	*/
   public void keyReleased (KeyEvent e)
   {
      if (e.getKeyCode () == KeyEvent.VK_D  && right || e.getKeyCode () == KeyEvent.VK_RIGHT && right)
         right = false;
      if (e.getKeyCode () == KeyEvent.VK_A && left || e.getKeyCode () == KeyEvent.VK_LEFT && left)
         left = false;
      if (e.getKeyCode () == KeyEvent.VK_W  && up || e.getKeyCode () == KeyEvent.VK_UP && up)
         up = false;
      if (e.getKeyCode () == KeyEvent.VK_S && down || e.getKeyCode () == KeyEvent.VK_DOWN && down)
         down = false;
   }
   /**Used to move ship when you press keys
	*@param e contains which key you pressed
	*/
   public void keyPressed(KeyEvent e)
   {
      if (e.getKeyCode () == KeyEvent.VK_D && !right || e.getKeyCode () == KeyEvent.VK_RIGHT  && !right)
         right = true;
      if (e.getKeyCode () == KeyEvent.VK_A  && !left || e.getKeyCode () == KeyEvent.VK_LEFT  && !left)
         left = true;
      if (e.getKeyCode () == KeyEvent.VK_W  && !up || e.getKeyCode () == KeyEvent.VK_UP  && !up)
         up = true;
      if (e.getKeyCode () == KeyEvent.VK_S && !down || e.getKeyCode () == KeyEvent.VK_DOWN && !down)
         down = true;
   }
	      /**method called at the very start of the program to start main thread
	*/
   public void start()
   {
      th = new Thread(this);
      th.start ();
   }
   /**Used to stop main thread
	*/
   public void stop()
   {
      //th.stop();
      th = null;
   }
   /**Other methods for window events
	*/
   public void windowClosing(WindowEvent e)
   {}
   public void windowOpened(WindowEvent e)
   {}
   public void windowIconified(WindowEvent e)
   {}
   public void windowClosed(WindowEvent e)
   {}
   public void windowDeiconified(WindowEvent e)
   {}
   public void windowActivated(WindowEvent e)
   {}
   public void windowDeactivated(WindowEvent e)
   {}
}