package gui;

import observer.Observed;
import utils.Point2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
// import java.awt.event.MouseEvent;
// import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ImageMatrixGUI extends Observed {

    private static ImageMatrixGUI INSTANCE;

    private final String IMAGE_DIR = "./images";
    private final int LABEL_HEIGHT = 20;
    private final int SQUARE_SIZE;

    private int n_squares_width = 5;
    private int n_squares_height = 5;

    private JFrame frame;
    private JPanel panel;
    private JLabel info;

    private Map<String, ImageIcon> imageDB = new HashMap<String, ImageIcon>();

    private List<ImageTile> images = new ArrayList<ImageTile>();

    // private Point2D lastMouseCoordinate;
    private boolean mouseClicked;

    private int lastKeyPressed;
    private boolean keyPressed;

    private int maxLevel;

    private ImageMatrixGUI() {
        SQUARE_SIZE = 50;
        init();
    }

    public static ImageMatrixGUI getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ImageMatrixGUI();
        return INSTANCE;
    }

    public void setName(final String name) {
        frame.setTitle(name); // Corrected 2-Mar-2016
    }

    private void init() {
        frame = new JFrame();
        panel = new DisplayWindow();
        info = new JLabel();

        panel.setPreferredSize(new Dimension(n_squares_width * SQUARE_SIZE, n_squares_height * SQUARE_SIZE));
        info.setPreferredSize(new Dimension(n_squares_width * SQUARE_SIZE, LABEL_HEIGHT));
//		panel.setPreferredSize(new Dimension(N_SQUARES_WIDTH * SQUARE_SIZE, N_SQUARES_HEIGHT * SQUARE_SIZE));
//		info.setPreferredSize(new Dimension(N_SQUARES_WIDTH * SQUARE_SIZE, LABEL_HEIGHT));
        info.setBackground(Color.BLACK);
        frame.add(panel);
        frame.add(info, BorderLayout.NORTH);
        frame.pack();
        frame.setResizable(false); // Added 27-Feb-2018
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //sets the IconImage on windows, doesn't work on macOS
        ImageIcon icon = new ImageIcon("icon/Game_Icon.png");
        frame.setIconImage(icon.getImage());

        initImages();

        new KeyWatcher().start();

//		new MouseWatcher().start();

//		new Ticker().start();

        frame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                lastKeyPressed = e.getKeyCode();
                keyPressed = true;
                releaseObserver();
            }
        });
    }

    synchronized void releaseObserver() {
        notifyAll();
    }

    synchronized void waitForKey() throws InterruptedException {
        while (!keyPressed) {
            wait();
        }
        notifyObservers();
        keyPressed = false;
    }

    synchronized void waitForClick() throws InterruptedException {
        while (!mouseClicked) {
            wait();
        }
        notifyObservers();
        mouseClicked = false;
    }

    void tick() throws InterruptedException {
        notifyObservers();
    }

    private void initImages() {
        File dir = new File(IMAGE_DIR);
        for (File f : dir.listFiles()) {
            assert (f.getName().lastIndexOf('.') != -1);
            imageDB.put(f.getName().substring(0, f.getName().lastIndexOf('.')),
                    new ImageIcon(IMAGE_DIR + "/" + f.getName()));
        }
    }

    public void go() {
        frame.setVisible(true);
    }

    public void addImages(final List<ImageTile> newImages) {
        synchronized (images) { // Added 16-Mar-2016
            if (newImages == null)
                throw new IllegalArgumentException("Null list");
            if (newImages.size() == 0)
                return;
            for (ImageTile i : newImages) {
                if (i == null)
                    throw new IllegalArgumentException("Null image");
                if (!imageDB.containsKey(i.getName())) {
                    throw new IllegalArgumentException("No such image in DB " + i.getName());
                }
                addImage(i);
            }
        }
    }

    public void removeImage(final ImageTile image) {
        if (image == null)
            throw new IllegalArgumentException("Null list");
        synchronized (images) { // Added 16-Mar-2016
            images.remove(image);
        }
    }

    // Added 2-Out-2017


    public void removeImages(final List<ImageTile> newImages) {
        if (newImages == null)
            throw new IllegalArgumentException("Null list");
        synchronized (images) {
            images.removeAll(newImages);
        }
    }

    public void addImage(final ImageTile image) {
        synchronized (images) { // Added 16-Mar-2016
            if (image == null)
                throw new IllegalArgumentException("Null image");
            if (image.getName() == null)
                throw new IllegalArgumentException("Null image name");
            if (image.getPosition() == null)
                throw new IllegalArgumentException("Null image position");
            if (image.getLayer() >= maxLevel)
                maxLevel = image.getLayer() + 1;
            if (!imageDB.containsKey(image.getName())) {
                throw new IllegalArgumentException("No such image in DB " + image.getName());
            }
            images.add(image);
        }
    }

    public void clearImages() {
        synchronized (images) { // Added 16-Mar-2016
            images.clear();
        }
    }

    public void setStatusMessage(String message) {
        info.setHorizontalAlignment(SwingConstants.LEFT);
        info.setVerticalAlignment(SwingConstants.CENTER);
        info.setText(message);
    }

    public void setMessage(String Message) {

        JOptionPane.showMessageDialog(panel, Message);
    }

    private class DisplayWindow extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            // System.out.println("Thread " + Thread.currentThread() + "
            // repainting");
            synchronized (images) { // Added 16-Mar-2016
                for (int j = 0; j != maxLevel; j++)
                    for (ImageTile i : images) {
                        if (i.getLayer() == j) {
                            g.drawImage(imageDB.get(i.getName()).getImage(), i.getPosition().getX() * SQUARE_SIZE,
                                    i.getPosition().getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, frame);
                        }
                    }
            }
        }
    }

    private class KeyWatcher extends Thread {
        public void run() {
            try {
                while (true)
                    waitForKey();
            } catch (InterruptedException e) {
            }
        }
    }

//	private class MouseWatcher extends Thread {
//		public void run() {
//			try {
//				while (true)
//					waitForClick();
//			} catch (InterruptedException e) {
//			}
//		}
//	}
//
//	private class Ticker extends Thread {
//		private static final long TICK_TIME = 1000;
//
//		public void run() {
//			try {
//				while (true) {
//					sleep(TICK_TIME);
//					tick();
//				}
//			} catch (InterruptedException e) {
//			}
//		}
//	}


    public void update() {

        frame.repaint();
    }

    public void dispose() {
        images.clear();
        imageDB.clear();
        frame.dispose();
    }

    public Dimension getGridDimension() {
//		return new Dimension(N_SQUARES_WIDTH, N_SQUARES_HEIGHT);
        return new Dimension(n_squares_width, n_squares_height);
    }

    public void setSize(int i, int j) {
        n_squares_width = i;
        n_squares_height = j;
        if (INSTANCE != null) {
            //This is a workaround to allow dynamic resizing
            INSTANCE.panel.setPreferredSize(new Dimension(n_squares_width * INSTANCE.SQUARE_SIZE, n_squares_height * INSTANCE.SQUARE_SIZE));
            INSTANCE.info.setPreferredSize(new Dimension(n_squares_width * INSTANCE.SQUARE_SIZE, INSTANCE.LABEL_HEIGHT));
            INSTANCE.frame.setSize(INSTANCE.frame.getPreferredSize());
        }
    }

    public boolean isWithinBounds(Point2D p) {
        return p.getX() >= 0 && p.getY() >= 0 && p.getX() < n_squares_width && p.getY() < n_squares_height;
    }

    public synchronized int keyPressed() {
        return lastKeyPressed;
    }

}
