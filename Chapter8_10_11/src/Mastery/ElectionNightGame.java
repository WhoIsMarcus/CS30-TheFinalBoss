
/*
 * Inheritance: CarneyCandidate, PoilievreCandidate, LewisCandidate, and MayCandidate
 * extend the abstract Candidate class, sharing its portrait and ballot-position logic.
 * Polymorphism: the game stores them in Candidate[] and calls getName() and getColor();
 * Java runs each subclass's overridden methods in the voting, results, and history views.
 */
package Mastery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ElectionNightGame extends JFrame {

/*
	 * Inheritance: CarneyCandidate, PoilievreCandidate, LewisCandidate, and MayCandidate
	 * extend the abstract Candidate class, sharing its portrait and ballot-position logic.
	 * Polymorphism: the game stores them in Candidate[] and calls getName() and getColor();
	 * Java runs each subclass's overridden methods in the voting, results, and history views.
	 */
	//
	// Parent class: shared data and behavior
    private static abstract class Candidate {
        private final int ballotPosition;

        Candidate(int ballotPosition) {
            this.ballotPosition = ballotPosition;
        }

        abstract String getName();

        abstract Color getColor();

        String getPortraitFile() {
            return "c" + ballotPosition + ".png";//
        }

        String getRole() {
            return "Prime minister candidate";
        }
    }

    // Each child overrides the same methods differently 
    private static class CarneyCandidate extends Candidate {
        CarneyCandidate() {
            super(0);
        }

        @Override
        String getName() {
            return "Mark Carney";
        }

        @Override
        Color getColor() {
            return new Color(86, 167, 255);
        }
    }

    private static class PoilievreCandidate extends Candidate { //child objects to call canadites
        PoilievreCandidate() {
            super(1);
        }

        @Override
        String getName() {
            return "Pierre Poilievre";
        }

        @Override
        Color getColor() {
            return new Color(242, 129, 165);
        }
    }

    private static class LewisCandidate extends Candidate {
        LewisCandidate() {
            super(2);
        }

        @Override
        String getName() {
            return "Avi Lewis";
        }

        @Override
        Color getColor() {
            return new Color(111, 209, 172);
        }
    }

    private static class MayCandidate extends Candidate {
        MayCandidate() {
            super(3);
        }

        @Override
        String getName() {
            return "Elizabeth May";
        }

        @Override
        Color getColor() {
            return new Color(244, 184, 94);
        }
    }

    // Parent-type array stores different child objects
    private static final Candidate[] CANDIDATES = {
            new CarneyCandidate(),
            new PoilievreCandidate(),
            new LewisCandidate(),
            new MayCandidate()
    };

    private static String[] candidateNames() {
        String[] names = new String[CANDIDATES.length];

        for (int i = 0; i < CANDIDATES.length; i++) {
            names[i] = CANDIDATES[i].getName();
        }

        return names;
    }

    private static final Color BACKGROUND = new Color(13, 22, 38);
    private static final Color CARD = new Color(25, 39, 62);
    private static final Color WHITE = new Color(242, 246, 252);
    private static final Random RANDOM = new Random();

    private static final BufferedImage[] PORTRAITS = loadPortraits();

    private static final Path HISTORY_FILE =
            Paths.get("elections.txt").toAbsolutePath();

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Load candidate pictures
    private static BufferedImage[] loadPortraits() {
        BufferedImage[] images = new BufferedImage[CANDIDATES.length];

        for (int i = 0; i < images.length; i++) {
            String name = CANDIDATES[i].getPortraitFile();

            URL resource = ElectionNightGame.class.getResource(name);

            try {
                if (resource != null) {
                    images[i] = ImageIO.read(resource);
                } else {
                    File file = new File(name);

                    if (!file.isFile()) {
                        file = new File("src/Mastery", name);
                    }

                    if (!file.isFile()) {
                        file = new File("Mastery", name);
                    }

                    if (!file.isFile()) {
                        file = new File("src/main/java/Mastery", name);
                    }

                    if (!file.isFile()) {
                        throw new IOException("Could not find " + name);
                    }

                    images[i] = ImageIO.read(file);
                }

                if (images[i] == null) {
                    throw new IOException("Cannot decode " + name);
                }

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Unable to load candidate picture " + name, e
                );
            }
        }

        return images;
    }

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);

    private final JLabel subtitle = new JLabel(
            "Choose one candidate to vote for.",
            SwingConstants.CENTER
    );

    private final ResultsPanel results = new ResultsPanel();
    private final HistoryPanel history = new HistoryPanel();

    public ElectionNightGame() {
        super("Election Night — Voting Game");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 760);
        setMinimumSize(new Dimension(800, 650));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        setContentPane(root);

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 5));
        heading.setOpaque(false);

        JLabel title = new JLabel(
                "ELECTION NIGHT",
                SwingConstants.CENTER
        );

        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(WHITE);

        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(173, 191, 216));

        heading.add(title);
        heading.add(subtitle);

        root.add(heading, BorderLayout.NORTH);

        pages.setOpaque(false);

        pages.add(makeVotingPage(), "vote");
        pages.add(results, "results");
        pages.add(history, "history");

        root.add(pages, BorderLayout.CENTER);

        // Past elections button
        JButton historyButton = new JButton("PAST ELECTIONS");

        historyButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        historyButton.setFocusPainted(false);
        historyButton.addActionListener(e -> showHistory());

        JPanel footerButtons = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );

        footerButtons.setOpaque(false);
        footerButtons.add(historyButton);

        JLabel footnote = new JLabel(
                "Marcus' Voting Sim",
                SwingConstants.CENTER
        );

        footnote.setForeground(new Color(130, 150, 176));

        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setOpaque(false);

        footer.add(footerButtons, BorderLayout.CENTER);
        footer.add(footnote, BorderLayout.SOUTH);

        root.add(footer, BorderLayout.SOUTH);
    }

    // Voting screen
    private JPanel makeVotingPage() {
        JPanel page = new JPanel(new GridLayout(1, 4, 14, 0));

        page.setOpaque(false);
        page.setBorder(new EmptyBorder(22, 0, 35, 0));

        for (int i = 0; i < CANDIDATES.length; i++) {
            final int candidate = i;

            JPanel card = new JPanel(new BorderLayout(0, 12));
            card.setBackground(CARD);
            card.setBorder(new EmptyBorder(15, 12, 22, 12));

            Portrait portrait = new Portrait(i);
            card.add(portrait, BorderLayout.CENTER);

            JPanel details = new JPanel(new GridLayout(3, 1, 0, 9));
            details.setOpaque(false);

            JLabel name = new JLabel(
                    CANDIDATES[i].getName(),
                    SwingConstants.CENTER
            );

            name.setFont(new Font("SansSerif", Font.BOLD, 19));
            name.setForeground(WHITE);

            JLabel role = new JLabel(
                    CANDIDATES[i].getRole(),
                    SwingConstants.CENTER
            );

            role.setFont(new Font("SansSerif", Font.PLAIN, 12));
            role.setForeground(new Color(171, 191, 215));

            JButton vote = new JButton("VOTE");

            vote.setBackground(CANDIDATES[i].getColor());
            vote.setForeground(new Color(14, 22, 37));
            vote.setFont(new Font("SansSerif", Font.BOLD, 15));
            vote.setFocusPainted(false);

            vote.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );

            vote.addActionListener(e -> castVote(candidate));

            details.add(name);
            details.add(role);
            details.add(vote);

            card.add(details, BorderLayout.SOUTH);
            page.add(card);
        }

        return page;
    }

    // Run and save an election
    private void castVote(int selected) {
        int[] percentages = randomPercentages();

        int winner = 0;

        for (int i = 1; i < CANDIDATES.length; i++) {
            if (percentages[i] > percentages[winner]) {
                winner = i;
            }
        }

        Election election = new Election(
                LocalDateTime.now().format(DATE_FORMAT),
                percentages,
                selected,
                winner
        );

        try {
            saveElection(election);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save this election to:\n"
                            + HISTORY_FILE
                            + "\n\n"
                            + ex.getMessage(),
                    "Save failed",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }
        
        

        subtitle.setText("The votes are being counted...");

        results.begin(election, false);
        cardLayout.show(pages, "results");
    }
    
    // Generate percentages totalling 100
    private static int[] randomPercentages() {
        while (true) {
            int[] weights = new int[CANDIDATES.length];
            int[] percentages = new int[CANDIDATES.length];
            double[] fractions = new double[CANDIDATES.length];

            int total = 0;
            int assigned = 0;

            for (int i = 0; i < CANDIDATES.length; i++) {
                weights[i] = 20 + RANDOM.nextInt(100);
                total += weights[i];
            }

            for (int i = 0; i < CANDIDATES.length; i++) {
                double share = 100.0 * weights[i] / total;

                percentages[i] = (int) share;
                fractions[i] = share - percentages[i];

                assigned += percentages[i];
            }

            List<Integer> order = new ArrayList<>();

            for (int i = 0; i < CANDIDATES.length; i++) {
                order.add(i);
            }

            order.sort(
                    Comparator.comparingDouble(
                            (Integer i) -> fractions[i]
                    ).reversed()
            );

            for (int j = 0; j < 100 - assigned; j++) {
                percentages[order.get(j)]++;
            }

            int maximum = 0;
            int tied = 0;

            for (int percentage : percentages) {
                maximum = Math.max(maximum, percentage);
            }

            for (int percentage : percentages) {
                if (percentage == maximum) {
                    tied++;
                }
            }

            if (tied == 1) {
                return percentages;
            }
        }
    }

    // Save election to text file
    private static void saveElection(Election election) throws IOException {
        boolean needsHeader =
                !Files.exists(HISTORY_FILE)
                || Files.size(HISTORY_FILE) == 0;

        String header =
                "Date\tYour vote\tWinner\t"
                + String.join("\t", candidateNames())
                + "\n";

        StringBuilder row = new StringBuilder(election.date)
                .append('\t')
                .append(CANDIDATES[election.selected].getName())
                .append('\t')
                .append(CANDIDATES[election.winner].getName());

        for (int percentage : election.percentages) {
            row.append('\t').append(percentage);
        }

        row.append('\n');

        Files.writeString(
                HISTORY_FILE,
                (needsHeader ? header : "") + row,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    // Load previous elections
    private static List<Election> loadElections() throws IOException {
        List<Election> elections = new ArrayList<>();

        if (!Files.exists(HISTORY_FILE)) {
            return elections;
        }

        List<String> lines = Files.readAllLines(
                HISTORY_FILE,
                StandardCharsets.UTF_8
        );

        for (int lineNumber = 1; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);

            if (line.isBlank()) {
                continue;
            }

            try {
                String[] parts = line.split("\t", -1);

                if (parts.length != 3 + CANDIDATES.length) {
                    throw new IllegalArgumentException(
                            "Expected " + (3 + CANDIDATES.length) + " columns"
                    );
                }

                LocalDateTime.parse(parts[0], DATE_FORMAT);

                int selected = candidateIndex(parts[1]);
                int winner = candidateIndex(parts[2]);

                int[] votes = new int[CANDIDATES.length];

                int total = 0;
                int highest = -1;
                int actualWinner = -1;

                for (int i = 0; i < votes.length; i++) {
                    votes[i] = Integer.parseInt(parts[i + 3]);

                    if (votes[i] < 0 || votes[i] > 100) {
                        throw new IllegalArgumentException(
                                "Percentage out of range"
                        );
                    }

                    total += votes[i];

                    if (votes[i] > highest) {
                        highest = votes[i];
                        actualWinner = i;
                    }
                }

                if (total != 100 || actualWinner != winner) {
                    throw new IllegalArgumentException("Invalid results");
                }

                elections.add(
                        new Election(parts[0], votes, selected, winner)
                );

            } catch (RuntimeException ex) {
                throw new IOException(
                        "Invalid election on line " + (lineNumber + 1),
                        ex
                );
            }
        }

        return elections;
    }

    // Find candidate by name
    private static int candidateIndex(String name) {
        for (int i = 0; i < CANDIDATES.length; i++) {
            if (CANDIDATES[i].getName().equals(name)) {
                return i;
            }
        }

        throw new IllegalArgumentException(
                "Unknown candidate: " + name
        );
    }

    // Open history screen
    private void showHistory() {
        results.stopAnimation();

        if (history.refresh()) {
            subtitle.setText(
                    "Select an election to view its original results."
            );

            cardLayout.show(pages, "history");
        }
    }

    // Election record
    private static class Election {
        final String date;
        final int[] percentages;
        final int selected;
        final int winner;

        Election(
                String date,
                int[] percentages,
                int selected,
                int winner
        ) {
            this.date = date;
            this.percentages = percentages.clone();
            this.selected = selected;
            this.winner = winner;
        }
    }

    // History screen
    private class HistoryPanel extends JPanel {
        private final DefaultListModel<String> model =
                new DefaultListModel<>();

        private final JList<String> electionList = new JList<>(model);

        private final List<Election> entries = new ArrayList<>();

        HistoryPanel() {
            setBackground(CARD);
            setLayout(new BorderLayout(12, 16));
            setBorder(new EmptyBorder(24, 25, 24, 25));

            JLabel heading = new JLabel("SAVED ELECTIONS");

            heading.setForeground(WHITE);
            heading.setFont(new Font("SansSerif", Font.BOLD, 24));

            add(heading, BorderLayout.NORTH);

            electionList.setBackground(BACKGROUND);
            electionList.setForeground(WHITE);

            electionList.setSelectionBackground(
                    new Color(62, 87, 124)
            );

            electionList.setFont(
                    new Font("SansSerif", Font.PLAIN, 16)
            );

            electionList.setFixedCellHeight(48);

            electionList.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION
            );

            electionList.addMouseListener(
                    new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(
                                java.awt.event.MouseEvent e
                        ) {
                            if (e.getClickCount() == 2) {
                                showSelectedElection();
                            }
                        }
                    }
            );

            add(
                    new JScrollPane(electionList),
                    BorderLayout.CENTER
            );

            JPanel buttons = new JPanel(
                    new FlowLayout(FlowLayout.CENTER, 14, 8)
            );

            buttons.setOpaque(false);

            JButton view = new JButton("VIEW RESULTS");

            view.addActionListener(
                    e -> showSelectedElection()
            );

            JButton back = new JButton("BACK TO VOTING");

            back.addActionListener(e -> {
                subtitle.setText(
                        "Choose one candidate to vote for."
                );

                cardLayout.show(pages, "vote");
            });

            buttons.add(view);
            buttons.add(back);

            JPanel bottom = new JPanel(
                    new BorderLayout(0, 8)
            );

            bottom.setOpaque(false);
            bottom.add(buttons, BorderLayout.CENTER);

            JLabel filePath = new JLabel(
                    "History file: " + HISTORY_FILE
            );

            filePath.setForeground(new Color(171, 191, 215));

            filePath.setFont(
                    new Font("SansSerif", Font.PLAIN, 11)
            );

            filePath.setToolTipText(HISTORY_FILE.toString());

            bottom.add(filePath, BorderLayout.SOUTH);

            add(bottom, BorderLayout.SOUTH);
        }

        // Refresh saved election list
        boolean refresh() {
            List<Election> loaded;

            try {
                loaded = loadElections();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        ElectionNightGame.this,
                        "Could not load election history:\n"
                                + HISTORY_FILE
                                + "\n\n"
                                + ex.getMessage(),
                        "History error",
                        JOptionPane.ERROR_MESSAGE
                );

                return false;
            }

            entries.clear();
            model.clear();

            for (int i = loaded.size() - 1; i >= 0; i--) {
                Election election = loaded.get(i);

                entries.add(election);

                model.addElement(
                        "Election #" + (i + 1)
                        + "  |  " + election.date
                        + "  |  Winner: " + CANDIDATES[election.winner].getName()
                        + "  |  Your vote: " + CANDIDATES[election.selected].getName()
                );
            }

            if (entries.isEmpty()) {
                model.addElement(
                        "No saved elections yet. Cast a vote to create one."
                );
            } else {
                electionList.setSelectedIndex(0);
            }

            return true;
        }

        // Show selected election
        private void showSelectedElection() {
            int index = electionList.getSelectedIndex();

            if (index < 0 || index >= entries.size()) {
                return;
            }

            Election election = entries.get(index);

            results.begin(election, true);

            subtitle.setText(
                    "Past election from " + election.date
            );

            cardLayout.show(pages, "results");
        }
    }

    // Draw circular portraits
    private static void drawPortrait(
            Graphics2D g,
            int index,
            int x,
            int y,
            int size
    ) {
        BufferedImage image = PORTRAITS[index];

        Graphics2D p = (Graphics2D) g.create();

        p.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        p.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        p.setColor(CANDIDATES[index].getColor().darker().darker());
        p.fillOval(x, y, size, size);

        p.setClip(new Ellipse2D.Double(x, y, size, size));

        double scale = Math.max(
                (double) size / image.getWidth(),
                (double) size / image.getHeight()
        );

        int width = (int) Math.ceil(image.getWidth() * scale);
        int height = (int) Math.ceil(image.getHeight() * scale);

        p.drawImage(
                image,
                x + (size - width) / 2,
                y + (size - height) / 2,
                width,
                height,
                null
        );

        p.dispose();
    }

    // Candidate portrait component
    private static class Portrait extends JComponent {
        private final int index;

        Portrait(int index) {
            this.index = index;
        }

        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();

            int size = Math.min(
                    Math.min(getWidth() - 4, getHeight() - 4),
                    185
            );

            drawPortrait(
                    g,
                    index,
                    (getWidth() - size) / 2,
                    (getHeight() - size) / 2,
                    size
            );

            g.dispose();
        }
    }

    // Results screen
    private class ResultsPanel extends JPanel {
        private final List<Confetti> confetti = new ArrayList<>();

        private Timer timer;

        private int[] percentages;
        private int selected;
        private int winner;

        private double progress;

        private boolean celebrationStarted;
        private boolean historical;

        private Election currentElection;

        private JButton backToHistory;

        ResultsPanel() {
            setBackground(CARD);
            setLayout(new BorderLayout());

            JButton again = new JButton("NEW ELECTION");

            again.setFont(
                    new Font("SansSerif", Font.BOLD, 14)
            );

            again.setFocusPainted(false);

            again.addActionListener(e -> {
                timer.stop();
                confetti.clear();

                subtitle.setText(
                        "Choose one candidate to vote for."
                );

                cardLayout.show(pages, "vote");
            });

            JPanel buttons = new JPanel(
                    new FlowLayout(FlowLayout.CENTER, 0, 15)
            );

            buttons.setOpaque(false);
            buttons.add(again);

            backToHistory = new JButton("BACK TO HISTORY");

            backToHistory.setFont(
                    new Font("SansSerif", Font.BOLD, 14)
            );

            backToHistory.addActionListener(
                    e -> showHistory()
            );

            backToHistory.setVisible(false);

            buttons.add(backToHistory);

            add(buttons, BorderLayout.SOUTH);

            // Vote counting animation
            timer = new Timer(16, (ActionEvent e) -> {
                if (progress < 1) {
                    progress = Math.min(1, progress + 0.012);

                    if (progress == 1) {
                        subtitle.setText(
                                selected == winner
                                        ? "Your candidate won!"
                                        : "The election is over. Your candidate did not win."
                        );

                        if (selected == winner) {
                            launchConfetti();
                        }
                    }
                }

                for (Confetti piece : confetti) {
                    piece.update();
                }

                confetti.removeIf(
                        piece -> piece.y > getHeight() + 25
                );

                repaint();

                if (progress == 1 && confetti.isEmpty()) {
                    timer.stop();
                }
            });
        }

        // Stop animation
        void stopAnimation() {
            timer.stop();
            confetti.clear();
            repaint();
        }

        // Display an election
        void begin(Election election, boolean fromHistory) {
            stopAnimation();

            currentElection = election;

            percentages = election.percentages.clone();
            selected = election.selected;
            winner = election.winner;

            historical = fromHistory;
            progress = fromHistory ? 1 : 0;

            celebrationStarted = false;

            backToHistory.setVisible(fromHistory);

            if (!fromHistory) {
                timer.start();
            }

            repaint();
        }

        // Start celebration
        private void launchConfetti() {
            if (celebrationStarted) {
                return;
            }

            celebrationStarted = true;

            for (int i = 0; i < 190; i++) {
                confetti.add(
                        new Confetti(
                                RANDOM.nextInt(
                                        Math.max(1, getWidth())
                                ),
                                -RANDOM.nextInt(520),
                                RANDOM.nextDouble() * 2 - 1,
                                2.5 + RANDOM.nextDouble() * 3.7,
                                CANDIDATES[RANDOM.nextInt(CANDIDATES.length)].getColor()
                        )
                );
            }
        }

        // Draw election results
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            if (percentages == null) {
                return;
            }

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            int barX = Math.max(255, w / 3);
            int barW = Math.max(100, w - barX - 100);

            int top = 153;

            int step = Math.max(
                    72,
                    Math.min(105, (h - 255) / CANDIDATES.length)
            );

            int highest = 0;

            for (int value : percentages) {
                highest = Math.max(highest, value);
            }

            int axisMax = Math.max(
                    40,
                    ((highest + 9) / 10) * 10
            );

            g.setFont(
                    new Font("SansSerif", Font.BOLD, 24)
            );

            g.setColor(WHITE);

            String heading = progress < 1
                    ? "COUNTING VOTES..."
                    : "WINNER: " + CANDIDATES[winner].getName();

            g.drawString(heading, 35, 58);

            g.setFont(
                    new Font("SansSerif", Font.PLAIN, 15)
            );

            g.setColor(new Color(174, 194, 218));

            g.drawString(
                    "Your vote: " + CANDIDATES[selected].getName(),
                    35,
                    88
            );

            g.drawString(
                    historical
                            ? "Saved election: " + currentElection.date
                            : "Random Distribution",
                    35,
                    112
            );

            // Graph grid
            g.setFont(
                    new Font("SansSerif", Font.PLAIN, 12)
            );

            for (int tick = 0; tick <= 4; tick++) {
                int x = barX + tick * barW / 4;

                g.setColor(new Color(57, 73, 96));

                g.drawLine(
                        x,
                        top - 14,
                        x,
                        top + step * (CANDIDATES.length - 1) + 47
                );

                g.setColor(new Color(162, 183, 210));

                String label = (tick * axisMax / 4) + "%";

                g.drawString(
                        label,
                        x - g.getFontMetrics().stringWidth(label) / 2,
                        top - 23
                );
            }

            double eased = 1 - Math.pow(1 - progress, 3);

            // Candidate bars
            for (int i = 0; i < CANDIDATES.length; i++) {
                int y = top + i * step;

                // Highlight your vote
                if (progress == 1 && i == selected) {
                    g.setColor(new Color(255, 215, 0));
                    g.setStroke(new BasicStroke(2.5f));

                    g.drawRoundRect(
                            12,
                            y - 17,
                            w - 24,
                            65,
                            18,
                            18
                    );
                }

                // Candidate picture
                drawPortrait(
                        g,
                        i,
                        24,
                        y - 11,
                        49
                );

                g.setFont(
                        new Font("SansSerif", Font.BOLD, 14)
                );

                g.setColor(WHITE);

                g.drawString(
                        CANDIDATES[i].getName(),
                        83,
                        y + 18
                );

                if (progress == 1 && i == selected) {
                    g.setColor(new Color(255, 215, 0));

                    g.setFont(
                            new Font("SansSerif", Font.BOLD, 11)
                    );

                    g.drawString(
                            "YOUR VOTE",
                            83,
                            y + 39
                    );
                }

                // Bar background
                g.setColor(new Color(43, 59, 81));

                g.fillRoundRect(
                        barX,
                        y,
                        barW,
                        34,
                        12,
                        12
                );

                int width = (int) Math.round(
                        barW * percentages[i]
                        / (double) axisMax * eased
                );

                // Coloured bar
                g.setColor(CANDIDATES[i].getColor());

                if (width > 0) {
                    g.fillRoundRect(
                            barX,
                            y,
                            width,
                            34,
                            12,
                            12
                    );
                }

                // Percentage
                g.setColor(WHITE);

                g.setFont(
                        new Font("SansSerif", Font.BOLD, 16)
                );

                g.drawString(
                        Math.round(percentages[i] * eased) + "%",
                        barX + barW + 13,
                        y + 24
                );

                // Winner label
                if (progress == 1 && i == winner) {
                    g.setColor(new Color(255, 218, 121));

                    g.setFont(
                            new Font("SansSerif", Font.BOLD, 12)
                    );

                    g.drawString(
                            "WINNER",
                            barX + 10,
                            y + 23
                    );
                }
            }

            // Confetti
            for (Confetti piece : confetti) {
                g.setColor(piece.color);

                g.fillRect(
                        (int) piece.x,
                        (int) piece.y,
                        piece.width,
                        piece.height
                );
            }

            g.dispose();
        }
    }

    // Confetti particle
    private static class Confetti {
        double x, y, vx, vy;

        final int width, height;
        final Color color;

        Confetti(
                double x,
                double y,
                double vx,
                double vy,
                Color color
        ) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;

            width = 5 + RANDOM.nextInt(7);
            height = 6 + RANDOM.nextInt(8);
        }

        void update() {
            x += vx;
            y += vy;

            vx += (RANDOM.nextDouble() - 0.5) * 0.16;
            vy = Math.min(7, vy + 0.025);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new ElectionNightGame().setVisible(true)
        );
    }
}