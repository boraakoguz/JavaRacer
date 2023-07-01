package JavaRacer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {
    public int playerX,playerY; //x,y coordinates and rotation of the car in degrees. 0 representing right as in cartesian system(stored in velocity)
    public Rectangle solidArea = new Rectangle(); // aka hitbox of the car
    public double frictionCoefficient; //road friction: 1 in asphalt, 0.1 in grass checked by CollisionControl class
    public int points, laps = 0;
    public boolean onFinishLine, offFinishLine, isCollided = false; //flags for game logic
    int screenMidX, screenMidY; 
    public BufferedImage playerModel;
    GameWindow gameWindow;
    KeyHandler keyHandle;
    Velocity velocity;
    int counter;
    public Player(GameWindow gw, KeyHandler kh){
        this.gameWindow = gw;
        this.keyHandle = kh;
        setDefault();
    }
    public void setDefault(){
        this.playerX=MapLoader.spawnX*gameWindow.tileSize;
        this.playerY=MapLoader.spawnY*gameWindow.tileSize;
        this.velocity = new Velocity();
        switch (MapLoader.spawnDirection) {
            case 0:
                velocity.angle = 0;
                break;
            case 1:
                velocity.angle = 90;
                break;
            case 2:
                velocity.angle = 180;
                break;
            case 3:
                velocity.angle = 270;
                break;
            default:
                break;
        }
        this.solidArea.x = 30;
        this.solidArea.y = 5;
        this.solidArea.width = 25;
        this.solidArea.height = 25;
    
        try {
            File tmp = new File("source/car.png");
            playerModel = ImageIO.read(tmp);
            this.screenMidX = this.gameWindow.WIDTH/2 - (playerModel.getWidth()/4);
            this.screenMidY = this.gameWindow.HEIGHT/2 -(playerModel.getHeight()/4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void update(){
        frictionCoefficient = gameWindow.collisionControl.checkCollision(this);
        checkLaps();
        calculatePoints();
        if (keyHandle.aPressed){
            int rotationAngle = 5;
            double netVelocity = velocity.netVelocity();
            if (netVelocity > 0) {
                double scale = 1.0 / (1.0 + netVelocity * 0.1); 
                rotationAngle *= scale;
                rotationAngle = Math.max(rotationAngle, 2);
            }
            velocity.rotate(rotationAngle);
        }
        else if(keyHandle.dPressed){
            int rotationAngle = -5;
            double netVelocity = velocity.netVelocity();
            if (netVelocity > 0) {
                double scale = 1.0 / (1.0 + netVelocity * 0.1); 
                rotationAngle *= scale;
                rotationAngle = Math.min(rotationAngle, -2); 
            }
            velocity.rotate(rotationAngle);
        }   
        else if(keyHandle.wPressed){
            velocity.accelerate(0.1*frictionCoefficient);
        }
        else if(keyHandle.sPressed){
            velocity.accelerate(-0.2);
        }
        
        while(frictionCoefficient<1&&velocity.netVelocity()>10*frictionCoefficient){
            velocity.accelerate(-0.5);
        }
        this.playerX += velocity.X;
        this.playerY -= velocity.Y;
    }
    public void checkLaps(){
        if(onFinishLine){
            if(offFinishLine&&points>(laps*2500)+1000){
                laps++;
                points += 5000;
            }
            offFinishLine = false;
        }
        else{
            offFinishLine = true;
        }
    }
    public void calculatePoints(){
        if(frictionCoefficient<1){
            if(!isCollided){
                points-=1000;
                isCollided = true;
            }
            points -= 2*velocity.netVelocity()/frictionCoefficient;
        }
        else{
            isCollided = false;
            points += velocity.netVelocity();
        }
        if(points<0){
            points = 0;
        }
    }
    public void draw(Graphics2D graphics){
        graphics.rotate(Math.toRadians(-velocity.angle),screenMidX+playerModel.getWidth()/4,screenMidY+playerModel.getHeight()/4);
        graphics.drawImage(playerModel,screenMidX,screenMidY,playerModel.getWidth()/2,playerModel.getHeight()/2,null);
    }
}
