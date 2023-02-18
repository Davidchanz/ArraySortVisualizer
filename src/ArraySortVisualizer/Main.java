package ArraySortVisualizer;

import com.FXChemiEngine.draw.BufferAWTImageDrawingObject;
import com.FXChemiEngine.engine.Scene;
import com.FXChemiEngine.engine.ShapeObject;
import com.FXChemiEngine.engine.shape.Rectangle;
import com.FXChemiEngine.math.UnityMath.Vector2;
import com.FXChemiEngine.util.Color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Random;
public class Main {
    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    public static boolean flag_sel = false;
    public static boolean flag_bub = false;
    public static boolean flag_ins = false;
    public static boolean flag_sha = false;
    public static boolean flag_sort = false;
    public static Scene scene;
    public static int maxH;
    public static int size;
    public static int itemSize;
    public static int interval;
    public static Color color;
    public static int startX;
    public static int startY;
    public static JFrame frame;
    public static int[] sizeBuff;
    private JPanel panel;
    private static Array<SortItem<Rectangle>> array;
    private static ShapeObject visual;
    private BufferedImage image;
    Main(){
        System.out.println("Hello world!");
        visual = new ShapeObject("array", 1);
        BufferAWTImageDrawingObject drawingObject = new BufferAWTImageDrawingObject();
        scene = new Scene(WIDTH, HEIGHT, Color.TRANSPARENT, drawingObject);
        scene.add(visual);
        //scene.setBorder(20, Color.CYAN);
        //scene.setCoordVisible(true);
        //scene.setCenterVisible(true);
        //scene.setBorderVisible(true);
        frame = new JFrame("Array sort Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH+100, HEIGHT+100));
        frame.setLayout(new BorderLayout());
        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        frame.add(panel, BorderLayout.CENTER);
        JButton buble = new JButton("Buble");
        JButton insert = new JButton("Insert");
        JButton select = new JButton("Select");
        JButton shafl = new JButton("Shafl");
        buble.setPreferredSize(new Dimension(70, 20));
        select.setPreferredSize(new Dimension(70, 20));
        insert.setPreferredSize(new Dimension(70, 20));

        shafl.setPreferredSize(new Dimension(70, 20));

        buble.addActionListener(actionEvent -> {if(flag_sort) return;flag_bub = true; flag_sort = true;});
        insert.addActionListener(actionEvent -> {if(flag_sort) return;flag_ins = true; flag_sort = true;});
        select.addActionListener(actionEvent -> {if(flag_sort) return;flag_sel = true; flag_sort = true;});

        shafl.addActionListener(actionEvent -> {if(flag_sort) return;flag_sha = true; flag_sort = true;});

        /*buble.addActionListener(actionEvent -> {
            bubleSort();
            flag_sel = false;
            flag_sort = false;
        });
        insert.addActionListener(actionEvent -> {
            selectionSort();
            flag_bub = false;
            flag_sort = false;});
        shafl.addActionListener(actionEvent -> {
            shafle();
            flag_sha = false;
            flag_sort = false;});*/
        array = new Array<>(0);
        array.setSwapConsumer((i, j) -> {
            array.get(i).getBody().color = Color.GREEN;
            array.get(j).getBody().color = Color.RED;
            SortItem.swap(array.get(i), array.get(j));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            array.get(i).getBody().color = color;
            array.get(j).getBody().color = color;
        });

        array.setAssignConsumer((i, j) -> {
            i.getBody().color = Color.GREEN;
            j.getBody().color = Color.RED;
            SortItem.assign(i, j);
            /*try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            i.getBody().color = color;
            j.getBody().color = color;
        });

        array.setSaveConsumer((x) -> {
            return SortItem.save(x);
        });

        Box vBox = Box.createVerticalBox();
        vBox.add(buble);
        vBox.add(insert);
        vBox.add(select);
        vBox.add(shafl);

        frame.add(vBox, BorderLayout.WEST);
        /*frame.add(insert, BorderLayout.EAST);
        frame.add(shafl, BorderLayout.SOUTH);*/
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
               scene.resize(frame.getWidth()-100, frame.getHeight()-100);
                //Scene.objects.clear();
                shafle(false);
            }
        });

        Timer t = new Timer(15, this::draw);

        frame.setVisible(true);
        shafle(true);
        //scene.repaint();
        t.start();
        /*frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });*/
    }

