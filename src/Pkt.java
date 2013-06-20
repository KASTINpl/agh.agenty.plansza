/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author student
 */
public class Pkt {
    public int x;
    public int y;
    Pkt() {
        this.x=0;
        this.y=0;
    }
    Pkt(Pkt n) {
        this.x=n.x;
        this.y=n.y;
    }
    Pkt(int xx, int yy) {
        this.x=xx;
        this.y=yy;
    }
    public Pkt kopia() {
        return new Pkt(this);
    }
    
    public boolean in_r(Pkt p, int r) {
        int dX = Math.abs(x-p.x); 
        int dY = Math.abs(y-p.y);
        if (dX<=r && dY<=r) return true;
        return false;
    }
    
    public boolean exual(Pkt n) {
        if (x==n.x && y==n.y) return true;
        return false;
    }
}