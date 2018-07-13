package com.bus.huyma.hbus.activity;

public class ItemDetailRoute {
    private int bieuTuong;
    private String cachdi;

    public ItemDetailRoute(int logo,String di){
        bieuTuong=logo;
        cachdi=di;
    }

    public ItemDetailRoute(){}

    public int getLogo(){
        return bieuTuong;
    }
    public void setLogo(int logo)
    {
        bieuTuong= logo;
    }
    public String getCachDi(){
        return cachdi;
    }
    public void setCachDi(String di ){
        cachdi=di;
    }
}
