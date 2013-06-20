/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author student
 */
public class Belka {
    public Pkt a;
    public Pkt b;
    Belka() {
        this.a = new Pkt(0,0);
        this.b = new Pkt(0,0);
    }
    Belka(Pkt aa, Pkt bb) {
        this.a = aa;
        this.b = bb;
    }
    
    public boolean in(Pkt p) {
        if (this.a.x==this.b.x) { // belka pionowa
            if (p.x==this.a.x) {
                if (p.y>=this.a.y && p.y<=this.b.y) return true;
                else if (p.y>=this.b.y && p.y<=this.a.y) return true;
            }
        } else if (this.a.y==this.b.y) { // belka pozioma
            if (p.y==this.a.y) {
                if (p.x>=this.a.x && p.x<=this.b.x) return true;
                else if (p.x>=this.b.x && p.x<=this.a.x) return true;
            }
        }
        return false;
    }
}