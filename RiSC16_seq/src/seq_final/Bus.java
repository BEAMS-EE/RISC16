package seq_final;
import java.awt.*;

public class Bus {
  private int data=0;
  private String word=new String("0000000000000000");
  private int x[][],y[][];
  private int out[];

  private int state=0;
  private int nboutact=0, cid=0; // current Id

  private float epaisseur;
  private Color couleur;
  private Color colorD=Color.black,colorE=new Color(255,205,80);//new Color(85,220,68);//Color.green;//orange;
  private BasicStroke pen,basic;   // pen = gros trait   | basic= trait blanc
  private boolean show=true;  // ctl



//================================================================================================
//   INITIALISATION
//================================================================================================
  //public Bus(){}
///////////////////////////////////////////// 1 vector - 2 points
  public Bus(int x1,int y1,int x2,int y2){
    x = new int[1][2];
    y = new int[1][2];
    x[0][0]=x1;
    x[0][1]=x2;
    y[0][0]=y1;
    y[0][1]=y2;

    epaisseur=5.0f;
    pen=new BasicStroke(epaisseur,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
    basic=new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
    couleur=Color.black;
    }
   /* public Bus(boolean small,int x1,int y1,int x2,int y2){
      x = new int[1][2];
      y = new int[1][2];
      x[0][0]=x1;
      x[0][1]=x2;
      y[0][0]=y1;
      y[0][1]=y2;

      epaisseur=2;
          couleur=Color.black;
      pen=new BasicStroke(epaisseur,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
      basic=new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
      }*/

///////////////////////////////////////////// . vector - . points
  public Bus(int x[][], int y[][]) {
    this.x=x;
    this.y=y;

    epaisseur=5.0f;
    pen=new BasicStroke(epaisseur,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
    basic=new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
    couleur=Color.black;
    }
///////////////////////////////////////////// . vector - . points
  public Bus(int a[],int b[]) {
    x = new int[1][a.length];
    y = new int[1][b.length];
    for (int i = 0; i < a.length; i++) {
      x[0][i] = a[i];
      y[0][i] = b[i];
      }
   //   nbout=a.length;
      epaisseur=5.0f;
      couleur=Color.black;
      pen=new BasicStroke(epaisseur,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
      basic=new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);

   }
///////////////////////////////////////////// . vector - . points + epaisseur
   public Bus(int a[],int b[],float epaisseur) {
    this.x = new int[1][a.length];
    this.y = new int[1][b.length];
    for (int i = 0; i < a.length; i++) {
      x[0][i] = a[i];
      y[0][i] = b[i];}


    this.epaisseur=epaisseur*1.5f;
    pen=new BasicStroke(2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
    basic=new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
    couleur=Color.black;
   }
   ///////////////////////////////////////////// . vector - . points
  public Bus(int x[][], int y[][],float epaisseur) {
    this.x=x;
    this.y=y;

    this.epaisseur=epaisseur*1.5f;
     pen=new BasicStroke(2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
     basic=new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
     couleur=Color.black;

    }

 //================================================================================================
   public int getId(){
	   //donne l'identité du bus cad par ex pour le bus b00 cid = 1 pour l'incrémenteur et cid = 2 pour la ROM
     cid=cid+1;                    // inc
    //System.out.println("BUS: getID "+(cid));//on devrait rajouter le num du bus pour que ça soit clair

     out = new int[cid];
     for (int i = 0; i <out.length; i++)
       out[i]=0;
     return cid; }
//================================================================================================

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//================================================================================================
//   MAIN
//================================================================================================


//public void activer (){  disable();    }
//public void desactiver (){  disable();    }

  public int readData(){  return data; }

  public int getData(){
    disable();
  return data; }

 public int getData(int i){
     disable(i);
  return data; }

public String getWord(int i){
    disable(i);
 return word; }




public void reset (){
  disable();
  this.word="0000000000000000";
     this.data=0;}

 /////////////////////////////
   public int[][] getX(){return x;}
   public int[][] getY(){return y;}
////////////////////////////
   public String getWord(){return word;}
   public void setWord(String word){this.word=word;}
//    public int getData(){return data; }
   public void setData(int data){this.data=data;}
 /////////////////////////////
 private String dataHex(int data){
    String temp=new String();
    temp=Integer.toHexString(data) ;
    while(temp.length()<4)  temp="0"+temp;
    return "0x"+temp;
 }
 private String dataBin(int data){
    String temp=new String();
    temp=Integer.toBinaryString(data);
    while(temp.length()<16)  temp="0"+temp;
    return temp;
 }

//================================================================================================
  public void enable(){

    state=1;
    couleur=colorE;

    if (out==null)
      System.out.println("!!!!!  BUS: output not defined");
    else{
   
      for (int j = 0; j < out.length; j++)
        out[j] = 1;
      nboutact = out.length;
    }
  }
//================================================================================================
  public void disable(){//
    if (out!=null)
      for (int j=0;j<out.length;j++)
        out[j]=0;
    nboutact=0;
    state=0;
    couleur=colorD;}
//================================================================================================
  private void disable(int i){
    if (out[i-1]==1) // si sortie active > on la desactive
    {
      out[i - 1] = 0;
      nboutact--;
      if (nboutact==0)
        disable();//va se désativer si tous les blocs à la sortie du bus on prit ce qu'il y avait sur le bus!

    }
    System.out.println("BUS: disableID# "+i+" - act= "+nboutact+"   - nbout= "+out.length);
  }
//================================================================================================
  public void receive(int data){
     System.out.println("BUS : receiving data =\t"+data+" \t|   "+dataHex(data)+" \t|   "+dataBin(data));
	  
     setData(data);
     enable();
 }
 public void receive(String word){
    System.out.println("BUS : receiving word = "+word);
    setWord(word);
    enable();
}
//================================================================================================

  public boolean isActive(){      return (state==1);}  // contient une donnée non lue !!
  public boolean isActive(int i){ return (out[i-1]==1);}  // la donnée i est non lue !!
///////////////////////////


//================================================================================================
//   GRAPHICs
//================================================================================================
  public void setBasicStroke(BasicStroke pen){this.pen=pen;}
  public void setBasicStrokeMilieu(BasicStroke basic){this.basic=basic;}
  public void setColor(Color couleur){this.couleur=couleur;}
  public void setEpaisseur(int epaisseur){this.epaisseur=epaisseur;}
/////////////////////////////
  /*public void changeColor(){
  if(couleur==Color.black)  couleur=colorE;
  else                      couleur=Color.black;}*/
/////////////////////////////
  //public void hide(){show=false;}
  //public void show(){show=true;}
  public boolean getShow(){return show;}
/////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void paint(Graphics2D g){
     if(show){
      for(int j=0;j<x.length;j++)
        for(int i=0;i<x[0].length-1;i++){
          g.setColor(couleur); // couleur & pen adéquats
          g.setStroke(pen);
          if(x[j][i+1]!=0){
            if(i+1!=x[0].length-1 && x[j][i+2]!=0){
                 g.drawLine(x[j][i],y[j][i],x[j][i+1],y[j][i+1]);
                 g.setColor(Color.white);
                 g.setStroke(basic);
                 g.drawLine(x[j][i],y[j][i],x[j][i+1],y[j][i+1]);
                 }
            //c'est la fin d'une branche de bus=> faut mettre une fleche
            else
               if(x[j][i]==x[j][i+1])//test si c une ligne verical
                 if(y[j][i]<y[j][i+1]){//test si ca va vers le bas
                   g.drawLine(x[j][i],y[j][i],x[j][i+1],y[j][i+1]-10);
                   int[] xtemp={x[j][i]-(int)(1.5*epaisseur),
                       x[j][i]+(int)(1.5*epaisseur),x[j][i]};
                   int[] ytemp={y[j][i+1]-10,y[j][i+1]-10,y[j][i+1]};
                   g.fillPolygon(xtemp,ytemp,3);
                   g.setColor(Color.white);
                   g.setStroke(basic);
                   g.drawLine(x[j][i],y[j][i],x[j][i+1],y[j][i+1]-10);
                   }
                 else{//vers le haut
                   g.drawLine(x[j][i],y[j][i],x[j][i+1],y[j][i+1]+10);
                   int[] xtemp={x[j][i]-(int)(1.5*epaisseur),
                       x[j][i]+(int)(1.5*epaisseur),x[j][i]};
                   int[] ytemp={y[j][i+1]+10,y[j][i+1]+10,y[j][i+1]};
                   g.fillPolygon(xtemp,ytemp,3);
                   g.setColor(Color.white);
                   g.setStroke(basic);
                   g.drawLine(x[j][i],y[j][i],x[j][i+1],y[j][i+1]+10);
                   }
                else
                  if(x[j][i]<x[j][i+1]){//test si ca va vers la droite
                    g.drawLine(x[j][i],y[j][i],x[j][i+1]-10,y[j][i+1]);
                    int[] xtemp={x[j][i+1]-10,x[j][i+1]-10,x[j][i+1]};
                    int[] ytemp={y[j][i+1]-(int)(1.5*epaisseur),
                        y[j][i+1]+(int)(1.5*epaisseur),y[j][i+1]};
                    g.fillPolygon(xtemp,ytemp,3);
                    g.setColor(Color.white);
                    g.setStroke(basic);
                    g.drawLine(x[j][i],y[j][i],x[j][i+1]-10,y[j][i+1]);
                    }
                  else  // fleche vers la gauche
                    {g.drawLine(x[j][i],y[j][i],x[j][i+1]+10,y[j][i+1]);
                    int[] xtemp={x[j][i+1]+10,x[j][i+1]+10,x[j][i+1]};
                    int[] ytemp={y[j][i+1]-(int)(1.5*epaisseur),
                        y[j][i+1]+(int)(1.5*epaisseur),y[j][i+1]};
                    g.fillPolygon(xtemp,ytemp,3);
                    g.setColor(Color.white);
                    g.setStroke(basic);
                    g.drawLine(x[j][i],y[j][i],x[j][i+1]+10,y[j][i+1]);
                    }
          }
        }
    g.setStroke(basic);// on remet basic stroke
 }}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
