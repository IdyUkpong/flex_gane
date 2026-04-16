package com.flex.config;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class meme extends Application {

    static final int WIDTH = 600;
    static final int HEIGHT = 600;
    static final int SIZE = 20;

    List<Point> snake = new ArrayList<>();
    Point food;

    String direction = "RIGHT";
    boolean running = true;
    boolean paused = false;

    int score = 0;
    int foodEaten = 0;

    long speed = 200_000_000;

    Random random = new Random();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        snake.add(new Point(5, 5));
        spawnFood();

        Group root = new Group(canvas);
        Scene scene = new Scene(root);

        // 🎮 Controls
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP && !direction.equals("DOWN")) direction = "UP";
            if (e.getCode() == KeyCode.DOWN && !direction.equals("UP")) direction = "DOWN";
            if (e.getCode() == KeyCode.LEFT && !direction.equals("RIGHT")) direction = "LEFT";
            if (e.getCode() == KeyCode.RIGHT && !direction.equals("LEFT")) direction = "RIGHT";

            // ⏸ Pause toggle
            if (e.getCode() == KeyCode.SPACE) paused = !paused;
        });

        stage.setScene(scene);
        stage.setTitle("Snake Game");
        stage.show();

        new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate > speed) {
                    if (!paused) update();
                    draw(gc);
                    lastUpdate = now;
                }
            }
        }.start();
    }

    void update() {
        if (!running) return;

        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        switch (direction) {
            case "UP" -> newHead.y--;
            case "DOWN" -> newHead.y++;
            case "LEFT" -> newHead.x--;
            case "RIGHT" -> newHead.x++;
        }

        // 🌀 Wrap around walls
        if (newHead.x < 0) newHead.x = WIDTH / SIZE - 1;
        if (newHead.y < 0) newHead.y = HEIGHT / SIZE - 1;
        if (newHead.x >= WIDTH / SIZE) newHead.x = 0;
        if (newHead.y >= HEIGHT / SIZE) newHead.y = 0;

        // 💀 Self collision
        for (Point p : snake) {
            if (p.equals(newHead)) {
                running = false;
                return;
            }
        }

        snake.add(0, newHead);

        // 🍎 Eat food
        if (newHead.equals(food)) {
            score += 5;
            foodEaten++;
            if (foodEaten % 5 == 0) {
                speed -= 10_000_000;

            if (speed < 80_000_000) {
                speed = 80_000_000;
            }
        }
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    void draw(GraphicsContext gc) {
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        //Grid
        gc.setStroke(Color.web("#1a1a1a"));
        for(int i=0;i<WIDTH; i += SIZE){
            gc.strokeLine(i, 0, i, HEIGHT);
            gc.strokeLine(0, i, WIDTH, i);
        }
        // snake

        for (int i = 0; i < snake.size(); i++){
            Point p = snake.get(i);
            if(i ==0){
                //Head
                gc.setFill(Color.GREENYELLOW);
                gc.fillOval(p.x * SIZE, p.y * SIZE, SIZE, SIZE);

                //EYES

                gc.setFill(Color.BLACK);

                double x1 = 5, x2 =11, y =5;

                switch (direction){
                    case "UP" -> y = 3;
                    case "DOWN" -> y = 11;
                    case "LEFT" ->  {
                        x1 = 3;
                        x2 = 3;
                    }

                    case "RIGHT" -> {
                        x1 = 13;
                        x2 =13;
                    }
                }

                gc.fillOval(p.x * SIZE + x1, p.y * SIZE +y, 4,4);
                gc.fillOval(p.x * SIZE + x2, p.y * SIZE +y, 4,4);
            } else{

                //Gradient body

                double factor = (double) i/ snake.size();
                gc.setFill((Color.color(0, 1- factor * -0.5, 0)));
                gc.fillOval(p.x * SIZE, p.y * SIZE, SIZE,SIZE);
            }
        }


        // food
        gc.setFill(Color.RED);
        gc.fillRect(food.x * SIZE, food.y * SIZE, SIZE, SIZE);

        // 🧮 Score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(20));
        gc.fillText("Score: " + score, 10, 20);

        // ⏸ Pause text
        if (paused) {
            gc.fillText("PAUSED", WIDTH / 2 - 40, HEIGHT / 2);
        }

        // 💀 Game Over
        if (!running) {
            gc.fillText("GAME OVER", WIDTH / 2 - 60, HEIGHT / 2);
        }
    }

    // 🚫 Prevent food spawning on snake
    void spawnFood() {
        while (true) {
            Point newFood = new Point(
                    random.nextInt(WIDTH / SIZE),
                    random.nextInt(HEIGHT / SIZE)
            );

            boolean onSnake = false;
            for (Point p : snake) {
                if (p.equals(newFood)) {
                    onSnake = true;
                    break;
                }
            }

            if (!onSnake) {
                food = newFood;
                break;
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return x == p.x && y == p.y;
        }
    }
}