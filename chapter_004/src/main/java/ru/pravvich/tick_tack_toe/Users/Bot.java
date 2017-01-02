package ru.pravvich.tick_tack_toe.Users;

import ru.pravvich.tick_tack_toe.Users.position.Posit;
import ru.pravvich.tick_tack_toe.Users.position.Position;
import ru.pravvich.tick_tack_toe.desk.Desc;

public class Bot implements Gamers {
    private Position posit = new Posit();
    private char color;

    @Override
    public void setColor(char color) {
        this.color = color;
    }

    @Override
    public char getColor() {
        return this.color;
    }

    @Override
    public Position getPosit() {
        return this.posit;
    }

    @Override
    public void setPosit() {
        this.posit = this.generatePos();
    }

    private Position generatePos() {
        char[][] desc = Desc.getInfoDesc();
        for (int i = 0; i < desc.length; i++) {
            for (int j = 0; j < desc.length; j++) {
                if (desc[j][i] == ' ') {
                    return new Posit(i, j);
                }
            }
        }
        System.err.println("Что-то пошло не так");
        return new Posit();
    }
}