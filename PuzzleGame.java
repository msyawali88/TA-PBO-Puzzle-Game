import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class PuzzleGame extends JFrame implements ActionListener {

    JButton[] tiles = new JButton[9];
    Timer timer;
    int seconds;
    private String playerName;

    public PuzzleGame() {
        super("Puzzle Game - 2200018067");

        this.playerName = JOptionPane.showInputDialog("Nama Player :");

        if (playerName == null || playerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama player Harus di isi. Keluar dari permainan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }

        showWelcomeDialog();

        for (int i = 0; i < 9; i++) {
            tiles[i] = createButton(Integer.toString(i + 1));
        }
        tiles[8].setText("");

        List<JButton> buttonList = Arrays.asList(tiles);
        Collections.shuffle(buttonList);

        setLayout(new GridLayout(3, 3, 5, 5));

        for (JButton button : buttonList) {
            add(button);
            button.addActionListener(this);
        }

        seconds = 0;
        timer = new Timer(1000, e -> updateTimerLabel());
        timer.start();

        setSize(300, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        return button;
    }

    private void updateTimerLabel() {
        seconds++;
        setTitle("Puzzle Game - 2200018067 | Waktu : " + seconds + " detik");
    }

    public void actionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String label = sourceButton.getText();

        if (label.equals("")) {
            return;
        }

        int labelInt = Integer.parseInt(label);
        JButton emptyButton = findEmptyButton();

        if (isAdjacent(sourceButton, emptyButton)) {
            emptyButton.setText(label);
            sourceButton.setText("");
        }

        if (checkWin()) {
            timer.stop();
            showWinningDialog();
            saveScore();
            resetGame();
        }
    }

    private JButton findEmptyButton() {
        for (JButton button : tiles) {
            if (button.getText().equals("")) {
                return button;
            }
        }
        return null;
    }

    private boolean isAdjacent(JButton button1, JButton button2) {
        int index1 = Arrays.asList(tiles).indexOf(button1);
        int index2 = Arrays.asList(tiles).indexOf(button2);

        int row1 = index1 / 3;
        int col1 = index1 % 3;
        int row2 = index2 / 3;
        int col2 = index2 % 3;

        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
    }

    private boolean checkWin() {
        for (int i = 0; i < 8; i++) {
            if (!tiles[i].getText().equals(Integer.toString(i + 1))) {
                return false;
            }
        }
        return true;
    }

    private void resetGame() {
        List<JButton> buttonList = Arrays.asList(tiles);
        Collections.shuffle(buttonList);

        for (int i = 0; i < 9; i++) {
            tiles[i].setText(buttonList.get(i).getText());
            tiles[i].setEnabled(true);
        }

        seconds = 0;
        timer.start();
    }

    private void showWelcomeDialog() {
        JOptionPane.showMessageDialog(this, "Welcome to Puzzle Game!", " TA PBO - 2200018067", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWinningDialog() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Selamat, " + playerName + "! Anda Menang!\nWaktu yang dibutuhkan: " + seconds + " detik.\nIngin melihat daftar waktu tercepat?",
                "Kemenangan",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            showHighScoresDialog();
        }
    }

    private void showHighScoresDialog() {
        List<Score> scores = readScores();
        StringBuilder sb = new StringBuilder("Daftar waktu tercepat:\n");
        int rank = 1;
        for (Score score : scores) {
            sb.append(rank++).append(". ").append(score.getPlayerName()).append(" - ").append(score.getSeconds()).append(" detik\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Top Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveScore() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("scores.txt", true))) {
            writer.println(playerName + "   :   " + seconds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Score> readScores() {
        List<Score> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("scores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String playerName = parts[0];
                    int seconds = Integer.parseInt(parts[1]);
                    scores.add(new Score(playerName, seconds));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PuzzleGame());
    }
}

class Score implements Comparable<Score> {
    private String playerName;
    private int seconds;

    public Score(String playerName, int seconds) {
        this.playerName = playerName;
        this.seconds = seconds;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.seconds, other.seconds);
    }
}
