package com.bus.huyma.hbus.activity;

/**
 * Created by huyma on 02/04/2016.
 */
public class bus {
    private int logo;
    private String tuyen;
    private String maso;
    private String lotrinh;

    public bus(int logo, String tuyen, String maso, String lotrinh){
        this.logo=logo;
        this.tuyen=tuyen;
        this.maso=maso;
        this.lotrinh=lotrinh;
    }
    public bus(){
    }
    public int getDodaimaso(){
        return maso.length();
    }
    public int getLogo(){
        return logo;
    }
    public void setLogo(int logo)
    {
        this.logo= logo;
    }
    public String getTuyen(){
        return tuyen;
    }
    public void setTuyen(String tuyen ){
        this.tuyen=tuyen;
    }
    public String getMaso(){
        return maso;
    }
    public void setMaso(String maso ){
        this.maso=maso;
    }
    public String getLotrinh(){
        return lotrinh;
    }
    public void setLotrinh(String lotrinh) {this.lotrinh = lotrinh;}
}
