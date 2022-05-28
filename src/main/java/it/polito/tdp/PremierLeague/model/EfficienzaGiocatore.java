package it.polito.tdp.PremierLeague.model;

public class EfficienzaGiocatore {

	private Player p1;
	private double efficienza;

	public EfficienzaGiocatore(Player p1, double efficienza) {
		this.p1 = p1;
		this.efficienza = efficienza;
	}

	public Player getP1() {
		return p1;
	}

	public void setP1(Player p1) {
		this.p1 = p1;
	}

	public double getEfficienza() {
		return efficienza;
	}

	public void setEfficienza(double efficienza) {
		this.efficienza = efficienza;
	}

}
