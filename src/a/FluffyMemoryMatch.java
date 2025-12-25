package a;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.*;
import javax.swing.Timer;

public class FluffyMemoryMatch extends JFrame {

    public static int WIDTH;
    public static int HEIGHT;
    public static double SCALE;

    private static final int MAX_CARDS = 20;

    private JPanel currentPanel;
    private Image backgroundMenu;
    private Image backgroundGame;
    private Image icon;

    public FluffyMemoryMatch() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        HEIGHT = (int) (screen.height * 0.99);
        WIDTH = (int) (HEIGHT * 0.92);
        SCALE = HEIGHT / 1000.0;

        setTitle("Fluffy!");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        setIconImages(AssetsHelper.loadAppIcons());

        backgroundMenu = loadImage("/img/background1.png");
        backgroundGame = loadImage("/img/background2.png");
        icon = loadImage("/img/icon.png");

        AssetsHelper.loadAllImages();

        showMenu();
        setVisible(true);
    }

    private Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            return null;
        }
    }

    // ================= MENU SCREEN =================
    private void showMenu() {
        currentPanel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundMenu != null) {
                    g.drawImage(backgroundMenu, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(Box.createVerticalStrut(90));
        center.add(createIconCircle());
        center.add(Box.createVerticalStrut(25));
        center.add(createTitle());
        center.add(Box.createVerticalStrut(40));

        JButton selectLevel = createFluffyButton("Select Level", false);
        JButton start = createFluffyButton("Start Game", true);
        JButton exit = createFluffyButton("Exit", false);

        selectLevel.addActionListener(e -> showLevelSelection());
        start.addActionListener(e -> showGameScreen(1));
        exit.addActionListener(e -> System.exit(0));

        center.add(selectLevel);
        center.add(Box.createVerticalStrut(18));
        center.add(start);
        center.add(Box.createVerticalStrut(18));
        center.add(exit);
        
        // Added padding at the very bottom
        center.add(Box.createVerticalStrut(60)); 

        currentPanel.add(center, BorderLayout.CENTER);

        setContentPane(currentPanel);
        revalidate();
        repaint();
    }

    private JComponent createIconCircle() {
        return new JComponent() {
            protected void paintComponent(Graphics g) {
                if (icon == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Increased size from 180 to 220
                int size = 220; 
                int x = (getWidth() - size) / 2;
                int y = 0;

                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillOval(x + 6, y + 8, size, size);

                Ellipse2D circle = new Ellipse2D.Float(x, y, size, size);
                g2.setClip(circle);
                g2.drawImage(icon, x, y, size, size, this);
                g2.setClip(null);

                g2.setStroke(new BasicStroke(4f));
                g2.setColor(new Color(235, 225, 255));
                g2.draw(circle);
                g2.dispose();
            }

            public Dimension getPreferredSize() {
                // Increased preferred height to accommodate larger circle
                return new Dimension(250, 240); 
            }
        };
    } 

    private JComponent createTitle() {
        JLabel title = new JLabel("Fluffy Memory Match", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 34));
        title.setForeground(new Color(90, 80, 110));
        return title;
    }

    private JButton createFluffyButton(String text, boolean hero) {
        Color top = hero ? new Color(178, 242, 187) : new Color(245, 240, 255);
        Color bottom = hero ? new Color(150, 220, 180) : new Color(220, 235, 230);

        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(16, 60, 16, 60));
        btn.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, hero ? 20 : 18));
        btn.setForeground(new Color(80, 70, 100));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // ================= GAME SCREEN =================
    private void showGameScreen(int level) {
        currentPanel = new GameScreen(level);
        setContentPane(currentPanel);
        revalidate();
        repaint();
    }
    // ================= GAME SCREEN PANEL =================
    private class GameScreen extends JPanel {

        private int level;
        private int cardCount;
        private Card firstCard, secondCard;
        private boolean checking = false;
        private JPanel cardGrid;

        public GameScreen(int level) {
            this.level = level;
            this.cardCount = Math.min(4 + (level - 1) * 2, MAX_CARDS);

            setLayout(new BorderLayout());
            setOpaque(false);

            createTopBar();
            createCards();
            setBackground(new Color(0,0,0,0));
        }
        //kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk
        private int getCardCountForLevel(int level) {
            // Level 1 → 4 cards, Level 2 → 6, ..., Level 10 → 20
            int count = 2 * (level + 1); // level 1 → 4, level 2 → 6, ..., level 10 → 20
            return Math.min(count, 20);
        }



        @Override
        protected void paintComponent(Graphics g) {
            if (backgroundGame != null) {
                g.drawImage(backgroundGame, 0, 0, getWidth(), getHeight(), this);
            }
            
            super.paintComponent(g);
        }

        // ========== TOP BAR WITH MENU ==========
        private void createTopBar() {
            JButton menuBtn = createFluffyButton("☰", false);
            menuBtn.setPreferredSize(new Dimension(60, 50));

            menuBtn.addActionListener(e -> showPauseMenu());

            JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            top.setOpaque(false);
            top.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            top.add(menuBtn);

            add(top, BorderLayout.NORTH);
        }

     // ========== GRID CREATION ==========
        private void createCards() {
            final int cols = 5;
            final int totalCards = Math.min(getCardCountForLevel(level), 20); // cards based on level
            final int rows = (int) Math.ceil(totalCards / (double) cols);

            // ----- SELECT IMAGES -----
            int pairs = totalCards / 2;
            ArrayList<String> pool = new ArrayList<>(AssetsHelper.imgs);
            Collections.shuffle(pool);

            ArrayList<String> chosen = new ArrayList<>();
            for (int i = 0; i < pairs; i++) {
                chosen.add(pool.get(i));
                chosen.add(pool.get(i));
            }
            Collections.shuffle(chosen);

            // ----- GRID PANEL -----
            int topPadding = 10;     // less on top
            int bottomPadding = 80;  // more at bottom
            int sidePadding = 20;
            int gap = 10;

            int availableW = WIDTH - sidePadding * 2;
            int availableH = HEIGHT - topPadding - bottomPadding - 100; // 100 for top bar & extra UI

         // ----- FIXED CARD SIZE -----
            int cardW = 120; // fixed width
            int cardH = 150; // fixed height

            cardGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // use FlowLayout instead of GridLayout
            cardGrid.setOpaque(false);
            cardGrid.setBorder(BorderFactory.createEmptyBorder(topPadding, sidePadding, bottomPadding, sidePadding));

            for (String img : chosen) {
                Card c = new Card(img, AssetsHelper.BACK_IMAGE, cardW, cardH); // pass fixed size
                cardGrid.add(c);
            }


            add(cardGrid, BorderLayout.CENTER);
        }




        // ========== PAUSE MENU ==========
        private void createPauseButton() {
            JButton pauseBtn = createFluffyButton("☰", false);
            pauseBtn.setPreferredSize(new Dimension(60, 50));

            pauseBtn.addActionListener(e -> showPauseMenu());

            JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            top.setOpaque(false);
            top.add(pauseBtn);

            add(top, BorderLayout.NORTH);
        }

        private void showPauseMenu() {
            JDialog dlg = new JDialog(FluffyMemoryMatch.this, "Paused", true);
            dlg.setSize(300, 260);
            dlg.setLocationRelativeTo(this);
            dlg.setUndecorated(true);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(255,240,250));
            panel.setBorder(BorderFactory.createLineBorder(new Color(200,180,220), 3));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JButton resume = createFluffyButton("Resume", true);
            JButton restart = createFluffyButton("Restart", false);
            JButton quit = createFluffyButton("Quit", false);

            resume.addActionListener(e -> dlg.dispose());
            restart.addActionListener(e -> {
                dlg.dispose();
                showGameScreen(level);
            });
            quit.addActionListener(e -> {
                dlg.dispose();
                showMenu();
            });

            panel.add(Box.createVerticalGlue());
            panel.add(resume);
            panel.add(Box.createVerticalStrut(12));
            panel.add(restart);
            panel.add(Box.createVerticalStrut(12));
            panel.add(quit);
            panel.add(Box.createVerticalGlue());

            dlg.setContentPane(panel);
            dlg.setVisible(true);
        }

        // ========== CARD CLICK LOGIC ==========
        public void cardClicked(Card c) {
            if (checking || c.isRevealed() || c.isMatched()) return;

            c.flip(true);

            if (firstCard == null) {
                firstCard = c;
                return;
            }

            secondCard = c;
            checking = true;

            new Timer(600, e -> {
                if (!firstCard.getFront().equals(secondCard.getFront())) {
                    firstCard.flip(false);
                    secondCard.flip(false);
                } else {
                    firstCard.setMatched(true);
                    secondCard.setMatched(true);
                }

                firstCard = secondCard = null;
                checking = false;

                ((Timer) e.getSource()).stop();

                if (allMatched()) showLevelComplete();
            }).start();
        }


        private void checkMatch() {
            if (firstCard.getFront().equals(secondCard.getFront())) {
                firstCard.setMatched(true);
                secondCard.setMatched(true);
            } else {
                firstCard.flip(false);
                secondCard.flip(false);
            }

            firstCard = secondCard = null;
            checking = false;

            if (allMatched()) showLevelComplete();
        }

        private boolean allMatched() {
            for (Component c : cardGrid.getComponents()) {
                if (c instanceof Card && !((Card) c).isMatched()) return false;
            }
            return true;
        }


        private void showLevelComplete() {
            JOptionPane.showMessageDialog(
                this,
                "Level " + level + " Complete!",
                "Nice!",
                JOptionPane.INFORMATION_MESSAGE
            );
            showGameScreen(Math.min(level + 1, 10));
        }

        // ========== CARD CLASS (FLIP UNCHANGED) ==========
        private class Card extends JButton {
            private String frontPath;
            private boolean revealed = false;
            private boolean matched = false;
            private boolean flipping = false;
            private float flipProgress = 1f;

            private ImageIcon frontIcon, backIcon;
            private Image scaledFront, scaledBack;
            private int fixedW, fixedH;

            public Card(String frontPath, String backPath, int w, int h) {
                this.frontPath = frontPath;
                this.fixedW = w;
                this.fixedH = h;

                frontIcon = AssetsHelper.loadIcon(frontPath);
                backIcon = AssetsHelper.loadIcon(backPath);

                scaledFront = frontIcon.getImage().getScaledInstance(fixedW, fixedH, Image.SCALE_SMOOTH);
                scaledBack = backIcon.getImage().getScaledInstance(fixedW, fixedH, Image.SCALE_SMOOTH);

                setIcon(new ImageIcon(scaledBack));
                setPreferredSize(new Dimension(fixedW, fixedH));
                setMinimumSize(new Dimension(fixedW, fixedH));
                setMaximumSize(new Dimension(fixedW, fixedH));

                setContentAreaFilled(false);
                setFocusPainted(false);
                setOpaque(false);
                setBorder(BorderFactory.createLineBorder(new Color(255, 180, 220), 2));

                addActionListener(e -> {
                    if (!revealed && !matched && !flipping) cardClicked(this);
                });
            }


            public void flip(boolean showFront) {
                if (matched) return; // matched cards stay revealed

                revealed = showFront; // record state
                flipProgress = 1f;    // reset progress
                flipping = true;

                Timer t = new Timer(16, null);
                t.addActionListener(e -> {
                    flipProgress -= 0.15f;

                    if (flipProgress <= 0f) {
                        // swap image at midpoint
                        setIcon(new ImageIcon(showFront ? scaledFront : scaledBack));
                    }

                    if (flipProgress <= -1f) {
                        flipProgress = 1f;
                        flipping = false;
                        ((Timer) e.getSource()).stop();
                    }

                    repaint();
                });
                t.start();
            }


            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int h = getHeight();
                g2.translate(0, h * (1 - flipProgress) / 2f);
                g2.scale(1, flipProgress);

                super.paintComponent(g2);
                g2.dispose();
            }

            public boolean isMatched() { return matched; }
            public boolean isRevealed() { return revealed; }
            public String getFront() { return frontPath; }

            public void setMatched(boolean m) {
                matched = m;
                revealed = true;
                setEnabled(false);
                setBorder(BorderFactory.createLineBorder(new Color(255, 100, 180), 4));
            }
        }

    }

    // ================= LEVEL SELECTION =================
    public void showLevelSelection() {
        JPanel panel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundMenu != null)
                    g.drawImage(backgroundMenu, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JLabel title = new JLabel("Select Level", SwingConstants.CENTER);
        title.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 38));
        title.setForeground(new Color(90, 80, 110));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        JPanel grid = new JPanel(new GridLayout(2, 5, 20, 20));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        for (int i = 1; i <= 10; i++) {
            int lvl = i;
            JButton btn = createFluffyButton(String.valueOf(i), false);
            btn.addActionListener(e -> showGameScreen(lvl));
            grid.add(btn);
        }

        JButton back = createFluffyButton("Back", false);
        back.addActionListener(e -> showMenu());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(back);

        panel.add(title, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FluffyMemoryMatch::new);
    }
}
