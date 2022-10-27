package PokerJava;

import Client.Client;
import Commands.Enums.CommandEnum;
import Commands.Model.CommandModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ClientWindow extends JFrame {
    private JPanel mainPanel;
    private JButton BETButton;
    private JPanel bottomInterface;
    private JPanel interface1;
    private JPanel interface2;
    private JPanel betsPanel;
    private JPanel tablePanel;
    private JPanel topInterface;
    private JPanel rightPlayers;
    private JPanel leftPlayers;
    private JPanel player1Panel;
    private JPanel player2Panel;
    private JPanel player3Panel;
    private JPanel player4Panel;
    private JLabel flopCard1;
    private JLabel flopCard2;
    private JLabel flopCard3;
    private JLabel turnCard;
    private JLabel riverCard;
    private JLabel player1Label;
    private JLabel player2Label;
    private JLabel player3Label;
    private JLabel player4Label;
    private JLabel player1Card1;
    private JLabel player1Card2;
    private JLabel player2Card1;
    private JLabel player2Card2;
    private JLabel player3Card1;
    private JLabel player3Card2;
    private JLabel player4Card2;
    private JLabel player4Card1;
    private JComboBox betComboBox;
    private JButton CHECKButton;
    private JButton FOLDButton;
    private JTextArea textArea1;
    private JTextField textField1;
    private JProgressBar player1ProgressBar;
    private JProgressBar player2ProgressBar;
    private JProgressBar player3ProgressBar;
    private JProgressBar player4ProgressBar;
    private JProgressBar myProgressBar;
    private JLabel[] myCards;
    private JLabel myCard1;
    private JLabel myCard2;
    private JLabel chipsImage;
    private int chips;
    private JLabel potLabel;
    private JLabel potTitle;
    private JButton RAISEButton;
    private JButton CALLButton;
    private JLabel player1BetLabel;
    private JLabel player2BetLabel;
    private JLabel player3BetLabel;
    private JLabel player4BetLabel;
    private JLabel chipsLabel;
    private JLabel myBetLabel;
    private JLabel[] tableCardLabels;
    private JLabel[] playerLabels;
    private JLabel[][] playersCards;
    private JLabel roleLabel;

    private JProgressBar[] playersProgressBars;
    private JLabel[] playersBetLabels;
    private boolean isSpam = false;
    private Timer spamTimer;
    private Timer progressBarTimer;
    private int playerTurnIndex = 0;
    private Player myPlayer;
    private int winnerIndex;
    private int pot = 0;
    private int currentBet = 0;

    private ArrayList<Player> windowPlayers;
    private Client myClient;
    public void addPlayer(Player player) {
        if(player == null) return;
        if(player.role.equals("Player"))
            playerLabels[player.place].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/user.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        if(player.role.equals("Big Blind"))
            playerLabels[player.place].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/big_blind_user.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        if(player.role.equals("Small Blind")) {
            playerLabels[player.place].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/small_blind_user.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        }
        if(player.role.equals("Dealer")) {
            playerLabels[player.place].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/dealer_user.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        }
        playerLabels[player.place].setText(player.nickName);
        playerLabels[player.place].setHorizontalTextPosition(JLabel.CENTER);
        playerLabels[player.place].setVerticalTextPosition(JLabel.BOTTOM);
    }
    public void removePlayer(int place) {
        playerLabels[place].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/absense.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        playerLabels[place].setText("");
    }
    public void openPlayersCards() {
        for(Player p : windowPlayers) {
            if(p == null) continue;
            if(p.isFold) continue;
            for(int i = 0; i < p.hand.size(); i++)
                playersCards[p.place][i].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("cards/" + p.hand.get(i).GetColor() + "/" + p.hand.get(i).GetName() + ".jpg")).getImage().getScaledInstance(75, 100, Image.SCALE_SMOOTH)));
        }
    }
    public void showPlayersCards(ArrayList<Player> players) {
        windowPlayers = players;
        for(Player p : players) {
            if(p == null) continue;
            if(p.isFold) continue;
            for(int i = 0; i < p.hand.size(); i++) {
                if(p.getPlace() != myPlayer.getPlace())
                    playersCards[p.place][i].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("cards/shirt.png")).getImage().getScaledInstance(75, 100, Image.SCALE_SMOOTH)));
            }
        }
    }
    public void showMyCards(Player me) {
        myPlayer = me;
        if(me == null) return;
        for(int i = 0; i < me.hand.size(); i++)
            myCards[i].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("cards/" + me.hand.get(i).GetColor() + "/" + me.hand.get(i).GetName() + ".jpg")).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
        playerLabels[me.place].setVisible(false);
    }
    public void startGame() {
        for(JLabel jl : tableCardLabels) {
            jl.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("cards/shirt.png")).getImage().getScaledInstance(125, 175, Image.SCALE_SMOOTH)));
        }
        startProgressBar();
    }
    public void stopProgressBar() {
        progressBarTimer.cancel();
        progressBarTimer.purge();
        for(JProgressBar pb : playersProgressBars) {
            pb.setValue(0);
            pb.setVisible(false);
        }
        myProgressBar.setValue(0);
        myProgressBar.setVisible(false);
    }
    public void startSpamTimer() {
        spamTimer.purge();
        isSpam = true;
        spamTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                isSpam = false;
            }
        }, 300);
    }
    public void startProgressBar() {
        stopProgressBar();
        System.out.println("Client window player turn: " + playerTurnIndex);
        for(JProgressBar pb : playersProgressBars) {
            pb.setVisible(false);
        }
        if(myPlayer.getPlace() == playerTurnIndex) {
            myProgressBar.setVisible(true);
        }
        else {
            myProgressBar.setVisible(false);
            playersProgressBars[playerTurnIndex].setVisible(true);
        }
        progressBarTimer = new Timer();
        progressBarTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(playerTurnIndex == myPlayer.getPlace()) {
                    myProgressBar.setValue(myProgressBar.getValue()+5);
                    if(myProgressBar.getValue() >= 100) {
                        progressBarTimer.cancel();
                        progressBarTimer.purge();
                    }
                }
                else {
                    playersProgressBars[playerTurnIndex].setValue(playersProgressBars[playerTurnIndex].getValue()+5);
                    if(playersProgressBars[playerTurnIndex].getValue() >= 100) {
                        progressBarTimer.cancel();
                        progressBarTimer.purge();
                    }
                }
            }
        }, 1000, 1000);
    }
    public void setPlayerTurnIndex(int place) {
        playerTurnIndex = place;
    }

    public void setPot(int pot) {
        this.pot = pot;
        potLabel.setText(String.valueOf(pot));
    }

    public void setChips(int chips) {
        chipsLabel.setText("CHIPS: " + chips);
    }

    public void setMyBet(int bet) {
        myBetLabel.setText("BET: " + bet);
    }

    public void setMyPlayer(Player player) {
        System.out.println("WTF" + player.getBet());
        myPlayer = player;
        setChips(myPlayer.getChips());
        setMyBet(myPlayer.getBet());
    }

    public void setPlayersBets(ArrayList<Player> players) {
        for(Player p : players) {
            if(p.getPlace() == myPlayer.getPlace())
            {
                if(p.getBet() == 0)
                    myBetLabel.setText("CHECK");
                if(p.getBet() == -1)
                    myBetLabel.setText("");
                if(p.getBet() > 0)
                    myBetLabel.setText("BET: " + p.getBet());
                continue;
            }
            if(p.getBet() == 0)
                playersBetLabels[p.getPlace()].setText("CHECK");
            if(p.getBet() == -1)
                playersBetLabels[p.getPlace()].setText("...");
            if(p.getBet() > 0)
                playersBetLabels[p.getPlace()].setText("BET: " + p.getBet());
        }
    }

    public void setTableCards(ArrayList<Card> cards) {
        for(int i = 0; i < 5; i++) {
            if(cards.get(i).isOpened())
                tableCardLabels[i].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("cards/"+ cards.get(i).GetColor()+"/"+cards.get(i).GetName()+".jpg")).getImage().getScaledInstance(125, 175, Image.SCALE_SMOOTH)));
            else
                tableCardLabels[i].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("cards/shirt.png")).getImage().getScaledInstance(125, 175, Image.SCALE_SMOOTH)));
        }
    }

    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

    public void setPlayerFold(int playerPlace) {
        for(int i = 0; i < 2; i++)
            playersCards[playerPlace][i].setVisible(false);
        playerLabels[playerPlace].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/fold_user.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));

    }

    public void setWinner(int playerPlace) {
        winnerIndex = playerPlace;
    }

    public void setMyChips(int chips) {
        this.chips = chips;
    }

    public void showWinner() {
        stopProgressBar();
        if(winnerIndex < 0) return;
        playerLabels[winnerIndex].setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/winner_user.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
    }

    public void setMoveButtons() {
        if((currentBet == 0)||(currentBet == myPlayer.bet)) {
            CALLButton.setVisible(false);
            RAISEButton.setVisible(false);
            BETButton.setVisible(true);
            CHECKButton.setVisible(true);
        } else {
            CALLButton.setVisible(true);
            RAISEButton.setVisible(true);
            BETButton.setVisible(false);
            CHECKButton.setVisible(false);
        }
    }

    public ClientWindow(Client client) {
        myClient = client;
        tableCardLabels = new JLabel[5];
        tableCardLabels[0] = flopCard1;
        tableCardLabels[1] = flopCard2;
        tableCardLabels[2] = flopCard3;
        tableCardLabels[3] = turnCard;
        tableCardLabels[4] = riverCard;
        playerLabels = new JLabel[4];
        playerLabels[0] = player1Label;
        playerLabels[1] = player2Label;
        playerLabels[2] = player3Label;
        playerLabels[3] = player4Label;
        for(int i = 0; i < 4; i++) {
            playersCards = new JLabel[4][2];
            playersCards[0] = new JLabel[2];
        }
        playersCards[0][0] = player1Card1;
        playersCards[0][1] = player1Card2;
        playersCards[1][0] = player2Card1;
        playersCards[1][1] = player2Card2;
        playersCards[2][0] = player3Card1;
        playersCards[2][1] = player3Card2;
        playersCards[3][0] = player4Card1;
        playersCards[3][1] = player4Card2;
        myCards = new JLabel[2];
        myCards[0] = myCard1;
        myCards[1] = myCard2;
        for(JLabel jl : tableCardLabels) {
            jl.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("cards/absense.png")).getImage().getScaledInstance(125, 175, Image.SCALE_SMOOTH)));
        }
        for(JLabel jl : playerLabels) {
            jl.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/absense.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        }
        playersProgressBars = new JProgressBar[4];
        playersProgressBars[0] = player1ProgressBar;
        playersProgressBars[1] = player2ProgressBar;
        playersProgressBars[2] = player3ProgressBar;
        playersProgressBars[3] = player4ProgressBar;
        for(JProgressBar pb : playersProgressBars) {
            pb.setPreferredSize(new Dimension(100, 10));
        }
        progressBarTimer = new Timer();
        chipsImage.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("interface/chips.png")).getImage().getScaledInstance(50,50,Image.SCALE_SMOOTH)));
        playersBetLabels = new JLabel[4];
        playersBetLabels[0] = player1BetLabel;
        playersBetLabels[1] = player2BetLabel;
        playersBetLabels[2] = player3BetLabel;
        playersBetLabels[3] = player4BetLabel;

        setMoveButtons();

        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        CommandModel commandModel = new CommandModel();
        spamTimer = new Timer();
        BETButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPlayer.getPlace() != playerTurnIndex) return;
                if(isSpam) return;
                if(e.getSource() == BETButton) {
                    int bet = Integer.parseInt(betComboBox.getSelectedItem().toString());
                    commandModel.set(CommandEnum.SendPlayerMoveToServer.toString(), myPlayer.getPlace(), "BET", bet);
                    myClient.sendMessage(commandModel.getString());
                    startSpamTimer();
                }
            }
        });
        CHECKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPlayer.getPlace() != playerTurnIndex) return;
                if(isSpam) return;
                if(e.getSource() == CHECKButton) {
                    commandModel.set(CommandEnum.SendPlayerMoveToServer.toString(), myPlayer.getPlace(), "CHECK", 0);
                    myClient.sendMessage(commandModel.getString());
                    startSpamTimer();
                }
            }
        });
        CALLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPlayer.getPlace() != playerTurnIndex) return;
                if(isSpam) return;
                if(e.getSource() == CALLButton) {
                    commandModel.set(CommandEnum.SendPlayerMoveToServer.toString(), myPlayer.getPlace(), "CALL", 0);
                    myClient.sendMessage(commandModel.getString());
                    startSpamTimer();
                }
            }
        });
        RAISEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPlayer.getPlace() != playerTurnIndex) return;
                if(isSpam) return;
                if(e.getSource() == CHECKButton) {
                    int bet = Integer.parseInt(betComboBox.getSelectedItem().toString());
                    commandModel.set(CommandEnum.SendPlayerMoveToServer.toString(), myPlayer.getPlace(), "RAISE", bet);
                    myClient.sendMessage(commandModel.getString());
                    startSpamTimer();
                }
            }
        });
        FOLDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPlayer.getPlace() != playerTurnIndex) return;
                if(isSpam) return;
                if(e.getSource() == FOLDButton) {
                    commandModel.set(CommandEnum.SendPlayerMoveToServer.toString(), myPlayer.getPlace(), "FOLD", 0);
                    myClient.sendMessage(commandModel.getString());
                    startSpamTimer();
                }
            }
        });
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (e.getSource() instanceof JFrame) {
                    JFrame frame = (JFrame)(e.getSource());
                   // frame.repaint();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }


}
