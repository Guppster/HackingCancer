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
//import java.io.BufferedInputStream;
//import java.io.*;
//import javax.imageio.*;
//import javax.swing.ImageIcon;
public class PolyRacer extends Applet
 implements MouseListener, MouseMotionListener, KeyListener, Runnable, WindowListener
{
   /**
 * 
 */
   private static final long serialVersionUID = 1L;
   static Random random = new Random ();//used to create random numbers
   Thread th, l, s;//threads
   //volatile int textFocus = 0;
   Font font;
   Dimension dim;
   //String [] extrasTextBoxes = new String [8];
   //int type = 0, renderDistance = 50;
   //double printTimer = 0;
   //private long framesSkipped = 0L;
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
   Line2D[] data = new Line2D[600];
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
      setSize(pWidth,pHeight);
      setBackground(Color.black);
      dim = getSize(); 
      bufferGraphics = bf.getGraphics(); 
      addKeyListener (this);
      addMouseListener (this);
      addMouseMotionListener (this);
      font = new Font ("Impact", Font.PLAIN, 20);
      for(int i = 0; i < 600; i++)
         data[i] = new Line2D.Double(i, random.nextInt(50) + 150, i, random.nextInt(50) + 200);
      /*Thread t = new Thread(createCubes);//starts thread to creates cubes
      t.start();
      Thread p = new Thread(createPowerups);//starts thread to create powerups
      p.start();
      try{p.join(500);}//waits for thread to finish
      catch (InterruptedException e){}
      try{t.join(500);}//waits for thread to finish
      catch (InterruptedException e){}*/
      start();//starts main thread
   }

   /**when something changes it calls this to update the screen
	*@param g used to print graphics
	*/
   public void update(Graphics g)
   {
      paint(g);
   }
   
      /*
      Thread s = new Thread(sort);//starts thread to sort cubes
            s.start();
            try{s.join(500);}//waits for thread to finish sorting the cubes
            catch (InterruptedException ee){}*/
   /**calls merge sort to sort the cubes depending on their z coordinate*/
   Runnable sort = 
      new Runnable() 
      {
         public void run() 
         {
            //cubes = mergeSort(cubes);
         }
      }; 
      
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
      //bufferGraphics.setColor(Color.black);
      //bufferGraphics.fillRect(0, 0, 600, 400);
      //Graphics2D g2d = (Graphics2D) bufferGraphics;//sets font for current score and highscore menu
       Graphics2D g2 = (Graphics2D) bufferGraphics;
      g2.setFont (font);
      /*if(!limbo)//in between games i dont want to print the level
      {//makes new thread for printing the level
         Thread l = new Thread(printLevel);
         l.start();
         try{l.join(500);}//waits for it to finish
         catch(Exception e){System.out.println("Failed to wait for printLevel");}
      }
      if(play)//if game is started it prints ship
      {
         Thread s = new Thread(printShip);
         s.start();
         if(c.z-100<printTimer)
         {
            bufferGraphics.setColor(Color.green);
            bufferGraphics.drawString(powerupType[type],(pWidth/2)-100, 100);
         }
         try{s.join(500);}
         catch(Exception e){}
      }
      else if(limbo)
      {*/
      g2.setColor(Color.black);
      g2.fillRect(0, 0, 600, 400);
      g2.setColor(Color.white);
      for(int i = 0; i < 600; i++)
          g2.draw(data[i]);
   
      g2.fillRect((int)mouse.getX(), (int)mouse.getY(), 10, 10);
      g2.fillRect((int)thing.getX(), (int)thing.getY(), 10, 10);
      //for double buffer, when it is done printing everything
      //bufferGraphics.drawString (powerups.size() + "", 100, 100);
      g.drawImage(bf,0,0,this);
   }
   
   /**does all movement, moves the camera and the ship
	*/
   Runnable movement = 
      new Runnable() 
      {
         public void run() 
         {
            /*if(play)
            {
               if(c.z-100>pUp && pUp != 0)//for powerup that levitates you above the cubes
                  pUp=0;
               if(pUp!=0 && shipYPosition < 5)//brings you up
               {
                  shipYPosition+=0.05;
                  ship.move(0,0.05,0);
               }
               else if(pUp==0 && shipYPosition > 0)//brings you back down
               {
                  shipYPosition-=0.05;
                  ship.move(0,-0.05,0);
               }
               if(left)//left
                  la += acceleration;
               else
               {
                  if(la>0)
                     la-=handling;
                  if(la<0)
                     la=0;
               }
               if(right)//right
                  ra += acceleration;
               else
               {
                  if(ra>0)
                     ra-=handling;
                  if(ra<0)
                     ra=0;
               }//has max turn speed so they cant turn away from cubes
               if(ra>maxTurn)//faster than they spawn resulting in invincibility
                  ra = maxTurn;
               if(la>maxTurn)
                  la = maxTurn;
               if(c.z-100>slow && slow != 0)//slow powerup
                  slow=0;
               if(turbo<speed && turbo !=0)//turbo powerup
                  turbo=0;
               if(slow>0)
                  ship.move(0,0,startSpeed);//moves ship with camera
               else if(turbo>0)
                  ship.move(0,0,turbo);
               else
                  ship.move(0,0,speed);
               ship.move((ra*(-1)),0,0);//moving ship left/right with camera
               ship.move(la,0,0);
               c.x-=ra;//moving camera left/right
               c.x+=la;
            }
            else if(c.z > 20000)//during main menu the speed increases
               c.z=0;//the prevents it from getting too high
            if(slow>0)//always moves camera(for main screen)
               c.z+=startSpeed;
            else if(turbo>0)
            {
               c.z+=turbo;
               turbo-=0.02;
            }
            else
               c.z+=speed;*/
         }
      };
      
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
      /*if(menu == 2 && !textBoxRunning)
      {
         textBoxRunning = true;
         Thread t = new Thread(textBox);//does movement
         t.start();
         //extrasTextBoxes[textFocus] = extrasTextBoxes[textFocus] + "|";
      }
      else if(menu != 2 && textBoxRunning)
         textBoxRunning = false;
      if(!limbo)//if in between games then dont do anything
      {//used to optimaize game speed
         rotation();//rotates screen
         Thread m = new Thread(movement);//does movement
         m.start();
         if(c.z-100>resize && resize != 0)//after 100 units the blocks go back to normal size
         {//when they have activated the resize powerup
            resize=0;
         }
         Thread co = new Thread(collisions);//does collisions
         co.start();
         try{
            if(cubes.get(0).getFrontZ()<c.z+renderDistance && cubes.size()<numberOfCubes*2+10)//respawns powerups and cubes
            {
               Thread p = new Thread(createPowerups);
               p.start();
               Thread t = new Thread(createCubes);
               t.start();
               try{t.join(500);}
               catch (InterruptedException e){System.out.println("Not enought times whaa?");}
            }
         }
         catch(IndexOutOfBoundsException e){}
         catch(NullPointerException e){}
      
         try{m.join(500);}//waits for the thread to finish before continuing
         catch (InterruptedException ee){}
         try{co.join(500);}//waits for collisions to end
         catch (InterruptedException ee){}
         repaint();//repaints screen
      }
      else
         repaint();*/
   
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
         gameUpdate(); 
         repaint();
         beforeTime = System.nanoTime();
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