public class Pair<L,R> {
  private L l;
  private R r;

  public Pair(L l, R r){
    this.l = l;
    this.r = r;
  }

  public L getL(){ return l; }
  public R getR(){ return r; }
  public void setL(L l){ this.l = l; }
  public void setR(R r){ this.r = r; }

  public boolean equals(Object o) {
    if (o instanceof Pair) {
        Pair p = (Pair)o;
        return p.l == l && p.r == r;
    }
    return false;
  }
  public int hashCode() {
      return new Integer((int) l).hashCode() * 31 + new Integer((int) r).hashCode();
  }

}