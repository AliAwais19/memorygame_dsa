import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class DSAMemoryGame extends JFrame {
    private final int gridSize;
    private final JButton[] buttons;
    private final Character[] tileValues;
    private final javax.swing.Timer hideTimer;
    private javax.swing.Timer pauseTimer;
    private int firstIndex = -1;
    private int secondIndex = -1;
    private boolean waitingToFlip = false;

    private final int currentLevel;

    private final Queue<String> matchHistory = new LinkedList<>(); // DSA Queue for matched tiles

    // Soft eye-friendly color scheme
    private final Color hiddenColor = new Color(199, 223, 255);   // #C7DFFF
    private final Color revealedColor = new Color(174, 223, 247); // #AEDFF7
    private final Color matchedColor = new Color(166, 227, 161);  // #A6E3A1
    private final Color textColor = new Color(44, 62, 80);         // #2C3E50

    public DSAMemoryGame(int size, int level) {
        this.gridSize = size;
        this.currentLevel = level;
        this.buttons = new JButton[size * size];
        this.tileValues = new Character[size * size];

        setTitle("DSA Memory Game - Level " + level);
        getContentPane().setBackground(new Color(242, 247, 255));
        setLayout(new GridLayout(size, size, 5, 5));
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        generateTiles();
        createBoard();

        showAllTiles();
        int memorizationTime = (currentLevel == 2) ? 10000 : 5000;
        hideTimer = new javax.swing.Timer(memorizationTime, e -> hideAllTiles());
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void generateTiles() {
        List<Character> values = new ArrayList<>();
        char ch = 'A';
        for (int i = 0; i < (gridSize * gridSize) / 2; i++) {
            values.add(ch);
            values.add(ch);
            ch++;
        }
        Collections.shuffle(values);
        values.toArray(tileValues);
    }

    private void createBoard() {
        for (int i = 0; i < tileValues.length; i++) {
            JButton btn = new JButton();
            btn.setFont(new Font("Arial", Font.BOLD, 36));
            btn.setBackground(hiddenColor);
            btn.setForeground(textColor);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 220), 2));
            int index = i;
            btn.addActionListener(e -> tileClicked(index));
            buttons[i] = btn;
            add(btn);
        }
    }

    private void tileClicked(int index) {
        if (buttons[index].getText().length() > 0 || !buttons[index].isEnabled()) return;

        // Handle early third click by immediately hiding previous unmatched tiles
        if (waitingToFlip && pauseTimer != null && pauseTimer.isRunning()) {
            pauseTimer.stop();
            hideUnmatchedTiles();
        }

        buttons[index].setText(tileValues[index].toString());
        buttons[index].setBackground(revealedColor);

        if (firstIndex == -1) {
            firstIndex = index;
        } else if (secondIndex == -1) {
            secondIndex = index;
            if (tileValues[firstIndex].equals(tileValues[secondIndex])) {
                // Matched
                buttons[firstIndex].setEnabled(false);
                buttons[secondIndex].setEnabled(false);
                buttons[firstIndex].setBackground(matchedColor);
                buttons[secondIndex].setBackground(matchedColor);
                matchHistory.add("Matched: " + tileValues[firstIndex]); // Add to queue
                resetSelection();
                checkWin();
            } else {
                // Not matched — wait briefly before flipping back
                waitingToFlip = true;
                pauseTimer = new javax.swing.Timer(800, e -> hideUnmatchedTiles());
                pauseTimer.setRepeats(false);
                pauseTimer.start();
            }
        }
    }

    private void hideUnmatchedTiles() {
        if (firstIndex != -1 && secondIndex != -1) {
            buttons[firstIndex].setText("");
            buttons[secondIndex].setText("");
            buttons[firstIndex].setBackground(hiddenColor);
            buttons[secondIndex].setBackground(hiddenColor);
        }
        resetSelection();
    }

    private void resetSelection() {
        firstIndex = -1;
        secondIndex = -1;
        waitingToFlip = false;
    }

    private void showAllTiles() {
        for (int i = 0; i < tileValues.length; i++) {
            buttons[i].setText(tileValues[i].toString());
            buttons[i].setBackground(revealedColor);
        }
    }

    private void hideAllTiles() {
        for (JButton btn : buttons) {
            if (btn.isEnabled()) {
                btn.setText("");
                btn.setBackground(hiddenColor);
            }
        }
    }

    private void checkWin() {
        for (JButton button : buttons) {
            if (button.isEnabled()) {
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Great! You completed Level " + currentLevel + "\nMatched tiles (queue):\n" + matchHistory);
        dispose();

        if (currentLevel == 1) {
            SwingUtilities.invokeLater(() -> new DSAMemoryGame(6, 2).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(null, "🎉 Congratulations! You completed all levels!");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DSAMemoryGame(4, 1).setVisible(true));
    }
}
