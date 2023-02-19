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
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static ArraySortVisualizer.Constants.*;

public class Main {
    public final AtomicBoolean flag_sort = new AtomicBoolean(false);
    public Scene scene;
    public Color itemColor = Color.ALICEBLUE;
    public JFrame frame;
    public int[] sizeBuff;
    private JPanel scenePane;
    private final Array<SortItem<Rectangle>> array;
    private final ShapeObject visual;
    private BufferedImage image;
    private final Timer timer;
    private JPanel settingPane;
    private StopWatchPane stopWatch;
    public Main(){
        visual = new ShapeObject("array", 1);
        BufferAWTImageDrawingObject drawingObject = new BufferAWTImageDrawingObject();
        scene = new Scene(WIDTH, HEIGHT, Color.DARKGRAY, drawingObject);
        scene.add(visual);

        array = new Array<>(SIZE);
        array.setSwapConsumer((i, j) -> {
            array.get(i).getBody().color = Color.GREEN;
            array.get(j).getBody().color = Color.RED;
            SortItem.swap(array.get(i), array.get(j));
            sleep();
            array.get(i).getBody().color = itemColor;
            array.get(j).getBody().color = itemColor;
        });

        array.setAssignConsumer((i, j) -> {
            i.getBody().color = Color.GREEN;
            j.getBody().color = Color.RED;
            SortItem.assign(i, j);
            sleep();
            i.getBody().color = itemColor;
            j.getBody().color = itemColor;
        });

        array.setSaveConsumer((x) -> SortItem.save(x));

        timer = new Timer(15, this::draw);
        setGUI();
        frame.setVisible(true);
        shafle(true);
    }

    private void setGUI(){
        frame = new JFrame("Array sort Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.setLayout(new BorderLayout());
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if(!flag_sort.get()) {
                    scene.resize(scenePane.getWidth(), scenePane.getHeight());
                    shafle(false);
                }
            }
        });
        scenePane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        stopWatch = new StopWatchPane();

        JComboBox<String> sortComboBox = new JComboBox<>();
        sortComboBox.addItem("Bubble");
        sortComboBox.addItem("Select");
        sortComboBox.addItem("Insert");

        JButton shafleButton = new JButton("Shafle");
        shafleButton.addActionListener(actionEvent -> {if(!flag_sort.get())shafle(true);});

        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(actionEvent -> new Thread(() -> sort(sortComboBox.getSelectedIndex())).start());

        JPanel sortingButtonPane = new JPanel();
        sortingButtonPane.setLayout(new GridBagLayout());

        GridBagConstraints sortingButtonsGridBox = new GridBagConstraints();
        sortingButtonsGridBox.gridx =0;
        sortingButtonsGridBox.gridy =0;
        sortingButtonsGridBox.weightx =1;
        sortingButtonsGridBox.gridwidth =GridBagConstraints.REMAINDER;
        sortingButtonsGridBox.anchor = GridBagConstraints.NORTH;
        sortingButtonsGridBox.insets =new Insets(4,4,4,4);

        sortingButtonPane.add(sortComboBox, sortingButtonsGridBox);
        sortingButtonsGridBox.gridy++;
        sortingButtonPane.add(sortButton, sortingButtonsGridBox);
        sortingButtonsGridBox.gridy++;
        sortingButtonPane.add(shafleButton, sortingButtonsGridBox);

        settingPane = new JPanel();
        settingPane.setLayout(new GridBagLayout());

        GridBagConstraints settingGridBox = new GridBagConstraints();
        settingGridBox.gridx =0;
        settingGridBox.gridy =0;
        settingGridBox.weightx =1;
        settingGridBox.gridwidth =GridBagConstraints.REMAINDER;
        settingGridBox.anchor = GridBagConstraints.NORTH;
        settingGridBox.insets =new Insets(4,4,4,4);

        JLabel sizeLabel = new JLabel("Array size:");
        JSpinner sizeSpiner = new JSpinner();
        var model = new SpinnerNumberModel();
        model.setMinimum(10);
        model.setMaximum(1000);
        model.setValue(SIZE);
        model.addChangeListener(changeEvent -> {
            SIZE = (int)model.getValue();
            shafle(true);
        });
        sizeSpiner.setModel(model);
        settingPane.add(sizeLabel, settingGridBox);
        settingGridBox.gridy++;
        settingPane.add(sizeSpiner, settingGridBox);

        JLabel colorLabel = new JLabel("Array color:");
        JButton colorChooser = new JButton("Color");
        colorChooser.addActionListener(actionEvent -> {
            var color = JColorChooser.showDialog(
                    settingPane,
                    "Choose SortItem Color", java.awt.Color.WHITE);
            itemColor = new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
            colorChooser.setBackground(color);
            shafle(false);
        });
        settingGridBox.gridy++;
        settingPane.add(colorLabel, settingGridBox);
        settingGridBox.gridy++;
        settingPane.add(colorChooser, settingGridBox);

        frame.add(scenePane, BorderLayout.CENTER);
        frame.add(stopWatch, BorderLayout.SOUTH);
        frame.add(sortingButtonPane, BorderLayout.WEST);
        frame.add(settingPane, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main m = new Main();
                m.startTimer();
            }
        });
    }

    public void startTimer(){
        timer.start();
    }

    /**Sleep for algorithm visible*/
    private void sleep(){
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**Draw frame*/
    private void draw(ActionEvent actionEvent) {
        scene.repaint();
        image = scene.getImage();
        scenePane.repaint();
    }

    /**Shafl random array*/
    public void shafle(boolean rand){
        if (rand) {
            visual.clear();
            array.resize(SIZE);
            sizeBuff = new int[SIZE];
        }

        float interval = 3f;
        float itemSize = (((scenePane.getWidth()-(SIZE+1)*interval)/(SIZE*2f)));
        int maxH = (scenePane.getHeight())/3;
        float startX = -scenePane.getWidth()/2f + itemSize + interval;
        int startY = -maxH;

        Random r = new Random();
        int oldMax = Arrays.stream(sizeBuff).max().getAsInt();
        int dif;
        if (oldMax > maxH) {
            dif = Math.abs(Arrays.stream(sizeBuff).max().getAsInt() - maxH);
        } else {
            dif = 0;
        }
        for(int i = 0; i < SIZE; ++i){
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
                SortItem<Rectangle> item = new SortItem<>(new Rectangle(value, (int)itemSize, new Vector2(startX + (i * (itemSize*2 + interval)), startY + value), itemColor), value);
                array.set(i, item);
                visual.add(item.getBody());
                sizeBuff[i] = item.getValue();
            }
            else {
                SortItem<Rectangle> item = new SortItem<>(new Rectangle(array.get(i).getValue(), (int)itemSize, new Vector2(startX + (i * (itemSize*2 + interval)), startY + value), itemColor), value);
                array.set(i, item);
                visual.set(i, item.getBody());
            }
        }

    }

    private void setSettingPaneEnable(boolean b){
        Arrays.stream(settingPane.getComponents()).toList().forEach(component -> component.setEnabled(b));
    }

    /**Sorting*/
    public void sort(int type){
        if(!flag_sort.get()) {
            flag_sort.set(true);
            setSettingPaneEnable(false);
            stopWatch.start();
            switch (type) {
                case 0 -> array.bubbleSort();
                case 1 -> array.selectSort();
                case 2 -> array.insertSort();
            }
            sizeBuff = Arrays.stream(sizeBuff).sorted().toArray();
            stopWatch.stop();
            setSettingPaneEnable(true);
            flag_sort.set(false);
        }
    }
}

