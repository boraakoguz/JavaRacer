package JavaRacer;

public class CollisionControl {
    GameWindow gameWindow;

    public CollisionControl(GameWindow gw){
        this.gameWindow = gw;
    }
    public double checkCollision(Player player){
        int leftX = player.playerX + player.solidArea.x;
        int rightX = leftX + player.solidArea.width;
        int upperY = player.playerY + player.solidArea.y;
        int lowerY = upperY + player.solidArea.height;

        int leftColNum = leftX / gameWindow.tileSize;
        int rightColNum = rightX / gameWindow.tileSize;
        int upperRowNum = upperY / gameWindow.tileSize;
        int lowerRowNum = lowerY / gameWindow.tileSize;

        if(leftColNum>=0&&rightColNum<=gameWindow.collumn-1&&upperRowNum>=0&&lowerRowNum<=gameWindow.row-1){
            player.onFinishLine = false;
            if(gameWindow.tileManager.tiles[gameWindow.tileManager.map[upperRowNum][leftColNum]].isFinishLine||gameWindow.tileManager.tiles[gameWindow.tileManager.map[lowerRowNum][rightColNum]].isFinishLine){
                player.onFinishLine = true;
            }
            double friction = Math.min(gameWindow.tileManager.tiles[gameWindow.tileManager.map[upperRowNum][leftColNum]].friction, gameWindow.tileManager.tiles[gameWindow.tileManager.map[lowerRowNum][rightColNum]].friction);
            return friction;
        }
        return 0.1;
    }
}
