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

    public boolean isDouble() {
        return left == right;
    }

    public DominoPiece flipped() {
        return new DominoPiece(right, left);
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

                if (piece.getRight() == leftEnd) {
                    board.add(0, piece);
                } else if (piece.getLeft() == leftEnd) {
                    board.add(0, piece.flipped());
                } else if (piece.getLeft() == rightEnd) {
                    board.add(piece);
                } else if (piece.getRight() == rightEnd) {
                    board.add(piece.flipped());
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
        return piece.getLeft() == leftEnd || piece.getRight() == leftEnd ||
                piece.getLeft() == rightEnd || piece.getRight() == rightEnd ||
                (piece.isDouble() && (piece.getLeft() == leftEnd || piece.getLeft() == rightEnd));
    }

    public boolean canPlayerMove() {
        for (DominoPiece piece : playerHand) {
            if (canPlayPiece(piece)) {
                return true;
            }
        }
        return false;
    }

    public boolean canComputerMove() {
        for (DominoPiece piece : computerHand) {
            if (canPlayPiece(piece)) {
                return true;
            }
        }
        return false;
    }

    public void drawForPlayer() {
        if (!deck.isEmpty()) {
            playerHand.add(deck.remove(0));
        }
    }

    public void drawForComputer() {
        if (!deck.isEmpty()) {
            computerHand.add(deck.remove(0));
        }
    }

    public void redistributeHands() {
        List<DominoPiece> allPieces = new ArrayList<>();
        allPieces.addAll(playerHand);
        allPieces.addAll(computerHand);
        allPieces.addAll(deck);
        playerHand.clear();
        computerHand.clear();
        deck.clear();

        Collections.shuffle(allPieces);

        for (int i = 0; i < 5; i++) {
            if (!allPieces.isEmpty()) playerHand.add(allPieces.remove(0));
            if (!allPieces.isEmpty()) computerHand.add(allPieces.remove(0));
        }

        deck.addAll(allPieces);
    }

    public void computerMove() {
        for (DominoPiece piece : computerHand) {
            if (playPiece(piece, false)) {
                computerHand.remove(piece);
                return;
            }
        }
        if (!deck.isEmpty()) {
            drawForComputer();
            computerMove();
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
        int rowHeight = 70;
        int piecesPerRow = getWidth() / 70;

        for (int i = 0; i < pieces.size(); i++) {
            DominoPiece piece = pieces.get(i);
            drawDomino(g, x, y, piece);
            x += 70;

            if ((i + 1) % piecesPerRow == 0) {
                x = 10;
                y += rowHeight;
            }
        }
    }

    private void drawDomino(Graphics g, int x, int y, DominoPiece piece) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, 60, 30);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, 60, 30);
        g.drawLine(x + 30, y, x + 30, y + 30);
        drawDots(g, x + 10, y + 10, piece.getLeft());
        drawDots(g, x + 40, y + 10, piece.getRight());
    }

    private void drawDots(Graphics g, int x, int y, int value) {
        int[][] positions = {
                {},
                {0, 0},
                {-5, -5, 5, 5},
                {-5, -5, 0, 0, 5, 5},
                {-5, -5, -5, 5, 5, -5, 5, 5},
                {-5, -5, -5, 5, 0, 0, 5, -5, 5, 5},
                {-5, -5, -5, 5, 0, -5, 0, 5, 5, -5, 5, 5}
        };
        for (int i = 0; i < positions[value].length; i += 2) {
            g.fillOval(x + positions[value][i], y + positions[value][i + 1], 4, 4);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 100);
    }
}

class HandPanel extends JPanel {
    private final List<DominoPiece> hand;
    private final DominoGame game;
    private final JPanel boardPanel;
    private final JButton drawButton;

    public HandPanel(List<DominoPiece> hand, DominoGame game, JPanel boardPanel) {
        this.hand = hand;
        this.game = game;
        this.boardPanel = boardPanel;

        drawButton = new JButton("Тянуть");
        drawButton.addActionListener(e -> {
            if (!game.canPlayerMove()) {
                if (!game.getDeck().isEmpty()) {
                    game.drawForPlayer();
                    repaint();
                } else if (!game.canPlayerMove() && !game.canComputerMove()) {
                    game.redistributeHands();
                    boardPanel.repaint();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "Нет ходов и добора! Конец.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Есть доступный ход!");
            }
        });
        add(drawButton);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = e.getX() / 70;
                int row = e.getY() / 70;
                index += row * (getWidth() / 70);

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
                        JOptionPane.showMessageDialog(null, "Неверный ход.");
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
        int rowHeight = 70;
        int piecesPerRow = getWidth() / 70;

        for (int i = 0; i < hand.size(); i++) {
            DominoPiece piece = hand.get(i);
            if (game.canPlayPiece(piece)) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.RED);
            }
            drawDomino(g, x, y, piece);
            x += 70;

            if ((i + 1) % piecesPerRow == 0) {
                x = 10;
                y += rowHeight;
            }
        }
    }

    private void drawDomino(Graphics g, int x, int y, DominoPiece piece) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, 60, 30);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, 60, 30);
        g.drawLine(x + 30, y, x + 30, y + 30);
        drawDots(g, x + 10, y + 10, piece.getLeft());
        drawDots(g, x + 40, y + 10, piece.getRight());
    }

    private void drawDots(Graphics g, int x, int y, int value) {
        int[][] positions = {
                {},
                {0, 0},
                {-5, -5, 5, 5},
                {-5, -5, 0, 0, 5, 5},
                {-5, -5, -5, 5, 5, -5, 5, 5},
                {-5, -5, -5, 5, 0, 0, 5, -5, 5, 5},
                {-5, -5, -5, 5, 0, -5, 0, 5, 5, -5, 5, 5}
        };
        for (int i = 0; i < positions[value].length; i += 2) {
            g.fillOval(x + positions[value][i], y + positions[value][i + 1], 4, 4);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, Math.max(100, (hand.size() / (getWidth() / 70) + 1) * 70));
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