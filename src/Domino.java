import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

class DominoPiece {
    private final int left;
    private final int right;

    public DominoPiece(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public boolean matches(int value) {
        return left == value || right == value;
    }

    @Override
    public String toString() {
        return "[" + left + "|" + right + "]";
    }
}

class DominoGame {
    private final List<DominoPiece> deck = new ArrayList<>();
    private final List<DominoPiece> board = new ArrayList<>();
    private final List<DominoPiece> playerHand = new ArrayList<>();
    private final List<DominoPiece> computerHand = new ArrayList<>();

    public DominoGame() {
        initializeDeck();
        shuffleDeck();
        dealPieces();
    }

    private void initializeDeck() {
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                deck.add(new DominoPiece(i, j));
            }
        }
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    private void dealPieces() {
        for (int i = 0; i < 5; i++) {
            playerHand.add(deck.remove(0));
            computerHand.add(deck.remove(0));
        }
    }

    public List<DominoPiece> getBoard() {
        return board;
    }

    public List<DominoPiece> getPlayerHand() {
        return playerHand;
    }

    public List<DominoPiece> getComputerHand() {
        return computerHand;
    }

    public List<DominoPiece> getDeck() {
        return deck;
    }

    public boolean playPiece(DominoPiece piece, boolean isPlayer) {
        if (canPlayPiece(piece)) {
            if (board.isEmpty()) {
                board.add(piece);
            } else {
                int leftEnd = board.get(0).getLeft();
                int rightEnd = board.get(board.size() - 1).getRight();

                if (piece.matches(leftEnd)) {
                    board.add(0, piece);
                } else if (piece.matches(rightEnd)) {
                    board.add(piece);
                }
            }
            return true;
        }
        return false;
    }

    public boolean canPlayPiece(DominoPiece piece) {
        if (board.isEmpty()) return true;
        int leftEnd = board.get(0).getLeft();
        int rightEnd = board.get(board.size() - 1).getRight();
        return piece.matches(leftEnd) || piece.matches(rightEnd);
    }

    public void computerMove() {
        for (DominoPiece piece : computerHand) {
            if (playPiece(piece, false)) {
                computerHand.remove(piece);
                return;
            }
        }
        // Draw piece if no valid move
        if (!deck.isEmpty()) {
            computerHand.add(deck.remove(0));
            computerMove(); // Retry move after drawing
        }
    }
}

class DominoPanel extends JPanel {
    private final List<DominoPiece> pieces;
    private final String label;

    public DominoPanel(String label, List<DominoPiece> pieces) {
        this.label = label;
        this.pieces = pieces;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(label, 10, 20);
        int x = 10;
        int y = 40;

        for (DominoPiece piece : pieces) {
            g.drawRect(x, y, 50, 25);
            g.drawString(piece.getLeft() + "|" + piece.getRight(), x + 10, y + 17);
            x += 60;
        }
    }
}

class HandPanel extends JPanel {
    private final List<DominoPiece> hand;
    private final DominoGame game;
    private final JPanel boardPanel;

    public HandPanel(List<DominoPiece> hand, DominoGame game, JPanel boardPanel) {
        this.hand = hand;
        this.game = game;
        this.boardPanel = boardPanel;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = e.getX() / 60;
                if (index >= 0 && index < hand.size()) {
                    DominoPiece piece = hand.get(index);
                    if (game.playPiece(piece, true)) {
                        hand.remove(piece);
                        boardPanel.repaint();
                        repaint();

                        // Trigger computer's turn
                        SwingUtilities.invokeLater(() -> {
                            game.computerMove();
                            boardPanel.repaint();
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid move! Try another piece.");
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("Your Hand", 10, 20);
        int x = 10;
        int y = 40;

        for (DominoPiece piece : hand) {
            g.drawRect(x, y, 50, 25);
            g.drawString(piece.getLeft() + "|" + piece.getRight(), x + 10, y + 17);
            x += 60;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 100);
    }
}

public class Domino {
    public static void main(String[] args) {
        DominoGame game = new DominoGame();

        JFrame frame = new JFrame("Domino Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        DominoPanel boardPanel = new DominoPanel("Board", game.getBoard());
        HandPanel playerPanel = new HandPanel(game.getPlayerHand(), game, boardPanel);

        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(playerPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

        playerPanel.repaint();
    }
}