    private void draw(ActionEvent actionEvent) {
        //System.out.println("start");
        scene.repaint();
        image = scene.getImage();
        panel.repaint();
        //System.out.println("stop");
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });*/
        new Main();
        while (true){
            if(flag_sel){
                selectionSort();
                flag_sel = false;
            }else if(flag_bub){
                bubleSort();
                flag_bub = false;
            }else if(flag_sha){
                shafle(true);
                flag_sha = false;
            }else if(flag_ins){
                insertSort();
                flag_ins = false;
            }
            flag_sort = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void shafle(boolean rand){
        //Scene.objects.clear();//TODO
        visual.clear();
        size = 200;
        array.resize(size);
        if (rand) {
            sizeBuff = new int[size];
        }

        interval = 0;
        itemSize = (Main.frame.getWidth()-150)/((size)*(interval+1));
        maxH = (Main.frame.getHeight()-100)/4;
        startX = -(((size-1)*(itemSize*(interval+1))))/2;
        startY = -maxH;
        color = Color.MAGENTA;

        Random r = new Random();
        int oldMax = Arrays.stream(sizeBuff).max().getAsInt();
        int dif;
        if (oldMax > maxH) {
            dif = Math.abs(Arrays.stream(sizeBuff).max().getAsInt() - maxH);
        } else {
            dif = 0;
        }
        for(int i = 0; i < size; ++i){
            int value;
            if (rand) {
                value = r.nextInt(maxH - 5) + 5;
            } else {
                value = sizeBuff[i] - dif;
                if (value < 0) {
                    value = 0;
                }
            }

            SortItem<Rectangle> item = new SortItem<>(new Rectangle(value, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY + value), color), value);
            array.add(item);
            visual.add(item.getBody());

            //ShapeObject item = new ShapeObject("Grapsh", 1);

            //item.add(new Rectangle(   value, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY + value), color));

            //if(i%2 == 0)      item.add(new Triangle(    value, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY + value), color));
            /*else if(i%3 == 0) item.add(new Circle(      value, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY + value), color));
            else              item.add(new Rectangle(   value, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY + value), color));

            if(i%2 == 0)      item.add(new Rectangle(   itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY+(value*2)), Color.BLUE));
            else if(i%3 == 0) item.add(new Triangle(    itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY+(value*2)), Color.BLUE));
            else              item.add(new Circle(      itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY+(value*2)), Color.BLUE));
            if(i%2 == 0)      item.add(new Circle(      itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), itemSize + (startY+(value*2))), Color.PINK));
            else if(i%3 == 0) item.add(new Rectangle(   itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), itemSize + (startY+(value*2))), Color.PINK));
            else              item.add(new Triangle(    itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), itemSize + (startY+(value*2))), Color.PINK));
            if(i%2 == 0)      item.add(new Triangle(    itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))),  2*itemSize + (startY+(value*2))), Color.ORANGE));
            else if(i%3 == 0) item.add(new Circle(      itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))),  2*itemSize + (startY+(value*2))), Color.ORANGE));
            else              item.add(new Rectangle(   itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))),  2*itemSize + (startY+(value*2))), Color.ORANGE));*/

            //item.add(new Line(Line.TYPE.VERTICAL, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))),  3*itemSize + (startY+(value*2))), Color.BLACK));
            //scene.add(item);
            //if(rand) sizeBuff[i] = item.body.get(0).height;
            if (rand) {
                sizeBuff[i] = item.getValue();
            }
        }

    }

    /**Selection Sort*/
    public static void selectionSort() {
        array.selectSort();
    }

    /**Buble Sort*/
    public static void bubleSort(){
        array.bubbleSort();
    }

    public static void insertSort(){
        array.insertSort();

        System.out.println(array);
        Clock clock = Clock.tickMillis(ZoneId.systemDefault());
        System.out.println(clock.millis());//todo

        System.out.println(clock.millis());//todo
    }
}

