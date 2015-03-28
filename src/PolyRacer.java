/*	Copyright (c) 2013, Micros. All rights reserved.
*	Redistribution and use in source and binary forms, 
*	with or without modification, are not permitted.
*/
/**
*@author Konrad Pfundner
	*/
import javafx.scene.shape.Circle;

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
   static int pWidth = 1200, pHeight = 700, dataMax = 0, dataMin = 0;
   Graphics bufferGraphics; 
   BufferedImage bf = new BufferedImage (pWidth, pHeight,BufferedImage.TYPE_INT_RGB);
   Point mouse = new Point(0, 0);
   ArrayList<Point> path = new ArrayList<Point>(), data = new ArrayList<Point>();
    Player player = null;
   boolean right = false, left = false, up = false, down = false;
   int framesPerSecond = 60, view = 0; double scaleAnimation = 1, score = 0;

   long period = ((long)(1000/framesPerSecond))*1000000L;
   Rectangle startButton;

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

      setSize(pWidth, pHeight);
      setBackground(Color.black);

      dim = getSize(); 
      bufferGraphics = bf.getGraphics();

      addKeyListener(this);
      addMouseListener(this);
      addMouseMotionListener (this);

      font = new Font ("Impact", Font.PLAIN, 20);

      startButton = new Rectangle(pWidth - 101, pHeight - 51, 100, 50);
      dataMax = 50 + 70 + (pHeight/2) + 25;
      dataMin = -70 + (pHeight/2) - 25;
      for(int i = 0, j = 0; i < 10000; i++)
      {
         int switchVar = random.nextInt(50)+1;

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


            //Main dipped dots
            data.add(new Point((int) ((double) (pWidth) / 10000 * i), randomValue + pHeight / 2 + 25));

            if(i < 99999)
            {
               //Random Extra dots
               data.add(++i, new Point((int) ((double) (pWidth) / 10000 * i), randomValue + randomOffset + pHeight / 2 + 25));
            }

         }
         else
         {
            int randomValue = random.nextInt(50);
            int randomOffset = random.nextInt(70);

            data.add(new Point((int) ((double) (pWidth) / 10000 * i), randomValue + pHeight / 2 - 25));

            if(i < 99999)
            {
               //Random Extra dots
               data.add(++i, new Point((int) ((double) (pWidth) / 10000 * i), randomValue - randomOffset + pHeight / 2-25));
            }

         }

      }

      start();//starts main thread
   }

    public void drawButton(Graphics2D g)
    {
        g.setColor(Color.red);
        g.drawRect((int)startButton.getX(), (int)startButton.getY(), (int)startButton.getWidth(), (int)startButton.getHeight());
    }

   /**when something changes it calls this to update the screen
	*@param g used to print graphics
	*/
   public void update(Graphics g)
   {
      paint(g);
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
      g2.setFont(font);

      g2.setColor(Color.black);
      g2.fillRect(0, 0, pWidth, pHeight);
       double yy = (((double)dataMin)/((double)pHeight/((double)dataMax-(double)dataMin)))*scaleAnimation;
       if(view == 0) {//SETTING PATH
           g2.setColor(Color.white);
           for (int i = 0; i < 10000; i++)
                g2.drawRect((int) data.get(i).getX(), (int) data.get(i).getY(), 1, 1);
           g2.setColor(Color.green);
           Point previous = null, next = null;
           Iterator<Point> it = path.iterator();
           while(it.hasNext())//prints path
           {
               if(previous == null)
                   previous = it.next();
               else
               {
                   next = it.next();
                   g2.draw(new Line2D.Double(previous.getX(), previous.getY(), next.getX(), next.getY()));
                   previous = next;
               }
               g2.fillOval((int) (previous.getX()-5), (int)(previous.getY()-5), 10, 10);
           }
           drawButton(g2);
       }
       else if(view == 1) {//ZOOMING IN ON GAME
           if (scaleAnimation < (double) pHeight / (double) (dataMax - dataMin))
               scaleAnimation += 0.01;
           else if (scaleAnimation > (double) pHeight / (double) (dataMax - dataMin)) {
               scaleAnimation = (double) pHeight / (double) (dataMax - dataMin);
               view = 2;
           }
           g2.setColor(Color.white);

           for (int i = 0; i < 10000; i++)
               g2.drawRect((int) (data.get(i).getX() * scaleAnimation), (int) ((data.get(i).getY() - (((double) dataMin) / ((double) pHeight / ((double) dataMax - (double) dataMin))) * scaleAnimation) * scaleAnimation), 1, 1);
           g2.setColor(Color.green);
           Point previous = null, next = null;
           Iterator<Point> it = path.iterator();
           while (it.hasNext()) {
               if (previous == null)
                   previous = it.next();
               else {
                   next = it.next();
                   g2.draw(new Line2D.Double(previous.getX() * scaleAnimation, (previous.getY() - yy) * scaleAnimation, next.getX() * scaleAnimation, (next.getY() - yy) * scaleAnimation));
                   previous = next;
               }
               g2.fillOval((int) (previous.getX() * scaleAnimation) - 5, (int) ((previous.getY() - yy) * scaleAnimation) - 5, 10, 10);
           }
       }
           else if(view == 2) {//PLAYING GAME
           g2.setColor(Color.cyan);
           g2.drawString("Score: " + score, 10, 10);
           g2.setColor(Color.white);
           Iterator<Point> dots = data.iterator();
           Point dot;
           while (dots.hasNext()) {//prints all the dots
               dot = dots.next();
               Point d = new Point((int) (dot.getX() * scaleAnimation), (int) ((dot.getY() - (((double) dataMin) / ((double) pHeight / ((double) dataMax - (double) dataMin))) * scaleAnimation) * scaleAnimation));
               g2.fillRect((int)d.getX(), (int)d.getY(), 1, 1);
               if(player.getRectangle().contains(d))
               {
                   dots.remove();
                   score++;
               }

           }
           g2.setColor(Color.green);
           Point previous = null, next = null;
           Iterator<Point> it = path.iterator();
           while (it.hasNext()) {//prints the line/land
               if (previous == null)
                   previous = it.next();
               else {
                   next = it.next();
                   Shape current = new Line2D.Double(previous.getX() * scaleAnimation, (previous.getY() - yy) * scaleAnimation, next.getX() * scaleAnimation, (next.getY() - yy) * scaleAnimation);
                   g2.draw(current);
                   previous = next;
                   if(player != null)
                        physics(current);
               }
               g2.fillOval((int) (previous.getX() * scaleAnimation) - 5, (int) ((previous.getY() - yy) * scaleAnimation) - 5, 10, 10);
           }
           g2.setColor(Color.red);
           g2.fillRect((int)(player.getRectangle().getX() * scaleAnimation), (int) ((player.getRectangle().getY() - yy) * scaleAnimation), 10, 20);
           //System.out.println(view);

       }
      //g2.draw(new Line2D.Double(0, dataMax, pWidth, dataMax));
      //g2.draw(new Line2D.Double(0, dataMin, pWidth, dataMin));
      //g2.fillRect((int)mouse.getX(), (int)mouse.getY(), 10, 10);
      //g2.fillRect((int)thing.getX(), (int)thing.getY(), 10, 10);
      //for double buffer, when it is done printing everything
      //bufferGraphics.drawString (powerups.size() + "", 100, 100);
      g.drawImage(bf,0,0,this);
   }

    public void physics(Shape current)
    {
        if(player.getRectangle().intersectsLine((Line2D) current))
            player.setVelocityY(1);
        else
            player.setVelocityY(-3);
    }

   public void gameUpdate()
   {
       if(player != null) {
           if (left)
               player.setVelocityX(-1);
           else if (right)
               player.setVelocityX(1);
           else if (player.getVelocityX() != 0)
               player.setVelocityX(0);
           if (up)
               player.setVelocityY(1);
           else if (player.getVelocityY() != 0)
               player.setVelocityY(0);
           player.update();
       }
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
   {
       Point m = new Point(e.getX(), e.getY());
       if(startButton.contains(m) && path.size() > 0) {

           view = 1;
       }
       else if(view == 0)
       {
           path.add(m);
           if(player == null) {
               double yy = (((double)dataMin)/((double)pHeight/((double)dataMax-(double)dataMin)))*scaleAnimation;
               player = new Player(new Rectangle((int) (m.getX() * scaleAnimation), (int) ((m.getY() - 20) * scaleAnimation), 10, 20));
               //player.setRectangle(new Rectangle((int)(player.getRectangle().getX() * scaleAnimation), (int) ((player.getRectangle().getY() - 20) * scaleAnimation), 10, 20));
           System.out.println("playerx: "+(int) (m.getX() * scaleAnimation) + "playery: "+(int) ((m.getY() - yy) * scaleAnimation));
           System.out.println("playerx: "+e.getX() * scaleAnimation  + "playery: "+(e.getY() - yy) * scaleAnimation);
           }
       }
   }
   public void mouseDragged (MouseEvent e)
   {}
   /**changes the cursor to the hand when you can click on a button
	*@param e info about the whereabouts of the mouse
	*/
   public void mouseMoved (MouseEvent e)
   {
      mouse.setLocation(e.getX(), e.getY());
       if(startButton.contains(mouse))
           setCursor(new Cursor(Cursor.HAND_CURSOR));
       else if(!(getCursor().equals(Cursor.CROSSHAIR_CURSOR)))
           setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
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