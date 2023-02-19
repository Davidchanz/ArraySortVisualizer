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
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    public static final AtomicBoolean flag_sort = new AtomicBoolean(false);
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
    private final JPanel panel;
    private static Array<SortItem<Rectangle>> array;
    private static ShapeObject visual;
    private BufferedImage image;
    private long delay = 10L;
    private Timer t;

    private StopWatchPane stopWatch;
    Main(){
        visual = new ShapeObject("array", 1);
        BufferAWTImageDrawingObject drawingObject = new BufferAWTImageDrawingObject();
        scene = new Scene(WIDTH, HEIGHT, Color.DARKGRAY, drawingObject);
        scene.add(visual);

        /*scene.setCoordVisible(true);
        scene.setCenterVisible(true);
        scene.setBorderVisible(true);*/

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
        stopWatch = new StopWatchPane();
        frame.add(panel, BorderLayout.CENTER);
        frame.add(stopWatch, BorderLayout.SOUTH);
        JButton buble = new JButton("Buble");
        JButton insert = new JButton("Insert");
        JButton select = new JButton("Select");
        JButton shafl = new JButton("Shafl");
        buble.setPreferredSize(new Dimension(70, 20));
        select.setPreferredSize(new Dimension(70, 20));
        insert.setPreferredSize(new Dimension(70, 20));
        shafl.setPreferredSize(new Dimension(70, 20));

        buble.addActionListener(actionEvent -> {
            new Thread(() -> sort(0)).start();
        });
        select.addActionListener(actionEvent -> {
            new Thread(() -> sort(1)).start();
        });
        insert.addActionListener(actionEvent -> {
            new Thread(() -> sort(2)).start();
        });
        shafl.addActionListener(actionEvent -> {
            shafle(true);
        });

        size = 200;
        array = new Array<>(size);
        array.setSwapConsumer((i, j) -> {
            array.get(i).getBody().color = Color.GREEN;
            array.get(j).getBody().color = Color.RED;
            SortItem.swap(array.get(i), array.get(j));
            sleep();
            array.get(i).getBody().color = color;
            array.get(j).getBody().color = color;
        });

        array.setAssignConsumer((i, j) -> {
            i.getBody().color = Color.GREEN;
            j.getBody().color = Color.RED;
            SortItem.assign(i, j);
            sleep();
            i.getBody().color = color;
            j.getBody().color = color;
        });

        array.setSaveConsumer((x) -> SortItem.save(x));

        Box vBox = Box.createVerticalBox();
        vBox.add(buble);
        vBox.add(insert);
        vBox.add(select);
        vBox.add(shafl);

        frame.add(vBox, BorderLayout.WEST);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if(!flag_sort.get()) {
                    scene.resize(frame.getWidth() - 100, frame.getHeight() - 100);
                    shafle(false);
                }
            }
        });

        t = new Timer(15, this::draw);

        frame.setVisible(true);
        shafle(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main m = new Main();
                m.t.start();
            }
        });
    }

    /**Sleep for algorithm visible*/
    private void sleep(){
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**Draw frame*/
    private void draw(ActionEvent actionEvent) {
        scene.repaint();
        image = scene.getImage();
        panel.repaint();
    }

    /**Shafl random array*/
    public void shafle(boolean rand){
        if (rand) {
            visual.clear();
            array.resize(size);
            sizeBuff = new int[size];
        }

        interval = 0;
        itemSize = (Main.frame.getWidth()-150)/((size)*(interval+1));
        maxH = (Main.frame.getHeight()-100)/4;
        startX = -(((size-1)*(itemSize*(interval+1))))/2;
        startY = -maxH;
        color = Color.WHITE;

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
            if(rand) {
                SortItem<Rectangle> item = new SortItem<>(new Rectangle(value, itemSize / 2, new Vector2(startX + (i * (itemSize * (interval + 1))), startY + value), color), value);
                array.set(i, item);
                visual.add(item.getBody());
                sizeBuff[i] = item.getValue();
            }
            else {
                SortItem<Rectangle> item = new SortItem<>(new Rectangle(array.get(i).getValue(), itemSize / 2, new Vector2(startX + (i * (itemSize * (interval + 1))), startY + value), color), value);
                array.set(i, item);
                visual.set(i, item.getBody());
            }
        }

    }

    public void sort(int type){
        if(!flag_sort.get()) {
            flag_sort.set(true);
            stopWatch.start();
            switch (type) {
                case 0 -> array.bubbleSort();
                case 1 -> array.selectSort();
                case 2 -> array.insertSort();
            }
            sizeBuff = Arrays.stream(sizeBuff).sorted().toArray();
            stopWatch.stop();
            flag_sort.set(false);
        }
    }

    private static class StopWatchPane extends JPanel {

        private JLabel label;
        private long lastTickTime;
        private Timer timer;

        public StopWatchPane() {

        setLayout(new GridBagLayout());
        label =new

        JLabel(String.format("%04d:%02d:%02d.%03d", 0,0,0,0));

        timer =new

        Timer(100,new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){
                long runningTime = System.currentTimeMillis() - lastTickTime;
                Duration duration = Duration.ofMillis(runningTime);
                long hours = duration.toHours();
                duration = duration.minusHours(hours);
                long minutes = duration.toMinutes();
                duration = duration.minusMinutes(minutes);
                long millis = duration.toMillis();
                long seconds = millis / 1000;
                millis -= (seconds * 1000);
                label.setText(String.format("%04d:%02d:%02d.%03d", hours, minutes, seconds, millis));
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx =0;
        gbc.gridy =0;
        gbc.weightx =1;
        gbc.gridwidth =GridBagConstraints.REMAINDER;
        gbc.insets =new

        Insets(4,4,4,4);

        add(label, gbc);

        /*JButton start = new JButton("Start");
        start.addActionListener(new

        ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){
                if (!timer.isRunning()) {
                    lastTickTime = System.currentTimeMillis();
                    timer.start();
                }
            }
        });
        JButton stop = new JButton("Stop");
        stop.addActionListener(new

        ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){
                timer.stop();
            }
        });

        gbc.gridx =0;
        gbc.gridy++;
        gbc.weightx =0;
        gbc.gridwidth =1;

        add(start, gbc);

        gbc.gridx++;

        add(stop, gbc);*/
    }
    public void start(){
        if (!timer.isRunning()) {
            lastTickTime = System.currentTimeMillis();
            timer.start();
        }
    }

    public void stop(){
            timer.stop();
    }
    }
}

