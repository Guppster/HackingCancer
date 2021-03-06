/*import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;*/

import java.applet.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.awt.geom.Line2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class HackingCancer extends Applet
        implements MouseListener, MouseMotionListener, KeyListener, Runnable, WindowListener
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    static Random random = new Random();//used to create random numbers
    Thread th;
    Font font;
    Dimension dim;
    private static final int NO_DELAYS_PER_YIELD = 16;
    private static int MAX_FRAME_SKIPS = 5;
    static int pWidth = 1000, pHeight = 600, dataMax = 0, dataMin = 0;
    Graphics bufferGraphics;
    BufferedImage bf = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_RGB);
    Point mouse = new Point(0, 0);
    ArrayList<Point> path = new ArrayList<Point>(), data = new ArrayList<Point>();
    ArrayList<Monster> mobs = new ArrayList<Monster>();
    int[] highScores = new int[10];
    Player player = null;
    Image outro, intro, instructPage, walkLeft, walkRight, blob, pathImg, standRight, standLeft;
    boolean right = false, left = false, up = false, down = false, scaled = false, sorted = false;
    int framesPerSecond = 60, view = 3, difficulty = 1;
    double scaleAnimation = 1, score = 0;


    long period = ((long) (1000 / framesPerSecond)) * 1000000L;
    Rectangle startButton, play, instructions, playAgain, back, hard, medium, easy;

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
        } catch(Exception e)
        {
            img = getImage(getCodeBase(), f);
        }

        MediaTracker mt = new MediaTracker(this);
        mt.addImage(img, 0);
        try
        {
            mt.waitForID(0);
        } catch(InterruptedException e)
        {
        }

        return img;
    }

    /*public boolean saveScore()
    {
        try
        {
            JSONObject obj = new JSONObject();
            JSONArray arr = new JSONArray(Arrays.asList(highScores));
            JSONArray jsonPath = new JSONArray(Arrays.asList(path));

            obj.put("highscores", arr);
            obj.put("path", jsonPath);

            HttpResponse<JsonNode> response = Unirest.post("https://tphummel-lru-cache.p.mashape.com/api/cache")
                    .header("X-Mashape-Key", "OpLVYdsmHgmshpfFS0t3pLcAcM0dp1lquf1jsnN0CCFde9HqSx")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(obj.toString())
                    .asJson();
            return true;
        } catch(UnirestException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void loadScore()
    {
        JSONObject data;
        //Check health of data
        try
        {
            HttpResponse<JsonNode> response = Unirest.get("https://tphummel-lru-cache.p.mashape.com/api/health")
                    .header("X-Mashape-Key", "OpLVYdsmHgmshpfFS0t3pLcAcM0dp1lquf1jsnN0CCFde9HqSx")
                    .header("Accept", "application/json")
                    .asJson();

            data = response.getBody().getObject();

            if(data.getString("status").equalsIgnoreCase("OK"))
            {
                response = Unirest.get("https://tphummel-lru-cache.p.mashape.com/api/cache/4551a08f-6506-48f4-afe9-e6add1b3bab3")
                        .header("X-Mashape-Key", "OpLVYdsmHgmshpfFS0t3pLcAcM0dp1lquf1jsnN0CCFde9HqSx")
                        .header("Accept", "application/json")
                        .asJson();
            }

            data = response.getBody().getObject();

            for(int i = 0; i < 10; i++)
            {
                highScores[i] = data.getJSONArray("highscores").getInt(i);
            }

        } catch(UnirestException e)
        {
            e.printStackTrace();
        }
    }*/

    /**
     * initializes applet
     */
    public void init()
    {

        setSize(pWidth, pHeight);
        setBackground(Color.black);

        dim = getSize();
        bufferGraphics = bf.getGraphics();

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        outro = getImage("OutroPage.png");
        intro = getImage("IntroPage.png");
        instructPage = getImage("InfoPage.png");
        walkLeft = getImage("left.gif");
        walkRight = getImage("right.gif");
        blob = getImage("blob.gif");
        pathImg = getImage("ResearchView.png");
        standRight = getImage("standRight.png");
        standLeft = getImage("standLeft.png");
        font = new Font("Impact", Font.PLAIN, 20);

        startButton = new Rectangle(810, 520, 130, 40);
        play = new Rectangle(120, 410, 130, 40);
        playAgain = new Rectangle(90, 480, 130, 40);
        instructions = new Rectangle(740, 410, 130, 40);
        back = new Rectangle(750, 510, 130, 40);
        hard = new Rectangle(pWidth / 2 - 150, pHeight - 50, 75, 20);
        medium = new Rectangle(pWidth / 2 - 50, pHeight - 50, 100, 20);
        easy = new Rectangle(pWidth / 2 + 80, pHeight - 50, 75, 20);

        for(int i = 0; i < 10; i++)
            highScores[i] = random.nextInt(2000);
        getData();

        start();//starts main thread
    }

    public void getData()
    {
        boolean dipp = false;
        dataMax = 50 + 70 + (pHeight / 2) + 25;
        dataMin = -70 + (pHeight / 2) - 25;
        for(int i = 0, j = 0; i < 10000; i++)
        {
            int switchVar = random.nextInt(50) + 1;

            if(j % switchVar == 0)
            {
                dipp = random.nextBoolean();
            }

            if(j == 0)
            {
                j = 1;
            }

            int randomMess = random.nextInt(150);
            int randomValue = random.nextInt(50);
            int randomOffset = random.nextInt(70);
            if(dipp)
            {

                //Main dipped dots
                data.add(new Point((int) ((double) (pWidth) / 10000 * i), randomValue + pHeight / 2 + 25));
                data.add(new Point((int) ((double) i), randomMess + pHeight / 3 + 20));
                data.add(new Point((int) ((double) i), randomMess + pHeight / 2));

                if(i < 99999)
                {
                    //Random Extra dots
                    data.add(++i, new Point((int) ((double) (pWidth) / 10000 * i), randomValue + randomOffset + pHeight / 2 + 25));
                }

            }
            else
            {

                data.add(new Point((int) ((double) (pWidth) / 10000 * i), randomValue + pHeight / 2 - 25));
                data.add(new Point((int) ((double) i), randomMess + pHeight / 3 + 20));
                data.add(new Point((int) ((double) i), randomMess + pHeight / 2));

                if(i < 99999)
                {
                    //Random Extra dots
                    data.add(++i, new Point((int) ((double) (pWidth) / 10000 * i), randomValue - randomOffset + pHeight / 2 - 25));
                }

            }

        }
    }

    /**
     * when something changes it calls this to update the screen
     *
     * @param g used to print graphics
     */
    public void update(Graphics g)
    {
        paint(g);
    }

    /**
     * This method is used to print everything to the screen
     *
     * @param g used to print graphics
     */
    public void paint(Graphics g)
    {
        if(bf == null)
        {
            bf = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_RGB);
            if(bf == null)
            {
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
        double yy = (((double) dataMin) / ((double) pHeight / ((double) dataMax - (double) dataMin))) * scaleAnimation;
        if(view == 3)
        {
            g2.drawImage(intro, 0, 0, null);
            g2.setColor(Color.white);
            if(difficulty == 2)
                g2.fillOval(pWidth / 2 - 150, pHeight - 50, 20, 20);
            else
                g2.drawOval(pWidth / 2 - 150, pHeight - 50, 20, 20);
            g2.drawString("Hard", pWidth / 2 - 125, pHeight - 30);
            if(difficulty == 1)
                g2.fillOval(pWidth / 2 - 50, pHeight - 50, 20, 20);
            else
                g2.drawOval(pWidth / 2 - 50, pHeight - 50, 20, 20);
            g2.drawString("Medium", pWidth / 2 - 25, pHeight - 30);
            if(difficulty == 0)
                g2.fillOval(pWidth / 2 + 80, pHeight - 50, 20, 20);
            else
                g2.drawOval(pWidth / 2 + 80, pHeight - 50, 20, 20);
            g2.drawString("Easy", pWidth / 2 + 105, pHeight - 30);
        }
        else if(view == 0)
        {//SETTING PATH
            g2.drawImage(pathImg, 0, 0, null);
            g2.setColor(Color.white);
            g2.drawString("Click to create a path consuming the greatest amount of orbs.", 50, 50);
            g2.setColor(Color.cyan);
            for(int i = 0; i < 10000; i++)
                g2.drawRect((int) data.get(i).getX(), (int) data.get(i).getY(), 1, 1);

            g2.setColor(Color.red);
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
                g2.fillOval((int) (previous.getX() - 5), (int) (previous.getY() - 5), 10, 10);
            }
        }
        else if(view == 1)
        {//ZOOMING IN ON GAME
            if(scaleAnimation < (double) pHeight / (double) (dataMax - dataMin))
                scaleAnimation += 0.01;
            else if(scaleAnimation > (double) pHeight / (double) (dataMax - dataMin))
            {
                scaleAnimation = (double) pHeight / (double) (dataMax - dataMin);
                view = 2;
            }
            g2.drawImage(pathImg, 0, 0, (int) (pWidth * scaleAnimation), (int) (pHeight * scaleAnimation), 0, 0, pWidth, pHeight, null);
            g2.setColor(Color.cyan);

            for(int i = 0; i < 10000; i++)
                g2.drawRect((int) (data.get(i).getX() * scaleAnimation), (int) ((data.get(i).getY() - (((double) dataMin) / ((double) pHeight / ((double) dataMax - (double) dataMin))) * scaleAnimation) * scaleAnimation), 1, 1);
            g2.setColor(Color.red);
            Point previous = null, next = null;
            Iterator<Point> it = path.iterator();
            while(it.hasNext())
            {
                if(previous == null)
                    previous = it.next();
                else
                {
                    next = it.next();
                    g2.draw(new Line2D.Double(previous.getX() * scaleAnimation, (previous.getY() - yy) * scaleAnimation, next.getX() * scaleAnimation, (next.getY() - yy) * scaleAnimation));
                    previous = next;
                }
                g2.fillOval((int) (previous.getX() * scaleAnimation) - 5, (int) ((previous.getY() - yy) * scaleAnimation) - 5, 10, 10);
            }
        }
        else if(view == 2)
        {//PLAYING GAME
            if(!scaled)
            {
                for(int i = 0; i < 10000; i++)
                    //prints all the dots
                    data.set(i, new Point((int) (data.get(i).getX() * scaleAnimation), (int) ((data.get(i).getY() - (((double) dataMin) / ((double) pHeight / ((double) dataMax - (double) dataMin))) * scaleAnimation) * scaleAnimation)));
                for(int j = 0; j < path.size(); j++)
                    path.set(j, new Point((int) (path.get(j).getX() * scaleAnimation), (int) ((path.get(j).getY() - (((double) dataMin) / ((double) pHeight / ((double) dataMax - (double) dataMin))) * scaleAnimation) * scaleAnimation)));
                player.setRectangle(new Rectangle((int) (player.getX() * scaleAnimation), 0, 40, 40));
                scaled = true;
            }
            g2.setColor(Color.white);
            g2.drawString("Score: " + score, 20, 20);
            g2.setColor(Color.cyan);
            Iterator<Point> dots = data.iterator();
            Point dot;
            while(dots.hasNext())
            {//prints all the dots
                dot = dots.next();
                //Point d = new Point((int) (dot.getX() * scaleAnimation), (int) ((dot.getY() - (((double) dataMin) / ((double) pHeight / ((double) dataMax - (double) dataMin))) * scaleAnimation) * scaleAnimation));
                if((dot.getX() > player.getX() - pWidth / 2 || dot.getX() < player.getX() + pWidth / 2) && (dot.getY() > player.getY() - pHeight / 2 || dot.getY() < player.getY() + pHeight / 2))
                    g2.fillRect((int) (dot.getX() - player.getX() + pWidth / 2), (int) (dot.getY() - player.getY() + pHeight / 2), 1, 1);
                if(player.getRectangle().contains(new Point((int) (dot.getX()), (int) (dot.getY()))))
                {
                    dots.remove();
                    score++;
                }
                else if(new Rectangle((int) (player.getX() - 20), (int) (player.getY() - 20), 80, 80).contains(new Point((int) (dot.getX()), (int) (dot.getY()))))
                {
                    if(dot.getX() < player.getX()) dot.setLocation(dot.getX() + 1, dot.getY());
                    if(dot.getX() > player.getX()) dot.setLocation(dot.getX() - 1, dot.getY());
                    if(dot.getY() < player.getY()) dot.setLocation(dot.getX(), dot.getY() + 1);
                    if(dot.getY() > player.getY()) dot.setLocation(dot.getX(), dot.getY() - 1);
                }
            }
            //System.out.println("X: " + path.get(0).getX() + " Y: " + path.get(0).getY() + "  " + path.size());
            g2.setColor(Color.green);
            Point previous = null, next = null;
            Iterator<Point> it = path.iterator();
            while(it.hasNext())
            {//prints the line/land
                if(previous == null)
                    previous = it.next();
                else
                {
                    next = it.next();
                    //Shape current = new Line2D.Double(previous.getX()- player.getX() + pWidth/2, previous.getY() - player.getY() + pHeight/2, next.getX() - player.getX() + pWidth/2, next.getY() - player.getY() + pHeight/2);
                    //g2.draw(current);
                    //Shape current = new Line2D.Double(previous.getX()- player.getX() + pWidth/2, previous.getY() - player.getY() + pHeight/2, next.getX() - player.getX() + pWidth/2, next.getY() - player.getY() + pHeight/2);
                    g2.setStroke(new BasicStroke(2));
                    g2.draw(new Line2D.Double((int) (previous.getX() - player.getX() + pWidth / 2), (int) (previous.getY() - player.getY() + pHeight / 2), (int) (next.getX() - player.getX() + pWidth / 2), (int) (next.getY() - player.getY() + pHeight / 2)));
                    //if(player != null)
                    //  physics(new Line2D.Double(previous.getX(), previous.getY(), next.getX(), next.getY()));//new Line2D.Double(previous.getX(), previous.getY(), next.getX(), next.getY()));
                    previous = next;
                }
                g2.fillOval((int) (previous.getX() - player.getX() + pWidth / 2 - 5), (int) (previous.getY() - player.getY() + pHeight / 2 - 5), 10, 10);

            }
            //g2.setColor(Color.red);
            for(int i = 0; i < mobs.size(); i++)
                g2.drawImage(blob, (int) (mobs.get(i).getX() - player.getX() + pWidth / 2),
                        (int) (mobs.get(i).getY() - player.getY() + pHeight / 2) + 20, null);
            //g2.fillRect((int) (mobs.get(i).getX() - player.getX() + pWidth / 2), (int) (mobs.get(i).getY() - player.getY() + pHeight / 2), 15, 15);
            //g2.setColor(Color.yellow);
            if(player.getVelocityX() != 0)
                if(player.isFacingRight())
                    g2.drawImage(walkRight, (int) (pWidth / 2), (int) (pHeight / 2), null);
                else
                    g2.drawImage(walkLeft, (int) (pWidth / 2), (int) (pHeight / 2), null);
            else if(player.isFacingRight())
                g2.drawImage(standRight, (int) (pWidth / 2), (int) (pHeight / 2), null);
            else
                g2.drawImage(standLeft, (int) (pWidth / 2), (int) (pHeight / 2), null);
            //g2.fillRect((int) (pWidth / 2), (int) (pHeight / 2), 10, 20);
            //g2.drawString("X: " + player.getX() + " Y: " + player.getY(), 20, 40);
            //g2.drawString("X: " + path.get(0).getX() + " Y: " + path.get(0).getY() + "  " + path.size(), 20, 60);
            //g2.drawString("X: " + path.get(1).getX() + " Y: " + path.get(1).getY() + "  " + path.size(), 20, 80);

        }
        if(view == 4)
        {
            g2.setColor(Color.white);
            g2.setFont(new Font("Courier New", Font.PLAIN, 23));
            g2.drawImage(outro, 0, 0, null);
            g2.drawString("" + (int) score, 850, 225);
            if(!sorted)
            {
                sorted = true;
                sortScore(highScores, (int) score);
            }
            //saveScore();
            for(int i = 0; i < 10; i++)
            {
                g2.drawString("" + (int) highScores[9-i], pWidth / 2 - 75, 200 + i * 30);
            }
            g2.setFont(font);
        }
        if(view == 5)
        {
            g2.drawImage(instructPage, 0, 0, null);
        }
        //g2.draw(new Line2D.Double(0, dataMax, pWidth, dataMax));
        //g2.draw(new Line2D.Double(0, dataMin, pWidth, dataMin));
        //g2.fillRect((int)mouse.getX(), (int)mouse.getY(), 10, 10);
        //g2.fillRect((int)thing.getX(), (int)thing.getY(), 10, 10);
        //for double buffer, when it is done printing everything
        //bufferGraphics.drawString (powerups.size() + "", 100, 100);
        g.drawImage(bf, 0, 0, this);
    }
    public void sortScore (int[] highScores, int score)
    {
        int i = 0, tmp;
        if ((int)score > highScores [0])
            highScores [0] = (int)score;
        for (int j = 0 ; j < 10 ; j++)
        {
            i = j - 1;
            tmp = highScores [j];
            while ((i >= 0) && (tmp < highScores [i]))
            {
                highScores [i + 1] = highScores [i];
                i--;
            }
            highScores [i + 1] = tmp;
        }
    }


        public  int[] selectionSort(int[] arr)
        {

            for(int i = 0; i < arr.length - 1; i++)
            {
                int index = i;
                for(int j = i + 1; j < arr.length; j++)
                    if(arr[j] < arr[index])
                        index = j;

                int smallerNumber = arr[index];
                arr[index] = arr[i];
                arr[i] = smallerNumber;
            }
            return arr;
        }

    public void physics(Sprite s, Line2D current)
    {

        if(s.getRectangle().intersectsLine(current))//player.getRectangle().intersectsLine((Line2D) current))
        {
            s.setGrounded(true);
            //player.setVelocityY(3);
            while(s.getRectangle().intersectsLine(current))
                s.setRectangle(new Rectangle((int) s.getX(), (int) (s.getY() - 1), 40, 40));
            s.setRectangle(new Rectangle((int) s.getX(), (int) (s.getY() - 1), 40, 40));
        }
    }

    public void jump(Sprite s)
    {
        if(s != null)
        {
            if(s.getJumpTimer() > 0)
            {
                s.setJumpTimer(s.getJumpTimer() - 1);
                s.setVelocityY(s.getJumpPower());
            }
            else if(s.getVelocityY() >= 0)
                s.setVelocityY(s.getVelocityY() - 1);
            s.update();
            if(s instanceof Monster)
                ((Monster) s).nextMove(player);
        }
    }

    public void gameUpdate()
    {
        //System.out.println("X: " + player.getX() + " Y: " + player.getY());
        //System.out.println("X: " + path.get(0).getX() + " Y: " + path.get(0).getY() + "  " + path.size());
        if(player != null)
        {
            if(player.getX() < 0)
                player.setVelocityX(2);
            if(player.getX() > pWidth * scaleAnimation)
                view = 4;
            jump(player);
        }
        try
        {
            if(mobs.size() > 0)
                for(int i = 0; i < mobs.size(); i++)
                {
                    if(new Rectangle((int) mobs.get(i).getX(), (int) mobs.get(i).getY() + 20, 30, 25).intersects(player.getRectangle()))
                        view = 4;
                    jump(mobs.get(i));
                }
        } catch(Exception e)
        {
        }
        Point previous = null, next = null;
        Iterator<Point> it = path.iterator();
        while(it.hasNext())
        {//prints the line/land
            if(previous == null)
                previous = it.next();
            else
            {
                next = it.next();
                //Shape current = new Line2D.Double(previous.getX()- player.getX() + pWidth/2, previous.getY() - player.getY() + pHeight/2, next.getX() - player.getX() + pWidth/2, next.getY() - player.getY() + pHeight/2);
                //g2.draw(current);
                //Shape current = new Line2D.Double(previous.getX()- player.getX() + pWidth/2, previous.getY() - player.getY() + pHeight/2, next.getX() - player.getX() + pWidth/2, next.getY() - player.getY() + pHeight/2);
                if(player != null)
                    physics(player, new Line2D.Double(previous.getX(), previous.getY(), next.getX(), next.getY()));//new Line2D.Double(previous.getX(), previous.getY(), next.getX(), next.getY()));
                if(mobs.size() > 0)
                    for(int i = 0; i < mobs.size(); i++)
                        physics(mobs.get(i), new Line2D.Double(previous.getX(), previous.getY(), next.getX(), next.getY()));
                previous = next;
            }
            if(player != null)
            {
                if(left)
                    player.setVelocityX(-2);
                else if(right)
                    player.setVelocityX(2);
                else if(player.getVelocityX() > 0)
                    player.setVelocityX(player.getVelocityX() - 1);
                else if(player.getVelocityX() < 0)
                    player.setVelocityX(player.getVelocityX() + 1);
                if(up && player.isGrounded())
                {
                    player.setGrounded(false);
                    player.setJumpTimer(15);
                    player.setVelocityY(player.getJumpPower());
                }
            }
        }
    }

    /**
     * Main thread that is started at the start of the program and calls everything to make the game smooth
     */
    public void run()
    {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;
        while(true)
        {
            gameUpdate();
            repaint();
            beforeTime = System.nanoTime();
            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;
            if(sleepTime > 0)
            {   // some time left in this cycle
                try
                {
                    Thread.sleep(sleepTime / 1000000L);  // nano -> ms
                } catch(InterruptedException ex)
                {
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            }
            else
            {    // sleepTime <= 0; the frame took longer than the period
                excess -= sleepTime;  // store excess time value
                overSleepTime = 0L;
                if(++noDelays >= NO_DELAYS_PER_YIELD)
                {
                    Thread.yield();   // give another thread a chance to run
                    noDelays = 0;
                }
            }
            beforeTime = System.nanoTime();
            int skips = 0;
            while((excess > period) && (skips < MAX_FRAME_SKIPS))
            {
                excess -= period;
                gameUpdate();    // update state but don't render
                skips++;
            }
        }
    }

    /**
     * for the keylisteners to get key events
     *
     * @param e info about the whereabouts of the mouse
     */
    public void mouseEntered(MouseEvent e)
    {
        requestFocusInWindow();
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
        Point m = new Point(e.getX(), e.getY());
        if(view == 0 && startButton.contains(m) && path.size() > 0)
        {
            //System.out.print(pWidth * scaleAnimation);
            view = 1;
            for(int i = 0; i < random.nextInt(3) + 3; i++)
                mobs.add(new Monster(new Rectangle(random.nextInt((int) (pWidth * (double) pHeight / (double) (dataMax - dataMin)) - 500) + 500, -100, 30, 25), difficulty));
            path.add(0, new Point(0, (int) path.get(0).getY()));
            path.add(new Point((int) (pWidth * scaleAnimation), (int) path.get(path.size() - 1).getY()));
            if(player == null)
            {
                player = new Player(new Rectangle((int) 0, (int) ((m.getY() - (((double) dataMin) / ((double) pHeight / ((double) dataMax - (double) dataMin))) * scaleAnimation) * scaleAnimation), 40, 40));
                //player.setRectangle(new Rectangle((int)(player.getRectangle().getX() * scaleAnimation), (int) ((player.getRectangle().getY() - 20) * scaleAnimation), 10, 20));
                //System.out.println("playerx: "+(int) (m.getX() * scaleAnimation) + "playery: "+(int) ((m.getY() - yy) * scaleAnimation));
                //System.out.println("playerx: "+e.getX() * scaleAnimation  + "playery: "+(e.getY() - yy) * scaleAnimation);
            }
        }
        else if(view == 0)
        {
            path.add(m);
        }
        else if(view == 4 && playAgain.contains(m))
        {
            view = 0;
            sorted = false;
            path = new ArrayList<Point>();
            data = new ArrayList<Point>();
            mobs = new ArrayList<Monster>();
            player = null;
            scaled = false;
            //score = 0;
            scaleAnimation = 1;
            getData();
        }
        else if(view == 3 && play.contains(m))
            view = 0;
        else if(view == 3 && hard.contains(m))
            difficulty = 2;
        else if(view == 3 && medium.contains(m))
            difficulty = 1;
        else if(view == 3 && easy.contains(m))
            difficulty = 0;
        else if(view == 3 && instructions.contains(m))
            view = 5;
        else if(view == 5 && back.contains(m))
            view = 3;
    }

    public void mouseDragged(MouseEvent e)
    {
    }

    /**
     * changes the cursor to the hand when you can click on a button
     *
     * @param e info about the whereabouts of the mouse
     */
    public void mouseMoved(MouseEvent e)
    {
        mouse.setLocation(e.getX(), e.getY());
        if(startButton.contains(mouse) && view == 0)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(hard.contains(mouse) && view == 3)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(medium.contains(mouse) && view == 3)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(easy.contains(mouse) && view == 3)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(play.contains(mouse) && view == 3)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(playAgain.contains(mouse) && view == 4)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(instructions.contains(mouse) && view == 3)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(back.contains(mouse) && view == 5)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(!(getCursor().equals(Cursor.CROSSHAIR_CURSOR)))
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * used for main menu to check when they click buttons
     *
     * @param e contains mouse position
     */
    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void keyTyped(KeyEvent e)
    {
    }

    /**
     * when you release the arrow keys it resets the vaiables
     *
     * @param e contains which key you released
     */
    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_D && right || e.getKeyCode() == KeyEvent.VK_RIGHT && right)
            right = false;
        if(e.getKeyCode() == KeyEvent.VK_A && left || e.getKeyCode() == KeyEvent.VK_LEFT && left)
            left = false;
        if(e.getKeyCode() == KeyEvent.VK_W && up || e.getKeyCode() == KeyEvent.VK_UP && up)
            up = false;
        if(e.getKeyCode() == KeyEvent.VK_S && down || e.getKeyCode() == KeyEvent.VK_DOWN && down)
            down = false;
    }

    /**
     * Used to move ship when you press keys
     *
     * @param e contains which key you pressed
     */
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_D && !right || e.getKeyCode() == KeyEvent.VK_RIGHT && !right)
            right = true;
        if(e.getKeyCode() == KeyEvent.VK_A && !left || e.getKeyCode() == KeyEvent.VK_LEFT && !left)
            left = true;
        if(e.getKeyCode() == KeyEvent.VK_W && !up || e.getKeyCode() == KeyEvent.VK_UP && !up)
            up = true;
        if(e.getKeyCode() == KeyEvent.VK_S && !down || e.getKeyCode() == KeyEvent.VK_DOWN && !down)
            down = true;
    }

    /**
     * method called at the very start of the program to start main thread
     */
    public void start()
    {
        th = new Thread(this);
        th.start();
    }

    /**
     * Used to stop main thread
     */
    public void stop()
    {
        //th.stop();
        th = null;
    }

    /**
     * Other methods for window events
     */
    public void windowClosing(WindowEvent e)
    {
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }
}