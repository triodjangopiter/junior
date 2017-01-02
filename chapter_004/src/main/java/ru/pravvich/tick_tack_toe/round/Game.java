package ru.pravvich.tick_tack_toe.round;

import ru.pravvich.tick_tack_toe.*;
import ru.pravvich.tick_tack_toe.Users.*;
import ru.pravvich.tick_tack_toe.desk.Board;
import ru.pravvich.tick_tack_toe.desk.Desc;
import ru.pravvich.tick_tack_toe.desk.Printer;
import ru.pravvich.tick_tack_toe.desk.ValidationWinnerUtil;

import java.util.ArrayList;

import static java.lang.String.format;

/**
 * Determines game process.
 */
public class Game implements Round {

    /**
     * winner.
     */
    private Gamers winner;

    /**
     * For input.
     */
    private In input = new Input();
    /**
     * Bot player.
     */
    private Gamers bot = new Bot();

    /**
     * User player.
     */
    private Gamers user = new User();

    /**
     * Desc for play.
     */
    private Board desc = new Desc();

    /**
     * List contain all gamer: bot and user.
     */
    private ArrayList<Gamers> gamers = new ArrayList<>();

    /**
     * Util class contain algorithm determining winner.
     */
    private ValidationWinnerUtil valid = new ValidationWinnerUtil();

    @Override
    public Gamers getWinner() {
        return this.winner;
    }

    /**
     * Check correct move.
     * @param player player which move.
     * @return true if move success. False if move fail
     */
    private boolean move(Gamers player) {
        player.setPosit();
        if (this.desc.getDesc()[player.getPosit().getY()][player.getPosit().getX()] == ' ') {
            this.desc.getDesc()[player.getPosit().getY()][player.getPosit().getX()] = player.getColor();
            return true;
        } else {
            System.err.println("Так ходить нельзя!");
            return false;
        }
    }

    /**
     * Determines fist move.
     */
    @Override
    public void firstMove() {
        this.desc.descSize( );
        System.out.println("Кто ходит первым? Enter: Bot / I");
        if (this.input.getStrInput().toUpperCase().equals("BOT")) {
            this.fstMoveBot();
        } else {
            this.fstMoveUsr();
        }
        Printer.printDesc(this.desc.getDesc());
        this.loopMoves();
    }

    /**
     * Configurable statement game if bot move second.
     */
    private void fstMoveBot() {
        this.bot.setColor('X');
        this.user.setColor('O');
        this.gamers.add(this.bot);
        this.gamers.add(this.user);
    }

    /**
     * Configurable statement game if user choice move first.
     */
    private void fstMoveUsr() {
        this.user.setColor('X');
        this.bot.setColor('O' );
        this.gamers.add(this.user);
        this.gamers.add( this.bot);
    }

    /**
     * Loop game process.
     */
    private void loopMoves() {
        Gamers winner = null;
        while (this.valid.gameCanGoOn(this.desc.getDesc())) {
            for (Gamers gamer : this.gamers) {

                if (this.valid.gameCanGoOn(this.desc.getDesc()) &&
                        this.move(gamer)
                        ) {

                    Printer.printDesc(this.desc.getDesc());
                    winner = gamer;

                } else if (this.valid.gameCanGoOn(this.desc.getDesc())) {
                    this.mistakeMove(gamer);
                    Printer.printDesc(this.desc.getDesc());
                }
            }
        }
        this.initResultGame(winner);
    }

    /**
     * Give more chance when player which mistake - try move in busy cell.
     * @param gamer player which mistake.
     */
    private void mistakeMove(Gamers gamer) {
        while (!this.move(gamer)) {
            mistakeMove(gamer);
        }
    }

    /**
     * Init result game.
     * @see TickTack#winners
     * @param winner gamer for estimated award.
     */
    private void initResultGame(Gamers winner) {
        if (!this.valid.emptyCallExist(  this.desc.getDesc()) &&
                !this.valid.winnerDetermines(this.desc.getDesc())
                ) {

            System.out.println("Ничья.");
        } else if (this.valid.winnerDetermines(  this.desc.getDesc())) {
            this.winner = winner;
            System.out.println(format("Победитель: %s", winner.getColor()));
        }
    }
}